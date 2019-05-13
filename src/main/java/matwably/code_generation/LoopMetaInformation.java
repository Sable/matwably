package matwably.code_generation;

import ast.Stmt;
import matwably.ast.Idx;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.util.Util;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

/**
 * TODO JAVADOC
 */
public class LoopMetaInformation {
    private LoopDirection direction;
    private Idx startLoop;
    private Idx endLoop;
    private Stmt loop;
    private List<Instruction> conditionCode;

    public LoopMetaInformation(Stmt loop, Idx startLoop, Idx endLoop, List<Instruction> conditionCode) {
        this.startLoop = startLoop;
        this.endLoop = endLoop;
        this.loop = loop;
        this.conditionCode = conditionCode;
    }

    public List<Instruction> getConditionCode() {
        return conditionCode;
    }

    public Idx getStartLoop() {
        return startLoop;
    }

    public Idx getEndLoop() {
        return endLoop;
    }

    public Stmt getLoop() {
        return loop;
    }

    public void setDirection(LoopDirection direction){
        this.direction = direction;
    }
    public LoopDirection getDirection(){
        return direction;
    }


    public static LoopDirection getLoopDirection(TIRForStmt tirStmt,
                                                 IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>
                                                         analysisFunction) {
        BasicMatrixValue valRangeLeft = Util.getBasicMatrixValue(analysisFunction, tirStmt, tirStmt.getLowerName().getID());
        BasicMatrixValue valRangeRight = Util.getBasicMatrixValue(analysisFunction, tirStmt, tirStmt.getUpperName().getID());
        if(valRangeRight.hasRangeValue()&&valRangeLeft.hasRangeValue()){
            if (valRangeRight.getRangeValue()
                    .isBothBoundsKnown()) {
                int valRight = valRangeRight.getRangeValue().getLowerBound().getIntValue();
                int valLeft =  valRangeLeft.getRangeValue().getUpperBound().getIntValue();
                if(valLeft == valRight) return LoopDirection.NonMoving;
                if(tirStmt.hasIncr()){
                    BasicMatrixValue val = Util.getBasicMatrixValue(analysisFunction, tirStmt, tirStmt.getIncName().getID());
                    if(val.hasRangeValue()){
                        if(val.getRangeValue().isRangeValueNegative()){
                            //e.g. -5:-1:5
                            if(valRight > valLeft) return LoopDirection.Empty;
                            return LoopDirection.Descending;
                        }else if(val.getRangeValue().isRangeValuePositive()){
                            //e.g. 5:1:-5
                            if(valRight < valLeft) return LoopDirection.Empty;
                            return LoopDirection.Ascending;
                        }
                    }else return LoopDirection.Unknown;

                }
                if(valRight > valLeft){
                    return LoopDirection.Ascending;
                }else{
                    return LoopDirection.Descending;
                }
            }
        }
        return (tirStmt.hasIncr())? LoopDirection.Unknown: LoopDirection.Ascending;}
}
