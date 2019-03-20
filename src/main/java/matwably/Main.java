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
import matwably.ast.Function;
import matwably.ast.ImportedWat;
import matwably.ast.Module;
import matwably.code_generation.FunctionGenerator;
import matwably.code_generation.wasm.FunctionExport;
import matwably.optimization.peephole.PeepholeOptimizer;
import matwably.pretty.PrettyPrinter;
import natlab.tame.BasicTamerTool;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Main {


    public static void main(String[] argv)
    {
        // Parse command line options
        CommandLineOptions opts = new CommandLineOptions();
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
        Set<String> generated = new HashSet<String>();
        String entryPointName = analysis.getMainNode().getFunction().getName();

        // Iterate and
        int numFunctions = analysis.getNodeList().size();
        Module module = new Module();
        PrettyPrinter prettyPrinter = new PrettyPrinter(module);
        try{
            String builtInDeclations =
                    readStreamIntoString(Main.class.
                            getResourceAsStream("/matmachjs/matmachjs.wat"));
            module.addImportedWat(new ImportedWat(builtInDeclations));
        }catch(IOException ex){
            throw new Error("MatMachJS library .wat file is missing from resources"+
                    ((opts.verbose)?ex.getMessage():""));
        }

        // Running analysis on functions.
        for (int i = 0; i < numFunctions; ++i) {
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis =
                        analysis.getNodeList().get(i).getAnalysis();
            FunctionGenerator gen = new FunctionGenerator(analysis, i, opts);
            String gen_function_name = gen.genFunctionName();
            if (!generated.contains(gen_function_name)) {
                Function func_wasm = gen.getAst();
                module.addFunctions(func_wasm);
                module.addExport(FunctionExport.generate(func_wasm));
                generated.add(gen_function_name);
                if(opts.verbose){
                    log(funcAnalysis.getTree().getPrettyPrinted());
                    log("Generated: " + func_wasm.getIdentifier().getName());
                }
            }
        }

        // Call Peephole optimizer
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
            }else{
                File temp = new File(wasmFile);
                loader = String.format(loader, temp.getName());
            }
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
     * Static function to read a stream in a hex string, used to inline wasm module into a hex string
     * @param inputStream InputStream to be converted
     * @return A hex string representation of the buffer
     * @throws IOException If it fails to read the input stream file, it throws an IOException
     */
    private static String readStreamIntoHexString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }

        return bytesToHex(buf.toByteArray());
    }
    /**
     * Static function to read a stream in a hex string, used to inline wasm module into a hex string
     * @param inputStream InputStream to be converted
     * @return A UTF-8 string representation of the buffer
     * @throws IOException If it fails to read the input stream file, it throws an IOException
     */
    private static String readStreamIntoString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString("UTF-8");
    }
    /**
     * Static function to convert a byte array  in a hex string, used to inline wasm module into a hex string
     * @param bytes Byte array to be converted into hex string
     * @return A hex string representation of the byte array
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
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

