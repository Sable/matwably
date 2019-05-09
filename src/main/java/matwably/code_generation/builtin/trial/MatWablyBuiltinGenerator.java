package matwably.code_generation.builtin.trial;


import ast.NameExpr;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.ExpressionGenerator;
import matwably.code_generation.builtin.ResultWasmGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.Util;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.toolkits.analysis.core.Def;

/**
 * This class serves
 */
public abstract class MatWablyBuiltinGenerator extends McLabBuiltinGenerator<ResultWasmGenerator> {
    protected final boolean disallow_logicals;
    protected MatWablyFunctionInformation matwably_analysis_set;
    protected ValueAnalysisUtil valueUtil;
    protected ExpressionGenerator expressionGenerator;
    private InterproceduralFunctionQuery functionQuery;
    // booleans for the type of call

    /**
     *  Returns whether the function is specialized for re-naming purposes.
     *  If we implement classes for all the generated built-in this function will get deprecated.
     *  The default is that it isn't
     * @return Returns whether the function is specialized.
     */
    public boolean isSpecialized() {
        return false;
    }

    @Override
    public boolean isMatlabBuiltin() {
        return super.isMatlabBuiltin() && !functionQuery.isUserDefinedFunction(callName);
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
        this.expressionGenerator = analyses.getExpressionGenerator();
        this.generatedCallName = this.callName;
        this.matwably_analysis_set = analyses;
        this.valueUtil = analyses.getValueAnalysisUtil();
        this.functionQuery = analyses.getFunctionQuery();
        this.disallow_logicals = analyses.getProgramOptions().disallow_logicals;
    }
    /**
     * Default generate input as it comes.
     */
    @Override
    public void generateInputs() {
        arguments.forEach((ast.Expr arg)->
                result.addInstructions(this.expressionGenerator.genNameExpr(((NameExpr) arg),this.node)));
    }

    /**
     * Generator function, it only generates call if the call is not pure or there is more than one target.
     */
    public void generate() {
        if (!functionQuery.isUserDefinedFunction(callName) && isPure()&& returnsZeroTargets()) return;
        generateExpression();
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
            arguments.getNameExpressions()
                    .forEach(( NameExpr argExpr)->{
                        if(valueUtil.isScalar(argExpr, node,true ))acc.append("S");
                        else acc.append("M");
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
     *  TODO: Throw error for user defined functions that try to access an unitialized variable
     *  TODO: Add logical targets
     */
    @Override
    void generateSetToTarget() {
        // Find out whether the function returns a result, if it does drop the target
        if(returnsZeroTargets()&&!expressionReturnsVoid()){
            if(!expressionHasSpecializationForScalar()){
                result.addInstructions(MatWablyArray.freeMachArray());
            }
            // Figure out whether expression returns a scalar
            result.addInstruction(new Drop());
        }else if(returnsSingleTarget()){

            String targetName = targets.getNameExpresion(0).getVarName();
            if(targetIsScalar()){
                // If is boxed scalar, get from the expression generated by the built-in
                if(targetIsBoxedScalar()){
                    String res_name = Util.genTypedLocalI32();
                    result.addInstructions(new TeeLocal(new Idx(res_name)));
                    result.addInstructions(MatWablyArray.getArrayIndexF64(0));
                    result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalF64(targetName))));
                    result.addInstructions(MatWablyArray.freeMachArray(res_name));
                }else{
                    //  If is a scalar simple
                    if(matwably_analysis_set.getLogicalVariableUtil().
                            isDefinitionLogical(targetName, (Def)node)){
                        result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalI32(targetName))));
                    }else{
                        result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalF64(targetName))));
                    }
                }
            }else{
                // returns matrix
                result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalI32(targetName))));
            }
        }else if(returnsList()){
            String targetCall = Util.genTypedLocalI32();
            result.addLocal(new TypeUse(targetCall,new I32()));
            result.addInstruction(new TeeLocal(new Idx(targetCall)));
            int i = 0;
            if(targetIsScalarVector()) {
                for(NameExpr nameExpr: targets.getNameExpressions()){
                    result.addInstructions(MatWablyArray.getArrayIndexF64(targetCall, i));
                    String arg_f64 = Util.getTypedLocalF64(nameExpr.getName().getID());
                    result.addInstruction(new SetLocal(new Idx(arg_f64)));
                    i++;
                }
            }else {
                // Last case, combination between boxed scalars and vectors. If we can verify that the function does
                // note return a boxed scalar, we simply return.
                for (NameExpr expr : targets.getNameExpressions()) {
                    result.addInstructions(MatWablyArray.getArrayIndexI32(targetCall, i));
                    if(valueUtil.isScalar(expr,node, false)){
                        String tmpI32 = Util.genTypedLocalI32();
                        result.addLocal(new TypeUse(tmpI32, new I32()));
                        String arg_typed = Util.getTypedLocalF64(expr.getName().getID());

                        result.addInstruction(new TeeLocal(new Idx(tmpI32)));
                        result.addInstructions(MatWablyArray.getArrayIndexF64(0));
                        result.addInstruction(new SetLocal(new Idx(arg_typed)));

                        result.addInstructions(MatWablyArray.freeMachArray(tmpI32));
                    }else{
                        String arg_typed = Util.getTypedLocalI32(expr.getName().getID());
                        result.addInstruction(new SetLocal(new Idx(arg_typed)));
                    }
                    i++;
                }
            }
            result.addInstructions(MatWablyArray.freeMachArray());
        }
    }

    /**
     * Returns whether the return value is known to be a scalar, and the targets is 1.
     * @return
     */
    public boolean targetIsScalar(){
        return targets.size() == 1
                && valueUtil.isScalar(targets.getName(0).getID(),node, false);
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
    private  boolean targetIsBoxedScalar(){
        return targetIsScalar() && !expressionHasSpecializationForScalar();
    }

    /**
     * To be implemented by actual Builtin. False if the built-in expression returns boxed scalar.
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
    public boolean targetIsScalarVector() {
        // At this level, we check value analysis for this.
        return this.targets.getNameExpressions().stream().
                allMatch((NameExpr expr)-> valueUtil.isScalar(expr.getName().getID(),node,false));
    }


    /**
     * Returns whether all the arguments are scalar
     * @return
     */
    public boolean argumentsAreScalar(){
        return arguments.getNameExpressions().stream().allMatch((NameExpr expr)->
                                valueUtil.isScalar(expr, node,true));
    }

}