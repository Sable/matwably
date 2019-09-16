package matwably.code_generation.builtin.matwably_builtin.input_generation;

import ast.ColonExpr;
import ast.NameExpr;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import matwably.util.Util;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;

public class VectorInputGenerator extends AbstractInputGenerator {


    public VectorInputGenerator(ast.ASTNode node, TIRCommaSeparatedList args, MatWablyFunctionInformation functionInformation) {
        super(node, args , functionInformation);
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
    public MatWablyBuiltinGeneratorResult generate() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        // TODO Refactor once subsasgn and subsref are implemented
        String vectorInputI32 = result.generateI32Local();
        result.addAllocationInstructions(MachArrayIndexing.createI32Vector(arguments.size(),
                vectorInputI32));
        result.addDellocationInstructions(MachArrayIndexing.freeMachArray(vectorInputI32));

        // Go through each argument, if some are actual scalars
        int i = 0;
        for (ast.Expr expr :this.arguments) {
            if(expr instanceof NameExpr){
                NameExpr nameExpr = (NameExpr) expr;
                // Scalar to boxed scalar.
                if(valueAnalysisUtil.isScalar(nameExpr,stmt, true)){
                    String boxed_scalar = result.generateI32Local();
                    result.addAllocationInstructions(MachArrayIndexing.createF64Vector(1,
                            boxed_scalar));
                    result.addInstructions(
                            MachArrayIndexing.setArrayIndexF64(boxed_scalar, 1,
                            name_expression_generator.genNameExpr(nameExpr,stmt)));
                    // Vector instructions
                    result.addInstructions(MachArrayIndexing.setArrayIndexI32NoCheck(vectorInputI32, i,
                            new GetLocal(new Idx(boxed_scalar))));
                    result.addDellocationInstructions(MachArrayIndexing.freeMachArray(boxed_scalar));

                }else{
                    // Vector instructions
                    result.addInstructions(MachArrayIndexing.setArrayIndexI32NoCheck(vectorInputI32, i,
                            name_expression_generator.genNameExpr(nameExpr,stmt)));
                }
            }else if(expr instanceof ColonExpr){
                // Move this to TIRGetArray TIRSetArray
                String arrayName = (stmt instanceof TIRArrayGetStmt)?
                        ((TIRArrayGetStmt) stmt).getArrayName().getID():
                        ((TIRArraySetStmt) stmt).getArrayName().getID();
                BasicMatrixValue bmv = Util.getBasicMatrixValue(
                        matwably_analysis_set.getFunctionAnalysis(),
                        stmt,arrayName);
                List<Instruction> dynamicSize = new List<>();
                String name = Util.genTypedLocalI32();
                result.addLocal(new TypeUse(name, new I32()));
                result.addAllocationInstructions( new ConstLiteral(new F64(), 1));
                if(bmv!=null && bmv.hasShape()){
                    DimValue val;
                    java.util.List<DimValue> dimValues = bmv.getShape().getDimensions();
                    if(arguments.size() == 1){

                        val = dimValues.stream().reduce(new DimValue(1,1+""),
                                ( acc,dim1)->new DimValue(acc.getIntValue()
                                        * dim1.getIntValue(),""));
                        if(!val.hasIntValue()){
                            dynamicSize.addAll(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                    new Call(new Idx("numel")));
                        }
                    }else{
                        val = dimValues.get(i);
                        if(!val.hasIntValue()){
                            dynamicSize.addAll(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                    new ConstLiteral(new F64(), i),
                                    new Call(new Idx("size_MS")));
                        }
                    }
                    if(val.hasIntValue()){
                        result.addAllocationInstructions(new ConstLiteral(new F64(), val.getIntValue()));
                    }else{
                        result.addAllocationInstructions(dynamicSize);
                    }
                }else{
                    if(arguments.size() == 1){
                        result.addAllocationInstructions(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                new Call(new Idx("numel")));
                    }else{
                        result.addAllocationInstructions(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))),
                                new ConstLiteral(new F64(), i),
                                new Call(new Idx("size_MS")));
                    }
                }

                result.addAllocationInstructions(
                        new Call(new Idx("colon_two")),
                        new SetLocal(new Idx(name))
                );

                if(arguments.size() == 1){

                    result.addAllocationInstructions(
                        new GetLocal(new Idx(name)),
                        new Call(new Idx("transpose_M")),
                        new SetLocal(new Idx(name))
                    );
                }
                result.addAllocationInstructions( MachArrayIndexing.setArrayIndexI32NoCheck(
                        vectorInputI32, i,new GetLocal(new Idx(name))) );
                result.addDellocationInstructions(MachArrayIndexing.
                        freeMachArray(name));
            }
            i++;
        }
        result.addInstruction(new GetLocal(new Idx(vectorInputI32)));
        return result;

    }
}
