package matwably.code_generation.builtin.trial.constructors;

import ast.ASTNode;
import ast.NameExpr;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class ShapeConstructorGenerator extends MatWablyBuiltinGenerator implements ShapeConstructor {
    public ShapeConstructorGenerator(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo){
        super(node, arguments, targs, callName, functionInfo);
    }

    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    public abstract String get2DConstructorName();


    /**
     *
     * To be implemented by actual Builtin. Specifies whether the built-in expression returns boxed scalar.
     * i.e. whether the generated built-in call does  not have specialization for the scalar cases. Saving by having
     * shape constructor
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        return arguments.size() == 0;
    }

    /**
     * TODO Add generation explanation
     */
    public void generateExpression() {
        int sizeArgs = arguments.size();

        if(sizeArgs == 0 ){
            generateScalarExpression();
        }else if(sizeArgs == 1){
            // Two cases, one where we have
            NameExpr nameExpr = arguments.getNameExpresion(0);
            if(this.valueUtil.isScalar(nameExpr, node)){
                Double valueConstant = this.valueUtil.getDoubleConstant(nameExpr,node);
                if(valueConstant != null){
                    result.addInstructions(new ConstLiteral(new F64(), valueConstant));
                    result.addInstructions(new ConstLiteral(new F64(), valueConstant));
                }else{
                    if(!nameExpressionGenerator.isSimpleExpression(nameExpr,node)){
                        String scalar_arg = result.generateF64Local();
                        result.addInstructions(
                                nameExpressionGenerator.genNameExpr(nameExpr, node)
                                .add(new TeeLocal(new Idx(scalar_arg))));
                        result.addInstructions(new GetLocal(new Idx(scalar_arg)));
                    }else{
                        result.addInstructions(nameExpressionGenerator.genNameExpr(nameExpr, node));
                        result.addInstructions(nameExpressionGenerator.genNameExpr(nameExpr, node));
                    }
                }
                this.result.addInstruction(new Call(new Idx(this.get2DConstructorName())));

            }else { // Could add further error here for incorrect programs and invalid inputs.
                result.addInstructions( nameExpressionGenerator.genNameExpr(nameExpr,node));
                this.generateCall();
            }
        }else if(sizeArgs == 2) {
            NameExpr arg1 = arguments.getNameExpresion(0);
            NameExpr arg2 = arguments.getNameExpresion(1);
            if(this.valueUtil.isScalar(arg1,node) && this.valueUtil.isScalar(arg2, node)){
                result.addInstructions(nameExpressionGenerator.genNameExpr(arg1, node));
                result.addInstructions(nameExpressionGenerator.genNameExpr(arg2, node));
            }else{ // We must have run-time boxed scalars
                if(this.valueUtil.isScalar(arg1,node)){
                    result.addInstructions(nameExpressionGenerator.genNameExpr(arg1, node));
                    result.addInstructions(nameExpressionGenerator.genNameExpr(arg2, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                }else if(this.valueUtil.isScalar(arg2,node)){
                    result.addInstructions(nameExpressionGenerator.genNameExpr(arg1, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                    result.addInstructions(nameExpressionGenerator.genNameExpr(arg2, node));
                }else{
                    result.addInstructions(nameExpressionGenerator.genNameExpr(arg1, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                    result.addInstructions(nameExpressionGenerator.genNameExpr(arg2, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                }
            }
            this.result.addInstruction(new Call(new Idx(this.get2DConstructorName())));
        }else{
            String input_arg = result.generateVectorInputF64(sizeArgs,true);
            // There are more than two arguments
            int i = 0;
            for(ast.NameExpr argExpr: arguments.getNameExpressions()){
                if(this.valueUtil.isScalar(argExpr,node)){
                    result.addInstruction(new GetLocal(new Idx(input_arg)));
                    result.addInstruction(new ConstLiteral(new I32(), i));
                    result.addInstructions(nameExpressionGenerator.genNameExpr(argExpr,node));
                }else{
                    result.addInstructions(nameExpressionGenerator.genNameExpr(argExpr,node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                }
                result.addInstruction(new Call(new Idx("set_array_index_f64_no_check")));
                i++;
            }
            result.addInstruction(new GetLocal(new Idx(input_arg)));
            this.generateCall();
        }

    }
}
