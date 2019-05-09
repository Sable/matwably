package matwably.code_generation.builtin.trial.matrix_operation;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.trial.binary_op.numerical.Rdivide;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Mrdivide extends Rdivide {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Mrdivide(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
        generatedCallName = "rdivide";
    }

    public void generateExpression(){
        if(arguments.size() > 2) throw new MatWablyError.TooManyInputArguments(callName, node);
        if(arguments.size() < 2) throw new MatWablyError.NotEnoughInputArguments(callName, node);
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)||
                valueUtil.isScalar(arguments.getNameExpresion(1),node,true)) {
            super.generateExpression();
        }else{
            throw new MatWablyError.UnsupportedBuiltinCallWithArguments(callName, node, arguments);
        }

    }
}
