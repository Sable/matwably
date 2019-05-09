package matwably.analysis;

import ast.Function;
import matwably.CommandLineOptions;
import matwably.analysis.ambiguous_scalar_analysis.AmbiguousVariableUtil;
import matwably.analysis.intermediate_variable.TreeExpressionBuilderAnalysis;
import matwably.code_generation.ExpressionGenerator;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.LogicalVariableUtil;
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


    private final LogicalVariableUtil logical_var_util;
    private ast.Function function;
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis;
    private InterproceduralFunctionQuery functionQueryAnalysis;
    private ExpressionGenerator expressionGenerator;
    private ReachingDefs reachingDefs;
    private ValueAnalysisUtil valueAnalysisUtil;
    private TreeExpressionBuilderAnalysis treeExpressionBuilderAnalysis;
    private CommandLineOptions program_options;
    private  AmbiguousVariableUtil amb_var_util;

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
     * @param expressionGenerator This is the expression generator for MatWably, is used throughout to generate expressions
     * @param opts Command line options for the program
     */
    public MatWablyFunctionInformation(
            Function function,
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis,
            InterproceduralFunctionQuery functionQueryAnalysis,
            ReachingDefs reachingDefs,
            ValueAnalysisUtil valueAnalysisUtil,
            ExpressionGenerator expressionGenerator,
            TreeExpressionBuilderAnalysis treeExpressionBuilderAnalysis,
            AmbiguousVariableUtil amb_var_util,
            LogicalVariableUtil logical_var_util,
            CommandLineOptions opts) {
        this.function = function;
        this.functionAnalysis = functionAnalysis;
        this.functionQueryAnalysis = functionQueryAnalysis;
        this.expressionGenerator = expressionGenerator;
        this.reachingDefs = reachingDefs;
        this.valueAnalysisUtil = valueAnalysisUtil;
        this.treeExpressionBuilderAnalysis = treeExpressionBuilderAnalysis;
        this.amb_var_util = amb_var_util;
        this.logical_var_util = logical_var_util;
        this.program_options = opts;
    }

    /**
     * Getter to obtain the LogicalVariableUtil helper class
     * @return an instance of LogicalVariableUtil utility class
     */
    public LogicalVariableUtil getLogicalVariableUtil() {
        return logical_var_util;
    }
    /**
     * Getter for the TreeExpressionBuilderAnalysis
     * @return an instance of the analysis on the current function
     */
    public TreeExpressionBuilderAnalysis getTreeExpressionBuilderAnalysis() {
        return treeExpressionBuilderAnalysis;
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
     * Getter for ExpressionGenerator analysis
     * @return Getter for ExpressionGenerator analysis
     */
    public ExpressionGenerator getExpressionGenerator() {
        return expressionGenerator;
    }

    /**
     * Getter for program options
     * @return Returns program options
     */
    public CommandLineOptions getProgramOptions() {
        return program_options;
    }

    public AmbiguousVariableUtil getAmbiguousVariableUtil() {
        return amb_var_util;
    }
}
