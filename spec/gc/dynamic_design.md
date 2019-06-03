# Dynamic Memory Allocation

Matlab is a managed language in terms of its memory. WebAssembly, on the other hand,
comes without native garbage collection. MatWably, a compiler from Matlab to WebAssembly,
must provide a garbage collection system to collect the Matlab memory in WebAssembly.
Providing a full-fledge garbage collection system is an difficult task that requires 
very careful consideration. Reference Counting is a garbage collection method, that keeps
track of objects in the heap through a run-time counter. This counter represents
the number of variables in the program that point to the particular site in memory.
In this document, we will stipulate the specification for completely 
dynamic memory management Matwably. This system should serve as baseline to 
 compare against for the other garbage collection mechanisms.
 
# Analysis
We are restricted to _gc_ collection in terms of an intra-procedural analysis.
This means EVERY variable jumping between bounds is _dynamic_, and there is no static 
knowledge of the variable.  



### How do we handle input arguments? We increase the reference count of the
To handle input arguments, we set a flag in the run-time that represents whether 
the site is external to the current function. This flag is set and unset by the
caller function. Inside the callee, we only mess with the counter if the sites are 
internal. 
### How do we handle returns?
In Matlab, one may return explicitly through a return statement, or
implicitely through a fall through at the end of the function body.

When we return, we want to treat memory depending on the type:
#### Internal
For internal memory if is not a return value, we want to free it immediately, 
if it is a return value, we want to set the RC to 0 when we return.
#### External
External memory in this simple model are arguments, arguments are completely ignored
in terms of memory management, but under this model we add dynamic checks. In MatWably, we know that arguments are never mapped to return values
due to CopyAnalysis, there is therefore no risk on this reference sites.

### Statements

## AssignStmt a = b, or a,b = ... , or a =  ...
For `a=b`:
- Increase the `b` site, decrease the `a` site, if allocated.

For `a,b = ...`:
- Decrease the `a`,`b` sites if allocated, otherwise nothing.
- `a,b = user_defined(c,d)`:
    - In terms of parameters `c,d`
        - Query: `isExternal` on site
        - If site external: do nothing.
        - If internal, tag site as external before the call, 
            return to internal after the call.
## Analysis Procedure
Keep as a flow-insensitive analysis. The analysis uses two other analysis as 
dependencies, _Reaching Definitions_ and _ValueAnalysisUtil_. The former will serve
to know which variables are defined at a program point, and the later will serve
to tell which of them is an actual site. i.e. not a scalar.
We then go over the function arguments. Followed by the AssignStmts
followed by return fall-through.

## Run-time Support
To support the **GC** every allocated MachArray, the run-time structure in
Matwably, will have an extra byte to keep track of the counter. We will use
two extra bytes. One for # of references, here we assume that the max. number 
of references in the program will not exceed 255, and the other one for flags.
i.e. External, Internal in the case of this analysis.
### Run-time Function Support
- `increaseCount(ref)`: 
    - Checks if is external, if it is, does nothing
    - If is not, increases reference count by 1.
- `decreaseCount(ref)`:
    - Check if is external, if it is, does nothing
    - If is not, decreases reference count by 1.
-  `matwably_free(ref)`:
    - Adds the reference to array of sites to free in static section.
    - If no more space, frees immediately.
## MatWably Implementation

There are two aspects: 
              
- Analysis:
    - Analysis produces a map from stmts to the set of variables to
     increase/decrease counts for, and the set of variables to free at each
     statement.
- CallInsertion:
    - Obtains the analysis, and transforms the sets into instructions, when
      we generate statements to "free" sites.
# Limitations

This method, although simple, comes with certain downfalls. The first is problem
with a reference counter garbage collection is the locality of the reference counter
operation. Consider the Matlab program below:
 ```matlab
function ex1()
    while i < 10
        a = rand(2,2)
        disp(a)
        a = rand(2,2)
        disp(a)
        i = i + 1
    end
end
```
Variable `a` is quickly reference and de-referenced inside the _while-loop_, moreover,
the `a` sites are local arrays. The loop leads to freeing
of the site allocated in the loop. Together with the reference counter 
operations, and the overhead of the freeing operation. This results in a large
overhead on the run-time. Moreover, if the program is multi-threaded in a 
shared memory model, the reference counter operations correspond to atomic 
operations that require synchronization of threads.

