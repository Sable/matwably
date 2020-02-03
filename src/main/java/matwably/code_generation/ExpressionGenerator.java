package matwably.code_generation;

import ast.ASTNode;
import ast.*;
import matwably.analysis.MatWablyBuiltinAnalysis;
import matwably.analysis.ambiguous_scalar_analysis.AmbiguousVariableUtil;
import matwably.analysis.intermediate_variable.TreeExpressionBuilderAnalysis;
import matwably.ast.*;
import matwably.ast.List;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.MatWablyBuiltinGenerator;
import matwably.util.LogicalVariableUtil;
import matwably.util.Util;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCopyStmt;

import java.util.HashMap;

/**
 * Represents the NameExpr generator for the MatWably compiler
 * @author David Herrera
 * @author davidfherrerar@gmail.com
 * @version 1.0.0
 */
public class ExpressionGenerator {
    private  AmbiguousVariableUtil ambVariableUtil;
    private  LogicalVariableUtil logicalVariableUtil;
    private  TreeExpressionBuilderAnalysis expression_builder = null;
    private  MatWablyBuiltinAnalysis builtinAnalysis;
    private  HashMap<Name, List<Instruction>> cached_expression_rebuild = new HashMap<>();
    private boolean disallow_logicals = false;
    public static boolean Debug = false;

    /**
     * Contains the map the NameExpr to expression map.
     */
    private HashMap<Name, AssignStmt> variable_expression_map = new HashMap<>();
    private ValueAnalysisUtil valueAnalysisUtil;
    public ExpressionGenerator(ValueAnalysisUtil valueAnalysisUtil,
                               AmbiguousVariableUtil ambVariableUtil,
                               LogicalVariableUtil logicalVariableUtil,
                               TreeExpressionBuilderAnalysis expressionBuilderAnalysis,
                               boolean disallow_logicals){
        this.valueAnalysisUtil  = valueAnalysisUtil;
        this.ambVariableUtil = ambVariableUtil;
        this.logicalVariableUtil = logicalVariableUtil;
        this.expression_builder = expressionBuilderAnalysis;
        this.variable_expression_map = expressionBuilderAnalysis.getUsesToExpressionMap();
        this.disallow_logicals = disallow_logicals;
        if(Debug) log();
    }

    public ExpressionGenerator(ValueAnalysisUtil valueAnalysisUtil,
                               AmbiguousVariableUtil amb_var_util,
                               LogicalVariableUtil logicalVariableUtil,
                               boolean disallow_logicals) {
        this.valueAnalysisUtil  = valueAnalysisUtil;
        this.ambVariableUtil = amb_var_util;
        this.logicalVariableUtil = logicalVariableUtil;
        this.disallow_logicals = disallow_logicals;

    }

    /**
     * Flag to indicate whether to build expression tree, or simple generateInstructions the NameExpr as is. i.e.
     * <pre>
     *     get_local $name_expr_id
     * </pre>
     */

    public List<Instruction> genExpr(ast.Expr expr, ASTNode<?> stmt){
        if (expr instanceof NameExpr)
                return genNameExpr((NameExpr)expr, stmt);
        if (expr instanceof ast.IntLiteralExpr)
            return  new List<>(new ConstLiteral(new F64(), Integer.parseInt(
                    ((IntLiteralExpr)expr).getValue().getText())));
        if (expr instanceof ast.FPLiteralExpr)
            return  new List<>(new ConstLiteral(new F64(), Double.parseDouble(
                    ((FPLiteralExpr)expr).getValue().getText())));
        if (expr instanceof ast.StringLiteralExpr)
            throw new UnsupportedOperationException("Strings are not supported by MatWably at the moment");
        if (expr instanceof ast.ColonExpr)
            throw new Error("Should have been generated else where");
        throw new UnsupportedOperationException(
            String.format("Expr node not supported. %d:%d: [%s] [%s]",
                expr.getStartLine(), expr.getStartColumn(),
                expr.getPrettyPrinted(), expr.getClass().getName())
        );
    }

    /**
     * Returns whether Name use is simple, i.e. it does not lead to re-computation of results once
     * the expression tree is built, e.g. ones(3,3) is not a simply, as re-computing is expensive.
     * A literal is simple. If it is a copy stmt is simple only if the source variable
     * is simple. If it is a Call statement it is never simple.
     * @param name Input name expression
     * @return Returns whether NameExpr is simple, this is only
     */
    public boolean isSimpleExpression(Name name) {

        return this.expression_builder == null
                || !variable_expression_map.containsKey(name) ||
                (variable_expression_map.get(name) instanceof TIRCopyStmt &&
                        isSimpleExpression(((TIRCopyStmt) variable_expression_map.get(name)).getSourceName()))||
                !(variable_expression_map.get(name) instanceof TIRCallStmt);
    }
    /**
     * Main function generates expressions, if optimization turned on, it creates a tree of expression.
     * @param nameExpr Takes the NameExpr to
     * @param stmt Statement from the NameExpression coming from Matlab
     * @return Generated expression
     */
    public List<Instruction> genNameExpr(NameExpr nameExpr, ASTNode stmt){
        return genName(nameExpr.getName(), stmt);
    }
    public List<Instruction> genName(Name name, ASTNode stmt) {
        if (expression_builder!=null && variable_expression_map.containsKey(name)) {
            List<Instruction> res = new List<>();
            if(cached_expression_rebuild.containsKey(name)){
                res.addAll(cached_expression_rebuild.get(name));
                return res;
            }
            AssignStmt mapped_stmt = variable_expression_map.get(name);
            if (mapped_stmt instanceof TIRAssignLiteralStmt) {
                res = genExpr(mapped_stmt.getRHS(),
                        mapped_stmt);
            } else if (mapped_stmt instanceof TIRCopyStmt) {// Copy Statement
               res = genName(((TIRCopyStmt) mapped_stmt).getSourceName(), mapped_stmt);
            } else if(mapped_stmt instanceof TIRCallStmt) {
                MatWablyBuiltinGenerator generator = builtinAnalysis.getGenerator(mapped_stmt);
                MatWablyBuiltinGeneratorResult result = generator.
                        getGeneratedExpression();
                res.addAll(result.getInstructions());
            }
            cached_expression_rebuild.put(name, res);
            return res;
        }

        String typedName = (!this.disallow_logicals && logicalVariableUtil.isUseLogical(name))? Util.getTypedLocalI32(name.getID()):
                valueAnalysisUtil.genTypedName(name.getID(), stmt,true);
        return new List<>( new GetLocal(new Idx(typedName)));
    }
    public void log(){
        this.variable_expression_map.forEach((Name name, Stmt stmt)->{
            System.out.println("Name:\n"+name.getID());
            System.out.println("Stmt:\n"+stmt.getPrettyPrinted());
        });
    }

    public void setBuiltinAnalysis(MatWablyBuiltinAnalysis analysis) {
        this.builtinAnalysis = analysis;
    }
}
