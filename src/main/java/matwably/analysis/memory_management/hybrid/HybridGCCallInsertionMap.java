package matwably.analysis.memory_management.hybrid;

import ast.ASTNode;
import ast.Stmt;
import matwably.ast.*;
import matwably.code_generation.stmt.StmtHook;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.*;

/**
 * Calls uses the analysis to produce the
 */
public class HybridGCCallInsertionMap {

    public static Map<ASTNode, StmtHook> generateInstructions(TIRFunction function, HybridRCGarbageCollectionAnalysis gcA) {
        return (new HybridGCCallInsertionBuilder(function, gcA)).build();
    }

    private static class HybridGCCallInsertionBuilder extends TIRAbstractNodeCaseHandler {
        private final TIRFunction function;
        private HybridRCGarbageCollectionAnalysis analysis;
        private Map<ASTNode, StmtHook> map_instructions = new HashMap<>();
        private Set<Integer> initial_sites_processed = new HashSet<>();
        private Map<ASTNode<? extends ASTNode>, String> initial_dynamic_sites = new HashMap<>();

        HybridGCCallInsertionBuilder(TIRFunction function, HybridRCGarbageCollectionAnalysis gcA){
            this.analysis = gcA;
            this.function = function;
        }
        Map<ASTNode, StmtHook> build(){
            function.analyze(this);
            buildInitialSites();
            return map_instructions;
        }

        private void buildInitialSites(){
            initial_dynamic_sites.forEach((key, value) -> map_instructions.get(key)
                    .addAfterInstructions(
                            new GetLocal(new Idx(value+"_i32")),
                            new ConstLiteral(new I32(), this.analysis.getOutFlowSets()
                                    .get(key).getStaticMemorySites()
                                    .get(value).getReferenceCount()),
                            new Call(new Idx("gcInitiateRC"))));
        }

        @Override
        public void caseTIRForStmt(TIRForStmt tirForStmt) {
            HybridReferenceCountMap inMap = this.analysis.getInFlowSets().get(tirForStmt);
            StmtHook gcInstructions = new StmtHook();
            String varLoop = tirForStmt.getLoopVarName().getID();
            if(inMap.getStaticMemorySites().containsKey(varLoop)){
                // check ref count if 1, free site.
                MemorySite site = inMap.getStaticMemorySites().get(varLoop);
                if(site.getReferenceCount() == 1){
                    gcInstructions.addInBetweenStmtInstructions(
                            new GetLocal(new Idx(varLoop+"_i32")),
                            new Call(new Idx("free_macharray")));
                }

                // if not one, do nothing.
            }else if(inMap.getDynamicMemorySites().containsKey(varLoop)){
                DynamicSite site = inMap.getDynamicMemorySites().get(varLoop);
                if(site.isInternal()){
                    gcInstructions.addInBetweenStmtInstructions(
                            new GetLocal(new Idx(varLoop+"_i32")),
                            new Call(new Idx("gcDecreaseRCSite")));
                }else if(site.isMaybeExternal()){
                    gcInstructions.addInBetweenStmtInstructions(
                            new GetLocal(new Idx(varLoop+"_i32")),
                            new Call(new Idx("gcCheckExternalToDecreaseRCSite")));
                }
                // decrease dynamically
            }
            // If var an array, do nothing as it is a static definition.
            this.caseASTNode(tirForStmt);
            map_instructions.put(tirForStmt, gcInstructions);
        }

