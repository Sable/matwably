package matwably.code_generation.builtin;

import matwably.ast.Instruction;
import matwably.ast.List;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public abstract class BuiltinSimplifier {
    public TIRNode node;
    public String callName;
    public TIRCommaSeparatedList arguments;
    public TIRCommaSeparatedList targets;
    public IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
    public abstract boolean isSimplifiable();
    public abstract List<Instruction> simplify();
    public TIRCommaSeparatedList getArguments() {
        return arguments;
    }

    public void setArguments(TIRCommaSeparatedList arguments) {
        this.arguments = arguments;
    }

    public void setCall(TIRNode node) {
        this.node = node;
    }

    public void setAnalysis(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis) {
        this.analysis = analysis;
    }

}
