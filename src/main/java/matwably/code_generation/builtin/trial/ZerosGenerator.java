package matwably.code_generation.builtin.trial;

import matwably.code_generation.NameExpressionGenerator;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class ZerosGenerator extends ShapeConstructorGenerator {
    /**
     * @param node
     * @param arguments
     * @param targs
     * @param callName
     * @param programAnalysis
     * @param analysis
     * @param functionQuery
     * @param nameExpressionGenerator
     */
    public ZerosGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName,
                          ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, InterproceduralFunctionQuery functionQuery, NameExpressionGenerator nameExpressionGenerator) {
        super(node, arguments, targs, callName, analysis, functionQuery, nameExpressionGenerator);
    }

    @Override
    public void generateScalarExpression() {

    }

    @Override
    public void generate2DMatrixExpression() {

    }

    @Override
    public void generateGeneralExpression() {

    }
}