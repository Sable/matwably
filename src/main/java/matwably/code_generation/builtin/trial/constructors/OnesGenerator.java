package matwably.code_generation.builtin.trial.constructors;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import natlab.tame.tir.TIRCommaSeparatedList;

public class OnesGenerator extends ShapeConstructorGenerator {


    public OnesGenerator(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo) {
        super(node, arguments, targs, callName, functionInfo);
    }

    @Override
    public String get2DConstructorName() {
        return "ones_2D";
    }

    @Override
    public void generateScalarExpression() {
        result.addInstruction(new ConstLiteral(new F64(), 1));
    }
}

