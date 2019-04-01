package matwably.code_generation;

import ast.Stmt;
import matwably.ast.Idx;

public class LoopMetaInformation {

    public LoopMetaInformation(Stmt loop, Idx startLoop, Idx endLoop) {
        this.startLoop = startLoop;
        this.endLoop = endLoop;
        this.loop = loop;
    }

    public Idx getStartLoop() {
        return startLoop;
    }

    public Idx getEndLoop() {
        return endLoop;
    }

    public Stmt getLoop() {
        return loop;
    }


    private Idx startLoop;
    private Idx endLoop;
    private Stmt loop;

}
