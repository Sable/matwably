package matwably.analysis.reaching_definitions;

import ast.*;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.core.Def;

import java.util.Set;
import java.util.stream.Collectors;

public class UseDefDefUse extends TIRAbstractSimpleStructuralForwardAnalysis<UseDefDefUseMap> {
    private final Function function;
    private boolean processArrays = true;

    public UseDefDefUse(TIRFunction astNode) {
        super(astNode);
        function = astNode;
    }

    public UseDefDefUse(TIRFunction astNode, boolean processArrays) {
        super(astNode);
        function = astNode;
        this.processArrays = processArrays;
    }

    @Override
    public void caseTIRFunction(TIRFunction func){
        currentInSet = newInitialFlow();
        currentOutSet = copy(currentInSet);
        func.getInputParamList().getNameExpressions().stream().map(NameExpr::getName).forEach(
                (Name param)-> replaceDefinition(param.getID(), param));
        caseASTNode(func);
        func.getOutputParamList().getNameExpressions().stream().map(NameExpr::getName).forEach(
                (Name param)-> mapUse(param, func));
    }

    /**
     * A global definitions are only added they have not been declared inside the context of the function.
     * In terms of execution, globals in Matlab are not hoisted to the top, this means variable names
     * are locals until the Global Stmt is declared. After this happens, if there are further global
     * stmts, they are practically ignored by the compiler. In terms of reaching definitions.
     * The definition of a new global, means the start of a new site for the variable. This variable
     * may have been assigned in some other function which executed earlier, which we do not know if the
     * global has been declared in the program statically.
     * @param stmt TameIR's global statement to process
     */
    @Override
    public void caseTIRGlobalStmt(TIRGlobalStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        stmt.getNames().forEach((Name name)->addGlobalDef(name.getID(), stmt));
        outFlowSets.put(stmt, currentOutSet);
    }

    @Override
    public void caseTIRCopyStmt(TIRCopyStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        replaceDefinition(stmt.getTargetName().getID(), stmt);
        mapUse(stmt.getSourceName(), stmt);
        outFlowSets.put(stmt, copy(currentOutSet));
    }

    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        replaceListVariableDefinitions(stmt.getTargets(), stmt);
        if(processArrays) mapUse(stmt.getArrayName(), stmt);
        mapUsesToDefinitions(stmt.getIndices().
                getNameExpressions().stream().map(NameExpr::getName).collect(Collectors.toSet()), stmt);
        outFlowSets.put(stmt, copy(currentOutSet));

    }

    private void mapUsesToDefinitions(Set<Name> names, ASTNode stmt) {
        names.forEach((Name use)->{
            mapUse(use, stmt);
        });
    }


    private void mapUse(Name use, ASTNode node) {
        String array = use.getID();
        // Map Defs to Uses
        Set<Def> defs = currentOutSet.get(array);

        defs.forEach((Def def)->{
            if(def instanceof Name){
                currentOutSet.mapDefUse((Name) def, use);
            }else if(def instanceof AssignStmt){
                if( def instanceof TIRAbstractAssignToListStmt){
                    TIRAbstractAssignToListStmt varStmt = (TIRAbstractAssignToListStmt) def;
                    Name defName = varStmt.getTargets().getNameExpressions().stream().map(NameExpr::getName)
                            .reduce(null,(Name name, Name acc)->{
                                if(name.getID().equals(array)){
                                    return name;
                                }else{
                                    return acc;
                                }
                            });
                    if(defName != null) currentOutSet.mapDefUse(defName, use);
                    else System.err.println("Array: "+array+" used without definition at: \n"+node.getPrettyPrinted());
                }else if(def instanceof TIRAbstractAssignToVarStmt){
                    TIRAbstractAssignToVarStmt varStmt = (TIRAbstractAssignToVarStmt) def;
                    currentOutSet.mapDefUse(varStmt.getTargetName(), use);
                }
            }
        });
        // Map Uses to Defs
        currentOutSet.mapUseDef(use, defs);


    }

    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        if(this.processArrays)
            replaceDefinition(stmt.getArrayName().getID(),stmt);

        mapUsesToDefinitions(stmt.getIndices().getNameExpressions()
                .stream().map(NameExpr::getName).collect(Collectors.toSet()), stmt);
        outFlowSets.put(stmt, copy(currentOutSet));

    }
    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        replaceDefinition(stmt.getTargetName().getID(), stmt);
        outFlowSets.put(stmt, copy(currentOutSet));

    }
    @Override
    public void caseTIRCallStmt(TIRCallStmt stmt){
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        for(ast.Expr expr: stmt.getTargets()){
            NameExpr nameExpr = (NameExpr) expr;
            replaceDefinition(nameExpr.getName().getID(), stmt);
        }
        mapUsesToDefinitions(stmt.getArguments().getNameExpressions().stream().
                map(NameExpr::getName).collect(Collectors.toSet()), stmt);
        outFlowSets.put(stmt, copy(currentOutSet));
    }

    @Override
    public void caseTIRIfStmt(TIRIfStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        mapUse(stmt.getConditionVarName(), stmt);
        super.caseTIRIfStmt(stmt);
        outFlowSets.put(stmt, copy(currentOutSet));
    }

    @Override
    public void caseTIRForStmt(TIRForStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        mapUse(stmt.getLowerName(), stmt);
        mapUse(stmt.getUpperName(), stmt);
        if(stmt.hasIncr()) mapUse(stmt.getIncName(), stmt);

        super.caseTIRForStmt(stmt);
        outFlowSets.put(stmt, copy(currentOutSet));
    }

    @Override
    public void caseTIRReturnStmt(TIRReturnStmt stmt) {
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        function.getOutputParamList().getNameExpressions().stream().map(NameExpr::getName).forEach(
                (Name param)-> mapUse(param, stmt));
        outFlowSets.put(stmt, copy(currentOutSet));
    }

    @Override
    public UseDefDefUseMap merge(UseDefDefUseMap reachingDefsMap, UseDefDefUseMap a1) {
        return reachingDefsMap.merge(a1);
    }

    @Override
    public UseDefDefUseMap copy(UseDefDefUseMap reachingDefsMap) {
        return reachingDefsMap.copy();
    }

    @Override
    public UseDefDefUseMap newInitialFlow() {
        return new UseDefDefUseMap();
    }

    @Override
    public void caseStmt(ast.Stmt node) {

        inFlowSets.put(node, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        outFlowSets.put(node, copy(currentOutSet));
    }
    // Helper functions
    private void replaceDefinition(String name, Def stmt){
        if(currentOutSet.containsKey(name)){
            currentOutSet.remove(name);
            currentOutSet.addDef(name,stmt);
        }else{
            currentOutSet.addDef(name,  stmt);
        }
    }
    private void addGlobalDef(String name, Def stmt){
        if(!currentOutSet.containsGlobal(name)){
            replaceDefinition(name, stmt);
            currentOutSet.addGlobalDef(name);
        }
    }
    private void replaceListVariableDefinitions(TIRCommaSeparatedList targets, Def stmt){
        for(NameExpr nameExpr: targets.getNameExpressions()){
            replaceDefinition(nameExpr.getName().getID(), stmt);
        }

    }
}
