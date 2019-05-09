package matwably.code_generation.builtin.trial.properties;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Size extends MatWablyBuiltinGenerator {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Size(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by actual Builtin. Specifies whether the built-in expression returns boxed scalar.
     * Returns whether the expression always returns a matrix. i.e. whether the generated built-in call does
     * not have specialization for the scalar cases.
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        return true;
    }

    /**
     * Function used to query whether the builtin function returns void
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    public void generateExpression(){
        if(arguments.size() > 2 ) throw new MatWablyError.TooManyInputArguments(callName, node);
        if(arguments.size() == 0) throw  new MatWablyError.NotEnoughInputArguments(callName, node);

        // Getting the length of a particular dimension
        if(arguments.size() == 2){
            if(targets.size() > 1) throw new MatWablyError.TooManyOutputArguments(callName, node);
            Double dimArg = valueUtil.getDoubleConstant(arguments.getNameExpresion(1),node);
            if(dimArg!=null && dimArg<1) throw new MatWablyError.
                    DimensionArgumentMustBeAPositiveScalar(callName, node);
            if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
                if(dimArg!=null){
                    // generate 1 constant load
                    result.addInstruction(new ConstLiteral(new F64(),1));
                }else{
                    result.addInstructions(
                            expressionGenerator
                                    .genExpr(arguments.getNameExpresion(0),node));
                    result.addInstructions(
                            expressionGenerator
                                    .genExpr(arguments.getNameExpresion(1),node));
                    // Generate dynamic code
                    result.addInstruction(new Call(new Idx("size_SM")));
                }
            }
        }else{
            result.addInstructions(
                    expressionGenerator
                            .genExpr(arguments.getNameExpresion(0),node));
            System.out.println(targets.size());
            result.addInstruction(new ConstLiteral(new I32(), targets.size()));
            // TODO test wasm function
            if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
                result.addInstruction(new Call(new Idx("size_S")));
            }else{
                result.addInstruction(new Call(new Idx("size_M")));
            }
        }

    }

}
