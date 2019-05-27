package matwably.analysis.memory_management;

import java.util.*;
import java.util.stream.Collectors;

final public class DynamicSite {
    public enum Kind {
        MaybeExternal("MaybeExternal"),
        External("External"),
        Internal("Internal"),
        Global("Global");
        String name;
        Kind(String name){
            this.name = name;
        }
        public String getName() {return this.name;}
    };

    private String variableName;
    private Kind kind;

    public List<MemorySite> getStaticDefinitions()  {
        return static_definitions;
    }

    private List<MemorySite> static_definitions = null;

    public static DynamicSite newInternalSite(String variableName, MemorySite... site){
        return new DynamicSite(variableName, site);
    }

    @SafeVarargs
    public static DynamicSite newInternalSite(String variableName, List<MemorySite>... site){
        DynamicSite res =new DynamicSite();

        res.static_definitions = Arrays.stream(site).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toList());
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
        this.static_definitions = Arrays.stream(initialCount).filter(Objects::nonNull).collect(Collectors.toList());
        this.variableName = variableName;
        this.kind = Kind.Internal;
    }
    private DynamicSite(){

    }
    private  DynamicSite(String variableName, Kind kind){
        this.kind = kind;
        this.variableName = variableName;
    }
    private DynamicSite( String variableName, List<MemorySite> sites, Kind kind) {
        this.static_definitions = new ArrayList<>(sites);
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
    public boolean isGlobal() {return this.kind == Kind.Global;}
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
