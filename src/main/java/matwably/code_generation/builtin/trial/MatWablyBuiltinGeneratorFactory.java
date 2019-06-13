package matwably.code_generation.builtin.trial;

import ast.ASTNode;
import matwably.code_generation.MatWablyError;
import matwably.code_generation.MatWablyFunctionInformation;
import matwably.code_generation.builtin.trial.binary_op.logical.*;
import matwably.code_generation.builtin.trial.binary_op.logical.bitwise.And;
import matwably.code_generation.builtin.trial.binary_op.numerical.*;
import matwably.code_generation.builtin.trial.concatanation.Cat;
import matwably.code_generation.builtin.trial.concatanation.Horzcat;
import matwably.code_generation.builtin.trial.concatanation.Vertcat;
import matwably.code_generation.builtin.trial.constant.E;
import matwably.code_generation.builtin.trial.constant.False;
import matwably.code_generation.builtin.trial.constant.Pi;
import matwably.code_generation.builtin.trial.constant.True;
import matwably.code_generation.builtin.trial.constructors.*;
import matwably.code_generation.builtin.trial.matrix_operation.*;
import matwably.code_generation.builtin.trial.matrix_query.*;
import matwably.code_generation.builtin.trial.reduction_operation.NumericReduction;
import matwably.code_generation.builtin.trial.unary_operation.logical.Not;
import matwably.code_generation.builtin.trial.unary_operation.numeric.*;
import matwably.code_generation.builtin.trial.utility.Disp;
import matwably.code_generation.builtin.trial.utility.Tic;
import matwably.code_generation.builtin.trial.utility.Toc;
import natlab.tame.tir.TIRCommaSeparatedList;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class MatWablyBuiltinGeneratorFactory {
     private static HashMap<String, Class<? extends MatWablyBuiltinGenerator>> matwablyGeneratorMap = new HashMap<>();
     static{
         // Constructors
         matwablyGeneratorMap.put("ones", Ones.class);
         matwablyGeneratorMap.put("zeros", Zeros.class);
         matwablyGeneratorMap.put("randn", Randn.class);
         matwablyGeneratorMap.put("rand", Rand.class);
         matwablyGeneratorMap.put("randi", Randi.class);
         matwablyGeneratorMap.put("eye", Eye.class);
         matwablyGeneratorMap.put("horzcat", Horzcat.class);
         matwablyGeneratorMap.put("cat", Cat.class);
         matwablyGeneratorMap.put("vertcat", Vertcat.class);
         matwablyGeneratorMap.put("colon", Colon.class);
         matwablyGeneratorMap.put("linspace", Linspace.class);

         //Utility
         matwablyGeneratorMap.put("disp", Disp.class);
         matwablyGeneratorMap.put("tic", Tic.class);
         matwablyGeneratorMap.put("toc", Toc.class);

         // Numerical Binary Operators
         matwablyGeneratorMap.put("uminus", Uminus.class);
         matwablyGeneratorMap.put("plus", Plus.class);
         matwablyGeneratorMap.put("power", Power.class);
         matwablyGeneratorMap.put("ldivide", Ldivide.class);
         matwablyGeneratorMap.put("rdivide", Rdivide.class);
         matwablyGeneratorMap.put("times", Times.class);
         matwablyGeneratorMap.put("minus", Minus.class);
         matwablyGeneratorMap.put("mod", Mod.class);

         // LogicalBinary Operators
         matwablyGeneratorMap.put("and", And.class);
         matwablyGeneratorMap.put("lt", Lt.class);
         matwablyGeneratorMap.put("gt", Gt.class);
         matwablyGeneratorMap.put("le", Le.class);
         matwablyGeneratorMap.put("ge", Ge.class);
         matwablyGeneratorMap.put("eq", Eq.class);
         matwablyGeneratorMap.put("ne", Ne.class);

         // Numerical Unary ops
         matwablyGeneratorMap.put("abs", Abs.class);
         matwablyGeneratorMap.put("ceil", Ceil.class);
         matwablyGeneratorMap.put("cos", Cos.class);
         matwablyGeneratorMap.put("exp", Exp.class);
         matwablyGeneratorMap.put("fix", Fix.class);
         matwablyGeneratorMap.put("floor", Floor.class);
         matwablyGeneratorMap.put("log", Log.class);
         matwablyGeneratorMap.put("round", Round.class);
         matwablyGeneratorMap.put("sin", Sin.class);
         matwablyGeneratorMap.put("sqrt", Sqrt.class);
         matwablyGeneratorMap.put("tan", Tan.class);
         matwablyGeneratorMap.put("Uminus", Uminus.class);
         matwablyGeneratorMap.put("Uplus", Uplus.class);
         // Logical Unary ops
         matwablyGeneratorMap.put("not", Not.class);

        // Constants
         matwablyGeneratorMap.put("false", False.class);
         matwablyGeneratorMap.put("true", True.class);
         matwablyGeneratorMap.put("pi", Pi.class);
         matwablyGeneratorMap.put("e", E.class);


         // Property
         matwablyGeneratorMap.put("size", Size.class);
         matwablyGeneratorMap.put("length", Length.class);
         matwablyGeneratorMap.put("ndims", Ndims.class);
         matwablyGeneratorMap.put("numel", Numel.class);
         matwablyGeneratorMap.put("isempty", Isempty.class);
         matwablyGeneratorMap.put("isscalar", Isscalar.class);
         matwablyGeneratorMap.put("isrow", Isrow.class);
         matwablyGeneratorMap.put("isvector", Isvector.class);
         matwablyGeneratorMap.put("iscolumn", Iscolumn.class);
         matwablyGeneratorMap.put("ismatrix", Ismatrix.class);

         // Reductions
         matwablyGeneratorMap.put("sum", NumericReduction.class);
         matwablyGeneratorMap.put("prod", NumericReduction.class);
         matwablyGeneratorMap.put("mean", NumericReduction.class);
         matwablyGeneratorMap.put("max", NumericReduction.class);
         matwablyGeneratorMap.put("min", NumericReduction.class);

         // Matrix ops
         matwablyGeneratorMap.put("transpose", Transpose.class);
         matwablyGeneratorMap.put("mtimes", Mtimes.class);
         matwablyGeneratorMap.put("mrdivide", Mrdivide.class);
         matwablyGeneratorMap.put("mldivide", Mldivide.class);
         matwablyGeneratorMap.put("mpower", Mpower.class);
         matwablyGeneratorMap.put("isequal", Isequal.class);


     }
    public static MatWablyBuiltinGenerator getGenerator(ASTNode node,
                                                        TIRCommaSeparatedList arguments,
                                                        TIRCommaSeparatedList targs, String callName,
                                                        MatWablyFunctionInformation analyses) {
         boolean isUserDefined = analyses.getFunctionQuery().isUserDefinedFunction(callName);
         if(isUserDefined){
           return new UserDefined(node, arguments,targs, callName, analyses);
         }else if(matwablyGeneratorMap.containsKey(callName)){
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
