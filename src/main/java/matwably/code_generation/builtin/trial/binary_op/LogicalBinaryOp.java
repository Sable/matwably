package matwably.code_generation.builtin.trial.binary_op;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.Convert;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.toolkits.analysis.core.Def;

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
    public void validateInput(){
        if(targets.size() > 1) throw new MatWablyError.TooManyOutputArguments(callName, node);
        if(arguments.size()<2) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size()>2) throw new MatWablyError.TooManyInputArguments(callName,node);
        if(arguments.size()>2) throw new MatWablyError.TooManyInputArguments(callName,node);
    }
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        validateInput();

        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        boolean arg1IsScalar = valueUtil.isScalar(arguments.getNameExpresion(0),node,true);
        boolean arg2IsScalar = valueUtil.isScalar(arguments.getNameExpresion(1),node,true);

        result.add(generateInputs());
        // Only scalars are supported with logical ops
        if(arg1IsScalar && arg2IsScalar){
            result.add(generateScalarCall());
        }else{
            throw new MatWablyError.UnsupportedBuiltinCall(callName,node);
        }
        promoteIfAmbiguousLogical(result);
        return result;
    }

     protected void promoteIfAmbiguousLogical(MatWablyBuiltinGeneratorResult result) {
        if(disallow_logicals||!matwably_analysis_set.getLogicalVariableUtil().
                isDefinitionLogical(targets.getName(0).getID(), (Def)node)){
            result.addInstruction(new Convert(new F64(),new I32(),  true));
        }
    }


}
