package matwably.code_generation;

import ast.Name;
import ast.NameExpr;
import ast.Stmt;
import matjuice.analysis.ParameterCopyAnalysis;
import matjuice.analysis.PointsToAnalysis;
import matjuice.transformer.CopyInsertion;
import matjuice.transformer.MJCopyStmt;
import matjuice.transformer.ParameterCopyTransformer;
import matwably.MatWablyCommandLineOptions;
import matwably.analysis.Locals;
import matwably.analysis.MatWablyBuiltinAnalysis;
import matwably.analysis.ambiguous_scalar_analysis.AmbiguousVariableAnalysis;
import matwably.analysis.ambiguous_scalar_analysis.AmbiguousVariableUtil;
import matwably.analysis.intermediate_variable.ReachingDefinitions;
import matwably.analysis.intermediate_variable.TreeExpressionBuilderAnalysis;
import matwably.analysis.intermediate_variable.UseDefDefUseChain;
import matwably.analysis.memory_management.dynamic.DynamicGCCallInsertion;
import matwably.analysis.memory_management.dynamic.DynamicRCGarbageCollection;
import matwably.analysis.memory_management.hybrid.HybridGCCallInsertionMap;
import matwably.analysis.memory_management.hybrid.HybridRCGarbageCollectionAnalysis;
import matwably.ast.*;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.MatWablyBuiltinGenerator;
import matwably.code_generation.stmt.StmtHook;
import matwably.code_generation.wasm.SwitchStatement;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import matwably.util.*;
import natlab.tame.tir.*;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.toolkits.rewrite.TempFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Class generates TIRFunctions given the ValueAnalysis, the index of the function inside the analysis to be generated
 * and the command line options for the generation. i.e. what optimizations to apply
 */
public class FunctionGenerator {


    private TIRFunction matlabFunction;


    private MatWablyCommandLineOptions opts;
    private List<TypeUse> output_parameters;
    private List<TypeUse> locals;
    private List<TypeUse> parameters;
    private HashMap<String, TypeUse> local_map;
    private Stack<LoopMetaInformation> loopStack = new Stack<>();
    private MatWablyBuiltinAnalysis builtin_analysis;
    private TreeExpressionBuilderAnalysis elim_var_analysis = null;
    private ValueAnalysisUtil valueAnalysisUtil;
    private LogicalVariableUtil logicalVariableUtil;
    private UseDefDefUseChain udChain;
    private String i32_scrap_local;
    private String f64_scrap_local;

    /**
     * Returns the ast of the generated function
     * @return Returns the generated WebAssembly function.
     */
    public Function getAst() {
        return function;
    }

    private Function function;

    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction;
    private InterproceduralFunctionQuery interproceduralFunctionQuery;
    private ExpressionGenerator expressionGenerator;
    private MatWablyFunctionInformation functionAnalyses;

