package matwably.code_generation.builtin.trial.unary_operation;

import ast.ASTNode;
import matwably.code_generation.MatWablyError;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class UnaryOp  extends MatWablyBuiltinGenerator {
    @Override
    public boolean isSpecialized() {
        return true;
    }

    /**
     * To be implemented by child classes, this function generates the scalar instruction or scalar call
     * e.g. if the operation is binary addition,
     * this function adds the instruction `f64.add` to the return object
     */
    public abstract void generateScalarCall();
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public UnaryOp(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }
    public void generateExpression(){
        if(arguments.size()<1) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size()>1) throw new MatWablyError.TooManyInputArguments(callName,node);
        generateInputs();
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
            generateScalarCall();
        }else{
            generateCall();
        }

    }
}
