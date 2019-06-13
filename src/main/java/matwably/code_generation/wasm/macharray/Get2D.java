package matwably.code_generation.wasm.macharray;

import matwably.ast.*;

public class Get2D {


    private static Function copyArrayValueToAnotherArray(String arr_name,
                                                 String offset,
                                                 String offset_new,
                                                 String stride_i,
                                                 String stride_new_i,
                                                 String new_i,
                                                 String i,String res_arr){
        Function func = new Function();
        List<Instruction> res = new List<>();
        res.addAll(
            new GetLocal(new Idx(res_arr)),
            new GetLocal(new Idx(offset_new)),
            new GetLocal(new Idx(stride_new_i)),
            new GetLocal(new Idx(new_i)),
            new Mul(new I32()),
            new Add(new I32()),
            new ConstLiteral(new I32(), 3),
            new Shl(new I32()),
            new GetLocal(new Idx(arr_name)),
            new GetLocal(new Idx(offset)),
            new GetLocal(new Idx(stride_i)),
            new GetLocal(new Idx(i)),
            new Mul(new I32()),
            new Add(new I32()),
            new ConstLiteral(new I32(), 3),
            new Shl(new I32()),
            new Load(new I32(), new Opt<>(), new Opt<>(), new Opt<>()),
            new Store(new I32(), new Opt<>(), new Opt<>())
        );
        return func;
    }
}
