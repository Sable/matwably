package matwably.code_generation.builtin.matwably_builtin.binary_op.logical;

import ast.ASTNode;
import matwably.ast.ConstLiteral;
import matwably.ast.Eq;
import matwably.ast.F64;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.binary_op.LogicalBinaryOp;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class Bitwise extends LogicalBinaryOp {


    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Bitwise(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {

        boolean arg1IsScalar = valueUtil.isScalar(arguments.
                getNameExpresion(0),node,true);
        boolean arg2IsScalar = valueUtil.isScalar(arguments.
                getNameExpresion(1),node,true);

        // Only scalars are supported with logical ops
        if(arg1IsScalar && arg2IsScalar){
            MatWablyBuiltinGeneratorResult result =
                    new MatWablyBuiltinGeneratorResult();
            result.addInstructions(expressionGenerator.genExpr(arguments.
                    getNameExpresion(0),node));
            result.addInstruction(new ConstLiteral(new F64(), 0));
            result.addInstruction(new Eq(new F64()));
            result.addInstructions(expressionGenerator.genExpr(arguments.
                    getNameExpresion(1),node));
            result.addInstruction(new ConstLiteral(new F64(), 0));
            result.addInstruction(new Eq(new F64()));
            result.add(generateScalarCall());
            promoteIfAmbiguousLogical(result);
            return result;
        }else{
            return super.generateExpression();
//            throw new MatWablyError.UnsupportedBuiltinCall(callName,node);
        }
    }
}
