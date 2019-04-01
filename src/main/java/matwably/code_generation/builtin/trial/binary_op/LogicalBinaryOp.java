package matwably.code_generation.builtin.trial.binary_op;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Convert;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.code_generation.MatWablyError;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class LogicalBinaryOp extends BinaryOp {

    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public LogicalBinaryOp(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    @Override
    public void generateExpression() {
        if(arguments.size()<2) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size()>2) throw new MatWablyError.TooManyInputArguments(callName,node);
        if(arguments.size()>2) throw new MatWablyError.TooManyInputArguments(callName,node);
        boolean arg1IsScalar = valueUtil.isScalar(arguments.getNameExpresion(0),node);
        boolean arg2IsScalar = valueUtil.isScalar(arguments.getNameExpresion(1),node);

        generateInputs();
        // Only scalars are supported with logical ops
        if(arg1IsScalar && arg2IsScalar){
            generateScalarCall();
            // TODO: (treat logicals as i32 in wasm local generation), for this I must map every function with type statically
            result.addInstruction(new Convert(new I32(),new F64(),  true));
        }else{
            throw new MatWablyError.UnsupportedBuiltinCall(callName,node);
        }
    }
}
