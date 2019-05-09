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

package matwably.util;
import ast.ASTNode;
import ast.*;
import ast.List;
import matwably.ast.*;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.rewrite.TempFactory;

public class Ast {

    /**
     * This function adds a list of
     * @param node
     * @param newSibling
     * @param offset
     */
    public static void addSiblingByOffset(ASTNode node, ASTNode newSibling, int offset){
        ASTNode parent = node.getParent();
        int index = parent.getIndexOfChild(node);
        int newOffset = index + offset;
        if(parent.getNumChild()+1 < newOffset || newOffset < 0){
            throw new IndexOutOfBoundsException("The new offset: "+newOffset+ ", for node insertion is out of bounce. " +
                    "for current node: "
                    + node.getPrettyPrinted()+"with index: "+index);
        }
        parent.insertChild(newSibling, index + offset);
    }

    /**
     * This function adds a list of children before the specified node by accessing the parent
     * @param node
     * @param nodes
     */
    public static void addLeftSiblings(ASTNode node, List<Stmt> nodes){
        for(Stmt n: nodes){
            ASTNode parent = node.getParent();
            int index = parent.getIndexOfChild(node);
            parent.insertChild(n, index);
        }
    }
    /**
     * This function creates a row array using a TIRCallStmt and returns the name of the variable
     * @param node
     * @param values
     * @return
     */
    public static String generateAndInsertTIRRowArray(ASTNode node, java.util.List<Expr> values){
        String newStmtTarget = TempFactory.genFreshTempString();
        TIRCommaSeparatedList args = new TIRCommaSeparatedList(); // Arguments of call
        List<Stmt> assignments = new List<>();
        values.forEach((Expr val)->{
            if(val instanceof FPLiteralExpr){
                String val1 = TempFactory.genFreshTempString();
                assignments.add(new TIRAssignLiteralStmt(new Name(val1), (FPLiteralExpr) val));
                args.add(new NameExpr(new Name(val1)));
            }else if(val instanceof NameExpr){
                args.add(val);
            }else{
                throw new UnsupportedOperationException("Cannot handle other types different from "
                +"literals and NameExpressions");
            }
        });
        TIRCallStmt newNode = new TIRCallStmt(new Name("horzcat"),
                new TIRCommaSeparatedList(new NameExpr(new Name(newStmtTarget))),
                args);
        assignments.add(newNode);
        addLeftSiblings(node, assignments);
        return newStmtTarget;
    }
    public static TypeUse genF64TypeUse(String name){
        TypeUse typeUse = new TypeUse();
        typeUse.setIdentifier(new Identifier(name));
        typeUse.setType(new F64());
        return typeUse;
    }
    public static TypeUse genI32TypeUse(String name){
        TypeUse typeUse = new TypeUse();
        typeUse.setIdentifier(new Identifier(name));
        typeUse.setType(new I32());
        return typeUse;
    }

    public static TypeUse genTypeUse(String name, BasicMatrixValue val) {
        TypeUse typeUse;
        if( val != null ){
            typeUse = new TypeUse();
            typeUse.setIdentifier(new Identifier(name));
            if( val.hasShape() && val.getShape().isScalar() ){
                typeUse.setType(new F64());
            }else{
                typeUse.setType(new I32());
            }
            return typeUse;
        }else{
            throw new Error("No analysis for variable: "+ name+ " locals analysis could not be finished");
        }
    }


    public static ValueType getValueType(String name, BasicMatrixValue val){
        if( val != null ){
            if( val.hasShape() && val.getShape().isScalar() ){
                return new F64();
            }else{
                return new I32();
            }
        }else{
            throw new Error("No analysis for variable: "+ name+ " locals analysis could not be finished");
        }
    }
}
