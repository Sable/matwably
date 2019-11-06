# Modified Variable analysis
We want to find the variables that are defined, and not modified at any program
point _p_. 
## Approximation 
We approximate this analysis with a set of statements at each program point.
This set of statements represents variable definitions, _s_, of the type `a = ...`,
 where `a` has not been modified between definitions _s_ and program point _p_. Note
 that we may have more than one variable defined in a particular statement, which,
 is why we have a statement map set. Each element of the set, is a map between
 the statement and the variables that are are defined and not modified in the analysis. 
## Problem definition
A variable definition _i_ defined at program point _d_ reaches given program point _p_ if in 
all paths from _d_ to _p_, the variable definition _i_ is not modified, or redefined.
## Merge Operations
Let _P1_ and _P2_ be two predecessor nodes at definingNodes _p_. We use set intersection  
to get the resulting set.
## Starting Aproximations
The __out__ set of the entry definingNodes is the empty set as we are not
interested on the parameters of the function. Every other statement _Si_ is approximated as
_out(Si)_ = {}.
## Flow Equations
- _TIRFunction_ ([] = func(a,b,c))
    - Add all a,b,c parameters. 
- _TIRCopyStmt_(a = b)
    - Kill any definition of 'a', add new statements to the 'a' variable map
- _TIRMJCopyStmt_ (a = copy(b)) 
    - Kill any definition of 'a', add new statements to the 'a' variable map
- _TIRCallStmt_ (c = call(...args))
    - Kill any definition of 'a','b', and 'c',
     add new statement to the variables
- _TIRArrayGetStmt_ (b = a(i,j,k))
    -  Eliminate any definition of 'b',
       add new statements to the variables.
- _TIRArraySetStmt_ a(i,j,k) = b
    - Eliminate a from the map, and all the stmts that are part of that map
- _TIRLiteralStmt_ a = 3, a = "a"
    -  Eliminate any definition of 'a', add new statements to the variables.
- _TIRReturn_ 
    - Do nothing!
_ _TIRBreak_
    - Do nothing!
- _TIRContinue_
    - Do nothing!
### Input Parameters
We ignore them and do not add anything here. 
The parameters cannot be eliminated as variables, therefore, 
we do not touch them. 

### Output Parameters
We are conservative and ignore output parameters as well.
### Globals
Since this requires inter-procedural knowledge, we may be conservative and
ignore any variable in the function that represents a global.
 
