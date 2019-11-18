package matwably.code_generation.builtin.matwably_builtin.concatanation;

import ast.ASTNode;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.matwably_builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.input_generation.VectorInputGenerator;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import natlab.tame.tir.TIRCommaSeparatedList;

/**
 * Generator for `cat` built-in
 */
public class Cat extends Concat {


    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Cat(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
     * Generates the code for the first argument and returns whether the first argument is a scalar
     * @return  returns whether the dim argument is a known scalar
     */
    private boolean generateDimensionCode(){
        if(arguments.size() >= 1){
            boolean returnValue = this.valueUtil.
                    isScalar(arguments.getNameExpresion(0),node,true);
            // Check if argument is scalar, if it is generateInstructions, if not, we assume it is either a boxed scalar or an input vec
            if(returnValue){
                this.result.addInstructions(this.expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
            }else{
                this.result.addInstructions(this.expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
            }
            return returnValue;
        }
        return false;
    }

    @Override
    public void validateInput() {
        if(arguments.size() == 0 )
            throw new MatWablyError.NotEnoughInputArguments(callName, node);
    }

    /**
     * Generation strategy for Matlab's  `cat` function.
     * Call contains the following format: cat(dim, arr1,arr2,...,arrN)
     * Gen dimension, based on whether it is scalar or not, since we may not know this at run-time, we need four versions
     * The arrays are generated by using the {@link VectorInputGenerator matwably.code_generation.builtin.trial.VectorInputGenerator}
     */
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        // First argument is the dimension, the rest of the arguments are to be transformed input a vector input
        boolean dimIsScalar = this.generateDimensionCode();
        if(arguments.size() == 1){
            if(dimIsScalar){
                result.addInstructions(MachArrayIndexing.createEmptyArray(2));
            }else{
                result.addInstruction(new Call(new Idx("cat_M")));
            }
        }else{
            TIRCommaSeparatedList list = (TIRCommaSeparatedList) arguments.copy();
            list.removeChild(0);// Remove dimension, and generateInstructions the rest
            VectorInputGenerator vectorInputGenerator = new VectorInputGenerator(node,list, matwably_analysis_set);
            result.add(vectorInputGenerator.generate());
            if(dimIsScalar){
                result.addInstruction(new Call(new Idx("cat_SM")));
            }else{
                result.addInstruction(new Call(new Idx("cat_MM")));
            }
        }
        return result;
    }

}