    /**
     * Function Generator constructor, takes the whole value elim_var_analysis-
     * procedural analysis, the index of the function i from the
     * analysis and the compilation options.
     * @param analysisFunction IntraproceduralValueAnalysis for the function being generated
     * @param functionQuery Function query object, allows to query elim_var_analysis procedural information.
     * @param opts Command-line options to apply optimizations
     */
    public FunctionGenerator( IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction,
                             InterproceduralFunctionQuery functionQuery,
                             MatWablyCommandLineOptions opts){
        this.opts = opts;
        this.analysisFunction = analysisFunction;
        this.matlabFunction = analysisFunction.getTree();
        this.interproceduralFunctionQuery = functionQuery;
        this.output_parameters = new List<>();
        this.parameters = new List<>();
        this.locals = new List<>();
        this.functionAnalyses = new MatWablyFunctionInformation(analysisFunction.getTree(),
                analysisFunction, functionQuery, opts);
    }
    public String genFunctionName() {
        TIRFunction func = analysisFunction.getTree();
        Args<AggrValue<BasicMatrixValue>> args =  analysisFunction.getArgs();
        StringBuilder suffix = new StringBuilder(func.getName().getID()+"_");
        for (Object arg : args) {
            BasicMatrixValue bmv = (BasicMatrixValue) arg;
            if (bmv.hasShape() && bmv.getShape().isScalar())
            {
                suffix.append("S");
            }else{
                suffix.append("M");
            }
        }
        return suffix.toString();
    }
    public void runAnalyses(boolean returnAdded){

        // Builds the hooks to place before, inBetween, and afterStmt
        // Add copy of parameters
        Map<TIRStatementList, Set<String>> writtenParams = ParameterCopyAnalysis.apply(matlabFunction);
        ParameterCopyTransformer.apply(matlabFunction, writtenParams);
        // Perform copy insertion
        if(!opts.omit_copy_insertion) {
            performCopyInsertion();
        }

        // Initialize hook map
        FunctionStmtHookMap hookMap = FunctionStmtHookMap.
                initializeFunctionHookMap(matlabFunction);
        functionAnalyses.setFunctionHookMap(hookMap);

        // Run reaching definition and add it to set of analysis
        ReachingDefinitions defs = new ReachingDefinitions(this.matlabFunction);
        defs.analyze();
        this.udChain = defs.getUseDefDefUseChain();

        // Requires chain for
        this.valueAnalysisUtil = new ValueAnalysisUtil(this.analysisFunction,
                this.udChain, returnAdded);

        this.functionAnalyses.setValueAnalysisUtil(valueAnalysisUtil);
        this.functionAnalyses.setReachingDefinitions(defs);



        this.logicalVariableUtil = new LogicalVariableUtil(interproceduralFunctionQuery, defs);
        this.functionAnalyses.setLogicalVariableUtil(this.logicalVariableUtil);

        // Ambiguous variable analysis
        AmbiguousVariableAnalysis amb_var = new AmbiguousVariableAnalysis(this.matlabFunction,
                valueAnalysisUtil);
        AmbiguousVariableUtil amb_var_util = new AmbiguousVariableUtil(amb_var);
        this.functionAnalyses.setAmbigousVariableUtil(amb_var_util);
    
        // Perform analysis
        // Run variable elimination analysis
        if(!opts.skip_variable_elimination){
            this.elim_var_analysis = new TreeExpressionBuilderAnalysis(
                    this.analysisFunction.getTree(),defs,
                    interproceduralFunctionQuery,
                    valueAnalysisUtil,
                    logicalVariableUtil);
            this.elim_var_analysis.analyze();
            this.functionAnalyses.setTreeExpressionBuilderAnalysis(this.elim_var_analysis);
            this.expressionGenerator = new ExpressionGenerator(
                    this.valueAnalysisUtil,
                    amb_var_util,
                    this.logicalVariableUtil,
                    this.elim_var_analysis);
            functionAnalyses.setExpressionGenerator(this.expressionGenerator);
        }else{
            this.expressionGenerator = new ExpressionGenerator(
                    this.valueAnalysisUtil,
                    amb_var_util,
                    this.logicalVariableUtil);
        }
        functionAnalyses.setExpressionGenerator(this.expressionGenerator);

        // Set Expression Generator



        // Perform call stmt analysis
        this.builtin_analysis = new MatWablyBuiltinAnalysis(this.functionAnalyses);
        expressionGenerator.setBuiltinAnalysis(this.builtin_analysis);
        this.builtin_analysis.analyze();
        functionAnalyses.setBuiltinAnalysis(this.builtin_analysis);




        if(!opts.disallow_free){
            if(!opts.gc_dynamic){
                HybridRCGarbageCollectionAnalysis gcA =
                        new HybridRCGarbageCollectionAnalysis(this.analysisFunction.getTree(),
                        valueAnalysisUtil,interproceduralFunctionQuery, opts);
                gcA.analyze();
                hookMap.addHookMap(HybridGCCallInsertionMap.
                        generateInstructions(matlabFunction, gcA, udChain));
            }else{
                DynamicRCGarbageCollection dynGC = new
                        DynamicRCGarbageCollection(matlabFunction,
                        valueAnalysisUtil, interproceduralFunctionQuery, defs);
                dynGC.analyze();
                hookMap.addHookMap(DynamicGCCallInsertion.
                        generateInstructions(matlabFunction, dynGC,
                                valueAnalysisUtil));
            }
        }
    }
    /**
     * Node adds a return stmt as the last stmt, only if the
     * last statement is not already a return.
     * @param tirFunction TameIR Function node to add return to.
     */
    private boolean checkAndAddReturnStmt(TIRFunction tirFunction) {
        TIRStatementList tirStmts = tirFunction.getStmtList();
        if (tirStmts.getChild(tirStmts.getNumChild()-1).getClass()
                != TIRReturnStmt.class) {
            TIRStatementList stmts = tirFunction.getStmtList();
            TIRReturnStmt ret = new TIRReturnStmt();
            stmts.add((Stmt) ret);
            tirFunction.setStmtList(stmts);
            return true;
        }
        return false;
    }
    /**
     * Function generator method, takes a tamer function as input
     * and generates a WebAssembly function
     */
    public Function genFunction(){
        // Adds return statement as the last stmt of
        boolean returnAdded = checkAndAddReturnStmt(matlabFunction);
        runAnalyses(returnAdded);

        // Setting function name and signature setting output_parameters
        Type funcType = genSignature();
        String function_name = genFunctionName();
        // Setting locals
        Locals localsAnalysis = new Locals();
        local_map = localsAnalysis.apply(matlabFunction,
                functionAnalyses.getValueAnalysisUtil(),
                functionAnalyses.getLogicalVariableUtil(),
                opts.disallow_logicals);
        locals.addAll(local_map.values());
        // Set scrip register
        TypeUse scrapLocal = Util.genI32TypeUse();
        locals.add(scrapLocal);
        // Set scrip register
        TypeUse scrapLocalF64 = Util.genF64TypeUse();
        locals.add(scrapLocalF64);
        this.i32_scrap_local = scrapLocal.getIdentifier().getName();
        this.f64_scrap_local = scrapLocalF64.getIdentifier().getName();
        functionAnalyses.setScrapLocals(this.i32_scrap_local,
                this.f64_scrap_local);
        // Setting instructions
        List<Instruction> instructions = genStmtList(matlabFunction.getStmtList());

        Expression exp = new Expression(instructions);
        // Setting return function, automatically adds return statements for variables
        return new Function(new Opt<>(new Identifier(function_name)),funcType, locals, exp );
    }

    /**
     * Generates the return instructions for the Matlab function
     * @return Returns Corresponding generated WebAssembly node.
     * @apiNote Does not support Globals.
     */
    private List<Instruction> genReturnStmt(TIRReturnStmt stmt) {
        List<Instruction> instructions = new List<>();
        StmtHook hook =
                this.functionAnalyses.getFunctionHookMap().getHook(stmt);
        if(hook.hasInBetweenStmtInstructions())
            instructions.addAll(hook.getInBetweenStmtInstructions());

        int size = this.output_parameters.getNumChild();
        if(size == 1){
            // If its only one, suffices to
            TypeUse typeUse = this.output_parameters.getChild(0);
            instructions.add(new GetLocal(new Idx(new Opt<>(typeUse.getIdentifier()),0 )));
        }else if(size > 1){
            // Check
            int i = 0;
            String nameInputVec;
            // Determines if all the values to be returned are f64 scalars, in this case, it returns an array of doubles
            // with all the appropriate return variables
            if(returnsScalarVector()){

                instructions.addAll(MachArrayIndexing.createF64Vector(
                        this.output_parameters.getNumChild()));
                nameInputVec = Util.genTypedLocalI32();
                locals.add(new TypeUse(nameInputVec, new I32()));
                // Add statement name
                instructions.add(new SetLocal(new Idx(nameInputVec)));
                for (TypeUse argTypeUse : this.output_parameters) {
                    instructions.add(new GetLocal(new Idx(nameInputVec)));
                    instructions.add(new ConstLiteral(new I32(), i));
                    instructions.add(new GetLocal(new Idx(argTypeUse.getIdentifier().getName())));
                    instructions.add(new Call(new Idx(new Opt<>(new Identifier("set_array_index_f64_no_check")),0)));
                    i++;
                }

            }else{
                // Otherwise, it boxes scalar values
                instructions.addAll(MachArrayIndexing.createCellVector(
                        this.output_parameters.getNumChild()));

                nameInputVec = Util.genTypedLocalI32();
                locals.add(new TypeUse(nameInputVec, new I32()));
                // Add statement to set the vector
                instructions.add(new SetLocal(new Idx(nameInputVec)));
                for (TypeUse argTypeUse : this.output_parameters) {
                    instructions.add(new GetLocal(new Idx(nameInputVec)));
                    instructions.add(new ConstLiteral(new I32(), i));
                    instructions.add(new GetLocal(new Idx(argTypeUse.getIdentifier().getName())));
                    if(argTypeUse.getType().isF64()){// If its scalar
                        instructions.add(new Call(new Idx(new Opt<>(new Identifier("convert_scalar_to_mxarray")),1)));
                    }
                    instructions.add(new Call(new Idx(new Opt<>(new Identifier("set_array_index_i32_no_check")),0)));
                    i++;

                }
            }
            instructions.add(new GetLocal(new Idx(new Opt<>(new Identifier(nameInputVec)),0)));
        }
        // Option to print memory information
        if(opts.print_memory_information && interproceduralFunctionQuery.
                isMainFunction(matlabFunction)){
            instructions.add(
                    new Call(new Idx("gcPrintMemoryUsageInformation")));
        }
        instructions.add(new Return());
        return instructions;
    }

