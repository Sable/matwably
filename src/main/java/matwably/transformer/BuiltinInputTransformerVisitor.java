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

import matwably.transformer.builtin_input.ShapeBuiltinInputTransformer;
import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

public class BuiltinInputTransformerVisitor extends BuiltinVisitor<ArgInputTransform, TIRCommaSeparatedList> {
    @Override
    public TIRCommaSeparatedList caseBuiltin(Builtin builtin, ArgInputTransform argInputTransform) {
        return null;
    }

    @Override
    public TIRCommaSeparatedList caseAbstractByShapeAndTypeMatrixCreation(Builtin builtin, ArgInputTransform argInputTransform) {
        // Transforms ones, zeroes, magic, eye into canonical form
        IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis = argInputTransform.getAnalysis();
        TIRCallStmt stmt = argInputTransform.getStmt();
        //1. Check if its in canonical mode
        //2. Transform if its not in canonical form.
        ShapeBuiltinInputTransformer transformer = new ShapeBuiltinInputTransformer(analysis, stmt);
        return transformer.transform();
    }
}
