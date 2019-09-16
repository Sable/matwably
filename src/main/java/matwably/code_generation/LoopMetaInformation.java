package matwably.code_generation;

import ast.Stmt;
import matwably.ast.Idx;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRForStmt;
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
    private List<Instruction> instructionsPostLoop = new List<>();
    private List<Instruction> instructionsInitialization = new List<>();

    public LoopMetaInformation(Stmt loop, Idx startLoop, Idx endLoop, List<Instruction> conditionCode) {
        this.startLoop = startLoop;
        this.endLoop = endLoop;
        this.loop = loop;
        this.conditionCode = conditionCode;
    }

    /**
     * Adds instructions, which will be added to the beginning of the loop.
     * @param initInst Instructions to add to initialization
     */
    public void addInstructionsInitialization(List<Instruction> initInst){
        this.instructionsInitialization =
                this.instructionsInitialization.addAll(initInst);
    }

    /**
     * Adds instructions, which will be added to the end of the loop
     * @param postLoop List of instructions to add to end
     */
    public void addInstructionsPostLoop(List<Instruction> postLoop){
        this.instructionsPostLoop =
                this.instructionsPostLoop.addAll(postLoop);
    }

    public List<Instruction> getInstructionsPostLoop() {
        return instructionsPostLoop;
    }

    public List<Instruction> getInstructionsInitialization() {
        return instructionsInitialization;
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
                                                 ValueAnalysisUtil valueAnalysisUtil) {
        BasicMatrixValue valRangeLeft =
                valueAnalysisUtil.getBasicMatrixValue(
                        tirStmt.getLowerName().getID(), tirStmt, true);
        BasicMatrixValue valRangeRight =
                valueAnalysisUtil.getBasicMatrixValue(
                        tirStmt.getUpperName().getID(), tirStmt, true);


        if(valRangeRight.hasRangeValue() && valRangeLeft.hasRangeValue()){
            if (valRangeRight.getRangeValue()
                    .isBothBoundsKnown()) {
                int valRight = valRangeRight.getRangeValue().getLowerBound().getIntValue();
                int valLeft =  valRangeLeft.getRangeValue().getUpperBound().getIntValue();
                if(valLeft == valRight) return LoopDirection.NonMoving;
                if(tirStmt.hasIncr()){
                    BasicMatrixValue val = valueAnalysisUtil.getBasicMatrixValue(
                            tirStmt.getIncName().getID(), tirStmt, true);
                    if(val !=null && val.hasRangeValue()){
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
