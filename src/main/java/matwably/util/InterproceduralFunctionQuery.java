package matwably.util;

import ast.Function;
import ast.Name;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class InterproceduralFunctionQuery {
    ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis;
    public InterproceduralFunctionQuery(ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis) {
        this.programAnalysis = programAnalysis;
    }
    public boolean isUserDefinedFunction(String name){
        return programAnalysis.getNodeList().stream().map(InterproceduralAnalysisNode::getAnalysis)
                .map(IntraproceduralValueAnalysis::getTree).map(Function::getName).map(Name::getID)
                .anyMatch((String str)->str.equals(name));
    }
}
