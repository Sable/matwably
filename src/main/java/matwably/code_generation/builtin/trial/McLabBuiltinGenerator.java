package matwably.code_generation.builtin.trial;

import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public abstract class  McLabBuiltinGenerator<Res> {
    TIRNode node;
    TIRCommaSeparatedList arguments;
    IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis;
    ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis;
    InterproceduralFunctionQuery functionQuery;
    TIRCommaSeparatedList targets;
    String callName;
    Res result;

    /**
     *
     * @param node
     * @param arguments
     * @param targs
     * @param callName
     * @param analysis
     * @param functionQuery
     */
    public McLabBuiltinGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName,
                                 IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
                                 InterproceduralFunctionQuery functionQuery){
        this.node = node;
        this.arguments = arguments;
        this.functionAnalysis = analysis;
        this.targets = targs;
        this.callName = callName;
        this.functionQuery = functionQuery;
    }

    public abstract boolean isSpecialized();
    public boolean returnsList() {
        return targets.size() > 0;
    }
    public boolean returnsVoid() {
        return targets.size() == 0;
    }
    public boolean isMatlabBuiltin() {
        return Builtin.getInstance(this.callName)!=null;
    }
    public abstract boolean isScalarOutput();
    public abstract boolean returnsBoxedScalar();
    public abstract boolean returnsScalarVector();

    public Res generate(){
            generateExpression();
            generateSetToTarget();
            return result;
    }
    public void generateExpression(){
        this.generateInputs();
        this.generateCall();
    }

    public boolean isInputInCanonicalForm(){
        return false;
    }

    public String getGeneratedFunctionName() {
        return this.callName;
    }

    abstract void generateInputs();

    abstract void generateCall();

    abstract void generateSetToTarget();




}
