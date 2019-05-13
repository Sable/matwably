package matwably.code_generation.builtin.trial.constructors;

import matwably.code_generation.builtin.MatWablyBuiltinGeneratorResult;

public interface ShapeConstructor {
    String get2DConstructorName();
    MatWablyBuiltinGeneratorResult generateScalarExpression();
}
