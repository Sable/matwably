package matwably.code_generation.builtin.trial.constructors;

import ast.ASTNode;
import ast.NameExpr;
import matwably.code_generation.MatWablyError;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.ast.Call;
import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import matwably.ast.Idx;
import matwably.code_generation.builtin.trial.HasMatWablyBuiltinAnalysis;
import matwably.code_generation.builtin.trial.MatWablyBuiltinGenerator;
import matwably.code_generation.builtin.trial.input_generation.VectorInputGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

public class Colon extends MatWablyBuiltinGenerator implements HasMatWablyBuiltinAnalysis {
    // For later use when generating subsref
    private boolean is_scalar = false;
    private int low = 0;
    private int step = 1;
    private int high = 0;
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
     * This function performs analysis over inputs, and sets the limits if statically possible. This is later use
     * to efficiently generate `subsasgn` and ` subsref`
     * TODO: Implement analyze for this function
     */
    public void analyze(){

    }

    /**
     * Function used to query whether the builtin function returns void
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a expression returns something.
     *
     * At this point we assume that the targets are larger than 0, since this is a pure function, if it
     * was not true, we would not generate anything {@link MatWablyBuiltinGenerator#generate()}
     * ValueAnalysis will tell cases where the output is larger than 0. If the output is a scalar.
     * If valueAnalysis says is scalar and we know low to be a constant, we get that value and
     * return.
     * If we know the result is empty, we generate an empty vector
     * colon(2,2,2) if we know the values for low and we know is a scalar, we generate the scalar
     * colon() if
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }
    public void generateExpression(){
        // Throw static errors when possible
        if(arguments.size() < 2) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size() > 3) throw new MatWablyError.TooManyInputArguments(callName,node);
        if(targets.size() > 1) throw new MatWablyError.TooManyOutputArguments(callName, node);

        // targets must be larger than
        Shape targetShape = valueUtil.getShape(targets.getName(0).getID(),node,false);
        if(targetShape!=null) {
            if (targetShape.isScalar()) {
                // Use low value to generate scalar value
                Double val = valueUtil.getDoubleConstant(arguments.getNameExpresion(0),node);
                if(val!=null){
                    result.addInstruction(new ConstLiteral(new F64(), val));
                    return;
                }
            }
        }
        if(targetShape == null || !targetShape.isConstant()||(targetShape.isConstant()
                &&targetShape.getHowManyElements(0)>0)){
            // Check if all arguments are scalar, if they are generate them without boxing the scalar.
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
                VectorInputGenerator vectorInputGenerator = new VectorInputGenerator(node,arguments, targets,matwably_analysis_set,result);
                vectorInputGenerator.generate();
                result.addInstruction(new Call(new Idx("colon")));
            }
        }else{
            // Generate empty vector since we know for a fact that the number of elements returned is 0
            result.addInstructions(MatWablyArray.createI32Vector(0));
        }
    }
}
