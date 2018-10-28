package matwably.code_generation.builtin;

import ast.Expr;
import ast.Name;
import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.OperatorGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.Util;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static matwably.util.Util.getBasicMatrixValue;

public class BuiltinGenerator {
    private static HashMap<String, BuiltinInputHandler> builtinMap = new HashMap<>();
    private TIRNode node;
    private TIRCommaSeparatedList arguments;
    private TIRCommaSeparatedList targets;
    private String callName;
    private String generatedCallName;
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
    private ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis;

    public BuiltinGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName,
                            ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis,
                            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction) {
        this.node = node;
        this.arguments = arguments;
        this.targets = targs;
        this.callName = callName;
        this.generatedCallName = callName;
        this.analysis = analysisFunction;
        this.programAnalysis =programAnalysis;
        this.result = new ResultWasmGenerator();
    }

    public ResultWasmGenerator getResult() {
        return result;
    }

    private ResultWasmGenerator result;

    private static HashMap<String, String> ALIAS_WASM = new HashMap<>();
    private static HashMap<String, BuiltinSimplifier> SIMPLIFIABLE = new HashMap<>();
    static {
        ALIAS_WASM.put("mtimes_SM","times_SM");
        ALIAS_WASM.put("mtimes_MS","times_MS");
        ALIAS_WASM.put("mldivide_SM","ldivide_SM");
        ALIAS_WASM.put("mldivide_MS","ldivide_MS");
        ALIAS_WASM.put("mldivide_SS","ldivide_SS");
        ALIAS_WASM.put("mrdivide_SS","rdivide_SS");
        ALIAS_WASM.put("mrdivide_SM","rdivide_SM");
        ALIAS_WASM.put("mrdivide_MS","rdivide_MS");
        SIMPLIFIABLE.put("plus", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("minus",OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("times", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("mtimes", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("mrdivide",OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("rdivide",OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("le", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("lt", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("ge", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("gt", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("eq", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("ne", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("uminus", OperatorGenerator.getInstance());
        SIMPLIFIABLE.put("true", TrueGenerator.getInstance());
        SIMPLIFIABLE.put("false", FalseGenerator.getInstance());
//        SIMPLIFIABLE.put("colon", ColonGenerator)

    }
    private static String[] SPECIALIZED = {
            "plus", "minus", "mtimes", "rem", "mod", "mrdivide","mldivide",
            "lt", "le", "gt", "ge", "eq", "ne", "length",
            "sin", "cos", "tan", "uminus", "exp", "rdivide","ldivide", "round", "sqrt",
            "mpower", "floor", "ceil", "power", "abs", "fix", "and", "times",
            "log","disp","round","transpose", "size","not","length","mean"
    };
    private static String[] SCALAR_OUTPUT = {
            "length","ndims","isscalar","isempty",
            "isrow","iscolumn","isvector","ismatrix",
            "numel","tic","toc","pi","e","length_M","size_MS"
    };

    private static String[] RETURNS_SCALAR_VECTOR = {

    };
    private static String[] DOES_NOT_HAVE_RETURN = {
        "disp"
    };
    static {
        builtinMap.put("ones", ShapeBuiltinInputHandler.getInstance());
        builtinMap.put("randn", ShapeBuiltinInputHandler.getInstance());
        builtinMap.put("rand", ShapeBuiltinInputHandler.getInstance());
        builtinMap.put("zeros", ShapeBuiltinInputHandler.getInstance());
        builtinMap.put("eye", ShapeBuiltinInputHandler.getInstance());
        builtinMap.put("vertcat", VectorBuiltinInputHandler.getInstance());
        builtinMap.put("horzcat", VectorBuiltinInputHandler.getInstance());
        builtinMap.put("concat", VectorBuiltinInputHandler.getInstance());
        builtinMap.put("colon", VectorBuiltinInputHandler.getInstance());
        builtinMap.put("get", VectorBuiltinInputHandler.getInstance());
        builtinMap.put("set", VectorBuiltinInputHandler.getInstance());
        builtinMap.put("reshape", OneMatrixAndShapeBuiltinInputHandler.getInstance());
        builtinMap.put("randi", OneMatrixAndShapeBuiltinInputHandler.getInstance());
        Arrays.sort(SPECIALIZED, String::compareTo);
        Arrays.sort(RETURNS_SCALAR_VECTOR, String::compareTo);
        Arrays.sort(SCALAR_OUTPUT, String::compareTo);
        Arrays.sort(DOES_NOT_HAVE_RETURN, String::compareTo);
    }

    public boolean getSimplification(){
        if(SIMPLIFIABLE.containsKey(callName)){
            BuiltinSimplifier simplifier = SIMPLIFIABLE.get(callName);
            simplifier.analysis = analysis;
            simplifier.arguments = arguments;
            simplifier.node = node;
            simplifier.callName = callName;
            simplifier.targets = targets;
            if(simplifier.isSimplifiable()){
                result.addInstructions(simplifier.simplify());
                return true;
            }
        }
        return false;
    }
    public static ResultWasmGenerator generate(TIRCallStmt tirFunction, ValueAnalysis<AggrValue<BasicMatrixValue>> programAnalysis,
                                               IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis){
        BuiltinGenerator generator = new BuiltinGenerator(tirFunction,tirFunction.getArguments(),
                tirFunction.getTargets(),tirFunction.getFunctionName().getID(),programAnalysis,
                analysis);
        if(generator.getSimplification()){
            return generator.result;
        }

        // Analyze inputs
        generator.generateInputs();

        // Make call
        generator.generateCall();

        // Set to targets
        generator.generateSetToTarget();
        // Need to drop result
        return generator.result;
    }


    public void generateInputs(){
        BuiltinInputHandler classGenerator;
        if(builtinMap.containsKey(this.callName)){
            classGenerator = builtinMap.get(callName);
        }else{
            classGenerator = DefaultBuiltinInputHandler.getInstance(node, arguments, analysis, result);
        }
        classGenerator.setStmt(node);
        classGenerator.setValueAnalysis(analysis);
        classGenerator.setResult(result);
        classGenerator.setArguments(arguments);
        if(classGenerator.isInForm())
            classGenerator = DefaultBuiltinInputHandler.getInstance(node, arguments, analysis, result);
        classGenerator.generate();
    }
    public void generateCall(){
        // Call Function
        generatedCallName  = callName;

        if(Builtin.getInstance(callName) == null || isSpecialized(callName)){
            StringBuilder suffix = new StringBuilder();
            for (ast.Expr argExpr :arguments) {
                ast.NameExpr nameExpr = (ast.NameExpr) argExpr;
                BasicMatrixValue bmv = getBasicMatrixValue(analysis, node, nameExpr.getName().getID());
                if (bmv.hasShape() && bmv.getShape().isScalar())
                    suffix.append("S");
                else
                    suffix.append("M");
            }
            generatedCallName+="_"+suffix;
        }
        if(hasAlias(generatedCallName))
            generatedCallName = ALIAS_WASM.get(generatedCallName);
        result.addInstruction(new Call(
                new Idx(new Opt<>(new Identifier(generatedCallName)),0)));
    }
    public boolean hasAlias(String functionName){return ALIAS_WASM.containsKey(functionName);}

    public void generateSetToTarget(){
        // If is
//        List<Instruction> result = new List<>();
        String functionName = callName;

        if(targets.size() == 0){
            // Find out whether the function returns a result, if it does drop the target
            if(hasReturn()){
                result.addInstruction(new Drop());
            }
        }else if(targets.size() == 1){
            String target = ((NameExpr)targets.getChild(0)).getName().getID();
            if(isScalarOutput()){
                result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalF64(target))));
                return;
            }

            BasicMatrixValue bmv = getBasicMatrixValue(analysis, node,
                    target);

            if(bmv.hasShape() && bmv.getShape().isScalar() &&
                    Builtin.getInstance(functionName)!= null){
                String temp = Util.genTypedLocalI32();
                result.addLocal(new TypeUse(new Opt<>(new Identifier(temp)),new I32()));
                result.addInstruction(new SetLocal(new Idx(temp)));
                result.addInstructions(MatWablyArray.getArrayIndexF64(temp, 0));
                String typedTarget = Util.getTypedLocalF64(target);
                result.addInstruction(new SetLocal(new Idx(new Opt<>(new Identifier(typedTarget)),0)));
            }else{
                result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalI32(target))));
            }
        }else{
            String targetCall = Util.genTypedLocalI32();
            result.addLocal(new TypeUse(targetCall,new I32()));
            result.addInstruction(new SetLocal(new Idx(targetCall)));
            int i = 0;
            if(returnsScalarVector()) {
                for (ast.Expr expr : targets) {
                    NameExpr exprNamed = (NameExpr) expr;
                    result.addInstruction(new GetLocal(new Idx(targetCall)));
                    result.addInstruction(new ConstLiteral(new I32(), i));
                    result.addInstruction(new Call(new Idx(new Opt<>(new Identifier("get_array_index_f64_no_check")), 0)));
                    String arg_f64 = Util.getTypedLocalF64(exprNamed.getName().getID());
                    result.addInstruction(new SetLocal(new Idx(arg_f64)));
                    i++;
                }
            }else {
                for (ast.Expr expr : targets) {
                    NameExpr exprNamed = (NameExpr) expr;
                    result.addInstruction(new GetLocal(new Idx(targetCall)));
                    result.addInstruction(new ConstLiteral(new I32(), i));
                    result.addInstruction(new Call(new Idx(new Opt<>(new Identifier("get_array_index_i32_no_check")), 0)));
                    BasicMatrixValue bmv = getBasicMatrixValue(analysis, node,
                            exprNamed.getName().getID());
                    String arg_typed = Util.getTypedLocalI32(exprNamed.getName().getID());
                    if (bmv.hasShape() && bmv.getShape().isScalar()) {
                        result.addInstruction(new ConstLiteral(new I32(), 0));
                        result.addInstruction(new Call(new Idx(new Opt<>(new Identifier("get_array_index_f64_no_check")), 0)));
                        arg_typed = Util.getTypedLocalF64(exprNamed.getName().getID());
                    }
                    result.addInstruction(new SetLocal(new Idx(arg_typed)));
                    i++;
                }
            }
        }
    }



    private static boolean isSpecialized(String name){
        return Arrays.binarySearch(SPECIALIZED, name, Comparator.naturalOrder()) >=0;
    }
    private boolean isScalarOutput(){
        // Implemented functions that return scalars
        if(Arrays.binarySearch(SCALAR_OUTPUT, generatedCallName, Comparator.naturalOrder()) >=0 ) return true;

        // Specialized functions are SS
        if(isSpecialized(callName)){
            for(Expr exp: arguments){
                ast.NameExpr nameExpr = (ast.NameExpr) exp;
                BasicMatrixValue bmv = getBasicMatrixValue(analysis, node, nameExpr.getName().getID());
                if (bmv.hasShape() && !bmv.getShape().isScalar()) return false;
            }
            return true;
        }
        // User defined functions that return scalars.
        return userDefinedReturnsScalar();
    }
    private boolean userDefinedReturnsScalar(){
        for (int i = 0; i < programAnalysis.getNodeList().size(); i++) {
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis =
                    programAnalysis.getNodeList().get(i).getAnalysis();
            ast.Function func = funcAnalysis.getTree();
            if(func.getName().getID().equals(callName)&&func.getOutputParamList().getNumChild() == 1) {
                BasicMatrixValue val = Util.getBasicMatrixValue(funcAnalysis,(TIRNode) func,
                        func.getOutputParamList().getChild(0).getID());
                if(val.hasShape())
                    return val.getShape().isScalar();
                else {
                    throw new Error("Must have ValueAnalysis information for output parameters: "
                    + func.getOutputParamList().getChild(0).getID());
                }
            }
        }
        return false;
    }
    private  boolean returnsScalarVector(){
        if(Builtin.getInstance(callName)!=null
                &&Arrays.binarySearch(RETURNS_SCALAR_VECTOR, callName,
                Comparator.naturalOrder()) >=0) return true;
        else{
            for (int i = 0; i < programAnalysis.getNodeList().size(); i++) {
                IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis =
                        programAnalysis.getNodeList().get(i).getAnalysis();
                ast.Function func = funcAnalysis.getTree();
                if(func.getName().getID().equals(callName)) {
                    for(Name expr: func.getOutputParamList()){
                        BasicMatrixValue val = Util.getBasicMatrixValue(funcAnalysis,(TIRNode) func,
                                expr.getID());
                        if(!val.hasShape()||!val.getShape().isScalar()) return false;
                    }
                    return true;
                }
            }
        }

        return false;

    }
    private  boolean hasReturn(){
        if(Builtin.getInstance(callName)== null){
            // Must be a call to a user function
            for (int i = 0; i < programAnalysis.getNodeList().size(); i++) {
                IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis =
                        programAnalysis.getNodeList().get(i).getAnalysis();
                ast.Function func = funcAnalysis.getTree();
                if(func.getName().getID().equals(callName)) {
                    return  func.getOutputParamList().getNumChild() > 0;
                }
            }
        }
        return !(Arrays.binarySearch(DOES_NOT_HAVE_RETURN, callName, Comparator.naturalOrder()) >=0);
    }
}

