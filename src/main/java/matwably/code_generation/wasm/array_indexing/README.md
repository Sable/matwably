# Dynamic generation of Matlab's Get/Set operations
We are trying to get rid of two aspects with this generation strategy:
- Array bound checks, whereby, we currently incur a bound check anytime we
 index into an array. 
- Allocation of arrays such as: `colon(low, step, high)`, `:`, whereby, instead of 
creating a loop with the bounds of the loop dictated by the values passed
to the `colon(low, step, high)`, we actually allocate a brand new MachArray
and use the values within it to traverse the array.
If we combine the generation of the loop to fetch an array value via indexing
with the bound checks, we can eliminate the check, and avoid the allocation
of the arrays associated with the indexing expressions `colon(low, step, high)`, `:`.

Expressions/expression types to take care of:
- `end()` function call
- `colon(low,step,high)`, and the possible occurrence of `end()` in those indexing exprs.
- `:` spread operator.
- `1,2,3,...` scalar values
- `[1,2,3]` array of values   
## Expressions to be reconstructed from TameIR
- Colon built-in calls
    - We only collect if only use is in the indexing expression.
    - For those who are in indexing expressions, we check arguments and,
        - If we know them statically, load constant values.
            What are the cases for statically knowing the arguments?
        - If we do not know them statically, we load them simply by 
          generating the expression.
    - There is a special case if we know an expression inside the colon is
      an indexing expression, i.e. end(). If that is the case, then we 
      also have to fetch this, and try to determine the value statically and
      check if we have to generate at all this expression. (Optional)
- End expressions
    ```Matlab
       A(end)
    ```
  - For these, we check if they can be found out statically, if they can, 
    we directly load the value.
  - For now this will be ignored as non of the benchmarks contain this.
## How do we handle these meta-structures?
- Spread operator
  - Produce the run-time representation of the spread operator.
In terms of indexing expressions we have the following cases:
- Colon operator
    - low:step:high
    - low:high
    - low:end
## How do we represent both values in the run-time?
The MachArray is defined by a TypeAttribute which contains a _Run-time kind_.
We use the _Run-time Kind_ identify and differentiate between, colon, MachArrays, and
scalars. Following the TypeAttribute, the next few bytes describe the meta-data structure.
For the spread operator setting the _Run-time kind_ will be sufficient to fully describe it.
The colon operator, on the other hand, requires to have three aligned values representing
the colon operator; these values must be aligned in terms of the memory accesses, 
since each of them is a double. This leaves the structure as a 32 byte structure.
This could eventually be further optimized if its an integer, in that case the structure
will be 16 bytes only. For now, we will assume the structure is 8 bytes. 

## Edge cases

We are not gonna worry, for now, about handling the edge cases of this optimization.
i.e. when the colon operator is empty. We are gonna handle bound checks
as this are important for the correctness of the subset of cases.

 

 

