package matwably.code_generation;

import ast.ASTNode;
import ast.Stmt;
import matwably.code_generation.stmt.StmtHook;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.HashMap;
import java.util.Map;

public class FunctionStmtHookMap {
    private Map<ASTNode, StmtHook> stmtHookMatWablyMap;

    private FunctionStmtHookMap(Map<ASTNode, StmtHook> map){
        this.stmtHookMatWablyMap = new HashMap<>(map);
    }

    public static FunctionStmtHookMap initializeFunctionHookMap(TIRFunction matlabFunction){
        Initializer builder = new Initializer();
        matlabFunction.analyze(builder);
        return new FunctionStmtHookMap(builder.getHookMap());
    }

    public FunctionStmtHookMap addHook(ASTNode stmt, StmtHook hook){
        if(!stmtHookMatWablyMap.containsKey(stmt)){
            stmtHookMatWablyMap.put(stmt, new StmtHook(hook));
        }else{
            stmtHookMatWablyMap.get(stmt).add(hook);
        }
        return this;
    }
    public FunctionStmtHookMap addHookMap(Map<ASTNode, StmtHook> map){
        for(Map.Entry<ASTNode, StmtHook> entry: map.entrySet()){
            if(!stmtHookMatWablyMap.containsKey(entry.getKey())){
                stmtHookMatWablyMap.put(entry.getKey(), new StmtHook(entry.getValue()));
            }else{
                stmtHookMatWablyMap.get(entry.getKey()).add(entry.getValue());
            }
        }
        return this;
    }
    public StmtHook getHook(ASTNode stmt){
        return this.stmtHookMatWablyMap.get(stmt);
    }


    private static class Initializer extends TIRAbstractNodeCaseHandler{

        private Map<ASTNode, StmtHook> hookMap = new HashMap<>();

        @Override
        public void caseStmt(Stmt stmt) {
            hookMap.put(stmt, new StmtHook());
            this.caseASTNode(stmt);
        }

        Map<ASTNode, StmtHook> getHookMap() {
            return hookMap;
        }
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
}
