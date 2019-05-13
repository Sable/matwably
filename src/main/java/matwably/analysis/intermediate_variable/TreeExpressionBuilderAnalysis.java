package matwably.analysis.intermediate_variable;


import ast.*;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.utils.NodeFinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class performs an analysis over the AST collecting a map of Variable names to expressions, and statements to be
 * eliminated. The actual transformation is performed by the code_generation package:
 */
public class TreeExpressionBuilderAnalysis extends TIRAbstractNodeCaseHandler {
    /**
     * TameIR function Node
     */
    private final TIRFunction function;
    private final InterproceduralFunctionQuery functionQuery;
    /**
     * Variable to expression map generated by the analysis
     */
    private HashMap<Name, AssignStmt> use_expr_map = new HashMap<>(); // Expr here can be either LiteralExpr or Parametrized expr

    /**
     * Set of statements to eliminate, specification of this analysis given by
     */
    private Set<Stmt> redundant_stmts = new HashSet<>();

    /**
     * Map of aliases, if a particular CopyStmt is deleted these mapping structure keeps track of the aliases formed.
     */
    private HashMap<Name, Name> aliases_map = new HashMap<>();
    /**
     * UseDefDefUseChain for the program obtained from UseDefDefUse
     */
    private final UseDefDefUseChain chain;
    /**
     * Set of strings containing output_names
     */
    private final Set<String> output_params;
    /**
     * boolean indicating whether the analysis is done
     */
    private boolean analysisDone;

    /**
     * boolean indicating whether the variable is to be eliminated
     * @param name AST node container for name
     * @return boolean indicating whether the variable is to be eliminated
     */
    public boolean isVariableEliminated(Name name){
        return use_expr_map.containsKey(name);
    }

    /**
     * Boolean indicating whether the analysis is been done
     * @return Boolean indicating whether the analysis is been done
     */
    public boolean isAnalysisDone() {
        return analysisDone;
    }

    /**
     * Gets alias map for TIRCopyStmt
     * @return Returns HashMap of aliased names in the program
     */
    public HashMap<Name, Name> getAliasMap() {
        return aliases_map;
    }

    /**
     * Gets alias map for TIRCopyStmt
     * @return Returns HashMap of aliased names in the program
     */
    public Set<Stmt> getRedundantStmtSet() {
        return redundant_stmts;
    }


    /**
     * Runs the analysis
     */
    public void analyze(){
        if(!this.analysisDone){
            this.analysisDone = true;
            function.analyze(this);
            redundant_stmts = getSetRedundantStatments();
        }

    }
    public Set<Stmt> getSetRedundantStatments(){
        HashSet<Stmt> set = new HashSet<>();
        for(Map.Entry<Name, AssignStmt> entry: use_expr_map.entrySet() )
            set.add(entry.getValue());
        return set;
    }

    /**
     * Contructor for TreeExpression mapping
     * @param function TIRFunction being analyzed
     * @param defs UseDefDefUse analysis
     */
    public TreeExpressionBuilderAnalysis(TIRFunction function,
                                         ReachingDefinitions defs,
                                         InterproceduralFunctionQuery functionQuery) {
        this.function = function;
        this.output_params = function.getOutputParams().stream().map(Name::getID).collect(Collectors.toSet());
        this.chain = defs.getUseDefDefUseChain();
        this.functionQuery = functionQuery;

//        UseDefDefUse modArr = new UseDefDefUse(function);
//        modArr.analyze();
//        this.mod_vars_analysis = modArr;
    }
    private boolean isNotOutputParameter(String varName){
        return this.output_params.stream().noneMatch((String name)->name.equals(varName));
    }
    /**
     *
     * @param uses Set of uses for a given variable definition
     * @param rhs Expressions to use in order to eliminate the variable definition
     */
    private void mapUsesToExpression(Set<Name> uses, AssignStmt rhs){
        uses.forEach((Name name)->use_expr_map.put(name, rhs));
    }



