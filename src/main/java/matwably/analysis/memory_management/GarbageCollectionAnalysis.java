package matwably.analysis.memory_management;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;
import matjuice.transformer.MJCopyStmt;
import matwably.MatWablyCommandLineOptions;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;

import java.util.Set;
import java.util.stream.Collectors;

import static natlab.tame.builtin.shapeprop.mathmode.ast.MathModeExprEvaluator.Debug;

/**
 *  Depends on ValueAnalysis
 *
 *  Do not analyze statements to eliminate via the intermediate_variable_elimination
 */
public class GarbageCollectionAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<ReferenceCountMap> {
    private final ValueAnalysisUtil valueAnalysisUtil;
    private final InterproceduralFunctionQuery functionQuery;
    private final MatWablyCommandLineOptions opts;
    private final TIRFunction function;

    /**
     * Base constructor for the analysis
     * @param astNode Function node
     * @param valueAnalysisUtil ValueAnalysisUtility
     * @param functionQuery Interprocedural Query Analysis
     */
    public GarbageCollectionAnalysis(TIRFunction astNode,
                                     ValueAnalysisUtil valueAnalysisUtil,
                                     InterproceduralFunctionQuery functionQuery,
                                     MatWablyCommandLineOptions opts) {
        super(astNode);
        this.function = astNode;
        this.valueAnalysisUtil = valueAnalysisUtil;
        this.functionQuery = functionQuery;
        this.opts = opts;
//        Debug = true;
    }

    @Override
    public ReferenceCountMap merge(ReferenceCountMap pointsToInformation, ReferenceCountMap a1) {
        return ReferenceCountMap.merge(pointsToInformation, a1);
    }

    @Override
    public ReferenceCountMap copy(ReferenceCountMap pointsToInformation) {
        return pointsToInformation.copy();
    }

    @Override
    public ReferenceCountMap newInitialFlow() {
        return new ReferenceCountMap();
    }

    /**
     * To handle this case, we take the inFlowSet, and anything variable that is not part of the output list
     * should be completely freed at this point, therefore we add freeing statements for this particular one
     * @param tirNode TIRReturnStmt
     *
     */
    public void caseTIRReturnStmt(TIRReturnStmt tirNode){
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        // Idea, check static set, if static free, if maybeExternal, check before freeing, if Internal free
       currentOutSet.freeRemaining(function.getOutputParamList()
               .stream().map(Name::getID).collect(Collectors.toSet()));
       outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);
    }

    /**
     * If the variable is a scalar, we need to simply remove the target variable
     * from the static or dynamic set.
     * If the variable is not a scalar, the analysis depends on the copy statement.
     * If it is a cloning  statement, we create a new static site for the source variable
     * If it is a reference statement, we create a reference between the two variables
     * if they copy statement is an MJCopyStmt or we omit copy analysis,
     * @param tirNode TIRCopyStmt to process
     */
    public void caseTIRCopyStmt(TIRCopyStmt tirNode){
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        if(!valueAnalysisUtil.isScalar(tirNode.getSourceName().getID(), tirNode,true)) {
            if (tirNode instanceof MJCopyStmt || opts.omit_copy_insertion) {
                currentOutSet.addNewStaticSite(tirNode.getTargetName().getID(), tirNode);
            } else {
                currentOutSet.writeReference(tirNode.getSourceName().getID(),
                        tirNode.getTargetName().getID(), tirNode);
            }
        }else{
            // Remove variable name from both static and dynamic sets.
            currentOutSet.decreaseReference(tirNode.getTargetName().getID());
        }
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);

    }

    /**
     * a = 3
     * a = 'a'
     * a = 3.00
     * Here, since we are handling only numbers, we simply remove the reference from the memory maps
     * @param tirNode Assign literal statement.
     */
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt tirNode){
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        currentOutSet.decreaseReference(tirNode.getTargetName().getID());
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);

    }


    /**
    *
     * For the statements of this nature, we check whether the variables are scalars or allocations sites.
     * If they are allocation sites i.e. arrays, we create new static allocation sites
     * @param tirNode [a,b,c] = A(i,s)
     */
    public void caseTIRArrayGetStmt(TIRArrayGetStmt tirNode){
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        for(NameExpr name: tirNode.getTargets().getNameExpressions()){
            if(!valueAnalysisUtil.isScalar(name.getName().getID(),tirNode,false)) {
                currentOutSet.addNewStaticSite(name.getName().getID(), tirNode);
            }else{
                currentOutSet.decreaseReference(name.getName().getID());
            }
        }
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);
    }

    public void caseTIRCallStmt(TIRCallStmt tirNode){
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        String callName = tirNode.getFunctionName().getID();
        if(functionQuery.isUserDefinedFunction(callName)){
            // Tag input matrices as external
            // Built-in, we only need to handle output arguments.
            for(NameExpr expr: tirNode.getArguments().getNameExpressions()){
                if(!valueAnalysisUtil.isScalar(expr.getName().getID(), tirNode,true)){
                    currentOutSet.setDynamicallySiteAsExternal(expr.getName().getID());
                }
            }
        }

        // Handle output targets we only need to handle output arguments.
        for(NameExpr name: tirNode.getTargets().getNameExpressions()){
            if(!valueAnalysisUtil.isScalar(name.getName().getID(),tirNode,false)) {
                currentOutSet.addNewStaticSite(name.getName().getID(), tirNode);
            }else{
                currentOutSet.decreaseReference(name.getName().getID());
            }
        }
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);
    }

    /**
     * To handle input arguments, we are going to take a conservative approach and simply make all input names
     * dynamic, to do this we add the name to dynamic site, and increase the reference dynamically by 1.
     * @param tirNode TIRFunction
     */
    public void caseTIRFunction(TIRFunction tirNode){
        currentInSet = newInitialFlow();
        currentOutSet = copy(currentInSet);
        for(Name input_name: function.getInputParamList())
            currentOutSet.addExternalDynamicSite(input_name.getID());
        caseASTNode(function);
//        caseTIRStatementList(function.getStmtList());
        Set<String> output = function.getOutputParamList()
                .stream().map(Name::getID).collect(Collectors.toSet());
        // Initiate the dynamic site with the argument

        output.forEach((String name)->{
            if(!valueAnalysisUtil.isScalar(name, function,false)){
                currentOutSet.initiateDynamicSite(name);
            }
        });
        currentOutSet.freeRemaining(output);
        outFlowSets.put(function, currentOutSet);
        if(Debug) log(tirNode);

    }

    /**
     * For this particular statement, we know that i is an scalar, all we have to do is to remove the i (loop-variable)
     * reference
     * @param tirNode for i=low:step:high
     */
    public void caseTIRForStmt(TIRForStmt tirNode){
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        currentOutSet.decreaseReference(tirNode.getLoopVarName().getID());
        caseTIRStatementList(tirNode.getStatements());
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);



    }
    public void log(ASTNode node){
        System.out.println("IR Node:");
        System.out.println(node.getPrettyPrinted());
        System.out.println("Current In Set:");
        System.out.println(currentInSet);
        System.out.println("Current Out Set:");
        System.out.println(currentOutSet);
    }
}
