package matwably.code_generation.builtin.matwably_builtin.constructors;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;


public class Randi extends ShapeConstructor {


    /**
     * IShapeConstructor constructor method
     *
     * @param node         TIRNode to generateInstructions
     * @param arguments    Arguments to node
     * @param targs        Targets to call
     * @param callName     Call name
     * @param functionInfo Series of MatWably Analysis
     */
    public Randi(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo) {
        super(node, arguments, targs, callName, functionInfo);
    }

    @Override
    public MatWablyBuiltinGeneratorResult generateScalarExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.addInstruction(new Call(new Idx("randi_S")));
        return result;
    }
    /**
     *
     * To be implemented by actual Builtin. Specifies whether the built-in expression returns boxed scalar.
     * i.e. whether the generated built-in call does  not have specialization for the scalar cases. Saving by having
     * shape constructor
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        return arguments.size() == 1;
    }
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        if(arguments.size() == 0) throw new MatWablyError.NotEnoughInputArguments(callName, node);
        Shape shape = valueUtil.getShape(arguments.getNameExpresion(0),node,true);
        if( shape!=null && shape.isConstant() && shape.getHowManyElements(0) > 1) {
            throw new MatWablyError.DimensionArgumentMustBeAPositiveScalar(callName, node);
        }else if( shape!=null && shape.isScalar()){
            result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
        }else{
            result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
            result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
        }
        TIRCommaSeparatedList tempArgs = arguments;
        arguments = new TIRCommaSeparatedList(arguments.copy());
        arguments.removeChild(0);
        super.validateInput();
        result.add(super.generateExpression());
        arguments = tempArgs;
        return result;
    }

    @Override
    public void validateInput() {
        if(arguments.size() == 0) throw new MatWablyError.
                NotEnoughInputArguments(callName, node);

    }
}
