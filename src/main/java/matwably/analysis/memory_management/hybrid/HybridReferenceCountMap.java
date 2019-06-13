package matwably.analysis.memory_management.hybrid;

import ast.ASTNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the point-to information for garbage collection purposes sites in the Matlab program
 * There are two main sets, which are for obvious reasons, mutually exclusive from each other:
 *  - static_memory_sites
 *  - dynamic_memory_sites
 * The first one is a map, since we need to statically increase of decrease the count of a given variable where
 * we know the count statically, we allow quick access via a map data structure. The only tricky aspect of this
 * is to make sure that aliased variables point to the exact same memory site. The other tricky aspect is to make
 * sure the sets remain mutually exclusive.
 */
public class HybridReferenceCountMap {
    /**
     * Map of variable names to static memory sites.
     */
    private HashMap<String, MemorySite> static_memory_sites = new HashMap<>(); //Map of names to memory sites

    /**
     * Dynamic memory sites of the program
     */
    private HashMap<String, DynamicSite> dynamic_memory_sites = new HashMap<>();



    private Set<String> dynamicInternalSetSiteAsExternal = new HashSet<>();
    private Set<String> dynamicInternalIncreaseReferenceSites = new HashSet<>();
    private Set<String> dynamicInternalDecreaseReferenceSites = new HashSet<>();



    private Set<String> dynamicInternalSetReturnFlagAndRCToZero = new HashSet<>();

    private Set<String> dynamicInternalSetRCToZero = new HashSet<>();
    private Set<String> dynamicInternalCheckReturnFlagToFreeSites = new HashSet<>();
    private Set<String> dynamicInternalFreeMemorySite = new HashSet<>();
    /**
     * Set of sites at given statement that we are making dynamic, a result of the analysis
     */
    private Set<String> dynamicCheckExternalToSetSiteAsExternal = new HashSet<>();
    private Set<String> dynamicCheckExternalToIncreaseReferenceSites = new HashSet<>();
    private Set<String> dynamicCheckExternalToDecreaseReferenceSites = new HashSet<>();
    private Set<String> dynamicCheckExternalSetReturnFlagAndRCToZero = new HashSet<>();
    private Set<String> dynamicCheckExternalAndReturnFlagToFreeSites = new HashSet<>();

    /**
     * Default constructor for PointsToInformation
     */
    HybridReferenceCountMap(){
    }

    private HybridReferenceCountMap(HashMap<String, MemorySite> static_memory_sites, HashMap<String, DynamicSite> dynamic) {
        this.static_memory_sites = static_memory_sites;
        this.dynamic_memory_sites = dynamic;
    }


