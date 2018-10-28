package matwably.optimization.peephole.patterns;

import matwably.ast.*;
import matwably.optimization.peephole.PeepholePattern;
import matwably.optimization.peephole.PeepholeTransformationResult;
import matwably.optimization.peephole.PeepholeUtility;

/**
 * set_local $i
 * get_local $i
 * ============
 * tee_local $i
 */
public class GetSetLocal implements PeepholePattern {
    @Override
    public PeepholeTransformationResult transform(List<Instruction> input_list, int start_index) {
        PeepholeTransformationResult  result = new PeepholeTransformationResult();
        Instruction inst = input_list.getChild(start_index);
        if(PeepholeUtility.isSetLocal(inst) &&
                PeepholeUtility.isGetLocal(input_list.getChild(start_index+1))){
            if(((SetLocal) inst).getIdx().getIdentifier().getName().equals(
                    ((GetLocal)input_list.getChild(start_index + 1))
                            .getIdx().getIdentifier().getName())){
                result.addInstruction(new TeeLocal(new Idx(((SetLocal) inst).getIdx()
                        .getIdentifier().getName())));
                result.setLength(2);
            }
        }
        return result;
    }
}
