/*
 *  Copyright (c) 2018. David Fernando Herrera, McGill University
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 *  OF ANY KIND, either express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 */

package matwably.transformer;

import ast.*;
import matwably.transformer.builtin_input.AbstractBuiltinInputTransformer;
import matwably.transformer.builtin_input.BuiltinInput;
import matwably.transformer.builtin_input.ColonBuiltinInputTransformer;
import matwably.transformer.builtin_input.ShapeBuiltinInputTransformer;
import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import java.util.HashSet;

import static matwably.transformer.builtin_input.BuiltinInput.getTransformer;

public class BuiltinInputTransformer {
    public static boolean apply(TIRFunction func, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis){
        BuiltinInputTransformerTraversal trans = new BuiltinInputTransformerTraversal(analysis);
        func.tirAnalyze(trans);
        return trans.transformed;
    }
    private static class BuiltinInputTransformerTraversal extends TIRAbstractNodeCaseHandler {
        AbstractBuiltinInputTransformer startBuiltinHandler;
        IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
        BuiltinInputTransformerVisitor visitor;
        boolean transformed = false;
        HashSet<ASTNode> visitedNodes = new HashSet<>();
        BuiltinInputTransformerTraversal(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis){
            this.analysis = analysis;
            this.visitor = new BuiltinInputTransformerVisitor();
        }
        @Override
        public void caseASTNode(ASTNode node) {
            for(int i = 0; i < node.getNumChild(); i++) {
                ASTNode child = node.getChild(i);
                if (child instanceof TIRNode) {
                    ((TIRNode) child).tirAnalyze(this);
                }
                else {
                    child.analyze(this);
                }
            }
        }
//        public void caseTIR
        public void caseTIRCallStmt(TIRCallStmt stmt) {
            Builtin funcBuiltin = Builtin.getInstance(stmt.getFunctionName().getID());
            AbstractBuiltinInputTransformer transformer = getAndInstantiateTransformer(stmt);
            // Check that the function call is a builtin function
            if(funcBuiltin != null && transformer != null && !visitedNodes.contains(stmt)){
                TIRCommaSeparatedList new_input = transformer.transform();
                if(new_input != null){
                    transformed = true;
                    stmt.setRHS(new ParameterizedExpr(new NameExpr(stmt.getFunctionName()),new_input));
                    System.out.println(stmt.getPrettyPrinted());
                    visitedNodes.add(stmt);
                }
            }
        }

        private AbstractBuiltinInputTransformer getAndInstantiateTransformer(TIRCallStmt stmt) {
            Class<? extends  AbstractBuiltinInputTransformer>  transformer =
                        BuiltinInput.getTransformer(stmt.getFunctionName().getID());
            if(transformer != null) {
                try{
                    return transformer.getConstructor(IntraproceduralValueAnalysis.class, TIRCallStmt.class)
                            .newInstance(analysis, stmt);
                }catch (Exception e){
                    throw new Error("Problem instantiating builtin tranformer");
                }
            }else{
                return null;
            }
        }


    }
}
