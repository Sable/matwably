package matwably.code_generation.stmt;

import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.ast.TypeUse;
import matwably.util.Util;

/**
 * Places Stmt hooks together
 */
public class StmtHook implements IStmtHook<List<Instruction>> {

    /**
     * Locals for the stmt hook
     */
    private List<TypeUse> localList = new List<>();
    private List<Instruction> beforeStmtInstructions = new List<>();
    private List<Instruction> afterStmtInstructions = new List<>();


    // Special case with user defined functions, we must set external flag.
    private List<Instruction> inBetweenCallStmtInstructions = new List<>();

    public StmtHook(StmtHook hook) {
        beforeStmtInstructions.addAll(hook.beforeStmtInstructions);
        afterStmtInstructions.addAll(hook.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(hook.inBetweenCallStmtInstructions);
    }

    public static StmtHook merge(StmtHook other, StmtHook stmtHook){
        List<Instruction> beforeStmtInstructions = new List<>();
        List<Instruction> afterStmtInstructions = new List<>();
        List<TypeUse> locals = new List<>();
        // Special case with user defined functions, we must set external flag.
        List<Instruction> inBetweenCallStmtInstructions = new List<>();
        beforeStmtInstructions.addAll(other.beforeStmtInstructions);
        afterStmtInstructions.addAll(other.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(other.inBetweenCallStmtInstructions);
        beforeStmtInstructions.addAll(stmtHook.beforeStmtInstructions);
        afterStmtInstructions.addAll(stmtHook.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(stmtHook.inBetweenCallStmtInstructions);
        locals.addAll(stmtHook.localList);
        locals.addAll(other.localList);
        return new StmtHook(beforeStmtInstructions,
                afterStmtInstructions,
                inBetweenCallStmtInstructions,
                locals);
    }

    public StmtHook(){ }
    public StmtHook(List<Instruction> bef, List<Instruction> aft,
                    List<Instruction> inBet, List<TypeUse> locals){
        this.beforeStmtInstructions = new List<Instruction>().addAll(bef);
        this.afterStmtInstructions = new List<Instruction>().addAll(aft);
        this.inBetweenCallStmtInstructions = new List<Instruction>().addAll(inBet);
        this.localList = new List<TypeUse>().addAll(locals);

    }



    public List<Instruction> getInBetweenCallStmtInstructions() {
        return inBetweenCallStmtInstructions;
    }

    public boolean hasBeforeStmtInstructions(){
        return beforeStmtInstructions.getNumChild() > 0;
    }
    public boolean hasAfterStmtInstructions(){
        return afterStmtInstructions.getNumChild() > 0;
    }
    public boolean hasInBetweenStmtInstructions(){
        return inBetweenCallStmtInstructions.getNumChild() > 0;
    }



    public StmtHook add(StmtHook other) {
        beforeStmtInstructions.addAll(other.beforeStmtInstructions);
        afterStmtInstructions.addAll(other.afterStmtInstructions);
        inBetweenCallStmtInstructions.addAll(other.inBetweenCallStmtInstructions);
        localList.addAll(other.localList);
        return this;
    }
    public void insertBeforeInstructions(List<Instruction> instructions) {
        List<Instruction> res = new List<>();
        res.addAll(instructions);
        res.addAll(this.beforeStmtInstructions);
        this.beforeStmtInstructions = res;
    }

    public void addBeforeInstruction(List<Instruction> instructions){
        beforeStmtInstructions.addAll(instructions);
    }

    public void addAfterInstructions(List<Instruction> instructions){
        afterStmtInstructions.addAll(instructions);
    }


    public void addInBetweenStmtInstructions(List<Instruction> instructions){
        inBetweenCallStmtInstructions.addAll(instructions);
    }
    public void addLocals(List<TypeUse> typeUse){
        this.localList.addAll(typeUse);
    }

    @Override
    public List<Instruction> getBeforeStmtInstructions() {
        return beforeStmtInstructions;
    }

    @Override
    public List<Instruction> getAfterStmtInstructions() {
        return afterStmtInstructions;
    }

    @Override
    public List<Instruction> getInBetweenStmtInstructions() {
        return inBetweenCallStmtInstructions;
    }

    public void addInBetweenStmtInstructions(Instruction... instructions) {
        inBetweenCallStmtInstructions.addAll(instructions);

    }

    public void addBeforeInstruction(Instruction... instructions){
        beforeStmtInstructions.addAll(instructions);
    }

    public void addAfterInstructions(Instruction... instructions){
        afterStmtInstructions.addAll(instructions);
    }

    public String addI32Local(){
        TypeUse local = Util.genI32TypeUse();
        this.localList.add(local);
        return local.getIdentifier().getName();
    }

    public String addF64Local(){
        TypeUse local = Util.genF64TypeUse();
        this.localList.add(local);
        return local.getIdentifier().getName();
    }

    public List<TypeUse> getLocalList() {
        return localList;
    }
}
