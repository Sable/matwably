package matwably.util;

import ast.Name;
import ast.Stmt;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCallStmt;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.ReachingDefs;

import java.util.Set;

public class LogicalVariableUtil  {
    private final ReachingDefs rd;
    private final InterproceduralFunctionQuery functionQuery;

    public LogicalVariableUtil(InterproceduralFunctionQuery functionQuery, ReachingDefs defs){
        this.functionQuery = functionQuery;
        this.rd = defs;
    }

    public boolean isUseLogical(Name name) {
        Set<Def> defs = rd.getUseDefDefUseChain().getDefs(name);
        return defs.size() > 0 && defs
                .stream().allMatch((Def def)->
                        def instanceof Stmt &&
                                def instanceof TIRCallStmt
                                && isCallLogical((TIRCallStmt) def));
    }
    public boolean isDefinitionLogical(String name, Def def){
        Set<Name> uses = rd.getUseDefDefUseChain()
                .getUsesOf(name, def);
        return uses.size() > 0 && uses
                .stream().allMatch(this::isUseLogical);
    }

    private boolean isCallLogical(TIRCallStmt callStmt){
        String call = callStmt.getFunctionName().getID();
        if(functionQuery.isUserDefinedFunction(call)) return false;
        Class<?> classObj = Builtin.getInstance(call).getClass();
        while(classObj!= null){
            if(classObj.getName().contains("Logical")) {
                return true;
            }
            classObj = classObj.getSuperclass();
        }
        return false;

    }
}
