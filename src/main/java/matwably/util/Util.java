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

import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.toolkits.rewrite.TempFactory;

public class Util {
    public static BasicMatrixValue getBasicMatrixValue(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
                                                                     TIRNode tirNode, String variable) {
        if (tirNode == null)
            return null;

        ValueSet<AggrValue<BasicMatrixValue>> val = analysis
                .getOutFlowSets()
                .get(tirNode)
                .get(variable);
        if(val == null) return null;
        if (val.getSingleton() instanceof BasicMatrixValue)
            return (BasicMatrixValue) val.getSingleton();
        else
            return null;
    }
    public static String getTypedLocalI32(String name){
        return name+"_i32";
    }
    public static String getTypedLocalF64(String name){
        return name+"_f64";
    }
    public static String genTypedLocalI32(){
        return TempFactory.genFreshTempString()+"_i32";
    }
    public static String genTypedLocalF64(){
        return TempFactory.genFreshTempString()+"_f64";
    }
    public static String getTypedName(String name, BasicMatrixValue val) {
        if(!val.hasShape()){
            throw new Error("Variable: ["+name+"] has unknown shape value: "+val);
        }
        return name + "_"+((val.getShape().isScalar()?"f64":"i32"));
    }
//    public static ValueType getValueType(IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>> analysis, TIRNode tirNode, String variable ){
//        BasicMatrixValue bmv = Util.getBasicMatrixValue(analysis, tirNode, variable);
//        if (bmv.hasShape() && bmv.getShape().isScalar())
//            return new F64();
//        else
//            return new I32();
//
//    }
}
