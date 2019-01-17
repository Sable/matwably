package matwably.code_generation;

import ast.*;
import matwably.ast.*;
import matwably.ast.List;
import matwably.util.Util;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

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
    HashMap<Name, Expr> variable_expression_map;// Null by default
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction;
    public NameExpressionGenerator(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction){
        this.analysisFunction = analysisFunction;
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
     * Main function generates expressions, if optimization turned on, it creates a tree of expression.
     * @param nameExpr Takes the NameExpr to
     * @param stmt Statement from the NameExpression coming from Matlab
     * @return
     */
    public List<Instruction> genNameExpr(NameExpr nameExpr, TIRNode stmt){
        Name name = nameExpr.getName();
        if(this.build_expression_tree && variable_expression_map.containsKey(name)){
            ast.Expr expr = variable_expression_map.get(name);
            if(expr instanceof LiteralExpr){
                return genLiteralExpr(expr);
            }else if(expr instanceof NameExpr){// Copy Statement
                return genNameExpr((NameExpr)expr, stmt);
            }
            return null;// TODO(dherre3): Remove this .
        }else{
            String id = name.getID();
            BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, stmt, id);
            String typedName= (val.hasShape()&& val.getShape().isScalar())?Util.getTypedLocalF64(id):Util.getTypedLocalI32(id);
            return new List<>(new GetLocal(new Idx(typedName)));
        }
    }


}