        @Override
        public void caseStmt(Stmt stmt) {
            HybridReferenceCountMap outMap = this.analysis.getOutFlowSets().get(stmt);
            StmtHook gcInstructionsHook = new StmtHook();

            getInitiatedSites(stmt);

            // Increase site
            outMap.getDynamicCheckExternalToDecreaseReferenceSites()
                    .forEach((String name)-> gcInstructionsHook.addInBetweenStmtInstructions(
                    new GetLocal(new Idx(name+"_i32")),
                    new Call(new Idx("gcCheckExternalToDecreaseRCSite"))));
            outMap.getDynamicInternalDecreaseReferenceSites()
                    .forEach((String name)-> gcInstructionsHook.addInBetweenStmtInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcDecreaseRCSite"))));

            outMap.getDynamicInternalSetSiteAsExternal()
                    .forEach((String name)->{
                        gcInstructionsHook.addBeforeInstruction(
                                new ConstLiteral(new I32(), 1),
                                new GetLocal(new Idx(name+"_i32")),
                                new Call(new Idx("gcSetExternalFlag")));
                    });
            outMap.getDynamicInternalSetSiteAsExternal()
                    .forEach((String name)->{
                        gcInstructionsHook.addInBetweenStmtInstructions(
                                new ConstLiteral(new I32(), 0),
                                new GetLocal(new Idx(name+"_i32")),
                                new Call(new Idx("gcSetExternalFlag")));
                    });

            outMap.getDynamicCheckExternalToSetSiteAsExternal()
                    .stream().sorted().forEach((String name)->{
                    gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcGetExternalFlag")));
                    });

            outMap.getDynamicCheckExternalToSetSiteAsExternal()
                    .stream().sorted(Collections.reverseOrder()).forEach((String name)->{
                gcInstructionsHook.addInBetweenStmtInstructions(
                        new GetLocal(new Idx(name+"_i32")),
                        new Call(new Idx("gcSetExternalFlag")));
            });
            // After instructions

            // Dynamic internal increase site, increase reference count only if not external
            outMap.getDynamicCheckExternalToIncreaseReferenceSites()
                    .forEach((String name)-> gcInstructionsHook.addAfterInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalToIncreaseRCSite"))));
            // Dynamic internal increase site, increase reference count.
            outMap.getDynamicInternalIncreaseReferenceSites()
                    .forEach((String name)-> gcInstructionsHook.addAfterInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcIncreaseRCSite"))));
            // Internal dynamic site, set return flag to prevent accidental freeing
            outMap.getDynamicInternalSetRCToZero()
                    .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcSetRCToZero"))));

            outMap.getDynamicInternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcSetReturnFlagAndSetRCToZero"))));
            // Dynamic Return value, ambiguous external flag, make sure that is external and set return flag to 1
            // along with RC
            outMap.getDynamicCheckExternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalToSetReturnFlagAndSetRCZero"))));

            // Freeing results, only contained within mandatory return statements.
            // Static site freeing
            outMap.getDynamicInternalFreeMemorySite()
                    .forEach((String name)-> gcInstructionsHook.addInBetweenStmtInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("free_macharray"))));
            // Dynamic but internal site freeing. Since is dynamic, have to check return flag
            outMap.getDynamicInternalCheckReturnFlagToFreeSites()
                    .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckReturnFlagToFreeSite"))));
            // Dynamic but ambiguous external state site. Since is dynamic, have to check return flag
            outMap.getDynamicCheckExternalAndReturnFlagToFreeSites()
                    .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalAndReturnFlagToFreeSite"))));

            // Restore return flags results
            outMap.getDynamicInternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcResetReturnFlag"))));

            outMap.getDynamicCheckExternalSetReturnFlagAndRCToZero()
                    .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("gcCheckExternalToResetReturnFlag"))));
            map_instructions.put(stmt, gcInstructionsHook);
            this.caseASTNode(stmt);
        }

        /**
         * Traverses all the dynamic sites, looks for newly initiated dynamic
         * sites and resolves the initialization count for the site.
         * Strategy is as follows:
         * If two site point to the same memory, check the latest aliased node.
         * If its the same:
         *  - Go to the latest node map, and get the static count from there.
         *  - If it is different, no conflict, and then add an initiating count
         *  to every path.
         */
        private void getInitiatedSites(Stmt stmt){
            this.analysis.getInFlowSets()
                    .get(stmt).getDynamicMemorySites()
                    .values().stream()
                    .map(DynamicSite::getStaticDefinitions)
                    .flatMap(Collection::stream)
                    .forEach((MemorySite site)->{
                        int processed =
                                site.getDefinition().hashCode()+
                                        site.getInitialVariableName().hashCode()+site.getLatestAliasAdded().hashCode();
                        if(!initial_sites_processed.contains(processed)){
                            initial_dynamic_sites.put(site.getLatestAliasAdded(), (new ArrayList<>(site.getAliasingNames()).get(0)));
                            initial_sites_processed.add(processed);
                        }

                    });
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
