package matwably.analysis;


import ast.*;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralBackwardAnalysis;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.core.UseDefDefUseChain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class IntermediateVariableElimination {
    private final Function function;
    private final InterproceduralFunctionQuery programInstanceQuery;
    public HashMap<Name, Expr> use_expr_map = new HashMap<>(); // Expr here can be either LiteralExpr or Parametrized expr
    public Set<Stmt> redundant_stmts = new HashSet<>();
    public IntermediateVariableElimination(Function function,
                                           InterproceduralFunctionQuery
                                                   interproceduralFunctionQuery)
    {
        this.function = function;
        this.programInstanceQuery = interproceduralFunctionQuery;
    }
    public void apply() {
        ReachingDefs reachDef = new ReachingDefs(function);
        reachDef.analyze();
        TreeExpressionForwardAnalysis treeExpr =
                new TreeExpressionForwardAnalysis(reachDef.getUseDefDefUseChain(),
                        programInstanceQuery);
        function.analyze(treeExpr);
        use_expr_map = treeExpr.use_expr_map;
        redundant_stmts = treeExpr.redundant_stmts;

        System.out.println("REDUNDANT STATEMENTS");
        treeExpr.redundant_stmts.stream().map(Stmt::getPrettyPrinted).forEach(System.out::println);
        System.out.println("USES & CORRESPONDING EXPRESSIONS");
        treeExpr.use_expr_map.forEach((Name name, Expr expr)->System.out.println("Name: "+name.getID()+
        ", Corresponding Expr: " + expr.getPrettyPrinted()));
    }
    private static class TreeExpressionForwardAnalysis extends TIRAbstractNodeCaseHandler {
        UseDefDefUseChain chain;
        InterproceduralFunctionQuery interproceduralFunctionQuery;
        HashMap<Name, Expr> use_expr_map = new HashMap<>(); // Expr here can be either LiteralExpr or Parametrized expr
        Set<Stmt> redundant_stmts = new HashSet<>();

        public TreeExpressionForwardAnalysis( UseDefDefUseChain chain,
                InterproceduralFunctionQuery interproceduralFunctionQuery) {
            this.chain = chain;
            this.interproceduralFunctionQuery = interproceduralFunctionQuery;
        }
        private void mapUsesToExpression(Set<Name> uses, Expr rhs){
            uses.forEach((Name name)->use_expr_map.put(name, rhs));
        }
        private void addRedundantStmt(Stmt stmt){
            redundant_stmts.add(stmt);
        }
        @Override
        public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt stmt) {
            // Check if its
            Expr rhs = stmt.getRHS();
            if(rhs instanceof IntLiteralExpr || rhs instanceof FPLiteralExpr ){
                Set<Name> usesDef = this.chain.getUsesOf(stmt.getTargetName().getID(), stmt);
                Set<Name> uniqueUses =  this.chain.getDefinedNames((Def)stmt)
                        .stream().map((Name name)-> this.chain.getUsesOf(name.getID(), stmt))
                        .flatMap((Set<Name> uses)-> uses.stream().filter((Name name)->{
                            Set<Def> defs = this.chain.getDefs(name);
                            return defs.size() == 1;
                        })).collect(Collectors.toSet());
                mapUsesToExpression(uniqueUses, rhs);
                if(uniqueUses.size() == usesDef.size()) addRedundantStmt(stmt);
            }
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
    private static class TreeExpressionBuild extends TIRAbstractSimpleStructuralBackwardAnalysis<HashSet<Stmt>> {

        UseDefDefUseChain chain;
        InterproceduralFunctionQuery interproceduralFunctionQuery;
        public TreeExpressionBuild(ASTNode<?> astNode, InterproceduralFunctionQuery interproceduralFunctionQuery) {
            super(astNode);
            ReachingDefs reachDef = new ReachingDefs(astNode);
            reachDef.analyze();
            chain = reachDef.getUseDefDefUseChain();
            this.interproceduralFunctionQuery = interproceduralFunctionQuery;
        }
        public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt assignLiteralStmt) {
            System.out.println("ANALYSIS");

            System.out.println(assignLiteralStmt.getPrettyPrinted());
            Set<Name> names = chain.getDefinedNames((Stmt)assignLiteralStmt);
            names.stream().map(Name::getID).forEach(System.out::println);

            System.out.println("ANALYSIS DONE");

        }
        public void caseTIRCallStmt(TIRCallStmt callStmt) {
            System.out.println("ANALYSIS CALL STMT");
            System.out.println(callStmt.getPrettyPrinted());
            Set<Name> names = chain.getUsedNames(callStmt);

            names.stream()
                    .filter((Name name) -> Builtin.getInstance(name.getID()) == null
                                && !interproceduralFunctionQuery.
                                isUserDefinedFunction(name.getID()))
                .forEach((Name name)->{
                    System.out.println("DEF of USED NAMES IN CALL STMT");
                    Set<Def> defs = chain.getDefs(name);
                    if(defs.size() == 1){
                        Def def =defs.iterator().next();
                        if(def instanceof Stmt){
                                System.out.println(((Stmt)def).getPrettyPrinted());
                        }else{
                            System.out.println(def.getClass());
                        }
                    }else{
                        System.out.println("Use has more than 1 definition, therefore ambigous");
                    }

                });
            System.out.println("ANALYSIS DONE");
        }

        @Override
        public HashSet<Stmt> merge(HashSet<Stmt> stmts, HashSet<Stmt> a1) {
//            stmts.addAll(a1);
            return  new HashSet<>();
        }

        @Override
        public HashSet<Stmt> copy(HashSet<Stmt> stmts) {
            return  new HashSet<>();
        }

        @Override
        public HashSet<Stmt> newInitialFlow() {
            return new HashSet<>();
        }
    }
    private static class LiteralElimination extends TIRAbstractNodeCaseHandler {
        ReachingDefs reachingDefAnalysis;
        UseDefDefUseChain chain;

        public HashMap<Expr, Number> getConstantMapping() {
            return map_constants;
        }

        private HashMap<Expr, Number> map_constants = new HashMap<>();
        public LiteralElimination(Function func, ReachingDefs reachingDef){
            reachingDefAnalysis = reachingDef;
            chain =  reachingDef.getUseDefDefUseChain();
            func.analyze(this);
            System.out.println(map_constants);
        }


        public void caseASTNode(ASTNode node){
            for (int i = 0; i < node.getNumChild(); i++) {
                ASTNode child = node.getChild(i);
                if (child instanceof TIRNode) {
                    ((TIRNode) child).tirAnalyze(this);
                }
                else {
                    child.analyze(this);
                }
            }
        }

        public void caseTIRCallStmt(TIRCallStmt callStmt){
            for(Expr name: callStmt.getArguments()){
                NameExpr nameExpr = (NameExpr) name;
                Set<Def> definitions = chain.getDefs(nameExpr.getName());
                if(definitions.size() == 1){
                    Def def = definitions.iterator().next();

                     if(def instanceof TIRAssignLiteralStmt ){
                         Expr expr = ((TIRAssignLiteralStmt) def).getRHS();
                         if(expr instanceof IntLiteralExpr){
                            Number val = ((IntLiteralExpr) expr).getValue().getValue();
                             map_constants.put(name, val);
                         }else if(expr instanceof FPLiteralExpr){
                            Number val = ((FPLiteralExpr) expr).getValue().getValue();
                            map_constants.put(name, val);
                         }
                     }
                 }
            }
//                for (Def tempDef : definitions) {
//                    if(tempDef instanceof TIRAssignLiteralStmt){
//                        TIRAssignLiteralStmt liteDef = (TIRAssignLiteralStmt) tempDef;
//                        System.out.println(liteDef.getRHS().getPrettyPrinted());
//
//                    }
//                }
        }
    }

}
