package matwably.code_generation.builtin.trial.constant;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

/**
 * This abstract class implements the constants for the MatWably compiler
 * i.e. True, False, PI , E.
 */
public abstract class Constant extends MatWablyBuiltinGenerator{
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Constant(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }
    /**
     * To be implemented by actual Builtin. Specifies whether the built-in expression returns boxed scalar.
     * Returns whether the expression always returns a matrix. i.e. whether the generated built-in call does
     * not have specialization for the scalar cases.
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        return arguments.size() == 0;
    }

    public abstract MatWablyBuiltinGeneratorResult generateConstant();
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        if(arguments.size()!= 0) throw new MatWablyError.UnsupportedBuiltinCall("Unsupported matrix call for constant:"+ callName +" in MatWably",node);
        return generateConstant();
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
}
