package matwably.analysis.memory_management;

import matwably.ast.Instruction;
import matwably.ast.List;

public class GCInstructions implements GCCalls<List<Instruction>>{

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
    public List<Instruction> getInBetweenStmtInstructions() {
        return inBetweenCallStmtInstructions;
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




    public void addBeforeInstruction(List<Instruction> instructions){
        beforeStmtInstructions.addAll(instructions);
    }

    public void addAfterInstructions(List<Instruction> instructions){
        afterStmtInstructions.addAll(instructions);
    }


    public void addInBetweenStmtInstructions(List<Instruction> instructions){
        inBetweenCallStmtInstructions.addAll(instructions);
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
