package matwably.analysis.ambiguous_scalar_analysis;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;

public class AmbiguousVariableAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<AmbiguousScalarMap> {
    private ValueAnalysisUtil valueAnalysisUtil;
    public AmbiguousVariableAnalysis(TIRFunction astNode, ValueAnalysisUtil valueAnalysisUtil) {
        super(astNode);
        this.valueAnalysisUtil = valueAnalysisUtil;
    }

    @Override
    public void caseTIRFunction(TIRFunction tirFunction) {
        currentInSet = newInitialFlow();
        currentOutSet = copy(currentInSet);
        for(Name name: tirFunction.getInputParamList())
            addDefinitionToOutSet(name.getID(), tirFunction, true);
        caseASTNode(tirFunction);
    }


    @Override
    public AmbiguousScalarMap merge(AmbiguousScalarMap a1, AmbiguousScalarMap a2) {
        return AmbiguousScalarMap.merge(a1,a1);
    }

    @Override
    public AmbiguousScalarMap copy(AmbiguousScalarMap ambiguousScalarMap) {
        return ambiguousScalarMap.copy();
    }

    @Override
    public AmbiguousScalarMap newInitialFlow() {
        return new AmbiguousScalarMap();
    }

    @Override
    public void caseTIRForStmt(TIRForStmt forStmt) {
        inFlowSets.put(forStmt,copy(currentInSet));
        currentOutSet = copy(currentInSet);
        addDefinitionToOutSet(forStmt.getLoopVarName().getID(), forStmt,false);
        caseASTNode(forStmt);
        outFlowSets.put(forStmt, currentOutSet);
    }

    @Override
    public void caseTIRAbstractAssignFromVarStmt(TIRAbstractAssignFromVarStmt tirAbstractAssignFromVarStmt) {
        addDefinitionToOutSet(((NameExpr)tirAbstractAssignFromVarStmt.getLHS()).getName().getID(),
                tirAbstractAssignFromVarStmt,false);
    }

    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt tirAbstractAssignToListStmt) {
        inFlowSets.put(tirAbstractAssignToListStmt,copy(currentInSet));
        currentOutSet = copy(currentInSet);
        for(NameExpr nameExpr:
                tirAbstractAssignToListStmt.getTargets().getNameExpressions()){
            addDefinitionToOutSet(nameExpr.getName().getID(), tirAbstractAssignToListStmt, false);
        }
        outFlowSets.put(tirAbstractAssignToListStmt, currentOutSet);
    }
    private void addDefinitionToOutSet(String name, ASTNode node, boolean isRHS){
        if(valueAnalysisUtil.isScalar(name,node,isRHS)){
            currentOutSet.addScalarDefinition(name, node);
        }else{
            currentOutSet.addMatrixDefinition(name, node);
        }
    }
}
