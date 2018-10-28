package matwably.optimization.peephole;

import matwably.ast.Instruction;
import matwably.ast.List;

public class PeepholeTransformationResult {
    public void setLength(int length_tranformation) {
        this.length_tranformation = length_tranformation;
    }

    private int length_tranformation;
    private List<Instruction> new_nodes = new List<>();
    private boolean transformed = false;

    public boolean isTransformed() {
        return transformed;
    }

    public int getLength() {
        return length_tranformation;
    }
    public List<Instruction> getInstructions() {
        return new_nodes.copy();
    }
    public void addInstruction(Instruction inst){
        transformed = true;
        length_tranformation++;
        new_nodes.add(inst);
    }

    @Override
    public String toString() {
        return "Transformed: "+transformed+", length: " + length_tranformation;
    }
}
