package matwably.code_generation.builtin.trial.constructors;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.Idx;
import natlab.tame.tir.TIRCommaSeparatedList;

public class RandnGenerator extends ShapeConstructorGenerator {


    public RandnGenerator(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo) {
        super(node, arguments, targs, callName, functionInfo);
    }

    @Override
    public String get2DConstructorName() {
        return "randn_2D";
    }

    @Override
    public void generateScalarExpression() {
        result.addInstruction(new Call(new Idx("randn_S")));
    }


}
