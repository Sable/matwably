package matwably.util;

import ast.*;
import matjuice.transformer.MJCopyStmt;
import matwably.analysis.intermediate_variable.UseDefDefUseChain;
import natlab.tame.tir.TIRCommentStmt;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueFlowMap;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.toolkits.analysis.core.Def;
import natlab.utils.NodeFinder;

import java.util.Set;
import java.util.stream.Collectors;

public class ValueAnalysisUtil {
    private final UseDefDefUseChain udChain;
    private IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction;

    public ValueAnalysisUtil(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysisFunction, UseDefDefUseChain udChain) {
        this.analysisFunction = analysisFunction;
        this.udChain = udChain;
    }

    public boolean isScalar(String name, ASTNode node, boolean isRHS) {
        BasicMatrixValue val = getMatrixValue(name, node, isRHS);
        return val != null && val.hasShape() && val.getShape().isScalar();
    }

    /**
     * Returns whether the nameExpr
     *
     * @param name NameExpression to analyze
     * @param node TIRNode node where there NameExpression happes
     * @return Returns double from a nameExpr if its a constant.
     */
    public boolean isVector(NameExpr name, ASTNode node) {
        BasicMatrixValue val = getMatrixValue(name.getVarName(), node, true);
        return val != null && val.hasShape() && val.getShape().isVector();
    }
    /**
     * Returns whether the nameExpr is for a fact a RowVector, if
     * its false, it may or it may not be one.
     *
     * @param name NameExpression to analyze
     * @param node TIRNode node where there NameExpression happes
     * @return Returns double from a nameExpr if its a constant.
     */
    public boolean isRowVector(NameExpr name, ASTNode node) {
        BasicMatrixValue val = getMatrixValue(name.getVarName(), node, true);
        return val != null && val.hasShape() && val.getShape().isRowVector();
    }
    /**
     * Returns whether the nameExpr is for a fact NOT RowVector, if
     * its false, it may or it may not be one.
     * @param name NameExpression to analyze
     * @param node TIRNode node where there NameExpression happes
     * @return Returns double from a nameExpr if its a constant.
     */
    public boolean isNotRowVector(NameExpr name, ASTNode node) {
        Shape val = getShape(name.getVarName(), node, true);
        if(val != null){
            return val.getDimensions().size() != 2 || (val.getDimensions()
                    .get(0).hasIntValue() &&
                    !val.getDimensions().get(0).equalsOne());
        }
        return false;
    }


    /**
     * Returns shape if it exists
     *
     * @param name Name to analyze
     * @param node TIRNode node where there NameExpression happes
     * @return Returns Shape from a nameExpr if it exists
     */
    public Shape getShape(String name, ASTNode node, boolean isRHS) {
        BasicMatrixValue val = getMatrixValue(name, node, isRHS);
        if (val != null && val.hasShape()) return val.getShape();
        return null;
    }

    /**
     * Returns shape if it exists
     *
     * @param name NameExpression to analyze
     * @param node TIRNode node where there NameExpression happes
     * @return Returns Shape from a nameExpr if it exists
     */
    public Shape getShape(NameExpr name, ASTNode node,boolean isRHS) {
        BasicMatrixValue val = getMatrixValue(name.getName().getID(), node, isRHS);
        if (val != null && val.hasShape()) return val.getShape();
        return null;
    }

    /**
     * If the value is a scalar, a constant, and a double, it returns the scalar.
     *
     * @param name NameExpression to analyze
     * @param node TIRNode node where there NameExpression happes
     * @return Returns double from a nameExpr if its a constant.
     */
    public Double getDoubleConstant(NameExpr name, ASTNode node) {
        BasicMatrixValue temp = getMatrixValue(name.getName().getID(), node, true);
        if (temp != null && temp.hasConstant() && temp.getConstant().getValue() instanceof Double) {
            return (Double) temp.getConstant().getValue();
        }
        return null;
    }

    /**
     * If the value is a scalar, a constant, and a double, it returns the scalar.
     *
     * @param name NameExpression to analyze
     * @param node TIRNode node where there NameExpression happes
     * @return Returns double from a nameExpr if its a constant.
     */
    public boolean isScalar(NameExpr name, ASTNode node, boolean isRHS) {
        BasicMatrixValue val = getMatrixValue(name.getVarName(), node, isRHS);
        return val != null && val.hasShape() && val.getShape().isScalar();
    }
    private BasicMatrixValue getArgumentMatrixValue(int argIndex){
        if(this.analysisFunction.getArgs().size() > argIndex){
            AggrValue<BasicMatrixValue> val = this.analysisFunction.getArgs().get(argIndex);
            if(val!= null && val instanceof BasicMatrixValue) {
                return (BasicMatrixValue) val;
            }
        }
        return null;
    }
    public Shape getArgumentShape(int argIndex){
        if(this.analysisFunction.getArgs().size() > argIndex){
            AggrValue<BasicMatrixValue> val = this.analysisFunction.getArgs().get(argIndex);
            if(val!= null && val instanceof BasicMatrixValue) {
                return ((BasicMatrixValue) val).getShape();
            }
        }
        return null;
    }

