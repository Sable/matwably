package matwably.code_generation.builtin.concatanation;


import ast.ASTNode;
import ast.NameExpr;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.GetLocal;
import matwably.ast.Idx;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.input_generation.VectorInputGenerator;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Horzcat extends Concat {

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
     * If no arguments, generateInstructions empty array.
     * If one or more and all scalars generateInstructions
     * If all arguments are known scalars, we may simply create a row vector
     */
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        if(arguments.size()>0){
            if(arguments.size() == 1) {
                result.addInstructions(expressionGenerator.genNameExpr(
                        arguments.getNameExpresion(0),node));
            }else if(allArgumentsScalars()){
                // Specialization for potential scalar
                String f64_input_vec = result.generateI32Local();
                result.addInstructions(MachArrayIndexing.createF64Vector(arguments.size(), f64_input_vec));
                int index = 0;
                for (NameExpr nameExpr : this.arguments.getNameExpressions()) {
                    result.addInstructions(
                            MachArrayIndexing.setArrayIndexF64NoCheck(f64_input_vec,
                                    index,
                                    expressionGenerator.genNameExpr(nameExpr,node)));
                    index++;
                }
                result.addInstruction(new GetLocal(new Idx(f64_input_vec)));
            }else{
                VectorInputGenerator input_generator = new VectorInputGenerator(node, arguments,  matwably_analysis_set);
                result.add(input_generator.generate());
                result.addInstruction(new Call(new Idx("horzcat")));
            }
        }else{
            result.addInstructions(MachArrayIndexing.createEmptyArray(2));
        }
        return result;
    }
}
