package matwably.code_generation.wasm;

import matwably.ast.*;

public class MatWablyErrorFactory {
    /**
     * Generates instructions to check whether a scalar is within bounds.
     * Note that the method assumes the expr is already simplified.
     * @param expr Expression generating scalar
     * @param low Low bound to check
     * @param high High bound check
     * @return  Returns set of instructions to check a scalar expression
     * is within bounds
     */
    public static List<Instruction>
    generateCheckArrayBounds(List<Instruction> expr, List<Instruction> low, List<Instruction> high){
        List<Instruction> res = new List<>();
        List<Instruction> resDim = new List<>();
        resDim.addAll(expr);
        resDim.add(new Lt());
        resDim.addAll(low);
        res.addAll(MatMachJSError.generateThrowError(MatMachJSError.ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX,
                resDim));
        resDim = new List<>();
        resDim.addAll(expr);
        resDim.addAll(high);
        resDim.addAll(
                new Gt(new F64(),false),
                new Or());
        res.addAll(MatMachJSError.generateThrowError(MatMachJSError.ARRAY_ACCESS_EXCEEDS_DIMENSION,
                resDim));
        return res;
    }

    public static List<Instruction>
    generateCheckUpperBoundScalar(List<Instruction> expr, List<Instruction> low, List<Instruction> high){
        List<Instruction> res = new List<>();
        List<Instruction> resDim = new List<>();
        resDim.addAll(expr);
        resDim.add(new Lt());
        resDim.addAll(low);
        res.addAll(MatMachJSError.generateThrowError(MatMachJSError.ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX,
                resDim));
        resDim = new List<>();
        resDim.addAll(expr);
        resDim.addAll(high);
        resDim.addAll(
                new Gt(new F64(),false),
                new Or());
        res.addAll(MatMachJSError.generateThrowError(MatMachJSError.ARRAY_ACCESS_EXCEEDS_DIMENSION,
                resDim));
        return res;
    }


}
