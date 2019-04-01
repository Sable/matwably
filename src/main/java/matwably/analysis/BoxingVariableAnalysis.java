package matwably.analysis;

import ast.ASTNode;
import ast.NameExpr;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

public class BoxingVariableAnalysis extends TIRAbstractNodeCaseHandler {
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt tirArrayGetStmt) {
        tirArrayGetStmt.getTargets().stream().map(NameExpr.class::cast)
                .forEach((NameExpr targets)->{

        });
    }

    @Override
    public void caseTIRCopyStmt(TIRCopyStmt tirCopyStmt) {

    }

    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt tirAssignLiteralStmt) {

    }

    @Override
    public void caseTIRCallStmt(TIRCallStmt tirCallStmt) {

    }

    @Override
    public void caseTIRForStmt(TIRForStmt tirForStmt) {

    }

    @Override
    public void caseTIRFunction(TIRFunction tirFunction) {

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
