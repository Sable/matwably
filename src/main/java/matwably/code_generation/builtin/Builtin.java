//package matwably.code_generation.builtin;
//
//
//import natlab.tame.builtin.BuiltinVisitor;
//
//public abstract class Builtin {
//
//    static {
//
//
//    }
//    public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
//        return visitor.caseBuiltin(this,arg);
//    }
//
//    public BuiltinInputHandler input_handler;
//
//    public abstract T generateCall();
//
//    public abstract T generateInputParameters();
//
//    public abstract boolean hasCanonicalForm();
//
//    public abstract boolean isInCanonicalForm();
//
//    public abstract boolean isSimplifiable();
//
//    public abstract Builtin getInstance();
//
//    abstract public T simplify();
//    public static abstract class ByShapeAndTypeMatrixCreation<T> extends Builtin<T>{
//        public boolean hasCanonicalForm(){
//            return true;
//        }
//
//
//    }
//    public static class False<T> extends ByShapeAndTypeMatrixCreation<T> {
//
//        private False instance;
//
//
//        @Override
//        public T generateCall() {
//            return null;
//        }
//
//        @Override
//        public T generateInputParameters() {
//            return null;
//        }
//
//        @Override
//        public boolean isInCanonicalForm() {
//            return false;
//        }
//
//        @Override
//        public boolean isSimplifiable() {
//            return false;
//        }
//
//        @Override
//        public Builtin getInstance() {
//            return null;
//        }
//
//        @Override
//        public T simplify() {
//            return null;
//        }
//    }
//
//
//    public static abstract class True<T, Arg> extends ByShapeAndTypeMatrixCreation<T> {
//        private False instance;
//
//
//    }
//    public static abstract class Ones<T, Arg> extends ByShapeAndTypeMatrixCreation<T> {
//        private Ones instance;
//
//    }
//
//    public static abstract class Zeros<T, Arg> extends ByShapeAndTypeMatrixCreation<T> {
//        private Zeros instance;
//
//
//    }
//    public static abstract class Eye<T, Arg> extends ByShapeAndTypeMatrixCreation<T> {
//        private Eye instance;
//
//
//    }
//    public static abstract class Randn<T, Arg> extends ByShapeAndTypeMatrixCreation<T> {
//        private Randn instance;
//
//
//    }
//    public static abstract class Rand<T, Arg> extends ByShapeAndTypeMatrixCreation<T> {
//        private Rand instance;
//
//
//    }
//}