    /**
     * Consider sets a_static (a_s), b_static (b_s), a_dynamic (a_d), b_dynamic (b_d)
     * @param first First PointsToInformation in the data flow graph
     * @param second Second PointsToInformation to compare against
     * @return Returns the results of comparing against
     */
    public static HybridReferenceCountMap merge(HybridReferenceCountMap first, HybridReferenceCountMap second){

        HashMap<String, MemorySite> newMapPointInfo = new HashMap<>();
        Set<String> namesFirst = first.getStaticMemorySites().keySet();
        Set<String> namesSecond = second.getStaticMemorySites().keySet();
        // Get the insersection between the two PointsTo static sites.
        Set<String> intersection = new HashSet<>(namesFirst);
        intersection.retainAll(namesSecond);

        // The new dynamic set is the compliment of the intersection.
        Set<String> newDyn = new HashSet<>();
        newDyn.addAll(namesFirst);
        newDyn.addAll(namesSecond);
        newDyn.removeAll(intersection);

        // Now in terms of the intersection we still need to check that all these variable point to the same
        // memory site in both sets. If not equal we add them to the dynamic set.
        // Otherwise, we map the variable with the StaticSite and maintain the static information.
        for (String variable : intersection){
            // If memory sites are not equal,
            if(!first.getStaticSite(variable).equals(second.getStaticSite(variable))) {

                newDyn.addAll(first.getStaticSite(variable).getAliasingNames());
                newDyn.addAll(second.getStaticSite(variable).getAliasingNames());
            }else{
                // If they are equal the MallocSite remains known statically
                newMapPointInfo.put(variable,first.getStaticSite(variable));
            }
        }
        HashMap<String, DynamicSite> newDynamicSites = new HashMap<>();
        // For the previously static sites that have become dynamic, add them to the new dynamic map.
        for(String newDynName: newDyn){
            DynamicSite newDimSite;
            // First argument
            if(first.staticSitesContainKey(newDynName) && second.staticSitesContainKey(newDynName)){
                    newDimSite = DynamicSite.newInternalSite(newDynName,
                            first.getStaticSite(newDynName),
                            second.getStaticSite(newDynName));
            }else if(second.staticSitesContainKey(newDynName)){
                newDimSite = DynamicSite.newInternalSite(newDynName,
                        second.getStaticSite(newDynName));
                // Checks if there is already a dynamic site for that variable.
                // if there isn't the variable is not defined yet, merges the two DynSites
                if(first.getDynamicMemorySites().containsKey(newDynName)){
                    newDimSite = newDimSite.merge(first.getDynamicMemorySites().
                            get(newDynName));
                }
            }else{
                newDimSite = DynamicSite.newInternalSite(newDynName,
                        first.getStaticSite(newDynName));
                // Checks if there is already a dynamic site for that variable.
                // if there isn't the variable is not defined yet, merges the two DynSites
                if(second.getDynamicMemorySites().containsKey(newDynName)){
                    newDimSite = newDimSite.merge(second.getDynamicMemorySites().
                            get(newDynName));
                }
            }
            // Add to current map.
            // Node -> Set<String> variables to initialize as dynamic.

            newDynamicSites.put(newDynName, newDimSite);
        }
        // Now add all the dynamic ones from both ReferenceMaps,
        // two PointToInformation objects
        // Since they are dynamic, we have to merge Kind of dynamic site
        // If they have the same type, it does not matter which site it may be, if they do not have the same kind,
        // We add a new DynamicSite as

        // The only ones missing here are the sites dynamic in both sets
        Set<String> dynamicNamesSitesMerge = new HashSet<>(first.getDynamicMemorySites().keySet());
        dynamicNamesSitesMerge.addAll(second.getDynamicMemorySites().keySet());
        for(String varName:dynamicNamesSitesMerge){
            if(!newDynamicSites.containsKey(varName)){
                if(first.getDynamicMemorySites().containsKey(varName)&&
                        !second.getDynamicMemorySites().containsKey(varName)){
                    newDynamicSites.put(varName,first.getDynamicMemorySites().
                            get(varName).copy());

                }else if(!first.getDynamicMemorySites().containsKey(varName)&&
                        second.getDynamicMemorySites().containsKey(varName)){
                    newDynamicSites.put(varName,second.getDynamicMemorySites().
                            get(varName).copy() );
                }else{
                    DynamicSite firstSite = first.getDynamicMemorySites().get(varName);
                    DynamicSite secondSite = second.getDynamicMemorySites().get(varName);
                    newDynamicSites.put(varName, firstSite.merge(secondSite));
                }
            }
        }
        return new HybridReferenceCountMap(newMapPointInfo, newDynamicSites);
    }


    /**
     * We copy static and dynamic memory sites. However, we are careful to maintain the reference to the same
     * memory site for aliased sites.
     * @return Returns copy of PointsToInformation
     */
    public HybridReferenceCountMap copy(){
        HashMap<String, MemorySite> map = new HashMap<>();

        for(Map.Entry<String, MemorySite> entry: static_memory_sites.entrySet()){
            if(!map.containsKey(entry.getKey())){
                MemorySite tempSite = entry.getValue().copy();
                for (AliasingSite temp : tempSite.getAliasingSites()) {
                    map.put(temp.getName(), tempSite);
                }
            }
        }
        return new HybridReferenceCountMap(map, new HashMap<>(dynamic_memory_sites));
    }

    /**
     * Adds a new external dynamic site
     * @param name Name of external site
     */
    public void addExternalDynamicSite(String name){
        dynamic_memory_sites.put(name, DynamicSite.newExternalSite(name));
    }

    /**
     * This method is used by declaring statements, it creates a new MallocSite, decreases dynamic variable,
     * if dynamic, or lowers static reference count while deleting alias statically
     * @param name name of the variable that is linked initially with the declaring statement.
     * @param node Defining stmt node
     */
    public void addNewStaticSite(String name, ASTNode<? extends ASTNode> node) {
        assert checkValidity():"Not a valid ReferenceMap"+this.toString();
        // The set of dynamic names and the map of dynamic maps should always be mutually exclusive
        assert !(dynamic_memory_sites.containsKey(name) && static_memory_sites.containsKey(name) ):
                "Variable"+name+" is both dynamic and static! This should not be possible";
        static_memory_sites.put(name, MemorySite.createMemorySite(name, node));
    }

