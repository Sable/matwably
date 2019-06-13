package matwably.util;

import ast.Function;
import ast.Name;
import natlab.tame.builtin.Builtin;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.tir.TIRFunction;
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

    public TIRFunction getFunctionInCallGraph(String name){
        return programAnalysis.getNodeList().stream().map(InterproceduralAnalysisNode::getAnalysis)
                .map(IntraproceduralValueAnalysis::getTree).filter((TIRFunction f)->{
                    return f.getName().getID().equals(name);
                }).findFirst().orElse(null);
    }
    public boolean isCallPure(String name){
        // Traverse through class hierarchy with reflection, to check for pure class
        if(isUserDefinedFunction(name))return false;
        Class<?> classObj = Builtin.getInstance(name).getClass();
        while(classObj!= null){
            if(classObj.getName().contains("Pure")) return true;
            classObj = classObj.getSuperclass();
        }
        return false;
    }
}
