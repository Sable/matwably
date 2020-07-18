package matwably.optimization.peephole;

import matwably.ast.Instruction;
import matwably.ast.List;

public interface PeepholePattern {
    PeepholeTransformationResult transform(List<Instruction> input_list, int start_index);
}

//
