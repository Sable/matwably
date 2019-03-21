package matwably.analysis;

import ast.Function;
import matwably.code_generation.NameExpressionGenerator;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.analysis.core.ReachingDefs;

public class MatWablyFunctionInformation {
    private ast.Function function;
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis;
    private InterproceduralFunctionQuery functionQueryAnalysis;
    private NameExpressionGenerator nameExpressionGenerator;
    private ReachingDefs reachingDefs;

    public MatWablyFunctionInformation(
            ast.Function function,
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis,
            InterproceduralFunctionQuery functionQueryAnalysis,
            ReachingDefs reachingDefs,
           NameExpressionGenerator nameExpressionGenerator) {
        this.function = function;
        this.functionAnalysis = functionAnalysis;
        this.functionQueryAnalysis = functionQueryAnalysis;
        this.nameExpressionGenerator = nameExpressionGenerator;
        this.reachingDefs = reachingDefs;
    }
    public ReachingDefs getReachingDefs(){
        return reachingDefs;
    }
    public Function getFunction() {
        return function;
    }

    public InterproceduralFunctionQuery getFunctionQuery(){
        return functionQueryAnalysis;
    }
    public IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> getFunctionAnalysis(){
        return functionAnalysis;
    }

    public NameExpressionGenerator getNameExpressionGenerator() {
        return nameExpressionGenerator;
    }
}
