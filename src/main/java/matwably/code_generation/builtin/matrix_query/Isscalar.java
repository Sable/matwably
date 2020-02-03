package matwably.code_generation.builtin.matrix_query;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.ConstLiteral;
import matwably.ast.I32;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

public class Isscalar extends LogicalProperty {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Isscalar(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
        return shape!=null && shape.getHowManyElements(0) == 1;
    }
    /**
     * Logical flag indicating whether the property is true for a scalar
     */
    @Override
    protected MatWablyBuiltinGeneratorResult generateLogicalScalarExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.addInstruction(new ConstLiteral(new I32(), 1));
        return result;
    }

}
