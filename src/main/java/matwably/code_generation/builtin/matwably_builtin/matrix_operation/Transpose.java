package matwably.code_generation.builtin.matwably_builtin.matrix_operation;


import ast.ASTNode;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.matwably_builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Transpose extends MatrixOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Transpose(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);

    }

    /**
     *  Returns whether the function is specialized for re-naming purposes.
     *  If we implement classes for all the generated built-in this function will get deprecated.
     *  The default is that it isn't
     * @return Returns whether the function is specialized.
     */
    @Override
    public boolean isSpecialized() {
        return true;
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
    public MatWablyBuiltinGeneratorResult generateExpression() {
        if(valueUtil.isScalar(arguments.getNameExpresion(0),
                node,true)){
            MatWablyBuiltinGeneratorResult res =
                    new MatWablyBuiltinGeneratorResult();
            res.addInstructions(expressionGenerator.genExpr(arguments.
                    getNameExpresion(0),node));
            return res;
        }else{
            return super.generateExpression();
        }
    }

    @Override
    public void validateInput() {
        if(arguments.size()==0) throw new MatWablyError.
                NotEnoughInputArguments(callName, node);
        if(arguments.size()>1) throw new MatWablyError.
                TooManyInputArguments(callName, node);
        if(targets.size()>1) throw new MatWablyError.
                TooManyOutputArguments(callName,node);
    }
}
