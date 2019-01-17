package matwably.analysis;

import ast.Name;
import ast.NameExpr;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.core.Def;

public class ModifiedArrayAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<UntouchedVariableMap> {

    public ModifiedArrayAnalysis(TIRFunction astNode) {
        super(astNode);
    }

    @Override
    public void caseTIRCopyStmt(TIRCopyStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        replaceDefinition(stmt.getTargetName().getID(), stmt);
    }

    @Override
    public void caseTIRFunction(TIRFunction func){
        currentInSet = newInitialFlow();
        currentOutSet = copy(currentInSet);
        for(Name param: func.getInputParamList())  currentOutSet.put(param.getID(),param);
        caseASTNode(func);
    }
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        replaceListVariableDefinitions(stmt.getTargets(), stmt);
    }
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        currentOutSet.remove(stmt.getArrayName().getID());
    }
    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        replaceDefinition(stmt.getTargetName().getID(), stmt);
    }
    @Override
    public void caseTIRCallStmt(TIRCallStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        for(ast.Expr expr: stmt.getTargets()){
            NameExpr nameExpr = (NameExpr) expr;
            replaceDefinition(nameExpr.getName().getID(), stmt);
        }
    }

    @Override
    public UntouchedVariableMap merge(UntouchedVariableMap untouchedVariableMap, UntouchedVariableMap a1) {
        return untouchedVariableMap.merge(a1);
    }

    @Override
    public UntouchedVariableMap copy(UntouchedVariableMap untouchedVariableMap) {
        return untouchedVariableMap.copy();
    }

    @Override
    public UntouchedVariableMap newInitialFlow() {
        return new UntouchedVariableMap();
    }

    @Override
    public void caseStmt(ast.Stmt node) {

        inFlowSets.put(node, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        outFlowSets.put(node, copy(currentOutSet));
    }
    // Helper functions
    private void replaceDefinition(String name, Def stmt){
        if(currentOutSet.containsKey(name)){
            currentOutSet.remove(name);
            currentOutSet.put(name,stmt);
        }else{
            currentOutSet.put(name,  stmt);
        }
    }
    private void replaceListVariableDefinitions(TIRCommaSeparatedList targets, Def stmt){
        for(ast.Expr expr: targets){
            NameExpr nameExpr = (NameExpr) expr;
            replaceDefinition(nameExpr.getName().getID(), stmt);
        }

    }
}
