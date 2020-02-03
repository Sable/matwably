
package matwably.analysis;

import ast.ASTNode;
import ast.Function;
import ast.Stmt;
import matwably.analysis.intermediate_variable.TreeExpressionBuilderAnalysis;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.ast.TypeUse;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.MatWablyBuiltinGenerator;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorFactory;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.*;

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

    private final TreeExpressionBuilderAnalysis expr_elim_analysis;
    private final boolean skip_variable_elim;
    private final Function matlabFunction;
    private HashSet<TypeUse> locals = new HashSet<>();
    private HashMap<ASTNode, MatWablyBuiltinGenerator> callGeneratorMap = new HashMap<>();
    private HashMap<Stmt, List<Instruction>> loopAllocationInstructions = new HashMap<>();
    private HashMap<Stmt, List<Instruction>> loopFreeingInstructions = new HashMap<>();
    private Stack<Stmt> currentLoopStack = new Stack<>();
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
        if(functionInformation == null) throw new Error("Must have analyses to generateInstructions builtin");
        this.functionInformation = functionInformation;
        this.matlabFunction = functionInformation.getFunction();
        this.expr_elim_analysis = functionInformation.getTreeExpressionBuilderAnalysis();
        this.skip_variable_elim = functionInformation.getProgramOptions().disallow_variable_elimination;


    }

    /**
     * Run analysis
     */
    public void analyze(){
        this.matlabFunction.analyze(this);
    }

    /**
     * This visitor method gets generator for TIRCallStmt puts it in map, and additionally computes the isLogical call.
     * Notice, that this call is actually static
     * @param callStmt Call stmt. to process
     */
    @Override
    public void caseTIRCallStmt(TIRCallStmt callStmt) {
        MatWablyBuiltinGenerator generator = MatWablyBuiltinGeneratorFactory.getGenerator(callStmt,
                        callStmt.getArguments(),callStmt.getTargets(),
                callStmt.getFunctionName().getID(), functionInformation);
//        MatWablyBuiltinGeneratorResult result;
//        if(!this.skip_variable_elim && this.expr_elim_analysis.isStmtRedundant(callStmt)){
////            result = generator.generateExpression();
////            this.locals.addAll(result.getLocals());
//        }else{
////            result = generator.generateInstructions();
//            // Add free/alloc instructions to loop.
//
//        }
//        addLoopInstructions(result);
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
         * TODO we can avoid it if is in the get, I need to someone map them with statements to not generateInstructions in the compiler*/
        MatWablyBuiltinGenerator generator = MatWablyBuiltinGeneratorFactory.getGenerator(setStmt,
                setStmt.getIndices(),new TIRCommaSeparatedList(),
                "subsref", functionInformation);
        callGeneratorMap.put(setStmt, generator);

//        generator = MatWablyBuiltinGeneratorFactory.getGenerator(setStmt, setStmt.getIndices(),
//                null, "subsasgn", functionInformation);
//        if(generator != null){
//            callGeneratorMap.addDef(setStmt, generator);
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
           TODO we can avoid it if is in the get, I need to someone map them with statements to not generateInstructions in the compiler*/
        MatWablyBuiltinGenerator generator = MatWablyBuiltinGeneratorFactory.getGenerator(getStmt,
                getStmt.getIndices(),getStmt.getTargets(),
                "subsasgn", functionInformation);
        callGeneratorMap.put(getStmt, generator);


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
//        callGeneratorMap.addDef(getStmt, generator);
//        addLoopInstructions(generator);
    }

    /**
     * While stmt. used to set the current loop and initialize the set for the input vectors.
     * @param whileStmt
     */
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt whileStmt){
        currentLoopStack.push(whileStmt);
        loopAllocationInstructions.put(whileStmt, new List<>());
        loopFreeingInstructions.put(whileStmt, new List<>());
        this.caseWhileStmt(whileStmt);
        currentLoopStack.pop();
    }
    /**
     * For stmt. used to set the current loop and initialize the set for the input vectors.
     * @param forStmt
     */
    @Override
    public void caseTIRForStmt(TIRForStmt forStmt){
        currentLoopStack.push(forStmt);
        loopAllocationInstructions.put(forStmt, new List<>());
        loopFreeingInstructions.put(forStmt, new List<>());
        super.caseForStmt(forStmt);
        currentLoopStack.pop();
    }


    /**
     * Map generator allocation instructions to loop maps.
     * @param result {@link MatWablyBuiltinGenerator} result
     */
    private void addLoopInstructions(MatWablyBuiltinGeneratorResult result){

        if(!this.currentLoopStack.isEmpty()){
            Stmt currentLoopStmt = this.currentLoopStack.peek();
            this.loopAllocationInstructions.get(currentLoopStmt).addAll(result.getAllocInstructions());
            this.loopFreeingInstructions.get(currentLoopStmt).addAll(result.getFreeingInstructions());
        }else{
            result.mergeAllocationInstructions();
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
