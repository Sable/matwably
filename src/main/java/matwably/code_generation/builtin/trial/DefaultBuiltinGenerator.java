package matwably.code_generation.builtin.trial;

import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.NameExpressionGenerator;
import matwably.code_generation.builtin.ResultWasmGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.Util;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import static matwably.util.Util.getBasicMatrixValue;

public class DefaultBuiltinGenerator extends McLabBuiltinGenerator<ResultWasmGenerator> {

    String generatedCallName;
    NameExpressionGenerator nameExpressionGenerator;

    // booleans for the type of call
    boolean isSpecialized = false;

    public boolean hasAlias(){
        return false;
    }

    public boolean isSpecialized() {
        return isSpecialized;
    }

    @Override
    public boolean isScalarOutput() {
        return false;
    }

    /**
     *
     * @param node
     * @param arguments
     * @param targs
     * @param callName
     * @param analysis
     * @param functionQuery
     * @param nameExpressionGenerator
     */
    public DefaultBuiltinGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName,
                                   IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
                                   InterproceduralFunctionQuery functionQuery,
                                    NameExpressionGenerator nameExpressionGenerator) {
        super(node, arguments,targs,callName, analysis, functionQuery);
        this.result = new ResultWasmGenerator();
        this.nameExpressionGenerator = nameExpressionGenerator;
        this.generatedCallName = this.callName;


    }

    @Override
    void generateInputs() {
        arguments.forEach((ast.Expr arg)->
                result.addInstructions(this.nameExpressionGenerator.genNameExpr(((NameExpr) arg),this.node)));
    }

    @Override
    public boolean isInputInCanonicalForm() {
        return true;
    }

    @Override
    void generateCall() {
        // Call Function
        if(hasAlias())
            generatedCallName = getAlias() ;

        if(isMatlabBuiltin() || isSpecialized()){
            StringBuilder acc = new StringBuilder(generatedCallName);
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
    public String  getAlias() {
        return generatedCallName;
    }
    @Override
    void generateSetToTarget() {
        String functionName = callName;

        if(targets.size() == 0){
            // Find out whether the function returns a result, if it does drop the target
            if(this.returnsVoid()){
                result.addInstruction(new Drop());
            }
        }else if(targets.size() == 1){
            String target = ((NameExpr)targets.getChild(0)).getName().getID();
            if(isScalarOutput()){
                result.addInstruction(new SetLocal(new Idx(Util.getTypedLocalF64(target))));
            }else if(returnsBoxedScalar()){
                String temp = Util.genTypedLocalI32();
                result.addLocal(new TypeUse(new Opt<>(new Identifier(temp)),new I32()));
                result.addInstruction(new SetLocal(new Idx(temp)));
                result.addInstructions(MatWablyArray.getArrayIndexF64(temp, 0));
                String typedTarget = Util.getTypedLocalF64(target);
                result.addInstruction(new SetLocal(new Idx(new Opt<>(new Identifier(typedTarget)),0)));
            }else{
                // returns matrix
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
                    BasicMatrixValue bmv = getBasicMatrixValue(functionAnalysis , node,
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
    public boolean returnsBoxedScalar(){
        return false;
    }
    public boolean returnsScalarVector() {
        //TODO Implement this
        return true;
    }
}
