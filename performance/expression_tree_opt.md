# Expression tree optimization
**Problem**: Based on TameIR code, we obtain mostly a three-address code for given operations where
a lot of unnecessary local variables are created.

**Question**: Given a set of statements, how do we reconstruct expressions trees in order 
to avoid unnecessary locals?

**Goal**: Eliminate set of temporary local variables, but only when convenient.

Example:
```matlab
function f()
    a = 3
    b = 4
    c = plus(a,b)
    e = a < b
    while e
        a = 5
        e = a<b
    end
end
```
After transformation:
```matlab
function f()
    c = plus(3,4)
    e = 3 < 4
    while e
        e = 5 < b
    end
end

```
How about when we switch the statements around:
```matlab
function f()
    a = 3
    b = 4
    c = plus(3,4)
    e = a < b
    while e
        e = a < b
        a = 5
    end
end

```
If a use gives me two definitions ignore the variable in the expression. And for 
every other use of that variable, if the use respects that rule,
 and the replacement is convenient, apply replacement of that variable by expression given
 by the definition.
After transformation:
```matlab
function f()
    a = 3
    c = plus(3,4)
    e = a < 4
    while e
        e = a < 4
        a = 5
    end
end
```

A more complicated example:
```matlab
function f()
    a = ones(3,3)
    b = 3
    c = times(b,a)
end
```
After transformation:

```matlab
function f()
    c = times(ones(3,3),3)
end
```
If there is only one use of a more complicated variable, such as `a` in this example, where by complicated 
I mean calling ones(3,3), replace variable by expression and delete statement definition, in this case we delete 
variable `a`. Note that this works because `ones` is a pure function and `a` is only
used once, without ambiguity in that use. 

If a variable is defined as a literal scalar of any number type and the variable has more 
than one use, for each use, check if that is the only definition, and if it is, replace by literal scalar.

 
How about when is not convenient?
```matlab
function f()
    c = 4+ 53*4+40/4
    a = c + d
    disp(c)
end
```
Here the transformation would result in actually computing the RHS of the C definition twice.
This would be inefficient. Therefore in this case we do not eliminate C.

How about when the expression tree involves parameters?

```matlab
function f(e)
    a = 3/e
    disp(c)
end

```
Here, we do not track `e` further and leave it as it is. Unless there
is a re-definition of `e`

How about when an expression is part of a while loop?
```matlab
function f()
    a = 3;
    b = 5;
    while a <= b
        if b == -1
            disp(b)
        end
        a = a - 1
        b = a - 2
    end
end
    
```
In this case, the two uses of `b`: `b == -1` & `disp(b)` would return
two definitions. At this point we move on to the next assign statement since
its an ambiguous replacement, and elimination. 

What about this case?
```matlab
function f()
    c = randn()
    disp(c)
end
```
Since the builtin is not a pure function, we continue. Replacing here would be a 
mistake as we cannot simply eliminate c without affecting the whole program. 

What about calls to user defined functions?
```matlab
function f()
    c = user_defined()
    disp(c)
end
```
We continue along as we would need to determine a series of things for a user_defined
function to be eliminated, whether it has side-effects, whether use_defined depends
on some global variable definition (in which case, the order of operations would 
matter. i.e we would need to check that the global was not modified in between.)

#### Notes: 
- To run this analysis we need to have an idea of the uses and definitions
    for each variable. If we can determine the use and definition for a variable
    via a UDDU chain analysis. We can use that analysis to do a flow-insensitive
    analysis and eliminate locals this way. Another point to make, is that
    the analysis will be an Intraprocedural analysis as what we want to do
    is eliminate the use of locals inside functions, moreover,
     calls to user_defined functions are ignore in the analysis 
     because we would need to determine things like whether the user_defined
     function is pure, or whether it has side-effects, or whether is "simple"
     enough.
- If we have the UDDU chain we can do this by either going backwards or forwards in
    the program. i.e. by starting at uses, and going to definitions, or starting
    at definitions and going to uses.
<div style="display:none">
    One way to do this is to do a backwards analysis where we go through
        each statement, check for uses at that statement, then check for definitions
        of each use, if there is only one definition and the definition is "simple",
        we can surely eliminate the variable.
</div>

### Forward Traversal Algorithm:
**Idea**: Since we still need value analysis and value analysis depends on TameIR
for which there are only NameExpr as part of expressions. Then we 
must have extra data structures to do the analysis. (Since re-constructing the
expression tree actually goes against TameIR rules).
 For this I'm thinking of two data structures, the first data structure is a set, 
 where the members are statements to be ignored during generation, let's call it
 `S_{kill_stmt}`. The second data structure is a dictionary whose keys are NameExpr
 that represent uses, and the values are the corresponding RHS expression of 
 the definition for that NameExpr, which I'll call `M_{use,expr}`. The idea is that as
  we step through AssignStmts, we check their uses, and when <i>convenient</i>, 
  we add the AssignStmt to the set of stmts to be deleted and add a mapping 
  between "abiding uses" and the RHS of AssignStmt which is an expression. The notions
  of when is <i>convenient</i>, and an "abiding use" will be specified below.
  
