package matwably.analysis;

import ast.Function;
import matwably.code_generation.NameExpressionGenerator;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.analysis.core.ReachingDefs;

/**
 * This class encompasses all the analysis for the Matlab function, it makes it easier to be passed around through all
 * the constructs. Otherwise we require very long constructors.
 */
public class MatWablyFunctionInformation {

    private ast.Function function;
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis;
    private InterproceduralFunctionQuery functionQueryAnalysis;
    private NameExpressionGenerator nameExpressionGenerator;
    private ReachingDefs reachingDefs;
    private ValueAnalysisUtil valueAnalysisUtil;


    /**
     * Getter for valueAnalysisUtil
     * @return ValueAnalysisUtility object.
     */
    public ValueAnalysisUtil getValueAnalysisUtil() {
        return valueAnalysisUtil;
    }


    /**
     * Default constructor takes all the analyses for the MatWably code generator.
     * @param function TameIR function
     * @param functionAnalysis IntraproceduralValueAnalysis for the function. May be remove later, ones ValueAnalysisUtility
     *                         replaces every use of it.
     * @param functionQueryAnalysis InterproceduralFunctionQuery is an API that allows functions to query program-wise
     *                              information
     * @param reachingDefs  Reaching Definitions analysis for the current program
     * @param valueAnalysisUtil Is a ValueAnalysisUtility that allows easy query of variables from IntraproceduralValueAnalysis.
     * @param nameExpressionGenerator This is the expression generator for MatWably, is used throughout to generate expressions
     *                                where necessary.
     */
    public MatWablyFunctionInformation(
            ast.Function function,
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis,
            InterproceduralFunctionQuery functionQueryAnalysis,
            ReachingDefs reachingDefs,
           ValueAnalysisUtil valueAnalysisUtil,
           NameExpressionGenerator nameExpressionGenerator) {
        this.function = function;
        this.functionAnalysis = functionAnalysis;
        this.functionQueryAnalysis = functionQueryAnalysis;
        this.nameExpressionGenerator = nameExpressionGenerator;
        this.reachingDefs = reachingDefs;
        this.valueAnalysisUtil = valueAnalysisUtil;
    }

    /**
     * Getter for ReachingDefs analysis
     * @return Getter for ReachingDefs analysis
     */
    public ReachingDefs getReachingDefs(){
        return reachingDefs;
    }

    /**
     * Getter for TameIR function, since some analyses require a higher ast.Function, we make it into ast.Function.
     * @return Getter for ast.Function analysis
     */
    public Function getFunction() {
        return function;
    }
    /**
     * Getter for InterproceduralFunctionQuery analysis
     * @return Getter for InterproceduralFunctionQuery analysis
     */
    public InterproceduralFunctionQuery getFunctionQuery(){
        return functionQueryAnalysis;
    }
    /**
     * Getter for IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis
     * @return Getter for IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis
     */
    public IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> getFunctionAnalysis(){
        return functionAnalysis;
    }
    /**
     * Getter for NameExpressionGenerator analysis
     * @return Getter for NameExpressionGenerator analysis
     */
    public NameExpressionGenerator getNameExpressionGenerator() {
        return nameExpressionGenerator;
    }
}