    /**
     * Helper method to find out whether the function returns a scalar array
     * @return Boolean value
     */
    private boolean returnsScalarVector(){
        // Check if all the return values are f64
        for(TypeUse typeUse: this.output_parameters)
            if(!typeUse.getType().isF64()) return false;
        return this.output_parameters.getNumChild() > 1;
    }

    /**
     * Generates Matlab TameIR statement list
     * @param stmtList TIR statement list
     * @return Returns generated wasm instructions for the statement list
     */
    private List<Instruction> genStmtList(TIRStatementList stmtList) {
        List<Instruction> instructionList = new List<>();
        for(ast.Stmt stmt: stmtList){
            locals.addAll(this.functionAnalyses
                    .getFunctionHookMap().getHook(stmt)
                    .getLocalList());
            instructionList.addAll(this.functionAnalyses
                    .getFunctionHookMap().getHook(stmt)
                    .getBeforeStmtInstructions());
            if(shouldGenerateStmt(stmt)) {
                instructionList.addAll(genStmt(stmt));
            }
            instructionList.addAll(this.functionAnalyses
                    .getFunctionHookMap().getHook(stmt)
                    .getAfterStmtInstructions());
        }
        return instructionList;
    }

    /**
     * Returns whether to generateInstructions the stmt. A statement is generated if either
     * the {@link TreeExpressionBuilderAnalysis} optimization is not performed.
     * Or if it is performed but the analysis did not flag the statement as irrelevant.
     * @param stmt Stmt to test whether to generateInstructions by the compiler
     * @return Boolean indicating whether to skip generation of stmt
     */
    private boolean shouldGenerateStmt(Stmt stmt) {
        return opts.skip_variable_elimination || !elim_var_analysis.isStmtRedundant(stmt);
    }

    /**
     * Generates the particular statement
     * @param tirStmt TIR statement node
     * @return Returns list of instructions for the particular node
     */
    private List<Instruction> genStmt(Stmt tirStmt) {
        if (tirStmt instanceof TIRAssignLiteralStmt) {
            return genAssignLiteralStmt((TIRAssignLiteralStmt) tirStmt);
        } else if (tirStmt instanceof TIRCallStmt) {
           return genCallStmt((TIRCallStmt)tirStmt);
        }else if(tirStmt instanceof MJCopyStmt){
            return genMJCopyStmt((TIRCopyStmt) tirStmt);
        }else if (tirStmt instanceof TIRCopyStmt) {
            return genCopyStmt((TIRCopyStmt) tirStmt);
        } else if(tirStmt instanceof TIRArrayGetStmt){
            return genGetArrayStmt((TIRArrayGetStmt)tirStmt);
        }else if(tirStmt instanceof TIRArraySetStmt){
            return genSetArrayStmt((TIRArraySetStmt) tirStmt);
        }else if(tirStmt instanceof TIRForStmt){
            return genForStmt((TIRForStmt) tirStmt);
        }else if(tirStmt instanceof TIRContinueStmt){
            return genContinueStmt((TIRContinueStmt)tirStmt);
        }else if(tirStmt instanceof TIRWhileStmt){
            return genWhileStmt((TIRWhileStmt) tirStmt);
        }else if(tirStmt instanceof TIRBreakStmt){
            return genBreakStmt((TIRBreakStmt) tirStmt);
        }else if(tirStmt instanceof TIRIfStmt) {
            return genIfStmt((TIRIfStmt) tirStmt);
        }else if(tirStmt instanceof TIRReturnStmt){
            // Setting return function, automatically adds return statements for variables
            return genReturnStmt((TIRReturnStmt) tirStmt);
        }else if(tirStmt instanceof TIRCommentStmt){
            List<Instruction> res = new List<>();
            StmtHook gcInst = this.functionAnalyses
                    .getFunctionHookMap().getHook(tirStmt);
            if(gcInst.hasInBetweenStmtInstructions()) {
                res.addAll(gcInst.getInBetweenStmtInstructions());
            }
            return res;
        }
        throw new UnsupportedOperationException(
                String.format("Stmt node not supported. %d:%d: [%s] [%s]",
                        tirStmt.getStartLine(), tirStmt.getStartColumn(),
                        tirStmt.getPrettyPrinted(), tirStmt.getClass().getName())
        );
    }

