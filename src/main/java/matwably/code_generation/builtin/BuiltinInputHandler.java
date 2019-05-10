package matwably.code_generation.builtin;

import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public abstract class BuiltinInputHandler {
    TIRNode stmt;
    TIRCommaSeparatedList arguments;
    IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> valueAnalysis;

    public void setResult(MatWablyBuiltinGeneratorResult result) {
        this.result = result;
    }

    MatWablyBuiltinGeneratorResult result;
    abstract boolean isInForm();
    abstract void generate();
    public TIRNode getStmt() {
        return stmt;
    }

//    public TIRCommaSeparatedList getArguments() {
//        return arguments;
//    }
//
//    public IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> getValueAnalysis() {
//        return valueAnalysis;
//    }

    public void setStmt(TIRNode stmt) {
        this.stmt = stmt;
    }

    public void setArguments(TIRCommaSeparatedList arguments) {
        this.arguments = arguments;
    }

    public void setValueAnalysis(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> valueAnalysis) {
        this.valueAnalysis = valueAnalysis;
    }
}
