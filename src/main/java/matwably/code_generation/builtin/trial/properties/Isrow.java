package matwably.code_generation.builtin.trial.properties;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

public class Isrow extends LogicalProperty {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Isrow(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * boolean indicating whether the matrix property is true
     *
     * @param shape Shape for the target variable
     * @return boolean indicating whether the inputs matrix property is true
     */
    @Override
    protected boolean shapeHasProperty(Shape shape) {
        return shape!=null && shape.isRowVector();
    }
    /**
     * Logical flag indicating whether the property is true for a scalar
     */
    @Override
    protected void generateLogicalScalarExpression() {

    }

}
