package matwably.code_generation.builtin;

import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.ast.TypeUse;

import java.util.Arrays;
import java.util.HashSet;

public class ResultWasmGenerator {
    private HashSet<TypeUse> locals_instructions = new HashSet<>();
    private List<Instruction> call_instructions = new List<>();
    private List<Instruction> free_input_vec_instructions = new List<>();
    private List<Instruction> alloc_input_vec_instructions = new List<>();

    public ResultWasmGenerator() { }

    public ResultWasmGenerator(HashSet<TypeUse> locals, List<Instruction> stmts) {
        this.locals_instructions = locals;
        this.call_instructions = stmts;
    }
    public List<Instruction> getFree_input_vec_instructions() {
        return free_input_vec_instructions;
    }

    public List<Instruction> getAlloc_input_vec_instructions() {
        return alloc_input_vec_instructions;
    }
    public ResultWasmGenerator(List<Instruction> stmts) {
        this.call_instructions = stmts;
    }
    public HashSet<TypeUse> getLocals() {
        return locals_instructions;
    }
    public List<Instruction> getInstructions() {
        return call_instructions;
    }
    public void addInstructions(List<Instruction> stmt){
        call_instructions.addAll(stmt);
    }
    public void addInstructions(Instruction... stmts){
        for (Instruction instruction : stmts) {
            this.call_instructions.add(instruction);
        }
    }

    public void addInstruction(Instruction inst)
    {
        call_instructions.add(inst);
    }
    public void addLocal(TypeUse local){
        locals_instructions.add(local);
    }
    public void addLocals(TypeUse... loc){
        locals_instructions.addAll(Arrays.asList(loc));
    }
}
