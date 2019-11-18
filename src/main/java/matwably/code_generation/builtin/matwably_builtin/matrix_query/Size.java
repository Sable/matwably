package matwably.code_generation.builtin.matwably_builtin.matrix_query;

import ast.ASTNode;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.matwably_builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;


public class Size extends MatrixQuery {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Size(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
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
     * a
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    public MatWablyBuiltinGeneratorResult generateExpression(){


        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        // Getting the length of a particular dimension
        if(arguments.size() == 2){
            Double dimArg = valueUtil.getDoubleConstant(arguments.getNameExpresion(1),node);
            if(dimArg!=null && dimArg < 1) throw new MatWablyError.
                    DimensionArgumentMustBeAPositiveScalar(callName, node);
            if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
                if(dimArg!=null){
                    // generateInstructions 1 constant load
                    result.addInstruction(new ConstLiteral(new F64(),1));
                }else{
                   result.add(generateInputs());
                    // Generate dynamic code
                    result.addInstruction(new Call(new Idx("size_SM")));
                }
            }else{
                if(dimArg != null){
                    Shape shape = valueUtil.getShape(arguments.getNameExpresion(0)
                            ,node,true);

                    if(shape != null && dimArg.intValue() > shape.getDimensions().size()){
                        result.addInstruction(new ConstLiteral(new F64(),1));
                    }else if(shape != null && dimArg.intValue() <= shape.getDimensions().size()
                        && shape.getDimensions().get(dimArg.intValue()-1)
                            .hasIntValue()){
                        result.addInstruction(new ConstLiteral(new F64(),
                                shape.getDimensions().get(dimArg.intValue()-1)
                                .getIntValue()));
                    }else{
                        result.add(generateInputs());
                        result.addInstruction(new Call(new Idx("size_MS")));
                    }
                }else if(valueUtil.isScalar(arguments.getNameExpresion(1),
                        node,
                        true)){
                    result.add(generateInputs());
                    result.addInstruction(new Call(new Idx("size_MS")));
                }else{
                    result.add(generateInputs());
                    result.addInstruction(new Call(new Idx("size_MM")));
                }
            }
        }else{
            result.addInstructions(
                    expressionGenerator
                            .genExpr(arguments.getNameExpresion(0),node));
            result.addInstruction(new ConstLiteral(new I32(), targets.size()));
            if(valueUtil.isScalar(arguments.getNameExpresion(0),node,true)){
                result.addInstruction(new Call(new Idx("size_S")));
            }else{
                result.addInstruction(new Call(new Idx("size_M")));
            }
        }
        return result;
    }

    @Override
    public void validateInput() {
        if(arguments.size() > 2 ) throw new MatWablyError.TooManyInputArguments(callName, node);
        if(arguments.size() == 0) throw  new MatWablyError.NotEnoughInputArguments(callName, node);
        if(arguments.size() == 2 &&
                targets.size() > 1) throw new MatWablyError.TooManyOutputArguments(callName, node);
    }

}
