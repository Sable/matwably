package matwably.code_generation.builtin.matwably_builtin.matrix_operation;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;
import matwably.ast.*;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.wasm.MatMachJSError;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.Shape;

import static matwably.code_generation.wasm.MatMachJSError.ARRAY_ACCESS_EXCEEDS_DIMENSION;
import static matwably.code_generation.wasm.MatMachJSError.ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX;

abstract public class MatrixIndexing  extends MatrixOp{
    final Name arrayName;
    private final Shape arrayShape;
    final TIRCommaSeparatedList indices;
    private final boolean disallow_range_opt;
    private final boolean ignore_bound_checks;

    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public MatrixIndexing(ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
        if(node instanceof TIRArrayGetStmt){
            this.arrayName = ((TIRArrayGetStmt)node).getArrayName();
        }else if(node instanceof TIRArraySetStmt){
            this.arrayName = ((TIRArraySetStmt)node).getArrayName();
        }else{
            throw new MatWablyError.
                    UnsupportedBuiltinCallWithArguments(callName, node, arguments);
        }
        this.ignore_bound_checks = analyses.getProgramOptions().ignore_bounds_check;
        this.disallow_range_opt = analyses.getProgramOptions()
                .disallow_range_opt;
        this.indices = arguments;
        this.arrayShape = valueUtil.getShape(arrayName.getID(),node,
                true);
    }
    List<Instruction> generateScalarIndex() {
        List<Instruction> result = new List<>();

        Integer[] stride = computeStride();
        // Compute index
        result.addAll(expressionGenerator.
                genNameExpr(indices.getNameExpresion(0),node));
        if(!this.ignore_bound_checks)
            result.addAll(computeIndexChecks(0));
        result.addAll(new List<>(
                new ConstLiteral(new F64(),1),
                new Sub(new F64())));
        for (int i = 1; i < indices.size(); ++i) {

            if(!this.ignore_bound_checks)
                result.addAll(computeIndexChecks(i));

            if (stride != null && stride[i] != null) {
                result.add(new ConstLiteral(new F64(), stride[i]));
            } else {
                result.addAll(expressionGenerator.
                        genName(arrayName,node));
                result.addAll(new List<>(
                        new ConstLiteral(new F64(), i),
                        new Call(new Idx("mxarray_stride_MS_nocheck"))
                ));
            }

            result.addAll(expressionGenerator.
                    genNameExpr(indices.getNameExpresion(i),
                            node));
            result.add(new ConstLiteral(new F64(), 1));
            result.add(new Sub(new F64()));
            result.add(new Mul(new F64()));
            result.add(new Add(new F64()));
        }
        result.add(new CvTrunc(new I32(), new F64(), true));
        return result;
    }

