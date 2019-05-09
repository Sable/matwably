package matwably.code_generation.builtin.trial.constant;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Pi extends Constant {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Pi(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    @Override
    public void generateConstant() {
        result.addInstruction(new Call(new Idx("PI")));
    }



}