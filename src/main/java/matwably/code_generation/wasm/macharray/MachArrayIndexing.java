package matwably.code_generation.wasm.macharray;

import matwably.ast.*;

public class MachArrayIndexing {
    private enum Kind {
        I32(new I32()),
        F64(new F64());

        private final ValueType valueType;
        private int value;
        private int twoPower;
        Kind(ValueType valueType){
            this.valueType = valueType;
            this.value =valueType.getByteLength();
            this.twoPower = (int) (Math.log(this.value)/Math.log(2));
        }
        public ValueType getValueType(){
            return valueType.treeCopy();
        }
        public int getValue(){
            return value;
        }
        public int getTwoPower(){
            return twoPower;
        }

    }
    public static List<Instruction> setArrayIndexF64(String arr_name,int index,  List<Instruction> value) {
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index));
        ret.addAll(value);
        ret.add(new Call(new Idx("set_array_index_f64")));
        return ret;
    }
    public static List<Instruction> setArrayIndexF64(String arr_name,int index, Instruction value) {
       return  setArrayIndexF64(arr_name, index, new List<>(value));
    }

    public static List<Instruction> setArrayIndexF64NoCheck(List<Instruction> array,
                                                            List<Instruction> index,
                                                            List<Instruction> value) {
        return setArrayIndexNoCheck(array, index, value, Kind.F64);
    }
    public static List<Instruction> setArrayIndexF64NoCheck(
            String arr_name, int index, Instruction... value){
        return setArrayIndexF64NoCheck(arr_name, index, new List<>(value));
    }
    public static List<Instruction> setArrayIndexF64NoCheck(
            String arr_name, int index, List<Instruction> value){
       return setArrayIndexNoCheck(new List<>(new GetLocal(new Idx(arr_name))),
               new List<>(new ConstLiteral(new I32(), index)), value, Kind.F64);
    }

    public static List<Instruction> freeMachArray(String arr_name){
        return new List<>(new GetLocal(new Idx(arr_name)),
                new Call(new Idx("free_macharray")));
    }
    public static List<Instruction> freeMachArray(){
        return new List<>(new Call(new Idx("free_macharray")));
    }
    public static List<Instruction> getArrayIndexF64(String arr_name,int index) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index+1));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("get_array_index_f64")));
        return ret;
    }
    public static List<Instruction> getArrayIndexF64(int index) {
        List<Instruction> ret = new List<>();
        ret.add(new ConstLiteral(new I32(), index+1));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for expression array");
        ret.add(new Call(new Idx("get_array_index_f64")));
        return ret;
    }
    public static List<Instruction> getArrayIndexI32(String arr_name, int index) {
        List<Instruction> ret = new List<>();
        ret.add(new GetLocal(new Idx(arr_name)));
        ret.add(new ConstLiteral(new I32(), index+1));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for: "+arr_name);
        ret.add(new Call(new Idx("get_array_index_i32")));
        return ret;
    }
    public static List<Instruction> getArrayIndexI32(int index) {
        List<Instruction> ret = new List<>();
        ret.add(new ConstLiteral(new I32(), index));
        if(index < 0 ) throw new IndexOutOfBoundsException("Index: "+index+" out of bounce for expression array");
        ret.add(new Call(new Idx("get_array_index_i32")));
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

    public static List<Instruction> getArrayIndexF64NoCheck(List<Instruction> arr_name,
                                                            List<Instruction> index){
        return getArrayIndexNoCheck(arr_name, index, Kind.F64);
    }

    public static List<Instruction> getArrayIndexI32NoCheck(List<Instruction> arr_name,
                                                            List<Instruction> index){
        return getArrayIndexNoCheck(arr_name, index, Kind.I32);
    }


    private static List<Instruction> getArrayIndexNoCheck(List<Instruction> arr_name,
                                                          List<Instruction> index,
                                                          Kind kind){
        List<Instruction> ret = new List<>();
        ret.addAll(arr_name);
        ret.add(new Load(new I32(), new Opt<>(new MemArg(
                (short)8,(short)4)), new Opt<>(), new Opt<>()));
        ret.addAll(index);
        ret.addAll( new ConstLiteral(new I32(), kind.getTwoPower()),
                    new Shl(new I32()),
                    new Add(new I32()));

        ret.add(new Load(kind.getValueType(),
                new Opt<>(new MemArg((short)0,(short)kind.getValue())),
                new Opt<>(), new Opt<>()));
        return ret;
    }
    private static List<Instruction> setArrayIndexNoCheck(List<Instruction> arr_name,
                                                         List<Instruction> index,
                                                         List<Instruction> value,
                                                          Kind kind){
        List<Instruction> ret = new List<>();
        // Load array
        ret.addAll(arr_name);
        ret.add(new Load(new I32(), new Opt<>(new MemArg(
                        (short)8,(short)4)), new Opt<>(), new Opt<>()));
        // Load index
        ret.addAll(index);
        ret.addAll( new ConstLiteral(new I32(),
                        kind.getTwoPower()),
                new Shl(new I32()),
                new Add(new I32()));
        // Load value and store
        ret.addAll(value);

        ret.add(new Store(kind.getValueType(), new Opt<>(new MemArg(
                        (short)0,(short)kind.getValue())), new Opt<>()));
        return ret;

    }
    public static List<Instruction> setArrayIndexI32NoCheck(
            String arr_name, int index, Instruction... value) {
        return setArrayIndexNoCheck(new List<>(new GetLocal(new Idx(arr_name))), new List<>(new ConstLiteral(new I32(), index)), new List<>(value), Kind.I32);
    }
    public static List<Instruction> setArrayIndexI32NoCheck(String arr_name, int index,  List<Instruction> values) {
        return setArrayIndexNoCheck(new List<>(new GetLocal(new Idx(arr_name))), new List<>(new ConstLiteral(new I32(), index)), values, Kind.I32);
    }
    public static List<Instruction> setArrayIndexI32NoCheck(List<Instruction> array ,
                                                            int index, List<Instruction> values) {
        return setArrayIndexNoCheck(array, new List<>(new ConstLiteral(new I32(), index)), values, Kind.I32);
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
