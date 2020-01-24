package matwably.analysis.memory_management.dynamic;

import ast.ASTNode;
import ast.Name;
import ast.Stmt;
import matwably.ast.*;
import matwably.code_generation.stmt.StmtHook;
import matwably.util.Util;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRReturnStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DynamicGCCallInsertion {

    /**
     * Method used by client to generate GC instructions
     * @param function TameIR function
     * @param analysis Analysis to construct the actual GC calls
     * @param valueUtil
     * @return returns map of stmts to StmtHooks
     */
    public static Map<ASTNode, StmtHook>
        generateInstructions(TIRFunction function,
                             DynamicRCGarbageCollection analysis,
                             ValueAnalysisUtil valueUtil){
        return (new DynamicGCCallInsertionBuilder(analysis, function,
                valueUtil)).build();
    }
    // Builder class for insertion nodes
    private static class DynamicGCCallInsertionBuilder extends TIRAbstractNodeCaseHandler {
        private final TIRFunction function;
        private final DynamicRCGarbageCollection analysis;
        private final ValueAnalysisUtil valueAnalysis;
        private Map<ASTNode, StmtHook> map_instructions = new HashMap<>();
        private List<TypeUse> locals_input_parameters = new List<>();
        private List<Instruction> input_set_external = new List<>();
        private List<Instruction> input_reset_external = new List<>();

        /**
         * Constructor
         * @param analysis DynamicRCGarbageCollection analysis
         * @param function TamerIR function
         * @param valueUtil ValueAnalysisUtil used to find out whether inputs are arrays
         */
        DynamicGCCallInsertionBuilder(DynamicRCGarbageCollection analysis,
                                      TIRFunction function,
                                      ValueAnalysisUtil valueUtil) {
            this.analysis = analysis;
            this.function = function;
            this.valueAnalysis = valueUtil;
        }

        /**
         * Method runs the analysis to produce the instructions
         * @return map of the stmt mapped to stmt hooks containing gc instructions
         */
        Map<ASTNode, StmtHook> build(){
            analyzeInputParameters();

            this.function.analyze(this);
            // Check if there is a stmt, if there is adds the instructions
            // for inputs
            if( this.function.hasStmt() ){
                StmtHook stmtHookFunctionTop =
                        map_instructions.get(this.function.getStmt(0));
                stmtHookFunctionTop.addLocals(this.locals_input_parameters);
                stmtHookFunctionTop.insertBeforeInstructions(input_set_external);
                map_instructions.put( this.function.getStmt(0),stmtHookFunctionTop );
            }
            return map_instructions;
        }

        /**
         * Analyzes inputs parameters and generates instructions to keep track of them inside
         * as external at runtime
         */
        private void analyzeInputParameters() {
            this.function.getInputParamList().
                    forEach( (Name name) -> {
                        if(!this.valueAnalysis.isArgumentScalar(name)) {
                            TypeUse typeUse = Util.genI32TypeUse();
                            input_set_external.addAll(setExternalFlag(name.getID(), typeUse.
                                    getIdentifier().getName()));
                            input_reset_external.addAll(resetExternalFlat(typeUse.
                                    getIdentifier().getName()));
                            locals_input_parameters.add(typeUse);
                        }
                    });
        }

        /**
         * General stmt case traversal
         * @param stmt TameIR stmt to generate instructions for based on the
         *             analysis
         */
        @Override
        public void caseStmt(Stmt stmt) {
            StmtHook gcInstructions = new StmtHook();
            DynamicRCSet gcSet = this.analysis.getGcCallsMapping()
                    .get(stmt);
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

            this.map_instructions.put(stmt, gcInstructions);
            this.caseASTNode(stmt);
        }

        /**
         * At return points in the function, we add instructions to set the flag
         * for input parameters again
         * @param tirReturnStmt Return stmt for TameIR
         */
        @Override
        public void caseTIRReturnStmt(TIRReturnStmt tirReturnStmt) {
            super.caseTIRReturnStmt(tirReturnStmt);
            // Process all the set external flags
            this.map_instructions.get(tirReturnStmt).
                    addBeforeInstruction(this.input_reset_external);
        }

        /**
         * Function generates the external flag instruction for input parameters
         * It assumes the value to be store as return flag is already on the stack.
         * @param name Name of parameter
         * @return Set of instructions to reset external flag
         */
        private List<Instruction> resetExternalFlat(String name) {
            List<Instruction> res = new List<>();
            res.addAll( new GetLocal(new Idx(name)),
                    new Call(new Idx("gcSetExternalFlag")));
            return res;
        }
        /**
         * Function generates the external flag instruction for input parameters
         * It assumes the value to be store as return flag is already on the stack.
         * @param nameArgument Name of parameter
         * @param nameLocal Name of tmp local to contain parameter
         * @return Set of instructions to get external flag.
         */
        private List<Instruction> setExternalFlag( String nameArgument, String nameLocal ) {
            List<Instruction> res = new List<>();
            res.addAll( new GetLocal(new Idx(nameArgument+"_i32")),
                    new SetLocal(new Idx(nameLocal)),
                    new GetLocal(new Idx(nameArgument+"_i32")),
                    new Call(new Idx("gcGetExternalFlag")),
                    new ConstLiteral(new I32(), 1),
                    new GetLocal(new Idx(nameArgument+"_i32")),
                    new Call(new Idx("gcSetExternalFlag"))
                    );
            return res;
        }

        /**
         * General method for traversal of the IR
         * @param astNode ASTNode for TameIR
         */
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
