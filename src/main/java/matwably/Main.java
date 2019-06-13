/*
 *  Copyright (c) 2018. David Fernando Herrera, McGill University
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 *  OF ANY KIND, either express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 */

package matwably;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import matwably.ast.Module;
import matwably.code_generation.ProgramGenerator;
import matwably.optimization.peephole.PeepholeOptimizer;
import matwably.pretty.PrettyPrinter;
import natlab.tame.BasicTamerTool;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import static matwably.util.FileUtility.readStreamIntoHexString;
import static matwably.util.FileUtility.readStreamIntoString;

public class Main {


    public static void main(String[] argv)
    {
        // Parse command line options
        MatWablyCommandLineOptions opts = new MatWablyCommandLineOptions();
        JCommander optParse = null;
        try{
            optParse = new JCommander(opts, argv);
        }catch(ParameterException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        optParse.setProgramName("MatWably");

        // Print usage if asked for help
        if( opts.help )
        {
            optParse.usage();
            System.exit(0);
        }
        // get args to entry function
        String[] args_entry_function = opts.getEntryFunctionArgs(optParse);

        // Generate McLab File and perform value analysis to optain call graph.
        GenericFile gfile = opts.getGenericFile();
        FileEnvironment fenv = new FileEnvironment(gfile);
        ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool.analyze(args_entry_function, fenv);

        // Interface to query inter-procedurally.

        // Generate program
        ProgramGenerator pg = new ProgramGenerator(analysis, opts);
        // Generate Matlab program
        Module module = pg.genProgram();
        // Pretty print program
        PrettyPrinter prettyPrinter = new PrettyPrinter(module);
        // Call Peephole optimizer works at the Wasm level.
        if(opts.peephole){
            PeepholeOptimizer peep = new PeepholeOptimizer(module);
            peep.optimize();
            if(opts.verbose){
                log(peep.getFrequenciesAsCsvString());
            }
        }

        FileWriter out;
        try{
            out = new FileWriter(opts.basename_output_file+".wat");
            out.write(prettyPrinter.toString());
            out.close();
        }catch(Exception e){
            throw new Error("Error writing to file: "+ opts.output_file+e);
        }

        try{
            String wasmFile = opts.basename_output_file+".wasm";

            // Compile wat file to wasm.
            Process process = Runtime.getRuntime().exec(
                    "wat2wasm " +opts.basename_output_file+".wat -o "+ wasmFile);
            if(opts.verbose)
                log("wat2wasm " +opts.basename_output_file+".wat -o "+ wasmFile);
            int exitFlag = process.waitFor();
            if(exitFlag == 1)
                throw new Error("Error: compilation of wat file failed: \n"+
                        readStreamIntoString(process.getErrorStream()));
            // Do not generate wat file based on command line opt.
            if(!opts.generate_wat_file)
                    Runtime.getRuntime().exec("rm "+opts.basename_output_file+".wat");

            // Decide which loader to use
            String loader = readStreamIntoString((opts.inline_wasm)?
                        Main.class.getResourceAsStream("/inline_wasm_module_loader.js"):
                        Main.class.getResourceAsStream("/wasm_module_loader.js"));
            StringBuilder outfileBuilder = new StringBuilder();
            String js_support_lib = readStreamIntoString(Main.class.getResourceAsStream("/matmachjs/matmachjs-lib.js"));
            if(opts.inline_wasm){
                Runtime.getRuntime().exec("rm "+ opts.basename_output_file+".wasm");
                // Create array with wasm module in hex bytes
                outfileBuilder.append("let wasmModuleHexString = \"");
                outfileBuilder.append(readStreamIntoHexString(new FileInputStream(new File(wasmFile))));
                outfileBuilder.append("\";\n");
            }

//            loader = String.format(loader, opts.basename_output_file);
            // Append library
            outfileBuilder.append(js_support_lib);
            // Append loader
            outfileBuilder.append(loader);

            // Load file in
            FileWriter wasmOut= new FileWriter(opts.basename_output_file+".js");
            wasmOut.write(outfileBuilder.toString());
            wasmOut.close();
        }catch ( IOException | InterruptedException ex){
            throw new Error("Error compiling to wasm: \n"+ ex.getMessage());
        }
    }



    /**
     * Shortening log function, in the future this could be replaced by an actual log file that may or may not
     * print to stdout based on an option
     * @param str input object
     */
    private static void log(Object str){
        System.out.println(str);
    }



}

