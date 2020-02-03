package matwably.code_generation.builtin;

import matwably.ast.*;
import matwably.code_generation.wasm.macharray.MachArrayIndexing;
import matwably.util.Util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MatWablyBuiltinGeneratorResult implements McLabBuiltinGenerationResult<MatWablyBuiltinGeneratorResult>{

    private HashSet<TypeUse> locals_instructions = new HashSet<>();
    private List<Instruction> call_instructions = new List<>();
    private List<Instruction> freeingInstructions = new List<>();
    private List<Instruction> allocInstructions = new List<>();



    public static MatWablyBuiltinGeneratorResult merge(MatWablyBuiltinGeneratorResult m1, MatWablyBuiltinGeneratorResult m2){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();

        result.locals_instructions.addAll(m1.locals_instructions);
        result.locals_instructions.addAll(m2.locals_instructions);

        result.allocInstructions.addAll(m1.allocInstructions);
        result.allocInstructions.addAll(m2.allocInstructions);

        result.call_instructions.addAll(m1.call_instructions);
        result.call_instructions.addAll(m2.call_instructions);

        result.freeingInstructions.addAll(m1.freeingInstructions);
        result.freeingInstructions.addAll(m2.freeingInstructions);
        return result;
    }

    @Override
    public MatWablyBuiltinGeneratorResult add(MatWablyBuiltinGeneratorResult m1) {
        this.locals_instructions.addAll(m1.locals_instructions);
        this.allocInstructions.addAll(m1.allocInstructions);
        this.call_instructions.addAll(m1.call_instructions);
        this.freeingInstructions.addAll(m1.freeingInstructions);
        return this;
    }

    public void mergeAllocationInstructions(){
        List<Instruction> result = new List<>();
        result.addAll(this.allocInstructions);
        result.addAll(this.call_instructions);
        result.addAll(this.freeingInstructions);
        call_instructions = result;
        allocInstructions = new List<>();
        freeingInstructions = new List<>();
    }
    @Override
    public MatWablyBuiltinGeneratorResult copy(){
        MatWablyBuiltinGeneratorResult result = new MatWablyBuiltinGeneratorResult();
        result.locals_instructions.addAll(this.locals_instructions);

        result.allocInstructions.addAll(this.allocInstructions);

        result.call_instructions.addAll(this.call_instructions);

        result.freeingInstructions.addAll(this.freeingInstructions);

        return result;
    }


    public MatWablyBuiltinGeneratorResult() { }

    public MatWablyBuiltinGeneratorResult(HashSet<TypeUse> locals, List<Instruction> stmts) {
        this.locals_instructions = locals;
        this.call_instructions = stmts;
    }
    public List<Instruction> getFreeingInstructions() {
        return freeingInstructions;
    }

    public List<Instruction> getAllocInstructions() {
        return allocInstructions;
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
    public void addAllocationInstructions(List<Instruction> stmts){
            this.allocInstructions.addAll(stmts);
    }

    public void addAllocationInstructions(Instruction... inst)
    {
        allocInstructions.addAll(inst);
    }
    public void addDellocationInstructions(List<Instruction>  stmts){
        this.freeingInstructions.addAll(stmts);
    }
    public void addDellocationInstructions(Instruction... stmts){
        this.freeingInstructions.addAll(stmts);
    }
    public void addVectorInputInstructions(List<Instruction> alloc, List<Instruction> dealloc){
        this.allocInstructions.addAll(alloc);
//        this.call_instructions.addAll(alloc);// TODO temporary fix
        this.freeingInstructions.addAll(dealloc);
    }
    public void addLoopDellocationInstruction(Instruction inst)
    {
        freeingInstructions.add(inst);
    }
    public void addLocal(TypeUse local){
        locals_instructions.add(local);
    }
    public void addLocals(TypeUse... loc){
        locals_instructions.addAll(Arrays.asList(loc));
    }
    public void addLocals(List<TypeUse> loc){
        locals_instructions.addAll(StreamSupport.stream(loc.spliterator(),
                true).collect(Collectors.toSet()));
    }

    /**
     * Function generates vector input instructions, adds a
     * @param size
     * @return Generate variable for which the instructions point to
     */
    public String generateVectorInputF64(int size, boolean isRowVector) {
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        this.addLocal(input_argType);
        this.addVectorInputInstructions(
                MachArrayIndexing.createF64Vector(size, input_arg,isRowVector),
                MachArrayIndexing.freeMachArray(input_arg));
        return input_arg;
    }
    public String generateVectorInputI32(int size) {
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        this.addLocal(input_argType);
        this.addVectorInputInstructions(
                MachArrayIndexing.createI32Vector(size, input_arg),
                MachArrayIndexing.freeMachArray(input_arg));
        return input_arg;
    }
    public String generateF64Local(){
        String input_arg = Util.genTypedLocalF64();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new F64());
        this.addLocal(input_argType);
        return input_arg;
    }

    public String generateBoxedScalar( List<Instruction> value){
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        this.addLocal(input_argType);
        this.addVectorInputInstructions(
                MachArrayIndexing.createF64Vector(1, input_arg),
                MachArrayIndexing.freeMachArray(input_arg));
        this.addInstructions(MachArrayIndexing.setArrayIndexF64NoCheck(input_arg, 0,
                value));
        return input_arg;
    }

    public String generateI32Local() {
        String input_arg = Util.genTypedLocalI32();
        TypeUse input_argType = new TypeUse(new Opt<>(new Identifier(input_arg)),new I32());
        this.addLocal(input_argType);
        return input_arg;
    }

}