    /**
     * Generates instructions for the TameIR TIRCallStmt node
     *  using the built-in framework
     * {@link MatWablyBuiltinGenerator}
     * @param tirStmt TameIR node
     */
    private List<Instruction> genCallStmt(TIRCallStmt tirStmt) {
        List<Instruction> res = new List<>();
        StmtHook gcInst = this.functionAnalyses
                .getFunctionHookMap().getHook(tirStmt);

        MatWablyBuiltinGenerator generator = builtin_analysis.getGenerator(tirStmt);
        MatWablyBuiltinGeneratorResult  resultGenerator =
                generator.getGeneratedExpression();
        locals.addAll(resultGenerator.getLocals());
        // Add the alloc instructions
        boolean allocLoopOpt = opts.opt_loop_alloc && !loopStack.empty();
        if(allocLoopOpt){
            LoopMetaInformation loopInfo = loopStack.peek();
            loopInfo.addInstructionsInitialization(
                    resultGenerator.getAllocInstructions());
            res.addAll(resultGenerator.getInstructions());
            loopInfo.addInstructionsPostLoop(resultGenerator.
                    getFreeingInstructions());
        }else{
            res.addAll(resultGenerator.getAllocInstructions());
            res.addAll(resultGenerator.getInstructions());
        }

        // GC calls

        if( gcInst.hasInBetweenStmtInstructions() && !generator.doesNotGenerateInstructions()){
            res.addAll(produceInBetweenExpressionGCIntructions(gcInst,tirStmt.getTargets().size() > 0,tirStmt.getTargets().size()>1 ||
                    (tirStmt.getTargets().size() == 1 && !valueAnalysisUtil.isScalar(tirStmt.getTargets().getNameExpresion(0),
                            tirStmt, false))));
        }

        MatWablyBuiltinGeneratorResult targetGenerator = generator.getGeneratedSetToTarget();
        locals.addAll(targetGenerator.getLocals());
        res.addAll(targetGenerator.getInstructions());
        if(!allocLoopOpt) res.addAll(resultGenerator.
                getFreeingInstructions());

        return res;
    }

    /**
     * Helper method to generate instructions in between gc instructions
     * @param gcInst StmtHook
     * @param returnsATarget Whether the function returns a target
     * @param arrayTarget Whether the return value is an array target
     * @return
     */
    private List<Instruction> produceInBetweenExpressionGCIntructions(StmtHook gcInst,boolean returnsATarget, boolean arrayTarget) {
        List<Instruction> res = new List<>();
        if( returnsATarget ){
            if(arrayTarget){
                res.add(new SetLocal(new Idx(
                        this.i32_scrap_local)));
                res.addAll(gcInst.
                        getInBetweenStmtInstructions());
                res.add(
                        new GetLocal(new Idx(
                                this.i32_scrap_local)));
            }else{
                res.add(new SetLocal(new Idx(
                        this.f64_scrap_local)));
                res.addAll(gcInst.
                        getInBetweenStmtInstructions());
                res.add(
                        new GetLocal(new Idx(
                                this.f64_scrap_local)));
            }

        }else{
            res.addAll(gcInst.
                    getInBetweenStmtInstructions());
        }

        return res;
    }

    /**
     * Cloning node from MJCopyStmt, since we know is a matrix, we may simply clone
     * @param tirStmt input node where they copy happens
     * @return Returns generated list of instructions
     */
    private List<Instruction> genMJCopyStmt(TIRCopyStmt tirStmt) {
        StmtHook gcInst = this.functionAnalyses.getFunctionHookMap().getHook(tirStmt);
        List<Instruction> res = new List<>();
        if(valueAnalysisUtil.isScalar(tirStmt.getSourceName().getID(), tirStmt, false)){
            // Only generateInstructions if scalars are different names, otherwise computation does nothing
            if(!tirStmt.getSourceName().getID().
                    equals(tirStmt.getTargetName().getID())){
                res.addAll(new GetLocal(new Idx(Util.
                        getTypedLocalF64(tirStmt.getSourceName().getID()))));
                res.addAll(new SetLocal(new Idx(valueAnalysisUtil.genTypedName(tirStmt.getTargetName().getID()
                        ,tirStmt,false))));
            }
        }else{
            res.addAll(expressionGenerator.genNameExpr((NameExpr)tirStmt.getRHS(), tirStmt));
            res.addAll(new Call(new Idx(new Opt<>(new Identifier("clone")), -1)));
            if(gcInst.hasInBetweenStmtInstructions()){
               res.addAll(produceInBetweenExpressionGCIntructions(gcInst,true, true));
            }
            res.addAll(new SetLocal(new Idx(valueAnalysisUtil.genTypedName(tirStmt.getTargetName().getID()
                    ,tirStmt,false))));
        }
        return res;
    }

    /**
     * Generates instructions for the TameIR TIRWhileStmt node
     * @param tirStmt TameIR node
     * @return generated instructions for TameIR node
     */
    private List<Instruction> genCondition(Stmt tirStmt, Name name){
        List<Instruction> res = new List<>();
        // Condition
        if(valueAnalysisUtil.isScalar(name.getID(), tirStmt, true)){
            // Check if is logical here
            res.addAll(expressionGenerator.genName(name, tirStmt));
            if(opts.disallow_logicals
                    || ! logicalVariableUtil.isUseLogical(name)){
                res.add(new ConstLiteral(new F64(),0));
                res.add(new Ne(new F64()));
            }
        }else{
            res.addAll(expressionGenerator.genName(name, tirStmt));
            res.add(new Call(new Idx("all_nonzero_reduction")));
        }
        return res;
    }
    /**
     * Generates TIRIfStmt node
     * @param tirStmt TIRIfstmt node
     * @return Returns list of instructions for if statement.
     */
    private List<Instruction> genIfStmt(TIRIfStmt tirStmt) {
        List<Instruction> res = new List<>();
        // Generate condition
        res.addAll(genCondition(tirStmt, tirStmt.getConditionVarName()));

        // Initialize
        If ifStmt = new If();
        res.add(ifStmt);

        // Generate statements
        ifStmt.setInstructionsIfList(genStmtList(tirStmt.getIfStatements()));
        if(tirStmt.hasElseBlock()){
            ifStmt.setInstructionsElseList(genStmtList(tirStmt.getElseStatements()));
        }
        return res;
    }
    /**
     * Generates instructions for the TameIR TIRContinueStmt node
     * @param tirStmt TameIR node
     * @return generated instructions for TameIR node
     */
    private List<Instruction> genContinueStmt(TIRContinueStmt tirStmt) {
        List<Instruction> res = new List<>();
        StmtHook gcInst = this.functionAnalyses
                .getFunctionHookMap().getHook(tirStmt);
        if(gcInst.hasInBetweenStmtInstructions()) res.addAll(gcInst.getInBetweenStmtInstructions());

        LoopMetaInformation infoLoop = loopStack.peek();
        res.addAll(infoLoop.getConditionCode().treeCopy());
        res.add(new BrIf(infoLoop.getStartLoop()));
        res.add(new Br(infoLoop.getEndLoop()));

        return res;
    }
    /**
     * Generates instructions for the TameIR TIRBreakStmt node
     * @param tirStmt TameIR node
     * @return generated instructions for TameIR node
     */
    private List<Instruction> genBreakStmt(TIRBreakStmt tirStmt) {
        List<Instruction> res = new List<>();
        StmtHook gcInst = this.functionAnalyses
                .getFunctionHookMap().getHook(tirStmt);
        if(gcInst.hasInBetweenStmtInstructions()) res.addAll(gcInst.getInBetweenStmtInstructions());
        return res.add(new Br(loopStack.peek().getEndLoop()));
    }

