package matwably.analysis.memory_management.hybrid;

import ast.ASTNode;
import ast.Stmt;
import matwably.analysis.memory_management.GCInstructions;
import matwably.ast.*;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Calls uses the analysis to produce the
 */
public class HybridGCCallInsertionMap {

    public static Map<ASTNode,GCInstructions> generateInstructions(TIRFunction function, HybridRCGarbageCollectionAnalysis gcA) {
        return (new HybridGCCallInsertionBuilder(function, gcA)).build();
    }

    private static class HybridGCCallInsertionBuilder extends TIRAbstractNodeCaseHandler {
        private final TIRFunction function;
        private HybridRCGarbageCollectionAnalysis analysis;
        private Map<ASTNode, GCInstructions> map_instructions = new HashMap<>();

        HybridGCCallInsertionBuilder(TIRFunction function, HybridRCGarbageCollectionAnalysis gcA){
            this.analysis = gcA;
            this.function = function;
        }
        Map<ASTNode,GCInstructions> build(){
            function.analyze(this);
            return map_instructions;
        }

        @Override
        public void caseStmt(Stmt stmt) {
            HybridReferenceCountMap outMap = this.analysis.getOutFlowSets().get(stmt);
            GCInstructions gcInstructions = new GCInstructions();


            // Check for initiating dynamic sites
            this.analysis.getInFlowSets().get(stmt)
                    .getDynamicSitesToInialize()
                    .forEach((MemorySite site)->
                            map_instructions.get(site.getDefinition())
                            .addBeforeInstruction(
                                new GetLocal(new Idx(site.getInitialVariableName())),
                                new ConstLiteral(new I32(), site.getReferenceCount()),
                                new Call(new Idx("gcInitiateRC"))));


            // Increase site
            outMap.getDynamicCheckExternalToDecreaseReferenceSites()
                    .forEach((String name)-> gcInstructions.addInBetweenStmtInstructions(
                    new GetLocal(new Idx(name+"_i32")),
                    new Call(new Idx("gcCheckExternalToDecreaseRCSite"))));
            outMap.getDynamicInternalDecreaseReferenceSites()
                    .forEach((String name)-> gcInstructions.addInBetweenStmtInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcDecreaseRCSite"))));

            outMap.getDynamicInternalSetSiteAsExternal()
                    .forEach((String name)->{
                        gcInstructions.addBeforeInstruction(
                                new ConstLiteral(new I32(), 1),
                                new GetLocal(new Idx(name+"_i32")),
                                new Call(new Idx("gcSetExternalFlag")));
                    });
            outMap.getDynamicInternalSetSiteAsExternal()
                    .forEach((String name)->{
                        gcInstructions.addInBetweenStmtInstructions(
                                new ConstLiteral(new I32(), 0),
                                new GetLocal(new Idx(name+"_i32")),
                                new Call(new Idx("gcSetExternalFlag")));
                    });

            outMap.getDynamicCheckExternalToSetSiteAsExternal()
                    .stream().sorted().forEach((String name)->{
                    gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcGetExternalFlag")));
                    });

            outMap.getDynamicCheckExternalToSetSiteAsExternal()
                    .stream().sorted(Collections.reverseOrder()).forEach((String name)->{
                gcInstructions.addInBetweenStmtInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcSetExternalFlag")));
            });
            // After instructions

            // Dynamic internal increase site, increase reference count only if not external
            outMap.getDynamicCheckExternalToIncreaseReferenceSites()
                    .forEach((String name)-> gcInstructions.addAfterInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalToIncreaseRCSite"))));
            // Dynamic internal increase site, increase reference count.
            outMap.getDynamicInternalIncreaseReferenceSites()
                    .forEach((String name)-> gcInstructions.addAfterInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcIncreaseRCSite"))));
            // Internal dynamic site, set return flag to prevent accidental freeing
            outMap.getDynamicInternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructions.addAfterInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcSetReturnFlagAndSetRCToZero"))));
            // Dynamic Return value, ambiguous external flag, make sure that is external and set return flag to 1
            // along with RC
            outMap.getDynamicCheckExternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructions.addAfterInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalToSetReturnFlagAndSetRCZero"))));

            // Freeing results, only contained within mandatory return statements.
            // Static site freeing
            outMap.getDynamicInternalFreeMemorySite()
                    .forEach((String name)-> gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcFreeSite"))));
            // Dynamic but internal site freeing. Since is dynamic, have to check return flag
            outMap.getDynamicInternalCheckReturnFlagToFreeSites()
                    .forEach((String name)-> gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckReturnFlagToFreeSite"))));
            // Dynamic but ambiguous external state site. Since is dynamic, have to check return flag
            outMap.getDynamicCheckExternalAndReturnFlagToFreeSites()
                    .forEach((String name)-> gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalAndReturnFlagToFreeSite"))));

            // Restore return flags results
            outMap.getDynamicInternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcResetReturnFlag"))));

            outMap.getDynamicCheckExternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructions.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalToResetReturnFlag"))));
            map_instructions.put(stmt, gcInstructions);
            this.caseASTNode(stmt);
        }

        private void getInitiatedSites(){

        }

        @Override
        public void caseASTNode(ASTNode astNode) {
            for (int i = 0; i < astNode.getNumChild(); i++) {
                ASTNode child = astNode.getChild(i);
                if (child instanceof TIRNode) {
                    ((TIRNode) child).tirAnalyze(this);
                }
                else {
                    child.analyze(this);
                }
            }
        }
    }
}
