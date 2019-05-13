package matwably.code_generation.builtin.trial.properties;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.ConstLiteral;
import matwably.ast.Convert;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.toolkits.analysis.core.Def;

public abstract class LogicalProperty extends Property {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    LogicalProperty(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    @Override
    public MatWablyBuiltinGeneratorResult generateScalarExpression() {
        return generateLogicalScalarExpression();
    }


    /**
     * Generated method for the built-in
     */
    public MatWablyBuiltinGeneratorResult generateExpression(){
        validateInput();
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        if(valueUtil.isScalar(arguments.getNameExpresion(0), node,true)){
            result.add(generateLogicalScalarExpression());
        }else{
            Shape shape = valueUtil.getShape(arguments.getNameExpresion(0), node, false);
            if(shape != null){
                if(shapeHasProperty(shape)){
                    result.addInstruction(new ConstLiteral(new I32(), 1));
                }else{
                    result.addInstruction(new ConstLiteral(new I32(), 0));
                }
            }else{
               result.add(super.generateExpression());
            }
        }
        if(disallow_logicals ||!matwably_analysis_set.getLogicalVariableUtil().
                isDefinitionLogical(targets.getName(0).getID(), (Def)node)){
            result.addInstruction(new Convert(new F64(),new I32(),  true));
        }

        return result;
    }

    /**
     *boolean indicating whether the matrix property is true
     * @param shape Shape for the target variable
     * @return boolean indicating whether the inputs matrix property is true
     */
    protected abstract boolean shapeHasProperty(Shape shape);

    /**
     * Logical flag indicating whether the property is true for a scalar
     */
    protected abstract MatWablyBuiltinGeneratorResult generateLogicalScalarExpression();
}
