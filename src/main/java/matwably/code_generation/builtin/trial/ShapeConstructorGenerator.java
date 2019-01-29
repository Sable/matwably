package matwably.code_generation.builtin.trial;

import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.NameExpressionGenerator;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.Util;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;

public abstract class ShapeConstructorGenerator extends DefaultBuiltinGenerator {

    /**
     * @param node TIRNode for the call
     * @param arguments Arguments for the call
     * @param targs Target names for the call
     * @param callName Actual function name
     * @param analysis IntraproceduralValueAnalysis
     * @param functionQuery InterproceduralFunctionQuery
     * @param nameExpressionGenerator NameExpressionGenerator
     */
    public ShapeConstructorGenerator(TIRNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, InterproceduralFunctionQuery functionQuery, NameExpressionGenerator nameExpressionGenerator) {
        super(node, arguments, targs, callName, analysis, functionQuery, nameExpressionGenerator);
    }

    @Override
    public boolean isInputInCanonicalForm() {
        if(arguments.size() == 1){
            NameExpr nameExpr = (NameExpr)arguments.getChild(0);
            BasicMatrixValue bmv = Util.getBasicMatrixValue(functionAnalysis,node,
                    nameExpr.getName().getID());
            if(bmv.hasShape() && !bmv.getShape().isScalar() && (bmv.getShape().getDimensions().size() == 2)){
                java.util.List<DimValue> dimValues = bmv.getShape().getDimensions();
                return dimValues.get(0).getIntValue() == 1 && dimValues.get(1).getIntValue() >= 2;
            }
        }
        return false;
    }

    public abstract void generateScalarExpression();

    public void generateExpression() {
        TIRCommaSeparatedList args = arguments;
        int sizeArgs = arguments.size();
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        result.addLocal(input_argType);
        if(sizeArgs == 0){
            generateScalarExpression();
        }else if(sizeArgs == 1){
            NameExpr nameExpr = (NameExpr)arguments.getChild(0);
            BasicMatrixValue bmv = Util.getBasicMatrixValue(functionAnalysis,node,
                    nameExpr.getName().getID());
            if(!bmv.hasShape()){
                throw new Error("Shape information must be known in order to generate");
            }
            if(bmv.getShape().isScalar()){
                result.addInstructions(MatWablyArray.createF64Vector(2));
                result.addInstruction(new SetLocal(new Idx(input_arg)));
                result.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, 0,nameExpressionGenerator.genNameExpr(nameExpr,node)));
                result.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, 1, nameExpressionGenerator.genNameExpr(nameExpr,node)));
            }else if(bmv.getShape().isRowVector()) {
                result.addInstructions( nameExpressionGenerator.genNameExpr(nameExpr,node));
            }else{
                throw new Error("Size vector should be a row vector with real elements.");
            }
            result.addInstruction(new GetLocal(new Idx(input_arg)));
            this.generateCall();
        }else{
            result.addInstructions(MatWablyArray.createF64Vector(sizeArgs));
            result.addInstruction(new SetLocal(new Idx(input_arg)));
            // There are more than two arguments
            int i = 0;
            for(ast.Expr argExpr: arguments){
                NameExpr nameExpr = ((ast.NameExpr) argExpr);
                BasicMatrixValue matVal = Util.getBasicMatrixValue(functionAnalysis,node, nameExpr.getName().getID());
                if(!matVal.hasShape() || !matVal.getShape().isScalar()){
                    throw new Error("Size inputs must be scalar.");
                }
                result.addInstruction(new GetLocal(new Idx(input_arg)));
                result.addInstruction(new ConstLiteral(new I32(), i));
                result.addInstructions(nameExpressionGenerator.genNameExpr(nameExpr,node));
                result.addInstruction(new Call(new Idx("set_array_index_f64_no_check")));
                i++;
            }
            result.addInstruction(new GetLocal(new Idx(input_arg)));
            this.generateCall();
        }

    }
}
