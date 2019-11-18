package matwably.analysis.intermediate_variable;

import ast.ASTNode;
import ast.Function;
import ast.Stmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeDepthCollector extends TIRAbstractNodeCaseHandler {
    private Map<Stmt, List<Stmt>> depthStmtMap = new HashMap<>();
    private List<Stmt> currentList = new ArrayList<>();
    public void caseStmt(Stmt stmt){
        this.depthStmtMap.put(stmt, currentList);
        super.caseStmt(stmt);
    }

    /**
     * Private constructor
     */
    private NodeDepthCollector(){ }

    public static NodeDepthCollector collectStmtDepth(Function matlabFunction){
        NodeDepthCollector collector = new NodeDepthCollector();
        matlabFunction.analyze(collector);
        return collector;
    }

    public boolean stmtsHaveSameDepthPath(Stmt a, Stmt b){
        return depthStmtMap.get(a).equals(depthStmtMap.get(b));
    }
    /**
     * Add stmt to depth for current stmt
     * @param whileStmt
     */
    public void caseTIRWhileStmt(TIRWhileStmt whileStmt){
        List<Stmt> newList = new ArrayList<>(currentList);
        newList.add(whileStmt);
        currentList = newList;
        this.depthStmtMap.put(whileStmt, currentList);
        this.visitChildren(whileStmt);
        this.currentList.remove(this.currentList.size()-1);
    }


    public void caseTIRForStmt(TIRForStmt forStmt){
        List<Stmt> newList = new ArrayList<>(currentList);
        newList.add(forStmt);
        currentList = newList;
        this.depthStmtMap.put(forStmt, currentList);
        this.visitChildren(forStmt);
        this.currentList.remove(this.currentList.size()-1);
    }

    private void visitChildren(Stmt stmt){
        this.caseASTNode(stmt);
    }
    /**
     * This default function simply visits all children
     * @param astNode Matlab AST Node
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