    /**
     * Checks every definition of a literal, if every use of the variable being defined only has one definition.
     * It adds the stmt to the redundant stmt set, and adds the mapping.
     * @param stmt TIRAssignLiteralStmt statement node
     * TODO remove check coverage once TIRGetStmt is handled correctly
     */
    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt stmt) {
        // Check if its
        Expr rhs = stmt.getRHS();
        if(rhs instanceof IntLiteralExpr || rhs instanceof FPLiteralExpr ){
            // If the uses only have that particular definition.
            String variableName = stmt.getTargetName().getID();
            if( checkCoverage(variableName,stmt)) return;

            if(isNotOutputParameter(variableName) &&isVariableDefinitionNonAmbiguous(variableName, stmt)){
                // Check that in all the uses the variable is not modified
                mapUsesToExpression(this.chain.getUsesOf(variableName, stmt), stmt);
            }
        }
    }

    @Override
    public void caseTIRCallStmt(TIRCallStmt tirStmt){

        if(tirStmt.getTargets().size() == 1 ){
            String targetName = tirStmt.getTargets().getNameExpresion(0).getVarName();
            if( checkCoverage(targetName,tirStmt)) return;
            if( isNotOutputParameter(targetName)
                    && isUsedOnlyOnce(targetName, tirStmt)
                    && isVariableDefinitionNonAmbiguous(targetName, tirStmt)
                    && isCallPure(tirStmt.getFunctionName().getID())
                    && !isUseDefined(tirStmt.getFunctionName().getID())){

                mapUsesToExpression(this.chain.getUsesOf(targetName, tirStmt), tirStmt);
            }
        }
    }

    private boolean isUseDefined(String id) {
        return functionQuery.isUserDefinedFunction(id);
    }

    private boolean isCallPure(String functionName) {
        return functionQuery.isCallPure(functionName);
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
    public void caseTIRCopyStmt(TIRCopyStmt stmt){
        if( checkCoverage(stmt.getTargetName().getID(),stmt)) return;

        Name rhs = stmt.getSourceName();
        Name lhs = stmt.getTargetName();
        if(    isNotOutputParameter(lhs.getID())
            && isVariableDefinitionNonAmbiguous(rhs.getID(),stmt)
            && isAliasReplacementNonAmbiguous(lhs.getID(),rhs.getID(), stmt))
        {
            mapAlisedNames(lhs, rhs);
            mapUsesToExpression(this.chain.getUsesOf(lhs.getID(), stmt), stmt);
        }
    }

    /**
     * Temporary method to be replaced once we have updated TIRGetStmt
     * @param target Target string name
     * @param stmt AssignStmt where the target name is defined
     * @return boolean indicating whether to process the variable
     */
    private boolean checkCoverage(String target, AssignStmt stmt) {
        return this.chain.getUsesOf(target, stmt).stream().
                anyMatch((Name name)-> NodeFinder.
                        findParent(Stmt.class, name)
                        instanceof TIRArrayGetStmt);
    }

    private void mapAlisedNames(Name lhs, Name rhs) {
        this.aliases_map.put(lhs, rhs);
    }

    /**
     * Returns whether the variable is only used once.
     * @param name Name of variable at the definition
     * @param stmt Assign stmt that defines variable name
     * @return
     */
    private boolean isUsedOnlyOnce(String name, AssignStmt stmt){
        return this.chain.getUsesOf(name, stmt).size() == 1;
    }

//    /**
//     *
//     * @param dest Destination variable being defined
//     * @param src Source variable being assigned to destination
//     * @param stmt TIRCopyStmt defining dest
//     * @return This returns if for every use of the dest variable,
//     * if we were to replace the use by the src var, whether the src var is not modified between program point at stmt
//     * and the use for all uses.
//     */
//    @SuppressWarnings("unchecked")
//    private boolean isAliasReplacementUntouchedBeforeAnyUse(String dest, String src, TIRCopyStmt stmt) {
//        return this.chain.getUsesOf(dest, stmt).stream()
//                    .allMatch((Name name)->{
//                        // Replace TIRCopyStmt with AssignStmt of src then performed temporary analysis
//
//                        // Node finder to find list parent containing the stmt
//                        List<Stmt> list =  NodeFinder.findParent(List.class, stmt);
//
//                        // Check for modification between definition
//                        Stmt useStmt =  NodeFinder.findParent(Stmt.class, name);
//                        int i = 0,index = -1;
//                        for(Stmt tempStmt: list){
//                            if(tempStmt == stmt) index = i;
//                            i++;
//                        }
//                        if(index == -1) return false;
//                        AssignStmt tempAssign = new TIRAssignLiteralStmt(new Name(src),
//                                new FPLiteralExpr(new FPNumericLiteralValue("3.0")));
//                        list.removeChild(index);
//                        list.insertChild(tempAssign, index);
//                        // Perform analysis
//                        UseDefDefUse temp_mod_vars = new UseDefDefUse(function);
//                        function.tirAnalyze(temp_mod_vars);
//
//                        Set<Def> setStmt =  temp_mod_vars.getInFlowSets()
//                                .get(useStmt).get(src);
//                        list.removeChild(index);
//                        list.insertChild(stmt,index);
//                        return setStmt!= null && setStmt.contains(tempAssign);
//                    });
//    }

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

//    /**
//     * Checks that every use contains an unmodified definition. Retuns
//     * @param variableName Variable being Defined
//     * @param stmt Statement defining variable
//     * @return Returns true if all uses of the variable being defined
//     *  at every program point contains an unmodified definition.
//     */
//    private boolean isVariableUntouchedBeforeAnyUse(String variableName, Stmt stmt) {
//        return this.chain.getUsesOf(variableName,(Def) stmt).stream()
//                .allMatch((Name name)->{
//                    // Find stmt for the use.
//                    Stmt useStmt = (Stmt) NodeFinder.findParent(Stmt.class, name);
//                    // Check in-flow sets for definition inclusion
//                    Set<Def> setStmt = this.mod_vars_analysis.getInFlowSets()
//                            .get(useStmt).get(name.getID());
//                    return setStmt!= null && setStmt.contains(stmt);
//                });
//    }
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

    /**
     * Obtains map for uses to expressions, note that the expressions are actually TIRAbstractAssignStmt
     * @return Hash map containing the use to expression map
     */
    public HashMap<Name,AssignStmt> getUsesToExpressionMap() {
        return use_expr_map;
    }

    public boolean isStmtRedundant(Stmt stmt) {
        return redundant_stmts.contains(stmt);
    }
}

