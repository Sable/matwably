package matwably.code_generation.builtin.trial;

import matwably.code_generation.builtin.ResultWasmGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public abstract class  Builtin {
    TIRNode stmt;
    TIRCommaSeparatedList arguments;
    IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> callAnalysis;
    ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis;
    TIRCommaSeparatedList targets;
    String callName;
    ResultWasmGenerator result;

    abstract boolean canBeSimplified();

    abstract boolean simplify();

    abstract void isInputCanonical();

    abstract void generateInputs();

    abstract void generateCall();

    abstract void generateSetToTarget();


}
