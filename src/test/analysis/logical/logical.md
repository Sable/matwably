# Logical Variable Optimization

MatWably currently only supports doubles, this means when we evaluate conditions 
for if and while statements, we must perform a logical operations in 
_wasm_, which return an i32 value, then we must convert to a double before 
setting the actual variable. Then, when we use the variable as a condition in 
an if/while stmt, we check the f64 value to not equal 0. This represents an overall
overhead of 3 instructions for logical scalar variable.

This analysis therefore performs the logical variable optimization, whereby,
we use _ReachingDefinitions_ and the built-in framework to determine whether
the a given variable in the program is a logical.

This analysis has a compiler flag _disallow_logicals_ to turn optimization on/off. By default, 
the compiler generates code using logical
variables. If the compiler is ran with a particular flag, the generated _wasm_, 
performs the extra three instructions, and ignores the concept of logicals.

Example: Input is a _ValueAnalysis_ where the two arguments are scalar doubles.

```matlab

function logical_use(a,b)
    if a < b
        % ... 
    end 
end
```

Without Optimization:
```wasm
(func $logical_use (param $a_f64 f64)(param $b_f64 f64)
    
    get_local $a_f64
    get_local $b_f64
    f64.lt
    f64.convert_s/i32
    f64.const 0
    f64.ne
    if
        ;; ...
    end
)
```
 With Optimization:
 ```wasm
 (func $logical_use (param $a_f64 f64)(param $b_f64 f64)
     get_local $a_f64
     get_local $b_f64
     f64.lt
     if
         ;; ...
     end
 )
 ```
 