package matwably.analysis.memory_management.dynamic;

import ast.*;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicRCGarbageCollection extends TIRAbstractNodeCaseHandler{
    private final ValueAnalysisUtil valueUtil;
    private final InterproceduralFunctionQuery functionQuery;

    /**
     * Set keeps track of currently defined sites in the program
     */
    private Set<String> definedSites = new HashSet<>();

    /**
     * Map from TameIR nodes to DynamicRCSet
     */
    private Map<ASTNode, DynamicRCSet> stmt_mapping = new HashMap<>();

    /**
     * Matlab function to analyze
     */
    private Function function  = null;

    private ASTNode node;

    /**
     * Its applied in an arbitrary ASTNode
     * @param node TameIR ast Node
     * @param valueAnalysisUtil parameters helps us find out whether the variable is an array or a scalar
     * @param functionQuery Function query to determine dynamic calls
     */
    public DynamicRCGarbageCollection(ASTNode<? extends ASTNode> node, ValueAnalysisUtil valueAnalysisUtil, InterproceduralFunctionQuery functionQuery) {
        this.functionQuery = functionQuery;
        if(node instanceof Function){
            this.function = (Function) node;
        }
        this.node = node;
        this.valueUtil = valueAnalysisUtil;
    }
    /**
     * Its applied in an arbitrary ASTNode
     * @param function TameIR Function Node
     * @param valueAnalysisUtil parameters helps us find out whether the variable is an array or a scalar
     * @param functionQuery Function query to determine dynamic calls
     */
    public DynamicRCGarbageCollection(TIRFunction function, ValueAnalysisUtil valueAnalysisUtil,
                                      InterproceduralFunctionQuery functionQuery) {
        this.function = function;
        this.valueUtil = valueAnalysisUtil;
        this.node = function;
        this.functionQuery = functionQuery;
    }

    /**
     * Runs the analysis once instantiated
     */
    public void analyze(){
        node.analyze(this);
    }

    /**
     * Function visitor.
     *  - Adds argument paramters to defined set
     *  - Processes tirFunction visitor
     *  - To the last statement,
     *    it merges the return handle
     *
     * @param tirFunction TameIR Function
     */
    @Override
    public void caseFunction(Function tirFunction) {
        TIRFunction func = (TIRFunction) tirFunction;
        DynamicRCSet set = initializeSites();
        // Mapp function to input parameters
        stmt_mapping.put(func, set);
        super.caseFunction(func);
        if( tirFunction.getStmtList().getChild(tirFunction.getStmtList()
                .getNumChild()-1).getClass()
                != TIRReturnStmt.class) {
            DynamicRCSet ret = processReturn();
            TIRStatementList stmtList = func.getStmtList();
            Stmt lastStmt = stmtList.getChild(stmtList.getNumChild() - 1);
            stmt_mapping.get(lastStmt)
                    .merge(ret);
        }
    }

    /**
     * Adds all the initial set which is composed of the arguments. In terms of arguments,
     * we never modify external sites.
     * @return Set of GC calls that initializes the sites
     */
    private DynamicRCSet initializeSites(){
        DynamicRCSet set = new DynamicRCSet();
        definedSites.addAll(function.getInputParamList().getNameExpressions()
                .stream().map(NameExpr::getVarName).filter((String param)->{
                    if(!valueUtil.isScalar(param, function, true)){
                        // To not touch external sites.
                        addDefinedSite(param);
                        return true;
                    }
                    return false;
                }).collect(Collectors.toSet()));
        return set;
    }

    /**
     * Getter for map of nodes to GcCallSets
     * @return Map of program nodes to GCCallSet
     */
    public Map<ASTNode, DynamicRCSet> getGcCallsMapping() {
        return stmt_mapping;
    }


    /**
     * Stmts of the for [a,b,...c] = A(i,j) or [a,b,...,c] = call().
     * If its a user_defined function, we add external tag to the site of the arguments passed.
     * @param tirAbstractAssignToListStmt TameIR Node
     */
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt tirAbstractAssignToListStmt) {
        DynamicRCSet res = new DynamicRCSet();
        tirAbstractAssignToListStmt.getTargets()
                .getNameExpressions().forEach((NameExpr nameExpr)->{
            addDefGCCalls(res, nameExpr.getVarName(), tirAbstractAssignToListStmt);
        });
        // If is a call stmt and is a used_defined function
        if(tirAbstractAssignToListStmt instanceof TIRCallStmt)
        {
            TIRCallStmt callStmt = (TIRCallStmt)tirAbstractAssignToListStmt;
            if(this.functionQuery.isUserDefinedFunction(callStmt.
                    getFunctionName().getID())){
                callStmt.getArguments()
                    .getNameExpressions().stream().map(NameExpr::getName).map(Name::getID)
                    .filter((String name)-> !valueUtil.isScalar(name, callStmt, true))
                    .forEach(res::checkAndAddExternalFlag);
            }

        }
        stmt_mapping.put(tirAbstractAssignToListStmt, res);
    }

    /**
     *  Adds a particular variable definition. Here are the steps:
     *      - If variable is already defined, decrease the reference
     *      - If variable is an array (not scalar), increase reference of result,
     *        and if its not defined in set, add name to defined variables.
     *      - If variable is a scalar, remove from defined sites.
     * @param res Result set for the GC operation
     * @param name Name of variable being defiend
     * @param node TameIR node for definition
     */
    private void addDefGCCalls(DynamicRCSet res, String name, ASTNode node){
        // If is already defined, decrease the reference for the site already defined
        if(isSiteDefined(name))
            res.decreaseReference(name);
        // If is
        boolean isScalar = valueUtil.isScalar(name, node,false);
        if(!isScalar){
            res.increaseReference(name);
            addDefinedSite(name);
        }else{
            removeDefinedSite(name);
        }
    }

    /**
     * Takes care of two statements a=b, and a = 1, adds the definition of variable `a`
     * @param tirAbstractAssignToVarStmt TameIR Node
     */
    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt tirAbstractAssignToVarStmt) {
        DynamicRCSet res = new DynamicRCSet();
        addDefGCCalls(res, tirAbstractAssignToVarStmt.getVarName(), tirAbstractAssignToVarStmt);
        stmt_mapping.put(tirAbstractAssignToVarStmt, res);
    }



    /**
     * Case stmt, if it does not hit our cases, then simply add map an empty set
     * @param tirStmt Stmt node
     */
    @Override
    public void caseStmt(Stmt tirStmt) {
        DynamicRCSet res = new DynamicRCSet();
        stmt_mapping.put(tirStmt, res);
        super.caseStmt(tirStmt);
    }
    /**
     * Case stmt, if it does not hit our cases, then simply add map an empty set
     * @param tirStmt Stmt node
     */
    @Override
    public void caseTIRCommentStmt(TIRCommentStmt tirStmt) {
        DynamicRCSet res = new DynamicRCSet();
        stmt_mapping.put(tirStmt, res);
        super.caseTIRCommentStmt(tirStmt);
    }

    /**
     * Assume that for-loop is not empty!
     * @param tirForStmt For-loop statement node
     */
    @Override
    public void caseTIRForStmt(TIRForStmt tirForStmt) {
        DynamicRCSet res = new DynamicRCSet();
        // We know the loop variables is a scalar
        if(isSiteDefined(tirForStmt.getLoopVarName().getID()))
            res.decreaseReference(tirForStmt.getLoopVarName().getID());
        super.caseTIRForStmt(tirForStmt);
        stmt_mapping.put(tirForStmt,res);
    }

    /**
     * Process return statement {@link DynamicRCGarbageCollection#processReturn()}
     * @param tirReturnStmt TIRReturnStmt node
     */
    @Override
    public void caseTIRReturnStmt(TIRReturnStmt tirReturnStmt) {
        stmt_mapping.put(tirReturnStmt, processReturn());
    }
    private DynamicRCSet processReturn(){
        DynamicRCSet res = new DynamicRCSet();
        // For all defined variables not part of return values,
        // free unless external
        // i.e. mark sites as external at runtime.
        // For all the returned variables, set RC to 0, since they are NOT
        // mapped to input arguments,
        Set<String> outParams = function
                .getOutputParamList()
                .stream().map(Name::getID).collect(Collectors.toSet());
        outParams = outParams.stream().filter(this::isSiteDefined)
                .peek(res::addCheckExternalAndSetReturnFlagToSetRCToZero)
                .collect(Collectors.toSet());
        // For the rest of sites, check that they are not external
        // free if they not call.
        Set<String> restSites = new HashSet<>(definedSites);
        restSites.removeAll(outParams);
        restSites.forEach(res::addCheckExternalAndCheckReturnFlagToFree);
        return res;
    }

    private boolean isSiteDefined(String varName) {
        return definedSites.contains(varName);
    }

    private void addDefinedSite(String site){
        definedSites.add(site);
    }

    private void removeDefinedSite(String site){
        definedSites.remove(site);
    }

    @Override
    public void caseASTNode(ASTNode astNode) {
        for(int i = 0; i < astNode.getNumChild(); ++i) {
            ASTNode child = astNode.getChild(i);
            if (child instanceof TIRNode) {
                ((TIRNode)child).tirAnalyze(this);
            } else {
                child.analyze(this);
            }
        }
    }
}
