package matwably.code_generation.builtin.utility;

import ast.ASTNode;
import ast.NameExpr;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Disp extends Utility {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Disp(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
        return true;
    }

    /**
     * Function used to query whether the builtin function returns void
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return true;
    }

    /**
     * Disp generation requires there too be only one input, as otherwise Matlab throws an error.
     * For this we have made compilation error classes under the {@link MatWablyError} class.
     * If the call does have one argument, we check whether we know is scalar statically and simply
     * call the appropriate specialization.
     */
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        NameExpr arg = arguments.getNameExpresion(0);
        result.addInstructions(expressionGenerator.genNameExpr(arg,node));
        String nameCall = (valueUtil.isScalar(arg,node,true))?"disp_S":"disp_M";
        result.addInstruction(new Call(new Idx(nameCall)));
        return result;
    }

    @Override
    public void validateInput() {
        if(arguments.size()== 0)
            throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size() > 1)
            throw new MatWablyError.TooManyInputArguments(callName,node);
    }
}
