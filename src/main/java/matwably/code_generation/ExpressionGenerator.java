package matwably.code_generation;

import ast.ASTNode;
import ast.*;
import matwably.analysis.MatWablyBuiltinAnalysis;
import matwably.analysis.ambiguous_scalar_analysis.AmbiguousVariableUtil;
import matwably.analysis.intermediate_variable.TreeExpressionBuilderAnalysis;
import matwably.ast.*;
import matwably.ast.List;
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
    private final AmbiguousVariableUtil ambVariableUtil;
    private final LogicalVariableUtil logicalVariableUtil;
    private final TreeExpressionBuilderAnalysis expression_builder;
    private final MatWablyBuiltinAnalysis builtinAnalysis;
    /**
     * Contains the map the NameExpr to expression map.
     */
    private HashMap<Name, AssignStmt> variable_expression_map = new HashMap<>();
    private ValueAnalysisUtil valueAnalysisUtil;
    public ExpressionGenerator(ValueAnalysisUtil valueAnalysisUtil,
                               AmbiguousVariableUtil ambVariableUtil,
                               LogicalVariableUtil logicalVariableUtil,
                               MatWablyBuiltinAnalysis builtinAnalysis,
                               TreeExpressionBuilderAnalysis expressionBuilderAnalysis){
        this.valueAnalysisUtil  = valueAnalysisUtil;
        this.ambVariableUtil = ambVariableUtil;
        this.logicalVariableUtil = logicalVariableUtil;
        this.expression_builder = expressionBuilderAnalysis;
        this.builtinAnalysis = builtinAnalysis;
        this.variable_expression_map = expressionBuilderAnalysis.getUsesToExpressionMap();
    }
    public ExpressionGenerator(ValueAnalysisUtil valueAnalysisUtil,
                               AmbiguousVariableUtil ambVariableUtil,
                               LogicalVariableUtil logicalVariableUtil,
                               MatWablyBuiltinAnalysis builtinAnalysis){
        this.valueAnalysisUtil  = valueAnalysisUtil;
        this.ambVariableUtil = ambVariableUtil;
        this.logicalVariableUtil = logicalVariableUtil;
        this.builtinAnalysis = builtinAnalysis;
        this.expression_builder = null;
    }

    /**
     * Flag to indicate whether to build expression tree, or simple generate the NameExpr as is. i.e.
     * <pre>
     *     get_local $name_expr_id
     * </pre>
     */

    private boolean build_expression_tree = false;


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
     * Returns whether NameExpr is simple, i.e. it does not lead to re-computation of results once
     * the expression tree is built.
     * @param nameExpr Input name expression
     * @return Returns whether NameExpr is simple
     */
    public boolean isSimpleExpression(NameExpr nameExpr, ASTNode stmt){
       // TODO: implement this once we have the expression tree re-builder
       return this.build_expression_tree && !variable_expression_map.containsKey(nameExpr.getName());
    }
    /**
     * Main function generates expressions, if optimization turned on, it creates a tree of expression.
     * @param nameExpr Takes the NameExpr to
     * @param stmt Statement from the NameExpression coming from Matlab
     * @return Generated expression
     */
    public List<Instruction> genNameExpr(NameExpr nameExpr, ASTNode stmt){
//        Name name = nameExpr.getName();
//        if(this.build_expression_tree && variable_expression_map.containsKey(name)){
////            ast.Expr expr = variable_expression_map.get(name);
////            if(expr instanceof LiteralExpr){
////                return genLiteralExpr(expr);
////            }
////            else if(expr instanceof NameExpr){// Copy Statement
////                return genNameExpr((NameExpr)expr, stmt);
////            }
//            return new List<>(new GetLocal(
//                    new Idx(valueAnalysisUtil.genTypedName(nameExpr,stmt,true))));
//        }else{
//        }
        String typedName = (logicalVariableUtil.isUseLogical(nameExpr.getName()))?
                Util.getTypedLocalI32(nameExpr.getName().getID()):
                valueAnalysisUtil.genTypedName(nameExpr.getName().getID(), stmt,true);

        return new List<>(new GetLocal(new Idx(typedName)));

    }
    public List<Instruction> genName(Name name, ASTNode stmt) {

        String typedName = (logicalVariableUtil.isUseLogical(name))? Util.getTypedLocalI32(name.getID()):
                valueAnalysisUtil.genTypedName(name.getID(), stmt,true);
        if (expression_builder!=null && variable_expression_map.containsKey(name)) {
            AssignStmt mapped_stmt = variable_expression_map.get(name);
            if (stmt instanceof TIRAssignLiteralStmt) {
                return genExpr(mapped_stmt.getRHS(), mapped_stmt);
            } else if (mapped_stmt instanceof TIRCopyStmt) {// Copy Statement
                return genName(((TIRCopyStmt) mapped_stmt).getSourceName(), mapped_stmt);
            } else if(mapped_stmt instanceof TIRCallStmt) {
//                MatWablyBuiltinGenerator generator = builtinAnalysis.getGenerator(stmt);

            }
        }

        return new List<>( new GetLocal(new Idx(typedName)));
    }
    public List<Instruction> genSubsrefSubasgnExpression(NameExpr expr, ASTNode stmt){


        return null;

    }


}
