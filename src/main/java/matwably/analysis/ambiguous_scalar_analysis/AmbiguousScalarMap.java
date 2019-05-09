package matwably.analysis.ambiguous_scalar_analysis;

import ast.ASTNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AmbiguousScalarMap {
    /**
     * Map of variables to definitions, ambiguous variables will have two definitions, a scalar one and an
     * array definition.
     */
    private HashMap<String, Set<ASTNode>> scalar_map = new HashMap<>();

    private HashMap<String, Set<ASTNode>> matrix_map = new HashMap<>();

    public AmbiguousScalarMap(HashMap<String, Set<ASTNode>> scalar, HashMap<String, Set<ASTNode>> matrix){
        this.scalar_map = scalar;
        this.matrix_map = matrix;
    }
    public AmbiguousScalarMap(){
    }
    public static AmbiguousScalarMap merge(AmbiguousScalarMap first, AmbiguousScalarMap second){
        AmbiguousScalarMap scalarMap = new AmbiguousScalarMap();
        for(Map.Entry<String, Set<ASTNode>> entry: first.getScalarMap().entrySet()){
            scalarMap.addScalar(entry.getKey(),entry.getValue());
        }
        for(Map.Entry<String, Set<ASTNode>> entry: second.getScalarMap().entrySet()){
            scalarMap.addScalar(entry.getKey(),entry.getValue());
        }
        for(Map.Entry<String, Set<ASTNode>> entry: first.getScalarMap().entrySet()){
            scalarMap.addMatrix(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<String, Set<ASTNode>> entry: second.getScalarMap().entrySet()){
            scalarMap.addMatrix(entry.getKey(), entry.getValue());
        }
        return scalarMap;
    }
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof AmbiguousScalarMap)) return false;
        AmbiguousScalarMap other = (AmbiguousScalarMap) obj;
        return other.getMatrixMap().equals(this.getMatrixMap())
                && other.getScalarMap().equals(this.getScalarMap());
    }
    public AmbiguousScalarMap copy(){
        return new AmbiguousScalarMap(this.scalar_map, this.matrix_map);
    }

    public HashMap<String, Set<ASTNode>> getScalarMap(){
        return scalar_map;
    }
    public HashMap<String, Set<ASTNode>> getMatrixMap(){
        return scalar_map;
    }
    public boolean isAmbiguous(String name){
        return scalar_map.containsKey(name) && matrix_map.containsKey(name);
    }
    public void addMatrixDefinition(String name, ASTNode def){
        matrix_map.remove(name);
        scalar_map.remove(name);
        Set<ASTNode> set =  new HashSet<>();
        set.add(def);
        matrix_map.put(name,set);
    }
    public void addScalarDefinition(String name, ASTNode def){
        matrix_map.remove(name);
        scalar_map.remove(name);
        Set<ASTNode> set =  new HashSet<>();
        set.add(def);
        scalar_map.put(name,set);
    }
    private void addScalar(String name, Set<ASTNode> def){
        if(scalar_map.containsKey(name)){
            scalar_map.get(name).addAll(def);
        }else{
            scalar_map.put(name, new HashSet<>(def));
        }
    }
    private void addMatrix(String name, Set<ASTNode> def){
        if(matrix_map.containsKey(name)){
            matrix_map.get(name).addAll(def);
        }else{
            matrix_map.put(name, new HashSet<>(def));
        }
    }
}
