package matwably.code_generation;

import matwably.CommandLineOptions;
import matwably.Main;
import matwably.ast.Function;
import matwably.ast.ImportedWat;
import matwably.ast.Module;
import matwably.code_generation.wasm.FunctionExport;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static matwably.util.FileUtility.readStreamIntoString;

/**
 * Program generator for the MatWably compiler, takes as input the InterproceduralValueAnalysis
 * result.
 */
public class ProgramGenerator {
    /**
     * Reference to the ValueAnalysis result for the Matlab program
     */
    private ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
    /**
     * Command line options for the compiler
     */
    private CommandLineOptions opts;
    /**
     * InterproceduralFunctionQuery object field
     */
    private InterproceduralFunctionQuery interproceduralFunctionQuery;

    /**
     * Base constructor for the program generator.
     * @param funcAnalysis InterproceruralValueAnalysis use to specialize the compiler
     * @param opts Command line options for the compiler.
     */
    public ProgramGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis,
                            CommandLineOptions opts) {
        this.analysis = funcAnalysis;
        this.interproceduralFunctionQuery = new InterproceduralFunctionQuery(analysis);
        this.opts = opts;
    }

    /**
     * Method to generate the WebAssembly module given the results for the InterproceduralValueAnalysis.
     * @return Returns the resulting wasm program module.
     */
    public Module genProgram(){
        
        int functionNumber = analysis.getNodeList().size();

        Module wasmModule = new Module();
        try{
            // Hard codes the built-ins by using a hack, ideally the built-in wat file should be parsed and
            // translated into the internal WasmIR. For now, we simply hack this
            //TODO Finish with the beaver front-end to construct the built-ins in WasmIR.
            String builtInDeclations =
                    readStreamIntoString(Main.class.
                            getResourceAsStream("/matmachjs/matmachjs.wat"));
            wasmModule.addImportedWat(new ImportedWat(builtInDeclations));
        }catch(IOException ex){
            throw new Error("MatMachJS library .wat file is missing from resources"+
                    ((opts.verbose)?ex.getMessage():""));
        }

        // Running generation of functions inside module.
        Set<String> generatedSoFar = new HashSet<>();
        for (int i = 0; i < functionNumber; ++i) {

            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>  funcAnalysis =
                    analysis.getNodeList().get(i).getAnalysis();

            FunctionGenerator gen = new FunctionGenerator(funcAnalysis, analysis,
                        interproceduralFunctionQuery, opts);
            // Get function name
            String gen_function_name = gen.genFunctionName();
            if (!generatedSoFar.contains(gen_function_name)) {
                gen.generate();
                Function func_wasm = gen.getAst();
                wasmModule.addFunctions(func_wasm);
                wasmModule.addExport(FunctionExport.generate(func_wasm));
                generatedSoFar.add(gen_function_name);
                if(opts.verbose){
                    log(funcAnalysis.getTree().getPrettyPrinted());
                    log("Generated: " + func_wasm.getIdentifier().getName());
                }
            }
        }
        return wasmModule;
    }
    private void log(Object object) {
        System.out.println(object);
    }
}
