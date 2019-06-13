package matwably.code_generation.stmt;

import matwably.ast.Instruction;
import matwably.ast.List;

/**
 * Places Stmt hooks together
 */
public class StmtHook implements IStmtHook<List<Instruction>> {


    private List<Instruction> beforeStmtInstructions = new List<>();
    private List<Instruction> afterStmtInstructions = new List<>();
    // Special case with user defined functions, we must set external flag.
    private List<Instruction> inBetweenCallStmtInstructions = new List<>();

    public StmtHook(StmtHook hook) {
        beforeStmtInstructions.addAll(hook.beforeStmtInstructions);
        afterStmtInstructions.addAll(hook.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(hook.inBetweenCallStmtInstructions);
    }

    public static StmtHook merge(StmtHook other, StmtHook stmtHook){
        List<Instruction> beforeStmtInstructions = new List<>();
        List<Instruction> afterStmtInstructions = new List<>();
        // Special case with user defined functions, we must set external flag.
        List<Instruction> inBetweenCallStmtInstructions = new List<>();
        beforeStmtInstructions.addAll(other.beforeStmtInstructions);
        afterStmtInstructions.addAll(other.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(other.inBetweenCallStmtInstructions);
        beforeStmtInstructions.addAll(stmtHook.beforeStmtInstructions);
        afterStmtInstructions.addAll(stmtHook.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(stmtHook.inBetweenCallStmtInstructions);
        return new StmtHook(beforeStmtInstructions,
                afterStmtInstructions, inBetweenCallStmtInstructions);
    }

    public StmtHook(){ }
    public StmtHook(List<Instruction> bef, List<Instruction> aft,
                    List<Instruction> inBet){
        this.beforeStmtInstructions = new List<Instruction>().addAll(bef);
        this.afterStmtInstructions = new List<Instruction>().addAll(aft);
        this.inBetweenCallStmtInstructions = new List<Instruction>().addAll(inBet);

    }




    public boolean hasBeforeStmtInstructions(){
        return beforeStmtInstructions.getNumChild() > 0;
    }
    public boolean hasAfterStmtInstructions(){
        return afterStmtInstructions.getNumChild() > 0;
    }
    public boolean hasInBetweenStmtInstructions(){
        return inBetweenCallStmtInstructions.getNumChild() > 0;
    }



    public StmtHook add(StmtHook other) {
        beforeStmtInstructions.addAll(other.beforeStmtInstructions);
        afterStmtInstructions.addAll(other.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(other.inBetweenCallStmtInstructions);
        return this;
    }


    public void addBeforeInstruction(List<Instruction> instructions){
        beforeStmtInstructions.addAll(instructions);
    }

    public void addAfterInstructions(List<Instruction> instructions){
        afterStmtInstructions.addAll(instructions);
    }


    public void addInBetweenStmtInstructions(List<Instruction> instructions){
        inBetweenCallStmtInstructions.addAll(instructions);
    }

    @Override
    public List<Instruction> getBeforeStmtInstructions() {
        return beforeStmtInstructions;
    }

    @Override
    public List<Instruction> getAfterStmtInstructions() {
        return afterStmtInstructions;
    }

    @Override
    public List<Instruction> getInBetweenStmtInstructions() {
        return inBetweenCallStmtInstructions;
    }

    public void addInBetweenStmtInstructions(Instruction... instructions) {
        inBetweenCallStmtInstructions.addAll(instructions);

    }

    public void addBeforeInstruction(Instruction... instructions){
        beforeStmtInstructions.addAll(instructions);
    }

    public void addAfterInstructions(Instruction... instructions){
        afterStmtInstructions.addAll(instructions);
    }



}
