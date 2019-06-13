package matwably.code_generation.builtin.trial.reduction_operation;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

/**
 * The following are the variations of reduction generation:
 * In all of them, A may be a matrix or a scalar
 * S = sum(A)
 * S = sum(A,dim)
 * Any other version of the reduction operation is not taken care of by `ValueAnalysis`.
 */

public class NumericReduction extends MatWablyBuiltinGenerator{
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public NumericReduction(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
        if(arguments.size() < 1) throw new MatWablyError.NotEnoughInputArguments(callName, node);
        if(arguments.size() > 2) throw new MatWablyError.TooManyInputArguments(callName, node);
        if(arguments.size()== 2 &&
                (valueUtil.hasMoreThanTwoDimensions(arguments.getNameExpresion(1),node, true))){
            throw new MatWablyError.DimensionArgumentMustBeAPositiveScalar(callName, node);
        }

    }
    public boolean isSpecialized(){
        return true;
    }
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression(){
        validateInput();
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();

        result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
        if(arguments.size() == 2)
            result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(1),node));
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
            // Check the second argument is actually a scalar in right range, throw error otherwise, as would Matlab
            if(arguments.size()== 2){
                if(!valueUtil.isScalar(arguments.getNameExpresion(1),node,true)){
                    result.addInstruction(new Call(new Idx("isscalar")));
                    result.addInstruction(new If(new Opt<>(),new Opt<>(),
                            new List<>(new ConstLiteral(new I32(), 15),
                                    new Call(new Idx("throwError"))),new List<>()));
                    result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
                    // Get first value
                    result.addInstructions(new Load(new I32(), new Opt<>(new MemArg((short)8,
                            (short)4)), new Opt<>(), new Opt<>()),new Load(new F64(),new Opt<>(),
                            new Opt<>(), new Opt<>()));
                }
                // check that is an appropriate dimension
                result.addInstructions(new ConstLiteral(new F64(), 1), new Lt(new F64(), false));
                result.addInstruction(new If(new Opt<>(),new Opt<>(),
                        new List<>(new ConstLiteral(new I32(), 15),
                                new Call(new Idx("throwError"))),new List<>()));
            }
        }else{
            // Extra argument for the omitnan!!
            result.addInstruction(new ConstLiteral(new I32(),0));
            result.add(generateCall());
        }
        return result;
    }
}
