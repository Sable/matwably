package matwably.code_generation.builtin.trial.binary_op.logical;

import ast.ASTNode;
import matwably.ast.F64;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.trial.binary_op.LogicalBinaryOp;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Equals extends LogicalBinaryOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Equals(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
        result.addInstruction(new matwably.ast.Eq(new F64()));
        return result;
    }

    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        super.validateInput();
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        boolean arg1IsScalar = valueUtil.isScalar(arguments.getNameExpresion(0),node,true);
        boolean arg2IsScalar = valueUtil.isScalar(arguments.getNameExpresion(1),node,true);

        result.add(generateInputs());
        // Only scalars are supported with logical ops
        if(arg1IsScalar && arg2IsScalar) {
            result.add(generateScalarCall());

        }else{
            result.add(super.generateCall());
        }
        return result;
    }
}
