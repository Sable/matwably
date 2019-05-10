package matwably.code_generation.builtin.trial.input_generation;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.code_generation.ExpressionGenerator;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class AbstractInputGenerator {
    ExpressionGenerator name_expression_generator;
    ASTNode stmt;
    TIRCommaSeparatedList arguments;
    TIRCommaSeparatedList targets;
    MatWablyFunctionInformation matwably_analysis_set;
    MatWablyBuiltinGeneratorResult result;
    ValueAnalysisUtil valueAnalysisUtil;
     boolean isInForm(){
         return false;
     }
    abstract void generate();

    public ASTNode getStmt() {
        return stmt;
    }

    public AbstractInputGenerator(ASTNode node, TIRCommaSeparatedList args, TIRCommaSeparatedList targets,
                               MatWablyFunctionInformation functionInformation,
                               MatWablyBuiltinGeneratorResult result){
        this.stmt = node;
        this.arguments = args;
        this.targets = targets;
        this.matwably_analysis_set = functionInformation;
        this.valueAnalysisUtil = functionInformation.getValueAnalysisUtil();
        this.name_expression_generator = functionInformation.getExpressionGenerator();
        this.result = result;
    }
}
