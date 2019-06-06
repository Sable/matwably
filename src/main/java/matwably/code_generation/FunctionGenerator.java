package matwably.code_generation;

import ast.ASTNode;
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
import matwably.analysis.memory_management.GCInstructions;
import matwably.analysis.memory_management.dynamic.DynamicGCCallInsertion;
import matwably.analysis.memory_management.dynamic.DynamicRCGarbageCollection;
import matwably.analysis.memory_management.hybrid.HybridGCCallInsertionMap;
import matwably.analysis.memory_management.hybrid.HybridRCGarbageCollectionAnalysis;
import matwably.ast.*;
import matwably.code_generation.builtin.BuiltinGenerator;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.code_generation.wasm.SwitchStatement;
import matwably.util.*;
import natlab.tame.tir.*;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.toolkits.rewrite.TempFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Class generates TIRFunctions given the ValueAnalysis, the index of the function inside the analysis to be generated
 * and the command line options for the generation. i.e. what optimizations to apply
 * TODO: Globals are not supported
 * TODO: TIRReturn statement
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

    /**
     * Returns the ast of the generated function
     * @return Returns the generated WebAssembly function.
     */
    public Function getAst() {
        return function;
    }

    private Function function;

    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction;
    /**
     * @deprecated
     * TODO Get rid of this once we finalize get and set in the compiler.
     */
    private ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis;
    private InterproceduralFunctionQuery interproceduralFunctionQuery;
    private ExpressionGenerator expressionGenerator;
    private MatWablyFunctionInformation functionAnalyses;

    /**
     * Function Generator constructor, takes the whole value elim_var_analysis-
     * procedural analysis, the index of the function i from the
     * analysis and the compilation options.
     * @param analysisFunction IntraproceduralValueAnalysis for the function being generated
     * @param analysis ValueAnalysis with shape information
     * @param functionQuery Function query object, allows to query elim_var_analysis procedural information.
     * @param opts Command-line options to apply optimizations
     */
    public FunctionGenerator(
                             IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction,
                             ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
                             InterproceduralFunctionQuery functionQuery,
                             MatWablyCommandLineOptions opts){
        this.opts = opts;
        this.programAnalysis = analysis;
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
    public void runAnalyses(){

        // Add copy of parameters
        Map<TIRStatementList, Set<String>> writtenParams = ParameterCopyAnalysis.apply(matlabFunction);
        ParameterCopyTransformer.apply(matlabFunction, writtenParams);
        // Perform copy insertion
        if(!opts.omit_copy_insertion) {
            performCopyInsertion();
        }


        // Run reaching definition and add it to set of analysis
        ReachingDefinitions defs = new ReachingDefinitions(this.matlabFunction);
        defs.analyze();
        this.udChain = defs.getUseDefDefUseChain();

        // Requires chain for
        this.valueAnalysisUtil = new ValueAnalysisUtil(this.analysisFunction, this.udChain);
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
                    valueAnalysisUtil);
            this.elim_var_analysis.analyze();
            this.functionAnalyses.setTreeExpressionBuilderAnalysis(this.elim_var_analysis);
        }

        // Set Expression Generator
        this.expressionGenerator = new ExpressionGenerator(
                this.valueAnalysisUtil,
                amb_var_util,
                this.logicalVariableUtil,
                this.elim_var_analysis);
        functionAnalyses.setExpressionGenerator(this.expressionGenerator);


        // Perform call stmt analysis
        this.builtin_analysis = new MatWablyBuiltinAnalysis(this.functionAnalyses);
        expressionGenerator.setBuiltinAnalysis(this.builtin_analysis);
        this.builtin_analysis.analyze();
        functionAnalyses.setBuiltinAnalysis(this.builtin_analysis);




        if(!opts.disallow_free){
            if(!opts.gc_dynamic){
                HybridRCGarbageCollectionAnalysis gcA = new HybridRCGarbageCollectionAnalysis(this.analysisFunction.getTree(),
                        valueAnalysisUtil,interproceduralFunctionQuery, opts);
                gcA.analyze();
                functionAnalyses.setGCInstructionMapping(HybridGCCallInsertionMap.generateInstructions(matlabFunction, gcA));
            }else{
                DynamicRCGarbageCollection dynGC = new DynamicRCGarbageCollection(this.analysisFunction.getTree(),
                        valueAnalysisUtil, interproceduralFunctionQuery);
                dynGC.analyze();
                functionAnalyses.setGCInstructionMapping(DynamicGCCallInsertion.generate(dynGC));
            }
        }
    }

    /**
     * Node adds a return stmt as the last stmt, only if the
     * last statement is not already a return.
     * @param tirFunction TameIR Function node to add return to.
     */
    private static void checkAndAddReturnStmt(TIRFunction tirFunction) {
        TIRStatementList tirStmts = tirFunction.getStmtList();
        if (tirStmts.getChild(tirStmts.getNumChild()-1).getClass() != TIRReturnStmt.class) {
            TIRStatementList stmts = tirFunction.getStmtList();
            TIRStmt ret = new TIRReturnStmt();
            stmts.add(ret);
            tirFunction.setStmtList(stmts);
        }
    }
    /**
     * Function generator method, takes a tamer function as input
     * and generates a WebAssembly function
     */
    public void generate(){
        // Adds return statement as the last stmt of
        checkAndAddReturnStmt(matlabFunction);
        runAnalyses();
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
        this.i32_scrap_local = scrapLocal.getIdentifier().getName();
        // Setting instructions
        List<Instruction> instructions = genStmtList(matlabFunction.getStmtList());

        Expression exp = new Expression(instructions);
        // Setting return function, automatically adds return statements for variables
        this.function =  new Function(new Opt<>(new Identifier(function_name)),funcType, locals, exp );
    }

    /**
     * Generates the return instructions for the Matlab function
     * TODO Add freeing instruction for boxing to the
     * @return Returns Corresponding generated WebAssembly node.
     * @apiNote Does not support Globals.
     */
    private List<Instruction> genReturnStmt() {
        List<Instruction> instructions = new List<>();
//        analysis.getResult()
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

                instructions.addAll(MatWablyArray.createF64Vector(
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
                // Otherwise, it boxes scalar values, inefficient, but that is as good as we can to do statically.
                instructions.addAll(MatWablyArray.createCellVector(
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
            if(shouldGenerateStmt(stmt)) {
                instructionList.addAll(this.functionAnalyses
                        .getGCInstructionMapping().get(stmt)
                        .getBeforeStmtInstructions());
                instructionList.addAll(genStmt(stmt));
                instructionList.addAll(this.functionAnalyses
                        .getGCInstructionMapping().get(stmt)
                        .getAfterStmtInstructions());
            }
        }
        return instructionList;
    }

    /**
     * Returns whether to generate the stmt. A statement is generated if either
     * the {@link TreeExpressionBuilderAnalysis} optimization is not performed.
     * Or if it is performed but the analysis did not flag the statement as irrelevant.
     * @param stmt Stmt to test whether to generate by the compiler
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
            List<Instruction> res = new List<>();
            GCInstructions gcInst = this.functionAnalyses
                    .getGCInstructionMapping().get(tirStmt);

            MatWablyBuiltinGenerator generator = builtin_analysis.getGenerator(tirStmt);
            MatWablyBuiltinGeneratorResult  resultGenerator =
                    generator.getGeneratedExpressionResult();
            // GC calls
            if(gcInst.hasInBetweenStmtInstructions()){
                resultGenerator.addInstructions(produceInBetweenExpressionGCIntructions(gcInst));
            }
            resultGenerator.add(generator.getGeneratedSetToTargetResult());
            locals.addAll(resultGenerator.getLocals());
            res.addAll(resultGenerator.getInstructions());
            return res;
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
            return genReturnStmt();
        }else if(tirStmt instanceof TIRCommentStmt){
            List<Instruction> res = new List<>();
            GCInstructions gcInst = this.functionAnalyses
                    .getGCInstructionMapping().get(tirStmt);
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

    private List<Instruction> produceInBetweenExpressionGCIntructions(GCInstructions gcInst) {
        List<Instruction> res = new List<>();
        res.add(new SetLocal(new Idx(
                this.i32_scrap_local)));
        res.addAll(gcInst.
                getInBetweenStmtInstructions());
        res.add(
                new GetLocal(new Idx(
                        this.i32_scrap_local)));
        return res;
    }

    /**
     * Cloning node from MJCopyStmt, since we know is a matrix, we may simply clone
     * @param tirStmt input node where they copy happens
     * @return Returns generated list of instructions
     */
    private List<Instruction> genMJCopyStmt(TIRCopyStmt tirStmt) {
        GCInstructions gcInst = this.functionAnalyses.getGCInstructionMapping().get(tirStmt);
        List<Instruction> res = new List<>();
        if(valueAnalysisUtil.isScalar(tirStmt.getSourceName().getID(), tirStmt, false)){
            // Only generate if scalars are different names, otherwise computation does nothing
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
               res.addAll(produceInBetweenExpressionGCIntructions(gcInst));
            }
            res.addAll(new SetLocal(new Idx(valueAnalysisUtil.genTypedName(tirStmt.getTargetName().getID()
                    ,tirStmt,false))));
        }
        return res;
    }
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

    private List<Instruction> genContinueStmt(TIRContinueStmt tirStmt) {
        List<Instruction> res = new List<>();
        GCInstructions gcInst = this.functionAnalyses
                .getGCInstructionMapping().get(tirStmt);
        if(gcInst.hasInBetweenStmtInstructions()) res.addAll(gcInst.getInBetweenStmtInstructions());

        LoopMetaInformation infoLoop = loopStack.peek();
        res.addAll(infoLoop.getConditionCode().treeCopy());
        res.add(new BrIf(infoLoop.getStartLoop()));
        res.add(new Br(infoLoop.getEndLoop()));

        return res;
    }

    private List<Instruction> genBreakStmt(TIRBreakStmt tirStmt) {
        List<Instruction> res = new List<>();
        GCInstructions gcInst = this.functionAnalyses
                .getGCInstructionMapping().get(tirStmt);
        if(gcInst.hasInBetweenStmtInstructions()) res.addAll(gcInst.getInBetweenStmtInstructions());
        return res.add(new Br(loopStack.peek().getEndLoop()));
    }
    private List<Instruction> genWhileStmt(TIRWhileStmt tirStmt) {
        List<Instruction> res = new List<>();
        NameExpr expr = tirStmt.getCondition();
        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt,expr.getName().getID() );

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
        ifStmt.addInstructionsIf(loop);
        ifStmt.setLabel(endLabel);
        loop.setLabel(startLabel);
        loop.getInstructionList().addAll(genStmtList(tirStmt.getStatements()));

        // Add condition again
        loop.getInstructionList().addAll(condition);
        loop.addInstruction(new BrIf(startLabel));
        loopStack.pop();
        return res;
    }

    /**
     * // TODO This function is right when for i=a:b:c; end; and i=[], for now, since handling this correctly significant
     * // TODO worsens performance, we do not use it.
     * @param tirStmt For loop to generate
     * @return List of instructions generated for a non-moving for-loop.
     */
    private List<Instruction> genEmptyForLoop(TIRForStmt tirStmt){
        List<Instruction> res = MatWablyArray.createVector(0, false, true);
        res.add(new SetLocal(new Idx(valueAnalysisUtil.genTypedName(tirStmt.getLoopVarName().getID(), tirStmt, false))));
        return res;
    }

    private List<Instruction> genForStmt(TIRForStmt tirStmt) {
        List<Instruction> res =new List<>();
        res.addAll(this.functionAnalyses.getGCInstructionMapping()
                .get(tirStmt).getBeforeStmtInstructions());

        LoopDirection direction = LoopMetaInformation.getLoopDirection(tirStmt,
                functionAnalyses.getFunctionAnalysis());
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
        res.addAll(this.functionAnalyses.getGCInstructionMapping()
                .get(tirStmt).getAfterStmtInstructions());
        return res;
    }

    @SuppressWarnings("unchecked")
    private List<Instruction> genDynamicForLoop(TIRForStmt tirStmt) {
        List<Instruction> res = new List<>();
        String isEmptyFlag = Util.genTypedLocalI32();
        locals.add(new TypeUse(isEmptyFlag, new I32()));
        // Dynamically generate increase, decrease empty or not moving
        // Add checks for empty arrays, if arrays.
        String typedLow;
        typedLow = valueAnalysisUtil.genTypedName(tirStmt.getLowerName().getID(),tirStmt,true);
        if(!opts.skip_variable_elimination && this.elim_var_analysis.isVariableEliminated(tirStmt.getLowerName())) {
            if(valueAnalysisUtil.isScalar(tirStmt.getLowerName().getID(),tirStmt,true)){
                typedLow = Util.genTypedLocalF64();
                locals.add(Ast.genF64TypeUse(typedLow));
            }else{
                typedLow = Util.genTypedLocalI32();
                locals.add(Ast.genF64TypeUse(typedLow));
            }
            res.addAll(expressionGenerator.genName(tirStmt.getLowerName(), tirStmt));
            res.add(new SetLocal(new Idx(typedLow)));
        }
        if(!valueAnalysisUtil.isScalar(tirStmt.getLowerName().getID(),tirStmt, true)){
            res.addAll(MatWablyArray.isEmpty(typedLow));
            List<Instruction> get_first_elem = new List<>();
            get_first_elem.addAll(MatWablyArray.getArrayIndexF64CheckBounds(typedLow, 1));
            typedLow = Util.genTypedLocalF64();
            get_first_elem.add(new SetLocal(new Idx(typedLow)));
            res.add(new If(new Opt<>(),new Opt<>(),
                    new List<>(new ConstLiteral(new I32(), 1), new SetLocal(new Idx(isEmptyFlag)))
                    ,get_first_elem));
            locals.add(new TypeUse(typedLow, new F64()));
        }
        String typedHigh;
        if(!opts.skip_variable_elimination&&this.elim_var_analysis.isVariableEliminated(tirStmt.getUpperName())){
            if(valueAnalysisUtil.isScalar(tirStmt.getUpperName().getID(),tirStmt,true)){
                typedHigh = Util.genTypedLocalF64();
                locals.add(Ast.genF64TypeUse(typedHigh));
            }else{
                typedHigh = Util.genTypedLocalI32();
                locals.add(Ast.genF64TypeUse(typedHigh));
            }
            res.addAll(expressionGenerator.genName(tirStmt.getUpperName(),tirStmt));
            res.add(new SetLocal(new Idx(typedHigh)));
        }
        typedHigh = valueAnalysisUtil.genTypedName(tirStmt.getUpperName().getID(),tirStmt,true);
        if(!valueAnalysisUtil.isScalar(tirStmt.getUpperName().getID(),tirStmt, true)){
            res.addAll(MatWablyArray.isEmpty(typedHigh));
            List<Instruction> get_first_elem = MatWablyArray.getArrayIndexF64CheckBounds(typedHigh, 1);
            typedHigh = Util.genTypedLocalF64();
            get_first_elem.add(new SetLocal(new Idx(typedHigh)));
            res.add(new If(new Opt<>(),new Opt<>(),
                    new List<>(new ConstLiteral(new I32(), 1), new SetLocal(new Idx(isEmptyFlag)))
                    ,get_first_elem));
            locals.add(new TypeUse(typedHigh, new F64()));
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
                res.addAll(MatWablyArray.isEmpty(typedInc));
                List<Instruction> get_first_elem = MatWablyArray.getArrayIndexF64CheckBounds(typedInc, 1);
                typedInc = Util.genTypedLocalF64();
                get_first_elem.add(new SetLocal(new Idx(typedInc)));
                res.add(new If(new Opt<>(),new Opt<>(),
                        new List<>(new ConstLiteral(new I32(), 1), new SetLocal(new Idx(isEmptyFlag)))
                        ,get_first_elem));
                locals.add(new TypeUse(typedInc, new F64()));
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

    private List<Instruction> genNonMovingForloop(TIRForStmt tirStmt) {
        Block block = new Block();
        Idx endLabel = new Idx("block_"+TempFactory.genFreshTempString()) ;
        loopStack.push(new LoopMetaInformation(tirStmt, endLabel, endLabel,null));
        block.setLabel(endLabel);

        List<Instruction> res = new List<>();
        // Set the initial value of variable, Add instructions with no loop
        res.addAll(expressionGenerator.genName(tirStmt.getLowerName(),tirStmt));
        if(!valueAnalysisUtil.isScalar(tirStmt.getLowerName().getID(), tirStmt,true)){
            res.addAll(MatWablyArray.getArrayIndexF64Check(1));
        }
        GCInstructions gcInst = this.functionAnalyses.getGCInstructionMapping().get(tirStmt);
        if(gcInst.hasInBetweenStmtInstructions()) res.addAll(produceInBetweenExpressionGCIntructions(gcInst));
        res.add(new SetLocal(new Idx(valueAnalysisUtil.
                genTypedName(tirStmt.getLoopVarName().getID(),
                        tirStmt, false))));
        res.addAll(genStmtList(tirStmt.getStatements()));
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
     * @param tirStmt
     * @param direction
     * @param typedLow
     * @param typedHigh
     * @param incrementVar
     * @return
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
        GCInstructions gcInst = this.functionAnalyses.getGCInstructionMapping().get(tirStmt);
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
        listInstLoop.addAll(endLoopInstr);
        listInstLoop.add(new BrIf(startLabel));
        ifStmt.addInstructionsIf(loop);
        loopStack.pop();
        return res;

    }


    // TODO Fix this
    private List<Instruction> genSetArrayStmt(TIRArraySetStmt tirStmt){
        String val = tirStmt.getValueName().getID();
        String typedArr = Util.getTypedLocalI32(tirStmt.getArrayName().getID());
        BasicMatrixValue bmv = Util.getBasicMatrixValue(analysisFunction, tirStmt, val);
        List<Instruction> res = new List<>();

        if(isSlicingOperation(tirStmt, tirStmt.getIndices())){
            BuiltinGenerator generator = new BuiltinGenerator(tirStmt,tirStmt.getIndices(),
                    null,"subsref",programAnalysis,
                    analysisFunction, expressionGenerator);
            MatWablyBuiltinGeneratorResult result =  generator.getResult();
            result.addInstruction(new GetLocal(new Idx(typedArr)));
            // Generate inputs
            generator.generateInputs();
            // Make call to get_f62

            locals.addAll(result.getLocals());
            // Get shape of rhs
            if(!bmv.hasShape()){
                throw new Error("Shape information necessary for compilation");
            }
            String valArr = Util.getTypedLocalI32(tirStmt.getValueName().getID());
            // Convert value to array if not array
            if(bmv.getShape().isScalar()){
                val = Util.getTypedLocalF64(tirStmt.getValueName().getID());
                valArr = Util.genTypedLocalI32();
                locals.add(new TypeUse(valArr, new I32()));
                res.addAll(MatWablyArray.createF64Vector(1));
                res.add(new SetLocal(new Idx(valArr)));
                res.addAll(MatWablyArray.setArrayIndexF64(valArr, 0,
                        new GetLocal(new Idx(val))));
            }
            result.addInstructions(
                    new GetLocal(new Idx(valArr)),
                    new Call(new Idx("set_f64"))
            );
            res.addAll(result.getInstructions());
        }else{

            res.add(new GetLocal(new Idx(typedArr)));
            res.addAll(computeIndexWasm(tirStmt, tirStmt.getArrayName().getID(),tirStmt.getIndices()));
            if(bmv.hasShape() && bmv.getShape().isScalar()){
                res.add(new GetLocal(new Idx(Util.getTypedLocalF64(tirStmt.getValueName().getID()))));
            }else{
                res.addAll(MatWablyArray.getArrayIndexF64CheckBounds(tirStmt.getValueName().getID()+"_i32",1));
            }
            res.add(new Call(new Idx("set_array_index_f64")));
        }
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

    // TODO Include new generation strategy checking ranges
    private List<Instruction> genGetArrayStmt(TIRArrayGetStmt tirStmt) {
        List<Instruction> res = new List<>();

        if(valueAnalysisUtil.isScalar(tirStmt.getArrayName().getID(), tirStmt, true)){
            res.addAll(new GetLocal(new Idx(Util.getTypedLocalF64(tirStmt.getArrayName().getID()))));
        }
        if(isSlicingOperation(tirStmt,tirStmt.getIndices())){
            BuiltinGenerator generator = new BuiltinGenerator(tirStmt,tirStmt.getIndices(),
                    tirStmt.getTargets(),"subsasgn",programAnalysis,
                    analysisFunction, expressionGenerator);

            MatWablyBuiltinGeneratorResult result =  generator.getResult();
            result.addInstruction(new GetLocal(new Idx(Util.getTypedLocalI32(tirStmt.getArrayName().getID()))));
            // Generate inputs
            generator.generateInputs();
            // Make call to get_f64
            result.addInstructions(new List<>(
                    new Call(new Idx("get_f64"))
            ));
            generator.generateSetToTarget();
            res.addAll(result.getInstructions());
            locals.addAll(result.getLocals());
        }else {
            String arrayName = tirStmt.getArrayName().getID();

            // Make Call
            res.add(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))));
            res.addAll(computeIndexWasm(tirStmt, arrayName, tirStmt.getIndices()));
            res.add(new Call(new Idx("get_array_index_f64")));

            // Set variable
            if(tirStmt.getTargets().size() > 0){
                res.add(new SetLocal(new Idx(Util.getTypedLocalF64(tirStmt.getTargetName().getID()))));
            }else{
                res.add(new Drop());
            }
        }
        return res;
    }
    private List<Instruction> computeIndexWasm(ASTNode node, String arrayName, TIRCommaSeparatedList indices){
        List<Instruction> res = new List<>();
        String typedArrayName = Util.getTypedLocalI32(arrayName);
        if ( indices.size() == 1) {
            NameExpr idx = (NameExpr) indices.getChild(0);
            res.add(new GetLocal(new Idx(Util.getTypedLocalF64(idx.getName().getID()))));
        }else {
            Integer[] stride = computeStride(node, arrayName);
            // Compute index
            res.addAll(new List<>( new GetLocal(new Idx(
                    Util.getTypedLocalF64(((NameExpr)indices.getChild(0)).getName().getID()))),
                    new ConstLiteral(new F64(),1),
                    new Sub(new F64())));
            for (int i = 1; i < indices.size(); ++i) {
                if (stride[i] != null) {
                    res.add(new ConstLiteral(new F64(), stride[i]));
                } else {
                    res.addAll(new List<>(
                            new GetLocal(new Idx(typedArrayName)),
                            new ConstLiteral(new F64(), i),
                            new Call(new Idx("get_array_stride"))
                    ));
                }
                res.add(new GetLocal(new Idx(Util.getTypedLocalF64(
                        ((NameExpr)indices.getChild(i)).getName().getID()))));
                res.add(new ConstLiteral(new F64(), 1));
                res.add(new Sub(new F64()));
                res.add(new Mul(new F64()));
                res.add(new Add(new F64()));
            }
            // Add one to index
            res.addAll(new List<>(
                    new ConstLiteral(new F64(),1),
                    new Add(new F64())));
            // Convert final index to i32
        }
        res.add(new CvTrunc(new I32(), new F64(), true));
        return res;
    }
    private Integer[] computeStride(ASTNode node, String arrayName) {
        BasicMatrixValue bmv = Util.getBasicMatrixValue(analysisFunction, node, arrayName);
        int numDimensions = bmv.getShape().getDimensions().size();
        Integer[] stride = new Integer[numDimensions];
        stride[0] = 1;

        for (int i = 1; i < numDimensions; ++i) {
            DimValue dv = bmv.getShape().getDimensions().get(i-1);
            if (dv.hasIntValue()) {
                stride[i] = stride[i-1] * dv.getIntValue();
            } else {
                break;
            }
        }

        return stride;
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
        GCInstructions gcInst = this.functionAnalyses.getGCInstructionMapping()
                .get(tirStmt);
        String source = tirStmt.getSourceName().getID();
        String target = tirStmt.getTargetName().getID();
        instructions.addAll(expressionGenerator.genName(tirStmt.getSourceName(),tirStmt));

        if(gcInst.hasInBetweenStmtInstructions())
            instructions.addAll(produceInBetweenExpressionGCIntructions(gcInst));

        if(valueAnalysisUtil.isScalar(source, tirStmt,true)){
            instructions.add(new SetLocal( new Idx(valueAnalysisUtil.genTypedName(target, tirStmt,false))));
        }else{
            if(opts.omit_copy_insertion){
                instructions.add(new Call(new Idx(new Opt<>(new Identifier("clone")), -1)));
                instructions.add(new SetLocal( new Idx(valueAnalysisUtil.genTypedName(target, tirStmt,false))));
            }else{
                instructions.add(new SetLocal( new Idx(valueAnalysisUtil.genTypedName(target, tirStmt,false))));
            }
        }
        return instructions;
    }
    // TODO: Tree_expr opt
    private boolean isSlicingOperation(ASTNode tirStmt, TIRCommaSeparatedList indices) {
        for (ast.Expr index : indices) {
            if (index instanceof ast.ColonExpr)
                return true;
            if (index instanceof ast.NameExpr) {
                ast.Name indexName = ((ast.NameExpr) index).getName();
                BasicMatrixValue bmv = Util.getBasicMatrixValue(analysisFunction, tirStmt, indexName.getID());
                if (!bmv.getShape().isScalar())
                    return true;
            }
        }
        return false;
    }

    /**
     * Generator for TIRLiteralStmt
     * @param tirStmt TameIR node
     * @return Generated set of instructions.
     */
    private List<Instruction> genAssignLiteralStmt(TIRAssignLiteralStmt tirStmt) {
        List<Instruction> inst = new List<>();
        GCInstructions gcInst = this.functionAnalyses
                .getGCInstructionMapping().get(tirStmt);


        inst.addAll(expressionGenerator.genExpr(tirStmt.getRHS(), tirStmt));

        if(gcInst.hasInBetweenStmtInstructions())
            inst.addAll(produceInBetweenExpressionGCIntructions(gcInst));

        // Set target
        String newName = valueAnalysisUtil.genTypedName(tirStmt.getLHS().getVarName(),tirStmt, true);
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
        // When we generate call then, make this determination again for the output arguments to throw run-time error.

        for(Name name: func.getOutputParamList()){
            if(!valueAnalysisUtil.hasBasicMatrixValue(name.getID(), func, false)){
                // TODO(dherre3) Fix this, this is an error only if we query the function for the non-defined return parameter.
                // TODO(dherre3) If we return i.e. [a,b,c] = ret1() and ret1() does not defined b, its an error, instead if [a] = ret1(), this is not an error.
                throw new Error("Return variable: \""+ name.getID()+"\" is never defined in function context: "
                + func.getName().getID());
            }
            // TODO:(dherre3) Refactor this!
            TypeUse typeUse = new TypeUse(valueAnalysisUtil.genTypedName(name.getID(), func, false),
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
