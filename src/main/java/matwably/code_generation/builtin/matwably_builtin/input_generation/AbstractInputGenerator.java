package matwably.code_generation.builtin.matwably_builtin.input_generation;

import ast.ASTNode;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.ExpressionGenerator;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class AbstractInputGenerator {
    ExpressionGenerator name_expression_generator;
    ASTNode stmt;
    TIRCommaSeparatedList arguments;
    MatWablyFunctionInformation matwably_analysis_set;
    MatWablyBuiltinGeneratorResult result;
    ValueAnalysisUtil valueAnalysisUtil;
     boolean isInForm(){
         return false;
     }
    abstract MatWablyBuiltinGeneratorResult generate();

    public ASTNode getStmt() {
        return stmt;
    }

    public AbstractInputGenerator(ASTNode node, TIRCommaSeparatedList args,
                               MatWablyFunctionInformation functionInformation){
        this.stmt = node;
        this.arguments = args;
        this.matwably_analysis_set = functionInformation;
        this.valueAnalysisUtil = functionInformation.getValueAnalysisUtil();
        this.name_expression_generator = functionInformation.getExpressionGenerator();
    }
}
