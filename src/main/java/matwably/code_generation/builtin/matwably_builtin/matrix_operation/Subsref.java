package matwably.code_generation.builtin.matwably_builtin.matrix_operation;

import ast.ASTNode;
import ast.Name;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.input_generation.VectorInputGenerator;
import matwably.code_generation.wasm.MatMachJSError;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import matwably.code_generation.wasm.macharray.MachArray;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Subsref extends MatrixIndexing {
    private final Name valueName;

    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Subsref(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
        TIRArraySetStmt setStmt = (TIRArraySetStmt) node;
        this.valueName = setStmt.getValueName();
    }

    /**
     * To be implemented by actual Builtin. False if the built-in expression returns boxed scalar.
     * Returns whether the expression always returns a matrix. i.e. whether the generated built-in call does
     * not have specialization for the scalar cases.
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        return true;
    }

    /**
     * Function used to query whether the builtin function returns void
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    @Override
    protected MatWablyBuiltinGeneratorResult generateExpression() {
        MatWablyBuiltinGeneratorResult result =
                new MatWablyBuiltinGeneratorResult();
        if(valueUtil.isScalar(arrayName.getID(), node, true)){
            throw new MatWablyError.
                    UnsupportedBuiltinCallWithArguments(callName,
                    node,arguments);
        }else{
            if( isSlicingOperation()){
                result.addInstructions(expressionGenerator.
                        genName(arrayName, node));
                VectorInputGenerator generator =
                        new VectorInputGenerator(node, indices,matwably_analysis_set);
                result.add(generator.generate());
                // Box value for call
                if(valueUtil.isScalar(valueName.getID(),node,true)){
                    result.addInstruction(
                            new GetLocal(new Idx(result.generateBoxedScalar(
                                    expressionGenerator.genName(
                                        valueName, node)))));
                }else{
                    result.addInstructions(expressionGenerator.genName(
                                            valueName, node));
                }
                result.addInstructions(new List<>(
                        new Call(new Idx("set_f64"))
                ));
            }else{

                result.addInstructions(
                        MachArrayIndexing.setArrayIndexF64NoCheck(
                                expressionGenerator.
                                        genName(this.arrayName, node),
                                generateScalarIndex(),
                                generateScalarValueInstructions()));
            }
        }
        return result;
    }

    private List<Instruction> generateScalarValueInstructions() {
        List<Instruction> res = new List<>();
        if( !valueUtil.isScalar(valueName.getID(), node, true) ){
            // Check that is a scalar dynamically,
            res.addAll(MatMachJSError.generateThrowError(
                    MatMachJSError.SUBSCRIPTED_ASSIGNMENT_DIMENSION_MISMATCH,
                    MachArray.isScalar(expressionGenerator.
                    genName(arrayName, node))));
            res.addAll(MachArrayIndexing.getArrayIndexF64NoCheck(expressionGenerator.genName(valueName,node),
                    new List<>(new ConstLiteral(new I32(), 0))));
        }else{
            res.addAll(expressionGenerator.genName(valueName,node));
        }

        return res;
    }

    /* no-op */
    @Override
    public void validateInput() { }
}