    public boolean isArgumentScalar(int argIndex){
        BasicMatrixValue val = this.getArgumentMatrixValue(argIndex);
        return val != null && val.hasShape() && val.getShape().isScalar();
    }
    public boolean isArgumentScalar(Name name){
        for(int i = 0; i < this.analysisFunction.getTree()
                .getInputParamList().getNumChild(); i++){
            Name argName = this.analysisFunction.getTree().
                    getInputParam(i);
            if(argName == name) return this.isArgumentScalar(i);
        }
        return false;
    }
    /**
     * Returns whether the nameExpr
     *
     * @param name  NameExpression to analyze
     * @param node  TIRNode node where there NameExpression happes
     * @param isRHS whether we want to analyze the inFlow or OutFlow. i.e. parameters or. arguments to calls.
     * @return Returns double from a nameExpr if its a constant.
     */
    public boolean isVector(String name, ASTNode node, boolean isRHS) {

        BasicMatrixValue val = getMatrixValue(name, node, isRHS);
        return val != null && val.hasShape() && val.getShape().isVector();
    }

    /**
     * If the value is a scalar, a constant, and a double, it returns the scalar.
     *
     * @param name  String name to analyze
     * @param node  TIRNode node where there NameExpression happens
     * @param isRHS whether we want to analyze the inFlow or OutFlow. i.e. parameters or. arguments to calls.
     * @return Returns double from a nameExpr if its a constant.
     */
    public Double getDoubleConstant(String name, ASTNode node, boolean isRHS) {
        BasicMatrixValue temp = getMatrixValue(name, node, isRHS);
        if (temp != null && temp.hasConstant() && temp.getConstant().getValue() instanceof Double) {
            return (Double) temp.getConstant().getValue();
        }
        return null;
    }


    /**
     * Returns the BasicMatrixValue from value analysis. If it a MJCopyStmt, it goes to Source in the statement
     * to get the value. Patch since ValueAnalysis is performed before the insertion of the MJCopyStmt's
     * @param name  String name to analyze
     * @param node  TIRNode node where there NameExpression happes
     * @param isRHS whether we want to analyze the inFlow or OutFlow. i.e. parameters or. arguments to calls.
     * @return Returns the matrix value for the
     */
    private BasicMatrixValue getMatrixValue(String name, ASTNode node, boolean isRHS) {
        ValueSet<AggrValue<BasicMatrixValue>> val;
        if(node instanceof MJCopyStmt)
        {
            MJCopyStmt stmt = (MJCopyStmt)node;
            // If only one definition, return that one. If all scalar, return one scalar def, if not return null
            Set<Def> defs = this.udChain.getDefs(stmt.getSourceName());
            if(defs.size() == 1){
                ASTNode defNode = (ASTNode) defs.toArray()[0];
                // If is Name it's an argument in the function, therefore the correct node is actually
                // the function itself.
                if(defNode instanceof Name){
                    defNode = NodeFinder.findParent(Function.class, defNode);
                }
                return getMatrixValue(stmt.getSourceName().getID(), defNode, false);
            }else{
                boolean allScalar = defs.stream().allMatch((Def def) ->
                        this.isScalar(stmt.getSourceName().getID(),
                                (ASTNode) def, false));
                if(allScalar && defs.size() > 0){
                    return getMatrixValue(stmt.getSourceName().getID(), (ASTNode) defs.toArray()[0], false);
                }else{
                    return null;
                }
            }

        }else {
            if (isRHS ) {
                val = analysisFunction
                        .getInFlowSets()
                        .get(node)
                        .get(name);
            } else {
                val = analysisFunction
                        .getOutFlowSets()
                        .get(node)
                        .get(name);

            }
        }

        if (val != null && val.getSingleton() instanceof BasicMatrixValue) return (BasicMatrixValue) val.getSingleton();
        return null;

    }

    public boolean isDefined(String name, ASTNode node, boolean isRHS){
       return getMatrixValue(name, node, true) != null;
    }

    public String genTypedName(String name, ASTNode node, boolean isRHS) {
        if (this.isScalar(name, node, isRHS)) {
            return name + "_f64";
        } else {
            return name + "_i32";
        }
    }

    public String genTypedName(NameExpr name, ASTNode node, boolean isRHS) {
        String id = name.getVarName();
        if (this.isScalar(name, node, isRHS)) {
            return id + "_f64";
        } else {
            return id + "_i32";
        }
    }

    public boolean isMatrix(NameExpr nameExpresion, ASTNode node) {

        BasicMatrixValue val = getMatrixValue(nameExpresion.getVarName(), node, true);
        return val != null && val.hasShape() && val.getShape().isMatrix();
    }

    public boolean hasMoreThanTwoDimensions(NameExpr nameExpresion, ASTNode node, boolean isRHS) {
        Shape sh = getShape(nameExpresion, node,isRHS);
        return sh != null && sh.ndim() > 2;
    }

    public boolean hasBasicMatrixValue(String name, ASTNode node, boolean isRHS) {
        return getMatrixValue(name, node, isRHS) != null;
    }

    public BasicMatrixValue getBasicMatrixValue(String name, ASTNode node, boolean isRHS) {
        return getMatrixValue(name, node, isRHS);
    }

    public void mapNewReturn() {
        java.util.List<Stmt> list =
                this.analysisFunction.getTree().getStmtList()
                .stream().filter((s)->!(s instanceof TIRCommentStmt)).
                collect(Collectors.toList());
        Stmt ret = list.get(list.size()-1);
        ValueFlowMap<AggrValue<BasicMatrixValue>> mapIn =
                this.analysisFunction.getOutFlowSets().get(
                list.get(list.size()-2));
        this.analysisFunction.getInFlowSets().put(ret, mapIn);
        this.analysisFunction.getOutFlowSets().put(ret, mapIn);
    }
}
