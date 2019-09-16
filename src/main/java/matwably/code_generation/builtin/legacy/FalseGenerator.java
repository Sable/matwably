package matwably.code_generation.builtin.legacy;

import matwably.ast.ConstLiteral;
import matwably.ast.F64;
import matwably.ast.Instruction;
import matwably.ast.List;

public class FalseGenerator extends BuiltinSimplifier{
    public static BuiltinSimplifier getInstance() {
        return new FalseGenerator();
    }

    @Override
    public boolean isSimplifiable() {
        this.isSimplifiable = arguments.size() == 0;
        return isSimplifiable;
    }

    @Override
    public List<Instruction> simplify() {

        return new List<>(new ConstLiteral(new F64(), 0));

    }

    @Override
    public boolean returnsScalar() {
        return this.isSimplifiable;
    }
}
