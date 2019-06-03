package matwably.analysis.memory_management;

import matwably.ast.Instruction;
import matwably.ast.List;

public class GCInstructions {

    private List<Instruction> beforeStmtInstructions = new List<>();
    private List<Instruction> afterStmtInstructions = new List<>();
    // Special case with user defined functions, we must set external flag.
    private List<Instruction> inBetweenCallStmtInstructions = new List<>();

    public List<Instruction> getBeforeStmtInstructions() {
        return beforeStmtInstructions;
    }

    public List<Instruction> getAfterStmtInstructions() {
        return afterStmtInstructions;
    }
    public boolean hasBeforeInstructions(){
        return beforeStmtInstructions.getNumChild() > 0;
    }

    public boolean hasAfterInstructions(){
        return afterStmtInstructions.getNumChild() > 0;
    }
    public boolean hasInBetweenInstructions(){
        return inBetweenCallStmtInstructions.getNumChild() > 0;
    }
    public void addBeforeInstruction(List<Instruction> instructions){
        beforeStmtInstructions.addAll(instructions);
    }

    public void addAfterInstructions(List<Instruction> instructions){
        afterStmtInstructions.addAll(instructions);
    }

    public void addInBetweenStmtInstructions(Instruction... instructions){
        inBetweenCallStmtInstructions.addAll(instructions);
    }


    public void addBeforeInstruction(Instruction... instructions){
        beforeStmtInstructions.addAll(instructions);
    }

    public void addAfterInstructions(Instruction... instructions){
        afterStmtInstructions.addAll(instructions);
    }

    public List<Instruction> getInBetweenCallStmtInstructions() {
        return inBetweenCallStmtInstructions;
    }

}
