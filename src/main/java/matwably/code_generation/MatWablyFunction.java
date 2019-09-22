package matwably.code_generation;

import matwably.ast.Function;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;

/**
 * Class contains the result of the generation of each function in the MatWably
 * compiler
 */
public class MatWablyFunction {
    InterproceduralAnalysisNode<
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>,
            Args<AggrValue<BasicMatrixValue>>,
            Res<AggrValue<BasicMatrixValue>>> analysisNode;
    private Function wasmFunction;

    public MatWablyFunction(InterproceduralAnalysisNode<IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>, Args<AggrValue<BasicMatrixValue>>, Res<AggrValue<BasicMatrixValue>>> analysisNode, Function wasmFunction) {
        this.analysisNode = analysisNode;
        this.wasmFunction = wasmFunction;
    }

    public String getGeneratedFunctionName(){
        return this.wasmFunction.
                getIdentifier().getName();
    }
}
