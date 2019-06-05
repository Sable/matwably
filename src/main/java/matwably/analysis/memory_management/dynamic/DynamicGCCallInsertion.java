package matwably.analysis.memory_management.dynamic;

import ast.ASTNode;
import matwably.analysis.memory_management.GCInstructions;
import matwably.ast.Call;
import matwably.ast.GetLocal;
import matwably.ast.Idx;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DynamicGCCallInsertion {

    private final DynamicRCGarbageCollection analysis;
    public static Map<ASTNode, GCInstructions> generate(DynamicRCGarbageCollection analysis){
        return (new DynamicGCCallInsertion(analysis)).collectInstruction();
    }

    private Map<ASTNode,GCInstructions> collectInstruction() {
        Map<ASTNode, GCInstructions> res = new HashMap<>();
        for(Map.Entry<ASTNode, DynamicRCSet> entry: analysis.getGcCallsMapping().entrySet()){
            res.put(entry.getKey(), generateGCInstructions(entry.getValue()));
        }
        return res;
    }
    // TODO: Inline these calls
    private GCInstructions generateGCInstructions(DynamicRCSet gcSet) {
        GCInstructions gcInstructions = new GCInstructions();
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
        gcSet.getCheckAndAddExternalFlagSet().stream().
                sorted(Collections.reverseOrder()).forEach((String name)->
                gcInstructions.addInBetweenStmtInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcSetExternalFlag"))));


        gcSet.getCheckExternalToSetReturnFlagAndSetRCToZero()
                .forEach((String name)-> gcInstructions.addBeforeInstruction(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalToSetReturnFlagAndSetRCZero"))));

        gcSet.getCheckExternalAndCheckReturnFlagToFree()
                .forEach((String name)-> gcInstructions.addBeforeInstruction(new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalAndReturnFlagToFreeSite"))));
        return gcInstructions;
    }

    private DynamicGCCallInsertion(DynamicRCGarbageCollection analysis){
        this.analysis = analysis;
    }


}
