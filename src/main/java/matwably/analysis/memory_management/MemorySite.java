package matwably.analysis.memory_management;

import ast.ASTNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MemorySite {
    public ASTNode<? extends ASTNode> getDefinition() {
        return node;
    }

    /**
     * ASTNode defining the malloc site, use for debugging purposes
     */
    private final ASTNode<? extends ASTNode> node;

    public String getInitialVariableName() {
        return name;
    }

    /**
     * Variable name for initial declaration, added for debugging purposes.
     */
    private String name;
    /**
     * Keeps track of the aliasing statements for debugging
     */
    private Set<AliasingSite> aliasing_sites = new HashSet<>(); // This is use for

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


    private MemorySite(ASTNode<? extends ASTNode> node, String name, Set<AliasingSite> aliasing_sites,
                       Set<String> aliasing_names,
                       boolean staticallyFreed, int reference_count) {
        this.node = node;
        this.name = name;
        this.aliasing_sites = aliasing_sites;
        this.aliasing_names = aliasing_names;
        this.reference_count = reference_count;
        this.staticallyFreed = staticallyFreed;
    }
    public MemorySite( String varName, ASTNode<? extends ASTNode> node){
        this.node = node;
        this.name = varName;
        reference_count++;
        aliasing_sites.add(new AliasingSite(varName,node));
        aliasing_names.add(varName);
    }

    /**
     * Increases reference count
     */
    public void increaseReferenceCount(String varName, ASTNode<? extends ASTNode> node) {
        aliasing_sites.add(new AliasingSite(varName, node));
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
                    aliasing_sites.remove(new AliasingSite(name,node));
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
        return reference_count == 0 && staticallyFreed;
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
    public Set<AliasingSite> getAliasingSites() {
        return aliasing_sites;
    }
    /**
     * Getter for the aliasing sites of the memory site
     * @return Returns set of aliasing sites.
     */
    public Set<String> getAliasingNames() {
        return aliasing_names;
    }

    /**
     * Method to add AliasingSites
     * @param sites A number of alasing sites
     */
    public void addAliasingSites(AliasingSite... sites){
        aliasing_sites.addAll(Arrays.stream(sites).collect(Collectors.toSet()));
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
        MemorySite that = (MemorySite)obj;
        return that.getReferenceCount() == this.reference_count && that.getAliasingNames().equals(this.aliasing_names)
                && that.isDynamic() == this.isDynamic();
    }

    /**
     * This method copies memory sites, the only difference is that it passes the defining node by reference
     * and the AliasingSite set creates a shallow copy
     * @return returns a copy of the memory site
     */
    public MemorySite copy(){
        // Creates shallow copy of aliasing sites, passes reference to aliasing site and defining node.
        Set<AliasingSite> thatSet = new HashSet<>(this.aliasing_sites);
        Set<String> thaSet = new HashSet<>(this.aliasing_names);
        return new MemorySite(node,name, thatSet,thaSet,staticallyFreed, reference_count);
    }

    @Override
    public String toString() {
        return "Memsite:{count:"+reference_count+", defining_name:"+name+", defining_stmt"+node.getPrettyPrinted()+"aliasing_stmt: "+aliasing_sites.toString()+"}";
    }
}
