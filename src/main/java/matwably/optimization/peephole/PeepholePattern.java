package matwably.optimization.peephole;

import matwably.ast.Instruction;
import matwably.ast.List;

public interface PeepholePattern {
    public abstract PeepholeTransformationResult transform(List<Instruction> input_list, int start_index);
}

//
