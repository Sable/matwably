package matwably;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import natlab.toolkits.filehandling.GenericFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Command line options class for the MatWably compiler, based on
 * {@link JCommander}
 */
@Parameters(separators = "=")
public final class MatWablyCommandLineOptions {




    @Parameter(names = {"--disallow-logical"},
            description = "Flag to disallow logical variable in the program")
    public boolean disallow_logicals = false;

    @Parameter(names = {"--opt-peephole"},
            description = "Option to turn on the peephole optimizer, default is on")
    public boolean peephole = false;



    @Parameter(names = {"--omit-copy-insertion","-ci"},
            description = "Option to omit copy insertion")
    public boolean omit_copy_insertion = false;

    @Parameter(names = {"--verbose","-v"},
            description = "Verbose information for program")
    public boolean verbose = false;

    @Parameter(names = {"--ignore-bounds-check","-ib"},
            description = "Compiler option to ignore bound checks")
    public boolean ignore_bounds_check = false;
    @Parameter(names = {"--gen-wast","-w"},
            description = "Option to also generateInstructions a wat file for the resulting wasm, default is true")
    public boolean generate_wat_file = false;

    // TODO: Actually add support for this.
    @Parameter(names = {"--inline-builtins"},description = "Inlines built-in calls where possible")
    public boolean inline_builtins = false;

    // TODO: Place flag in code to make it viable.
    @Parameter(names = {"--loop-invariant-opt","-li"}, description = "Optimizes allocations " +
            "for intermediate parameters in built-in call " +
            "by pushing allocation outside loops")
    public boolean opt_loop_alloc = false;

    @Deprecated
    @Parameter(names = {"--opt-builtin-alloc","-ba"}, description = "Optimizes allocations for built-in call " +
            "re-using particular sites")
    public boolean opt_builtin_alloc_sites = false;

    @Parameter(names = {"--disallow-free", "-nf"}, description = "Takes away free calls from code, results in memory leakage")
    public boolean disallow_free = false;

    @Parameter(names = {"--disallow-variable-elimination","-e"},
            description = "Option to turn on intermediate variable elimination")
    public boolean disallow_variable_elimination = false;



    // TODO Check args for entry function are empty
    @Parameter(names = {"-a","--args"},
            description = "Arguments for entry function, e.g."+"'[\"DOUBLE&1*1&REAL\",\"DOUBLE&1*1&REAL\"]'\n"+
            "\t\t\t\t\t   Representing two parameters, both, double, 1-by-1, real Matlab matrices", required = true)
    public String args = "";

    // TODO: Measure compilation time and time for each optimization
    @Parameter(names={ "--time" }, arity=1, description="time compilation time")
    public boolean timeCompilation = false;

    @Parameter(names={ "--gc-dynamic" ,"-gcd"}, description="Dynamic GC, applies a completely dynamic garbage collection strategy")
    public boolean gc_dynamic = false;

    @Parameter(names={ "--print-memory-info" ,"-m"}, description="Print memory information")
    public boolean print_memory_information = false;

    @Parameter(names={"--run-program", "-r"}, description = "Adds function to run the entry function " +
            "with arguments provided")
    boolean run_program = false;
    /**
     * Runner arguments, format -ra "121,1231,2131", only accepts numbers
     */
    @Parameter(names = {"--runner-arguments","-ra"}, description = "Arguments to pass to the runner")
    String runner_arguments = "";
    /**
     * Basename for input file
     */
    String basename_output_file;
    @Parameter(names = {"-o","--output-file"},description = "Outfile to place code")
    String output_file;
    // Contains the list of input files to process
    @Parameter(description = "Flag to activate range optimization on array " +
            "lookups", names = {"--disallow-range-opt", "-ro"})
    public boolean disallow_range_opt = false;
    // Contains the list of input files to process
    @Parameter(description = "The list of files to process")
    private ArrayList<String> input_files = new ArrayList<String>();

    // Help parameter
    @Parameter(names = {"-h","--help"}, hidden = true)
    boolean help = false;

    @Parameter(names = {"--inline-wasm", "-iw"},
            description = "Option to in-line wasm code in a JavaScript UInt8Array instead of using I/O")
    boolean inline_wasm = false;
    /**
     * Parses the entry function parameters and prints usage if it gails.
     * @param commander {@link JCommander} object
     * @return An array of formatted string arguments.
     */
    String[] getEntryFunctionArgs()
    {
//      "[DOUBLE&1*1&REAL]";
        if(!this.args.isEmpty() && (!this.args.contains("[")|| !this.args.contains("]")))
        {
            System.err.println("Error: Invalid format for arguments to entry function, check usage\n");
            System.exit(1);
        }
        return  this.args.replaceAll("\\[|\\]","")
                .replaceAll("\"","").split(",");
    }

    /**
     * Processes the matlab input files and creates {@link GenericFile}
     * @return Returns a Generic MatLab file {@link GenericFile}
     */
    GenericFile getGenericFile()
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
