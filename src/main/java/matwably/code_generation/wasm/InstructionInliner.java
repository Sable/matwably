package matwably.code_generation.wasm;

import matwably.ast.*;
import matwably.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Naive 'wasm' call in-liner, this class provides methods to produce a MatWablyBuiltinGeneratorResult,
 * Basic idea is to map all the locals to freshly allocated names. Set them, and then in-line all the body
 * of the function by using these local map.
 *
 */
public class InstructionInliner{
    private ModuleBuilder moduleBuilder;

    public GenerationResult inlineCallWithProvidedArguments(String name, List<Instruction> parameters){
        Function func = this.moduleBuilder.findFunction(name);
        if(func == null) throw new Error("Attempting to in-line, non-existent 'wasm' function: "+name);
        return inlineCallWithProvidedArguments(func, parameters);
    }
    public GenerationResult inlineCallWithProvidedArguments(Function func, List<Instruction> parameters){
        if(parameters.getNumChild() != func.getType().getParameters().getNumChild())
            throw new Error("Attempting to in-line, parameters do not match with size of function parameters: ");
        HashMap<String, Instruction>  localMap = new HashMap<>();
        GenerationResult result = new GenerationResult();

        func.getLocalsList()
                .forEach((typeUse -> {
                    String newId = Util.genID();
                    String idParam = typeUse.getIdentifier().getName();
                    result.addLocals(new TypeUse(newId, typeUse.getType()));
                    localMap.put(idParam, new GetLocal(new Idx(newId)));
                }));
        List<TypeUse> params = func.getType().getParameters();
        for (int i = 0; i < params.getNumChild(); i++) {
            localMap.put(params.getChild(i).getIdentifier().getName(),
                    parameters.getChild(i));
        }
        result.addInstructionList(aliasInstructionList(func.getBody().getInstructionsList(), localMap));
        return result;
    }
    public GenerationResult inlineCallInModule(String name){
        Function func = this.moduleBuilder.findFunction(name);
        if(func == null) throw new Error("Attempting to in-line, non-existent 'wasm' function: "+name);
        return inlineCallInModule(func);
    }

        // Assumes the arguments are already loaded on the stack
    public GenerationResult inlineCallInModule(Function func){
        HashMap<String, Instruction>  localMap = new HashMap<>();
        GenerationResult result = new GenerationResult();

//        if(func.hasLocals())
//            result.addLocalList(func.getLocalsList().treeCopyNoTransform());
        func.getType()
                .getParameters()
                .forEach((typeUse -> {
                    String newId = Util.genID();
                    String idParam = typeUse.getIdentifier().getName();
                    result.addLocals(new TypeUse(newId, typeUse.getType()));
                    localMap.put(newId, new GetLocal(new Idx(idParam)));


                }));
        func.getLocalsList()
                .forEach((typeUse -> {
                    String newId = Util.genID();
                    String idParam = typeUse.getIdentifier().getName();
                    result.addLocals(new TypeUse(newId, typeUse.getType()));
                    localMap.put(newId, new GetLocal(new Idx(idParam)));
                }));

        func.getType().getParameters()
                .forEach((typeUse -> {
                    GetLocal local = (GetLocal)localMap.get(typeUse.getIdentifier().getName());
                    result.addInstruction(
                            new SetLocal(new Idx(local.getIdx().getIdentifier().getName())));
                }));

        result.addInstructionList(aliasInstructionList(func.getBody().getInstructionsList(), localMap));
        return result;
    }

    private List<Instruction> aliasInstructionList(List<Instruction> instr, Map<String, Instruction> map_aliases){
         java.util.List<Instruction> list = StreamSupport.stream(instr.spliterator(), true).map((Instruction inst)->{
            if(inst instanceof If){
                If currIf = (If) inst;
                If newIf = new If();
                if(currIf.hasLabel()) newIf.setLabel(currIf.getLabel().treeCopy());
                if(currIf.hasReturnType()) newIf.setReturnType(currIf.getReturnType().treeCopy());
                newIf.setInstructionsIfList(aliasInstructionList(currIf.getInstructionsIfList(), map_aliases));
                newIf.setInstructionsElseList(aliasInstructionList(currIf.getInstructionsElseList(), map_aliases));
                return newIf;
            }else if(inst instanceof Loop){
                Loop curr_node = (Loop) inst;
                Loop new_node = new Loop();
                if(curr_node.hasLabel()) new_node.setLabel(curr_node.getLabel().treeCopy());
                if(curr_node.hasReturnType()) new_node.setReturnType(curr_node.getReturnType().treeCopy());
                new_node.setInstructionList(aliasInstructionList(curr_node.getInstructionList(), map_aliases));
                return new_node;

            }else if(inst instanceof Block){
                Block curr_node = (Block) inst;
                Block new_node = new Block();
                if(curr_node.hasLabel()) new_node.setLabel(curr_node.getLabel().treeCopy());
                if(curr_node.hasReturnType()) new_node.setReturnType(curr_node.getReturnType().treeCopy());
                new_node.setInstructionList(aliasInstructionList(curr_node.getInstructionList(), map_aliases));
                return new_node;
            }else{
                return inst.treeCopy();
            }
        }).collect(Collectors.toList());
         return new List<Instruction>().addAll(list);
    }






    public InstructionInliner(ModuleBuilder wasmModule){
        this.moduleBuilder = wasmModule;
    }
    
}
