package matwably.code_generation.builtin.legacy;

import ast.Expr;
import ast.List;
import ast.NameExpr;
import matwably.ast.GetLocal;
import matwably.ast.Idx;
import matwably.util.Util;
import natlab.tame.tir.TIRCommaSeparatedList;

public class OneMatrixAndShapeBuiltinInputHandler extends BuiltinInputHandler
{


    private NameExpr matrix;
    public TIRCommaSeparatedList shapeArgs;
    public ShapeBuiltinInputHandler shapeTransformer;
    public static BuiltinInputHandler getInstance() {
        return new OneMatrixAndShapeBuiltinInputHandler();
    }
    @Override
    public void setArguments(TIRCommaSeparatedList arguments) {
        List<Expr> args = arguments.copy();
        this.matrix = (NameExpr) args.getChild(0);
        args.removeChild(0);
        this.shapeArgs = new TIRCommaSeparatedList(args);
        this.arguments = arguments;
        shapeTransformer =  new ShapeBuiltinInputHandler();
        shapeTransformer.setArguments(shapeArgs);

        if(stmt != null){
            shapeTransformer.setStmt(stmt);
        }
        if(valueAnalysis != null){
            shapeTransformer.setValueAnalysis(valueAnalysis);
        }
        if(result!=null){
            shapeTransformer.setResult(result);
        }
    }

    @Override
    public boolean isInForm() {
        return shapeTransformer.isInForm();
    }

    @Override
    protected void generate() {
        // Add matrix
         result.addInstruction(new GetLocal(new Idx(
                 Util.getTypedLocalF64(this.matrix.getName().getID()))));

         // Get Shaped parameters
         shapeTransformer.generate();
         result = shapeTransformer.result;
    }
}
