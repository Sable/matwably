package matwably.code_generation.wasm;

import matwably.ast.*;

public enum MatMachJSError {
    ARRAY_ACCESS_ZERO_OR_NEGATIVE_INDEX(3, "Subscript indices must either be " +
            "real positive integers or logicals"),
    ARRAY_ACCESS_EXCEEDS_DIMENSION(4, "Index exceeds matrix dimensions");

    private final String description;
    private final int code;

    MatMachJSError(int i, String s) {
        this.code = i;
        this.description = s;
    }


    public static List<Instruction> generateThrowError(MatMachJSError errorCode,
                                                       List<Instruction> condition){
        If ifStmt = new If();
        ifStmt.addInstructionsIf(new ConstLiteral(new I32(), errorCode.getCode()));
        ifStmt.addInstructionsIf(new Call(new Idx("throwError")));
        condition.addAll(ifStmt);
        return condition;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "MatMachJSError{" +
                "description='" + description + '\'' +
                ", code=" + code +
                '}';
    }
}
