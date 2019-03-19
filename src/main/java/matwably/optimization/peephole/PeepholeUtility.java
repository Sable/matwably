package matwably.optimization.peephole;

import matwably.ast.*;

public class PeepholeUtility {

    public static boolean isGetLocal(Instruction inst){
        return inst instanceof GetLocal;
    }
    public static boolean isSetLocal(Instruction inst){
        return inst instanceof SetLocal;
    }
    public static boolean is64Convert32U(Instruction inst){
        return inst instanceof Convert &&((Convert)inst).getType().isF64()&& ((Convert)inst).getType().isI32();
    }


    public static boolean isF64Constant(Instruction inst) {
        return inst instanceof ConstLiteral && ((ConstLiteral)inst).getType().isF64();
    }

    public static boolean isTeeLocal(Instruction child) {
        return child instanceof TeeLocal;
    }

    public static boolean isF64Eq(Instruction child) {
        return child instanceof Eq && ((Eq)child).getType().isF64();
    }

    public static boolean isI32Eqz(Instruction child) {
        return child instanceof Eqz && ((Eqz) child).getType().isI32();
    }
}
