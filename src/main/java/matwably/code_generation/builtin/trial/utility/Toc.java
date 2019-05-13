package matwably.code_generation.builtin.trial.utility;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Toc  extends MatWablyBuiltinGenerator {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Toc(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
     *
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    @Override
    public boolean isSpecialized() {
        return false;
    }

    /**
     * Simply generate the call for toc
     * @return MatWablyBuiltinGeneratorResult generation result
     */
    public MatWablyBuiltinGeneratorResult generateExpression(){
        if(arguments.size() > 1) throw new MatWablyError.TooManyInputArguments(callName, node);
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        if(arguments.size() == 1){
            result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
            if(!valueUtil.isScalar(arguments.getNameExpresion(0), node,true)){
                result.addInstruction(new Call(new Idx("check_boxed_scalar")));
            }
        }
        result.addInstruction(new Call(new Idx("toc")));
        return result;
    }
}

