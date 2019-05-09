package matwably.code_generation.builtin.trial.input_generation;

import ast.ColonExpr;
import ast.NameExpr;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.builtin.ResultWasmGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.Util;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;

public class VectorInputGenerator extends AbstractInputGenerator {


    public VectorInputGenerator(ast.ASTNode node, TIRCommaSeparatedList args, TIRCommaSeparatedList targets, MatWablyFunctionInformation functionInformation, ResultWasmGenerator result) {
        super(node, args, targets, functionInformation, result);
    }

    @Override
    boolean isInForm() {
        return false;
    }

    /**
     * Generation of this input type is based on a resulting array of arrays.
     * This input generator transforms scalars into arrays and sets them as elements of another input
     * array. Example functions: horzcat, vertcat, arrsubsref, arrasgnref, cat.
     * There are two types of Expressions in TameIR, NameExpr and ColonExpr.
     */
    @Override
    public void generate() {
        // TODO Add freeMacharray functions
        // TODO Refactor once subsasgn and subsref are implemented
        String vectorInputI32 = result.generateVectorInputI32(arguments.size());
        // Go through each argument, if some are actual scalars
        int i = 0;
        for (ast.Expr expr :this.arguments) {
            if(expr instanceof NameExpr){
                NameExpr nameExpr = (NameExpr) expr;
                // Scalar to boxed scalar.
                if(valueAnalysisUtil.isScalar(nameExpr,stmt, true)){
                    String i32_boxed_scalar = result.generateBoxedScalar(this.name_expression_generator.genNameExpr(nameExpr, stmt));
                    result.addInstructions(MatWablyArray.setArrayIndexI32(vectorInputI32, i, new GetLocal(new Idx(i32_boxed_scalar))));
                }else{
                    // Vector instructions
                    result.addInstructions(MatWablyArray.setArrayIndexI32(vectorInputI32, i, name_expression_generator.genNameExpr(nameExpr,stmt)));
                }
            }else if(expr instanceof ColonExpr){
                String arrayName = (stmt instanceof TIRArrayGetStmt)?
                        ((TIRArrayGetStmt) stmt).getArrayName().getID():
                        ((TIRArraySetStmt) stmt).getArrayName().getID();
                BasicMatrixValue bmv = Util.getBasicMatrixValue(matwably_analysis_set.getFunctionAnalysis(), stmt,arrayName);
                if(!bmv.hasShape()){
                    throw new Error("Could not get value shape for array name: " + arrayName);
                }

                java.util.List<DimValue> dimValues = bmv.getShape().getDimensions();
                DimValue val;
                // TODO (dherre3) Find dimensions dynamically
                List<Instruction> dynamicSize = new List<>();
                if(arguments.size() == 1){
                    val = dimValues.stream().reduce(new DimValue(1,1+""),
                            ( acc,dim1)->new DimValue(acc.getIntValue() *dim1.getIntValue(),""));
                    if(!val.hasIntValue()){
                        dynamicSize.addAll(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                new Call(new Idx("numel")));
                    }
                }else{
                    val = dimValues.get(i-1);
                    if(!val.hasIntValue()){
                        dynamicSize.addAll(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                new ConstLiteral(new F64(), i),
                                new Call(new Idx("size_MS")));
                    }
                }
                String name = Util.genTypedLocalI32();
                result.addLocal(new TypeUse(name, new I32()));

                result.addInstruction( new ConstLiteral(new F64(), 1));

                if(val.hasIntValue()){
                    result.addInstruction(new ConstLiteral(new F64(), val.getIntValue()));
                }else{
                    result.addInstructions(dynamicSize);
                }

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
                        new GetLocal(new Idx(vectorInputI32)),
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
        result.addInstruction(new GetLocal(new Idx(vectorInputI32)));

    }
}
