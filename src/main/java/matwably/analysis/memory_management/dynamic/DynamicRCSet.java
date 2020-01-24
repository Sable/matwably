package matwably.analysis.memory_management.dynamic;

import java.util.HashSet;
import java.util.Set;

class DynamicRCSet {

    private Set<String> checkExternalDecreaseSite = new HashSet<>();
    private Set<String> checkExternalIncreaseSite = new HashSet<>();
    private Set<String> checkExternalToSetReturnFlagAndSetRCToZero = new HashSet<>();
    private Set<String> checkExternalAndCheckReturnFlagToFree = new HashSet<>();
    private Set<String> checkAndAddExternalFlagSet = new HashSet<>();

    void increaseReference(String site){
        checkExternalIncreaseSite.add(site);
    }
    void decreaseReference(String site){
        checkExternalDecreaseSite.add(site);
    }

    Set<String> getCheckExternalAndCheckReturnFlagToFree() {
        return checkExternalAndCheckReturnFlagToFree;
    }
    Set<String> getCheckExternalDecreaseSite() {
        return checkExternalDecreaseSite;
    }
    Set<String> getCheckExternalIncreaseSite() {
        return checkExternalIncreaseSite;
    }
    Set<String> getCheckAndAddExternalFlagSet() {
        return checkAndAddExternalFlagSet;
    }
    Set<String> getCheckExternalToSetReturnFlagAndSetRCToZero() {
        return checkExternalToSetReturnFlagAndSetRCToZero;
    }
    void addCheckExternalAndSetReturnFlagToSetRCToZero(String site) {
        this.checkExternalToSetReturnFlagAndSetRCToZero.add(site);
    }
    void addCheckExternalAndCheckReturnFlagToFree(String site) {
        this.checkExternalAndCheckReturnFlagToFree.add(site);
    }
    void merge(DynamicRCSet ret) {
        this.checkExternalDecreaseSite.addAll(ret.checkExternalDecreaseSite);
        this.checkExternalIncreaseSite.addAll(ret.checkExternalIncreaseSite);
        this.checkExternalAndCheckReturnFlagToFree.addAll(ret.checkExternalAndCheckReturnFlagToFree);
    }
}
