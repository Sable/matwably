package matwably.util;

import ast.Name;
import ast.Stmt;
import matwably.analysis.intermediate_variable.ReachingDefinitions;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.analysis.core.Def;
import natlab.utils.NodeFinder;

import java.util.Set;

public class LogicalVariableUtil  {
    private final ReachingDefinitions rd;
    private final InterproceduralFunctionQuery functionQuery;

    public LogicalVariableUtil(InterproceduralFunctionQuery functionQuery, ReachingDefinitions defs){
        this.functionQuery = functionQuery;
        this.rd = defs;
    }

    public boolean isUseLogical(Name name) {
        Set<Def> defs = rd.getUseDefDefUseChain().getDefs(name);
        Stmt useStmt = NodeFinder.findParent(Stmt.class,name);

        return (useStmt instanceof TIRIfStmt || useStmt instanceof TIRWhileStmt)
         && (defs.size() > 0 && defs
                .stream().allMatch((Def def)->
                        def instanceof TIRCallStmt
                                && isCallLogical((TIRCallStmt) def)));
    }
    public boolean isDefinitionLogical(String name, Def def){
        Set<Name> uses = rd.getUseDefDefUseChain()
                .getUsesOf(name, def);
        return def instanceof TIRCallStmt && isCallLogical((TIRCallStmt) def)
                && uses
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
