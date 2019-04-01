package matwably.code_generation.builtin.trial.constructors;

import ast.ASTNode;
import ast.NameExpr;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class AbstractConcat extends MatWablyBuiltinGenerator {


    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public AbstractConcat(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    public boolean allArgumentsScalars(){
        return this.arguments.getNameExpressions().stream().allMatch((NameExpr expr)-> this.valueUtil.isScalar(expr,node));
    }
    public boolean expressionReturnsVoid(){
        return false;
    }

}
