package matwably.analysis.intermediate_variable;

import ast.Name;
import ast.Stmt;
import natlab.toolkits.analysis.core.Def;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UseDefDefUseMap {
    /**
     * Definitions map, gives the definitions at every program point
     */
    private Map<String, Set<Def>> defs = new HashMap<>();
    /**
     * Defines map of definitions to uses definitions.
     */
    private Map<Name, Set<Name>> defUse = new HashMap<>();

    private Map<Name, Set<Def>> useDef = new HashMap<>();


    private Set<String> globals = new HashSet<>();

    public UseDefDefUseMap(){}

    public UseDefDefUseMap(HashMap<String, Set<Def>> result, Map<Name, Set<Name>> array_uses){
        this.defs = result;
        this.defUse = array_uses;
    }
    public boolean isArrayVariableUsed(Name varName){
        return defUse.get(varName)!=null && defUse.get(varName).size() > 0;
    }
    public boolean isArrayDefinitionUntouched(String varName, Stmt stmt){
        Set<Def> set =this.defs.get(varName);
        return (set != null) && set.contains((Def) stmt);
    }
    private Set<String> keySet(){ return defs.keySet();}

    public void addDef(String key, Def stmt) {
        if(defs.containsKey(key)){
             defs.get(key).add( stmt);
        }else{
            Set<Def> set = new HashSet<>();
            set.add(stmt);
            defs.put(key, set);
        }
    }
    public void addGlobalDef(String name) {
        globals.add(name);
    }
    public void addDefs(String key, Set<Def> stmt) {
        if(defs.containsKey(key)){
            defs.get(key).addAll(stmt);
        }else{
            Set<Def> set = new HashSet<>(stmt);
            defs.put(key, set);
        }
    }

    public boolean containsKey(String key){
        return defs.containsKey(key);
    }

    public Set<Def> get(String key){return defs.get(key);}

    public void remove(String key) {
        defs.remove(key);
    }

    public UseDefDefUseMap merge(UseDefDefUseMap other){
        UseDefDefUseMap that = other.copy();
        for(String var: this.keySet()){
            that.addDefs(var,this.get(var));
        }
        for(Name name: this.defUse.keySet()){
            that.mapDefUses(name, this.defUse.get(name));
        }
        that.globals = new HashSet<>(this.globals);
        return that;
    }

    public String toString() {
        return this.defs.toString();
    }

    @Override
    public boolean equals(Object other) {
        if( this == other) return true;
        if (!(other instanceof UseDefDefUseMap)) {
            return false;
        } else {
            UseDefDefUseMap otherPtm = (UseDefDefUseMap) other;
            return this.defs.equals(otherPtm.defs) && this.defUse.equals(otherPtm.defUse)
                    && this.globals.equals(otherPtm.globals);
        }
    }

    public void mapDefUse(Name def, Name use){
        if(!defUse.containsKey(def)){
            defUse.put(def, new HashSet<>());
        }
        defUse.get(def).add(use);
    }

    public void mapDefUses(Name def, Set<Name> uses){
        if(!defUse.containsKey(def)){
            defUse.put(def, new HashSet<>());
        }
        defUse.get(def).addAll(uses);
    }

    @Override
    public int hashCode() {
        return this.defs.hashCode();
    }

    public UseDefDefUseMap copy() {
        HashMap<String, Set<Def>> result = new HashMap<>();
        for(String key: this.defs.keySet()) {
            result.put(key,new HashSet<>(this.defs.get(key)));
        }
        Map<Name, Set<Name>> uses_map = new HashMap<>();
        for(Map.Entry<Name, Set<Name>> entry: defUse.entrySet() ){
            uses_map.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return new UseDefDefUseMap(result, uses_map);
    }

    public boolean containsGlobal(String name) {
        return globals.contains(name);
    }



    public void mapUseDef(Name use, Set<Def> defs) {
        if(!useDef.containsKey(use)){
            useDef.put(use, new HashSet<>());
        }
        useDef.get(use).addAll(defs);
    }
}
