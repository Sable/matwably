package matwably.code_generation.builtin;

import matwably.ast.*;
import matwably.util.Util;

public class TrueGenerator extends BuiltinSimplifier{
    public static BuiltinSimplifier getInstance() {
        return new TrueGenerator();
    }

    @Override
    public boolean isSimplifiable() {
        return arguments.size()==0;
    }

    @Override
    public List<Instruction> simplify() {
        String typedTarget = Util.getTypedLocalF64(targets.getChild(0).getVarName());
        return new List<>(new ConstLiteral(new F64(), 1),
                new SetLocal(new Idx(typedTarget)));
    }
}
