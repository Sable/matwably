package matwably.code_generation;

/**
 * Empty,                Nothing in the loop executes, e.g. for i=[]:1:10. In this case i remains undefined
 * NonMoving,            Bounds are equal so that the statements in the loop only run ones. In this case we can
 *                       skip the loop.
 * Ascending/Descending, Loop goes in increasing or decreasing fashion
 * Unknown,              Bounds are not known statically and the loop is generated dynamically.
 */
public enum LoopDirection {
    Empty,
    NonMoving,
    Ascending,
    Descending,
    Unknown
}
