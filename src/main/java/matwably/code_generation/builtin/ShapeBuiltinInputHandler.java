package matwably.code_generation.builtin;

import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.Util;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;

public class ShapeBuiltinInputHandler extends BuiltinInputHandler {

    public static BuiltinInputHandler getInstance() {
      return new ShapeBuiltinInputHandler();
    }
    @Override
    public boolean isInForm() {
        if(arguments.size() == 1){
            NameExpr nameExpr = (NameExpr)arguments.getChild(0);
            BasicMatrixValue bmv = Util.getBasicMatrixValue(valueAnalysis,stmt,
                    nameExpr.getName().getID());
            if(bmv.hasShape()&&!bmv.getShape().isScalar() && bmv.getShape().getDimensions().size() == 2){
                java.util.List<DimValue> dimValues = bmv.getShape().getDimensions();
                return dimValues.get(0).getIntValue() == 1 && dimValues.get(1).getIntValue() >= 2;
            }
        }
        return false;
    }

    @Override
    public void generate(){
        TIRCommaSeparatedList args = arguments;
        int sizeArgs = arguments.size();
        String input_arg = Util.genTypedLocalI32();
//        result.setTargetName(input_arg);
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        result.addLocal(input_argType);
        if(sizeArgs == 0){
            result.addInstructions(MatWablyArray.createF64Vector(2));
            result.addInstruction(new SetLocal(new Idx(input_arg)));
            result.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, 0, new ConstLiteral(new F64(), 1)));
            result.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, 1, new ConstLiteral(new F64(), 1)));
        }else if(sizeArgs == 1){
            NameExpr nameExpr = (NameExpr)arguments.getChild(0);
            BasicMatrixValue bmv = Util.getBasicMatrixValue(valueAnalysis,stmt,
                    nameExpr.getName().getID());
            if(bmv.hasShape() && bmv.getShape().isScalar()){
                result.addInstructions(MatWablyArray.createF64Vector(2));
                result.addInstruction(new SetLocal(new Idx(input_arg)));
                String expr1 = Util.getTypedLocalF64(((NameExpr)args.getChild(0)).getName().getID());
                result.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, 0, new GetLocal(new Idx(expr1))));
                result.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, 1, new GetLocal(new Idx(expr1))));
            }else if(bmv.hasShape()&&!bmv.getShape().isRowVector()) {
                throw new Error("Size vector should be a row vector with real elements.");
            }
        }else{
            result.addInstructions(MatWablyArray.createF64Vector(sizeArgs));
            result.addInstruction(new SetLocal(new Idx(input_arg)));
            // There are more than two arguments
            int i = 0;
            for(ast.Expr argExpr: arguments){
                String nameExpr = ((ast.NameExpr) argExpr).getName().getID();
                BasicMatrixValue matVal = Util.getBasicMatrixValue(valueAnalysis,stmt, nameExpr);
                if(!matVal.hasShape() || !matVal.getShape().isScalar()){
                    throw new Error("Size inputs must be scalar.");
                }
                result.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, i,
                        new GetLocal(new Idx(Util.getTypedLocalF64(nameExpr)))));
                i++;
            }
        }
        result.addInstruction(new GetLocal(new Idx(input_arg)));
    }

}
