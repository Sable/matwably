package matwably.analysis;


import ast.*;
import matwably.util.InterproceduralFunctionQuery;
import natlab.FPNumericLiteralValue;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.NodeFinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class performs an analysis over the AST collecting a map of Variable names to expressions, and statements to be
 * eliminated. The actual transformation is performed by the code_generation package:
 *
 */
public class IntermediateVariableAnalysis {
    /**
     * TameIR function Node
     */
    private final TIRFunction function;
    /**
     * Query for user functions in Matlab
     */
    private final InterproceduralFunctionQuery programInstanceQuery;
    /**
     * Variable to expression map generated by the analysis
     */
    public HashMap<Name, Expr> use_expr_map = new HashMap<>(); // Expr here can be either LiteralExpr or Parametrized expr
    /**
     * Set of statements to eliminate, specification of this analysis given by
     */
    public Set<Stmt> redundant_stmts = new HashSet<>();
    /**
     * Map of aliases, if a particular CopyStmt is deleted these mapping structure keeps track of the aliases formed.
     */
    public HashMap<Name, Name> aliases_map = new HashMap<>();
    /**
     * Contructor, takes the Function node to analyze, and the CallGraph InterproceduralFunctionQuery.
     * @param function TameIR Function Node
     * @param interproceduralFunctionQuery CallGraph for the Matlab program.
     */
    public IntermediateVariableAnalysis(TIRFunction function,
                                        InterproceduralFunctionQuery
                                                   interproceduralFunctionQuery)
    {
        this.function = function;
        this.programInstanceQuery = interproceduralFunctionQuery;
    }

