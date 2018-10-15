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

import natlab.tame.builtin.Builtin;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

import java.util.HashSet;

public abstract class AbstractBuiltinInputTransformer {
    TIRCallStmt stmt;
    IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> valueAnalysis;
    public AbstractBuiltinInputTransformer(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, TIRCallStmt stmt){
        this.valueAnalysis = analysis;
        this.stmt = stmt;
    }
    public TIRCommaSeparatedList transform(){
        if(isInForm()){
            return null;
        }else{
            return convertInput();
        }
    }
    public abstract boolean isInForm();
    protected abstract TIRCommaSeparatedList convertInput();
}
