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
import ast.Name;
import ast.NameExpr;
import matwably.ast.F64;
import matwably.ast.I32;
import matwably.ast.Identifier;
import matwably.ast.TypeUse;
import matwably.util.LogicalVariableUtil;
import matwably.util.Util;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.toolkits.analysis.core.Def;

import java.util.HashMap;

public class Locals {
    public  HashMap<String, TypeUse> apply(TIRFunction func, ValueAnalysisUtil valueAnalysisUtil,
                                           LogicalVariableUtil logicalVariableUtil, boolean disallow_logicals){
        LocalsFinder finder = new LocalsFinder(valueAnalysisUtil, logicalVariableUtil,disallow_logicals);
        func.analyze(finder);
        return finder.names_mapping;
    }


    private static class LocalsFinder extends TIRAbstractNodeCaseHandler {
        private final LogicalVariableUtil logicalVariableUtil;
        private final boolean disallow_logicals;
        private HashMap<String,TypeUse> names_mapping = new HashMap<>();
        ValueAnalysisUtil valueAnalysisUtil;

        LocalsFinder(ValueAnalysisUtil valueUtil, LogicalVariableUtil logicalVariableUtil, boolean disallow_logicals) {
            super();
            this.valueAnalysisUtil = valueUtil;
            this.logicalVariableUtil = logicalVariableUtil;
            this.disallow_logicals = disallow_logicals;
        }

        /**
         * Adds declaration to WebAssembly local set
         * @param node ast node associated with the declaration
         * @param name  Name of variable to add
         */
        private void addDeclaration(Name name, ASTNode node){
            TypeUse typeUse = new TypeUse();
            String identifier_name;
            boolean isScalar = valueAnalysisUtil.isScalar(name.getID(), node, false);
            if(isScalar) {
                if(!disallow_logicals &&
                        node instanceof Def &&
                        this.logicalVariableUtil.
                                isDefinitionLogical(name.getID(), (Def) node)){

                    identifier_name = Util.getTypedLocalI32(name.getID());
                    typeUse.setIdentifier(new Identifier(identifier_name));
                    typeUse.setType(new I32());
                }else{
                    identifier_name = Util.getTypedLocalF64(name.getID());
                    typeUse.setIdentifier(new Identifier(identifier_name));
                    typeUse.setType(new F64());
                }
            }else{
                identifier_name = Util.getTypedLocalI32(name.getID());
                typeUse.setIdentifier(new Identifier(identifier_name));
                typeUse.setType(new I32());
            }

            if(!names_mapping.containsKey(identifier_name)){
                names_mapping.put(identifier_name, typeUse);
            }
        }

        @Override
        public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt tirAbstractAssignToVarStmt) {
            addDeclaration(tirAbstractAssignToVarStmt.getTargetName(), tirAbstractAssignToVarStmt);
        }

        @Override
        public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt tirAbstractAssignToListStmt) {
            for(NameExpr name: tirAbstractAssignToListStmt.getTargets().getNameExpressions()){
                addDeclaration(name.getName(), tirAbstractAssignToListStmt);
            }
        }

        @Override
        public void caseTIRForStmt(TIRForStmt tirForStmt){
            addDeclaration(tirForStmt.getLoopVarName(), tirForStmt);
            caseASTNode(tirForStmt);
        }


        @Override
        public void caseTIRFunction(TIRFunction function){
            for(int i = 0;i<function.getInputParamList().getNumChild();i++){
                String typedName = function.getInputParam(i).getID();
                typedName+=(valueAnalysisUtil.isArgumentScalar(i))?"_f64":"_i32";
                names_mapping.remove(typedName);

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
