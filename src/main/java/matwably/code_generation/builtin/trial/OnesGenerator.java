package matwably.code_generation.builtin.trial;

import matwably.code_generation.NameExpressionGenerator;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class OnesGenerator extends ShapeConstructorGenerator {
    /**
     * @param node
     * @param arguments
     * @param targs
     * @param callName
     * @param analysis
     * @param functionQuery
     * @param nameExpressionGenerator
     */
    public OnesGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, InterproceduralFunctionQuery functionQuery, NameExpressionGenerator nameExpressionGenerator) {
        super(node, arguments, targs, callName, analysis, functionQuery, nameExpressionGenerator);
    }
}