    /**
     * Generates instructions for the TameIR TIRWhileStmt node
     * @param tirStmt TameIR node
     * @return generated instructions for TameIR node
     */
    private List<Instruction> genWhileStmt(TIRWhileStmt tirStmt) {
        List<Instruction> res = new List<>();

        Idx startLabel = new Idx("loop_"+TempFactory.genFreshTempString()) ;
        Idx endLabel = new Idx("block_"+TempFactory.genFreshTempString());

        // Condition
        List<Instruction> condition = genCondition(tirStmt, tirStmt.getCondition().getName());
        loopStack.push(new LoopMetaInformation(tirStmt, startLabel, endLabel,condition ));
        res.addAll(condition);

        // Generate loop
        If ifStmt = new If();
        res.add(ifStmt);
        Loop loop = new Loop();
        LoopMetaInformation loopInfo = loopStack.peek();
        // Optimization for loops
        ifStmt.getInstructionsIfList().addAll(loopInfo.
                getInstructionsInitialization());
        ifStmt.addInstructionsIf(loop);
        ifStmt.getInstructionsIfList().addAll(loopInfo.
                getInstructionsPostLoop());
        ifStmt.setLabel(endLabel);
        loop.setLabel(startLabel);

        // Loop Generation
        loop.getInstructionList()
                .addAll(genStmtList(tirStmt.getStatements()));
        loop.getInstructionList().addAll(condition);
        loop.addInstruction(new BrIf(startLabel));
        loopStack.pop();
        return res;
    }

