package matwably.code_generation.builtin.trial.matrix_operation;

import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;
import natlab.tame.tir.TIRCommaSeparatedList;

public class Subasgn extends MatrixOp {
    /**
     * Constructor for class MatWablyBuiltinGenerator
     *
     * @param node      TIRNode which we pass as an ASTNode,
     * @param arguments Arguments to the call
     * @param targs     Targets for the call
     * @param callName  Original Matlab call name
     * @param analyses  Set of MatWably analyses.
     */
    public Subasgn(ast.ASTNode node, TIRCommaSeparatedList arguments, TIRCommaSeparatedList targs, String callName, MatWablyFunctionInformation analyses) {
        super(node, arguments, targs, callName, analyses);
    }

    /**
     * To be implemented by actual Builtin. False if the built-in expression returns boxed scalar.
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
    public List<Instruction>[] validateInput(){
//        List[] validate = new List[arguments.size()];
//
//        if(arguments.size() <= 0 ) throw new MatWablyError.
//                NotEnoughInputArguments(callName, node);
//        if(arguments.size() == 1){
//            NameExpr index1 = arguments.getNameExpresion(0);
//            if(this.valueUtil.isScalar(tirStmt.getArrayName().getID(),
//                    tirStmt, true)){
//                Double constant  = this.valueUtil.
//                        getDoubleConstant(index1.getVarName(),node,true))
//                if(constant != null && constant.intValue() != 1){
//                    throw new MatWablyError.
//                            IndexExceedsMatrixDimensions(callName, node);
//                }else if(this.valueUtil.isScalar(index1,
//                            tirStmt, true)){
//                        List<Instruction> res = new List<>();
//                        res.add(new ConstLiteral(new I32(), 1));
//                        validate[0] =
//                                MatWablyErrorFactory.
//                                        generateCheckArrayBounds(
//                                                expressionGenerator.genExpr(index1, node),
//                                                new List<>(new ConstLiteral(new I32(),0)),
//                                                MachArray.getLength(expressionGenerator.genExpr(index1,
//                                                        node)));
//                    }else{
//
//                        validate[0] = (new List<>())
//                                .addAll(expressionGenerator.genExpr(index1,node))
//                                .add(
//                                new Call(new Idx("check_index_1d_subasgn_scalar")));
//                    }
//                }else {
//
//            }
//            }else{
//
//
//            }
//        }
//        boolean isValid = arguments.getNameExpressions().stream().skip(1)
//                .anyMatch((NameExpr expr)->{
//
//                });
//        if(!isValid) throw new MatWablyError.
//                IndexExceedsMatrixDimensions(callName, node);
        return null;
    }
    @Override
    public MatWablyBuiltinGeneratorResult generateExpression() {
//        if(arguments.)
        List<Instruction> res = new List<>();
//        TIRArrayGetStmt tirStmt = (TIRArrayGetStmt) node;
//        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
//        if(this.valueUtil.isScalar(tirStmt.getArrayName().getID(), tirStmt, true))
//        {
//
//            result.addInstructions(expressionGenerator.genName(tirStmt.getArrayName(),node));
//
//            res.addAll(
//                    new GetLocal(new Idx(Util.getTypedLocalF64()));
//        }
//        if(isSlicingOperation(tirStmt,tirStmt.getIndices())){
//            BuiltinGenerator generator = new BuiltinGenerator(tirStmt,tirStmt.getIndices(),
//                    tirStmt.getTargets(),"subsasgn",programAnalysis,
//                    analysisFunction, expressionGenerator);
//
//            MatWablyBuiltinGeneratorResult result =  generator.getResult();
//            result.addInstruction(new GetLocal(new Idx(Util.getTypedLocalI32(tirStmt.getArrayName().getID()))));
//            // Generate inputs
//            generator.generateInputs();
//            // Make call to get_f64
//            result.addInstructions(new List<>(
//                    new Call(new Idx("get_f64"))
//            ));
//            generator.generateSetToTarget();
//            res.addAll(result.getInstructions());
//            locals.addAll(result.getLocals());
//        }else {
//            String arrayName = tirStmt.getArrayName().getID();
//
//            // Make Call
//            res.add(new GetLocal(new Idx(Util.getTypedLocalI32(arrayName))));
//            res.addAll(computeIndexWasm(tirStmt, arrayName, tirStmt.getIndices()));
//            res.add(new Call(new Idx("get_array_index_f64")));
//
//            // Set variable
//            if(tirStmt.getTargets().size() > 0){
//                res.add(new SetLocal(new Idx(Util.getTypedLocalF64(tirStmt.getTargetName().getID()))));
//            }else{
//                res.add(new Drop());
//            }
//        }
        return null;
    }
}
