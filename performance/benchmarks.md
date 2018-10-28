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

