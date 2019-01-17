package matwably.code_generation.builtin.trial;

import matwably.ast.ConstLiteral;
import matwably.ast.F64;
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
    public ZerosGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, InterproceduralFunctionQuery functionQuery, NameExpressionGenerator nameExpressionGenerator) {
        super(node, arguments, targs, callName, programAnalysis, analysis, functionQuery, nameExpressionGenerator);
    }
    @Override
    public void simplify() {
        result.addInstruction(new ConstLiteral(new F64(), 0));
    }

}
