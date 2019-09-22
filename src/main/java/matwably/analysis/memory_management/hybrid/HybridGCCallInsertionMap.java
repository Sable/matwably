package matwably.analysis.memory_management.hybrid;

import ast.ASTNode;
import ast.Name;
import ast.Stmt;
import matwably.analysis.intermediate_variable.UseDefDefUseChain;
import matwably.ast.List;
import matwably.ast.*;
import matwably.code_generation.stmt.StmtHook;
import matwably.util.Util;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRReturnStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.*;

/**
 * Calls uses the analysis to produce the
 */
public class HybridGCCallInsertionMap {

    public static Map<ASTNode, StmtHook> generateInstructions(TIRFunction function,
                                                              HybridRCGarbageCollectionAnalysis gcA,
                                                              UseDefDefUseChain udChain ) {
        return (new HybridGCCallInsertionBuilder(function, udChain, gcA))
                .build();
    }

    private static class HybridGCCallInsertionBuilder extends TIRAbstractNodeCaseHandler {
        private final TIRFunction function;
        private final UseDefDefUseChain uddu;
        private HybridRCGarbageCollectionAnalysis analysis;
        private Map<ASTNode, StmtHook> map_instructions = new HashMap<>();
        private Set<Integer> initial_sites_processed = new HashSet<>();
        private Map<ASTNode<? extends ASTNode>, String> initial_dynamic_sites = new HashMap<>();
        private List<Instruction> input_set_external = new List<>();
        private List<Instruction> input_reset_external = new List<>();
        private List<TypeUse> locals_input_parameters = new List<>();
        HybridGCCallInsertionBuilder(TIRFunction function,
                                     UseDefDefUseChain udChain,
                                     HybridRCGarbageCollectionAnalysis gcA){
            this.analysis = gcA;
            this.uddu = udChain;
            this.function = function;
        }
        private void analyze(){
            analyzeInputParameters();
            function.analyze(this);

        }
        Map<ASTNode, StmtHook> build(){
            analyze();
            insertInputParameterExternalCalls();
            insertDynamicSiteInitialization();
            return map_instructions;
        }

        /**
         * Inserts the input parameters calls to set them as external
         */
        private void insertInputParameterExternalCalls() {
            if(this.function.hasStmt()){
                StmtHook stmtHookFunctionTop =
                        map_instructions.get(this.function.getStmt(0));
                stmtHookFunctionTop.addLocals(this.locals_input_parameters);
                stmtHookFunctionTop.insertBeforeInstructions(input_set_external);
            }
        }
        private List<Instruction> setExternalFlag( String nameArgument, String nameLocal ) {
            List<Instruction> res = new List<>();
            res.addAll( new GetLocal(new Idx(nameArgument+"_i32")),
                    new SetLocal(new Idx(nameLocal)),
                    new GetLocal(new Idx(nameLocal)),
                    new Call(new Idx("gcGetExternalFlag")),
                    new ConstLiteral(new I32(), 1),
                    new GetLocal(new Idx(nameLocal)),
                    new Call(new Idx("gcSetExternalFlag")));

            return res;
        }
        // Analyze input parameters and use locals
        private void analyzeInputParameters() {
            this.function.getInputParamList().
                    forEach((Name name)->{
                        if(
                        this.isArgumentSiteDynamic(name)){
                            TypeUse typeUse = Util.genI32TypeUse();
                            input_set_external.addAll(setExternalFlag(name.getID(), typeUse.
                                    getIdentifier().getName()));
                            input_reset_external.addAll(resetExternalFlat(typeUse.
                                    getIdentifier().getName()));
                            locals_input_parameters.add(typeUse);
                        }
                    });


        }

        private List<Instruction> resetExternalFlat(String name) {
            List<Instruction> res = new List<>();
            res.addAll( new GetLocal(new Idx(name)),
                        new Call(new Idx("gcSetExternalFlag")));
            return res;
        }

        /**
         * This site
         * @param name parameter name to check
         * @return
         */
        private boolean isArgumentSiteDynamic(Name name){
            // If any of the uses have two definitions, and the definitions are
            // not external.
            return this.uddu.getUses(name).stream().
                    anyMatch(( use )-> this.uddu.getDefs(use).size() > 1);
        }



        private void insertDynamicSiteInitialization(){
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
            HybridReferenceCountMap outMap = this.analysis.
                    getOutFlowSets().get(stmt);
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
            Set<String> dynamicInternalFreeMemorySite = outMap.getDynamicInternalFreeMemorySite();
            dynamicInternalFreeMemorySite
                    .forEach((String name)->
                            gcInstructionsHook.addInBetweenStmtInstructions(
                            new GetLocal(new Idx(name+"_i32")),
                            new Call(new Idx("free_macharray"))));
            boolean produceLocalStackForReturn =
                    outMap.getDynamicInternalCheckReturnFlagToFreeSites()
                            .size()>0
                    || outMap.getDynamicCheckExternalAndReturnFlagToFreeSites()
                            .size()>0;
            if( produceLocalStackForReturn ){
                String local_stack_top = gcInstructionsHook.addI32Local();
                gcInstructionsHook.addBeforeInstruction(
                        new GetGlobal(new Idx("STACKTOP")),
                        new TeeLocal(new Idx(local_stack_top)),
                        new ConstLiteral(new I32(), 0),
                        new Store(new I32(),new Opt<>(),new Opt<>()));
                // Dynamic but internal site freeing. Since is dynamic, have to check return flag
                outMap.getDynamicInternalCheckReturnFlagToFreeSites()
                        .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                                new GetLocal(new Idx(local_stack_top)),
                                new GetLocal(new Idx(name+"_i32")),
                                new Call(new Idx("gcCheckReturnFlagToFreeSite"))));
                // Dynamic but ambiguous external state site. Since is dynamic, have to check return flag
                outMap.getDynamicCheckExternalAndReturnFlagToFreeSites()
                        .forEach((String name)-> gcInstructionsHook.addBeforeInstruction(
                                new GetLocal(new Idx(local_stack_top)),
                                new GetLocal(new Idx(name+"_i32")),
                                new Call(new Idx("gcCheckExternalAndReturnFlagToFreeSite"))));

            }
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
                                        site.getInitialVariableName().hashCode()
                                        +site.getLatestAliasAdded().hashCode();
                        if(!initial_sites_processed.contains(processed)){
                            initial_dynamic_sites.put(site.getLatestAliasAdded(),
                                    (new ArrayList<>(site.getAliasingNames()).get(0)));
                            initial_sites_processed.add(processed);
                        }

                    });
        }

        @Override
        public void caseTIRReturnStmt(TIRReturnStmt tirReturnStmt) {
            // Process all sites
            super.caseTIRReturnStmt(tirReturnStmt);
            // Process all the set external flags
           this.map_instructions.get(tirReturnStmt).
                   addBeforeInstruction(this.input_reset_external);
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
