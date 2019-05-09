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

public class ProgramGenerator {

    ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
    CommandLineOptions opts;
    InterproceduralFunctionQuery interproceduralFunctionQuery;

    /**
     * Base constructor for the program generator.
     * @param funcAnalysis
     * @param opts
     */
    public ProgramGenerator(ValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis,
                            CommandLineOptions opts) {
        this.analysis = funcAnalysis;
        this.interproceduralFunctionQuery = new InterproceduralFunctionQuery(analysis);
        this.opts = opts;
    }

    public Module genProgram(){
        // Iterate and
        int numFunctions = analysis.getNodeList().size();
        Module module = new Module();
        try{
            String builtInDeclations =
                    readStreamIntoString(Main.class.
                            getResourceAsStream("/matmachjs/matmachjs.wat"));
            module.addImportedWat(new ImportedWat(builtInDeclations));
        }catch(IOException ex){
            throw new Error("MatMachJS library .wat file is missing from resources"+
                    ((opts.verbose)?ex.getMessage():""));
        }

        Set<String> generated = new HashSet<>();
        // Running analysis on functions.
        for (int i = 0; i < numFunctions; ++i) {
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>  funcAnalysis =
                    analysis.getNodeList().get(i).getAnalysis();

            FunctionGenerator gen = new FunctionGenerator(funcAnalysis, analysis,
                        interproceduralFunctionQuery, opts);
            // Get function name
            String gen_function_name = gen.genFunctionName();
            if (!generated.contains(gen_function_name)) {
                gen.generate();
                Function func_wasm =gen.getAst();
                module.addFunctions(func_wasm);
                module.addExport(FunctionExport.generate(func_wasm));
                generated.add(gen_function_name);
                if(opts.verbose){
                    log(funcAnalysis.getTree().getPrettyPrinted());
                    log("Generated: " + func_wasm.getIdentifier().getName());
                }
            }
        }

        return module;
    }

    private void log(Object object) {
        System.out.println(object);
    }
}
