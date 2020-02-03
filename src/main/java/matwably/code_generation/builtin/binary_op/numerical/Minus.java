package matwably.code_generation.builtin.binary_op.numerical;

import ast.ASTNode;
import matwably.ast.F64;
import matwably.ast.Sub;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.binary_op.NumericBinaryOp;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Minus extends NumericBinaryOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Minus(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
        result.addInstruction(new Sub(new F64()));
        return result;
    }
}
