package matwably.code_generation.builtin.matwably_builtin;

import ast.ASTNode;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public abstract class  McLabBuiltinGenerator<Res extends McLabBuiltinGenerationResult> {
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
    public Res getResult()
    {
        if(result == null) return generate();
        return result;
    }


    /**
     * Returns whether the function call is either 'subsref' or 'subasgsn' matlab
     * functions
     * @return Returns whether the function call is either 'subsref' or 'subasgsn'
     */
    protected boolean isIndexing() {
        // Traverse through class hierarchy with reflection, to check for pure class
        if(Builtin.getInstance(this.callName) == null) return false;
        return Builtin.getInstance(this.callName) instanceof Builtin.AbstractIndexing;
    }

    /**
     * Returns whether the function is a known as pure, note that if it returns false, it may still be the case that is
     * this is for cases of user defined functions.
     * @return Returns whether the function is a known as pure.
     */
    protected boolean isPure() {
        // Traverse through class hierarchy with reflection, to check for pure class
        if(Builtin.getInstance(this.callName) == null) return false;
        return Builtin.getInstance(this.callName) instanceof Builtin.AbstractPureFunction;
    }
    public String getGeneratedBuiltinName(){
        return  (generatedCallName != null)?generatedCallName: callName;
    }

    /**
     * Generator function, it only generates call if the call is not pure or there is more than one target.
     * @return Generator result
     */
    protected abstract Res generate();

    /**
     * Generates expression
     * @return Generator result
     */
    protected abstract Res generateExpression();
    /**
     * Default generateInstructions input as it comes.
     */
    protected  abstract Res generateInputs();

    protected abstract Res generateCall();

    protected  abstract Res generateSetToTarget();

    public abstract void validateInput();

    public abstract Res getGeneratedExpression();
    public abstract Res getGeneratedCallInstruction();
    public abstract Res getGeneratedCallStmt();
    public abstract Res getGeneratedSetToTarget();


}
