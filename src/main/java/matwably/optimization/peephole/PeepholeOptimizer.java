package matwably.optimization.peephole;

import matwably.ast.Function;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.ast.Module;
import matwably.optimization.peephole.patterns.GetSetLocal;

import java.util.ArrayList;
import java.util.HashSet;

public class PeepholeOptimizer {
    java.util.HashSet<Class<? extends PeepholePattern>> patterns_set = new HashSet<>();
    java.util.ArrayList<PeepholePattern> patterns = new ArrayList<>();
    public int frequencies[] = {};
    Module wasm_module;
    public final int MAX_PATTERNS = 100;
    public PeepholeOptimizer(Module wasm_mod){
        this(wasm_mod,GetSetLocal.class);
    }
    public PeepholeOptimizer(Module wasm_mod, Class<? extends PeepholePattern>... patternList){
        if(wasm_mod == null) throw new Error("Wasm module must be defined");
        wasm_module = wasm_mod;
        int i = 0;
        for (Class<? extends PeepholePattern> pattern : patternList) {
            if(!patterns_set.contains(pattern)) {
                if(i == MAX_PATTERNS) throw new Error("Maxium number of peephole patterns allowed is 100");
                patterns_set.add(pattern);
                patterns.add(buildDependecy(pattern));
                i++;
            }
        }
        frequencies = new int[patterns.size()];
    }
    public void optimize(){
        System.out.println(wasm_module.hasFunctions());
        if(wasm_module.hasFunctions()){
        boolean changed = true;
            while(changed){
                for(Function func:  wasm_module.getFunctionsList()){
                    changed = optimizeInstructions(
                            func.getBody().getInstructionsList());
                }
            }
        }
    }
    private void replaceInstructions(int start_index, int length_transformation,
                                     List<Instruction> new_instructions, List<Instruction> list){
        if(length_transformation < 1) throw new Error("Transformation length must replace " +
                "more than 1 instructions");
        for (int i = 0; i < length_transformation; i++) list.removeChild(start_index);
        for (int i = 0; i < new_instructions.getNumChild();i++) list.insertChild(new_instructions.getChild(i),
                start_index+i);
    }
    private boolean optimizeInstructions(List<Instruction> list) {
        boolean change = true;
        boolean totalChange = true;
        for (int i = 0; i < list.getNumChild(); i++) {

            while (change) {
                change = false;
                for (int j = 0;j< patterns.size();j++) {
                    PeepholeTransformationResult result = patterns.get(j)
                            .transform(list, i);
                    if (result.isTransformed()) {
                        replaceInstructions(i, result.getLength(), result.getInstructions(), list);
                        frequencies[j]++;
                    }
                    change = change || result.isTransformed();
                }
                totalChange = change;
            }
            change = true;
        }
        return totalChange;

    }
    private PeepholePattern buildDependecy(Class<? extends PeepholePattern> pattern){
        try{
            return pattern.getConstructor().newInstance();
        }catch (Exception ex){
            throw new Error("Could not build Peephole dependency for class ["+
                    pattern.getCanonicalName()+"] at:"+ex.getMessage());
        }

    }

    public String getFrequenciesAsCsvString(){
        StringBuilder strBuilder= new StringBuilder();
        strBuilder.append("pattern,frequency\n");
        int i = 0;
        for (PeepholePattern pattern: patterns) {
            strBuilder.append(pattern.getClass().getSimpleName());
            strBuilder.append(",");
            strBuilder.append(frequencies[i]);
            strBuilder.append("\n");
            i++;
        }
        return strBuilder.toString();
    }
}