Once the analysis is done, when we are generating statements, as we
traverse the TameIR tree, we check if the statement is part of `S_{kill_stmt}`,
if true, we do not generate it. On the other hand, when we hit generation
of expressions, for every variable in use that we hit in expressions, 
we check every NameExpr against the map of uses. If we find the use in the map,
we get the corresponding expr, and we generate it recursively, 
in order to find the other nested expression.

The rules on how to handle each Statement in TameIR are defined below. 


 **Start point:**
    A TameIR program. 
Since we are doing a forward analysis, we only care about AssignStatements in TameIR.

The following are `AssignStatements` in TameIR.
```
    TIRCellArraySetStmt // Ignored
    TIRDotSetStmt // Ignored
    TIRCellArrayGetStmt // Ignored
    TIRDotGetStmt // Ignored
    TIRCreateFunctionReferenceStmt // Ignored
    TIRCreateLambdaStmt // Ignored  
    TIRArraySetStmt // LHS is ignore but the RHS will be affected when appropriate
    TIRArrayGetStmt // Definitions used and a transformation to the uses of this 
                    // variables only done if they are used only once.
    TIRCallStmt // Depending on how complicated the function call is we would transform
                // and eliminate this sort of definitions. This depends on simplifications
                // done to the call, whether the call is user defined, 
                // whether the call is pure, and the parameters to the call. (Whether the 
                // parameters are themselves variables to be eliminated in the analysis)
   TIRAssignLiteralStmt // If Float/Int literal, we find all "proper" uses and replace
                    there the value instead of the definition.
                    If more complicated, we only delete statement if variable used ones.
   TIRCopyStmt // Both RHS/LHS taken care of by analysis.
   
      
```
The first 5 are ignored because `MatWably` only currently supports arrays. If we
ever have more than one target variable, we also ignore it. (Although, note here, we could
optimize if its an array literal as this would be no different than assigning to 
a scalar.)
The rest the handling is as follows:
- `TIRArraySetStmt`: Here the LHS is indexing of an array, so we cannot eliminate 
                    this statement, however, we could elimate the RHS which should
                    have actually been taken care of by the other parts of the analysis.
- `TIRArrayGetStmt`: We would check how many uses there are of the variable,
                     if there is one use, and the use has only that variable as the 
                     definition, we add the statement to the set of statements to be
                     ignored during generation, and add the key,value of the use and the
                     RHS expression of the definition, which in this case would be 
                     an array get.
- `TIRCallStmt`: In this case, the analysis depends on the type of 
                 calls and arguments to the call. We only touch calls from
                 built-ins that are pure, from there we have simple calls,
                 where the value produced is always a constant, or more complicated calls.
                 Examples are given below for each. For "complex" calls, we will check how many times a given
                 definition is being used, if used only one time and that use only has that definition,
                 we add it to our `S_{kill_stmt}` & `M_{use,expr}`. For "simple calls",
                 we go over all uses, and for the uses that only have that definition
                 as the call, we add them to `M_{use,expr}`. Finally if all the uses
                 are bound to only that definition, we kill the definition.
-  `TIRAssignLiteralStmt`: Similar to above, depending on the literal we treat it as complex or simple.
-   `TIRCopyStmt`: We ignore this case, as there are complications, if the 
                    two variables not scalars, we must keep track of ANY change made to them. Since
                    that makes the analysis complicated, we restrict ourselves in
                    optimizing copy statements to eliminate variables.
                   ```matlab
                   function ex1()
                    a = 3;
                    b = a;
                    c = b + c;
                   end
                   %becomes:
                  function ex1()
                    a = 3;
                    c = a + c;
                  end
                  %But:
                  function ex1()
                      a = 3; 
                      b = a;
                      a = 5;
                      c = b + c;
                  end
                  % Stays the same.
                  ```
- `TIRGlobalStmt`: Ignore any statements of this kind, this is ignored by
the MatWably back-end compiler. Handling this would require an interprocedural
analysis.

                  
 Example of simple calls:
 ```
    ones(), eye(), zeros(), PI(), E() etc
 ```
 Example of complicated calls:
```
    plus(3,3),minus(3,3) // Any binary/unary operation between scalar arguments
    eq,le,lt,gt,ge,ne,mod etc.
    ones(3,3), Calls which reproducing would have a bad cost or would produce a side
                effect, so either pure functions that are non-trivial to run. 
                or impure functions, such as rand()
```
## Laurie's Six Steps

1. State the problem precisely
- What is the set of variable definitions that can be easily deleted?
2. Define the domain (e.g. for liveness analysis, sets of strings representing variable names)
- `IntermediateVariableElimination`, set of variable statements definitions
 that can be safely eliminated, and a set of expression mapping these variables 
 to their definitions.
3. Is this a forward or backward analysis?
- Forward analysis.
4. Write down flow equations for different language constructs.
Let `var_defs` be a map between declarations an their rhs.
- For variable declarations of type `a = exp`
```
var_defs = var_def U {a, exp}


```
5. What’s the merge operation?
6. What’s the initial dataflow fact?
        
        
      
### Questions

- What do we do about return parameters in terms of aliases?
    - For now we can simply ignore any alias that matches a return variable
    - Later on... the analysis would have to produce a mapping between return
        value and replacing expression, or set of expressions.
    - 



