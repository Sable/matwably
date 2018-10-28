package matwably.code_generation;

import ast.*;
import matjuice.analysis.PointsToAnalysis;
import matjuice.transformer.CopyInsertion;
import matjuice.transformer.MJCopyStmt;
import matwably.analysis.ConstantLoadElimination;
import matwably.analysis.Locals;
import matwably.ast.*;
import matwably.ast.Function;
import matwably.ast.List;
import matwably.ast.Opt;
import matwably.code_generation.builtin.BuiltinGenerator;
import matwably.code_generation.builtin.ResultWasmGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.code_generation.wasm.SwitchStatement;
import matwably.util.Ast;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.Util;
import natlab.tame.tir.*;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.toolkits.rewrite.TempFactory;

import java.util.HashMap;
import java.util.Stack;

public class FunctionGenerator {


    /**
     * Empty,                Nothing in the loop executes, e.g. for i=[]:1:10. In this case i remains undefined
     * NonMoving,            Bounds are equal so that the statements in the loop only run ones. In this case we can
     *                       skip the loop.
     * Ascending/Descending, Loop goes in increasing or decreasing fashion
     * Unknown,              Bounds are not known statically and the loop is generated dynamically.
     */
    private enum LoopDirection {
        Empty,
        NonMoving,
        Ascending,
        Descending,
        Unknown
    };

    private List<TypeUse> output_parameters;
    private List<TypeUse> locals;
    private List<TypeUse> parameters;
    private  HashMap<String, TypeUse> local_map;
    private Stack<Idx> startLoop = new Stack<>();
    private Stack<Idx> endLoop = new Stack<>();
    private String function_name;

    /**
     * Returns the ast of the generated function
     * @return
     */
    public Function getAst() {
        return function;
    }

    Function function;
//    List<>
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction;
    private ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis;
    private  InterproceduralFunctionQuery interproceduralFunctionQuery;

