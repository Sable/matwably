package matwably.analysis.memory_management;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;
import ast.Stmt;
import matjuice.transformer.MJCopyStmt;
import matwably.MatWablyCommandLineOptions;
import matwably.util.InterproceduralFunctionQuery;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
// Allocate A site, use throughout expression evaluation
// B = 3*A + 10*B
/**
 *  Depends on ValueAnalysis
 *
 *  Do not analyze statements to eliminate via the intermediate_variable_elimination
 */
public class HybridRCGarbageCollectionAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<ReferenceCountMap> {
    private final ValueAnalysisUtil valueAnalysisUtil;
    private final InterproceduralFunctionQuery functionQuery;
    private final MatWablyCommandLineOptions opts;
    private final TIRFunction function;
    public static boolean Debug = false;
    /**
     * Processes dynamic sites, this site serves as cached initiated dynamic site.
     */
    private Set<DynamicSite> processed_initially_dynamic = new HashSet<>();

    /**
     * Base constructor for the analysis
     * @param astNode Function node
     * @param valueAnalysisUtil ValueAnalysisUtility
     * @param functionQuery Interprocedural Query Analysis
     */
    public HybridRCGarbageCollectionAnalysis(TIRFunction astNode,
                                             ValueAnalysisUtil valueAnalysisUtil,
                                             InterproceduralFunctionQuery functionQuery,
                                             MatWablyCommandLineOptions opts) {
        super(astNode);
        this.function = astNode;
        this.valueAnalysisUtil = valueAnalysisUtil;
        this.functionQuery = functionQuery;
        this.opts = opts;
        Debug = true;
    }

    @Override
    public ReferenceCountMap merge(ReferenceCountMap pointsToInformation, ReferenceCountMap a1) {
        return ReferenceCountMap.merge(pointsToInformation, a1);
    }

    @Override
    public ReferenceCountMap copy(ReferenceCountMap pointsToInformation) {
        return pointsToInformation.copy();
    }

    @Override
    public ReferenceCountMap newInitialFlow() {
        ReferenceCountMap map = new ReferenceCountMap();
        function.getInputParamList().stream().map(Name::getID).
                filter((String paramName)->!valueAnalysisUtil.isScalar(paramName,function,true))
                .forEach(map::addExternalDynamicSite);
        return map;
    }

