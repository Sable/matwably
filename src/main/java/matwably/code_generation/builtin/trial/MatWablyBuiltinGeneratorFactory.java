package matwably.code_generation.builtin.trial;

import ast.ASTNode;
import matwably.analysis.MatWablyFunctionInformation;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.builtin.trial.binary_op.logical.*;
import matwably.code_generation.builtin.trial.binary_op.numerical.*;
import matwably.code_generation.builtin.trial.constructors.*;
import matwably.code_generation.builtin.trial.unary_op.Uminus;
import matwably.code_generation.builtin.trial.utility.Disp;
import natlab.tame.tir.TIRCommaSeparatedList;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class MatWablyBuiltinGeneratorFactory {
     private static HashMap<String, Class<? extends MatWablyBuiltinGenerator>> matwablyGeneratorMap = new HashMap<>();
     static{
         // Constructors
         matwablyGeneratorMap.put("ones", OnesGenerator.class);
         matwablyGeneratorMap.put("zeros", ZerosGenerator.class);
         matwablyGeneratorMap.put("randn", RandnGenerator.class);
         matwablyGeneratorMap.put("rand", RandGenerator.class);
         matwablyGeneratorMap.put("eye", EyeGenerator.class);
         matwablyGeneratorMap.put("horzcat", Horzcat.class);
         matwablyGeneratorMap.put("vertcat", Vertcat.class);
         matwablyGeneratorMap.put("colon", Colon.class);
         //Utility
         matwablyGeneratorMap.put("disp", Disp.class);

         // Numerical Binary Operators
         matwablyGeneratorMap.put("uminus", Uminus.class);
         matwablyGeneratorMap.put("plus", Plus.class);
         matwablyGeneratorMap.put("power", Power.class);
         matwablyGeneratorMap.put("ldivide", Ldivide.class);
         matwablyGeneratorMap.put("rdivide", Rdivide.class);
         matwablyGeneratorMap.put("times", Times.class);
         matwablyGeneratorMap.put("minus", Minus.class);
         // LogicalBinary Operators
         matwablyGeneratorMap.put("lt", Lt.class);
         matwablyGeneratorMap.put("gt", Gt.class);
         matwablyGeneratorMap.put("le", Le.class);
         matwablyGeneratorMap.put("ge", Ge.class);
         matwablyGeneratorMap.put("eq", Eq.class);
         matwablyGeneratorMap.put("ne", Ne.class);
     }
    public static MatWablyBuiltinGenerator getGenerator(ASTNode node,
                                                        TIRCommaSeparatedList arguments,
                                                        TIRCommaSeparatedList targs, String callName,
                                                        MatWablyFunctionInformation analyses) {
        if(matwablyGeneratorMap.containsKey(callName)){
            try{
                Class<? extends MatWablyBuiltinGenerator> classGenerator = matwablyGeneratorMap.get(callName);
                Constructor generatorConstructor  = classGenerator.getConstructor(
                                                        ASTNode.class,
                                                        TIRCommaSeparatedList.class,
                                                        TIRCommaSeparatedList.class,
                                                        String.class,
                                                        MatWablyFunctionInformation.class);
                return (MatWablyBuiltinGenerator) generatorConstructor.newInstance(node, arguments,targs, callName, analyses);
            }catch(Exception e){
                throw new MatWablyError.UnsupportedBuiltinCall(callName, node);
            }
        }
        throw new MatWablyError.UnsupportedBuiltinCall(callName, node);
    }
}
