package matwably.code_generation.builtin.trial;

public interface McLabBuiltinGenerationResult<T extends McLabBuiltinGenerationResult> {

    T add(T m1);

    T copy();

}
