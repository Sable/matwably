package matwably.code_generation.wasm.macharray;

import matwably.ast.*;

public class MachArray {

    public static List<Instruction> getLength(List<Instruction> array){
        List<Instruction> res = new List<>();
        res.addAll(array);
        res.add(new Load(new I32(), new Opt<>(new MemArg((short)4,(short)4)),
                new Opt<>(), new Opt<>()));
        return res;
    }
    public static List<Instruction> isEmpty(List<Instruction> array){
        List<Instruction> res = getLength(array);
        res.addAll(new Eqz(new I32()));
        return res;
    }

    public static List<Instruction> isScalar(List<Instruction> array){

        List<Instruction> res = new List<>();
        res.addAll(MachArray.getLength(array));
        res.add(new ConstLiteral(new I32(), 1));
        res.add(new Eq(new I32()));
        return res;
    }
}
