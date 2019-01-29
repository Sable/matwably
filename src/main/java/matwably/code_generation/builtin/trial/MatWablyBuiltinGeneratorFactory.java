package matwably.code_generation.builtin.trial;

import matwably.code_generation.NameExpressionGenerator;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class MatWablyBuiltinGeneratorFactory {
    public static DefaultBuiltinGenerator  getGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName,
                    InterproceduralFunctionQuery functionQuery,
                    IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction,
                    NameExpressionGenerator name_expr_generator) {
        if (callName.equals("ones")) {
            return new OnesGenerator(node, arguments, targs, callName
                    , analysisFunction, functionQuery, name_expr_generator);
        }else if(callName.equals("zeros")){
            return new ZerosGenerator(node, arguments, targs, callName
                    , analysisFunction, functionQuery, name_expr_generator);
        }else {
            return new DefaultBuiltinGenerator(node, arguments, targs, callName
                    , analysisFunction, functionQuery, name_expr_generator);
        }


    }

    public static boolean isVectorInputBuiltin() {

        return false;
    }
}
