

Need a peephole optimizer.

How to construct it?
- Create a visitor pattern on the Module ast.
- The visitor will be an abstract class call caseHandler and
    it will work in a top-down fashion, i.e. the visitor will
    go from parent to children, and will be only over 
- Create class that call the whole optimization, counting over instructions.
- Use reflection to save the peepwhole patterns.
- Create static method to call all optimization.
- Create PeepholeTransformation class
- Create Abstract Peephole pattern class
