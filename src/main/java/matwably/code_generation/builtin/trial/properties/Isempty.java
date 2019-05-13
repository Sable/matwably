package matwably.code_generation.builtin.trial.properties;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.ConstLiteral;
import matwably.ast.I32;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

public class Isempty extends LogicalProperty {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Isempty(ASTNode node, TIRCommaSeparatedList arguments,
                   TIRCommaSeparatedList targs,
                   String callName,
                   MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * Boolean indicating whether the matrix property is true
     * @param shape Shape for the target variable
     * @return boolean indicating whether the inputs matrix property is true
     */
    @Override
    protected boolean shapeHasProperty(Shape shape) {
        return shape!=null  && shape.getHowManyElements(0) == 0;
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
    /**
     * Logical flag indicating whether the property is true for a scalar
     */
    @Override
    protected MatWablyBuiltinGeneratorResult generateLogicalScalarExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.addInstruction(new ConstLiteral(new I32(), 0));
        return result;
    }

}
