package matwably.code_generation;

import ast.ASTNode;
import ast.*;
import matwably.ast.*;
import matwably.ast.List;
import matwably.util.ValueAnalysisUtil;

import java.util.HashMap;

/**
 * Represents the NameExpr generator for the MatWably compiler
 * @author David Herrera
 * @author davidfherrerar@gmail.com
 * @version 1.0.0
 */
public class NameExpressionGenerator {
    /**
     * Contains the map the NameExpr to expression map.
     */
    HashMap<Name, Expr> variable_expression_map = new HashMap<>();
    private ValueAnalysisUtil valueAnalysisUtil;
    public NameExpressionGenerator(ValueAnalysisUtil valueAnalysisUtil){
        this.valueAnalysisUtil  = valueAnalysisUtil;
    }
    /**
     * Flag to indicate whether to build expression tree, or simple generate the NameExpr as is. i.e.
     * <pre>
     *     get_local $name_expr_id
     * </pre>
     */
    private boolean build_expression_tree = false;

    /**
     * Sets the analysis for expression tree built
     * @param inter_var Variable to expression map. This map is used to build up expressions again, and is produced by
     *
     */
    public void setNameExpressionTreeMap(  HashMap<Name, Expr> inter_var ) {
        this.variable_expression_map = inter_var;
        this.build_expression_tree = true;
    }

    public List<Instruction> genLiteralExpr(ast.Expr expr){
        if (expr instanceof ast.IntLiteralExpr)
            return  new List<>(new ConstLiteral(new F64(), Integer.parseInt(
                    ((IntLiteralExpr)expr).getValue().getText())));
        if (expr instanceof ast.FPLiteralExpr)
            return  new List<>(new ConstLiteral(new F64(), Double.parseDouble(
                    ((FPLiteralExpr)expr).getValue().getText())));
        if (expr instanceof ast.StringLiteralExpr)
            throw new UnsupportedOperationException("Strings are not supported by MatWably at the moment");
        if (expr instanceof ast.ColonExpr)
            throw new Error("Should have been generated at a higher level");
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
       return this.build_expression_tree && !variable_expression_map.containsKey(nameExpr);
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
        return new List<>(new GetLocal(new Idx(valueAnalysisUtil.genTypedName(nameExpr,stmt))));

    }


}