    private List<Instruction> computeIndexChecks(int i){
        List<Instruction> res = new List<>();
        Double index = valueUtil.getDoubleConstant(indices.
                getNameExpresion(i),node);
        boolean shapeConstant = arrayShape != null && arrayShape.isConstant();
        boolean indexConstant = index != null;
        if(shapeConstant && indexConstant){
            int upperLimit;
            int indexValue = index.intValue();
            if(indices.size() == 1){
                upperLimit = arrayShape.getHowManyElements(0);
            }else{
                upperLimit = arrayShape.getDimensions().get(i)
                        .getIntValue();
            }
            if(indexValue <= 0){
                throw new MatWablyError.
                        DimensionArgumentMustBeAPositiveScalar(callName, node);
            }else if(indexValue > upperLimit){
                throw new MatWablyError.
                        IndexExceedsMatrixDimensions(callName, node);
            }
        }else if(shapeConstant){
            BasicMatrixValue bmv = valueUtil.getBasicMatrixValue(
                    indices.getNameExpresion(i).getVarName(),
                    node, true);
            // Check range of index if index not constant.
            if(!this.disallow_range_opt && bmv != null && bmv.hasRangeValue() && bmv.getRangeValue().hasLowerBound() ){
                int indexValue = bmv.getRangeValue().getLowerBound().getIntValue();
                if(indexValue <= 0)
                    throw new MatWablyError.
                            DimensionArgumentMustBeAPositiveScalar(callName, node);
            }else{
                // Check bounds
                res.addAll(expressionGenerator.
                        genNameExpr(indices.getNameExpresion(i),node));
                res.addAll(new ConstLiteral(new F64(),
                                1),
                        new Lt(new F64(),false));
                res.addAll(MatMachJSError.
                        generateThrowError(ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX));
            }
            if(indices.size() == 1){
                int arrLength = arrayShape.getHowManyElements(i);
                if(!this.disallow_range_opt && bmv != null && bmv.hasRangeValue() &&
                        bmv.getRangeValue().hasUpperBound() ) {
                    int indexValue = bmv.getRangeValue().getUpperBound()
                            .getIntValue();
                    if (indexValue > arrLength) {
                        throw new MatWablyError.
                                IndexExceedsMatrixDimensions(callName, node);
                    }
                }else{
                    res.addAll(expressionGenerator.
                            genNameExpr(indices.getNameExpresion(i),node));
                    res.addAll(new ConstLiteral(new F64(),arrLength),
                            new Gt(new F64(),false));
                    res.addAll(MatMachJSError.
                            generateThrowError(ARRAY_ACCESS_EXCEEDS_DIMENSION));
                }
            }else{
                int dimLength = arrayShape.getDimensions().get(i)
                        .getIntValue();
                if(bmv != null && bmv.hasRangeValue() &&  bmv.getRangeValue().hasUpperBound()){
                    int indexValue = bmv.getRangeValue().getUpperBound()
                            .getIntValue();
                    if (indexValue > dimLength) {
                        throw new MatWablyError.
                                IndexExceedsMatrixDimensions(callName, node);
                    }
                }else{
                    res.addAll(expressionGenerator.
                            genNameExpr(indices.getNameExpresion(i),node));
                    // Get dimension dynamically
                    res.add(new ConstLiteral(new F64(),dimLength));
                    res.addAll(new Gt(new F64(),false));
                    res.addAll(MatMachJSError.
                            generateThrowError(ARRAY_ACCESS_EXCEEDS_DIMENSION));
                }
            }

        }else if(indexConstant){
            // Check bounds
            res.addAll(new ConstLiteral(new F64(), index.intValue()));
            res.addAll(new ConstLiteral(new F64(),
                            1),
                    new Lt(new F64(),false));
            res.addAll(MatMachJSError.
                    generateThrowError(ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX));

            if(indices.size() == 1){

                res.addAll(new ConstLiteral(new F64(), index.intValue()));
                res.addAll(expressionGenerator.
                        genName(arrayName,node));
                res.add(new Load(new I32(), new Opt<>(new MemArg((short)4,
                        (short) 4)),
                        new Opt<>(),new Opt<>()));
                res.addAll(new Convert(new F64(), new I32(), true));
                res.addAll( new Gt(new F64(),false));

            }else{
                res.addAll(new ConstLiteral(new F64(), index.intValue()));
                // Get dimension dynamically
                res.addAll(expressionGenerator.
                        genName(arrayName,node));
                res.addAll(new ConstLiteral(new F64(), i));
                res.add(new Call(new Idx("mxarray_size_MS_nocheck")));
                res.addAll(new Gt(new F64(),false));
            }
            res.addAll(MatMachJSError.
                    generateThrowError(ARRAY_ACCESS_EXCEEDS_DIMENSION));
        }else{
            res.addAll(expressionGenerator.
                    genNameExpr(indices.getNameExpresion(0),node));
            res.addAll(new ConstLiteral(new F64(),
                            1),
                    new Lt(new F64(),false));
            res.addAll(MatMachJSError.
                    generateThrowError(ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX));

            if(indices.size() == 1){

                res.addAll(expressionGenerator.
                        genNameExpr(indices.getNameExpresion(0),node));
                res.addAll(expressionGenerator.
                        genName(arrayName,node));

                res.addAll(new Load(new I32(), new Opt<>(new MemArg((short)4,
                        (short) 4)),
                        new Opt<>(),new Opt<>()));

                res.addAll(new Convert(new F64(), new I32(), true));
                res.addAll( new Gt(new F64(),false));

            }else{

                res.addAll(expressionGenerator.
                        genNameExpr(indices.getNameExpresion(i),node));
                // Get dimension dynamically
                res.addAll(expressionGenerator.
                        genName(arrayName,node));
                res.addAll(new ConstLiteral(new F64(), i));
                res.add(new Call(new Idx("mxarray_size_MS_nocheck")));
                res.addAll(new Gt(new F64(),false));
            }
            res.addAll(MatMachJSError.
                    generateThrowError(ARRAY_ACCESS_EXCEEDS_DIMENSION));
        }
        return res;
    }
    private Integer[] computeStride() {
        Shape shape = valueUtil.getShape(arrayName.getID(), node, true);
        if(shape != null ){
            Integer[] stride = new Integer[shape.getDimensions().size()];
            stride[0] = 1;
            for (int i = 1; i < stride.length; ++i) {
                if ( shape.getDimensions().get(i-1).hasIntValue() &&
                        stride[i-1] != null ) {
                    stride[i] = stride[i-1] *
                            shape.getDimensions().get(i-1).getIntValue();
                } else {
                    stride[i] = null;
                    return stride;
                }
            }
            return stride;
        }
        return null;
    }

    /**
     * Function returns whether built-in is a Slicing Operation
     * @return returns whether built-in is a Slicing Operation
     */
    boolean isSlicingOperation() {
        return indices.stream().anyMatch((expr)->
                expr instanceof ast.ColonExpr ||
                        !valueUtil.isScalar(((NameExpr)expr).getName().getID(),
                                node,true));
    }
}
