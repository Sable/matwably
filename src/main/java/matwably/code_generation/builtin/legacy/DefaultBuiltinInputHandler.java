package matwably.code_generation.builtin.legacy;

import ast.ASTNode;
import ast.NameExpr;
import matwably.ast.GetLocal;
import matwably.ast.Idx;
import matwably.util.Util;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class DefaultBuiltinInputHandler extends BuiltinInputHandler {

    public DefaultBuiltinInputHandler(TIRNode stmt, TIRCommaSeparatedList args,
                                       IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> valueAnalysis
        ,MatWablyBuiltinGeneratorResult result)
    {
        this.arguments = args;
        this.stmt = stmt;
        this.valueAnalysis = valueAnalysis;
        this.result = result;
    }
    public static BuiltinInputHandler getInstance(TIRNode stmt, TIRCommaSeparatedList args,
                                              IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> valueAnalysis
    , MatWablyBuiltinGeneratorResult result) {

            return new DefaultBuiltinInputHandler(stmt,args,valueAnalysis, result);
    }

    @Override
    boolean isInForm() {
        return false;
    }

    @Override
    public void generate() {
        for (ast.Expr arg : arguments) {
            String name = ((NameExpr) arg).getName().getID();
            BasicMatrixValue val = Util.getBasicMatrixValue(valueAnalysis, (ASTNode) stmt, name);

            if(!val.hasShape()){
                throw new Error("This does not have shape");
            }
            name = Util.getTypedName(name, val);
            result.addInstruction(new GetLocal(new Idx(name)));
        }
    }
}