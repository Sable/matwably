package matwably.code_generation.builtin.matwably_builtin.constructors;

import ast.ASTNode;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Linspace extends Constructor {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Linspace(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

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

    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {

        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        boolean arg1IsScalar = valueUtil.isScalar(arguments.
                getNameExpresion(0),node,true);
        boolean arg2IsScalar = valueUtil.isScalar(arguments.
                getNameExpresion(1),node,true);
        if(arg1IsScalar && arg2IsScalar){
            return super.generateExpression();
        }else if(arg1IsScalar){
            result.addInstructions(expressionGenerator.genExpr
                    (arguments.getNameExpresion(0),node));
            result.addInstructions(expressionGenerator.genExpr
                    (arguments.getNameExpresion(1),node));
            result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
        }else if(arg2IsScalar){
            result.addInstructions(expressionGenerator.genExpr
                    (arguments.getNameExpresion(0),node));
            result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
            result.addInstructions(expressionGenerator.genExpr
                    (arguments.getNameExpresion(1),node));
        }else{
            result.addInstructions(expressionGenerator.genExpr
                    (arguments.getNameExpresion(0),node));
            result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
            result.addInstructions(expressionGenerator.genExpr
                    (arguments.getNameExpresion(1),node));
            result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
        }
        if(arguments.size() == 3){
            result.addInstructions(expressionGenerator.genExpr
                    (arguments.getNameExpresion(2),node));
            if(!valueUtil.isScalar(arguments.getNameExpresion(2),node,true)){
                result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
            }
        }
        result.add(this.generateCall());
        return result;
    }

    @Override
    public void validateInput() {
        if(arguments.size() < 2) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size() > 3) throw new MatWablyError.TooManyOutputArguments(callName,node);
    }
}
