package matwably.code_generation.wasm;

import matwably.ast.Idx;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.util.Util;


class LoopStructure {
    private final String loopVar;
    private final int low;
    private final int step;
    private final int high;
    private final Idx breakLabel;
    private final Idx continueLabel;
    private List<Instruction> instructionList;

    private LoopStructure(String loopVar,
                  int low,
                  int step,
                  int high,
                  Idx breakLabel,
                  Idx continueLabel,
                  List<Instruction> instructionList){
        this.loopVar = loopVar;
        this.low = low;
        this.step = step;
        this.high = high;
        this.breakLabel = breakLabel;
        this.continueLabel = continueLabel;
        this.instructionList = instructionList;
    }


    public List<Instruction> addInstructions(List<Instruction> instructions){
        this.instructionList.addAll(instructions);
        return this.instructionList;
    }
    public List<Instruction> addInstructions(Instruction... instructions){
        this.instructionList.addAll(instructions);
        return this.instructionList;
    }

    public static class Builder{
        private final String loopVar;
        private int low;
        private int step;
        private int high;
        private Idx breakLabel;
        private Idx continueLabel;
        private List<Instruction> instructionList = new List<>();
        private Idx highIdx;
        private Idx lowIdx;
        private Idx stepIdx;


        Builder(String loopVar) {
            this.loopVar = loopVar;
            this.step = 1;
            this.breakLabel = new Idx(Util.genID());
            this.continueLabel = new Idx(Util.genID());
        }
        Builder high(int high){
            this.high = high;
            return this;
        }
        public Builder step(int step){
            this.step = step;
            return this;
        }
        Builder body(List<Instruction> instructions){
            this.instructionList = instructions;
            return this;
        }
        Builder low(int low){
            this.low = low;
            return this;
        }
        LoopStructure build(){
            return new  LoopStructure(this.loopVar,
                    low,
                    step,
                    high,
                    this.breakLabel,
                    this.continueLabel,
                    instructionList);
        }

    }

    public Idx getBreakLabel() {
        return breakLabel;
    }

    public Idx getContinueLabel() {
        return continueLabel;
    }

    public List<Instruction> getInstructionList() {
        return instructionList;
    }

    public String getLoopVar() {
        return loopVar;
    }

    public int getLow() {
        return low;
    }

    public int getStep() {
        return step;
    }

    public int getHigh() {
        return high;
    }
}
