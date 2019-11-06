package matwably.analysis.memory_management.hybrid;

import ast.ASTNode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

final public class MemorySite {
    public Set<ASTNode<? extends ASTNode>> getDefinitions() {
        return definingNodes;
    }

    /**
     * ASTNode defining the malloc site, use for debugging purposes
     */
    private final Set<ASTNode<? extends ASTNode>> definingNodes;


    private Set<ASTNode<? extends ASTNode>> latestAliasAdded;

    /**
     * Keeps track of aliasing names
     */
    private Set<String> aliasing_names = new HashSet<>(); // This is use for


    /**
     * Sets a flag to check whether the MemorySite is to be freed.
     */
    private boolean staticallyFreed = false;

    /**
     * Keeps counts of reference statements.
     */
    private int reference_count = 0;


    private MemorySite(Set<ASTNode<? extends ASTNode>> definingNodes,
                       Set<ASTNode<? extends ASTNode>> latestAliasAdded,
                       Set<String> aliasing_names,
                       boolean staticallyFreed, int reference_count) {
        this.definingNodes = definingNodes;
        this.latestAliasAdded = latestAliasAdded;
        this.aliasing_names = aliasing_names;
        this.reference_count = reference_count;
        this.staticallyFreed = staticallyFreed;
    }

    private MemorySite( String varName, ASTNode<? extends ASTNode> definingNodes){
        Objects.requireNonNull(varName, "Cannot create memory site with null variable name");
        Objects.requireNonNull(definingNodes,"Cannot create memory site with null argument TameIR definingNodes");
        this.definingNodes = new HashSet<>();
        this.definingNodes.add(definingNodes);
        this.latestAliasAdded = new HashSet<>();
        this.latestAliasAdded.add(definingNodes);
        reference_count++;
        aliasing_names.add(varName);
    }

    /**
     * Factory method for MemorySite
     * @param varName variable name
     * @param node  TamerIR definingNodes
     * @return Returns
     */
    public static MemorySite createMemorySite(String varName, ASTNode<? extends ASTNode> node){
        return  new MemorySite(varName, node);
    }

    /**
     * Increases reference count
     */
    public void increaseReferenceCount(String varName, ASTNode<? extends ASTNode> node) {
        assert !this.aliasing_names.contains(varName):"Aliased variable names cannot be the same";
        Objects.requireNonNull(varName, "Cannot alias memory site with null variable name");
        Objects.requireNonNull(node,"Cannot alias memory site with null argument TameIR definingNodes");
        this.latestAliasAdded = new HashSet<>();
        this.latestAliasAdded.add(node);
        aliasing_names.add(varName);
        reference_count++;
    }

    /**
     * Decreases the static reference count for the site
     * @param name Name of variable that has been de-aliased
     * @return Returns wheter the has reached 0 reference count
     */
    public boolean decreaseReferenceCount(String name){
        if(!staticallyFreed){
            assert aliasing_names.size() == reference_count :"Aliasing sites must always be equal in count to referenceCount" ;
            // TODO test that hash code will allow AliasingSite to be equivalent to string
            if(aliasing_names.contains(name)){
                if(reference_count > 0){
                    reference_count--;
                    aliasing_names.remove(name);
                    if(reference_count == 0) staticallyFreed = true;
                }else{
                    System.err.println("Attempting to decrease static MemorySite: "+this.toString()+" with" +
                            " an invalid alias address");
                }
            }else{
                System.err.println("Attempting to decrease static MemorySite: "+this.toString()+" with" +
                        " an invalid alias address");
            }

        }else{
           System.err.println("Attempting to decrease dynamic MemorySite: "+this.toString()+"has already beed freed.");
        }
        return staticallyFreed;
    }


    /**
     * Returns whether the site's reference site goes to zero.
     * @return Returns whether the site's reference site goes to zero.
     */
    public boolean referenceCountIsZero(){
        assert (!staticallyFreed &&reference_count == 0) || (staticallyFreed && reference_count>0)
                :"Statically freed flag is has not been set property and does not match the referenceCount";
        return aliasing_names.size() == 0;
    }

    /**
     * Gets the reference count value
     * @return Gets the reference count value
     */
    public int getReferenceCount(){
        return reference_count;
    }

    /**
     * Returns whether MemorySite is dynamic
     * @return
     */
    public boolean isDynamic(){
        return staticallyFreed;
    }

    /**
     * Getter for the aliasing sites of the memory site
     * @return Returns set of aliasing sites.
     */
    public Set<String> getAliasingNames() {
        return aliasing_names;
    }


    /**
     * Equals implementation. A MemorySite is considered equal if:
     *  1. Is the same object
     *  2. Has MemorySite class && the reference count is equal && the set of aliasing statements are also equal.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(!(obj instanceof MemorySite)) return false;
        MemorySite that = (MemorySite) obj;
        return that.getReferenceCount() == this.reference_count && that.getAliasingNames().equals(this.aliasing_names)
                && that.isDynamic() == this.isDynamic();
    }

    /**
     * This method copies memory sites, the only difference is that it passes the defining definingNodes by reference
     * and the AliasingSite set creates a shallow copy
     * @return returns a copy of the memory site
     */
    public MemorySite copy(){
        // Creates shallow copy of aliasing sites, passes reference to aliasing site and defining definingNodes.
        Set<String> thaSet = new HashSet<>(this.aliasing_names);
        return new MemorySite(new HashSet<>(definingNodes),latestAliasAdded,
                thaSet,staticallyFreed, reference_count);
    }

    @Override
    public String toString() {

        String latestNodes = latestAliasAdded.stream().
                map(ASTNode::getPrettyPrinted).
                reduce("",(acc,strNode)-> acc + strNode);
        String definingNodes = this.definingNodes.stream().
                map(ASTNode::getPrettyPrinted).
                reduce("", (acc,strNode)->acc+", "+strNode);
        return "Memsite:{count:"+reference_count+
                ", defining_stmts: "+ definingNodes+"aliasing_names: "+
                aliasing_names.toString()+
                ", lastAliasedStmts: "+latestNodes+"}";
    }

    public Set<ASTNode<? extends ASTNode>> getLatestAliasAdded() {
        return latestAliasAdded;
    }

    public MemorySite mergeEqualSites(MemorySite staticSite) {
        assert staticSite.equals(this):
                "Error: Cannot merge unequal static sites";
        Set<ASTNode<? extends ASTNode>> definingStmtSets
                = new HashSet<>(staticSite.getDefinitions());
        definingStmtSets.addAll(this.getDefinitions());
        Set<ASTNode<? extends ASTNode>> lastestAliasStmts
                = new HashSet<>(staticSite.getLatestAliasAdded());
        lastestAliasStmts.addAll(this.getLatestAliasAdded());
        return new MemorySite(definingStmtSets,lastestAliasStmts,
                new HashSet<>(aliasing_names),
                staticallyFreed, reference_count);
    }
}
