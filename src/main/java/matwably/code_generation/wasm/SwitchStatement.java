package matwably.code_generation.wasm;

import matwably.ast.*;

public class SwitchStatement {


    private List<List<Instruction>> clauses= new List<>();
    private java.util.List<Boolean> breakingClauses = new java.util.ArrayList<>();
    private List<Instruction> condition = new List<>();
    public SwitchStatement(List<Instruction> condition, java.util.List<Boolean> br,List<Instruction>... clauses){
        if(br.size() != clauses.length) throw new Error("Breaks and clauses must be have the same length");
        this.condition = condition;
        for (int i = 0; i < clauses.length; i++) {
            this.clauses.add(clauses[i]);
            this.breakingClauses.add(br.get(i));
        }
    }

    /**
     * Assumes clauses break
     * @param clauses
     */
    public SwitchStatement(List<Instruction> condition, List<Instruction>... clauses) {
        this.condition = condition;
        for (List<Instruction> inst: clauses) {
            this.clauses.add(inst);
            this.breakingClauses.add(true);
        }
    }
    public List<List<Instruction>> getClauses() {
        return clauses;
    }
    public void addClause(List<Instruction> inst, boolean clauseBreaks){
        clauses.add(inst);
        breakingClauses.add(clauseBreaks);
    }
    public void addClause(List<Instruction> inst){
        clauses.add(inst);
        breakingClauses.add(true);
    }
    public List<Instruction> generate(){
        List<Instruction> res = new List<>();
//        res.addAll();
        Block prev = new Block();
        prev.setInstructionList(condition);
        BrTable br_table = new BrTable();
        prev.addInstruction(br_table);
        List<Idx> indeces = new List<>();
        for (int i = 0; i < clauses.getNumChild(); i++) {
            indeces.add(new Idx(new Opt<>(), i));
            Block next = new Block();
            next.addInstruction(prev);
            next.getInstructionList().addAll(clauses.getChild(i));
            if(breakingClauses.get(i))
                next.addInstruction(
                    new Br(new Idx( new Opt<>(),
                    clauses.getNumChild() - i-1))
                );
            prev = next;
        }
        br_table.setLabelsList(indeces);
        res.add(prev);
        return res;
    }

}
