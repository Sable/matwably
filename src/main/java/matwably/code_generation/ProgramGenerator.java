package matwably.code_generation;

import matwably.Main;
import matwably.MatWablyCommandLineOptions;
import matwably.ast.Function;
import matwably.ast.ImportedWat;
import matwably.ast.WasmModule;
import matwably.code_generation.wasm.FunctionExport;
import matwably.util.InterproceduralFunctionQuery;
import natlab.tame.BasicTamerTool;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static matwably.util.FileUtility.readStreamIntoString;

/**
 * Program generator for the MatWably compiler, takes as input the InterproceduralValueAnalysis
 * to produce a WebAssembly module.
 */
public class ProgramGenerator {

    private final ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
    private final InterproceduralFunctionQuery interproceduralFunctionQuery;
    private final MatWablyCommandLineOptions opts;
    private final boolean print_memory_info;


    /**
     * Base constructor for the program generator.
     * @param funcAnalysis InterproceruralValueAnalysis use to specialize the compiler
     * @param opts Command line options for the compiler.
     */
    private ProgramGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis,
                            MatWablyCommandLineOptions opts) {
        this.analysis = funcAnalysis;
        this.interproceduralFunctionQuery = new InterproceduralFunctionQuery(analysis);
        this.opts = opts;
        this.print_memory_info = opts.print_memory_information;
    }

    /**
     * Method to generateInstructions the WebAssembly module given the results for the InterproceduralValueAnalysis.
     * @return Returns the resulting wasm program module.
     */
    private WasmModule generateWasmModule(){
        WasmModule wasmModule = new WasmModule();
        loadMatMachJSLibraryToModule(wasmModule, this.print_memory_info);
        generateProgramFunctions(wasmModule, analysis.getNodeList());
        return wasmModule;
    }

    /**
     * Generate program function
     * @param matlabFileToGenerate GenericFile object pointing to input file
     * @param entryFunctionArgs Entry arguments for the program
     * @param opts Compiler options, passed along the chain and instantiated.
     * @return MatWablyProgram instance representing the result of the compiler
     */
    public static MatWablyProgram generate(GenericFile matlabFileToGenerate,
                                           String[] entryFunctionArgs,
                                           MatWablyCommandLineOptions opts){
        ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool.analyze(entryFunctionArgs,
                                                            new FileEnvironment(matlabFileToGenerate));
        ProgramGenerator progGenerator = new ProgramGenerator(analysis, opts);
        String entryFunctionName = FunctionGenerator.genFunctionName(analysis.getMainNode().getAnalysis());
        WasmModule wasmModule = progGenerator.generateWasmModule();
        return new MatWablyProgram(wasmModule, entryFunctionName);
    }

    /**
     * Generates all the MatWably functions for the program, adds them to the module.
     * Relies on ValueAnalysis {@link ValueAnalysis} results to get call graph, tameIR, etc.
     * @param wasmModule Wasm module to upload the functions to
     * @param analysisNodeList Analysis list of function nodes for the analysis
     */
    private void generateProgramFunctions(WasmModule wasmModule,
                                          List<InterproceduralAnalysisNode<
                                                  IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>,
                                                  Args<AggrValue<BasicMatrixValue>>, Res<AggrValue<BasicMatrixValue>>>>
                                                  analysisNodeList) {
        Set<String> functionSoFar = new HashSet<>();
        for (InterproceduralAnalysisNode<IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>,
                Args<AggrValue<BasicMatrixValue>>,
                Res<AggrValue<BasicMatrixValue>>> analysisNode : analysisNodeList) {
            String generatedFunctionName = FunctionGenerator.genFunctionName(analysisNode.getAnalysis());
            if( !functionSoFar.contains(generatedFunctionName)){
                Function functionGenerated = FunctionGenerator.generate(analysisNode.getAnalysis(),
                                            interproceduralFunctionQuery, opts);
                wasmModule.addFunctions(functionGenerated);
                wasmModule.addExport(FunctionExport.generate(functionGenerated));
                functionSoFar.add(generatedFunctionName);
            }
        }
    }

    /**
     * Loads the MatMachJS library, inserts calls to measure RC statistics inside
     * the library
     * @return returns string with library call
     * TODO Finish with the beaver front-end to construct the built-ins in WasmIR. Hack loads it as a string, in a more through version, this will load as wasmIR
     */
    private static void loadMatMachJSLibraryToModule(WasmModule wasmModule,
                                                     boolean generateMemoryStats){
        try{
            String builtInDeclations =
                    readStreamIntoString(Main.class.
                            getResourceAsStream("/matmachjs/matmachjs.wat"));
            if(generateMemoryStats){
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
                        replace(";; %gc-macharray-allocation",
                                "get_local 0\n\tcall $gcMachArrayAllocation");
                builtInDeclations = builtInDeclations.
                        replace(";; %gc_malloc",
                                "get_local 0\n\tcall $gcRecordAllocation");
            }
            wasmModule.addImportedWat(new ImportedWat(builtInDeclations));
        }catch(IOException ex){
            throw new Error("MatMachJS library .wat file is missing from resources");
        }
    }
}
