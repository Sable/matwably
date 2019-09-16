package matwably.code_generation.builtin.matwably_builtin.constructors;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Randn extends ShapeConstructor {


    public Randn(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo) {
        super(node, arguments, targs, callName, functionInfo);
    }

    @Override
    public MatWablyBuiltinGeneratorResult generateScalarExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.addInstruction(new Call(new Idx("randn_S")));
        return result;
    }


}
