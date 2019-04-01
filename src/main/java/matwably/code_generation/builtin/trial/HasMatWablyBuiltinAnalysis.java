package matwably.code_generation.builtin.trial;

/**
 *
 */
public interface HasMatWablyBuiltinAnalysis {

    /**
     * This is for functions generator to perform an analysis of the call before generation.
     * For the purposes of optimizing further analysis.
     */
    void analyze();
}
