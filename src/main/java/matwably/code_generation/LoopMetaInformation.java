package matwably.code_generation;

import ast.Stmt;
import matwably.ast.Idx;
import matwably.ast.Instruction;
import matwably.ast.List;
import matwably.util.ValueAnalysisUtil;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;

/**
 * Class contains the loop information:
 *  - Instructions to promote outside the loop as loop-invariants
 *  - Instructions to move immediately after the loop
 *  - Instructions for condition
 *  - WasmIR indices to break and stop
 *  - LoopDirection and calculation
 */
public class LoopMetaInformation {
    /**
     * Loop direction
     */
    private LoopDirection direction;
    /**
     * Index to jump back to the beginning of the loop
     */
    private Idx startLoop;
    /**
     * Index to jump to the end of the loop
     */
    private Idx endLoop;
    /**
     * TameIr stmt for the loop
     */
    private Stmt loop;
    /**
     * Condition instructions, they are used twice, to jump back to the beginning
     * and before a continue statement
     */
    private List<Instruction> conditionCode;
    /**
     * Post loop instructions
     */
    private List<Instruction> instructionsPostLoop = new List<>();
    /**
     * Pre-loop instructions
     */
    private List<Instruction> instructionsInitialization = new List<>();

    /**
     * Constructor
     * @param loop TameIR Loop, either TIRForStmt, or TIRWhileStmt
     * @param startLoop Index to beginning of loop
     * @param endLoop Index to end of loop
     * @param conditionCode Instructions for condition
     */
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

    /**
     * Getter instructions post-loop
     * @return returns instructions post-loop
     */
    public List<Instruction> getInstructionsPostLoop() {
        return instructionsPostLoop;
    }
    /**
     * Getter instructions pre-loop
     * @return returns instructions pre-loop
     */
    public List<Instruction> getInstructionsInitialization() {
        return instructionsInitialization;
    }

    /**
     * Getter condition instructions
     * @return Returns condition instructions
     */
    public List<Instruction> getConditionCode() {
        return conditionCode;
    }
    /**
     * Getter index start of loop
     * @return Returns Idx to jump to the start of loop
     */
    public Idx getStartLoop() {
        return startLoop;
    }
    /**
     * Getter index end of loop
     * @return Returns Idx to jump to the end of loop
     */
    public Idx getEndLoop() {
        return endLoop;
    }

    /**
     * Getter condition instructions
     * @return Returns loop stmt
     */
    public Stmt getLoop() {
        return loop;
    }

    /**
     * Get direction loop
     * @return Returns loop direction enum
     */
    public LoopDirection getDirection(){
        return direction;
    }

    /**
     * Computes the loop direction using range analysis
     * @param tirStmt For-stmt to run analysis for
     * @param valueAnalysisUtil Analysis to get ranges for loop variables
     * @return returns the direction of the loop,
     * i.e. constant (one-time), increasing, decreasing, empty
     */
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