    /**
     * To handle input arguments, we are going to take a conservative approach and simply make all input names
     * dynamic, to do this we add the name to dynamic site, and increase the reference dynamically by 1.
     * @param tirNode TIRFunction
     */
    public void caseTIRFunction(TIRFunction tirNode){
        currentInSet = newInitialFlow();
        if(Debug) log(currentInSet);
        currentOutSet = copy(currentInSet);
        caseASTNode(function);
        if(tirNode.getStmtList().getChild(tirNode.getStmtList()
                .getNumChild()-1).getClass()
                != TIRReturnStmt.class){
            processReturn();
        }
        outFlowSets.put(function, currentOutSet);
        if(Debug) log(tirNode);

    }
    /**
     * To handle this case, we take the inFlowSet, and anything variable that is not part of the output list
     * should be completely freed at this point, therefore we add freeing statements for this particular one
     * @param tirNode TIRReturnStmt
     *
     */
    public void caseTIRReturnStmt(TIRReturnStmt tirNode){
        checkForInitialDynamicSites();
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        processReturn();
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);
    }

    /**
     * Helper function adds the return logic.
     * In terms of returns, CopyAnalysis guarantees that return parameters are not
     * aliased to each other.
     * For sites, if they are to be returned, we need to make sure
     * that the aliased sites are not freed.
     *
     * Dynamic sites may be aliased to each other, in this case, we need a way
     * to dynamically check we are not freeing potentially aliased sites that
     * we expect to be returned.
     *
     * Here is the procedure,
     * For variables being returned:
     *  - If static:
     *      - Set RC to 0
     *      - Add all aliased sites to set of "processed" sites.
     *  - If dynamic:
     *      - Call to check if its internal
     *      - Internal
     *          -  Set return flag and reference count to 0.
     *
     * For variables being freed:
     *  - If static:
     *      - Check that is not aliased to variable that is returned.
     *          - If is not aliased, add to free set.
     *  - If dynamic:
     *      - Check two flags: Internal and Return flag is not set.
     *          - Free if the flags are as above.
     */
    private void processReturn(){
        Set<String> output = function.getOutputParamList()
                .stream().map(Name::getID)
                .filter((String varName)->!valueAnalysisUtil.
                        isScalar(varName, function,false))
                .collect(Collectors.toSet());
        // Initiate the dynamic site with the argument
        Set<String> processed = new HashSet<>();
        output.forEach((String name)->{
            if(currentOutSet.getStaticMemorySites().containsKey(name)){
                if(!processed.contains(name)){
                    currentOutSet.addDynamicInternalSetReturnFlagAndRCToZero(name);
                    processed.addAll(currentOutSet.getStaticMemorySites()
                            .get(name).getAliasingNames());
                }
            }
            if(currentOutSet.getDynamicMemorySites().containsKey(name)){
                DynamicSite site = currentOutSet.getDynamicMemorySites().get(name);
                if(site.isInternal()){
                    currentOutSet.addDynamicInternalSetReturnFlagAndRCToZero(name);
                }else if (site.isMaybeExternal()){
                    currentOutSet.addDynamicCheckExternalSetReturnFlagAndRCToZero(name);
                }
            }
        });

        // Free rest of arguments.
        // Process static freeing
        Set<String> free_static_sites_set = new HashSet<>(currentOutSet.getStaticMemorySites().keySet());
        free_static_sites_set.removeAll(output);
        free_static_sites_set.removeAll(processed);
        currentOutSet.addInternalFreeMemorySite(free_static_sites_set);
        // Process dynamic freeing
        Set<String> dynamic_site_names = new HashSet<>(currentOutSet.
                getDynamicMemorySites().keySet());
        dynamic_site_names.removeAll(output);
        dynamic_site_names.forEach((String name)->{
            DynamicSite site = currentOutSet.getDynamicMemorySites().get(name);
            if(site.isInternal()) {
                currentOutSet.addDynamicInternalCheckReturnFlagToFreeSites(name);
            }else if(site.isMaybeExternal()){
                currentOutSet.addDynamicCheckExternalAndReturnFlagToFreeSites(name);
            }
        });
    }

    /**
     * If the variable is a scalar, we need to simply remove the target variable
     * from the static or dynamic set.
     * If the variable is not a scalar, the analysis depends on the copy statement.
     * If it is a cloning  statement, we create a new static site for the source variable
     * If it is a reference statement, we create a reference between the two variables
     * if they copy statement is an MJCopyStmt or we omit copy analysis,
     * @param tirNode TIRCopyStmt to process
     */
    public void caseTIRCopyStmt(TIRCopyStmt tirNode){
        checkForInitialDynamicSites();
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        if(!valueAnalysisUtil.isScalar(tirNode.getSourceName().getID(), tirNode,true)) {
            if (tirNode instanceof MJCopyStmt || opts.omit_copy_insertion) {
                currentOutSet.decreaseReference(tirNode.getTargetName().getID());
                currentOutSet.addNewStaticSite(tirNode.getTargetName().getID(), tirNode);
            } else {
                if(!tirNode.getSourceName().getID().equals(tirNode.getTargetName().getID())){
                    currentOutSet.writeReference(tirNode.getSourceName().getID(),
                            tirNode.getTargetName().getID(), tirNode);
                }
            }
        }else{
            // Remove variable name from both static and dynamic sets.
            currentOutSet.decreaseReference(tirNode.getTargetName().getID());
        }
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);

    }

    /**
     * a = 3
     * a = 'a'
     * a = 3.00
     * Here, since we are handling only numbers, we simply remove the reference from the memory maps
     * @param tirNode Assign literal statement.
     */
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt tirNode){
        checkForInitialDynamicSites();
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        currentOutSet.decreaseReference(tirNode.getTargetName().getID());
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);
    }


    /**
    *
     * For the statements of this nature, we check whether the variables are scalars or allocations sites.
     * If they are allocation sites i.e. arrays, we create new static allocation sites
     * @param tirNode [a,b,c] = A(i,s)
     */
    public void caseTIRArrayGetStmt(TIRArrayGetStmt tirNode){
        checkForInitialDynamicSites();
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        for(NameExpr name: tirNode.getTargets().getNameExpressions()){
            if(!valueAnalysisUtil.isScalar(name.getName().getID(),tirNode,false)) {
                currentOutSet.decreaseReference(name.getVarName());
                currentOutSet.addNewStaticSite(name.getVarName(), tirNode);
            }else{
                currentOutSet.decreaseReference(name.getName().getID());
            }
        }
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);
    }

    /**
     * Get set of definitions for each name if the definition is a parameter in the
     * function, we add a dynamic check for external, if its external, we do not free it.
     * If is not a parameter, and is pointing to a site either dynamically or statically,
     * the global statement automatically frees the memory, from then on in the program,
     * the variable becomes and stays dynamic.
     * @param tirGlobalStmt TameIR Global statement.
     */
    @Override
    public void caseTIRGlobalStmt(TIRGlobalStmt tirGlobalStmt) {
        throw new UnsupportedOperationException("No Globals in the Analysis");
    }

    /**
     * There are two aspects, for calls to "user_defined" functions:
     * - If
     * @param tirNode TameIR node to be analized
     */
    public void caseTIRCallStmt(TIRCallStmt tirNode){
        checkForInitialDynamicSites();
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        String callName = tirNode.getFunctionName().getID();
        if(functionQuery.isUserDefinedFunction(callName)){
            // Tag input matrices as external
            // Built-in, we only need to handle output arguments.
            for(NameExpr expr: tirNode.getArguments()
                    .getNameExpressions()){
                String varName = expr.getName().getID();
                if(!valueAnalysisUtil.isScalar(varName, tirNode,true)){
                    if(currentOutSet.getStaticMemorySites().containsKey(varName)){
                        currentOutSet.addDynamicInternalSetSiteAsExternal(varName);
                    }else {
                        DynamicSite site = currentOutSet.getDynamicMemorySites()
                                .get(varName);
                        if(site.isInternal()){
                            currentOutSet.addDynamicInternalSetSiteAsExternal(varName);
                        }else if(site.isMaybeExternal()){
                            currentOutSet
                                    .addDynamicCheckExternalToSetSiteAsExternal(varName);
                        }
                    }
                }
            }
        }

        // Handle output targets we only need to handle output arguments.
        for(NameExpr name: tirNode.getTargets().getNameExpressions()){
            if(!valueAnalysisUtil.isScalar(name.getName().getID(),tirNode,false)) {
                currentOutSet.decreaseReference(name.getVarName());
                currentOutSet.addNewStaticSite(name.getName().getID(), tirNode);
            }else{
                currentOutSet.decreaseReference(name.getName().getID());
            }
        }
        outFlowSets.put(tirNode, copy(currentOutSet));
        if(Debug) log(tirNode);
    }





    /**
     * For this particular statement, we know that i is an scalar, all we have to do is to remove the i (loop-variable)
     * reference
     * @param tirNode for i=low:step:high
     */
    public void caseTIRForStmt(TIRForStmt tirNode) {
        checkForInitialDynamicSites();
        inFlowSets.put(tirNode, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        currentOutSet.decreaseReference(tirNode.getLoopVarName().getID());
        caseTIRStatementList(tirNode.getStatements());
        outFlowSets.put(tirNode, copy(currentOutSet));
        if (Debug) log(tirNode);
    }

    /**
     * Default to propagate sets through stmts
     * @param stmt Stmt TameIR node in the program
     */
    @Override
    public void caseStmt(Stmt stmt) {
        checkForInitialDynamicSites();
        inFlowSets.put(stmt, copy(currentInSet));
        currentOutSet = copy(currentInSet);
        outFlowSets.put(stmt, copy(currentOutSet));
    }

    /**
     * Whenever we perform a merge operation, there are sites that flow from the
     * static set into the dynamic set, these sites must be initiated dynamically
     * at those points to their lastly known static reference count.
     */
    private void checkForInitialDynamicSites(){
        Set<DynamicSite> dynamicSites = currentInSet.getInitiatedDynamicSites();
        dynamicSites.stream().filter(processed_initially_dynamic::contains)
                .forEach((DynamicSite site)->{
                    processed_initially_dynamic.add(site);
                    currentInSet.addNewInitiatingDynamicSite(site);
                });
    }
    private void log(ReferenceCountMap map){
        System.out.println("Initial Flow: ");
        System.out.println(map);
    }

    /**
     * Logging function for debugging purposes, prints results
     * of current set.
     * @param node AST node currently being processed.
     */
    private void log(ASTNode node){
        System.out.println("IR Node:");
        System.out.println(node.getPrettyPrinted());
        System.out.print("Current In Set:");
        System.out.println(currentInSet);
        System.out.print("Current Out Set:");
        System.out.println(currentOutSet);
    }
}
