package matwably.code_generation.builtin.trial.binary_op;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class NumericBinaryOp extends BinaryOp{
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public NumericBinaryOp(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }
}
