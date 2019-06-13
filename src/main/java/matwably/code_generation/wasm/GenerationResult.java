package matwably.code_generation.wasm;

import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.ast.TypeUse;

public class GenerationResult {

    private List<Instruction> instructionList = new List<>();
    private List<TypeUse> locals = new List<>();

    public GenerationResult(){
    }

    public List<TypeUse> addLocals(TypeUse... inst){
        return this.locals.addAll(inst);
    }


    public List<TypeUse> addLocalList(List<TypeUse> inst){
        return this.locals.addAll(inst);
    }

    public List<Instruction> addInstruction(Instruction... inst){
        return this.instructionList.addAll(inst);
    }

    public List<Instruction> addInstructionList(List<Instruction> inst){
        return this.instructionList.addAll(inst);
    }

    public List<TypeUse> getLocals() {
        return locals;
    }

    public List<Instruction> getInstructionList() {
        return instructionList;
    }
}
