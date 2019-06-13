package matwably.analysis.intermediate_variable;

import ast.*;
import com.google.common.collect.Sets;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.core.Def;
import natlab.utils.NodeFinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReachingDefinitions extends TIRAbstractSimpleStructuralForwardAnalysis<Map<String, Set<Def>>> {

    private boolean array_definitions;

    private HashMap<String, Set<Def>> initialMap;
    public Set<Name> getDefs() {
        return defs;
    }

    private Set<Name> defs = new HashSet<>();
    private Set<String> globals = new HashSet<>();


    public ReachingDefinitions(ASTNode<?> astNode) {
        super(astNode);
        this.array_definitions = true;
        if(astNode instanceof TIRFunction) createInitialMap((TIRFunction) astNode);
    }

    public ReachingDefinitions(TIRFunction function){
        super(function);
        this.array_definitions = true;
        createInitialMap(function);
    }

    public ReachingDefinitions(ASTNode<?> astNode, boolean array_definitions){
        super(astNode);
        this.array_definitions = true;
        if(astNode instanceof TIRFunction) createInitialMap((TIRFunction) astNode);
    }
    private void createInitialMap(TIRFunction func){
        initialMap = new HashMap<>();
        func.getInputParamList().stream().forEach(
                (Name param)-> {
                    initialMap.put(param.getID(), Sets.<Def>newHashSet(param));
                });
    }
    public boolean isNameGlobalDefinition(Name name){
        if(!defs.contains(name)) return false;
        GlobalStmt stmt = NodeFinder.findParent(GlobalStmt.class, name);
        return stmt != null;
    }
    public boolean isNameArgumentDefinition(Name name){
        if(!defs.contains(name)) return false;
        TIRFunction stmt = NodeFinder.findParent(TIRFunction.class, name);
        return stmt.getInputParamList().stream().anyMatch((Name param)-> param == name);
    }

    public boolean isNameAssignStmtDefinition(Name name){
        if(!defs.contains(name)) return false;
        AssignStmt stmt = NodeFinder.findParent(AssignStmt.class, name);
        return stmt!= null;
    }


    public boolean isDef(Name name){
        return defs.contains(name);
    }

    @Override
    public void caseTIRFunction(TIRFunction func){
        currentInSet = newInitialFlow();
        currentOutSet = copy(currentInSet);
        caseASTNode(func);
    }

    private void processDef(Name name, Def def) {
        String varName = name.getID();
        currentOutSet.remove(varName);
        Set<Def> tmpDefs = new HashSet<>();
        tmpDefs.add(def);
        currentOutSet.put(varName, tmpDefs);
        defs.add(name);
    }
    @Override
    public void caseTIRCopyStmt(TIRCopyStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        processDef(stmt.getTargetName(), stmt);
        outFlowSets.put(stmt, copy(currentOutSet));
    }

//    @Override
//    public void caseTIRForStmt(TIRForStmt stmt){
//        inFlowSets.put(stmt, copy(currentInSet));
//        currentOutSet = copy(currentInSet);
////        this.caseAssignStmt(stmt.getAssignStmt());
//        super.caseAssignStmt(stmt.getAssignStmt());
////        processDef(stmt.getLoopVarName(),stmt.getAssignStmt());
//        super.caseTIRForStmt(stmt);
//        outFlowSets.put(stmt, copy(currentOutSet));
//    }
    @Override
    public void caseAssignStmt(AssignStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        processDef(((NameExpr) stmt.getLHS()).getName(), stmt);
        outFlowSets.put(stmt, copy(currentOutSet));
    }
    /**
     * A global definitions are only added they have not been declared inside the context of the function.
     * In terms of execution, globals in Matlab are not hoisted to the top, this means variable names
     * are locals until the Global Stmt is declared. After this happens, if there are further global
     * stmts, they are practically ignored by the compiler. In terms of reaching definitions.
     * The definition of a new global, means the start of a new site for the variable. This variable
     * may have been assigned in some other function which executed earlier, which we do not know if the
     * global has been declared in the program statically.
     * @param stmt TameIR's global statement to process
     */
    @Override
    public void caseTIRGlobalStmt(TIRGlobalStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        stmt.getNames().forEach((Name name)->addGlobalDef(name, stmt));
        outFlowSets.put(stmt, currentOutSet);
    }

    private void addGlobalDef(Name name, TIRGlobalStmt stmt) {
        String id = name.getID();
        if(!globals.contains(id)){
            globals.add(id);
            processDef(name, stmt);
        }
    }

    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        processDef(stmt.getTargetName(), stmt);
        outFlowSets.put(stmt, currentOutSet);
    }

    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        for(NameExpr nameExpr: stmt.getTargets().getNameExpressions()){
            processDef(nameExpr.getName(), stmt);
        }
        outFlowSets.put(stmt, copy(currentOutSet));
    }


    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        if(array_definitions){
            Name arrayName = stmt.getArrayName();
            processDef(arrayName, stmt);
        }
        outFlowSets.put(stmt, copy(currentOutSet));
    }

    @Override
    public void caseStmt(ast.Stmt node) {
        inFlowSets.put(node, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        outFlowSets.put(node, copy(currentOutSet));
    }

    @Override
    public Map<String, Set<Def>> merge(Map<String, Set<Def>> a1, Map<String, Set<Def>> a2) {
        Map<String, Set<Def>> other = new HashMap<>();
        for(Map.Entry<String, Set<Def>> entry: a1.entrySet()){
            other.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        for(Map.Entry<String, Set<Def>> entry: a2.entrySet()){
            if(other.containsKey(entry.getKey())){
                other.get(entry.getKey()).addAll(entry.getValue());
            }else{
                other.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
        }

        return other;
    }

    @Override
    public Map<String, Set<Def>> copy(Map<String, Set<Def>> a1) {
        Map<String, Set<Def>> other = new HashMap<>();
        for(Map.Entry<String, Set<Def>> entry: a1.entrySet()){
            other.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return other;
    }

    @Override
    public Map<String, Set<Def>> newInitialFlow() {
        return new HashMap<>(initialMap);
    }
    private UseDefDefUseChain udduChainCached = null;

    public UseDefDefUseChain getUseDefDefUseChain() {
        if (udduChainCached == null) {
            udduChainCached = UseDefDefUseChain.fromReachingDefs(this, this.array_definitions);
        }
        return udduChainCached;
    }
//    @Override
//    public void caseASTNode(ASTNode astNode) {
//        for (int i = 0; i < astNode.getNumChild(); i++) {
//            ASTNode child = astNode.getChild(i);
//            if (child instanceof TIRNode) {
//                ((TIRNode) child).tirAnalyze(this);
//            }
//            else {
//                child.analyze(this);
//            }
//        }
//    }
}
