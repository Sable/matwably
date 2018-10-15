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

package matwably.analysis;

import ast.ASTNode;
import ast.Expr;
import ast.Name;
import ast.NameExpr;
import matwably.ast.TypeUse;
import matwably.util.Ast;
import matwably.util.Util;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Locals {
    public  HashMap<String, TypeUse> apply(TIRFunction func, IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis){
        LocalsFinder finder = new LocalsFinder(analysis);
        func.analyze(finder);
        return finder.names_mapping;
    }
    private static class LocalsFinder extends TIRAbstractNodeCaseHandler {
        private HashMap<String,TypeUse> names_mapping = new HashMap<>();
        private Set<String> names = new HashSet<>();
        IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis;

        private LocalsFinder(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis) {
            super();
            this.analysis = analysis;
        }

        @Override
        public void caseTIRArrayGetStmt(TIRArrayGetStmt tirArrayGetStmt) {
            addTargets(tirArrayGetStmt.getTargets(), tirArrayGetStmt);
        }


        @Override
        public void caseTIRCopyStmt(TIRCopyStmt tirCopyStmt) {
            String name = tirCopyStmt.getLHS().getVarName();
            addDeclaration(tirCopyStmt, name);
        }
        private String getTypedName(TIRNode node, String name){
            BasicMatrixValue nodeVal = Util.getBasicMatrixValue(analysis, node, name);
            return name +"_"+ ((nodeVal.getShape().isScalar())?"f64":"i32");
        }

        /**
         * Adds declaration to WebAssembly local set
         * @param node
         * @param name
         */
        private void addDeclaration(TIRNode node, String name){
            BasicMatrixValue nodeVal = Util.getBasicMatrixValue(analysis, node, name);
            String newName =  Util.getTypedName(name, nodeVal);
            if(!names_mapping.containsKey(newName)){
                TypeUse typeUse = Ast.genTypeUse(newName, nodeVal);
                names_mapping.put(newName, typeUse);
            }
        }
        private void addTargets(TIRCommaSeparatedList targets, TIRNode tirNode){
            for(Expr expr : targets){
                NameExpr exprName = (NameExpr) expr;
                String name = exprName.getName().getID();
                addDeclaration(tirNode, name);
            }
        }
        @Override
        public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt tirAssignLiteralStmt) {
            String name = tirAssignLiteralStmt.getLHS().getVarName();
            addDeclaration(tirAssignLiteralStmt, name);
        }


        @Override
        public void caseTIRCallStmt(TIRCallStmt tirCallStmt) {
            addTargets(tirCallStmt.getTargets(), tirCallStmt);
        }



        @Override
        public void caseTIRIfStmt(TIRIfStmt tirIfStmt) {
            caseASTNode(tirIfStmt.getIfStatements());
            if(tirIfStmt.hasElseBlock()){
                caseASTNode(tirIfStmt.getElseStatements());
            }
        }

        @Override
        public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt stmt) {
            for (String lhsName: stmt.getLValues())
                addDeclaration(stmt, lhsName);
        }

        @Override
        public void caseTIRForStmt(TIRForStmt stmt) {
            addDeclaration(stmt, stmt.getLoopVarName().getID());
            caseASTNode(stmt);
        }

        @Override
        public void caseTIRFunction(TIRFunction function){
            for (Name expr : function.getInputParamList()) {
                String newName = getTypedName(function, expr.getID());
                names_mapping.remove(newName);
            }
            caseASTNode(function.getStmtList());
        }



        @Override
        public void caseASTNode(ASTNode astNode) {
            for (int i = 0; i < astNode.getNumChild(); i++) {
                ASTNode child = astNode.getChild(i);
                if (child instanceof TIRNode) {
                    ((TIRNode) child).tirAnalyze(this);
                }
                else {
                    child.analyze(this);
                }
            }
        }

    }

}
