package matwably.code_generation.builtin.legacy;

import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import matwably.ast.Instruction;
import matwably.ast.List;

public class TrueGenerator extends BuiltinSimplifier{
    public static BuiltinSimplifier getInstance() {
        return new TrueGenerator();
    }

    @Override
    public boolean isSimplifiable() {
        this.isSimplifiable = arguments.size() == 0;
        return isSimplifiable;
    }

    @Override
    public List<Instruction> simplify() {
        return new List<>(new ConstLiteral(new F64(), 1));
    }

    @Override
    public boolean returnsScalar() {
        return this.isSimplifiable;
    }
}
