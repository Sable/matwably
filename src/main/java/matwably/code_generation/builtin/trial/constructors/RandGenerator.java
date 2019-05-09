package matwably.code_generation.builtin.trial.constructors;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import natlab.tame.tir.TIRCommaSeparatedList;

public class RandGenerator extends ShapeConstructorGenerator {

    public RandGenerator(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo) {
        super(node, arguments, targs, callName, functionInfo);
    }

    @Override
    public String get2DConstructorName() {
        return "rand_2D";
    }

    @Override
    public void generateScalarExpression() {
        result.addInstruction(new Call(new Idx("rand_S")));
    }
}