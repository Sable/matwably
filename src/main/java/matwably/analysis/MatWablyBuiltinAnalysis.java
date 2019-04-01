
package matwably.analysis;

import ast.*;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGeneratorFactory;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.analysis.core.ReachingDefs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dherre3
 * This class runs the built-in generators to produce an analysis of them and all the stmts needed to make the call
 * in an optimal way.
 * - First optimization is that of pushing allocation out-of-loops. To do
 * this we need to recognize which operations are loop-invariant. An easy start is actually to do so for input-vectors,
 * where if we know how the call is made, the allocation for the inputs is pushed outside the loop. To do this we carry
 * to extra data structures: {@link matwably.analysis.MatWablyBuiltinAnalysis#loopAllocationInstructions} and
 * {@link matwably.analysis.MatWablyBuiltinAnalysis#loopFreeingInstructions}
 * - Moreover, to save another extra analysis, it also has a Set called `logicalUses` which keeps track of variables
 * that come from a logical function call, this is used in loops and if-statements, in order to save a
 * few-extra instructions. To this end, we maintain here a Set called, logicalUses.
 * {@link matwably.analysis.MatWablyBuiltinAnalysis#logicalUses}.
 * - Another optimization is that of tagging Name definitions to indices of get/set array, in this case, we use ReachingDefs,
 * and do so when statically possible. The idea is that we will be able to
 * re-build get/set statements and use a more optimal built-in implementation for array accessing. For instance,
 * if you use b = a(2:10,:), this will be translated into temp= colon(2,10);b = a(temp,:),we merely recognize that temp
 * is actually only part of the get expression, and instead of allocating an array, we use a get specialization that
 * actully indices the array through the 2:10 expression.
 */
public class MatWablyBuiltinAnalysis extends TIRAbstractNodeCaseHandler {
    private Set<Name> logicalUses = new HashSet<>();
    private HashMap<TIRNode, MatWablyBuiltinGenerator> callGeneratorMap = new HashMap<>();
    private HashMap<Stmt, List<Instruction>> loopAllocationInstructions = new HashMap<>();
    private HashMap<Stmt, List<Instruction>> loopFreeingInstructions = new HashMap<>();
    private Stmt currentLoopStmt;
    private ReachingDefs reachingDefs;
    private MatWablyFunctionInformation functionInformation;

    /**
     * Getter for the function generator.
     * @param node TIRNode is one of TIRCallStmt or TIR[Get|Set]ArrayStmt
     * @return Returns the generator for the function call
     */
    public MatWablyBuiltinGenerator getGenerator(TIRNode node){
        return  callGeneratorMap.get(node);
    }
    //

    /**
     * Returns whether a variable use is logical statically. Note that this is limited
     * to cases where is there is only one definition
     * @param name Use of a variable
     * @return Boolean variable which describes when a variable is logical. Please note the limitations of this function
     */
    public boolean isLogicalUse(Name name){
        return logicalUses.contains(name);
    }

    /**
     * @param functionInformation Constructor takes all the analyses for the Function call
     */
    public MatWablyBuiltinAnalysis(MatWablyFunctionInformation functionInformation){
        if(functionInformation == null) throw new Error("Must have analyses to generate builtin");
        this.functionInformation = functionInformation;
        this.reachingDefs = functionInformation.getReachingDefs();
    }
    public void analyze(){
        functionInformation.getFunction().analyze(this);
    }

    /**
     * This visitor method gets generator for TIRCallStmt puts it in map, and additionally computes the isLogical call.
     * Notice, that this call is actually static
     * @param callStmt Call stmt. to process
     */
    @Override
    public void caseTIRCallStmt(TIRCallStmt callStmt) {
        MatWablyBuiltinGenerator generator;
        generator = MatWablyBuiltinGeneratorFactory.getGenerator(callStmt, callStmt.getArguments(),
                                callStmt.getTargets(), callStmt.getFunctionName().getID(), functionInformation);
        generator.generate();
        if( generator.isLogical()){
            if(callStmt.getTargets().size() > 1) throw new Error("Logical function must always return one value");
            Set<Name> uses = reachingDefs.getUseDefDefUseChain().getUses(callStmt.getTargetName());
            // Adds all the logical uses that are not ambiguous so that
            logicalUses.
                    addAll(uses.stream().
                            filter((Name name)->reachingDefs.getUseDefDefUseChain().getDefs(name).size() == 1)
                            .collect(Collectors.toSet()));
        }

        // Add free/alloc instructions to loop.
        addLoopInstructions(generator);
        // Put the built-in generator in map for later use
        callGeneratorMap.put(callStmt, generator);
    }

    /**
     * TIRArraySet case, similar to function call.
     * @param setStmt TIRArraySetStmt
     */
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt setStmt){
        MatWablyBuiltinGenerator generator;
        generator = MatWablyBuiltinGeneratorFactory.getGenerator(setStmt, setStmt.getIndices(),
                null, "subsasgn", functionInformation);
        if(generator != null){
            callGeneratorMap.put(setStmt, generator);
            addLoopInstructions(generator);
        }
    }
    /**
     * TIRArrayGet case, similar to function call.
     * @param getStmt TIRArrayGetStmt
     */
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt getStmt){
        /* TODO (Dherre3) handle logic here where we avoid the statement generation for a colon expression, since
        * TODO we can avoid it if is in the get*/
        TIRCommaSeparatedList indices = getStmt.getIndices();
        // Go through each, if index ReachingDefs only has one definition, tag that as statement as one not to be
        // generated. Make sure that the call for get actually handles this case.
        NameExpr[] indiceNameExpr = (NameExpr[]) indices.stream().filter((Expr expr)-> expr instanceof NameExpr &&
                reachingDefs.getUseDefDefUseChain().getDefs(((NameExpr)expr).getName()).size() == 1
        && reachingDefs.getUseDefDefUseChain().getDefs(((NameExpr)expr).getName()).toArray()[0]
                instanceof TIRAbstractAssignStmt).toArray();

        MatWablyBuiltinGenerator generator;
        generator = MatWablyBuiltinGeneratorFactory.getGenerator(getStmt, getStmt.getIndices(),
                null, "subsref", functionInformation);
        if(generator != null){
            callGeneratorMap.put(getStmt, generator);
            addLoopInstructions(generator);
        }
    }

    /**
     * While stmt. used to set the current loop and initialize the set for the input vectors.
     * @param whileStmt
     */
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt whileStmt){
        initializeLoopInstructions(whileStmt);
    }
    /**
     * For stmt. used to set the current loop and initialize the set for the input vectors.
     * @param forStmt
     */
    @Override
    public void caseTIRForStmt(TIRForStmt forStmt){
        initializeLoopInstructions(forStmt);
    }

    /**
     *  Set current loop and initialize loop allocation mappings
     * @param stmt Statement to initialize
     */
    private void initializeLoopInstructions(Stmt stmt){
        currentLoopStmt = stmt;
        loopAllocationInstructions.put(currentLoopStmt, new List<>());
        loopFreeingInstructions.put(currentLoopStmt, new List<>());
    }

    /**
     * Map generator allocation instructions to loop maps.
     * @param generator
     */
    private void addLoopInstructions(MatWablyBuiltinGenerator generator){
        if(currentLoopStmt != null){
            loopAllocationInstructions.get(currentLoopStmt).addAll(generator.getResult().getAlloc_input_vec_instructions());
            loopFreeingInstructions.get(currentLoopStmt).addAll(generator.getResult().getFree_input_vec_instructions());
        }
    }

    /**
     * Dummy case that must be implemented in the TIRAbstractNodeCaseHandler.
     * @param astNode
     */
    @Override
    public void caseASTNode(ASTNode astNode) {
        for (int i = 0; i < astNode.getNumChild(); i++) {
            ASTNode child = astNode.getChild(i);
            if (child instanceof TIRNode) {
                ((TIRNode) child).tirAnalyze(this);
            }
            else {
                child.analyze(this);
            }
        }
    }
}
