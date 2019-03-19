package matwably.optimization.peephole.patterns;

import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.optimization.peephole.PeepholePattern;
import matwably.optimization.peephole.PeepholeTransformationResult;

public class BooleanCondition implements PeepholePattern {
    /**
     *
     * @param input_list
     * @param start_index
     * @return
     * <em> [i32]
     *      f64.convert_u/i32 // Converts boolean back to f64
            set_local $mc_t1_f64
            get_local $mc_t1_f64
            f64.const 0.0
            f64.eq
            i32.eqz
            =========>
            [i32]
     *
     * </em>
     */
    @Override
    public PeepholeTransformationResult transform(List<Instruction> input_list, int start_index) {
//        PeepholeTransformationResult  result = new PeepholeTransformationResult();
//        Instruction inst = input_list.getChild(start_index);
//        if(PeepholeUtility.is64Convert32U(inst) &&
//                PeepholeUtility.isSetLocal(input_list.getChild(start_index+1))&&
//                    PeepholeUtility.isGetLocal(input_list.getChild(start_index+2))
//                &&
//                PeepholeUtility.isF64Constant(input_list.getChild(start_index+3)) &&
//                PeepholeUtility.isF64Eq(input_list.getChild(start_index+4)) &&
//                PeepholeUtility.isI32Eqz(input_list.getChild(start_index+5))){
//            if(((ConstLiteral)input_list.getChild(start_index+5)).getValue().doubleValue() == 1)
//            {
////                SetLocal set_local_inst = input_list.getChild(start_index+1);
//
//            }
//
//
//
//        }
//
//                ){
//
//            if(((SetLocal) inst).getIdx().getIdentifier().getName().equals(
//                    ((GetLocal)input_list.getChild(start_index + 1))
//                            .getIdx().getIdentifier().getName())){
//                result.addInstruction(new TeeLocal(new Idx(((SetLocal) inst).getIdx()
//                        .getIdentifier().getName())));
//                result.setLength(2);
//            }
//        }
        return null;
    }
}
