package matwably.analysis.memory_management.dynamic;

import java.util.HashSet;
import java.util.Set;

public class DynamicRCSet {

    private Set<String> checkExternalDecreaseSite = new HashSet<>();
    private Set<String> checkExternalIncreaseSite = new HashSet<>();
    private Set<String> checkExternalToSetReturnFlagAndSetRCToZero = new HashSet<>();
    private Set<String> checkExternalAndCheckReturnFlagToFree = new HashSet<>();


    private Set<String> checkAndAddExternalFlagSet = new HashSet<>();


    private DynamicRCSet(Set<String> decreaseReferenceSet, Set<String> increaseReferenceSet, Set<String> checkExternalSetRCZero) {
        this.checkExternalDecreaseSite = decreaseReferenceSet;
        this.checkExternalIncreaseSite = increaseReferenceSet;
        this.checkExternalToSetReturnFlagAndSetRCToZero = checkExternalSetRCZero;
    }
    private DynamicRCSet(DynamicRCSet gcCall){
        this.checkExternalDecreaseSite = new HashSet<>(gcCall.getCheckExternalDecreaseSite());
        this.checkExternalIncreaseSite = new HashSet<>(gcCall.getCheckExternalIncreaseSite());
        this.checkExternalToSetReturnFlagAndSetRCToZero = new HashSet<>(gcCall.checkExternalToSetReturnFlagAndSetRCToZero);
        this.checkExternalAndCheckReturnFlagToFree = new HashSet<>(gcCall.getCheckExternalAndCheckReturnFlagToFree());
    }
    DynamicRCSet(){

    }
    public void increaseReference(String site){
        checkExternalIncreaseSite.add(site);
    }
    public void decreaseReference(String site){
        checkExternalDecreaseSite.add(site);
    }

    /**
     * Getter for external sites
     * @return
     */
    public Set<String> getCheckExternalAndCheckReturnFlagToFree() {
        return checkExternalAndCheckReturnFlagToFree;
    }

    public Set<String> getCheckExternalDecreaseSite() {
        return checkExternalDecreaseSite;
    }

    public Set<String> getCheckExternalIncreaseSite() {
        return checkExternalIncreaseSite;
    }

    public Set<String> getCheckAndAddExternalFlagSet() {
        return checkAndAddExternalFlagSet;
    }
    public Set<String> getCheckExternalToSetReturnFlagAndSetRCToZero() {
        return checkExternalToSetReturnFlagAndSetRCToZero;
    }
    public void addCheckExternalAndSetReturnFlagToSetRCToZero(String site) {
        this.checkExternalToSetReturnFlagAndSetRCToZero.add(site);
    }
    public void addCheckExternalAndCheckReturnFlagToFree(String site) {
        this.checkExternalAndCheckReturnFlagToFree.add(site);
    }

    public void merge(DynamicRCSet ret) {
        this.checkExternalDecreaseSite.addAll(ret.checkExternalDecreaseSite);
        this.checkExternalIncreaseSite.addAll(ret.checkExternalIncreaseSite);
        this.checkExternalAndCheckReturnFlagToFree.addAll(ret.checkExternalAndCheckReturnFlagToFree);
    }

    public void checkAndAddExternalFlag(String name) {
        this.checkAndAddExternalFlagSet.add(name);
    }
}
