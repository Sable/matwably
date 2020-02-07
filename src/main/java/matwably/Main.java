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
import matwably.code_generation.MatWablyProgram;
import matwably.code_generation.ProgramGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import static matwably.util.FileUtility.readStreamIntoHexString;
import static matwably.util.FileUtility.readStreamIntoString;

public class Main {


    public static void main(String[] argv)
    {
        MatWablyCommandLineOptions opts = getMatWablyProgramOptions(argv);
        // Generate ast for MatWably program
        MatWablyProgram program = ProgramGenerator.generate(opts.getGenericFile(),
                opts.getEntryFunctionArgs(), opts);
        // Write to wat file
        writeToFile(opts.basename_output_file +".wat", program.toString(),
                "Error generating programs Wasm text file\n");
        // Compile wast program to wasm
        compileWastProgram(opts.basename_output_file +".wat", opts.basename_output_file +".wasm",
                !opts.generate_wat_file, opts.verbose);
        // Build MatWably program package
        writeToFile(opts.basename_output_file +".js",
                packageMatWablyProgram(opts.basename_output_file +".wasm",
                        program.getEntryFunctionName(),
                        opts.inline_wasm,
                        opts.run_program, opts.runner_arguments), "Error generating final packaged .js file");
    }

    private static MatWablyCommandLineOptions getMatWablyProgramOptions(String[] argv) {
        // Parse command line options
        MatWablyCommandLineOptions opts = new MatWablyCommandLineOptions();
        JCommander optParse = null;
        try{
            optParse = new JCommander(opts, argv);
        }catch(ParameterException e)
        {
            throw new Error(e.getMessage());
        }
        optParse.setProgramName("MatWably");
        // Print usage if asked for help
        if( opts.help )
        {
            optParse.usage();
            System.exit(0);
        }
        return opts;
    }

    private static void writeToFile(String outputFile, String outputFileContent, String errorMessage){
        try{
            // Load file in
            FileWriter wasmOut= new FileWriter(outputFile);
            wasmOut.write(outputFileContent);
            wasmOut.close();
        }catch ( IOException ex){
            throw new Error(errorMessage + ex.getMessage());
        }
    }

    private static String packageMatWablyProgram(String generatedWasmFile, String entryFunctionName,
                                                 boolean inline_wasm, boolean run_program,
                                                 String runner_arguments){
        try{
            StringBuilder outfileBuilder = new StringBuilder();
            // Decide which loader to use
            String loader = readStreamIntoString((inline_wasm)?
                    Main.class.getResourceAsStream("/inline_wasm_module_loader.js"):
                    Main.class.getResourceAsStream("/wasm_module_loader.js"));
            if(inline_wasm) loader = String.format(loader);
            else loader = String.format(loader,  generatedWasmFile);

            if(inline_wasm){
                // Create array with wasm module in hex bytes
                String resultingWasmModule = String.format("let wasmModuleHexString = \"%s\";\n",
                        readStreamIntoHexString(new FileInputStream(new File(generatedWasmFile))));
                Runtime.getRuntime().exec("rm "+generatedWasmFile);
                outfileBuilder.append(resultingWasmModule);
            }
            String program_runner_code = "";
            if(run_program){
                String runner = readStreamIntoString(
                        Main.class.getResourceAsStream("/runner.js"));
                program_runner_code = String.format(runner, entryFunctionName, runner_arguments);
            }

            // Append loader
            outfileBuilder.append(loader);
            // Append library
            String js_support_lib = readStreamIntoString(Main.class.getResourceAsStream("/matmachjs/native/matmachjs-lib.js"));
            outfileBuilder.append(js_support_lib);
            // Append program_runner
            outfileBuilder.append(program_runner_code);
            return outfileBuilder.toString();
        }catch ( IOException ex){
            throw new Error("Error creating packaged file: \n"+ ex.getMessage());
        }}

    private static void compileWastProgram(String inWastFile, String outWasmFile,
                                             boolean rmWastFile, boolean verbose){
        try{
            // Compile wat file to wasm.
            String command = "wat2wasm " +inWastFile+" -o "+ outWasmFile;
            if(verbose) log(command);
            Process process = Runtime.getRuntime().exec(command);
            if(process.waitFor() == 1) throw new Error("Error: compilation of wat file failed: \n"+
                        readStreamIntoString(process.getErrorStream()));
            // Remove wat file
            String deleteCommand = "rm "+inWastFile;
            if(verbose && rmWastFile) log(deleteCommand);
            if(rmWastFile) Runtime.getRuntime().exec(deleteCommand);
        }catch ( IOException | InterruptedException ex){
            throw new Error("Error compiling to wast file to wasm: \n"+ ex.getMessage());
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

