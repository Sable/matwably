package matwably.code_generation.builtin.trial.input_generation;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.code_generation.NameExpressionGenerator;
import matwably.code_generation.builtin.ResultWasmGenerator;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRCommaSeparatedList;

public abstract class AbstractInputGenerator {
    NameExpressionGenerator name_expression_generator;
    ASTNode stmt;
    TIRCommaSeparatedList arguments;
    TIRCommaSeparatedList targets;
    MatWablyFunctionInformation matwably_analysis_set;
    ResultWasmGenerator result;
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
                               ResultWasmGenerator result){
        this.stmt = node;
        this.arguments = args;
        this.targets = targets;
        this.matwably_analysis_set = functionInformation;
        this.valueAnalysisUtil = functionInformation.getValueAnalysisUtil();
        this.name_expression_generator = functionInformation.getNameExpressionGenerator();
        this.result = result;
    }
}
