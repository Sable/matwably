package matwably.code_generation.wasm;


import matwably.ast.*;
import matwably.util.Util;


public class LoopFactory {

    public static LoopStructure createLoopWithConstantBounds(String loopVarName,
                                               int low,
                                               int step,
                                               int high,
                                               List<Instruction> body){

        List<Instruction> res = new List<>();

        If ifStmt = new If();
        Idx breakId = new Idx(Util.genID());
        ifStmt.setLabel(breakId);
        res.addAll(new ConstLiteral(new I32(), low),
                new ConstLiteral(new I32(), high),
                new Lt(new I32(),true));

        List<Instruction> init_loop_var = new List<>();
        init_loop_var.addAll(
                new ConstLiteral(new I32(), low),
                new SetLocal(new Idx(loopVarName))
        );


        // Add loop
        Loop loop = new Loop();
        Idx continueId = new Idx(Util.genID());
        loop.setLabel(continueId);
        loop.setInstructionList(body);
        List<Instruction> condition = new List<>();
        condition.addAll(
                new GetLocal(new Idx(loopVarName)),
                new ConstLiteral(new I32(), high),
                new Lt(new I32(),true));

        loop.getInstructionList()
                .addAll(new GetLocal(new Idx(loopVarName)),
                        new ConstLiteral(new I32(), step),
                        new Add(new I32()),
                        new SetLocal(new Idx(loopVarName)))
                .addAll(condition.treeCopy())
                .add(new BrIf(continueId));

        init_loop_var.add(loop);
        ifStmt.setInstructionsIfList(init_loop_var);
        res.add(ifStmt);
        return (new LoopStructure.Builder(loopVarName))
                .high(high).low(low).body(body).build();
    }
}
