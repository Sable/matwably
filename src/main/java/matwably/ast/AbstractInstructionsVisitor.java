package matwably.ast;

public interface AbstractInstructionsVisitor<Res> {
    public  Res transform(List<Instruction> list_inst);
}
