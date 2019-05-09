package matwably.code_generation.builtin.trial.properties;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Ndims extends Property {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Ndims(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    @Override
    public void generateScalarExpression() {
        result.addInstruction(new ConstLiteral(new F64(),2));
    }
}