    public void decreaseReference(String name){
        assert checkValidity():"Not a valid ReferenceMap"+this.toString();
        if(dynamic_memory_sites.containsKey(name)) {
            decreaseDynamicCount(name);
        }
        if(static_memory_sites.containsKey(name)) {
            decreaseStaticCount(name);
        }
    }
    private void decreaseStaticCount(String name){
        assert checkValidity():"Not a valid ReferenceMap"+this.toString();
        if(static_memory_sites.containsKey(name)) {
            boolean setFree = static_memory_sites.get(name).decreaseReferenceCount(name);
            if(setFree){
                // At this point, if there was only one alias and it was deleted,
                // the static freeing acts
                dynamicInternalFreeMemorySite.add(name);
                // Its okay to remove as there will be other keys pointing to the same MemorySite, unless
                // it was the only reference, at that point the above call to decreaseReferenceCount should have returned true
                assert static_memory_sites.get(name).getReferenceCount() == 0: "Reference count should be zero here";
            }
            static_memory_sites.remove(name);
        }
    }
    /**
     * This method is used by aliasing statements.
     * When we have a copy statements and the dynamic sites are aliased.
     * Keeping track of dynamic aliases makes no difference in terms of run-times.
     * There is only an advantage when returning, but to support this, we would
     * need to define a merge operation even for dynamic site aliases. Basically,
     * In this merge operation, if the aliases differ, we would convert all the variables
     * involved in separate dynamic sites. But for now, we will ignore that little optimization,
     * and simply convert them all to different sites right away.
     * @param src Source of which points to the aliasing site
     * @param dest New variable name which will point to the src
     * @param node Copy stmt node.
     */
    public void writeReference(String src, String dest, ASTNode<? extends ASTNode> node){
        // The set of dynamic names and the map of dynamic maps should always be mutually exclusive
        assert !(dynamic_memory_sites.containsKey(src) && static_memory_sites.containsKey(src) ):
                "Variable"+src+" is both dynamic and static! This should not be possible";

        if(static_memory_sites.containsKey(dest)){
            // decrease
            decreaseStaticCount(dest);
        }else if(dynamic_memory_sites.containsKey(dest)){
            decreaseDynamicCount(dest);
        }

        // Increase src count,
        if(static_memory_sites.containsKey(src)){
            // Increase the reference and add ref to the set of aliasing stmts.
            static_memory_sites.get(src).increaseReferenceCount(dest, node);
            static_memory_sites.put(dest, static_memory_sites.get(src));
            // Map it to the source object
        }else if(dynamic_memory_sites.containsKey(src)){
            increaseDynamicCount(src);
            dynamic_memory_sites.put(dest,
                    DynamicSite.newSite(dest,
                            dynamic_memory_sites.get(src).getKind()));
        }
    }

    private void decreaseDynamicCount(String name){
        // if
        if(dynamic_memory_sites.containsKey(name)){
            if(dynamic_memory_sites.get(name).isInternal()){
                dynamicInternalDecreaseReferenceSites.add(name);
            }else if(dynamic_memory_sites.get(name).isMaybeExternal()){
                dynamicCheckExternalToDecreaseReferenceSites.add(name);
            }
            dynamic_memory_sites.remove(name);
        }
    }

    private void increaseDynamicCount(String name) {
        // if
        if(dynamic_memory_sites.get(name).isInternal()){
            dynamicInternalDecreaseReferenceSites.add(name);
        }else if(dynamic_memory_sites.get(name).isMaybeExternal()){
            dynamicCheckExternalToDecreaseReferenceSites.add(name);
        }
    }

    public HashMap<String, MemorySite> getStaticMemorySites() {
        return static_memory_sites;
    }

    public HashMap<String, DynamicSite> getDynamicMemorySites() {
        return dynamic_memory_sites;
    }


    public MemorySite getStaticSite(String name){
        return static_memory_sites.get(name);
    }
    public boolean dynamicSitesContainKey(String name){
        return dynamic_memory_sites.containsKey(name);
    }

    public boolean staticSitesContainKey(String name){
        return static_memory_sites.containsKey(name);
    }


    /**
     * Method to check for validity of PointsTo set.
     * @return Returns whether PointsToInformation set is a valid one
     */
    private boolean checkValidity(){
        if(!Collections.disjoint(this.static_memory_sites.keySet(),
                dynamic_memory_sites.keySet())) return false;
        for(Map.Entry<String, MemorySite> entry: this.static_memory_sites.entrySet()){
            if(entry.getValue().getReferenceCount() !=
                    entry.getValue().getAliasingSites().size()) return false;
            if(!entry.getValue().getAliasingSites().stream().allMatch(
                    (site)-> this.static_memory_sites.
                            get(site.getName()) == entry.getValue()))
                return  false;
        }
        return true;
    }




