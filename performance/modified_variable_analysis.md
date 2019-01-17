# Modified Variable analysis
We want to find the variables that are defined, and not modified at any program
point _p_. 
## Approximation 
We approximate this analysis with a set of statement maps at each program point.
This set of statement maps represents variable definitions stmt, _s_, of the type `a = ...`,
 where `a` has not been modified between definition _s_ and program point _p_. Note
 that we may have more than one variable defined in a particular statement, which,
 is why we have a statement map set. Each element of the set, is a map between
 the statement and the variables that are are defined and not modified in the analysis. 
## Problem definition
A variable _i_ defined at program point _d_ reaches given program point _p_ if in 
all paths from _d_ to _p_ variable _i_ is not modified, or redefined.
## Merge Operations
Let _P1_ and _P2_ be two predecessor nodes at node _p_. We use union intersection  
operation to add statements with the same modification.
## Starting Aproximations
The __out__ set of the entry node is the empty set as we are not
interested on the parameters of the function. Every other statement _Si_ is approximated as
_out(Si)_ = {}.
## Flow Equations
- _TIRFunction_ ([] = func(a,b,c))
    - Add all a,b,c parameters.
    
- _TIRCopyStmt_(a = b)
    - Eliminate any definition of 'a', add new statements to the 'a' variable map
- _TIRMJCopyStmt_ (a = copy(b)) 
    - Eliminate any definition of 'a', add new statements to the 'a' variable map
- _TIRCallStmt_ (a, b, c = call(...args))
    - Eliminate any definition of 'a','b', and 'c',
     add new statements to the the variables
- _TIRArrayGetStmt_ (b = a(i,j,k))
    -  Eliminate any definition of 'b',
       add new statements to the variables.
- _TIRArraySetStmt_ a(i,j,k) = b
    - Eliminate a from the map, and all the stmts that are part of that map
- _TIRLiteralStmt_ a = 3, a = "a"
    -  Eliminate any definition of 'a', add new statements to the variables.
- _TIRReturn_ 
    - Do nothing! But in the other analysis keep map of aliases.
_ _TIRBreak_
    - Do nothing!
- _TIRContinue_
    - Do nothing!
### Input Parameters
We ignore them and do not add anything here. 
The parameters cannot be eliminated as variables, therefore, 
we do not touch them. 

### Output Parameters
During this analysis we collect them as if they were any other variable definitions.

 