    /**
     * // TODO This function is right when for i=a:b:c; end; and i=[], for now, since handling this correctly significant worsens performance, we do not use it.
     * @param tirStmt For loop to generateInstructions
     * @return List of instructions generated for a non-moving for-loop.
     */
    private List<Instruction> genEmptyForLoop(TIRForStmt tirStmt){
        List<Instruction> res = MachArrayIndexing.createVector(0, false, true);
        res.add(new SetLocal(new Idx(valueAnalysisUtil.genTypedName(tirStmt.getLoopVarName().getID(), tirStmt, false))));
        return res;
    }
    /**
     * Generation instructions for the TIRForStmt
     * @param tirStmt TIRForStmt
     * @return Returns list of instructions for generated loop
     */
    private List<Instruction> genForStmt(TIRForStmt tirStmt) {
        List<Instruction> res =new List<>();
        res.addAll(this.functionAnalyses.getFunctionHookMap()
                .getHook(tirStmt).getBeforeStmtInstructions());

        LoopDirection direction = LoopMetaInformation.getLoopDirection(tirStmt,
                valueAnalysisUtil);
        // If the boundaries do not enter the loop, skip the loop creation entirely

        if(direction == LoopDirection.Empty) return res;
//        Instruction increment;
        if(direction == LoopDirection.Ascending||direction == LoopDirection.Descending) {
            // We need to check if this variables are eliminated by expression eliminator analysis
            // If they are, we create new ones for the loop variables
            String typedLow = Util.getTypedLocalF64(tirStmt.getLowerName().getID());
            String typedHigh = Util.getTypedLocalF64(tirStmt.getUpperName().getID());
            String typedInc = (tirStmt.hasIncr())?Util.getTypedLocalF64(tirStmt.getIncName().getID()):null;
            if(!opts.skip_variable_elimination) {
                if(this.elim_var_analysis.isVariableEliminated(tirStmt.getLowerName())){
                    typedLow = Util.genTypedLocalF64();
                    locals.add(Ast.genF64TypeUse(typedLow));
                    res.addAll(expressionGenerator.genName(tirStmt.getLowerName(),tirStmt));
                    res.add(new SetLocal(new Idx(typedLow)));
                }
                if(this.elim_var_analysis.isVariableEliminated(tirStmt.getUpperName())){
                    typedHigh = Util.genTypedLocalF64();
                    locals.add(Ast.genF64TypeUse(typedHigh));
                    res.addAll(expressionGenerator.genName(tirStmt.getUpperName(),tirStmt));
                    res.add(new SetLocal(new Idx(typedHigh)));
                }
                if(tirStmt.hasIncr() &&
                        this.elim_var_analysis.isVariableEliminated(tirStmt.getIncName())){
                    typedInc = Util.genTypedLocalF64();
                    locals.add(Ast.genF64TypeUse(typedInc));
                    res.addAll(expressionGenerator.genName(tirStmt.getIncName(),tirStmt));
                    res.add(new SetLocal(new Idx(typedInc)));
                }
            }
            res.addAll(genStaticLoop(tirStmt, direction,
                    typedLow,
                    typedHigh,
                    typedInc));
            return res;
        }else if(direction == LoopDirection.NonMoving){
            res.addAll(genNonMovingForloop(tirStmt));
        }else if(direction == LoopDirection.Unknown){
           res.addAll(genDynamicForLoop(tirStmt));
        }
        res.addAll(this.functionAnalyses.getFunctionHookMap()
                .getHook(tirStmt).getAfterStmtInstructions());
        return res;
    }
    /**
     * Generates loop that is completely dynamic
     * @param tirStmt TIRForStmt
     * @return Returns list of instructions for generated loop
     */
    @SuppressWarnings("unchecked")
    private List<Instruction> genDynamicForLoop(TIRForStmt tirStmt) {
        List<Instruction> res = new List<>();
        String isEmptyFlag = Util.genTypedLocalI32();
        locals.add(new TypeUse(isEmptyFlag, new I32()));
        // Dynamically generateInstructions increase, decrease empty or not moving
        // Add checks for empty arrays, if arrays.
        String typedLow;
        typedLow = valueAnalysisUtil.genTypedName(tirStmt.getLowerName().getID(),tirStmt,true);
        if(!opts.skip_variable_elimination &&
                this.elim_var_analysis.isVariableEliminated(tirStmt.getLowerName())) {
            res.addAll(expressionGenerator.genName(tirStmt.getLowerName(), tirStmt));
            if(valueAnalysisUtil.isScalar(tirStmt.getLowerName().getID(),tirStmt,true)){
                typedLow = Util.genTypedLocalF64();
                locals.add(Ast.genF64TypeUse(typedLow));
            }else{
                typedLow = Util.genTypedLocalI32();
                locals.add(Ast.genI32TypeUse(typedLow));
            }
            res.add(new SetLocal(new Idx(typedLow)));
        }
        if(!valueAnalysisUtil.isScalar(tirStmt.getLowerName().getID(),tirStmt, true)){
            res.addAll(MachArrayIndexing.isEmpty(typedLow));
            List<Instruction> get_first_elem =
                    MachArrayIndexing.getArrayIndexF64CheckBounds(typedLow, 1);
            typedLow = setLoopBoundFromArray(res, isEmptyFlag, get_first_elem);
        }
        String typedHigh = valueAnalysisUtil.genTypedName(tirStmt.getUpperName().getID(),
                tirStmt,true);
        if(!opts.skip_variable_elimination&&this.elim_var_analysis.isVariableEliminated(tirStmt.getUpperName())){
            res.addAll(expressionGenerator.genName(tirStmt.getUpperName(),tirStmt));
            if(valueAnalysisUtil.isScalar(tirStmt.getUpperName().getID(),tirStmt,true)){
                typedHigh = Util.genTypedLocalF64();
                locals.add(Ast.genF64TypeUse(typedHigh));
            }else{
                typedHigh = Util.genTypedLocalI32();
                locals.add(Ast.genI32TypeUse(typedHigh));
            }
            res.add(new SetLocal(new Idx(typedHigh)));
        }
        if(!valueAnalysisUtil.isScalar(tirStmt.getUpperName().getID(),tirStmt, true)){
            res.addAll(MachArrayIndexing.isEmpty(typedHigh));
            List<Instruction> get_first_elem = MachArrayIndexing.getArrayIndexF64CheckBounds(typedHigh, 1);
            typedHigh = setLoopBoundFromArray(res, isEmptyFlag, get_first_elem);
        }
        String typedInc = "";

        if(tirStmt.hasIncr()){
            typedInc = valueAnalysisUtil.genTypedName(tirStmt.getIncName().getID(),tirStmt,true);
            if(tirStmt.hasIncr() && !opts.skip_variable_elimination &&
                    this.elim_var_analysis.isVariableEliminated(tirStmt.getIncName())){
                if(valueAnalysisUtil.isScalar(tirStmt.getIncName().getID(),tirStmt,true)){
                    typedInc = Util.genTypedLocalF64();
                    locals.add(Ast.genF64TypeUse(typedInc));
                }else{
                    typedInc = Util.genTypedLocalI32();
                    locals.add(Ast.genF64TypeUse(typedInc));
                }
                res.addAll(expressionGenerator.genName(tirStmt.getIncName(),tirStmt));
                res.add(new SetLocal(new Idx(typedInc)));
            }
            if(!valueAnalysisUtil.isScalar(tirStmt.getIncName().getID(),tirStmt, true)){
                res.addAll(MachArrayIndexing.isEmpty(typedInc));
                List<Instruction> get_first_elem = MachArrayIndexing.getArrayIndexF64CheckBounds(typedInc, 1);
                typedInc = setLoopBoundFromArray(res, isEmptyFlag, get_first_elem);
            }
        }
        // Add if statement, if we enter the if statement the loop executes, otherwise it does not
        // execute

        List<Instruction> condSwitch = new List<>();
        if(tirStmt.hasIncr()){
            condSwitch.addAll(
                    new GetLocal(new Idx(typedLow)),
                    new GetLocal(new Idx(typedInc)),
                    new GetLocal(new Idx(typedHigh)),
                    new Call(new Idx("dynamic_loop_three")));
        }else{
            condSwitch.addAll(
                    new GetLocal(new Idx(typedLow)),
                    new GetLocal(new Idx(typedHigh)),
                    new Call(new Idx("dynamic_loop_two")));
        }
        SwitchStatement swit = new SwitchStatement(
                condSwitch,
                genNonMovingForloop(tirStmt),
                genStaticLoop(tirStmt,LoopDirection.Descending, typedLow, typedHigh, typedInc),
                genStaticLoop(tirStmt,LoopDirection.Ascending, typedLow, typedHigh, typedInc)
        );
        res.addAll(
                new GetLocal(new Idx(isEmptyFlag)),
                new Eqz(new I32()));
        If ifStmt = new If();
        ifStmt.setInstructionsIfList(swit.generate());
//        ifStmt.getInstructionsElseList().addAll(genEmptyForLoop(tirStmt));
        res.add(ifStmt);
        return res;
    }

    private String setLoopBoundFromArray(List<Instruction> res, String isEmptyFlag, List<Instruction> get_first_elem) {
        String newLoopVar;
        newLoopVar = Util.genTypedLocalF64();
        get_first_elem.add(new SetLocal(new Idx(newLoopVar)));
        res.add(new If(new Opt<>(),new Opt<>(),
                new List<>(new ConstLiteral(new I32(), 1),
                        new SetLocal(new Idx(isEmptyFlag)))
                ,get_first_elem));
        locals.add(new TypeUse(newLoopVar, new F64()));
        return newLoopVar;
    }

