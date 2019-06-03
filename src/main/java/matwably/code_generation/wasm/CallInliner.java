package matwably.code_generation.wasm;

import matwably.ast.Function;
import matwably.ast.Identifier;
import matwably.ast.Module;
import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;

import java.util.HashMap;

/**
 * Naive 'wasm' call in-liner, this class provides methods to produce a MatWablyBuiltinGeneratorResult,
 * Basic idea is to map all the locals to freshly allocated names. Set them, and then in-line all the body
 * of the function by using these local map.
 *
 */
public class CallInliner {
    private Module module;
    // Assumes the arguments are already loaded on the stack
    public MatWablyBuiltinGeneratorResult inlineCall(String name){
        HashMap<String, String>  localMap = new HashMap<>();

        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        Function func = findFunction(name);
        if(func == null) throw new Error("Attempting to in-line, non-existent 'wasm' function: "+name);
        if(func.hasLocals())
            result.addLocals(func.getLocalsList().treeCopyNoTransform());

        return result;
    }


    private Function findFunction(String name) {
        for (Function function : this.module.getFunctionsList()) {
            if(function.hasIdentifier() &&
                    function.getIdentifier().getName().equals(name)) return function;
        }
        return null;
    }

    public CallInliner(Module wasmModule){
        this.module = wasmModule;
    }
    
}
