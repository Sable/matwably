package matwably.code_generation.builtin.trial.unary_operation;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.Convert;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.toolkits.analysis.core.Def;

public abstract class LogicalUnaryOp extends UnaryOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public LogicalUnaryOp(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        if(arguments.size()<1) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size()>1) throw new MatWablyError.TooManyInputArguments(callName,node);

        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();

        boolean arg1IsScalar = valueUtil.isScalar(arguments.getNameExpresion(0),node,true);
        result.add(generateInputs());
        // Only scalars are supported with logical ops
        if(arg1IsScalar){
            result.add(generateScalarCall());
            if(disallow_logicals || !matwably_analysis_set.getLogicalVariableUtil().
                    isDefinitionLogical(targets.getName(0).getID(), (Def)node)){
                result.addInstruction(new Convert(new F64(),new I32(),  true));
            }
        }else{
            throw new MatWablyError.UnsupportedBuiltinCall(callName,node);
        }
        return result;
    }


}
