package matwably.analysis.ambiguous_scalar_analysis;

import ast.ASTNode;
import ast.NameExpr;

public class AmbiguousVariableUtil {

    private AmbiguousVariableAnalysis ambiguousVariableAnalysis;

    public AmbiguousVariableUtil(AmbiguousVariableAnalysis analysis){
        this.ambiguousVariableAnalysis = analysis;
    }

    public boolean isAmbiguous(String name, ASTNode node){
        return this.ambiguousVariableAnalysis.getOutFlowSets().get(node)!=null &&
                this.ambiguousVariableAnalysis.getOutFlowSets().get(node).isAmbiguous(name);
    }

    public boolean isAmbiguous(NameExpr name, ASTNode node){
        return this.ambiguousVariableAnalysis.getOutFlowSets().get(node)!=null   &&
                this.ambiguousVariableAnalysis.getOutFlowSets().get(node)
                .isAmbiguous(name.getName().getID());
    }

}
