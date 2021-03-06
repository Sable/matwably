package matwably.code_generation.builtin.constructors;

import ast.ASTNode;
import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.input_generation.ShapeInputGenerator;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.components.shape.Shape;

/**
 * Class serves as a base class for shape constructors
 * Example: ones(), zeros(), randn(), randi(), rand(), eye()
 */
public abstract class ShapeConstructor extends Constructor {
    /**
     * Name for the constructor in 2D
     */
    private final String constructor2DName;

    /**
     * IShapeConstructor constructor method
     * @param node TIRNode to generateInstructions
     * @param arguments Arguments to node
     * @param targs Targets to call
     * @param callName Call name
     * @param functionInfo Series of MatWably Analysis
     */
    public ShapeConstructor(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation functionInfo){
        super(node, arguments, targs, callName, functionInfo);
        this.constructor2DName = this.callName+"_2D";
    }

    /**
     * Returns boolean indicating whether the expression returns something
     * @return Returns boolean indicating whether the expression returns SOMETHING
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }

    /**
     * Generates the call instructions to when constructor returns a scalar
     * @return A List of instructions for the scalar call
     */
    protected abstract MatWablyBuiltinGeneratorResult generateScalarExpression();

    /**
     *
     * To be implemented by actual Builtin. Specifies whether the built-in expression returns boxed scalar.
     * i.e. whether the generated built-in call does  not have specialization for the scalar cases. Saving by having
     * shape constructor
     *
     * @return Specifies whether the built-in expression returns boxed scalar.
     */
    @Override
    public boolean expressionHasSpecializationForScalar() {
        if( arguments.size() == 1 ){
            Double valueConstant = this.valueUtil.getDoubleConstant(this.arguments.
                    getNameExpresion(0),node);
            return (valueConstant != null && valueConstant == 1);
        }
        return arguments.size() == 0;
    }

    /**
     * Generation strategy.
     *  Input: ones(2,2), ones(2)
     *  Description: MatMachJS support library offers 2D specializations
     *       for the constructors, these functions take two arguments indicating the
     *       size of the dimensions, i.e. ones(2,2), results in a 2x2 matrix of ones.
     *
     *  Input: ones(), ones(1,1)
     *  Description: We in-line directly the ConstLiteral 1, no calls are made.
     *
     *  Input: ones(M), ones(2,2,2), ones(M1,M2)
     *  Description: We call the more general constructor in MatMachJS,
     *  these constructor creates a temporary vector to load the inputs.
     */
    public MatWablyBuiltinGeneratorResult generateExpression() {
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        int sizeArgs = arguments.size();
        if(sizeArgs == 0 ){
            result.add(generateScalarExpression());
        }else if(sizeArgs == 1){
            // Two cases, one where we have
            NameExpr nameExpr = arguments.getNameExpresion(0);
            if(this.valueUtil.isScalar(nameExpr, node,true)){
                Double valueConstant = this.valueUtil.getDoubleConstant(nameExpr,node);
                if(valueConstant != null){
                    if(valueConstant == 1){
                        return this.generateScalarExpression();
                    }
                    result.addInstructions(new ConstLiteral(new F64(),
                                valueConstant));
                    result.addInstructions(new ConstLiteral(new F64(),
                                valueConstant));
                }else{
                    // Since we are duplicating the arg, if its a complicated expr. We may use a local instead, to
                    // store the result of the computation once.
                    if(!expressionGenerator.isSimpleExpression(nameExpr.getName())){
                        String scalar_arg = result.generateF64Local();
                        result.addInstructions(
                                expressionGenerator.genNameExpr(nameExpr, node)
                                .add(new TeeLocal(new Idx(scalar_arg))));
                        result.addInstructions(new GetLocal(new Idx(scalar_arg)));
                    }else{
                        result.addInstructions(expressionGenerator.genNameExpr(nameExpr, node));
                        result.addInstructions(expressionGenerator.genNameExpr(nameExpr, node));
                    }
                }
                result.addInstruction(new Call(new Idx(this.constructor2DName)));

            }else { // Could add further error here for incorrect programs and invalid inputs.
                result.addInstructions( expressionGenerator.genNameExpr(nameExpr,node));
                result.add(this.generateCall());
            }
        }else if(sizeArgs == 2) {
            NameExpr arg1 = arguments.getNameExpresion(0);
            NameExpr arg2 = arguments.getNameExpresion(1);
            if(this.valueUtil.isScalar(arg1,node,true) &&
                    this.valueUtil.isScalar(arg2, node,true)){
                result.addInstructions(expressionGenerator.genNameExpr(arg1, node));
                result.addInstructions(expressionGenerator.genNameExpr(arg2, node));
            }else{ // We must have run-time boxed scalars
                if(this.valueUtil.isScalar(arg1,node,true)){
                    result.addInstructions(expressionGenerator.genNameExpr(arg1, node));
                    result.addInstructions(expressionGenerator.genNameExpr(arg2, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                }else if(this.valueUtil.isScalar(arg2,node,true)){
                    result.addInstructions(expressionGenerator.genNameExpr(arg1, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                    result.addInstructions(expressionGenerator.genNameExpr(arg2, node));
                }else{
                    result.addInstructions(expressionGenerator.genNameExpr(arg1, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                    result.addInstructions(expressionGenerator.genNameExpr(arg2, node));
                    result.addInstruction(new Call(new Idx("check_boxed_scalar_value")));
                }
            }
            result.addInstruction(new Call(new Idx(this.constructor2DName)));
        }else{
            ShapeInputGenerator shapeInputGenerator = new ShapeInputGenerator(node, arguments,
                     matwably_analysis_set);
            result.add(shapeInputGenerator.generate());
            result.add(this.generateCall());
        }

        return result;

    }

    @Override
    public void validateInput() {
        // Does nothing, could check that all the arguments,
        // if one, is a row vector, if more than one
        //all are scalars.
        if(arguments.size() == 1){
            Shape shape = valueUtil.getShape(arguments.getName(0).getID(),
                    node,true);
            if(shape != null){
                if(shape.getDimensions().size() > 0 &&
                        shape.getDimensions().get(0).
                                hasIntValue()&&shape.getDimensions().
                        get(0).getIntValue()>1) {
                    throw new MatWablyError.
                            DimensionArgumentMustBeARowVector(callName, node);
                }
                if( shape.getDimensions().size()>3 && shape.getDimensions()
                        .stream().skip(2)
                        .anyMatch((dim)-> dim.hasIntValue()
                                && dim.getIntValue()>1)){
                    throw new MatWablyError.
                            DimensionArgumentMustBeARowVector(callName, node);

                }
            }
        }
    }
}
