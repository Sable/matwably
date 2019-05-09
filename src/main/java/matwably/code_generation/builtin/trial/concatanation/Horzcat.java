package matwably.code_generation.builtin.trial.concatanation;


import ast.ASTNode;
import ast.NameExpr;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.GetLocal;
import matwably.ast.Idx;
import matwably.code_generation.builtin.trial.input_generation.VectorInputGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Horzcat extends AbstractConcat {

    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Horzcat(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by actual Builtin. Specifies whether the built-in expression returns boxed scalar.
     * Returns whether the expression always returns a matrix. i.e. whether the generated built-in call does
     * not have specialization for the scalar cases.
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        return true;
    }

    /**
     * Generation method for horzcat function:
     * If no arguments, generate empty array.
     * If one or more and all scalars generate
     * If all arguments are known scalars, we may simply create a row vector
     */
    @Override
    public void generateExpression() {
        if(arguments.size()>0){
            if(arguments.size() == 1) {
                // Specialization for potential scalar
                result.addInstructions(expressionGenerator.genNameExpr(arguments.getNameExpresion(0),node));
            }else if(allArgumentsScalars()){
                String f64_input_vec = result.generateVectorInputF64(arguments.size(), true);
                int index = 0;
                for (NameExpr nameExpr : this.arguments.getNameExpressions()) {
                    result.addInstructions(
                            MatWablyArray.setArrayIndexF64(f64_input_vec,
                                    index,
                                    expressionGenerator.genNameExpr(nameExpr,node)));
                    index++;
                }
                result.addInstruction(new GetLocal(new Idx(f64_input_vec)));

            }else{
                VectorInputGenerator input_generator = new VectorInputGenerator(node, arguments, targets, matwably_analysis_set, result);
                input_generator.generate();
                result.addInstruction(new Call(new Idx("horzcat")));
            }
        }else{
            this.result.addInstructions(MatWablyArray.createEmptyArray(2));
        }
    }
}
