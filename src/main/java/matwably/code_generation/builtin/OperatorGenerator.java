package matwably.code_generation.builtin;

import ast.NameExpr;
import matwably.ast.*;
import matwably.util.Util;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class OperatorGenerator extends BuiltinSimplifier {
    private static HashMap<String, NumericInstruction> binaryOps = new HashMap<>();
    private static HashMap<String, NumericInstruction> unaryOps = new HashMap<>();
    private static String[] RETURNS_I32 = {
        "eq","le","lt","gt","ge","ne"//"and","not","or","xor","all","any"
    };
    static {
        binaryOps.put("plus", new Add(new F64()));
        binaryOps.put("minus", new Sub(new F64()));
        binaryOps.put("times", new Mul(new F64()));
        binaryOps.put("mtimes", new Mul(new F64()));
        binaryOps.put("mrdivide", new Div(new F64(),false));
        binaryOps.put("rdivide",new Div(new F64(),false));
        binaryOps.put("le", new Le(new F64(),false));
        binaryOps.put("lt", new Lt(new F64(),false));
        binaryOps.put("ge", new Ge(new F64(),false));
        binaryOps.put("gt", new Gt(new F64(),false));
        binaryOps.put("eq", new Eq(new F64()));
        binaryOps.put("ne", new Ne(new F64()));
        unaryOps.put("uminus", new Neg(new F64()));
        Arrays.sort(RETURNS_I32, String::compareTo);

    }

    public static OperatorGenerator getInstance(){
        return new OperatorGenerator();
    }

    public boolean isSimplifiable(){
        String funcName = callName;
        if (binaryOps.containsKey(funcName) || unaryOps.containsKey(funcName)) {
            for (ast.Expr arg : arguments) {
                String argVar = ((ast.NameExpr) arg)
                        .getName().getID();
                BasicMatrixValue bmv = Util.getBasicMatrixValue(analysis,(ast.ASTNode) node, argVar);
                if (bmv == null || bmv.getShape() == null || !bmv.getShape().isScalar()) {
                    this.isSimplifiable = false;
                    return false;
                }
            }
            this.isSimplifiable = true;
            return true;
        }
        this.isSimplifiable = false;
        return false;
    }

    @Override
    public List<Instruction> simplify() {
        List<Instruction> result = new List<>();
        String funcName = callName;
        if(unaryOps.containsKey(funcName)){
            result.add(new GetLocal(new Idx(Util.getTypedLocalF64(((NameExpr)arguments
                    .getChild(0)).getName().getID()))));
            result.add(unaryOps.get(funcName));
        }else if(binaryOps.containsKey(funcName)){
            result.addAll(expressionGenerator.genNameExpr((NameExpr)arguments.getChild(0),(ast.ASTNode)this.node));
            result.addAll(expressionGenerator.genNameExpr((NameExpr)arguments.getChild(1),(ast.ASTNode)this.node));
//            result.add(new GetLocal(new Idx(Util.getTypedLocalF64(((NameExpr)arguments.getChild(0)).getName().getID()))));
//            result.add(new GetLocal(new Idx(Util.getTypedLocalF64(((NameExpr)arguments.getChild(1)).getName().getID()))));
            result.add(binaryOps.get(funcName));

        }
        if(returnsI32(funcName)) result.add(new Convert(new F64(), new I32(), false));

//        String typedTarget = Util.getTypedLocalF64(targets.getChild(0).getVarName());
//        result.add(new SetLocal(new Idx(typedTarget)));
        return result;
    }

    private static boolean returnsI32(String name){
        return (Arrays.binarySearch(RETURNS_I32, name, Comparator.naturalOrder()) >=0);

    }
    private static boolean isBinaryOp(String name){
        return binaryOps.containsKey(name);
    }
    public static List<Instruction> generateBinOp(String name, NumericInstruction op1, NumericInstruction op2){
        List<Instruction> res;
        if(isBinaryOp(name)){
            res = new List<>(
                op1, op2, binaryOps.get(name)
            );
            if(returnsI32(name)){
                res.add(new Convert(new F64(), new I32(), false));
            }
        }else{
            throw new Error("Binomial operation: "+name+" does not exist");
        }

        return res;
    }
    @Override
    public boolean returnsScalar() {
        return this.isSimplifiable;
    }
}
