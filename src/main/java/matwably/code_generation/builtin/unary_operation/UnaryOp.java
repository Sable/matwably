package matwably.code_generation.builtin.unary_operation;

import ast.ASTNode;
import matwably.ast.Call;
import matwably.ast.ConstLiteral;
import matwably.ast.I32;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class UnaryOp  extends MatWablyBuiltinGenerator {
    @Override
    public boolean isSpecialized() {
        return true;
    }

    /**
     * To be implemented by child classes, this function generates the scalar instruction or scalar call
     * e.g. if the operation is binary addition,
     * this function adds the instruction `f64.add` to the return object
     */
    public  MatWablyBuiltinGeneratorResult generateScalarCall(){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.addInstruction(new Call(new Idx(generatedCallName+"_SS")));
        return result;
    }

    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public UnaryOp(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
    public MatWablyBuiltinGeneratorResult generateExpression(){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();

        result.add(generateInputs());
        if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
            result.add(generateScalarCall());
        }else{
            result.addInstruction(new ConstLiteral(new I32(),0));
            result.add(generateCall());
        }
        return result;
    }

    @Override
    public void validateInput() {
        if(arguments.size()<1) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size()>1) throw new MatWablyError.TooManyInputArguments(callName,node);
    }
}
