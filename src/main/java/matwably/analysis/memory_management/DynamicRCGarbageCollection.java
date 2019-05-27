package matwably.analysis.memory_management;

import ast.ASTNode;
import ast.Function;
import ast.NameExpr;
import ast.Stmt;
import matjuice.transformer.MJCopyStmt;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.
        Collectors;

public class DynamicRCGarbageCollection extends TIRAbstractNodeCaseHandler{
    private final ValueAnalysisUtil valueUtil;

    /**
     * Set keeps track of currently defined sites in the program
     */
    private Set<String> definedSites = new HashSet<>();


    /**
     * Map from TameIR nodes to GCCallsSet
     */
    private Map<ASTNode, GCCallsSet> stmt_mapping = new HashMap<>();

    /**
     * Matlab function to analyze
     */
    private Function function  = null;

    private ASTNode node;

    /**
     * Constructor for analysis for other nodes.
     * @param node Matlab node to traverse in the analysis
     * @
     */
    public DynamicRCGarbageCollection(ASTNode<? extends ASTNode> node, ValueAnalysisUtil valueAnalysisUtil) {
        if(node instanceof Function){
            this.function = (Function) node;
        }
        this.node = node;
        this.valueUtil = valueAnalysisUtil;
    }
    /**
     * Constructor for analysis for dynamic sites.
     * @param function  Matlab function to traverse in the analysis
     * @
     */
    public DynamicRCGarbageCollection(TIRFunction function, ValueAnalysisUtil valueAnalysisUtil) {
        this.function = function;
        this.valueUtil = valueAnalysisUtil;
        this.node = function;
    }

    public void apply(){
        node.analyze(this);
    }

    @Override
    public void caseTIRFunction(TIRFunction tirFunction) {
        GCCallsSet set = initializeSites();
        // Mapp function to input parameters
        stmt_mapping.put(tirFunction, set);
        super.caseTIRFunction(tirFunction);
        GCCallsSet ret = processReturn();
        TIRStatementList stmtList = tirFunction.getStmtList();
        Stmt lastStmt = stmtList.getChild(stmtList.getNumChild() - 1);
        stmt_mapping.get(lastStmt)
                .merge(ret);

    }

    private GCCallsSet initializeSites(){
        GCCallsSet set = new GCCallsSet();
        definedSites.addAll(function.getInputParamList().getNameExpressions()
                .stream().map(NameExpr::getVarName).filter((String param)->{
                    if(!valueUtil.isScalar(param, function, true)){
                        set.increaseReference(param);
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
    public Map<ASTNode, GCCallsSet> getGcCallsMapping() {
        return stmt_mapping;
    }
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt tirAbstractAssignToListStmt) {
        GCCallsSet res = new GCCallsSet();
        tirAbstractAssignToListStmt.getTargets()
                .getNameExpressions().forEach((NameExpr nameExpr)->{
            addDynamicCalls(res, nameExpr.getVarName(), tirAbstractAssignToListStmt);
        });
        stmt_mapping.put(tirAbstractAssignToListStmt, res);
    }
    private void addDynamicCalls(GCCallsSet res, String name, ASTNode node){
        if(siteDefined(name)){
            res.decreaseReference(name);
        }
        if(!valueUtil.isScalar(name, node, false)){
            removeDefinedSite(name);
        }else {
            res.increaseReference(name);
        }
        if(!siteDefined(name))
            addDefinedSite(name);
    }
    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt tirAbstractAssignToVarStmt) {
        GCCallsSet res = new GCCallsSet();
        addDynamicCalls(res, tirAbstractAssignToVarStmt.getVarName(), tirAbstractAssignToVarStmt);
        stmt_mapping.put(tirAbstractAssignToVarStmt, res);
    }

    @Override
    public void caseTIRCopyStmt(TIRCopyStmt tirCopyStmt) {
        GCCallsSet res = new GCCallsSet();
        addDynamicCalls(res, tirCopyStmt.getVarName(), tirCopyStmt);
        boolean isScalar = valueUtil.isScalar(tirCopyStmt.getSourceName().getID(), tirCopyStmt,true);
        if(!(tirCopyStmt instanceof MJCopyStmt) && isScalar){
            res.increaseReference(tirCopyStmt.getSourceName().getID());
        }
        stmt_mapping.put(tirCopyStmt, res);
    }

    @Override
    public void caseStmt(Stmt tirStmt) {
        GCCallsSet res = new GCCallsSet();
        stmt_mapping.put(tirStmt, res);
    }

    @Override
    public void caseTIRForStmt(TIRForStmt tirForStmt) {
        GCCallsSet res = new GCCallsSet();
        if(siteDefined(tirForStmt.getLoopVarName().getID()))
            res.decreaseReference(tirForStmt.getLoopVarName().getID());
        super.caseTIRForStmt(tirForStmt);
    }

    @Override
    public void caseTIRReturnStmt(TIRReturnStmt tirReturnStmt) {
        if(function != null){
            stmt_mapping.put(tirReturnStmt, processReturn());
        }
    }
    private GCCallsSet processReturn(){
        GCCallsSet res = new GCCallsSet();
        // For all defined variables not part of return values, free!
        // For all the defined variables, set reference count to 1.
        Set<String> outParams = function
                .getOutputParamList()
                .getNameExpressions()
                .stream().map(NameExpr::getVarName)
                .peek(outName ->{
                    if(siteDefined(outName)) res.setRCToOne(outName);
                }).collect(Collectors.toSet());
        // For the rest of sites, check that they are not external
        // free if they not call.
        Set<String> restSites = new HashSet<>(definedSites);
        restSites.removeAll(outParams);
        restSites.forEach(res::addCheckExternalAndFreeSite);
        return res;
    }

    private boolean siteDefined(String varName) {
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
