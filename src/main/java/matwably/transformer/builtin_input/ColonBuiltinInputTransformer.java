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

import ast.NameExpr;
import matwably.util.Util;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.components.shape.Shape;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ColonBuiltinInputTransformer extends AbstractBuiltinInputTransformer {

    public ColonBuiltinInputTransformer(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, TIRCallStmt stmt) {
        super(analysis, stmt);
    }
    @Override
    public boolean isInForm() {
        // TODO (dherre3): Generalize to all number types
        return false;
    }

    @Override
    public TIRCommaSeparatedList convertInput() {
        if(isInForm()) {
            return null;
        }
        for(ast.Expr argExpr: stmt.getArguments()){
            ast.NameExpr nameExpr = (ast.NameExpr) argExpr;
            System.out.println(nameExpr.getName().getID());
            AggrValue<BasicMatrixValue> val = valueAnalysis
                    .getOutFlowSets()
                    .get(stmt)
                    .get(nameExpr.getName().getID())
                    .getSingleton();
            BasicMatrixValue matVal = (BasicMatrixValue)val;
            if(matVal.hasShape()){
                System.out.println(nameExpr.getName().getID());
                System.out.println(matVal);
                System.out.println(stmt.getPrettyPrinted());
                System.out.println(matVal.getShape().getDimensions());
            }else{
                throw new UnsupportedOperationException();
            }

            BasicMatrixValue bmv = Util.getBasicMatrixValue(valueAnalysis,stmt,
                    ((NameExpr) argExpr).getName().getID());
        }

        return null;
    }
}
