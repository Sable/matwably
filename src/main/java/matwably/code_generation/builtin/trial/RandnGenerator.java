package matwably.code_generation.builtin.trial;

import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.NameExpressionGenerator;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class RandnGenerator extends ShapeConstructorGenerator {
    /**
     * @param node                    TIRNode for the call
     * @param arguments               Arguments for the call
     * @param targs                   Target names for the call
     * @param callName                Actual function name
     * @param analysis                IntraproceduralValueAnalysis
     * @param functionQuery           InterproceduralFunctionQuery
     * @param nameExpressionGenerator NameExpressionGenerator
     */
    public RandnGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, InterproceduralFunctionQuery functionQuery, NameExpressionGenerator nameExpressionGenerator) {
        super(node, arguments, targs, callName, analysis, functionQuery, nameExpressionGenerator);
    }

    @Override
    public void generateScalarExpression() {
        result.addInstruction(new Call(new Idx("randn_S")));
    }
}
