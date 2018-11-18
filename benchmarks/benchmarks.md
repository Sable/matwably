# Benchmarks
Running on environment: Node: v10.12.0
## Bubble

#### Description

Bubble sort, random array of 300 elements.

#### Inputs
Types:
- Jitted 10 Iterations: 
    - Warm up: 5 iters.
    - Steady state: 10 iters.
- Single Iteration drv_bubble(1) 
#### Characteristics
- No array creation in main computation.
- Single array setting/getting
#### Problems
- Matjuice V2:
    - Array Access
#### Results
MatjuiceJS: 

    - SingleIter: 0.124s
    - JITTED: 0.087s
MatjuiceV2:

    - SingleIter: 3.436s
    - JITTED: 3.104s
MatjuiceV2 Array Access Fixed:

    - SingleIter: 0.109s
    - JITTED: 0.084s
    
#### Outcome:
Get/Set individual indices make a huge difference making the actual benchmark as fast as the purely
JavaScript version.

## Bbai
#### Description
Babai Estimator: ||R*z-y||_2
#### Characteristics
- Temporary vector creation inside main loop `par`
- Operations:
    - zeros(n,1) Vector creation
    - mtimesVV() for vectors (dot produce)
    - rdivideSS
    - minus_SS
- Array Get Slice vector
- Array Get/Set single index.
#### Results
MatjuiceJS: 

    - SingleIter: 1.055s
    - JITTED: 1.055s
MatjuiceV2:

    - SingleIter: 0.707s
    - JITTED: 0.639s
MatjuiceV2 Array Access Fixed:

    - SingleIter: 0.687s
    - JITTED: 0.643s
#### Outcome:
Get/Set individual indices make no difference in this case.
## Collatz
#### Description

Implements collatz conjecture.

#### Characteristics
- Implements while-loop until n~1
- Simple while-loop
- No array manipulation
- Operations:
    - mod(n,2)
    - __scalar__: times, plus, rdivide.
#### Problems:
- The only difference between the two benchmark in terms of code generated
    is the mod_SS() call.
   
- __Experiment__: Run the mod_SS separately for different inputs
    in both JITTED and WARM UP runs.
- __mod_SS__:
```
WASM:
(func $mod_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    get_local 0
    get_local 1
    f64.div
    f64.floor
    f64.mul
    f64.sub      
)
JS:
function mc_mod_SS(x$2, y) {
    return x$2 - Math.floor(x$2 / y) * y;
}
```
See: [performance](https://github.com/Sable/matwably/performance), its very interesting, 
when running those benchmarks individually, i.e. mc_mod_SS(), $mod_SS.
 they show the following results:
- SINGLE ITERATION: 
    - js: 248916ns
    - wasm: 21504ns
- JITTED: 
    - js: 319ns
    - wasm: 272ns
So for the individual benchmarks 
#### Results
- MatjuiceJS: 
    - SingleIter: 0.191008ms
    - JITTED: 1.035s
- MatjuiceV2:
    - SingleIter:0.144689ms
    - JITTED: 3.131s
#### Outcome:
It seems like when combining calls, i.e. having WebAssembly calls
inside of JavaScript. The JIT compiled version actually performs
slightly worse than when all the calls are in JavaScript.
Moreover, the JIT compiled version of JavaScript performs better
than the JIT compiled version of WebAssembly.


## Other Ideas:

Provide an interface for a code generator to implement. So far all the back-ends
have used a where they re-do a lot of work. Of course this leads to a lot of problems and errors
when writing code. One may forget to handle a range of statements from the Matlab
language such as Global variable manipulation. Moreover, this will also provide correct
handling of constructs as the behaviour for a construct that is not supported should
be to throw a meaninful error as opposed to an opaque unrelated error.
The idea would be to create a set of interface or abstract classes to generate backe-end
code. This will make it much easier for back-end writters to acoomplish the following:
 
 - Cover all relevant constructs of Matlab without having to think of them themselves.
 This means that they will only have to worry about the subset of Matlab they want to implement.
 The provided class hierarchy will concern itself with throwing appropriate erros.
 Moreover they will have an idea of how much of the Matlab language they are targetting.
 - They will also get a set of unit tests meant for the abstract classes. The goal of
 these unit tests is to tell the back-end compiler developer, how much of the source language
 they have covered.
 - Avoiding call re-write. SInce every back-end developer interested in susing Matlab
 and tamer as a source language will have to implemenent the same constructs. The class
 hierarchy will provide a way to avoid all the boiler plate code for the same
 construct generator.
 - Moreover, based on all the analysis for Matlab code. We can provide options to, for
 instance, eliminate intermediate variables which a standard in an IR such as Tamer.
 The programmer will have the option to decide what to do with it.
 - Last thing the framework provides is a way to check whether the constructs
 supported are semantically equivalent. This will be simple, via inputs and outputs. 
 The main idea would be to use `disp` in a number of ways to check whether
 the generated code by the compiler supports given Matlab constructs.
 
 
 ## Jeff Benzanzon Thesis:
 
About dynamic type systems: 
The last point means that if the compiler is somehow able to statically evaluate x+y, it is free to do so, and gain sharper type information.
This is a general property of dynamic type inference systems:
    - A scheme for the automatic inference of variable types. 1980
    - Type inference system for Common Lisp. Software: Practice and Experience 1990
    -  Preliminary report on a practical type inference system for Common Lisp
    - Dynamic inference of static types for ruby. 2011
**Application binary interface (ABI):**: Explains how the application lays out the 
data structures of the language in the low-level target architectures. In this case, Julia lang uses the C structs, and 
array data layout as the ABI. 

Julia data types are either mutable or immutable. In the case of mutable, 
the ABI wraps the object around a cell which includes a pointer. In object comparison
pointers are compared.

  
 
 
 
  
  