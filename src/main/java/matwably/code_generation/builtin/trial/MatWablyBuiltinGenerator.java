package matwably.code_generation.builtin.trial;


import ast.Expr;
import ast.NameExpr;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.NameExpressionGenerator;
import matwably.code_generation.builtin.ResultWasmGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.Util;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import static matwably.util.Util.getBasicMatrixValue;

/**
 * This class serves
 */
public abstract class MatWablyBuiltinGenerator extends McLabBuiltinGenerator<ResultWasmGenerator> {
    protected MatWablyFunctionInformation matwably_analysis_set;
    protected ValueAnalysisUtil valueUtil;
    protected NameExpressionGenerator nameExpressionGenerator;
    protected InterproceduralFunctionQuery functionQuery;
    // booleans for the type of call
    private boolean isSpecialized = false;

    /**
     *  Returns whether the function is specialized for re-naming purposes.
     *  If we implement classes for all the generated built-in this function will get deprecated.
     *  The default is that it isn't
     * @return Returns whether the function is specialized.
     */
    public boolean isSpecialized() {
        return isSpecialized;
    }


    /**
     * Constructor for class MatWablyBuiltinGenerator
     * @param node TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs Targets for the call
     * @param callName Original Matlab call name
     * @param analyses Set of MatWably analyses.
     */
    public MatWablyBuiltinGenerator(ast.ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName,
                                    MatWablyFunctionInformation analyses) {
        super(node, arguments,targs,callName, analyses.getFunctionAnalysis(), analyses.getFunctionQuery());
        this.result = new ResultWasmGenerator();
        this.nameExpressionGenerator = analyses.getNameExpressionGenerator();
        this.generatedCallName = this.callName;
        this.matwably_analysis_set = analyses;
        this.valueUtil = analyses.getValueAnalysisUtil();
        this.functionQuery = analyses.getFunctionQuery();
    }
    /**
     * Default generate input as it comes.
     */
    @Override
    public void generateInputs() {
        arguments.forEach((ast.Expr arg)->
                result.addInstructions(this.nameExpressionGenerator.genNameExpr(((NameExpr) arg),this.node)));
    }
    /**
     * Generator function, it only generates call if the call is not pure or there is more than one target.
     */
    public void generate() {
        if (isPure() && returnsZeroTargets()) return;
        if(functionQuery.isUserDefinedFunction(this.callName)){
            super.generateExpression();
        }else{
            generateExpression();
        }
        generateSetToTarget();

    }
    /**
     * Determines whether the
     */
    @Override
    public void generateCall() {

        String generatedFunctionName =  getGeneratedBuiltinName();

        if(isSpecialized()||functionQuery.isUserDefinedFunction(this.callName)){
            StringBuilder acc = new StringBuilder(generatedFunctionName);
            acc.append("_");
            arguments.stream()
                    .forEach(( ast.Expr argExpr)->{
                        ast.NameExpr nameExpr = (ast.NameExpr) argExpr;
                        BasicMatrixValue bmv = getBasicMatrixValue(functionAnalysis, node,
                                nameExpr.getName().getID());
                        if (bmv.hasShape() && bmv.getShape().isScalar())
                            acc.append("S");
                        else
                            acc.append("M");
            });
            generatedCallName = acc.toString();
        }

        result.addInstruction(new Call(
                new Idx(new Opt<>(new Identifier(generatedCallName)),0)));
    }

