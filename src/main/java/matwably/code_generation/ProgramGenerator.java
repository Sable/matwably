package matwably.code_generation;

import matwably.Main;
import matwably.MatWablyCommandLineOptions;
import matwably.ast.Function;
import matwably.ast.ImportedWat;
import matwably.ast.Module;
import matwably.code_generation.wasm.FunctionExport;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static matwably.util.FileUtility.readStreamIntoString;

/**
 * Program generator for the MatWably compiler, takes as input the InterproceduralValueAnalysis
 * to produce a WebAssembly module.
 */
public class ProgramGenerator {


    /**
     * Reference to the ValueAnalysis result for the Matlab program
     */
    private final ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
    /**
     * Command line options for the compiler
     */
    private final MatWablyCommandLineOptions opts;
    /**
     * InterproceduralFunctionQuery object field
     */
    private InterproceduralFunctionQuery interproceduralFunctionQuery;

    /**
     * Map from generated function name to generated function
     */
    private Map<String, MatWablyFunction> programGeneratorMap = new HashMap<>();

    /**
     * Wasm Module
     */
    private Module wasmModule = null;
    /**
     * Generated Function for the main node
     */
    private MatWablyFunction mainNode = null;

    /**
     * Base constructor for the program generator.
     * @param funcAnalysis InterproceruralValueAnalysis use to specialize the compiler
     * @param opts Command line options for the compiler.
     */
    public ProgramGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis,
                            MatWablyCommandLineOptions opts) {

        this.analysis = funcAnalysis;
        this.interproceduralFunctionQuery = new InterproceduralFunctionQuery(analysis);
        this.opts = opts;
    }

    /**
     * Getter for the generated module
     * @return Returns the wasm module associated with this function
     */
    public Module getGeneratedModule() {
        return wasmModule;
    }

    /**
     * Getter for inter-procedural node
     * @param functionName Node from ValueAnalysis
     * @return returns the generated wasm function
     */
    public MatWablyFunction getGeneratedFunction(String functionName){
        return programGeneratorMap.get(functionName);
    }

    /**
     * Method to generateInstructions the WebAssembly module given the results for the InterproceduralValueAnalysis.
     * @return Returns the resulting wasm program module.
     */
    public Module genProgram(){

        this.wasmModule = new Module();
        this.programGeneratorMap = new HashMap<>();

        // Add MatMachJS library
        try{
            //TODO Finish with the beaver front-end to construct the built-ins in WasmIR.
            String builtInDeclations = loadMatMachJSLibrary();
            wasmModule.addImportedWat(new ImportedWat(builtInDeclations));
        }catch(IOException ex){
            throw new Error("MatMachJS library .wat file is missing from resources"+
                    ((opts.verbose)?ex.getMessage():""));
        }
        this.mainNode = this.generateFunction(analysis.getMainNode());
        // Generate all functions
        this.analysis.getNodeList().forEach(node->
                this.generateFunction( node));
        return wasmModule;
    }

    private MatWablyFunction generateFunction( InterproceduralAnalysisNode<
                                          IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>,
                                          Args<AggrValue<BasicMatrixValue>>,
                                          Res<AggrValue<BasicMatrixValue>>> node ) {

        FunctionGenerator gen = new FunctionGenerator(node.getAnalysis(),
                    interproceduralFunctionQuery, opts);
        String funcName = gen.genFunctionName();

        if ( !programGeneratorMap.containsKey(funcName) ) {
            Function func_wasm = gen.genFunction();
            wasmModule.addFunctions(func_wasm);
            wasmModule.addExport(FunctionExport.generate(func_wasm));
            MatWablyFunction matWablyFunction = new MatWablyFunction(node, func_wasm);

            programGeneratorMap.put(funcName, matWablyFunction);
            if( opts.verbose ){
                log(node.getFunction().getAst()
                        .getPrettyPrinted());
                log("Generated: " + func_wasm.getIdentifier().getName());
            }
            return matWablyFunction;
        }else{
            return programGeneratorMap.get(funcName);
        }
    }

    /**
     * Loads the MatMachJS library, inserts calls to measure RC statistics inside
     * the library
     * TODO: Hack loads it as a string, in a more through version, this will load as wasmIR
     * @return returns string with library call
     * @throws IOException if library file is not present
     */
    private String loadMatMachJSLibrary() throws IOException {
        String builtInDeclations =
                readStreamIntoString(Main.class.
                        getResourceAsStream("/matmachjs/matmachjs.wat"));
        if(opts.print_memory_information){
            builtInDeclations = builtInDeclations.
                    replace(";; %gc-time-start",
                            "call $gcTic");
            builtInDeclations = builtInDeclations.
                    replace(";; %gc-time-end",
                            "call $gcToc");
            builtInDeclations = builtInDeclations.
                    replace(";; %gc_free",
                            "get_local 0\n\tcall $gcRecordFreeing");
            builtInDeclations = builtInDeclations.
                    replace(";; %gc_malloc",
                            "get_local 0\n\tcall $gcRecordAllocation");
        }
        return builtInDeclations;
    }

    /**
     * Helper logger function
     * @param object Object to print
     */
    private void log(Object object) {
        System.out.println(object);
    }

    /**
     * Getter for the main generated MatWably function in the Analysis
     * @return
     */
    public MatWablyFunction getMainNode() {
        if( mainNode == null ) throw new Error("Generation of Main function has" +
                " not occurred, please run 'ProgramGenerator#genProgram()' to " +
                "obtain the MatWablyFunction method");
        return mainNode;
    }
}
