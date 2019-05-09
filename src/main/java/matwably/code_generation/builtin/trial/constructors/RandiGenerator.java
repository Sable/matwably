package matwably.code_generation.builtin.trial.constructors;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

public class RandiGenerator extends ShapeConstructorGenerator  {


    /**
     * ShapeConstructor constructor method
     *
     * @param node         TIRNode to generate
     * @param arguments    Arguments to node
     * @param targs        Targets to call
     * @param callName     Call name
     * @param functionInfo Series of MatWably Analysis
     */
    public RandiGenerator(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo) {
        super(node, arguments, targs, callName, functionInfo);
    }

    @Override
    public String get2DConstructorName() {
        return "randi_2D";
    }

    @Override
    public void generateScalarExpression() {
        result.addInstruction(new Call(new Idx("randi_S")));
    }

    @Override
    public void generateExpression() {
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
        arguments.removeChild(0);
        super.generateExpression();
    }
}
