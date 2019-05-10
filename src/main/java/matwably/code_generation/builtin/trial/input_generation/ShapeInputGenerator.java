package matwably.code_generation.builtin.trial.input_generation;

import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.*;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class ShapeInputGenerator extends AbstractInputGenerator {

    public ShapeInputGenerator(ast.ASTNode node, TIRCommaSeparatedList args, TIRCommaSeparatedList targets, MatWablyFunctionInformation functionInformation, MatWablyBuiltinGeneratorResult result) {
        super(node, args, targets, functionInformation, result);
    }

    /**
     * Turns scalars into vectors to represent a shape
     */
    @Override
    public void generate() {
        String input_arg = result.generateVectorInputF64(arguments.size(),true);
        // There are more than two arguments
        int i = 0;
        for(ast.NameExpr argExpr: arguments.getNameExpressions()){
            if(this.valueAnalysisUtil.isScalar(argExpr,stmt,true)){
                result.addInstruction(new GetLocal(new Idx(input_arg)));
                result.addInstruction(new ConstLiteral(new I32(), i));
                result.addInstructions(name_expression_generator.genNameExpr(argExpr,stmt));
            }else{
                result.addInstructions(name_expression_generator.genNameExpr(argExpr,stmt));
                result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
            }
            result.addInstruction(new Call(new Idx("set_array_index_f64_no_check")));
            i++;
        }
        result.addInstruction(new GetLocal(new Idx(input_arg)));
    }
}
