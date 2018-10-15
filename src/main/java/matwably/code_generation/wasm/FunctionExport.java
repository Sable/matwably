package matwably.code_generation.wasm;

import matwably.ast.Export;
import matwably.ast.Function;
import matwably.ast.FunctionExportDesc;
import matwably.ast.Idx;

public class FunctionExport {

    public static Export generate(Function wasmFunc){
        return new Export(wasmFunc.getIdentifier().getName(),
                new FunctionExportDesc(new Idx(wasmFunc.getIdentifier().getName())));
    }
}
