package matwably.code_generation.builtin.matwably_builtin.binary_op;

import ast.ASTNode;
import matwably.ast.*;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.MatWablyBuiltinGenerator;
import matwably.util.Util;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

/**
 * Class is the parent generator class for Binary Operations in MatWably, the
 * Default of the class is to implement scalar expressions differently, as we can in-line a lot of the operations.
 * This behaviour could later be replaced by having a default function in-liner, in that case we would not need the
 * method {@link BinaryOp#generateScalarCall}
 *
 * In terms of binary operators, every binary operator has the following specializations:
 *
 * SS: scalar,scalar
 * SM: scalar,matrix
 * MS: matrix,scalar
 * MM: matrix,matrix -> (this one contains further ones i.e. (samesize, broadcasting)
 *
 *
 * Moreover, to all of them, we have added an optional output parameters $res_ptr
 * which if not null or zero, will be used to store the result of the operations.
 * This is useful in terms of saving allocations, e.g. a = a + b, in this case, if a,b have the same
 * shape we may reuse a's allocation site for the result.
 *
 */
public abstract class BinaryOp extends MatWablyBuiltinGenerator {
    @Override
    public boolean isSpecialized() {
        return true;
    }

    /**
     * To be implemented by child classes, this function generates the scalar instruction or scalar call
     * e.g. if the operation is binary addition,
     * this function adds the instruction `f64.add` to the return object
     */
    public abstract MatWablyBuiltinGeneratorResult generateScalarCall();
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public BinaryOp(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
     * Function used to query whether the builtin function returns void
     * This is to be replaced by ValueAnalysis ShapePropagation, once we have a good way to determine when
     * a function returns something or void
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    public MatWablyBuiltinGeneratorResult generateExpression(){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();

        boolean arg1IsScalar = valueUtil.isScalar(arguments.getNameExpresion(0),node,true);
        boolean arg2IsScalar = valueUtil.isScalar(arguments.getNameExpresion(1),node,true);
        Shape arg1Shape = valueUtil.getShape(arguments.getNameExpresion(0),node,true);
        Shape arg2Shape = valueUtil.getShape(arguments.getNameExpresion(1),node,true);

        result.add(generateInputs());
        if(arg1IsScalar && arg2IsScalar){
            result.add(generateScalarCall());
        }else{

            // Generate extra res_ptr for matrix results
            Shape targetRHS = valueUtil.getShape(targets.getName(0).getID(),
                    node, true);
            Shape targetLHS = valueUtil.getShape(targets.getName(0).getID(),
                    node, false);
            if( this.matwably_analysis_set.getProgramOptions().
                    opt_builtin_alloc_sites &&
                    targetRHS !=null &&
                    targetLHS != null &&
                    targetLHS.isConstantAndEquals(targetRHS)){
                result.addInstruction(new GetLocal(
                        new Idx(Util.getTypedLocalI32(targets.getName(0)
                                .getID()))));
            }else{
                result.addInstruction(new ConstLiteral(new I32(),0));
            }
            // Lets try to generateInstructions same size, broadcasting, SM, MS specializations when possible.
            if(arg1IsScalar){
                result.addInstruction(new Call(new Idx(generatedCallName+"_SM")));
            }else if(arg2IsScalar){
                result.addInstruction(new Call(new Idx(generatedCallName+"_MS")));
            }else if(arg1Shape != null && arg2Shape!=null){
                int inputsAreBroadcastable = arg1Shape.isCompatible(arg2Shape);
                if(arg1Shape.equals(arg2Shape)){
                    // generateInstructions call for same shape
                    result.addInstruction(new Call(new Idx(generatedCallName+"_MM_samesize")));
                }else if(inputsAreBroadcastable == 1){
                    result.addInstruction(new Call(new Idx(generatedCallName+"_MM_broadcasting")));
                }else if(inputsAreBroadcastable == 0){
                    throw new MatWablyError.MatrixDimensionsMustAgree(generatedCallName, node);
                }else{
                    result.addInstruction(new Call(new Idx(generatedCallName+"_MM")));
                }
            }else{
                result.addInstruction(new Call(new Idx(generatedCallName+"_MM")));
            }
        }
        return result;
    }

    @Override
    public void validateInput() {
        if(arguments.size()<2) throw new MatWablyError.NotEnoughInputArguments(callName,node);
        if(arguments.size()>2) throw new MatWablyError.TooManyInputArguments(callName,node);
    }
}
