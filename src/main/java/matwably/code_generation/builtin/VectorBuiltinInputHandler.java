package matwably.code_generation.builtin;

import ast.ColonExpr;
import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.Util;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;

public class VectorBuiltinInputHandler extends BuiltinInputHandler {

    public static BuiltinInputHandler getInstance() {
        return new VectorBuiltinInputHandler();
    }

    @Override
    boolean isInForm() {
        return false;
    }

    @Override
    public void generate() {
        TIRCommaSeparatedList arguments = this.arguments;
        String input_arg = Util.genTypedLocalI32();
//        result.setTargetName(input_arg);
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        result.addLocal(input_argType);
        result.addInstructions(MatWablyArray.createCellVector(arguments.size()));
        result.addInstruction(new SetLocal(new Idx(input_arg)));

        // Go through each argument, if some are actual scalars
        int i = 1;
        for (ast.Expr expr :arguments) {
            if(expr instanceof NameExpr){
                NameExpr nameExpr = (NameExpr) expr;
                String name = nameExpr.getName().getID();
                BasicMatrixValue bmv = Util.getBasicMatrixValue(valueAnalysis,(ast.ASTNode) stmt, name);
                if( bmv.getShape().isScalar()) {
                    String typedName = Util.getTypedLocalF64 (name);
                    String typedNewName = Util.genTypedLocalI32();
                    TypeUse nameTypeUse = new TypeUse(new Opt<>(new Identifier(typedNewName)),new I32());
                    result.addLocal(nameTypeUse);
                    result.addInstruction(new GetLocal(new Idx(typedName)));
                    result.addInstruction(new Call(new Idx("convert_scalar_to_mxarray")));
                    result.addInstruction(new SetLocal(new Idx(typedNewName)));
                    result.addInstructions(MatWablyArray.setArrayIndexI32NoCheck(input_arg,i-1,new GetLocal(new Idx(typedNewName))));
                }else{
                    String typedName = Util.getTypedLocalI32(name);
                    result.addInstructions(MatWablyArray.setArrayIndexI32NoCheck(input_arg,i-1,new GetLocal(new Idx(typedName))));

                }
            }else if(expr instanceof ColonExpr){
                String arrayName = (stmt instanceof TIRArrayGetStmt)?
                        ((TIRArrayGetStmt) stmt).getArrayName().getID():
                        ((TIRArraySetStmt) stmt).getArrayName().getID();
                BasicMatrixValue bmv = Util.getBasicMatrixValue(valueAnalysis,(ast.ASTNode) stmt,arrayName);
                if(!bmv.hasShape()){
                    throw new Error("Could not get value shape for array name: " + arrayName);
                }

                java.util.List<DimValue> dimValues = bmv.getShape().getDimensions();
                DimValue val;
                List<Instruction> dynamicSize = new List<>();
                if(arguments.size() == 1){
////                    val = dimValues.stream().reduce(new DimValue(1,1+""),
////                            ( acc,dim1)->new DimValue(acc.getIntValue() *dim1.getIntValue(),""));
//                    if(!val.hasIntValue()){
                        dynamicSize.addAll(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                new Call(new Idx("numel")));
//                    }
                }else{
//                    val = dimValues.get(i-1);
//                    if(!val.hasIntValue()){
                        dynamicSize.addAll(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                new ConstLiteral(new F64(), i),
                                new Call(new Idx("size_MS")));
//                    }
                }
                String name = Util.genTypedLocalI32();
                result.addLocal(new TypeUse(name, new I32()));

                result.addInstruction( new ConstLiteral(new F64(), 1));

//                if(val.hasIntValue()){
//                    result.addInstruction(new ConstLiteral(new F64(), val.getIntValue()));
//                }else{
                    result.addInstructions(dynamicSize);
//                }

                result.addInstructions(
                        new Call(new Idx("colon_two")),
                        new SetLocal(new Idx(name))
                );
                if(arguments.size() == 1){
                    result.addInstructions(
                        new GetLocal(new Idx(name)),
                        new Call(new Idx("transpose_M")),
                        new SetLocal(new Idx(name))
                    );
                }
                result.addInstructions(
                        new GetLocal(new Idx(input_arg)),
                        new ConstLiteral(new I32(), i),
                        new GetLocal(new Idx(name)),
                        new Call(new Idx("set_array_index_i32"))
                );
            }
            else{
                throw new Error("Only named expressions allowed");
            }
            i++;
        }
        result.addInstruction(new GetLocal(new Idx(input_arg)));

    }
}
