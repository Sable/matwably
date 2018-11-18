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

    private static void log(Object str){
        System.out.println(str);
    }
    public static void main(String[] argv)
    {

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


        if( opts.help )
        {
            optParse.usage();
            System.exit(0);
        }

        String[] args_entry_function = opts.getEntryFunctionArgs(optParse);
        GenericFile gfile = opts.getGenericFile();
        FileEnvironment fenv = new FileEnvironment(gfile);
        ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool.analyze(args_entry_function, fenv);

        Set<String> generated = new HashSet<String>();
        String entryPointName = analysis.getMainNode().getFunction().getName();

        int numFunctions = analysis.getNodeList().size();
        Module module = new Module();
        PrettyPrinter prettyPrinter = new PrettyPrinter(module);
        try{
            String builtInDeclations =  readStreamIntoString(Main.class.getResourceAsStream("/builtins.wat"));//readStreamIntoString("builtins.wat");
            module.addImportedWat(new ImportedWat(builtInDeclations));
        }catch(IOException ex){
            throw new Error(ex);
        }

        for (int i = 0; i < numFunctions; ++i) {
            IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> funcAnalysis =
                        analysis.getNodeList().get(i).getAnalysis();
            String functionName = funcAnalysis.getTree().getName().getID();
            if (!generated.contains(functionName)) {
                FunctionGenerator gen = new FunctionGenerator(analysis, i, opts);
                Function func_wasm = gen.getAst();
                module.addFunctions(func_wasm);
                module.addExport(FunctionExport.generate(func_wasm));
                generated.add(functionName);
                System.out.println(funcAnalysis.getTree().getPrettyPrinted());
                System.out.println("Generated: " + func_wasm.getIdentifier().getName());
            }
        }

        // Call Peephole optimizer
        if(opts.peephole){
            PeepholeOptimizer peep = new PeepholeOptimizer(module);
            peep.optimize();
            System.out.println(peep.getFrequenciesAsCsvString());
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
            Process process = Runtime.getRuntime().exec(
                    "wat2wasm " +opts.basename_output_file+".wat -o "+ wasmFile);
            System.out.println("wat2wasm " +opts.basename_output_file+".wat -o "+ wasmFile);
            int exitFlag = process.waitFor();
            if(exitFlag == 1)
                throw new Error("Error compiling wat file: \n"+
                        readStreamIntoString(process.getErrorStream()));
            String loader = readStreamIntoString(Main.class.getResourceAsStream("/wasm_loader.js"));
            File temp = new File(wasmFile);

            loader = String.format(loader, temp.getName(),entryPointName);
            FileWriter wasmOut= new FileWriter(opts.basename_output_file+".js");
            wasmOut.write(loader);
            wasmOut.close();
        }catch ( IOException | InterruptedException ex){
            throw new Error("Error compiling to wasm: \n"+ ex.getMessage());
        }
    }
    private static String readStreamIntoString(InputStream inputStream) throws IOException {
//        InputStream inputStream = Main.class.getResourceAsStream("/" + file);
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString("UTF-8");
    }



}

