package matwably.code_generation.builtin.trial.matrix_operation;

import ast.ASTNode;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.trial.input_generation.VectorInputGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.toolkits.analysis.core.Def;

public class Isequal extends MatrixOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Isequal(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by actual Builtin. False if the built-in expression returns boxed scalar.
     * Returns whether the expression always returns a matrix. i.e. whether the generated built-in call does
     * not have specialization for the scalar cases.
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        return false;
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

    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        if(arguments.size() < 2) throw new MatWablyError.NotEnoughInputArguments(callName, node);
        MatWablyBuiltinGeneratorResult res= new MatWablyBuiltinGeneratorResult();
        if(arguments.size() == 2){
            res.addInstructions(expressionGenerator.genExpr(arguments.
                    getNameExpresion(0),node));
            res.addInstructions(expressionGenerator.genExpr(arguments.
                    getNameExpresion(2),node));
            res.addInstruction(new Call(new Idx("isequal_two")));
        }else{
            VectorInputGenerator inputHandler =
                    new VectorInputGenerator(node, arguments, targets,
                            matwably_analysis_set);
            res.add(inputHandler.generate());
            res.add(generateCall());
        }
        promoteIfAmbiguousLogical(res);
        return res;

    }
    protected void promoteIfAmbiguousLogical(MatWablyBuiltinGeneratorResult result) {
        if(disallow_logicals||!matwably_analysis_set.getLogicalVariableUtil().
                isDefinitionLogical(targets.getName(0).getID(), (Def)node)){
            result.addInstruction(new Convert(new F64(),new I32(),  true));
        }
    }
}
