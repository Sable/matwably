package matwably.analysis.memory_management;

import java.util.HashSet;
import java.util.Set;

public class GCCallsSet {

    private Set<String> checkExternalDecreaseSite = new HashSet<>();
    private Set<String> checkExternalIncreaseSite = new HashSet<>();
    private Set<String> checkExternalSetRCZero = new HashSet<>();
    private Set<String> checkExternalAndFree = new HashSet<>();
    private Set<String> freeSiteSet = new HashSet<>();


    private Set<String> checkAndAddExternalFlagSet = new HashSet<>();


    private GCCallsSet(Set<String> decreaseReferenceSet, Set<String> increaseReferenceSet, Set<String> checkExternalSetRCZero, Set<String> freeSiteSet) {
        this.checkExternalDecreaseSite = decreaseReferenceSet;
        this.checkExternalIncreaseSite = increaseReferenceSet;
        this.checkExternalSetRCZero = checkExternalSetRCZero;
        this.freeSiteSet = freeSiteSet;
    }
    private GCCallsSet(GCCallsSet gcCall){
        this.checkExternalDecreaseSite = new HashSet<>(gcCall.getCheckExternalDecreaseSite());
        this.checkExternalIncreaseSite = new HashSet<>(gcCall.getCheckExternalIncreaseSite());
        this.checkExternalSetRCZero = new HashSet<>(gcCall.getCheckExternalSetRCZero());
        this.checkExternalAndFree = new HashSet<>(gcCall.getCheckExternalAndFree());
        this.freeSiteSet= new HashSet<>(gcCall.getFreeSiteSet());
    }
    GCCallsSet(){

    }
    public void increaseReference(String site){
        checkExternalIncreaseSite.add(site);
    }
    public void decreaseReference(String site){
        checkExternalDecreaseSite.add(site);
    }

    public void addCheckExternalSetRCToZero(String site){
        checkExternalSetRCZero.add(site);
    }

    public void addFreeSite(String site){
        freeSiteSet.add(site);
    }

    /**
     * Getter for external sites
     * @return
     */
    public Set<String> getCheckExternalAndFree() {
        return checkExternalAndFree;
    }

    public Set<String> getCheckExternalSetRCZero() {
        return checkExternalSetRCZero;
    }
    public Set<String> getCheckExternalDecreaseSite() {
        return checkExternalDecreaseSite;
    }

    public Set<String> getCheckExternalIncreaseSite() {
        return checkExternalIncreaseSite;
    }
    public Set<String> getFreeSiteSet(){
        return freeSiteSet;
    }

    public Set<String> getCheckAndAddExternalFlagSet() {
        return checkAndAddExternalFlagSet;
    }

    public void addCheckExternalAndFreeSite(String site) {
        this.checkExternalAndFree.add(site);
    }

    public void merge(GCCallsSet ret) {
        this.checkExternalDecreaseSite.addAll(ret.checkExternalDecreaseSite);
        this.checkExternalIncreaseSite.addAll(ret.checkExternalIncreaseSite);
        this.checkExternalSetRCZero.addAll(ret.checkExternalSetRCZero);
        this.freeSiteSet.addAll(ret.freeSiteSet);
    }

    public void checkAndAddExternalFlag(String name) {
        this.checkAndAddExternalFlagSet.add(name);
    }
}
