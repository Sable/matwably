package matwably.code_generation.builtin.matwably_builtin.constructors;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Eye extends ShapeConstructor {


    public Eye(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo) {
        super(node, arguments, targs, callName, functionInfo);
    }


    @Override
    public MatWablyBuiltinGeneratorResult generateScalarExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.addInstruction(new ConstLiteral(new F64(), 1));
        return result;
    }
}
