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
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
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
import java.util.ArrayList;
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
                FunctionGenerator gen = new FunctionGenerator(analysis, i);
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

@Parameters(separators = " ")
final class CommandLineOptions {
    @Parameter(description = "The list of files to process")
    public ArrayList<String> input_files = new ArrayList<String>();
    @Parameter(names = {"-h","--help"}, hidden = true)
    public boolean help = false;

    @Parameter(names = {"-p","--peephole"}, description = "Option to turn on peephole optimizer, default is on")
    public boolean peephole = false;

    @Parameter(names = {"-o","--output-file"},description = "Outfile to place code")
    public String output_file;
    public String basename_output_file;
    // TODO Check args for entry function are empty
    @Parameter(names = {"-a","--args"},
            description = "Arguments for entry function, e.g."+"'[\"DOUBLE&1*1&REAL\",\"DOUBLE&1*1&REAL\"]'\n"+
            "\t\t\t\t\t   Representing two parameters, both, double, 1-by-1, real Matlab matrices")
    public String args = "";

    @Parameter(names={ "--time" }, arity=1, description="time compilation time")
    public boolean timeCompilation = false;
    /**
     * Parses the entry function parameters
     * @param commander
     * @return
     */
    public String[] getEntryFunctionArgs( JCommander commander)
    {
//      "[DOUBLE&1*1&REAL]";
        if(!this.args.isEmpty() && (!this.args.contains("[")|| !this.args.contains("]")))
        {
            System.err.println("Error: Invalid format for arguments to entry function, check usage\n");
            commander.usage();
            System.exit(1);
        }
        return  this.args.replaceAll("\\[|\\]","")
                .replaceAll("\"","").split(",");
    }

    /**
     * Processes the matlab input files and creates generic matlab files
     * @return Returns a Generic MatLab file using natlab.toolkits.filehandling
     * @see natlab.toolkits.filehandling.GenericFile
     */
    public GenericFile getGenericFile()
    {
        if(this.input_files.size() == 0)
        {
            System.err.println("Error: No input files");
            System.exit(1);
        }else if(this.input_files.size() > 1){
            System.err.println("Error: Only one main function file must be passed");
            System.exit(1);
        }
        String file_path = this.input_files.get(0);
        GenericFile gen_file = GenericFile.create(file_path);
        if(!gen_file.exists()){
            System.err.printf("Error: Path to Matlab file %s does not exist, \n",file_path);
            System.exit(0);
        }
        File file = new File(file_path);
        if(this.output_file == null){
            this.basename_output_file = file.getName();
            if(this.basename_output_file.contains("."))
                this.basename_output_file =
                        this.basename_output_file.substring(0, this.basename_output_file.lastIndexOf('.'));
        }else{
            file = new File(this.output_file);
            basename_output_file = file.getParent()+"/"+file.getName();

            this.basename_output_file =
                    this.basename_output_file.substring(0, this.basename_output_file.lastIndexOf('.'));
        }
        return gen_file;
    }



}
