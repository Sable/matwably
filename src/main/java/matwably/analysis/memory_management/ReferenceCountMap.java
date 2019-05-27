package matwably.analysis.memory_management;

import ast.ASTNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class ReferenceCountMap {
    /**
     * Map of variable names to static memory sites.
     */
    private HashMap<String, MemorySite> static_memory_sites = new HashMap<>(); //Map of names to memory sites

    /**
     * Dynamic memory sites of the program
     */
    private HashMap<String, DynamicSite> dynamic_memory_sites = new HashMap<>();

    /**
     * Set maintains for each newly dynamic definition, the site where it became dynamic.
     */
    private HashMap<ASTNode, HashMap<String, Integer>> dynamic_from_previously_static_sites = new HashMap<>();
    /**
     * Set of sites at given statement that we are making dynamic, a result of the analysis
     */
    private Set<DynamicSite> initiating_dynamic_sites = new HashSet<>();

    /**
     * Static variable to free at given statement, a result of the analysis
     */
    private HashSet<String> static_freeing_memory_sites = new HashSet<>(); //Set of memory sites to free at given stmt.

    /**
     * Set of dynamic statements to increase the count at run-time for given site
     */
    private HashSet<String> setSiteAsExternal = new HashSet<>();


    private HashSet<String> dynamicLocalIncreaseReferenceSites = new HashSet<>();


    private Set<String> dynamicCheckExternalIncreaseReferenceSites = new HashSet<>();
    private Set<String> dynamicLocalDecreaseReferenceSites = new HashSet<>();
    private Set<String> dynamicCheckExternalDecreaseReferenceSites = new HashSet<>();
    public Set<String> getCheck_external_before_freeing() {
        return check_external_before_freeing;
    }

    private Set<String> check_external_before_freeing = new HashSet<>();

    public Set<String> getDynamicCheckExternalIncreaseReferenceSites() {
        return dynamicCheckExternalIncreaseReferenceSites;
    }

    public Set<String> getDynamicLocalDecreaseReferenceSites() {
        return dynamicLocalDecreaseReferenceSites;
    }

    public Set<String> getDynamicCheckExternalDecreaseReferenceSites() {
        return dynamicCheckExternalDecreaseReferenceSites;
    }
    public void setDynamicallySiteAsExternal(String name){
        setSiteAsExternal.add(name);
    }

    public Set<String> getSetSiteAsExternal(){
        return setSiteAsExternal;
    }
    /**
     * @return Returns the dynamic increasing counts statements at given program point
     */
    public HashSet<String> getDynamicLocalIncreaseReferenceSites() {
        return dynamicLocalIncreaseReferenceSites;
    }

    /**
     * @return Returns statically freed names at given point-to
     */
    public HashSet<String> getStaticFreeingMemoryNames() {
        return static_freeing_memory_sites;
    }

    /**
     * Default constructor for PointsToInformation
     */
    ReferenceCountMap(){
    }



    private ReferenceCountMap(HashMap<String, MemorySite> static_memory_sites, HashMap<String, DynamicSite> dynamic) {
        this.static_memory_sites = static_memory_sites;
        this.dynamic_memory_sites = dynamic;
    }

    public MemorySite getStaticSite(String name){
        return static_memory_sites.get(name);
    }
    private void addNewInitiatingDynamicSite(DynamicSite dynamicSite){
        if(dynamicSite.isInternal()){

        }
        initiating_dynamic_sites.add(dynamicSite);
    }
    public void initiateDynamicSite(String newDynName){
        if(static_memory_sites.containsKey(newDynName)){
            addNewInitiatingDynamicSite(DynamicSite.newInternalSite(newDynName,
                    static_memory_sites.get(newDynName)));
        }
    }

    public boolean staticSitesContainKey(String name){
        return static_memory_sites.containsKey(name);
    }


    /**
     * @param first First PointsToInformation in the data flow graph
     * @param second Second PointsToInformation to compare against
     * @return Returns the results of comparing against
     */
    @SuppressWarnings("unchecked")
    public static ReferenceCountMap merge(ReferenceCountMap first, ReferenceCountMap second){

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
        // What about the intersection.
        for(String newDynName: newDyn){
            DynamicSite newDimSite = null;
            // First argument
            if(first.staticSitesContainKey(newDynName) && second.staticSitesContainKey(newDynName)){
                newDimSite = DynamicSite.newInternalSite(newDynName,
                        first.getStaticSite(newDynName),
                        second.getStaticSite(newDynName));
            }else if(second.staticSitesContainKey(newDynName)){
                newDimSite = DynamicSite.newInternalSite(newDynName,
                        second.getStaticSite(newDynName));
            }else{
                newDimSite = DynamicSite.newInternalSite(newDynName,
                        first.getStaticSite(newDynName));
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
        Set<String> dynamicNamesSitesMerge = new HashSet<>(first.getDynamicMemorySites().keySet());
        dynamicNamesSitesMerge.addAll(second.getDynamicMemorySites().keySet());
        for(String varName:dynamicNamesSitesMerge){
            if(first.getDynamicMemorySites().containsKey(varName)&&
                    !second.getDynamicMemorySites().containsKey(varName)){

                newDynamicSites.put(varName,first.getDynamicMemorySites().get(varName).copy() );

            }else if(!first.getDynamicMemorySites().containsKey(varName)&&
                    second.getDynamicMemorySites().containsKey(varName)){
                newDynamicSites.put(varName,second.getDynamicMemorySites().get(varName).copy() );
            }else{

                DynamicSite firstSite = first.getDynamicMemorySites().get(varName);
                DynamicSite secondSite = second.getDynamicMemorySites().get(varName);
                if(firstSite.getKind() == DynamicSite.Kind.Internal
                        && secondSite.getKind() == DynamicSite.Kind.Internal){
                        newDynamicSites.put(varName, DynamicSite.newInternalSite(varName, firstSite.getStaticDefinitions()
                        ,secondSite.getStaticDefinitions()));
                }else if(firstSite.getKind() == DynamicSite.Kind.External
                        && secondSite.getKind() == DynamicSite.Kind.External){
                    newDynamicSites.put(varName, firstSite.copy());
                }else{
                    newDynamicSites.put(varName, DynamicSite.newMaybeExternalSite(varName));
                }
            }
        }
        return new ReferenceCountMap(newMapPointInfo, newDynamicSites);
    }


    public HashMap<String, MemorySite> getStaticMemorySites() {
        return static_memory_sites;
    }

    public HashMap<String, DynamicSite> getDynamicMemorySites() {
        return dynamic_memory_sites;
    }

    /**
     * Returns the set of initiating sites at the given point, for a given initial site, we must also set the
     * initial reference count pointer.
     * @return Returns the set of initiating sites at the given point
     */
    public Set<DynamicSite> getDynamicSitesToInialize() {
        return initiating_dynamic_sites;
    }

    /**
     * We copy static and dynamic memory sites. However, we are careful to maintain the reference to the same
     * memory site for aliased sites.
     * @return Returns copy of PointsToInformation
     */
    public ReferenceCountMap copy(){
        HashMap<String, MemorySite> map = new HashMap<>();

        for(Map.Entry<String, MemorySite> entry: static_memory_sites.entrySet()){
            if(!map.containsKey(entry.getKey())){
                MemorySite tempSite = entry.getValue().copy();
                for (AliasingSite temp : tempSite.getAliasingSites()) {
                    map.put(temp.getName(), tempSite);
                }
            }
        }
        return new ReferenceCountMap(map, new HashMap<>(dynamic_memory_sites));
    }


    /**
     * This method is used by declaring statements, it creates a new MallocSite, decreases dynamic variable,
     * if dynamic, or lowers static reference count while deleting alias statically
     * @param name name of the variable that is linked initially with the declaring statement.
     * @param node Defining stmt node
     */
    public void addNewStaticSite(String name, ASTNode<? extends ASTNode> node) {
        // The set of dynamic names and the map of dynamic maps should always be mutually exclusive
        assert !(dynamic_memory_sites.containsKey(name) && static_memory_sites.containsKey(name) ):
                "Variable"+name+" is both dynamic and static! This should not be possible";
        decreaseReference(name);
        static_memory_sites.put(name, new MemorySite(name,node));
    }
    //TODO TEST the map correctlyx
    public void decreaseReference(String name){
        if(dynamic_memory_sites.containsKey(name)) {
            decreaseDynamicCount(name);
        }
        if(static_memory_sites.containsKey(name)) {
            decreaseStaticCount(name);
        }
    }
    private void decreaseStaticCount(String name){
        if(static_memory_sites.containsKey(name)) {
            boolean setFree = static_memory_sites.get(name).decreaseReferenceCount(name);
            if(setFree){
                // At this point, if there was only one alias and it was deleted,
                // the static freeing acts
                static_freeing_memory_sites.add(name);
                // Its okay to remove as there will be other keys pointing to the same MemorySite, unless
                // it was the only reference, at that point the above call to decreaseReferenceCount should have returned true
                assert static_memory_sites.get(name).getReferenceCount() == 0: "Reference count should be zero here";
            }
            static_memory_sites.remove(name);

        }
    }


    /**
     * Method to check for validity of PointsTo set.
     * @return Returns whether PointsToInformation set is a valid one
     */
    public boolean checkValidity(){
        if(this.static_memory_sites.keySet().retainAll(dynamic_memory_sites.keySet())) return false;
        for(Map.Entry<String, MemorySite> entry: this.static_memory_sites.entrySet()){
            if(entry.getValue().getReferenceCount() != entry.getValue().getAliasingSites().size()) return false;
            if(!entry.getValue().getAliasingSites().stream().allMatch(
                    (AliasingSite site)-> this.static_memory_sites.get(site.getName()) == entry.getValue()))
                return  false;
        }
        return true;
    }
    public void addExternalDynamicSite(String name){
        dynamic_memory_sites.put(name, DynamicSite.newExternalSite(name));

    }
    /**
     * This method is used by aliasing statements
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
        }
    }

    private void decreaseDynamicCount(String name){
        // if
        if(dynamic_memory_sites.containsKey(name)){
            if(dynamic_memory_sites.get(name).isInternal()){
                dynamicLocalDecreaseReferenceSites.add(name);
            }else if(dynamic_memory_sites.get(name).isMaybeExternal()){
                dynamicCheckExternalDecreaseReferenceSites.add(name);
            }
            dynamic_memory_sites.remove(name);
        }
    }

    private void increaseDynamicCount(String name) {
        // if
        if(dynamic_memory_sites.get(name).isInternal()){
            dynamicLocalDecreaseReferenceSites.add(name);
        }else if(dynamic_memory_sites.get(name).isMaybeExternal()){
            dynamicCheckExternalDecreaseReferenceSites.add(name);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nStatic Sites:\n");
        sb.append(static_memory_sites.toString());
        sb.append("\nDynamic Sites:\n");
        sb.append(dynamic_memory_sites.toString());
        sb.append("\nDynamic Sites to initialize:\n");
        sb.append(initiating_dynamic_sites.toString());
        sb.append("\nVariables to free statically:\n");
        sb.append(static_freeing_memory_sites);
        sb.append("\nLocal Dynamic variables to increase:\n");
        sb.append(dynamicLocalIncreaseReferenceSites);
        sb.append("\nLocal Dynamic variables to decrease:\n");
        sb.append(dynamicLocalDecreaseReferenceSites);
        sb.append("\nMaybe External dynamic variables to increase:\n");
        sb.append(dynamicCheckExternalIncreaseReferenceSites);
        sb.append("\nMaybe External dynamic variables to decrease:\n");
        sb.append(dynamicCheckExternalDecreaseReferenceSites);
        return sb.toString();
    }

    /**
     * This method frees all the variables that are not in the set.
     * If it is static, it adds freeing statement directly.
     * If it is dynamic:
     *  - If is external, does nothing
     *  - If is maybeExternal, checks  whether it is indeed external and dynamically frees  if it is not
     *  - IF is internal, frees immediately.
     * @param varName Set of strings of variables to maintain and not clean
     */
    public void freeRemaining(Set<String> varName) {

        // Process static
        Set<String> static_site_names = new HashSet<>(static_memory_sites.keySet());
        static_site_names.removeAll(varName);
        static_site_names.forEach(static_memory_sites::remove);
        this.static_freeing_memory_sites.addAll(static_site_names);

        // Process static
        Set<String> dynamic_site_names = new HashSet<>(dynamic_memory_sites.keySet());
        dynamic_site_names.removeAll(varName);
        dynamic_site_names.forEach((String name)->{
            DynamicSite site = dynamic_memory_sites.get(name);
            if(site.isInternal()) {
                this.static_freeing_memory_sites.add(name);
                dynamic_memory_sites.remove(name);
            }
            if(site.isMaybeExternal()) this.check_external_before_freeing.add(name);
        });
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof ReferenceCountMap)) return false;
        ReferenceCountMap otherPointsTo = (ReferenceCountMap) other;
        return this.static_memory_sites.equals(otherPointsTo.getStaticMemorySites())
                && this.dynamic_memory_sites.equals(otherPointsTo.getDynamicMemorySites());
    }


    public HashMap<ASTNode, HashMap<String, Integer>> getNewlyDynamicMap() {
        return dynamic_from_previously_static_sites;
    }
    public void addNewlyDynamic(MemorySite site){
        if(dynamic_from_previously_static_sites.containsKey(site.getDefinition())){
            dynamic_from_previously_static_sites.get(site.getDefinition()).
                    put(site.getInitialVariableName(), site.getReferenceCount());
        }else{
            HashMap<String, Integer> siteMap = new HashMap<>();
            siteMap.put(site.getInitialVariableName(), site.getReferenceCount());
            dynamic_from_previously_static_sites.put(site.getDefinition(), siteMap);
        }
    }
}
