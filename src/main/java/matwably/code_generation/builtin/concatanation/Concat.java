package matwably.code_generation.builtin.concatanation;

import ast.ASTNode;
import ast.NameExpr;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class Concat extends MatWablyBuiltinGenerator {


    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Concat(ASTNode node, TIRCommaSeparatedList arguments,
                  TIRCommaSeparatedList targs, String callName,
                  MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    public boolean allArgumentsScalars(){
        return this.arguments.getNameExpressions().stream().
                allMatch((NameExpr expr)-> this.valueUtil.isScalar(expr,node,true));
    }
    public boolean expressionReturnsVoid(){
        return false;
    }


    @Override
    public void validateInput() {
        // Could check for incompatible shapes
    }
}
