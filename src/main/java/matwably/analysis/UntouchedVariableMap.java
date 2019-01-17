package matwably.analysis;

import ast.Stmt;
import natlab.toolkits.analysis.core.Def;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UntouchedVariableMap  {

    private Map<String, Set<Def>> map;
    public UntouchedVariableMap(){
        this.map = new HashMap<>();
    }

    public UntouchedVariableMap(HashMap<String, Set<Def>> result){
        this.map = result;
    }

    public boolean isDefinitionUntouched(String varName, Stmt stmt){
        Set<Def> set =this.map.get(varName);
        return (set != null) && set.contains(stmt);
    }
    private Set<String> keySet(){ return map.keySet();}

    public void put(String key, Def stmt) {
        if(map.containsKey(key)){
             map.get(key).add( stmt);
        }else{
            Set<Def> set = new HashSet<>();
            set.add(stmt);
            map.put(key, set);
        }
    }
    public void put(String key, Set<Def> stmt) {
        if(map.containsKey(key)){
            map.get(key).addAll(stmt);
        }else{
            Set<Def> set = new HashSet<>(stmt);
            map.put(key, set);
        }
    }

    public boolean containsKey(String key){
        return map.containsKey(key);
    }

    public Set<Def> get(String key){return map.get(key);}

    public void remove(String key) {
        map.remove(key);
    }

    public UntouchedVariableMap merge(UntouchedVariableMap other){
        UntouchedVariableMap that = other.copy();
        for(String var: this.keySet()){
            that.put(var,this.get(var));
        }
        return that;
    }

    public String toString() {
        return this.map.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UntouchedVariableMap)) {
            return false;
        } else {
            UntouchedVariableMap otherPtm = (UntouchedVariableMap) other;
            return this.map.equals(otherPtm.map);
        }
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    public UntouchedVariableMap copy() {
        HashMap<String, Set<Def>> result = new HashMap<>();
        for(String key: this.map.keySet()) {
            result.put(key,new HashSet<>(this.map.get(key)));
        }
        return new UntouchedVariableMap(result);
    }

}
