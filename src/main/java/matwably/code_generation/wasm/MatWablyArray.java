package matwably.code_generation.wasm;

import matwably.ast.*;

public class MatWablyArray{

    public static List<Instruction> setArrayIndexF64(String arr_name,int index,  NumericInstruction value) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(value);
        ret.add(new Call(new Idx("set_array_index_f64_no_check")));
        return ret;
    }
    public static List<Instruction> getArrayIndexF64(String arr_name,int index) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("get_array_index_f64_no_check")));
        return ret;
    }
    public static List<Instruction> getArrayIndexF64CheckBounds(String arr_name,int index) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 1 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("get_array_index_f64")));
        return ret;
    }
    public static List<Instruction> setArrayIndexI32(String arr_name,int index,  NumericInstruction value) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(value);
        ret.add(new Call(new Idx("set_array_index_i32_no_check")));
        return ret;
    }
    public static List<Instruction> getArrayIndexI32(String arr_name,int index) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("get_array_index_i32_no_check")));
        return ret;
    }

    public static List<Instruction> isEmpty(String arrName){
        List<Instruction> instructions = new List<>();
        instructions.add(new GetLocal(new Idx(arrName)));
        instructions.add(new Call(new Idx("isempty")));
        return instructions;
    }
    public static List<Instruction> createCellVector(int capacity){
        return createVector(true, capacity);
    }
    public static List<Instruction> createF64Vector(int capacity){
        return createVector(false, capacity);
    }

    private static List<Instruction> createVector(boolean isCellArray, int capacity){
        List<Instruction> instructions = new List<>();
        int type = (isCellArray)?5:0;
        instructions.add(new ConstLiteral(new I32(), capacity));
        instructions.add(new ConstLiteral(new I32(), type ));
        instructions.add(new ConstLiteral(new I32(), 0));
        instructions.add(new ConstLiteral(new I32(), 0));
        instructions.add(new ConstLiteral(new I32(), 0));
        instructions.add(new ConstLiteral(new I32(), 0));
        instructions.add(new Call(new Idx(new Opt<>(new Identifier("create_mxvector")),0)));
        return instructions;
    }
}
