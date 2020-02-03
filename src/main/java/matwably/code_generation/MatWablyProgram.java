package matwably.code_generation;

import matwably.ast.Function;
import matwably.ast.Module;
import matwably.pretty.PrettyPrinter;

/**
 * Class contains the result of the generation of each function in the MatWably
 * compiler
 */
public class MatWablyProgram {
    private final String entryFunctionName;
    private final Module wasmModule;
    /**
     * Wasm Function generated for the corresponding analysis node
     */
    private Function wasmFunction;

    /**
     * MatWablyProgram is composed of the wasm module generated and the name of the generated entry function
      * @param wasmModule Module generated for program
     * @param entryFunctionName Name of entry function
     */
    public MatWablyProgram(Module wasmModule, String entryFunctionName) {
        this.wasmModule = wasmModule;
        this.entryFunctionName = entryFunctionName;
    }

    public Module getWasmModule() {
        return wasmModule;
    }

    public String getEntryFunctionName() {
        return entryFunctionName;
    }

    @Override
    public String toString() {
        PrettyPrinter prettyPrinter = new PrettyPrinter(this.wasmModule);
        return prettyPrinter.toString();
    }
}
