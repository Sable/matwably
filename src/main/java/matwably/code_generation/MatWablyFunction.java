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
    /**
     * Analysis node from {@link natlab.tame.valueanalysis.ValueAnalysis}
     */
    InterproceduralAnalysisNode<
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>,
            Args<AggrValue<BasicMatrixValue>>,
            Res<AggrValue<BasicMatrixValue>>> analysisNode;
    /**
     * Wasm Function generated for the corresponding analysis node
     */
    private Function wasmFunction;

    /**
     * MatWably function constructor
     * @param analysisNode ValueAnalysis node
     * @param wasmFunction  WebAssembly generated wasm function.
     */
    public MatWablyFunction(InterproceduralAnalysisNode<
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>,
            Args<AggrValue<BasicMatrixValue>>, Res<AggrValue<BasicMatrixValue>>>
                                    analysisNode, Function wasmFunction) {
        this.analysisNode = analysisNode;
        this.wasmFunction = wasmFunction;
    }

    /**
     * Returns the name of the actual generated wasm function
     * @return Name of generated function
     */
    public String getGeneratedFunctionName(){
        return this.wasmFunction.
                getIdentifier().getName();
    }

    /**
     * Returns name for the original Matlab function
     * @return Name of source Matlab  function
     */
    public String getFunctionName(){
        return this.analysisNode.getFunction().
                getAst().getName().getID();
    }
}
