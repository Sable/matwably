package matwably;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import natlab.toolkits.filehandling.GenericFile;

import java.io.File;
import java.util.ArrayList;

@Parameters(separators = "=")
public final class CommandLineOptions {
    @Parameter(description = "The list of files to process")
    public ArrayList<String> input_files = new ArrayList<String>();
    @Parameter(names = {"-h","--help"}, hidden = true)
    public boolean help = false;

    @Parameter(names = {"--opt-peephole"},
            description = "Option to turn on peephole optimizer, default is on")
    public boolean peephole = false;
    // TODO: Add this capability to the main file.
    @Parameter(names = {"--old-builtins"},
            description = "Option to use old built-in generation")
    public boolean old_builtin = false;

    @Parameter(names = {"--inline-wasm"},
            description = "Option to in-line wasm code in a JavaScript UInt8Array instead of using I/O")
    public boolean inline_wasm = false;

    @Parameter(names = {"--omit-copy-insertion","-ci"},
            description = "Option to omit copy insertion")
    public boolean omit_copy_insertion = false;
    @Parameter(names = {"--verbose","-v"},
            description = "Verbose information for program")
    public boolean verbose = false;
    @Parameter(names = {"--gen-wast"},
            description = "Option to also generate a wat file for the resulting wasm, default is true")
    public boolean generate_wat_file = false;
    // TODO: Actually add support for this.
    @Parameter(names = {"--inline-builtins"},description = "Inlines built-in calls where possible")
    public boolean inline_builtins = false;
    // TODO: Place flag in code to make it viable.
    @Parameter(names = {"--opt-alloc-params"}, description = "Optimizes allocations for built-in call parameters")
    public boolean opt_alloc_params = false;
    // TODO: Place flag in code to allow or disallow memory freeing.
    @Parameter(names = {"--disallow-free"}, description = "Takes away free calls from code, results in memory leakage")
    public boolean disallow_free = false;

    @Parameter(names = {"--variable-elimination","-e"},
            description = "Option to turn on intermediate variable elimination")
    public boolean variable_elimination = true;

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
