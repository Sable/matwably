package matwably.code_generation.builtin.matwably_builtin.constant;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.toolkits.analysis.core.Def;

public class True  extends Constant {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public True(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    @Override
    public MatWablyBuiltinGeneratorResult generateConstant() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        if(disallow_logicals ||!matwably_analysis_set.getLogicalVariableUtil().
                isDefinitionLogical(targets.getName(0).getID(), (Def)node)){
            result.addInstruction(new ConstLiteral(new F64(), 1));
        }else{
            result.addInstruction(new ConstLiteral(new I32(), 1));
        }
        return result;
    }



}