    /**
     * Generation strategy for setting to target.
     * 1. If zero targets and the expression does not return void. Add the drop instruction.
     * 2. If one target:
     *  a. If is a known scalar, simply set the scalar
     *  b. If is a boxed scalar, simply get scalar from generated expression.
     *  c. If is a matrix that may or may not be scalar, set it as an i32.
     * 3. If is a multi-target expression:
     *  a. If is an scalar vector, use `get_array_index_f64` directly.
     *  b. If is a combination: (i) if scalar, unbox, (ii) if vector, simply set.
     */
    @Override
    void generateSetToTarget() {
        // Find out whether the function returns a result, if it does drop the target
        if(returnsZeroTargets()&&!expressionReturnsVoid()){
            result.addInstruction(new Drop());
        }else if(returnsSingleTarget()){

            String targetName = targets.getNameExpresion(0).getVarName();
            if(returnsScalar()){
                if(returnsBoxedScalar()){
                    // If is boxed scalar, get from the expression generated by the built-in
                    result.addInstructions(MatWablyArray.getArrayIndexF64( 0));
                    result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalF64(targetName))));
                }else{
                    //  If is a scalar simple
                    result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalF64(targetName))));
                }
            }else{
                // returns matrix
                result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalI32(targetName))));
            }
        }else if(returnsList()){
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
                // Last case, combination between boxed scalars and vectors. If we can verify that the function does
                // note return a boxed scalar, we simply return.
                for (ast.Expr expr : targets) {
                    NameExpr exprNamed = (NameExpr) expr;
                    result.addInstruction(new GetLocal(new Idx(targetCall)));
                    result.addInstruction(new ConstLiteral(new I32(), i));
                    result.addInstruction(new Call(new Idx(new Opt<>(new Identifier("get_array_index_i32_no_check")), 0)));
                    BasicMatrixValue bmv = getBasicMatrixValue(functionAnalysis , node,
                            exprNamed.getName().getID());
                    String arg_typed = Util.getTypedLocalI32(exprNamed.getName().getID());
                    // If
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

    /**
     * Returns whether the return value is known to be a scalar, and the targets is 1.
     * @return
     */
    public boolean returnsScalar(){
        return targets.size() == 1 && valueUtil.isScalar(targets.getName(0).getID(),node, false);
    }

    /**
     * This are for cases where, through ValueAnalysis, the built-in
     * returns a scalar, but in our code-generation, we know the actual MatWably built-in does not return one.
     * In the future this could be avoided by having specializations for the returning scalar cases in the library.
     * Examples of these are:
     * e.g. sum(), prod(), mean() etc. if we know so statically
     * we can unbox it.
     * @return Whether the built-in expression returns boxed scalar.
     */
    private  boolean returnsBoxedScalar(){
        return returnsScalar() && !expressionHasSpecializationForScalar();
    }

    /**
     * To be implemented by actual Builtin. Specifies whether the built-in expression returns boxed scalar.
     * Returns whether the expression always returns a matrix. i.e. whether the generated built-in call does
     * not have specialization for the scalar cases.
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    public abstract boolean expressionHasSpecializationForScalar();
    /**
     * Returns a vector of scalars values, that we know statically. Size() is an example of this
     * Note that this states whether it returns a scalarVector for a fact, not whether it is possible
     * to return a scalarVector, it may still be possible. In this case, we have a boxed scalar that cannot
     * be recognized statically.
     * @return
     */
    public boolean returnsScalarVector() {
        // At this level, we check value analysis for this.
        return this.targets.stream().allMatch((Expr expr)->{
            BasicMatrixValue val = Util.getBasicMatrixValue(functionAnalysis, node, ((NameExpr)expr).getName().getID());
            return val.hasShape() && val.getShape().isScalar();
        });
    }
    /**
     * Returns a vector of scalars values, that we know statically. Size() is an example of this
     * @return
     */
    public boolean returnsMixedVectorAndScalar() {
        //TODO Implement this
        return true;
    }

    public boolean argumentsAreScalar(){
        return arguments.stream().allMatch((Expr expr)->
                        expr instanceof NameExpr &&
                                valueUtil.isScalar((NameExpr)expr, node));
    }

    /**
     * Returns whether the function is a built-in and the result is known to be logical. In MatWably
     * we currently only support logical scalars
     * TODO: Check this when we generalize library to more than just scalar logical built-ins.
     * @return Returns whether the underlying call results in a logical scalar
     */
    public boolean isLogical() {
        // Check for user defined, if it finds it, is not logical
        if(functionQuery.isUserDefinedFunction(this.callName)) return false;
        // Traverse through class hierarchy with reflection, to check for logical property
        Class<?> classObj = Builtin.getInstance(this.callName).getClass();
        while(classObj!= null){
            if(classObj.getName().contains("Logical")) return true;
            classObj = classObj.getSuperclass();
        }
        return false;
    }
}
