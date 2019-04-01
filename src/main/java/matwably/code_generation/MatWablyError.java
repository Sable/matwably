package matwably.code_generation;

import ast.ASTNode;

public class MatWablyError extends Error {

    public MatWablyError(String msg, ASTNode node){
        super(msg+"\nat TamerIR: "+node.getPrettyPrinted());
    }

    public static class NotEnoughInputArguments extends MatWablyError{

        public NotEnoughInputArguments(String functionName, ASTNode node) {
            super("Error using "+functionName+"\nNot enough input arguments.", node);
        }
    }
    public static class TooManyInputArguments extends MatWablyError{

        public TooManyInputArguments(String functionName, ASTNode node) {
            super("Error using "+functionName+"\nToo many input arguments.", node);
        }
    }
    public static class UnsupportedBuiltinCall extends MatWablyError {

        public UnsupportedBuiltinCall(String functionName, ASTNode node) {
            super("Built-in call to "+functionName+" not supported currently by the MatWably compiler.", node);
        }
    }
    public static class UnsupportedNDimensional extends MatWablyError {

        public UnsupportedNDimensional(String functionName, ASTNode node) {
            super("Error using "+functionName+"\nN-dimensional arrays are not supported.", node);
        }
    }

    public static class TooManyOutputArguments extends MatWablyError {
        public TooManyOutputArguments(String callName, ASTNode node) {
            super("Error using "+callName+"\nToo many output arguments.", node);

        }
    }
    public static class MatrixDimensionsMustAgree extends MatWablyError {
        public MatrixDimensionsMustAgree(String callName, ASTNode node) {
            super("Error using "+callName+"\n Matrix dimensions must agree.", node);

        }
    }

}
