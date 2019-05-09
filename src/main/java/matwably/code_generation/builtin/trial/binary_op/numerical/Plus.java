package matwably.code_generation.builtin.trial.binary_op.numerical;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Add;
import matwably.ast.F64;
import matwably.code_generation.builtin.trial.binary_op.BinaryOp;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Plus extends BinaryOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Plus(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by child classes, this function generates the scalar instruction or scalar call
     * e.g. if the operation is binary addition,
     * this function adds the instruction `f64.add` to the return object
     */
    @Override
    public void generateScalarCall() {
        result.addInstruction(new Add(new F64()));
    }
}