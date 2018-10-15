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

package matwably.transformer.builtin_input;

import ast.Expr;
import ast.FPLiteralExpr;
import ast.Name;
import ast.NameExpr;
import matwably.util.Ast;
import matwably.util.Util;
import natlab.FPNumericLiteralValue;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;

import java.util.ArrayList;
import java.util.List;

public class ShapeBuiltinInputTransformer extends AbstractBuiltinInputTransformer {
    public ShapeBuiltinInputTransformer(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, TIRCallStmt stmt) {
        super(analysis, stmt);
    }

    public boolean isInForm() {
        if(stmt.getArguments().size() == 1){
            NameExpr nameExpr = (NameExpr)stmt.getArguments().getChild(0);
            BasicMatrixValue bmv = Util.getBasicMatrixValue(valueAnalysis,stmt,
                    nameExpr.getName().getID());
            if(bmv.hasShape()&&!bmv.getShape().isScalar() && bmv.getShape().getDimensions().size() == 2){
                List<DimValue> dimValues = bmv.getShape().getDimensions();
                return dimValues.get(0).getIntValue() == 1 && dimValues.get(1).getIntValue() > 2;
            }
        }
        return false;
    }

    @Override
    public TIRCommaSeparatedList convertInput() {
        if(isInForm()) {
            return null;
        }
        String target = "";
        int sizeArgs = stmt.getArguments().size();
        List<Expr> out = new ArrayList<>();
        if(sizeArgs == 0){
            long val = 1;
            out.add(new FPLiteralExpr((new FPNumericLiteralValue(""+val,false))));
            out.add(new FPLiteralExpr((new FPNumericLiteralValue(""+val,false))));
            target = Ast.generateAndInsertTIRRowArray(stmt, out);
            return new TIRCommaSeparatedList(new NameExpr(new Name(target)));
        }else if(sizeArgs == 1){
            NameExpr nameExpr = (NameExpr)stmt.getArguments().getChild(0);
            BasicMatrixValue bmv = Util.getBasicMatrixValue(valueAnalysis,stmt,
                    nameExpr.getName().getID());
            if(bmv.hasShape()&&bmv.getShape().isScalar()){
                out.add(nameExpr);
                out.add(nameExpr);
                target = Ast.generateAndInsertTIRRowArray(stmt, out);
                return new TIRCommaSeparatedList(new NameExpr(new Name(target)));
            }else if(bmv.hasShape()&&!bmv.getShape().isRowVector()) {
                throw new Error("Invalid argument to builtin: "+stmt.getFunctionName().getID()+
                            ", Size vector should be a row vector with real elements.");
            }else{
                return null;
            }
        }else if(sizeArgs > 1){
            // There are more than two arguments
            // e.g. ones(4,4,4,4) -> ones([4,4,4,4]);
            for(ast.Expr argExpr: stmt.getArguments()){
                ast.NameExpr nameExpr = (ast.NameExpr) argExpr;
                BasicMatrixValue matVal = Util.getBasicMatrixValue(valueAnalysis,stmt, nameExpr.getName().getID());
                System.out.println(matVal);
                if(!matVal.hasShape() || !matVal.getShape().isScalar()){
                    throw new Error("Invalid argument to builtin: "+stmt.getFunctionName().getID()+
                            ", Size inputs must be scalar.");
                }
                out.add(nameExpr);
            }
            target = Ast.generateAndInsertTIRRowArray(stmt, out);
            return new TIRCommaSeparatedList(new NameExpr(new Name(target)));
        }


        return null;
    }
}
