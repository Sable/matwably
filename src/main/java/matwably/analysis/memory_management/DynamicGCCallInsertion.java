package matwably.analysis.memory_management;

import ast.ASTNode;
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
        for(Map.Entry<ASTNode, GCCallsSet> entry: analysis.getGcCallsMapping().entrySet()){
            res.put(entry.getKey(), generateGCInstructions(entry.getValue()));
        }
        return res;
    }
    // TODO: Inline these calls
    private GCInstructions generateGCInstructions(GCCallsSet gcSet) {
        GCInstructions gcInstructions = new GCInstructions();
        // Add decrease instructions
        gcSet.getCheckExternalDecreaseSite()
                .forEach((String name)-> gcInstructions.addInBetweenStmtInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalDecreaseRCSite"))));

        gcSet.getCheckExternalIncreaseSite()
                .forEach((String name)-> gcInstructions.addAfterInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalIncreaseRCSite"))));

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


        gcSet.getCheckExternalSetRCZero()
                .forEach((String name)-> gcInstructions.addAfterInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalSetRCZero"))));

        gcSet.getCheckExternalAndFree()
                .forEach((String name)-> gcInstructions.addAfterInstructions(new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcCheckExternalFreeSite"))));
        return gcInstructions;
    }

    private DynamicGCCallInsertion(DynamicRCGarbageCollection analysis){
        this.analysis = analysis;
    }


}
