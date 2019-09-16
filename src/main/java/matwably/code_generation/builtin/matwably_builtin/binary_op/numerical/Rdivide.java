package matwably.code_generation.builtin.matwably_builtin.binary_op.numerical;

import ast.ASTNode;
import matwably.ast.Div;
import matwably.ast.F64;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.binary_op.NumericBinaryOp;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Rdivide extends NumericBinaryOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Rdivide(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by child classes, this function generates the scalar instruction or scalar call
     * e.g. if the operation is binary addition,
     * this function adds the instruction `f64.add` to the return object
     */
    @Override
    public MatWablyBuiltinGeneratorResult generateScalarCall() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.addInstruction(new Div(new F64(), false));
        return result;
    }
}
