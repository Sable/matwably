package matwably.code_generation.builtin.matrix_operation;

import ast.ASTNode;
import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.ast.Mul;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Mtimes extends BinaryMatrixOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Mtimes(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
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

    public MatWablyBuiltinGeneratorResult generateExpression(){
        super.validateInput();
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)
                &&valueUtil.isScalar(arguments.getNameExpresion(1),node,true)){
            result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(0), node));
            result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(1), node));
            result.addInstruction(new Mul(new F64()));
        }else{
            result.add(super.generateInputs());
            result.addInstruction(new ConstLiteral(new I32(),0));
            result.add(super.generateCall());
        }
        return result;
    }

    @Override
    public String getGeneratedBuiltinName() {
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)
                || valueUtil.isScalar(arguments.getNameExpresion(1),node,true)){
            return "times";
        }
        return callName;
    }

    @Override
    public boolean isSpecialized() {
        return true;
    }
}
