package matwably.code_generation.builtin;

import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.ast.TypeUse;

import java.util.Arrays;
import java.util.HashSet;

public class ResultWasmGenerator {
    HashSet<TypeUse> locals = new HashSet<>();
    List<Instruction> stmts = new List<>();

    public ResultWasmGenerator() { }

    public ResultWasmGenerator(HashSet<TypeUse> locals, List<Instruction> stmts) {
        this.locals = locals;
        this.stmts = stmts;
    }
    public ResultWasmGenerator(List<Instruction> stmts) {
        this.stmts = stmts;
    }
    public HashSet<TypeUse> getLocals() {
        return locals;
    }
    public List<Instruction> getInstructions() {
        return stmts;
    }
    public void addInstructions(List<Instruction> stmt){
            stmts.addAll(stmt);
    }
    public void addInstructions(Instruction... stmts){
        for (Instruction instruction : stmts) {
            this.stmts.add(instruction);
        }
    }

    public void addInstruction(Instruction inst)
    {
        stmts.add(inst);
    }
    public void addLocal(TypeUse local){
        locals.add(local);
    }
    public void addLocals(TypeUse... loc){
        locals.addAll(Arrays.asList(loc));
    }
}
