package matwably.analysis;

import ast.ASTNode;
import ast.Name;
import ast.Stmt;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.analysis.core.ReachingDefs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class runs the built-in generators to produce an analysis of them and all the stmts needed to make the call.
 * Moreover, to save another extra analysis, it also has a Set called `logicalUses` which keeps track of variables
 * that come from a logical function call, this is used in loops and if-statements, in order to save a
 * few-extra instructions.
 * Lastly, it also takes care of mapping input_vec in function calls to loop stmts.
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
    public boolean isLogical(Name name){
        return logicalUses.contains(name);
    }

    /**
     * @param functionInformation Constructor takes all the analyses for the Function call
     */
    public MatWablyBuiltinAnalysis(MatWablyFunctionInformation functionInformation){
        if(functionInformation == null) throw new Error("Must have analyses to generate builtin");
        this.functionInformation = functionInformation;
        this.reachingDefs = functionInformation.getReachingDefs();
        functionInformation.getFunction().analyze(this);
    }

    /**
     * This visitor method gets generator for TIRCallStmt puts it in map, and additionally computes the isLogical call.
     * Notice, that this call is actually static
     * @param callStmt Call stmt. to process
     */
    @Override
    public void caseTIRCallStmt(TIRCallStmt callStmt) {
        MatWablyBuiltinGenerator generator = new MatWablyBuiltinGenerator(callStmt,
                    callStmt.getArguments(),
                    callStmt.getTargets(),callStmt.getFunctionName().getID(),
                    this.functionInformation );

        if(generator.isLogicalFunction()){
            if(callStmt.getTargets().size() > 1) throw new Error("Logical function must always return one value");
            Set<Name> uses = reachingDefs.getUseDefDefUseChain().getUses(callStmt.getTargetName());
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
        MatWablyBuiltinGenerator generator = new MatWablyBuiltinGenerator(setStmt,
                setStmt.getIndices(),
                null,"set",
                this.functionInformation );
        callGeneratorMap.put(setStmt, generator);
        addLoopInstructions(generator);
    }
    /**
     * TIRArrayGet case, similar to function call.
     * @param getStmt TIRArrayGetStmt
     */
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt getStmt){
        MatWablyBuiltinGenerator generator = new MatWablyBuiltinGenerator(getStmt,
                getStmt.getIndices(),
                getStmt.getTargets(),"get",
                this.functionInformation );
        callGeneratorMap.put(getStmt, generator);
        addLoopInstructions(generator);

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
