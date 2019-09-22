package matwably.code_generation.builtin.matwably_builtin.matrix_operation;

import ast.ASTNode;
import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.legacy.MatWablyBuiltinGeneratorResult;
import matwably.code_generation.builtin.matwably_builtin.input_generation.VectorInputGenerator;
import matwably.code_generation.wasm.MatMachJSError;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import natlab.tame.tir.TIRCommaSeparatedList;

import static matwably.code_generation.wasm.MatMachJSError.ARRAY_ACCESS_EXCEEDS_DIMENSION;
import static matwably.code_generation.wasm.MatMachJSError.ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX;

public class Subsasng extends MatrixIndexing {

    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Subsasng(ASTNode node, TIRCommaSeparatedList arguments,
                    TIRCommaSeparatedList targs, String callName,
                    MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by actual Builtin. False if the built-in
     * expression returns boxed scalar.
     * Returns whether the expression always returns a matrix. i.e.
     * whether the generated built-in call does
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
     * This is to be replaced by ValueAnalysis ShapePropagation, once we
     * have a good way to determine when
     *
     * @return boolean signifying whether the builtin call returns a value.
     */
    @Override
    public boolean expressionReturnsVoid() {
        return false;
    }


    @Override
    protected MatWablyBuiltinGeneratorResult generateExpression() {

        MatWablyBuiltinGeneratorResult result =
                new MatWablyBuiltinGeneratorResult();
        // If is scalar and there is one index, we actually have the code
        // Otherwise, if its scalar and there is more than one index, it becomes
        // unsupported operation.

        if(valueUtil.isScalar(this.arrayName.getID(), node, true)){
            // If one index, check is scalar, if
            if(indices.size() == 1){
                ast.Expr exprInd = this.indices.getChild(0);
                // If its a colon expression, then return the scalar
                if(exprInd instanceof ast.ColonExpr){
                    result.addInstructions(expressionGenerator.
                            genName(this.arrayName, node));
                }else{
                    NameExpr arg0 = this.indices.getNameExpresion(0);
                    if(valueUtil.isScalar(this.indices.getNameExpresion(0),
                            node, true)){
                        Double val = valueUtil.
                                getDoubleConstant(arg0,node);
                        if(val != null){
                            if(val > 1) throw new MatWablyError.
                                    IndexExceedsMatrixDimensions(callName,
                                    node);
                            if(val < 1) throw new
                                    MatWablyError.DimensionArgumentMustBeAPositiveScalar(callName,
                                    node);
                            result.addInstructions(expressionGenerator.
                                    genName(this.arrayName, node));
                        }else{
                            result.addInstructions(expressionGenerator.
                                    genNameExpr(arg0, node));
                            result.addInstructions(
                                    new ConstLiteral(new F64(), 1),
                                    new Lt(new F64(), false)
                            );
                            result.addInstructions(
                                    MatMachJSError.generateThrowError(ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX));
                            result.addInstructions(expressionGenerator.
                                    genNameExpr(arg0, node));
                            result.addInstructions(
                                    new ConstLiteral(new F64(), 1),
                                    new Gt(new F64(), false)
                            );
                            result.addInstructions(
                                    MatMachJSError.generateThrowError(ARRAY_ACCESS_EXCEEDS_DIMENSION));
                            result.addInstructions(expressionGenerator.
                                    genName(this.arrayName, node));
                        }
                    }else{
                        result.addInstructions(expressionGenerator.
                                genName(this.arrayName, node));
                        result.addInstructions(expressionGenerator.
                                genNameExpr(this.indices.
                                        getNameExpresion(0), node));
                        result.addInstruction(new Call(
                                new Idx("get_f64_one_index_SM")));
                    }
                }

            }else{
                throw new MatWablyError.
                        UnsupportedBuiltinCallWithArguments(this.callName,
                        this.node, this.arguments);

            }
            // verify statically scalar indices.
            // if one of them is unknown, add run-time check to compare against 1.
            // if shape is not known, add all_nonzero check, i32.eqz and error
        }else{
            if(isSlicingOperation()){
                result.addInstructions(expressionGenerator.
                        genName(arrayName, node));
                VectorInputGenerator generator =
                        new VectorInputGenerator(node, indices,matwably_analysis_set);
                result.add(generator.generate());
                result.addInstructions(new List<>(
                        new Call(new Idx("get_f64"))
                ));
            }else{
                result.addInstructions(
                        MachArrayIndexing.getArrayIndexF64NoCheck(expressionGenerator.
                            genName(this.arrayName, node),
                            generateScalarIndex()));
            }
        }
        return result;
    }

    @Override
    public void validateInput() {

    }


}