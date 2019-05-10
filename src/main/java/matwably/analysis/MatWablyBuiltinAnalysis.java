
package matwably.analysis;

import ast.ASTNode;
import ast.Stmt;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGeneratorFactory;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.HashMap;

/**
 * @author dherre3
 * This class runs the built-in generators to produce an analysis of them and all the stmts needed to make the call
 * in an optimal way.
 * - First optimization is that of pushing allocation out-of-loops. To do
 * this we need to recognize which operations are loop-invariant. An easy start is actually to do so for input-vectors,
 * where if we know how the call is made, the allocation for the inputs is pushed outside the loop. To do this we carry
 * to extra data structures: {@link matwably.analysis.MatWablyBuiltinAnalysis#loopAllocationInstructions} and
 * {@link matwably.analysis.MatWablyBuiltinAnalysis#loopFreeingInstructions}
 * - Another optimization is that of tagging Name definitions to indices of get/set array, in this case, we use ReachingDefs,
 * and do so when statically possible. The idea is that we will be able to
 * re-build get/set statements and use a more optimal built-in implementation for array accessing. For instance,
 * if you use b = a(2:10,:), this will be translated into temp= colon(2,10);b = a(temp,:),we merely recognize that temp
 * is actually only part of the get expression, and instead of allocating an array, we use a get specialization that
 * actually indices the array through the 2:10 expression.
 */
public class MatWablyBuiltinAnalysis extends TIRAbstractNodeCaseHandler {

    private HashMap<ASTNode, MatWablyBuiltinGenerator> callGeneratorMap = new HashMap<>();
    private HashMap<Stmt, List<Instruction>> loopAllocationInstructions = new HashMap<>();
    private HashMap<Stmt, List<Instruction>> loopFreeingInstructions = new HashMap<>();
    private Stmt currentLoopStmt;
    private MatWablyFunctionInformation functionInformation;

    /**
     * Getter for the function generator.
     * @param node TIRNode is one of TIRCallStmt or TIR[Get|Set]ArrayStmt
     * @return Returns the generator for the function call
     */
    public MatWablyBuiltinGenerator getGenerator(ASTNode node){
        return  callGeneratorMap.get(node);
    }
    //
    public List<Instruction> getLoopAllocationInstructions(Stmt stmt){
        return loopAllocationInstructions.get(stmt);
    }
    public List<Instruction> getLoopFreeingInstructions(Stmt stmt){
        return loopFreeingInstructions.get(stmt);
    }

    /**
     * @param functionInformation Constructor takes all the analyses for the Function call
     */
    public MatWablyBuiltinAnalysis(MatWablyFunctionInformation functionInformation){
        if(functionInformation == null) throw new Error("Must have analyses to generate builtin");
        this.functionInformation = functionInformation;
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
        System.out.println(callStmt.getFunctionName().getID());
        generator = MatWablyBuiltinGeneratorFactory.getGenerator(callStmt, callStmt.getArguments(),
                                callStmt.getTargets(), callStmt.getFunctionName().getID(), functionInformation);
        generator.generateExpression();
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
        /* TODO (Dherre3) handle logic here where we avoid the statement generation for a colon expression, since
         * TODO we can avoid it if is in the get, I need to someone map them with statements to not generate in the compiler*/
        MatWablyBuiltinGenerator generator;
//        generator = MatWablyBuiltinGeneratorFactory.getGenerator(setStmt, setStmt.getIndices(),
//                null, "subsasgn", functionInformation);
//        if(generator != null){
//            callGeneratorMap.put(setStmt, generator);
//            addLoopInstructions(generator);
//        }
    }
    /**
     * TIRArrayGet case, similar to function call.
     * @param getStmt TIRArrayGetStmt
     */
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt getStmt){
        /* TODO (Dherre3) handle logic here where we avoid the statement generation for a colon expression, since
        * TODO we can avoid it if is in the get, I need to someone map them with statements to not generate in the compiler*/
//        TIRCommaSeparatedList indices = getStmt.getIndices();
//        // Go through each, if index ReachingDefs only has one definition, tag that as statement as one not to be
//        // generated. Make sure that the call for get actually handles this case.
//        NameExpr[] indiceNameExpr = (NameExpr[]) indices.stream().filter((Expr expr)-> expr instanceof NameExpr &&
//                reachingDefs.getUseDefDefUseChain().getDefs(((NameExpr)expr).getName()).size() == 1
//        && reachingDefs.getUseDefDefUseChain().getDefs(((NameExpr)expr).getName()).toArray()[0]
//                instanceof TIRAbstractAssignStmt).toArray();
//
//        MatWablyBuiltinGenerator generator;
//        generator = MatWablyBuiltinGeneratorFactory.getGenerator(getStmt, getStmt.getIndices(),
//                null, "subsref", functionInformation);
//
//        callGeneratorMap.put(getStmt, generator);
//        addLoopInstructions(generator);
    }

    /**
     * While stmt. used to set the current loop and initialize the set for the input vectors.
     * @param whileStmt
     */
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt whileStmt){
        initializeLoopInstructions(whileStmt);
        this.caseWhileStmt(whileStmt);
    }
    /**
     * For stmt. used to set the current loop and initialize the set for the input vectors.
     * @param forStmt
     */
    @Override
    public void caseTIRForStmt(TIRForStmt forStmt){
        initializeLoopInstructions(forStmt);
        this.caseForStmt(forStmt);
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
