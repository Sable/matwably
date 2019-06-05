package matwably.analysis.memory_management.hybrid;

import java.util.*;
import java.util.stream.Collectors;

final public class DynamicSite {
    /**
     * Merges another Dynamic site into this one
     * @param dynamicSite DynamicSite to merge
     */
    public DynamicSite merge(DynamicSite dynamicSite) {
        assert dynamicSite.getVariableName().equals(this.variableName)
                :"Error: To merge variable sites, they must have the same variable name";
        DynamicSite newSite = new DynamicSite();
        // Set the name
        newSite.variableName = this.getVariableName();

        // Intersect the kind
        if(this.kind == dynamicSite.kind){
            newSite.kind = this.kind;
        }else if( (this.kind == Kind.Internal && dynamicSite.kind == Kind.External)
                || (dynamicSite.kind == Kind.Internal && this.kind == Kind.External)
                || dynamicSite.kind == Kind.MaybeExternal || this.kind == Kind.MaybeExternal){
            newSite.kind = Kind.MaybeExternal;
        }
        // Add the static sites for both.
        newSite.static_definitions = new HashSet<>(this.static_definitions);
        newSite.static_definitions.addAll(dynamicSite.getStaticDefinitions());
        return newSite;
    }

    public enum Kind {
        MaybeExternal("MaybeExternal"),
        External("External"),
        Internal("Internal");
        String name;
        Kind(String name){
            this.name = name;
        }
        public String getName() {return this.name;}
    };

    private String variableName;
    private Kind kind;

    private Set<MemorySite> static_definitions = new HashSet<>();

    public Set<MemorySite> getStaticDefinitions()  {
        return static_definitions;
    }
    public boolean hasStaticDefinitions(){
        return static_definitions.size()> 0;
    }

    public static DynamicSite newInternalSite(String variableName, MemorySite... site){
        return new DynamicSite(variableName, site);
    }

    /**
     * Factory method for generic new sites dynamic sites
     * @param name Name of variable
     * @param kind Kind of dynamic variable.
     * @return Returns a new instance of the dynamic site.
     */
    public static DynamicSite newSite(String name, DynamicSite.Kind kind){
        DynamicSite other = new DynamicSite();
        other.kind = kind;
        other.variableName = name;
        return other;
    }

    @SafeVarargs
    public static DynamicSite newInternalSite(String variableName, Set<MemorySite>... site){
        DynamicSite res =new DynamicSite();

        res.static_definitions = Arrays.stream(site).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toSet());
        res.variableName = variableName;
        return res;
    }
//    public static DynamicSite newInternalSite(String variableName){
//        return new DynamicSite(variableName, -1);
//    }
    public DynamicSite copy(){
        return new DynamicSite(this.variableName, this.static_definitions, this.kind);
    }
    public static DynamicSite newMaybeExternalSite(String variableName){
        return new DynamicSite(variableName ,Kind.MaybeExternal);
    }
    public static DynamicSite newExternalSite(String variableName){
        return new DynamicSite(variableName,Kind.External);
    }
    private DynamicSite(String variableName, MemorySite[] initialCount) {
        this.static_definitions = Arrays.stream(initialCount).filter(Objects::nonNull).collect(Collectors.toSet());
        this.variableName = variableName;
        this.kind = Kind.Internal;
    }
    private DynamicSite(){

    }
    private  DynamicSite(String variableName, Kind kind){
        this.kind = kind;
        this.variableName = variableName;
    }
    private DynamicSite( String variableName, Set<MemorySite> sites, Kind kind) {
        this.static_definitions = new HashSet<>(sites);
        this.variableName = variableName;
        this.kind = kind;
    }


    /**
     * Getter for the DynamicSite kind
     * @return Returns the kind of dynamic site
     */
    public Kind getKind() {
        return kind;
    }



    @Override
    public int hashCode() {
        return variableName.hashCode()+kind.getName().hashCode();
    }
    public boolean isExternal() {
        return this.kind == Kind.External;
    }
    public boolean isInternal() {
        return this.kind == Kind.Internal;
    }
    public boolean isMaybeExternal() {
        return this.kind == Kind.MaybeExternal;
    }

    public String getVariableName() {
        return variableName;
    }


    public boolean equals(Object obj) {
        return this == obj || (( obj instanceof DynamicSite) &&
                ((DynamicSite)obj).variableName.equals(this.getVariableName())) &&
                this.kind == ((DynamicSite)obj).getKind();
    }
    @Override
    public String toString() {
        return "{ Varname:"+variableName+"," +
                "Kind: "+ kind.getName() +","+
                "StaticDefinitions:"+static_definitions+"}";
    }


}
