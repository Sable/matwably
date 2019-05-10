package matwably.code_generation.builtin.trial;

import ast.ASTNode;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public abstract class  McLabBuiltinGenerator<Res> {
    private final InterproceduralFunctionQuery functionQuery;
    protected ASTNode node;
    protected TIRCommaSeparatedList arguments;
    protected TIRCommaSeparatedList targets;
    protected String callName;
    protected String generatedCallName;
    protected Builtin tamerBuiltin;
    protected Res result;

    /**
     *
     * @param node  ASTNode making the call, could either be TIRCallStmt, or TIRArrayGetStmt
     * @param arguments  Argument list of NameExpressions
     * @param targs Target List of NameExpressions
     * @param callName Name of call
     * @param analysis  IntraproceduralValueAnalysis performed on TameIR
     * @param functionQuery InterproceduralFunctionQuery based on the ValueAnalysis
     */
    public McLabBuiltinGenerator(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName,
                                 IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
                                 InterproceduralFunctionQuery functionQuery){
        this.node = node;
        this.arguments = arguments;
        this.targets = targs;
        this.callName = callName;
        this.tamerBuiltin = Builtin.getInstance(callName);
        this.functionQuery = functionQuery;
    }

    public abstract boolean isSpecialized();

    public boolean returnsList() {
        return targets.size() > 0;
    }
    public boolean returnsSingleTarget() {
        return targets.size() == 1;
    }
    public boolean returnsZeroTargets() {
        return targets.size() == 0;
    }

    /**
     * Function used to query whether the builtin function returns void
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a
     * @return boolean signifying whether the builtin call returns a value.
     */
    public abstract boolean expressionReturnsVoid();


    public boolean isMatlabBuiltin() {
        return Builtin.getInstance(this.callName)!=null;
    }
    public Res getResult(){
        return result;
    }

    /**
     * Generator function, it only generates call if the call is not pure or there is more than one target.
     */
    public void generate(){
        if (!functionQuery.isUserDefinedFunction(callName) && isPure()&& returnsZeroTargets()) return;
        generateExpression();
        generateSetToTarget();
    }
    public void generateExpression(){
        this.generateInputs();
        this.generateCall();
    }



    /**
     * Returns whether the function is a known as pure, note that if it returns false, it may still be the case that is
     * this is for cases of user defined functions.
     * @return Returns whether the function is a known as pure.
     */
    boolean isPure() {
        // Traverse through class hierarchy with reflection, to check for pure class
        Class<?> classObj = Builtin.getInstance(this.callName).getClass();
        while(classObj!= null){
            if(classObj.getName().contains("Pure")) return true;
            classObj = classObj.getSuperclass();
        }
        return false;
    }
    public String getGeneratedBuiltinName(){
        return  (generatedCallName != null)?generatedCallName: callName;
    }
    /**
     * Default generate input as it comes.
     */
    abstract void generateInputs();

    abstract void generateCall();

    abstract void generateSetToTarget();




}