    /**
     * Performs the actual analysis for the Function AST.
     */
    public void apply() {
        ReachingDefs reachDef = new ReachingDefs(function);
        reachDef.analyze();

        ModifiedArrayAnalysis mod_vars = new ModifiedArrayAnalysis(function);
        function.tirAnalyze(mod_vars);


        TreeExpressionForwardAnalysis treeExpr =
                new TreeExpressionForwardAnalysis(
                        function,
                        reachDef.getUseDefDefUseChain(),
                        mod_vars,
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

    /**
     * Private visitor class to run the ForwardAnalysis.
     */
    private static class TreeExpressionForwardAnalysis extends TIRAbstractNodeCaseHandler {
        private final TIRFunction function;
        private final UseDefDefUseChain chain;
        private final ModifiedArrayAnalysis mod_vars_analysis;
        private final InterproceduralFunctionQuery interproceduralFunctionQuery;
        private final Set<String> output_params;
        HashMap<Name, Expr> use_expr_map = new HashMap<>(); // Expr here can be either LiteralExpr or Parametrized expr
        Set<Stmt> redundant_stmts = new HashSet<>();

        /**
         * Contructor for TreeExpression mapping
         * @param function
         * @param chain
         * @param mod_vars
         * @param interproceduralFunctionQuery
         */
        TreeExpressionForwardAnalysis(TIRFunction function, UseDefDefUseChain chain,
                                      ModifiedArrayAnalysis mod_vars, InterproceduralFunctionQuery interproceduralFunctionQuery) {
            this.function = function;
            this.output_params = function.getOutputParams().stream().map(Name::getID).collect(Collectors.toSet());
            // TODO check for empty list of output params
            this.chain = chain;
            this.mod_vars_analysis = mod_vars;
            this.interproceduralFunctionQuery = interproceduralFunctionQuery;
        }
        private boolean isNotOutputParameter(String varName){
            return this.output_params.stream().noneMatch((String name)->name.equals(varName));
        }
        /**
         *
         * @param uses Set of uses for a given variable definition
         * @param rhs Expressions to use in order to eliminate the variable definition
         */
        private void mapUsesToExpression(Set<Name> uses, Expr rhs){
            uses.forEach((Name name)->use_expr_map.put(name, rhs));
        }

        /**
         * Helper function to add redundant stmt
         * @param stmt Stmt to eliminate by a code generator
         */
        private void addRedundantStmt(Stmt stmt){
            redundant_stmts.add(stmt);
        }

        /**
         * Checks every definition of a literal, if every use of the variable being defined only has one definition.
         * It adds the stmt to the redundant stmt set, and adds the mapping.
         * @param stmt TIRAssignLiteralStmt statement node
         */
        @Override
        public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt stmt) {
            // Check if its
            Expr rhs = stmt.getRHS();
            if(rhs instanceof IntLiteralExpr || rhs instanceof FPLiteralExpr ){
                // If the uses only have that particular definition.
                String variableName = stmt.getTargetName().getID();
                if(isNotOutputParameter(variableName) &&isVariableDefinitionNonAmbiguous(variableName, stmt)
                        && isVariableUntouchedBeforeAnyUse(variableName, stmt)){
                    // Check that in all the uses the variable is not modified
                    mapUsesToExpression(this.chain.getUsesOf(variableName, stmt), rhs);
                    addRedundantStmt(stmt);
                }
            }
        }

        /**
         * For this case, we check for the same variable definition ambiguous, and variable untouched
         * If both of those conditions are true, we replace the variables momentarily, run a temporary
         * ReachingDefs again, and check if those replacements are ambiguous, if they are, we go back.
         * <pre>
         *     {@code
         *     b = 21;
         *    c = b;
         *    while( true )
         *    d = 4 + c;
         *    b = 31;
         *    break;
         *    end
         *     }
         * </pre>
         * @param stmt
         */
//        public void caseTIRCopyStmt(TIRCopyStmt stmt){
//            Name rhs = stmt.getSourceName();
//            Name lhs = stmt.getTargetName();
//            if(    isNotOutputParameter(lhs.getID())
//                && isVariableDefinitionNonAmbiguous(rhs.getID(),stmt)
//                && isVariableUntouchedBeforeAnyUse(lhs.getID(),stmt)
//                && isAliasReplacementNonAmbiguous(lhs.getID(),rhs.getID(), stmt)
//                && isAliasReplacementUntouchedBeforeAnyUse(lhs.getID(),rhs.getID(), stmt))
//            {
//                mapUsesToExpression(this.chain.getUsesOf(lhs.getID(), stmt), stmt.getRHS());
//                addRedundantStmt(stmt);
//            }
//        }

        /**
         *
         * @param dest Destination variable being defined
         * @param src Source variable being assigned to destination
         * @param stmt TIRCopyStmt defining dest
         * @return This returns if for every use of the dest variable,
         * if we were to replace the use by the src var, whether the src var is not modified between program point at stmt
         * and the use for all uses.
         */
        private boolean isAliasReplacementUntouchedBeforeAnyUse(String dest, String src, TIRCopyStmt stmt) {
            return this.chain.getUsesOf(dest, stmt).stream()
                        .allMatch((Name name)->{
                            // Replace TIRCopyStmt with AssignStmt of src then performed temporary analysis

                            // Node finder to find list parent containing the stmt
                            List<Stmt> list =  NodeFinder.findParent(List.class, stmt);

                            // Check for modification between definition
                            Stmt useStmt = (Stmt) NodeFinder.findParent(Stmt.class, name);
                            int i = 0,index = -1;
                            for(Stmt tempStmt: list){
                                if(tempStmt == stmt) index = i;
                                i++;
                            }
                            if(index == -1) return false;
                            AssignStmt tempAssign = new TIRAssignLiteralStmt(new Name(src),
                                    new FPLiteralExpr(new FPNumericLiteralValue("3.0")));
                            list.removeChild(index);
                            list.insertChild(tempAssign, index);
                            // Perform analysis
                            ModifiedArrayAnalysis temp_mod_vars = new ModifiedArrayAnalysis(function);
                            function.tirAnalyze(temp_mod_vars);

                            Set<Def> setStmt =  temp_mod_vars.getInFlowSets()
                                    .get(useStmt).get(src);
                            list.removeChild(index);
                            list.insertChild(stmt,index);
                            return setStmt!= null && setStmt.contains(tempAssign);
                        });
        }

        /**
         *
         * @param dest Destination name var
         * @param src Source name var
         * @param stmt Statement defining copy Statement
         * @return Determines whether replacing the alias will cause ambiguity in the new variable
         */
        private boolean isAliasReplacementNonAmbiguous(String dest, String src,  Stmt stmt){
            return this.chain.getUsesOf(dest, (Def) stmt).stream()
                    .allMatch((Name name)->{
                        name.setID(src);// Set this temporarily to test
                        ReachingDefs tempReachDefs = new ReachingDefs(this.function);
                        tempReachDefs.analyze();
                        boolean isNonAmbi = tempReachDefs.getUseDefDefUseChain()
                                .getDefs(name).size() == 1;
                        name.setID(dest);
                        return isNonAmbi;
                    });

        }

        /**
         * Checks that every use contains an unmodified definition. Retuns
         * @param variableName Variable being Defined
         * @param stmt Statement defining variable
         * @return Returns true if all uses of the variable being defined
         *  at every program point contains an unmodified definition.
         */
        private boolean isVariableUntouchedBeforeAnyUse(String variableName, Stmt stmt) {
            return this.chain.getUsesOf(variableName,(Def) stmt).stream()
                    .allMatch((Name name)->{
                        // Find stmt for the use.
                        Stmt useStmt = (Stmt) NodeFinder.findParent(Stmt.class, name);
                        // Check in-flow sets for definition inclusion
                        Set<Def> setStmt = this.mod_vars_analysis.getInFlowSets()
                                .get(useStmt).get(name.getID());
                        return setStmt!= null && setStmt.contains(stmt);
                    });
        }
//        @Override
//        public void

        /**
         * This function determines whether the definition is ambiguous or not,
         * i.e. checks each use, and makes sure it could only have been defined at that point in the program.
         * @param definedVarName Variable name
         * @param stmtDefinition AST Statement where this variable is defined
         * @return Returns true if all used variable that correspond to the variable being defined
         * do not contain ambiguous definitions.
         */
        private boolean isVariableDefinitionNonAmbiguous(String definedVarName, AssignStmt stmtDefinition){
            return this.chain.getUsesOf(definedVarName, stmtDefinition)
                    .stream().noneMatch((Name name)->this.chain.getDefs(name).size() > 1);
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






}

