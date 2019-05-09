package matwably.code_generation.wasm;

import matwably.ast.*;

public class MatWablyArray{
    public static List<Instruction> setArrayIndexF64(String arr_name,int index,  List<Instruction> value) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.addAll(value);
        ret.add(new Call(new Idx("set_array_index_f64_no_check")));
        return ret;
    }
    public static List<Instruction> setArrayIndexF64(String arr_name,int index, Instruction value) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(value);
        ret.add(new Call(new Idx("set_array_index_f64_no_check")));
        return ret;
    }
    public static List<Instruction> freeMachArray(String arr_name){
        return new List<>(new GetLocal(new Idx(arr_name)), new Call(new Idx("free_macharray")));
    }
    public static List<Instruction> freeMachArray(){
        return new List<>(new Call(new Idx("free_macharray")));
    }
    public static List<Instruction> getArrayIndexF64(String arr_name,int index) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("get_array_index_f64_no_check")));
        return ret;
    }
    public static List<Instruction> getArrayIndexF64(int index) {
        List<Instruction> ret = new List<>();
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for expression array");
        ret.add(new Call(new Idx("get_array_index_f64_no_check")));
        return ret;
    }
    public static List<Instruction> getArrayIndexI32(String arr_name, int index) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("get_array_index_i32_no_check")));
        return ret;
    }
    public static List<Instruction> getArrayIndexI32(int index) {
        List<Instruction> ret = new List<>();
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for expression array");
        ret.add(new Call(new Idx("get_array_index_i32_no_check")));
        return ret;
    }
    public static List<Instruction> getArrayIndexF64Check(int index) {
        List<Instruction> ret = new List<>();
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for expression array");
        ret.add(new Call(new Idx("get_array_index_f64")));
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
    public static List<Instruction> setArrayIndexI32(String arr_name,int index,  Instruction value) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(value);
        ret.add(new Call(new Idx("set_array_index_i32_no_check")));
        return ret;
    }
    public static List<Instruction> setArrayIndexI32(String arr_name, int index,  List<Instruction> values) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        ret.addAll(values);
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("set_array_index_i32_no_check")));
        return ret;
    }
    public static List<Instruction> setArrayIndexI32(List<Instruction> array , int index,  List<Instruction> values) {
        List<Instruction> ret = new List<>();
        ret.addAll(array);
        ret.add(new ConstLiteral(new I32(), index));
        ret.addAll(values);
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for expression array");
        ret.add(new Call(new Idx("set_array_index_i32_no_check")));
        return ret;
    }

    public static List<Instruction> createEmptyArray(int dim_number){
        return new List<>(new ConstLiteral(new I32(),dim_number),
                new ConstLiteral(new I32(),0),
                new ConstLiteral(new I32(),0),
                new Call(new Idx("create_mxarray_empty")));
    }
    public static List<Instruction> isEmpty(String arrName){
        List<Instruction> instructions = new List<>();
        instructions.add(new GetLocal(new Idx(arrName)));
        instructions.add(new Call(new Idx("isempty")));
        return instructions;
    }
    public static List<Instruction> createI32Vector(int capacity){
        return createVector( capacity, true, true);
    }
    public static List<Instruction> createI32Vector(int capacity, String name){
        return createVector( capacity, true, true).add(new SetLocal(new Idx(name)));
    }
    public static List<Instruction> createI32Vector(int capacity, String name, boolean isRowVector){
        return createVector( capacity, true, isRowVector).add(new SetLocal(new Idx(name)));
    }
    public static List<Instruction> createCellVector(int capacity, boolean isRowVector){
        return createVector( capacity, true, isRowVector);
    }

    public static List<Instruction> createCellVector(int capacity){
        return createVector( capacity, true, true);
    }
    public static List<Instruction> createF64Vector(int capacity, boolean isRowVector){
        return createVector( capacity, false, isRowVector);
    }
    public static List<Instruction> createF64Vector(int capacity){
        return createVector( capacity, false, true);
    }
    public static List<Instruction> createF64Vector(int capacity, String name){
        return createVector( capacity,false,true).add(new SetLocal(new Idx(name)));
    }
    public static List<Instruction> createF64Vector(int capacity, String name, boolean isRowVector){
        return createVector( capacity,false,isRowVector).add(new SetLocal(new Idx(name)));
    }

    public static List<Instruction> createVector( int capacity,boolean isCellArray, boolean isRowVector){
        List<Instruction> instructions = new List<>();
        int type = (isCellArray)?5:0;
        int orientation = (isRowVector)?0:1;
        instructions.add(new ConstLiteral(new I32(), capacity));
        instructions.add(new ConstLiteral(new I32(), type ));
        instructions.add(new ConstLiteral(new I32(), 0));
        instructions.add(new ConstLiteral(new I32(), orientation));
        instructions.add(new Call(new Idx(new Opt<>(new Identifier("create_mxvector")),0)));
        return instructions;
    }
}
