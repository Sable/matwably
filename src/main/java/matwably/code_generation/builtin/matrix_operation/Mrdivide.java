package matwably.code_generation.builtin.matrix_operation;

import ast.ASTNode;
import matwably.ast.ConstLiteral;
import matwably.ast.Div;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Mrdivide extends BinaryMatrixOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Mrdivide(ASTNode node, TIRCommaSeparatedList arguments,
                    TIRCommaSeparatedList targs, String callName,
                    MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
        generatedCallName = "rdivide";
    }
    public boolean expressionHasSpecializationForScalar() {
        return true;
    }
    public MatWablyBuiltinGeneratorResult generateExpression(){
        validateInput();
        boolean arg1IsScalar = valueUtil.isScalar(arguments.
                getNameExpresion(0),node,true);
        boolean arg2IsScalar = valueUtil.isScalar(arguments.
                getNameExpresion(1),node,true);
        if(arg1IsScalar && arg2IsScalar) {
            MatWablyBuiltinGeneratorResult res = super.generateInputs();
            res.addInstruction(new Div(new F64(),false));
            return res;
        }else if(arg2IsScalar){
            MatWablyBuiltinGeneratorResult res = super.generateInputs();
            res.addInstruction(new ConstLiteral(new I32(), 0));
            return res.add(generateCall());
        }else{
            throw new MatWablyError.UnsupportedBuiltinCallWithArguments(callName, node, arguments);
        }
    }


}
