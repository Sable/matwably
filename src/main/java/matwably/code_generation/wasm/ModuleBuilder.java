package matwably.code_generation.wasm;

import matwably.ast.Function;
import matwably.ast.WasmModule;

import java.util.Objects;

public class ModuleBuilder {

    private WasmModule wasmModule;

    public ModuleBuilder(WasmModule mod){
        this.wasmModule = Objects.requireNonNull(mod);
    }

    public Function findFunction(String name) {
        for (Function function : this.wasmModule.getFunctionsList()) {
            if(function.hasIdentifier() &&
                    function.getIdentifier().getName().equals(name)) return function;
        }
        return null;
    }

}
