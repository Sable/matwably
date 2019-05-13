package matwably.code_generation.builtin;

import matwably.ast.*;
import matwably.code_generation.builtin.trial.McLabBuiltinGenerationResult;
import matwably.code_generation.wasm.MatWablyArray;
import matwably.util.Util;

import java.util.Arrays;
import java.util.HashSet;

public class MatWablyBuiltinGeneratorResult implements McLabBuiltinGenerationResult<MatWablyBuiltinGeneratorResult>{

    private HashSet<TypeUse> locals_instructions = new HashSet<>();
    private List<Instruction> call_instructions = new List<>();
    private List<Instruction> free_input_vec_instructions = new List<>();
    private List<Instruction> alloc_input_vec_instructions = new List<>();



    public static MatWablyBuiltinGeneratorResult merge(MatWablyBuiltinGeneratorResult m1, MatWablyBuiltinGeneratorResult m2){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();

        result.locals_instructions.addAll(m1.locals_instructions);
        result.locals_instructions.addAll(m2.locals_instructions);

        result.alloc_input_vec_instructions.addAll(m1.alloc_input_vec_instructions);
        result.alloc_input_vec_instructions.addAll(m2.alloc_input_vec_instructions);

        result.call_instructions.addAll(m1.call_instructions);
        result.call_instructions.addAll(m2.call_instructions);

        result.free_input_vec_instructions.addAll(m1.free_input_vec_instructions);
        result.free_input_vec_instructions.addAll(m2.free_input_vec_instructions);
        return result;
    }

    @Override
    public MatWablyBuiltinGeneratorResult add(MatWablyBuiltinGeneratorResult m1) {
        this.locals_instructions.addAll(m1.locals_instructions);
        this.alloc_input_vec_instructions.addAll(m1.alloc_input_vec_instructions);
        this.call_instructions.addAll(m1.call_instructions);
        this.free_input_vec_instructions.addAll(m1.free_input_vec_instructions);
        return this;
    }

    public void mergeAllocationInstructions(){
        List<Instruction> result = new List<>();
        result.addAll(this.alloc_input_vec_instructions);
        result.addAll(this.call_instructions);
        result.addAll(this.free_input_vec_instructions);
        call_instructions = result;
        alloc_input_vec_instructions = new List<>();
        free_input_vec_instructions = new List<>();
    }
    @Override
    public MatWablyBuiltinGeneratorResult copy(){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.locals_instructions.addAll(this.locals_instructions);

        result.alloc_input_vec_instructions.addAll(this.alloc_input_vec_instructions);

        result.call_instructions.addAll(this.call_instructions);

        result.free_input_vec_instructions.addAll(this.free_input_vec_instructions);

        return result;
    }


    public MatWablyBuiltinGeneratorResult() { }

    public MatWablyBuiltinGeneratorResult(HashSet<TypeUse> locals, List<Instruction> stmts) {
        this.locals_instructions = locals;
        this.call_instructions = stmts;
    }
    public List<Instruction> getFree_input_vec_instructions() {
        return free_input_vec_instructions;
    }

    public List<Instruction> getAlloc_input_vec_instructions() {
        return alloc_input_vec_instructions;
    }
    public MatWablyBuiltinGeneratorResult(List<Instruction> stmts) {
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
    public void addLoopAllocationInstructions(List<Instruction> stmts){
            this.alloc_input_vec_instructions.addAll(stmts);
    }

    public void addLoopAllocationInstruction(Instruction inst)
    {
        alloc_input_vec_instructions.add(inst);
    }
    public void addLoopDellocationInstructions(List<Instruction>  stmts){
        for (Instruction instruction : stmts) {
            this.free_input_vec_instructions.add(instruction);
        }
    }
    public void addVectorInputInstructions(List<Instruction> alloc, List<Instruction> dealloc){
        this.alloc_input_vec_instructions.addAll(alloc);
        this.free_input_vec_instructions.addAll(dealloc);
    }
    public void addLoopDellocationInstruction(Instruction inst)
    {
        free_input_vec_instructions.add(inst);
    }
    public void addLocal(TypeUse local){
        locals_instructions.add(local);
    }
    public void addLocals(TypeUse... loc){
        locals_instructions.addAll(Arrays.asList(loc));
    }

    /**
     * Function generates vector input instructions, adds a
     * @param size
     * @return
     */
    public String generateVectorInputF64(int size, boolean isRowVector) {
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        this.addLocal(input_argType);
        this.addVectorInputInstructions(
                MatWablyArray.createF64Vector(size, input_arg,isRowVector),
                MatWablyArray.freeMachArray(input_arg));
        return input_arg;
    }
    public String generateVectorInputI32(int size) {
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        this.addLocal(input_argType);
        this.addVectorInputInstructions(
                MatWablyArray.createI32Vector(size, input_arg),
                MatWablyArray.freeMachArray(input_arg));
        return input_arg;
    }
    public String generateF64Local(){
        String input_arg = Util.genTypedLocalF64();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new F64());
        this.addLocal(input_argType);
        return input_arg;
    }

    public String generateBoxedScalar( List<Instruction> scalar_instruction){
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        this.addLocal(input_argType);
        this.addVectorInputInstructions(
                MatWablyArray.createF64Vector(1, input_arg),
                MatWablyArray.freeMachArray(input_arg));
        this.addInstructions(MatWablyArray.setArrayIndexF64(input_arg, 0,scalar_instruction));
        return input_arg;
    }
}
