package matwably.code_generation.builtin.trial.binary_op.numerical;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.builtin.trial.binary_op.BinaryOp;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Mod extends BinaryOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Mod(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by child classes, this function generates the scalar instruction or scalar call
     * e.g. if the operation is binary addition,
     * this function adds the instruction `f64.add` to the return object
     *
     */
    @Override
    public void generateScalarCall() {
        /*
        *    get_local 0
             get_local 1
             get_local 0
             get_local 1
             f64.div
             f64.floor
             f64.mul
             f64.sub
         */
        generateInputs();
        result.addInstructions(new Div(new F64(),false),
                new Floor(new F64()),
                new Mul(new F64()),
                new Sub(new F64()));
    }
}
