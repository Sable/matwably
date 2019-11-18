package matwably.code_generation.builtin.matwably_builtin.constructors;

import ast.ASTNode;
import ast.NameExpr;
import matwably.ast.Call;
import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import matwably.ast.Idx;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.matwably_builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.MatWablyBuiltinGenerator;
import matwably.code_generation.builtin.matwably_builtin.input_generation.VectorInputGenerator;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

public class Colon extends Constructor {


    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Colon(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
        Shape targetShape = valueUtil.getShape(targets.getName(0).getID(),node,false);
        return targetShape!=null && targetShape.isScalar() &&
                valueUtil.getDoubleConstant(arguments.getNameExpresion(0),node)!=null;
    }

    /**
     * Function used to query whether the builtin function returns void
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a expression returns something.
     *
     * At this point we assume that the targets are larger than 0, since this is a pure function, if it
     * was not true, we would not generateInstructions anything {@link MatWablyBuiltinGenerator#generate()}
     * ValueAnalysis will tell cases where the output is larger than 0. If the output is a scalar.
     * If valueAnalysis says is scalar and we know low to be a constant, we get that value and
     * return.
     * If we know the result is empty, we generateInstructions an empty vector
     * colon(2,2,2) if we know the values for low and we know is a scalar, we generateInstructions the scalar
     * colon() if
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }
    protected MatWablyBuiltinGeneratorResult generateExpression(){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        // targets must be larger than
        Shape targetShape = valueUtil.getShape(targets
                .getNameExpresion(0)
                .getName()
                .getID(),node,false);

        if(targetShape!=null) {
            if (targetShape.isScalar()) {
                // Use low value to generateInstructions scalar value
                Double val = valueUtil.getDoubleConstant(arguments.getNameExpresion(0),node);
                if(val!=null){
                    result.addInstruction(new ConstLiteral(new F64(), val));
                    return result;
                }
            }
        }
        if(targetShape == null || !targetShape.isConstant() || targetShape.getHowManyElements(0) > 0){
            // Check if all arguments are scalar, if they are generateInstructions them without boxing the scalar.
            if(argumentsAreScalar()){
                // Input args
                for(NameExpr arg: arguments.getNameExpressions())
                    result.addInstructions(expressionGenerator.genNameExpr(arg, node));
                // Generate appropriate specialization
                if(arguments.size() == 2){
                    result.addInstruction(new Call(new Idx("colon_two")));
                }else {
                    result.addInstruction(new Call(new Idx("colon_three")));
                }
            }else{
                // We call a general implementation when we have to give up
                VectorInputGenerator vectorInputGenerator = new VectorInputGenerator(node,arguments,matwably_analysis_set);
                result.add(vectorInputGenerator.generate());
                result.addInstruction(new Call(new Idx("colon")));
            }
        }else{
            // Generate empty vector since we know for a fact that the number of elements returned is 0
            result.addInstructions(MachArrayIndexing.createF64Vector(0));
        }
        return result;
    }

    @Override
    public void validateInput() {
        // Throw static errors when possible
        if(arguments.size() < 2) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size() > 3) throw new MatWablyError.TooManyInputArguments(callName,node);
        if(targets.size() > 1) throw new MatWablyError.TooManyOutputArguments(callName, node);
    }

}
