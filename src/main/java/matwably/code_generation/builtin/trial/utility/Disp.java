package matwably.code_generation.builtin.trial.utility;

import ast.ASTNode;
import ast.NameExpr;
import matwably.code_generation.MatWablyError;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Disp extends MatWablyBuiltinGenerator {
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
        return false;
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
    public void generateExpression() {
        if(arguments.size()== 0){
            throw new MatWablyError.NotEnoughInputArguments(callName,node);
        }else if(arguments.size() > 1){
            throw new MatWablyError.TooManyInputArguments(callName,node);
        }else{
            NameExpr arg = arguments.getNameExpresion(0);
            result.addInstructions(nameExpressionGenerator.genNameExpr(arg,node));
            String nameCall = (valueUtil.isScalar(arg,node))?"disp_S":"disp_M";
            result.addInstruction(new Call(new Idx(nameCall)));
        }
    }
}
