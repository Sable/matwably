package matwably.code_generation.builtin;

public interface McLabBuiltinGenerationResult<T extends McLabBuiltinGenerationResult> {
    T add(T m1);

    T copy();

}