    public FunctionGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> analysis, int i) {
        this.programAnalysis = analysis;
        this.analysisFunction = analysis.getNodeList().get(i).getAnalysis();
        this.locals = new List<>();
        this.parameters = new List<>();
        this.output_parameters = new List<>();
        this.interproceduralFunctionQuery = new InterproceduralFunctionQuery(programAnalysis);
        this.function = genFunction(this.analysisFunction.getTree());
    }
    private Function genFunction( TIRFunction tirFunction ){

        // Run Literal elimination analysis

        ConstantLoadElimination.apply(this.analysisFunction.getTree(),interproceduralFunctionQuery);



        // Perform copy insertion
        performCopyInsertion();

        // Setting function name and signature setting output_parameters
        Type funcType = genSignature(tirFunction);

        // Setting locals
        Locals localsAnalysis = new Locals();
        local_map = localsAnalysis.apply(tirFunction, analysisFunction);
        locals.addAll(local_map.values());

        // Setting instructions
        List<Instruction> instructions = genInstructionList(tirFunction.getStmtList());
        Expression exp = new Expression(instructions);
        // Setting return function, automatically adds return statements for variables
        instructions.addAll(addReturn());
        return new Function(new Opt<>(new Identifier(this.function_name)),funcType, locals, exp );
    }

    private List<Instruction> addReturn() {
        List<Instruction> instructions = new List<>();
//        analysis.getResult()
        int size = this.output_parameters.getNumChild();
        if(size == 1){
            TypeUse typeUse = this.output_parameters.getChild(0);
            // TODO(dherre3) Fix for globals.
            if(typeUse.hasIdentifier()){
                instructions.add(new GetLocal(new Idx(new Opt<>(typeUse.getIdentifier()),0 )));
            }else{
                throw new Error("Identifier must be defined");
            }
        }else if(size > 1){
            // TODO: This is wrong

            int i = 0;
            String nameInputVec;
            if(returnsScalarVector()){
                instructions.addAll(MatWablyArray.createF64Vector(
                        this.output_parameters.getNumChild()));
                nameInputVec = Util.genTypedLocalI32();
                locals.add(new TypeUse(nameInputVec, new I32()));
                // Add statement name
                instructions.add(new SetLocal(new Idx(nameInputVec)));
                for (ValueSet<?> arg :  analysisFunction.getResult()) {
                    instructions.add(new GetLocal(new Idx(nameInputVec)));
                    TypeUse argTypeUse = this.output_parameters.getChild(i);
                    instructions.add(new ConstLiteral(new I32(), i));
                    instructions.add(new GetLocal(new Idx(argTypeUse.getIdentifier().getName())));
                    instructions.add(new Call(new Idx(new Opt<>(new Identifier("set_array_index_f64_no_check")),0)));
                    i++;
                }
            }else{
                instructions.addAll(MatWablyArray.createCellVector(
                        this.output_parameters.getNumChild()));

                nameInputVec = Util.genTypedLocalI32();
                locals.add(new TypeUse(nameInputVec, new I32()));
                // Add statement name
                instructions.add(new SetLocal(new Idx(nameInputVec)));
                for (ValueSet<?> arg :  analysisFunction.getResult()) {

                    instructions.add(new GetLocal(new Idx(nameInputVec)));
                    TypeUse argTypeUse = this.output_parameters.getChild(i);
                    instructions.add(new ConstLiteral(new I32(), i));
                    instructions.add(new GetLocal(new Idx(argTypeUse.getIdentifier().getName())));
                    if(argTypeUse.getType().getTypeValue() == 3){
                        instructions.add(new Call(new Idx(new Opt<>(new Identifier("convert_scalar_to_mxarray")),1)));
                    }
                    instructions.add(new Call(new Idx(new Opt<>(new Identifier("set_array_index_i32_no_check")),0)));
                    i++;
                }
            }
            instructions.add(new GetLocal(new Idx(new Opt<>(new Identifier(nameInputVec)),0)));
        }
        return instructions;
    }
    public boolean returnsScalarVector(){
        // Check if all the return values are f64
        for(TypeUse typeUse: this.output_parameters)
            if(typeUse.getType().getTypeValue() != 3) return false;
        return this.output_parameters.getNumChild() > 1;
    }


    private List<Instruction> genInstructionList(TIRStatementList stmtList) {
        List<Instruction> instructionList = new List<>();
        for(ast.Stmt stmt: stmtList){
            instructionList.addAll(genStmt(stmt));
        }
        return instructionList;
    }

    private List<Instruction> genStmt(Stmt tirStmt) {

        if (tirStmt instanceof TIRAssignLiteralStmt) {
            return genAssignLiteralStmt((TIRAssignLiteralStmt) tirStmt);
        } else if (tirStmt instanceof TIRCallStmt) {
//            BuiltinGenerator generator =
            ResultWasmGenerator inputHandler = BuiltinGenerator.
                    generate((TIRCallStmt) tirStmt, programAnalysis, analysisFunction);
            locals.addAll(inputHandler.getLocals());
            return inputHandler.getInstructions();
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
        }else if(tirStmt instanceof TIRIfStmt){
            return genIfStmt((TIRIfStmt)tirStmt);
        }else if(tirStmt instanceof TIRCommentStmt|| tirStmt instanceof TIRReturnStmt){
            return new List<>();
        }
        throw new UnsupportedOperationException(
                String.format("Expr node not supported. %d:%d: [%s] [%s]",
                        tirStmt.getStartLine(), tirStmt.getStartColumn(),
                        tirStmt.getPrettyPrinted(), tirStmt.getClass().getName())
        );
    }


    private List<Instruction> genIfStmt(TIRIfStmt tirStmt) {
        List<Instruction> res = new List<>();
        String condVar = tirStmt.getConditionVarName().getID();
        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt, condVar);
        If ifStmt = new If();

        if(val.hasShape()){
            if(val.getShape().isScalar()){
                res.add(new GetLocal(new Idx(Util.getTypedLocalF64(condVar))));
                res.add(new ConstLiteral(new F64(), 0));
                res.add(new Eq(new F64()));
            }else{
                res.add(new GetLocal(new Idx(Util.getTypedLocalI32(condVar))));
                res.add(new Call(new Idx("all_nonzero_reduction")));
            }
            res.add(new Eqz(new I32()));
            ifStmt.setInstructionsIfList(genInstructionList(tirStmt.getIfStatements()));
            if(tirStmt.hasElseBlock()){
                ifStmt.setInstructionsElseList(genInstructionList(tirStmt.getElseStatements()));
            }
        }else{
            throw new Error("Must have type information for if condition: "+tirStmt.getPrettyPrinted());
        }
        res.add(ifStmt);
        return res;
    }

    private List<Instruction> genContinueStmt(TIRContinueStmt tirStmt) {
        return new List<>(new Br(startLoop.peek()));
    }
    private List<Instruction> genBreakStmt(TIRBreakStmt tirStmt) {
        return new List<>(new Br(endLoop.peek()));
    }

    private List<Instruction> genWhileStmt(TIRWhileStmt tirStmt) {
        List<Instruction> res = new List<>();
        NameExpr expr = tirStmt.getCondition();
        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt,expr.getName().getID() );

        Idx startLabel = new Idx("loop_"+TempFactory.genFreshTempString()) ;
        Idx endLabel = new Idx("block_"+TempFactory.genFreshTempString()) ;
        endLoop.push(endLabel);
        startLoop.push(startLabel);

        Loop loop = new Loop();
        Block block = new Block();
        loop.setLabel(startLabel);
        block.setLabel(endLabel);
        loop.addInstruction(block);
        if(val.hasShape()){
            if(val.getShape().isScalar()){
                block.addInstruction(new GetLocal(new Idx(Util.getTypedLocalF64(expr.getName().getID()))));
                block.addInstruction(new ConstLiteral(new F64(), 0));
                block.addInstruction(new Eq(new F64()));
            }else{
                block.addInstruction(new GetLocal(new Idx(Util.getTypedLocalI32(expr.getName().getID()))));
                block.addInstruction(new Call(new Idx("all_nonzero_reduction")));
                block.addInstruction(new Eqz(new I32()));

            }
//            block.addInstruction(new Eqz(new I32()));
            block.addInstruction(new BrIf(endLabel));
            // Gen Stmts
            block.getInstructionList().addAll(genInstructionList(tirStmt.getStatements()));
            // Add break to loop beginning
            block.addInstruction(new Br(startLabel));
        }else{
            throw new Error("Must have type information for while loop condition: "+tirStmt.getPrettyPrinted());
        }
        res.add(loop);
        endLoop.pop();
        startLoop.pop();
        return res;
    }

    private List<Instruction> genForStmt(TIRForStmt tirStmt) {
        List<Instruction> res =new List<>();
        LoopDirection direction = getLoopDirection(tirStmt);
        // If the boundaries do not enter the loop, skip the loop creation entirely
        if(direction == LoopDirection.Empty) return res;
//        Instruction increment;
        if(direction == LoopDirection.Ascending||direction == LoopDirection.Descending) {
            return genStaticLoop(tirStmt, direction,
                    Util.getTypedLocalF64(tirStmt.getLowerName().getID()),
                    Util.getTypedLocalF64(tirStmt.getUpperName().getID()),
                    Util.getTypedLocalF64((tirStmt.hasIncr())?tirStmt.getIncName().getID():null));
        }else if(direction == LoopDirection.NonMoving){
            res.addAll(genNonMovingForloop(tirStmt));
        }else if(direction == LoopDirection.Unknown){
           res.addAll(genDynamicForLoop(tirStmt));
        }
        return res;
    }

    private List<Instruction> genDynamicForLoop(TIRForStmt tirStmt) {
        List<Instruction> res = new List<>();
        String isEmptyFlag = Util.genTypedLocalI32();
        locals.add(new TypeUse(isEmptyFlag, new I32()));
        // Dynamically generate increase, decrease empty or not moving
        // Add checks for empty arrays, if arrays.
        String lowTyped;
        String highTyped;
        BasicMatrixValue val_lower = Util.getBasicMatrixValue(analysisFunction,tirStmt, tirStmt.getLowerName().getID());
        BasicMatrixValue val_upper = Util.getBasicMatrixValue(analysisFunction,tirStmt, tirStmt.getUpperName().getID());
        if( val_lower.hasShape()){
            if(!val_lower.getShape().isScalar() ){

                lowTyped = Util.getTypedLocalI32(tirStmt.getLowerName().getID());
                res.addAll(MatWablyArray.isEmpty(lowTyped));
                List<Instruction> get_first_elem = MatWablyArray.getArrayIndexF64CheckBounds(lowTyped, 1);
                lowTyped = Util.genTypedLocalF64();
                get_first_elem.add(new SetLocal(new Idx(lowTyped)));
                res.add(new If(new Opt<>(),
                        new List<>(new ConstLiteral(new I32(), 1), new SetLocal(new Idx(isEmptyFlag)))
                        ,get_first_elem));
                locals.add(new TypeUse(lowTyped, new F64()));
            }else{
                lowTyped = Util.getTypedLocalF64(tirStmt.getLowerName().getID());
            }
        }else{
            throw new Error("Need shape information missing for variable: "+tirStmt.getLowerName().getID()+" in stmt: "+
                    tirStmt.getPrettyPrinted());
        }
        if(val_upper.hasShape()){
            if(!val_upper.getShape().isScalar() ){
                highTyped = Util.getTypedLocalI32(tirStmt.getUpperName().getID());
                res.addAll(MatWablyArray.isEmpty(highTyped));
                List<Instruction> get_first_elem = MatWablyArray.getArrayIndexF64CheckBounds(highTyped, 1);
                highTyped = Util.genTypedLocalF64();
                get_first_elem.add(new SetLocal(new Idx(highTyped)));
                res.add(new If(new Opt<>(),
                        new List<>(new ConstLiteral(new I32(), 1), new SetLocal(new Idx(isEmptyFlag)))
                        ,get_first_elem));

                locals.add(new TypeUse(highTyped, new F64()));
            }else{
                highTyped = Util.getTypedLocalF64(tirStmt.getUpperName().getID());
            }
        }else {
            throw new Error("Need shape information missing for variable: "+tirStmt.getLowerName().getID()+" in stmt: "
                    +tirStmt.getPrettyPrinted());
        }
        String increaseVar = "";
        if(tirStmt.hasIncr()){
            BasicMatrixValue val_incr = Util.getBasicMatrixValue(analysisFunction,tirStmt, tirStmt.getIncName().getID());
            if(val_incr.hasShape()){
                if(!val_incr.getShape().isScalar()){
                    increaseVar = Util.getTypedLocalI32(tirStmt.getIncName().getID());
                    res.addAll(MatWablyArray.isEmpty(increaseVar));
                    List<Instruction> get_first_elem = MatWablyArray.getArrayIndexF64CheckBounds(increaseVar, 1);
                    increaseVar = Util.genTypedLocalF64();
                    get_first_elem.add(new SetLocal(new Idx(increaseVar)));
                    res.add(new If(new Opt<>(),
                            new List<>(new ConstLiteral(new I32(), 1), new SetLocal(new Idx(isEmptyFlag)))
                            ,get_first_elem));
                    locals.add(new TypeUse(increaseVar, new F64()));
                }else{
                    increaseVar = Util.getTypedLocalF64(tirStmt.getIncName().getID());
                }
            }else {
                throw new Error("Need shape information missing for variable: "+tirStmt.getIncName().getID()+" in stmt: "
                        + tirStmt.getPrettyPrinted());
            }
        }
        // Add if statement, if we enter the if statement the loop executes, otherwise it does not
        // execute


        List<Instruction> condSwitch = new List<>();
        if(tirStmt.hasIncr()){
            condSwitch.addAll(
                    new GetLocal(new Idx(lowTyped)),
                    new GetLocal(new Idx(increaseVar)),
                    new GetLocal(new Idx(highTyped)),
                    new Call(new Idx("dynamic_loop_three")));
        }else{
            condSwitch.addAll(
                    new GetLocal(new Idx(lowTyped)),
                    new GetLocal(new Idx(highTyped)),
                    new Call(new Idx("dynamic_loop_two")));
        }
        SwitchStatement swit = new SwitchStatement(
                condSwitch,
                genNonMovingForloop(tirStmt),
                genStaticLoop(tirStmt,LoopDirection.Descending, lowTyped, highTyped, increaseVar),
                genStaticLoop(tirStmt,LoopDirection.Ascending, lowTyped, highTyped, increaseVar)
        );
        res.addAll(
                new GetLocal(new Idx(isEmptyFlag)),
                new Eqz(new I32()));
        If ifStmt = new If();
        ifStmt.setInstructionsIfList(swit.generate());
        res.add(ifStmt);
//        res.add(new Drop());
        return res;
    }

    private List<Instruction> genNonMovingForloop(TIRForStmt tirStmt) {
        Block block = new Block();
        Idx endLabel = new Idx("block_"+TempFactory.genFreshTempString()) ;
        endLoop.push(endLabel);
        startLoop.push(endLabel);

        block.setLabel(endLabel);
        List<Instruction> res = new List<>();
        // Set the initial value of variable, Add instructions with no loop
        res.add(new GetLocal(new Idx(Util.getTypedLocalF64(tirStmt.getLowerName().getID()))));
        res.add(new SetLocal(new Idx(Util.getTypedLocalF64(tirStmt.getLoopVarName().getID()))));
        res.addAll(genInstructionList(tirStmt.getStatements()));
        block.setInstructionList(res);
        endLoop.pop();
        startLoop.pop();
        return new List<>(block);
    }

    private List<Instruction> genStaticLoop(TIRForStmt tirStmt, LoopDirection direction,
                                            String typedLow, String typedHigh, String incrementVar) {

        // Get Flag for increase
        String flagFirstIterLocal = Util.genTypedLocalI32();
        locals.add(new TypeUse(flagFirstIterLocal, new I32()));
        Idx startLabel = new Idx("loop_"+TempFactory.genFreshTempString()) ;
        Idx endLabel = new Idx("block_"+TempFactory.genFreshTempString()) ;
        endLoop.push(endLabel);
        startLoop.push(startLabel);

        Loop loop = new Loop();
        Block block = new Block();
        loop.setLabel(startLabel);
        block.setLabel(endLabel);

        // I32 Variables for loop.
        String typedVarLoop = Util.getTypedLocalF64(tirStmt.getLoopVarName().getID());
        NumericInstruction increment;
        Instruction brIfCondition;//
        if(direction == LoopDirection.Ascending){
            increment = (tirStmt.hasIncr())?
                    new GetLocal(new Idx(incrementVar)):
                    new ConstLiteral(new F64(),1);
            brIfCondition = new Gt(new F64(), true);
        }else{
            increment =  (tirStmt.hasIncr())?
                    new GetLocal(new Idx(incrementVar)):
                    new ConstLiteral(new F64(),-1);
            brIfCondition = new Lt(new F64(), true);
        }

        // Add initialization of loop and increase flag.
        loop.addInstruction(new GetLocal(new Idx(flagFirstIterLocal)));
        List<Instruction> ifSmtsCond =  OperatorGenerator.generateBinOp("plus",
                new GetLocal(new Idx(typedVarLoop)),increment);
        ifSmtsCond.add(new SetLocal(new Idx(typedVarLoop)));

        loop.addInstruction(new If(new Opt<>(),new List<>(
                new GetLocal(new Idx(typedVarLoop)),
                increment,
                new Add(new F64()),
                new SetLocal(new Idx(typedVarLoop))
        ),new List<>(
                new ConstLiteral(new I32(),1),
                new SetLocal(new Idx(flagFirstIterLocal)),
                // Initialize loop
                new GetLocal(new Idx(typedLow)),
                new SetLocal(new Idx(typedVarLoop))
        )));
        block.addInstruction(new GetLocal(new Idx(typedVarLoop)));
        block.addInstruction(new GetLocal(new Idx(typedHigh)));
        block.addInstruction(brIfCondition);
        block.addInstruction(new BrIf(endLabel));
        // Gen Stmts
        block.getInstructionList().addAll(genInstructionList(tirStmt.getStatements()));
        // Add break to loop beginning
        block.addInstruction(new Br(startLabel));
        loop.addInstruction(block);
        endLoop.pop();
        startLoop.pop();
        return  new List<>(loop,
                new ConstLiteral(new I32(), 0),
                new SetLocal(new Idx(flagFirstIterLocal)));
    }

    private LoopDirection getLoopDirection(TIRForStmt tirStmt) {
            BasicMatrixValue valRangeLeft = Util.getBasicMatrixValue(analysisFunction, tirStmt, tirStmt.getLowerName().getID());
            BasicMatrixValue valRangeRight = Util.getBasicMatrixValue(analysisFunction, tirStmt, tirStmt.getUpperName().getID());
            if(valRangeRight.hasRangeValue()&&valRangeLeft.hasRangeValue()){
                if (valRangeRight.getRangeValue()
                        .isBothBoundsKnown()) {
                    int valRight = valRangeRight.getRangeValue().getLowerBound().getIntValue();
                    int valLeft =  valRangeLeft.getRangeValue().getUpperBound().getIntValue();
                    if(valLeft == valRight) return LoopDirection.NonMoving;
                    if(tirStmt.hasIncr()){
                        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt, tirStmt.getIncName().getID());
                        if(val.hasRangeValue()){
                            if(val.getRangeValue().isRangeValueNegative()){
                                //e.g. -5:-1:5
                                if(valRight > valLeft) return LoopDirection.Empty;
                                return LoopDirection.Descending;
                            }else if(val.getRangeValue().isRangeValuePositive()){
                                //e.g. 5:1:-5
                                if(valRight < valLeft) return LoopDirection.Empty;
                                return LoopDirection.Ascending;
                            }
                        }else return LoopDirection.Unknown;

                    }
                    if(valRight > valLeft){
                        return LoopDirection.Ascending;
                    }else{
                        return LoopDirection.Descending;
                    }
                }
            }
        return (tirStmt.hasIncr())? LoopDirection.Unknown: LoopDirection.Ascending;
    }

    private List<Instruction> genSetArrayStmt(TIRArraySetStmt tirStmt){
        String val = tirStmt.getValueName().getID();
        String typedArr = Util.getTypedLocalI32(tirStmt.getArrayName().getID());
        BasicMatrixValue bmv = Util.getBasicMatrixValue(analysisFunction, tirStmt, val);
        List<Instruction> res = new List<>();


        if(isSlicingOperation(tirStmt, tirStmt.getIndices())){
            BuiltinGenerator generator = new BuiltinGenerator(tirStmt,tirStmt.getIndices(),
                    null,"set",programAnalysis,
                    analysisFunction);
            ResultWasmGenerator result =  generator.getResult();
            result.addInstruction(new GetLocal(new Idx(typedArr)));
            // Generate inputs
            generator.generateInputs();
            // Make call to get_f64

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
            String valArr = Util.getTypedLocalF64(tirStmt.getValueName().getID());
            res.add(new GetLocal(new Idx(typedArr)));
            res.addAll(computeIndexWasm(tirStmt, tirStmt.getArrayName().getID(),tirStmt.getIndices()));
            res.add(new GetLocal(new Idx(valArr)));
            res.add(new Call(new Idx("set_array_index_f64")));
        }
        return res;
    }
    private void performCopyInsertion(){
        TIRFunction tirFunction = this.analysisFunction.getTree();
        PointsToAnalysis pta;
        do {
            pta = new PointsToAnalysis(tirFunction);
            tirFunction.tirAnalyze(pta);
        } while (CopyInsertion.apply(tirFunction, pta));
    }
    private List<Instruction> genGetArrayStmt(TIRArrayGetStmt tirStmt) {
        List<Instruction> res = new List<>();
        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt, tirStmt.getArrayName().getID());
        if(val.hasShape()&&val.getShape().isScalar()){
            res.addAll(new GetLocal(new Idx(Util.getTypedLocalF64(tirStmt.getArrayName().getID()))));
        }
        if(isSlicingOperation(tirStmt,tirStmt.getIndices())){
            BuiltinGenerator generator = new BuiltinGenerator(tirStmt,tirStmt.getIndices(),
                    tirStmt.getTargets(),"get",programAnalysis,
                    analysisFunction);

            ResultWasmGenerator result =  generator.getResult();
            result.addInstruction(new GetLocal(new Idx(Util.getTypedLocalI32(tirStmt.getArrayName().getID()))));
            // Generate inputs
            generator.generateInputs();
            // Make call to get_f64
            result.addInstructions(new List<Instruction>(
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
    private List<Instruction> computeIndexWasm(TIRNode node, String arrayName, TIRCommaSeparatedList indices){
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
    private Integer[] computeStride(TIRNode node, String arrayName) {
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

    private List<Instruction> genCopyStmt(TIRCopyStmt tirStmt) {
        List<Instruction> insts = new List<>();
        String name = tirStmt.getSourceName().getID();
        String target = tirStmt.getTargetName().getID();
        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt,name);
        String nodeName = Util.getTypedName(name, val);
        BasicMatrixValue valTarget = Util.getBasicMatrixValue(analysisFunction, tirStmt,target);
        String nodeTargetName = Util.getTypedName(target, valTarget);
        int index = findLocalsIndex(nodeName);
        int indexTarget = findLocalsIndex(nodeTargetName);
        if(tirStmt instanceof MJCopyStmt){
            insts.add(new GetLocal( new Idx(new Opt<>(new Identifier(nodeName)), index)));
            if(val.getShape().isScalar()){
                insts.add(new SetLocal( new Idx(new Opt<>(new Identifier(nodeTargetName)), index)));
            }else{
                insts.add(new Call(new Idx(new Opt<>(new Identifier("clone")), -1)));
                insts.add(new SetLocal(new Idx(new Opt<>(new Identifier(nodeTargetName)),indexTarget)));
            }
        }else{
            insts.add(new GetLocal( new Idx(new Opt<>(new Identifier(nodeName)), index)));
            insts.add(new SetLocal( new Idx(new Opt<>(new Identifier(nodeTargetName)), index)));
        }
        return insts;
    }
    private boolean isSlicingOperation(TIRStmt tirStmt, TIRCommaSeparatedList indices) {
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
    private List<Instruction> genAssignLiteralStmt(TIRAssignLiteralStmt tirStmt) {
        List<Instruction> inst = genExpr(tirStmt.getRHS(), tirStmt);
        String name = tirStmt.getLHS().getVarName();
        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt, name);
        String newName = Util.getTypedName(name, val);
        inst.add(new SetLocal(new Idx(new Opt<>(new Identifier(newName)),findLocalsIndex(newName))));
        return inst;
    }
    private List<Instruction> genExpr(ast.Expr expr, TIRStmt stmt) {
        if (expr instanceof ast.IntLiteralExpr)
            return genIntLiteralExpr((ast.IntLiteralExpr) expr);
        if (expr instanceof ast.FPLiteralExpr)
            return genFPLiteralExpr((ast.FPLiteralExpr) expr);
        if (expr instanceof ast.NameExpr)
            return genNameExpr((ast.NameExpr) expr, stmt);
        if (expr instanceof ast.StringLiteralExpr)
            throw new UnsupportedOperationException("Strings are not supported by MatWably at the moment");
        if (expr instanceof ast.ColonExpr)
            throw new Error("Should have been generated at a higher level");
        return new List<>();
//        throw new UnsupportedOperationException(
//                String.format("Expr node not supported. %d:%d: [%s] [%s]",
//                        expr.getStartLine(), expr.getStartColumn(),
//                        expr.getPrettyPrinted(), expr.getClass().getName())
//        );
    }
    private int findLocalsIndex(String name){
//        this.parameters.size()
        for(int i = 0; i< this.parameters.getNumChild();i++){
            TypeUse use = this.parameters.getChild(i);
            if(use.hasIdentifier()&&use.getIdentifier().getName().equals(name)){
                return i;
            }
        }
        int index =  this.parameters.getNumChild();
        for(int i = 0; i< this.locals.getNumChild();i++){
            TypeUse use = this.locals.getChild(i);
            if(use.hasIdentifier()&&use.getIdentifier().getName().equals(name)){
                return i+index;
            }
        }
        return -1;
    }
    private List<Instruction> genNameExpr(NameExpr expr, TIRStmt stmt) {
        // TODO(dherre3): Change this for globals
        String name = expr.getName().getID();
        int localIndex = findLocalsIndex(name);
        BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, stmt, name);
        if(localIndex == -1){
            throw new Error(String.format("Wasm index for variable: %s not found", name));
        }
        return new List<>(new GetLocal(new Idx(new Opt<>(new Identifier(Util.getTypedName(name, val))),localIndex)));
    }

    private List<Instruction> genFPLiteralExpr(FPLiteralExpr expr) {
        return new List<>(new ConstLiteral(new F64(), Double.parseDouble(expr.getValue().getText())));
    }

    private List<Instruction> genIntLiteralExpr(IntLiteralExpr expr) {
        return new List<>(new ConstLiteral(new F64(), Integer.parseInt(expr.getValue().getText())));
    }



    private Type genSignature(TIRFunction func){
        Args<AggrValue<BasicMatrixValue>> args =  analysisFunction.getArgs();
        List<TypeUse> result = new List<>();
        ast.List<Name> params = func.getInputParams();
        StringBuilder suffix = new StringBuilder();
        int paramIndex = 0;
        for (Object arg : args) {
            Name param = params.getChild(paramIndex);
            String paramName;
            ValueType type;
            BasicMatrixValue bmv = (BasicMatrixValue) arg;
            if (bmv.hasShape() && bmv.getShape().isScalar())
            {
                suffix.append("S");
                type = new F64();
                paramName = param.getID()+"_f64";
            }else{
                suffix.append("M");
                type = new I32();
                paramName = param.getID()+"_i32";
            }
            result.add(new TypeUse(new Opt<>(new Identifier(paramName)), type));
            paramIndex++;
        }

        List<ValueType> returnVals = new List<>();
        List<TypeUse> returnIds = new List<>();

        this.function_name = func.getName().getID()+"_"+suffix.toString();
        for(Name name: func.getOutputParamList()){
            BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, func, name.getID());
            if(val == null){
                throw new Error("Return variable: \""+ name.getID()+"\" is never defined in function context: "
                + function_name);
            }
            TypeUse typeUse = Ast.genTypeUse(Util.getTypedName(name.getID(),val), val);
            ValueType valueType = typeUse.getType();
            returnVals.add(valueType);
            returnIds.add(typeUse);
        }

        this.output_parameters = returnIds;
        this.parameters = result;

        // Setting output
        Opt<ReturnType> returnType;
        if(returnVals.getNumChild()>0){
            returnType = new Opt<>(new ReturnType((returnVals.getNumChild()>1)?new List<>(new I32()): returnVals));
        }else{
            returnType = new Opt<>();
        }
        return new Type(new Opt<>(), this.parameters,returnType);
    }

}
