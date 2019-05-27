package matwably.analysis.memory_management;

import java.util.HashSet;
import java.util.Set;

public class GCCallsSet {

    private Set<String> decreaseReferenceSet = new HashSet<>();
    private Set<String> increaseReferenceSet = new HashSet<>();
    private Set<String> setToOneDynamicallySet = new HashSet<>();
    private Set<String> freeSiteSet = new HashSet<>();


    private Set<String> checkExternalAndFree = new HashSet<>();


    private GCCallsSet(Set<String> decreaseReferenceSet, Set<String> increaseReferenceSet, Set<String> setToOneDynamicallySet, Set<String> freeSiteSet) {
        this.decreaseReferenceSet = decreaseReferenceSet;
        this.increaseReferenceSet = increaseReferenceSet;
        this.setToOneDynamicallySet = setToOneDynamicallySet;
        this.freeSiteSet = freeSiteSet;
    }
    private GCCallsSet(GCCallsSet gcCall){
        this.decreaseReferenceSet = new HashSet<>(gcCall.getDecreaseReferenceSet());
        this.increaseReferenceSet = new HashSet<>(gcCall.getIncreaseReferenceSet());
        this.setToOneDynamicallySet = new HashSet<>(gcCall.getSetToOneDynamicallySet());
        this.freeSiteSet= new HashSet<>(gcCall.getFreeSiteSet());
    }
    GCCallsSet(){

    }
    public void increaseReference(String site){
        increaseReferenceSet.add(site);
    }
    public void decreaseReference(String site){
        decreaseReferenceSet.add(site);
    }

    public void setRCToOne(String site){
        setToOneDynamicallySet.add(site);
    }

    public void setRCToZero(String site){
        freeSiteSet.add(site);
    }

    /**
     * Getter for external sites
     * @return
     */
    public Set<String> getCheckExternalAndFree() {
        return checkExternalAndFree;
    }

    public Set<String> getSetToOneDynamicallySet() {
        return setToOneDynamicallySet;
    }
    public Set<String> getDecreaseReferenceSet() {
        return decreaseReferenceSet;
    }

    public Set<String> getIncreaseReferenceSet() {
        return increaseReferenceSet;
    }
    public Set<String> getFreeSiteSet(){
        return freeSiteSet;
    }

    public void addCheckExternalAndFreeSite(String site) {
        this.checkExternalAndFree.add(site);
    }

    public void merge(GCCallsSet ret) {
        this.decreaseReferenceSet.addAll(ret.decreaseReferenceSet);
        this.increaseReferenceSet.addAll(ret.increaseReferenceSet);
        this.setToOneDynamicallySet.addAll(ret.setToOneDynamicallySet);
        this.freeSiteSet.addAll(ret.freeSiteSet);
    }
}
