package matwably.code_generation.builtin.matwably_builtin.matrix_query;

import ast.ASTNode;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class Property extends MatrixQuery {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Property(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }
    @Override
    public boolean isSpecialized() {
        return false;
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
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    public void validateInput(){
        if(arguments.size() > 1) throw new MatWablyError.TooManyInputArguments(callName,node);
        if(arguments.size() < 1) throw new MatWablyError.NotEnoughInputArguments(callName,node);
    }

    public abstract MatWablyBuiltinGeneratorResult generateScalarExpression();

    public MatWablyBuiltinGeneratorResult generateExpression(){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
            result.add(generateScalarExpression());
        }else{
            result.add(super.generateExpression());
        }
        return result;
    }
}
