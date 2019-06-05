package matwably.analysis.memory_management;


public interface GCCalls<T>{
    void  addBeforeInstruction(T instructions);
    void addAfterInstructions(T instructions);
    void addInBetweenStmtInstructions(T instructions);

    T getBeforeStmtInstructions();
    T getAfterStmtInstructions();
    T getInBetweenStmtInstructions();

    boolean hasBeforeStmtInstructions();
    boolean hasAfterStmtInstructions();
    boolean hasInBetweenStmtInstructions();


}