    @Override
    public String toString() {
        //        sb.append("\nDynamic Sites to initialize:\n");
//        sb.append("\nVariables to free statically:\n");
//        sb.append(dynamicInternalFreeMemorySite);
//        sb.append("\nLocal Dynamic variables to increase:\n");
//        sb.append(dynamicInternalIncreaseReferenceSites);
//        sb.append("\nLocal Dynamic variables to decrease:\n");
//        sb.append(dynamicInternalDecreaseReferenceSites);
//        sb.append("\nMaybe External dynamic variables to increase:\n");
//        sb.append(dynamicCheckExternalToIncreaseReferenceSites);
//        sb.append("\nMaybe External dynamic variables to decrease:\n");
//        sb.append(dynamicCheckExternalToDecreaseReferenceSites);
        return "\nStatic Sites:\n" +
                static_memory_sites.toString() +
                "\nDynamic Sites:\n" +
                dynamic_memory_sites.toString();
    }



    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof HybridReferenceCountMap)) return false;
        HybridReferenceCountMap otherPointsTo = (HybridReferenceCountMap) other;
        return this.static_memory_sites.equals(otherPointsTo.getStaticMemorySites())
                && this.dynamic_memory_sites.equals(otherPointsTo.getDynamicMemorySites());
    }




    public Set<DynamicSite> getInitiatedDynamicSites() {
        return dynamic_memory_sites.values().stream().
                filter(DynamicSite::hasStaticDefinitions).
                collect(Collectors.toSet());
    }

    public void addInternalFreeMemorySite(Set<String> free_static_sites_set) {
        this.dynamicInternalFreeMemorySite.addAll(free_static_sites_set);
    }

    public Set<String> getDynamicInternalCheckReturnFlagToFreeSites() {
        return dynamicInternalCheckReturnFlagToFreeSites;
    }
    public Set<String> getDynamicInternalSetRCToZero(){
        return dynamicInternalSetRCToZero;
    }

    public void addDynamicInternalSetReturnFlagAndRCToZero(String name){
        dynamicInternalSetReturnFlagAndRCToZero.add(name);
    }
    public void addDynamicInternalSetRCToZero(String name) {
        dynamicInternalSetRCToZero.add(name);

    }
    public void addDynamicCheckExternalSetReturnFlagAndRCToZero(String name){
        dynamicCheckExternalSetReturnFlagAndRCToZero.add(name);
    }
    public void addDynamicInternalCheckReturnFlagToFreeSites(String name){
        dynamicInternalCheckReturnFlagToFreeSites.add(name);
    }

    public void addDynamicCheckExternalAndReturnFlagToFreeSites(String name){
        dynamicCheckExternalAndReturnFlagToFreeSites.add(name);
    }

    public Set<String> getDynamicCheckExternalSetReturnFlagAndRCToZero() {
        return dynamicCheckExternalSetReturnFlagAndRCToZero;
    }
    public Set<String> getDynamicCheckExternalToSetSiteAsExternal() {
        return dynamicCheckExternalToSetSiteAsExternal;
    }

    public Set<String> getDynamicInternalSetSiteAsExternal() {
        return dynamicInternalSetSiteAsExternal;
    }

    public Set<String> getDynamicCheckExternalAndReturnFlagToFreeSites() {
        return dynamicCheckExternalAndReturnFlagToFreeSites;
    }

    public Set<String> getDynamicCheckExternalToIncreaseReferenceSites() {
        return dynamicCheckExternalToIncreaseReferenceSites;
    }

    public Set<String> getDynamicInternalDecreaseReferenceSites() {
        return dynamicInternalDecreaseReferenceSites;
    }

    public Set<String> getDynamicCheckExternalToDecreaseReferenceSites() {
        return dynamicCheckExternalToDecreaseReferenceSites;
    }

    public void addDynamicInternalSetSiteAsExternal(String name){
        dynamicInternalSetSiteAsExternal.add(name);
    }
    public void addDynamicCheckExternalToSetSiteAsExternal(String name){
        dynamicCheckExternalToSetSiteAsExternal.add(name);
    }

    public Set<String> getDynamicInternalFreeMemorySite() {
        return dynamicInternalFreeMemorySite;
    }
    public Set<String> getDynamicInternalSetReturnFlagAndRCToZero() {
        return dynamicInternalSetReturnFlagAndRCToZero;
    }

    /**
     * @return Returns the dynamic increasing counts statements at given program point
     */
    public Set<String> getDynamicInternalIncreaseReferenceSites() {
        return dynamicInternalIncreaseReferenceSites;
    }
}