    /**
     * Generation of non-empty loop
     * @param tirStmt TIRForStmt
     * @return Returns list of instructions for generated loop
     */
    private List<Instruction> genNonMovingForloop(TIRForStmt tirStmt) {
        Block block = new Block();
        Idx endLabel = new Idx("block_"+TempFactory.genFreshTempString()) ;
        loopStack.push(new LoopMetaInformation(tirStmt, endLabel, endLabel,null));
        block.setLabel(endLabel);

        List<Instruction> res = new List<>();
        // Set the initial value of variable, Add instructions with no loop
        res.addAll(expressionGenerator.genName(tirStmt.getLowerName(),tirStmt));
        if(!valueAnalysisUtil.isScalar(tirStmt.getLowerName().getID(), tirStmt,true)){
            res.addAll(MachArrayIndexing.getArrayIndexF64(0));
        }
        StmtHook gcInst = this.functionAnalyses.
                getFunctionHookMap().getHook(tirStmt);
        if(gcInst.hasInBetweenStmtInstructions())
            res.addAll(produceInBetweenExpressionGCIntructions(gcInst, true,false));
        res.add(new SetLocal(new Idx(valueAnalysisUtil.
                genTypedName(tirStmt.getLoopVarName().getID(),
                        tirStmt, false))));
        // Add instructions
        LoopMetaInformation loopInfo = loopStack.peek();
        List<Instruction> stmtList = genStmtList(tirStmt.getStatements());
        res.addAll(loopInfo.getInstructionsInitialization());
        res.addAll(stmtList);
        res.addAll(loopInfo.getInstructionsPostLoop());
        block.setInstructionList(res);

        loopStack.pop();
        return new List<>(block);
    }

    /**
     * For stmt translation
     * for i=1:1:10 or i=10:-2:1
     *  %STATEMENTS
     * end
     *  =====>
     * (set_local $i (get_local $low))
     * f64.le/ge (get_local $low)(get_local $high) ;; condition
     * if $l0
     *  loop $l1
     *      ;; STATEMENTS
     *      (set_local $i (f64.add/sub
     *          (get_local $i)(get_local $inc)))
     *      (br_if $l1 (f64.le/ge (get_local $i)(get_local $high) ;; condition)
     *  end
     * end
     * @param tirStmt Loop statement, TIRWhileStmt/TIRForStmt
     * @param direction  Direction for loop {@link LoopDirection}
     * @param typedLow Lower bound variable
     * @param typedHigh Upper bound variable
     * @param incrementVar Loop variable
     * @return Returns the instructions containing the loop
     */
    private List<Instruction> genStaticLoop(TIRForStmt tirStmt, LoopDirection direction,
                                            String typedLow, String typedHigh, String incrementVar) {


        String typedLoopVar = valueAnalysisUtil.genTypedName(tirStmt.getLoopVarName().getID(),
                                tirStmt,
                                false);

        Idx startLabel = new Idx("loop_"+TempFactory.genFreshTempString());
        Idx endLabel = new Idx("block_"+TempFactory.genFreshTempString());

        //Generate Condition (f64.le/ge (get_local $loop_var)(get_local $high))
        List<Instruction> condition = new List<>(new GetLocal(new Idx(typedLoopVar)));
        condition.addAll(new GetLocal(new Idx(typedHigh)));
        condition.addAll((direction==LoopDirection.Ascending)?new Le(new F64(),
                false):new Ge(new F64(),
                false));

        // Loop Increment
        List<Instruction> loopIncrement = new List<>((tirStmt.hasIncr())?
                new GetLocal(new Idx(incrementVar)):
                new ConstLiteral(new F64(),1));
        loopIncrement.addAll(
                new GetLocal(new Idx(typedLoopVar)),
                new Add(new F64()),
                new SetLocal(new Idx(typedLoopVar)));

        List<Instruction> endLoopInstr = new List<>();
        endLoopInstr.addAll(loopIncrement);
        endLoopInstr.addAll(condition);
        loopStack.push(new LoopMetaInformation(tirStmt, startLabel, endLabel, endLoopInstr));

        // Loop init
        List<Instruction> res = new List<>(new GetLocal(new Idx(typedLow)));
        StmtHook gcInst = this.functionAnalyses.
                getFunctionHookMap().getHook(tirStmt);
        if(gcInst.hasInBetweenStmtInstructions()) {
            res.addAll(gcInst.getInBetweenStmtInstructions());
        }
        res.add(new SetLocal(new Idx(typedLoopVar)));
        res.addAll(condition);

        // If statement
        If ifStmt = new If();
        res.add(ifStmt);
        ifStmt.setLabel(endLabel);

        Loop loop = new Loop();
        loop.setLabel(startLabel);
        List<Instruction> listInstLoop = loop.getInstructionList();
        listInstLoop.addAll(genStmtList(tirStmt.getStatements()));

        LoopMetaInformation loopInfo = loopStack.peek();
        listInstLoop.addAll(endLoopInstr);
        listInstLoop.add(new BrIf(startLabel));
        ifStmt.getInstructionsIfList()
                .addAll(loopInfo.getInstructionsInitialization());
        ifStmt.addInstructionsIf(loop);
        ifStmt.getInstructionsIfList()
                .addAll(loopInfo.getInstructionsPostLoop());
        loopStack.pop();
        return res;

    }



    private List<Instruction> genSetArrayStmt(TIRArraySetStmt tirStmt){
        List<Instruction> res = new List<>();

        MatWablyBuiltinGenerator generator = builtin_analysis.
                getGenerator(tirStmt);

        MatWablyBuiltinGeneratorResult  resultGenerator =
                generator.getGeneratedExpression();
        locals.addAll(resultGenerator.getLocals());
        // Add the alloc instructions
        res.addAll(resultGenerator.getAllocInstructions());
        res.addAll(resultGenerator.getInstructions());
        // GC calls
        res.addAll(resultGenerator.
                getFreeingInstructions());
        return res;
    }

    /**
     * Generates the copy insertion in MatJuice.
     */
    private void performCopyInsertion(){
        if(!opts.omit_copy_insertion){
            PointsToAnalysis pta;
            do {
                pta = new PointsToAnalysis(this.analysisFunction.getTree());
                this.analysisFunction.getTree().tirAnalyze(pta);
            } while (CopyInsertion.apply(this.analysisFunction.getTree(), pta));
        }

    }

