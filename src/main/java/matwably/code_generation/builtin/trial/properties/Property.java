package matwably.code_generation.builtin.trial.properties;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class Property extends MatWablyBuiltinGenerator {
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
        return true;
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
    protected void validateInput(){
        if(arguments.size() > 1) throw new MatWablyError.TooManyInputArguments(callName,node);
        if(arguments.size() < 1) throw new MatWablyError.NotEnoughInputArguments(callName,node);
    }

    public abstract void generateScalarExpression();

    public void generateExpression(){
        validateInput();
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
            generateScalarExpression();
        }else{
            super.generateExpression();
        }
    }
}
