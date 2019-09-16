package matwably.code_generation.builtin.matwably_builtin.matrix_operation;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Mpower extends BinaryMatrixOp{
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Mpower(ASTNode node, TIRCommaSeparatedList arguments,
                  TIRCommaSeparatedList targs, String callName,
                  MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
        generatedCallName = "power";
    }
}