    /**
     * Method returns the generated instructions for a TIRArrayGetStmt in
     * the compiler.
     * @param tirStmt Tamer IR statement
     * @return List of instructions for the TIRArrayGetStmt in WebAssembly
     */
    private List<Instruction> genGetArrayStmt(TIRArrayGetStmt tirStmt) {
        List<Instruction> res = new List<>();
        StmtHook gcInst = this.functionAnalyses
                .getFunctionHookMap().getHook(tirStmt);

        MatWablyBuiltinGenerator generator = builtin_analysis.getGenerator(tirStmt);
        MatWablyBuiltinGeneratorResult  resultGenerator =
                generator.getGeneratedExpression();
        locals.addAll(resultGenerator.getLocals());
        // Add the alloc instructions
        res.addAll(resultGenerator.getAllocInstructions());
        res.addAll(resultGenerator.getInstructions());
        // GC calls
        if( gcInst.hasInBetweenStmtInstructions() && !generator.doesNotGenerateInstructions()){
            res.addAll(produceInBetweenExpressionGCIntructions(gcInst,tirStmt.getTargets().size() !=0,
                    tirStmt.getTargets().size()>1 ||
                    (tirStmt.getTargets().size() == 1 && !valueAnalysisUtil.isScalar(tirStmt.getTargets().getNameExpresion(0),
                            tirStmt, false))));
        }
        MatWablyBuiltinGeneratorResult targetGenerator =
                generator.getGeneratedSetToTarget();
        locals.addAll(targetGenerator.getLocals());
        res.addAll(targetGenerator.getInstructions());
        res.addAll(resultGenerator.
                getFreeingInstructions());

        return res;
    }

    /**
     * Copy statement generator. Strategy:
     * If is a scalar, \wasm passes registers by value, as any low-level IR would
     * If it is a matrix. We find whether we are using omit_copy_insertion option,
     * this option treats variables as references and adds copies to abide to Matlab
     * semantics. If it is on, we simply copy reference. Otherwise we clone the array.
     * @param tirStmt Statement to translate
     * @return Instructions for the translated statement.
     */
    private List<Instruction> genCopyStmt(TIRCopyStmt tirStmt) {
        List<Instruction> instructions = new List<>();
        StmtHook gcInst = this.functionAnalyses.getFunctionHookMap()
                .getHook(tirStmt);
        String source = tirStmt.getSourceName().getID();
        String target = tirStmt.getTargetName().getID();
        instructions.addAll(expressionGenerator.genName(tirStmt.getSourceName(),tirStmt));



        if(valueAnalysisUtil.isScalar(source, tirStmt,true)){
            if(!target.equals(source))
                instructions.add(new SetLocal( new Idx(valueAnalysisUtil.genTypedName(target, tirStmt,false))));
        }else{
            if(gcInst.hasInBetweenStmtInstructions())
                instructions.addAll(produceInBetweenExpressionGCIntructions(gcInst,true,true));
            if(opts.omit_copy_insertion){
                instructions.add(new Call(new Idx(new Opt<>(new Identifier("clone")), -1)));
                instructions.add(new SetLocal( new Idx(valueAnalysisUtil.genTypedName(target, tirStmt,false))));
            }else{
                instructions.add(new SetLocal( new Idx(valueAnalysisUtil.genTypedName(target, tirStmt,false))));
            }
        }
        return instructions;
    }

    /**
     * Generator for TIRLiteralStmt
     * @param tirStmt TameIR node
     * @return Generated set of instructions.
     */
    private List<Instruction> genAssignLiteralStmt(TIRAssignLiteralStmt tirStmt) {
        List<Instruction> inst = new List<>();
        StmtHook gcInst = this.functionAnalyses
                .getFunctionHookMap().getHook(tirStmt);


        inst.addAll(expressionGenerator.genExpr(tirStmt.getRHS(), tirStmt));

        if(gcInst.hasInBetweenStmtInstructions())
            inst.addAll(produceInBetweenExpressionGCIntructions(gcInst,true,false));

        // Set target
        String newName = valueAnalysisUtil.genTypedName(tirStmt.getLHS().getVarName(),tirStmt, false);
        inst.add(new SetLocal(new Idx(newName)));

        return inst;
    }

    private Type genSignature(){
        TIRFunction func = this.analysisFunction.getTree();
        Args<AggrValue<BasicMatrixValue>> args =  analysisFunction.getArgs();
        List<TypeUse> result = new List<>();
        ast.List<Name> params = func.getInputParams();
        int paramIndex = 0;
        for (Object arg : args) {
            Name param = params.getChild(paramIndex);
            String paramName;
            ValueType type;
            BasicMatrixValue bmv = (BasicMatrixValue) arg;
            if (bmv.hasShape() && bmv.getShape().isScalar())
            {
                type = new F64();
                paramName = param.getID()+"_f64";
            }else{
                type = new I32();
                paramName = param.getID()+"_i32";
            }
            result.add(new TypeUse(new Opt<>(new Identifier(paramName)), type));
            paramIndex++;
        }

        List<ValueType> returnVals = new List<>();
        List<TypeUse> returnIds = new List<>();
        // For return variable it should, if not defined in function, return a null array.
        // When we generateInstructions call then, make this determination again for the output arguments to throw run-time error.

        // TODO(dherre3) Fix this, this is an error only if we query the function for the non-defined return parameter. If we return i.e. [a,b,c] = ret1() and ret1() does not defined b, its an error, instead if [a] = ret1(), this is not an error.
        for(Name name: func.getOutputParamList()){
            if(!valueAnalysisUtil.hasBasicMatrixValue(name.getID(), func, false)){
                throw new Error("Return variable: \""+ name.getID()+"\" is never defined in function context: "
                + func.getName().getID());
            }
            TypeUse typeUse = new TypeUse(valueAnalysisUtil.
                    genTypedName(name.getID(), func, false),
                    (valueAnalysisUtil.isScalar(name.getID(),func, false))?new F64():new F32());
            ValueType valueType = typeUse.getType();
            returnVals.add(valueType);
            returnIds.add(typeUse);
        }

        this.output_parameters = returnIds;
        this.parameters = result;

        // Setting output
        Opt<ReturnType> returnType = (returnVals.getNumChild()>0)?new Opt<>(
                new ReturnType((returnVals.getNumChild()>1)?
                        new List<>(new I32()): returnVals)):new Opt<>();
        return new Type(new Opt<>(), this.parameters,returnType);
    }
}
