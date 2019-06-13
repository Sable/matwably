package matwably.analysis.intermediate_variable;

import ast.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.analysis.core.Def;
import natlab.utils.NodeFinder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UseDefDefUseChain {
    private ImmutableSetMultimap<Name, Def> useDefChain;
    private ImmutableSetMultimap<Def, Name> defUseChain;

    private ImmutableSetMultimap<ASTNode, Name> defs;
    private ImmutableSetMultimap<ASTNode, Name> uses;

    /**
     * Given an occurrence of a variable, return all of the reaching definitions.
     */
    public Set<Def> getDefs(Name use) {
        return useDefChain.get(use);
    }

    /**
     * Given a definition return all of the uses reached by this definition. Since
     * definitions can define multiple names, the resulting set might contain occurrences
     * of several different names.
     */
    public Set<Name> getUses(Def def) {
        return defUseChain.get(def);
    }

    /**
     Given a definition, and one of the names it defines, return all of the uses
     of that name reached by this definition.
     */
    public Set<Name> getUsesOf(String name, Def def) {
        return getUses(def).stream()
                .filter(n -> n.getID().equals(name))
                .collect(Collectors.toSet());
    }

    /**
     * Given a def, return the names defined by that def.
     */
    public Set<Name> getDefinedNames(Def def) {
        if (def instanceof Name) {
            if (((ASTNode<?>) def).getParent().getParent() instanceof Function) {
                return ImmutableSet.of((Name) def);
            }
            return ImmutableSet.of();
        }
        return getDefinedNames((Stmt) def);
    }

    /**
     * Given a statement, return the names defined by that statement.
     */
    public Set<Name> getDefinedNames(Stmt node) {
        return defs.get(node);
    }

    /**
     * Given a statement, and one of the names it defines, return the name nodes
     * corresponding to that name.
     */
    public Set<Name> getDefinedNamesOf(String name, Stmt node) {
        return getDefinedNames(node).stream()
                .filter(n -> n.getID().equals(name))
                .collect(Collectors.toSet());
    }
    /**
     * Given a statement. return the names used by that statement.
     */
    public Set<Name> getUsedNames(Stmt node) {
        return uses.get(node);
    }

    /**
     * Given a statement, and one of the names it uses, return the name nodes
     * corresponding to that name.
     */
    public Set<Name> getUsedNamesOf(String name, Stmt node) {
        return getUsedNames(node).stream()
                .filter(n -> n.getID().equals(name))
                .collect(Collectors.toSet());
    }
    private UseDefDefUseChain(
            ImmutableSetMultimap<Name, Def> useDefChain,
            ImmutableSetMultimap<Def, Name> defUseChain,
            ImmutableSetMultimap<ASTNode, Name> defs,
            ImmutableSetMultimap<ASTNode, Name> uses) {
        this.useDefChain = useDefChain;
        this.defUseChain = defUseChain;
        this.defs = defs;
        this.uses = uses;
    }

    public static UseDefDefUseChain fromReachingDefs(ReachingDefinitions reachingDefinitions,
                                                     boolean processArrays){
        Builder builder = new Builder(reachingDefinitions, processArrays);
        ((TIRFunction)reachingDefinitions.getTree()).tirAnalyze(builder);
        return builder.build();
    }
    private static class Builder extends TIRAbstractNodeCaseHandler {
        private final ReachingDefinitions reaching_defs;
        private final Set<String> globals = new HashSet<>();
        private Function function;

        private boolean processArrays;
        private ImmutableSetMultimap.Builder<Name, Def> useDefChainBuilder =
                ImmutableSetMultimap.builder();
        private ImmutableSetMultimap.Builder<Def, Name> defUseChainBuilder =
                ImmutableSetMultimap.builder();
        private ImmutableSetMultimap.Builder<ASTNode,  Name> defsBuilder =
                ImmutableSetMultimap.builder();
        private ImmutableSetMultimap.Builder<ASTNode, Name> usesBuilder =
                ImmutableSetMultimap.builder();


        private Builder(ReachingDefinitions defs, boolean processArrays){
            this.reaching_defs = defs;
            this.processArrays = processArrays;
            this.function = (Function) reaching_defs.getTree();
        }

        public Set<Def> getReachingDefSet(Name name){
            ASTNode parent;
            parent = NodeFinder.findParent(Stmt.class, name);
            if (parent == null){
                 parent = NodeFinder.findParent(Function.class, name);
            }

            if(parent instanceof TIRWhileStmt || parent instanceof TIRForStmt){
                return reaching_defs.getOutFlowSets().get(parent).get(name.getID());
            }else{
                return reaching_defs.getInFlowSets().get(parent) .get(name.getID());

            }

        }
        public Set<Def> getReachingDefSet(Stmt stmt, Name name){
            return reaching_defs.getInFlowSets().get(stmt).get(name.getID());
        }

        @Override
        public void caseTIRArraySetStmt(TIRArraySetStmt stmt) {
            // Process uses
            stmt.getIndices().getNameExpressions()
                    .stream().map(NameExpr::getName).forEach(this::addUse);
            if(processArrays){
                addDef(stmt.getArrayName());
            }
            this.addUse(stmt.getValueName());
        }
        public void caseTIRIfStmt(TIRIfStmt stmt){
            addUse(stmt.getConditionVarName());
            this.caseASTNode(stmt.getIfStatements());
            if(stmt.hasElseBlock())
                this.caseASTNode(stmt.getElseStatements());
        }
        @Override
        public void caseTIRForStmt(TIRForStmt stmt) {
            addDef(stmt.getLoopVarName());
            addUse(stmt.getLowerName());
            addUse(stmt.getUpperName());
            if(stmt.hasIncr()) addUse(stmt.getIncName());
            this.caseASTNode(stmt.getStatements());
        }
        @Override
        public void caseTIRWhileStmt(TIRWhileStmt stmt) {
            addUse(stmt.getCondition().getName());
            super.caseTIRWhileStmt(stmt);
        }

        @Override
        public void caseTIRArrayGetStmt(TIRArrayGetStmt stmt){
            stmt.getIndices().getNameExpressions()
                    .stream().map(NameExpr::getName).forEach(this::addUse);
            this.addDef(stmt.getTargetName());
            if(processArrays){
                addUse(stmt.getArrayName());
            }
        }

        @Override
        public void caseTIRReturnStmt(TIRReturnStmt stmt){
            this.function.getOutputParamList().getNameExpressions()
                    .stream().map(NameExpr::getName)
                    .forEach((Name use)->{
                        usesBuilder.put(function, use);
                        Set<Def> defs = this.getReachingDefSet(stmt, use);
                        useDefChainBuilder.putAll(use, defs);
                        for (Def def : defs) {
                            defUseChainBuilder.put(def, use);
                        }
                    });
        }
        @Override
        public void caseTIRFunction(TIRFunction function){
            function.getInputParamList()
                    .stream().forEach(this::addDef);
            super.caseTIRFunction(function);
        }

        @Override
        public void caseTIRCallStmt(TIRCallStmt stmt) {
            stmt.getArguments().getNameExpressions()
                    .stream().map(NameExpr::getName).forEach(this::addUse);
            stmt.getTargets().getNameExpressions()
                    .stream().map(NameExpr::getName).forEach(this::addDef);
        }

        @Override
        public void caseTIRGlobalStmt(TIRGlobalStmt stmt) {
            stmt.getNames().stream()
                    .filter((Name name)->
                            !this.globals.contains(name.getID()))
                    .forEach((Name name)->{
                        this.globals.add(name.getID());
                        this.addDef(name);
                    });
        }

        @Override
        public void caseTIRCopyStmt(TIRCopyStmt stmt) {
            addUse(stmt.getSourceName());
            addDef(stmt.getTargetName());
        }

        @Override
        public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt stmt) {
            addDef(stmt.getTargetName());
        }

        private void addDef(Name def){
            ASTNode parent;
            parent = NodeFinder.findParent(Stmt.class, def);
            if (parent == null){
                parent = NodeFinder.findParent(Function.class, def);
            }
            defsBuilder.put(parent, def);
        }
        private void addUse(Name use){
            ASTNode parent;
            parent = NodeFinder.findParent(Stmt.class, use);
            if (parent == null){
                parent = NodeFinder.findParent(Function.class, use);
            }
            usesBuilder.put(parent, use);
            Set<Def> defs = getReachingDefSet(use);
            if(defs != null){
                useDefChainBuilder.putAll(use, defs);
                for (Def def : defs) {
                    defUseChainBuilder.put(def, use);
                }
            }else{
                System.err.println("No definition for variable use: "+use.getID()+", at: "+parent.getPrettyPrinted());
            }
        }


//        @Override
//        public void caseNameExpr(NameExpr use) {
//            Stmt parentStmt = NodeFinder.findParent(Stmt.class, use);
//            if (reaching_defs.isDef(use.getName())) {
//                defsBuilder.put(parentStmt, use.getName());
//                return;
//            }
//            usesBuilder.put(parentStmt, use.getName());
//            Set<Def> defs = getReachingDefSet(use.getName());
//            useDefChainBuilder.putAll(use.getName(), defs);
//            for (Def def : defs) {
//                defUseChainBuilder.put(def, use.getName());
//            }
//        }


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
        private UseDefDefUseChain build() {
            return new UseDefDefUseChain(
                    useDefChainBuilder.build(),
                    defUseChainBuilder.build(),
                    defsBuilder.build(),
                    usesBuilder.build());
        }
    }
}
