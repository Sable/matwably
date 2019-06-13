package matwably.code_generation.wasm;

import matwably.ast.Function;
import matwably.ast.Module;

import java.util.Objects;

public class ModuleBuilder {

    private Module module;

    public ModuleBuilder(Module mod){
        this.module = Objects.requireNonNull(mod);
    }

    public Function findFunction(String name) {
        for (Function function : this.module.getFunctionsList()) {
            if(function.hasIdentifier() &&
                    function.getIdentifier().getName().equals(name)) return function;
        }
        return null;
    }

}
