package matwably.analysis.memory_management.hybrid;

import ast.ASTNode;

/**
 * This class is intended as to represent an aliasing site.
 * It could be redcued to simply
 */
final public class AliasingSite {


    public int hashCode(){
        return name.hashCode();
    }
    /**
     * Name of the aliasing statement
     */
    final private String name;
    /**
     * Stmt where the aliasing happened.
     */
    final private ASTNode<? extends ASTNode> stmt;

    /**
     * Aliasing constructor site
     * @param name Name that is aliased
     * @param stmt Stmt where the aliasing was defined.
     */
    public AliasingSite(String name, ASTNode<? extends ASTNode> stmt) {
        this.name = name;
        this.stmt = stmt;
    }

    /**
     * Function written to compare aliasing sites and determine whether AliasingSites
     * @param obj AliasingSite to compare against.
     * @return whether two AliasingSites have the same variable
     */
    @Override
    public boolean equals(Object obj) {
        if( obj == this) return true;
        if(!(obj instanceof AliasingSite)) return false;
        AliasingSite that = (AliasingSite) obj;
        return name.equals(that.getName());
    }

    public String getName() {
        return name;
    }

    /**
     * To string class for debugging purposes.
     * @return String value for the class
     */
    @Override
    public String toString() {
        return "{ varname:"+name+"," +
                "alias stmt:"+stmt+"}";
    }

}
