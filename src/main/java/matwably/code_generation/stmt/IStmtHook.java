package matwably.code_generation.stmt;

public interface IStmtHook<T>{
    void  addBeforeInstruction(T instructions);
    void addAfterInstructions(T instructions);

    T getBeforeStmtInstructions();
    T getAfterStmtInstructions();
    T getInBetweenStmtInstructions();

    boolean hasBeforeStmtInstructions();
    boolean hasAfterStmtInstructions();
    boolean hasInBetweenStmtInstructions();

}