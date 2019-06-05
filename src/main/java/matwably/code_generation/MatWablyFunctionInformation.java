package matwably.code_generation;

import ast.ASTNode;
import ast.Function;
import matwably.MatWablyCommandLineOptions;
import matwably.analysis.MatWablyBuiltinAnalysis;
import matwably.analysis.ambiguous_scalar_analysis.AmbiguousVariableUtil;
import matwably.analysis.intermediate_variable.ReachingDefinitions;
import matwably.analysis.intermediate_variable.TreeExpressionBuilderAnalysis;
import matwably.analysis.memory_management.GCInstructions;
import matwably.analysis.memory_management.hybrid.HybridRCGarbageCollectionAnalysis;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.LogicalVariableUtil;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRFunction;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.analysis.core.ReachingDefs;

import java.util.Map;

/**
 * This class encompasses all the analysis for the Matlab function, it makes it easier to be passed around through all
 * the constructs. Otherwise we require very long constructors.
 */
public class MatWablyFunctionInformation {


    private ast.Function function;
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> functionAnalysis;
    private InterproceduralFunctionQuery functionQueryAnalysis;
    private ValueAnalysisUtil valueAnalysisUtil;
    private MatWablyCommandLineOptions program_options;

    private ExpressionGenerator expressionGenerator;
    private LogicalVariableUtil logical_var_util;
    private ReachingDefs reachingDefs;


    private ReachingDefinitions reachingDefinitions;
    private TreeExpressionBuilderAnalysis treeExpressionBuilderAnalysis;
    private AmbiguousVariableUtil amb_var_util;
    private MatWablyBuiltinAnalysis builtinAnalysis;
    private HybridRCGarbageCollectionAnalysis hybridRCGarbageCollectionAnalysis;
    private Map<ASTNode, GCInstructions> gcInstructionMapping;


    public MatWablyFunctionInformation(Function tree,
                                       IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction,
                                       InterproceduralFunctionQuery functionQuery,
                                       ValueAnalysisUtil valueAnalysisUtil,
                                       MatWablyCommandLineOptions opts) {
        this.function = tree;
        this.functionAnalysis = analysisFunction;
        this.functionQueryAnalysis = functionQuery;
        this.valueAnalysisUtil = valueAnalysisUtil;
        this.program_options = opts;
    }

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
            MatWablyCommandLineOptions opts) {
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
    public MatWablyFunctionInformation(TIRFunction tree, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction, InterproceduralFunctionQuery functionQuery, MatWablyCommandLineOptions opts){
        this.function = tree;
        this.functionAnalysis = analysisFunction;
        this.functionQueryAnalysis = functionQuery;
        this.program_options = opts;
    }
    public void setValueAnalysisUtil(ValueAnalysisUtil valueAnalysisUtil){
        this.valueAnalysisUtil = valueAnalysisUtil;
    }
    public MatWablyBuiltinAnalysis getBuiltinAnalysis() {
        return builtinAnalysis;
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
    public MatWablyCommandLineOptions getProgramOptions() {
        return program_options;
    }

    public AmbiguousVariableUtil getAmbiguousVariableUtil()
    {
        return amb_var_util;
    }

    public void setLogicalVariableUtil(LogicalVariableUtil logical_var_util) {
        this.logical_var_util = logical_var_util;
    }

    public void setExpressionGenerator(ExpressionGenerator expressionGenerator) {
        this.expressionGenerator = expressionGenerator;
    }

    public void setReachingDefinitions(ReachingDefinitions reachingDefs) {
        this.reachingDefinitions = reachingDefs;
    }

    /**
     * @deprecated
     * @param reachingDefs ReachingDefinitions from McSAF
     */
    public void setReachingDefs(ReachingDefs reachingDefs) {
        this.reachingDefs = reachingDefs;
    }

    public void setTreeExpressionBuilderAnalysis(TreeExpressionBuilderAnalysis treeExpressionBuilderAnalysis) {
        this.treeExpressionBuilderAnalysis = treeExpressionBuilderAnalysis;
    }

    public void setAmbigousVariableUtil(AmbiguousVariableUtil amb_var_util) {
        this.amb_var_util = amb_var_util;
    }
    public void setBuiltinAnalysis(MatWablyBuiltinAnalysis builtinAnalysis) {
        this.builtinAnalysis = builtinAnalysis;
    }

    public HybridRCGarbageCollectionAnalysis getHybridRCGarbageCollectionAnalysis() {
        return hybridRCGarbageCollectionAnalysis;
    }

    public void setHybridRCGarbageCollectionAnalysis(HybridRCGarbageCollectionAnalysis hybridRCGarbageCollectionAnalysis) {
        this.hybridRCGarbageCollectionAnalysis = hybridRCGarbageCollectionAnalysis;
    }

    public ReachingDefinitions getReachingDefinitions() {
        return reachingDefinitions;
    }

    public void setGCInstructionMapping(Map<ASTNode,GCInstructions> GCInstructionMapping) {
        this.gcInstructionMapping = GCInstructionMapping;
    }

    public Map<ASTNode, GCInstructions> getGCInstructionMapping() {
        return gcInstructionMapping;
    }
}
