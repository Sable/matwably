package matwably.analysis.memory_management.dynamic;

import ast.ASTNode;
import matwably.ast.*;
import matwably.code_generation.stmt.StmtHook;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DynamicGCCallInsertion {

    private final DynamicRCGarbageCollection analysis;

    public static Map<ASTNode, StmtHook> generateInstructions(DynamicRCGarbageCollection analysis){
        return (new DynamicGCCallInsertion(analysis)).collectInstruction();
    }

    private Map<ASTNode, StmtHook> collectInstruction() {
        Map<ASTNode, StmtHook> res = new HashMap<>();
        for(Map.Entry<ASTNode, DynamicRCSet> entry: analysis.getGcCallsMapping().entrySet()){
            res.put(entry.getKey(), generateGCInstructions(entry.getValue()));
        }
        return res;
    }
    // TODO: Inline these calls
    private StmtHook generateGCInstructions(DynamicRCSet gcSet) {
        StmtHook gcInstructions = new StmtHook();
        // Add decrease instructions
        gcSet.getCheckExternalDecreaseSite()
                .forEach((String name)-> gcInstructions.addInBetweenStmtInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalToDecreaseRCSite"))));

        gcSet.getCheckExternalIncreaseSite()
                .forEach((String name)-> gcInstructions.addAfterInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalToIncreaseRCSite"))));

        gcSet.getCheckAndAddExternalFlagSet().stream().sorted()
                .forEach((String name)->{
                    gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcGetExternalFlag")));

                });
        gcSet.getCheckAndAddExternalFlagSet().stream().sorted()
                .forEach((String name)-> gcInstructions.addBeforeInstruction(
                        new ConstLiteral(new I32(), 1),
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcSetExternalFlag"))));

        gcSet.getCheckAndAddExternalFlagSet().stream().
                sorted(Collections.reverseOrder()).forEach((String name)->
                gcInstructions.addInBetweenStmtInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcSetExternalFlag"))));


        gcSet.getCheckExternalToSetReturnFlagAndSetRCToZero()
                .forEach((String name)-> gcInstructions.addBeforeInstruction(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx(
                                "gcCheckExternalToSetReturnFlagAndSetRCZero"))));


        boolean produceLocalStackForReturn =
                gcSet.getCheckExternalAndCheckReturnFlagToFree()
                        .size()>0;
        if(produceLocalStackForReturn) {
            String local_stack_top = gcInstructions.addI32Local();
            gcInstructions.addBeforeInstruction(
                    new GetGlobal(new Idx("STACKTOP")),
                    new TeeLocal(new Idx(local_stack_top)),
                    new ConstLiteral(new I32(), 0),
                    new Store(new I32(), new Opt<>(), new Opt<>()));


            gcSet.getCheckExternalAndCheckReturnFlagToFree()
                    .forEach((String name) -> gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(local_stack_top)),
                            new GetLocal(new Idx(name + "_i32")),
                            new Call(new Idx(
                                    "gcCheckExternalAndReturnFlagToFreeSite"))));
        }
        gcSet.getCheckExternalToSetReturnFlagAndSetRCToZero()
                .forEach((String name)-> gcInstructions.addBeforeInstruction(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx(
                                "gcResetReturnFlag"))));
        return gcInstructions;
    }

    private DynamicGCCallInsertion(DynamicRCGarbageCollection analysis){
        this.analysis = analysis;
    }


}
