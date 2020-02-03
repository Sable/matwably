package matwably.code_generation.builtin.matrix_operation;

import ast.ASTNode;
import ast.NameExpr;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class End extends MatrixOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public End(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
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
    public void validateInput() {
        if(arguments.size() != 3) throw
                new MatWablyError.
                        NotEnoughInputArguments(callName, node);

    }

    @Override
    protected MatWablyBuiltinGeneratorResult generateExpression() {
        MatWablyBuiltinGeneratorResult result =
                new MatWablyBuiltinGeneratorResult();
        for(NameExpr arg: this.arguments.getNameExpressions())
            result.addInstructions(expressionGenerator.genNameExpr(arg, node));

        result.addInstruction(new Call(new Idx("end")));
        return result;
    }
}
