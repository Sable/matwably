package matwably.optimization.peephole;

import matwably.ast.GetLocal;
import matwably.ast.Instruction;
import matwably.ast.SetLocal;

public class PeepholeUtility {

    public static boolean isGetLocal(Instruction inst){
        return inst instanceof GetLocal;
    }
    public static boolean isSetLocal(Instruction inst){
        return inst instanceof SetLocal;
    }


}
