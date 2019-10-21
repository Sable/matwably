;; Function types
(type $1 (func (param i32) (result i32)))
(type $2 (func (result i32)))
(type $3 (func (param i32)))
(type $4 (func (param i32 i32) (result i32)))
(type $5 (func (param i32 i32 i32 i32 i32 i32 f64)))

(import "env" "DYNAMICTOP_PTR" (global $2 i32))
(import "env" "STACKTOP" (global $5 i32))
(import "env" "STACK_MAX" (global $6 i32))
(import "env" "memory" (memory $mem 256))
(import "env" "enlargeMemory" (func $enlargeMemory (type $2)))
(import "env" "___setErrNo" (func $___setErrNo (type $3)))
(import "env" "memoryBase" (global $0 i32))
(import "env" "getTotalMemory" (func $getTotalMemory (type $2)))
(import "env" "abortOnCannotGrowMemory" (func $abortOnCannotGrowMemory (type $2)))
(import "env" "abortStackOverflow" (func $abortStackOverflow (type $3)))
(import "env" "gcPrintMemoryUsage" (func $gcPrintMemoryUsage (type $5)))
(import "js" "printError" (func $printError (param i32 i32)(result i32)))
(import "js" "print_array_f64" (func $print_array_f64 (param i32 i32)))
(import "js" "printString" (func $printString (param i32 i32)(result i32)))
(import "js" "printDouble" (func $printDouble (param i32)(result i32)))
(import "js" "printDouble" (func $printDoubleNumber (param f64) (result f64)))  
(import "js" "time" (func $time (result f64)))
(import "js" "printTime" (func $printTime (param f64)(result f64)))  
(import "js" "printMachArrayHeader" (func $printMachArrayHeader (param i32 i32)))  
(import "debug" "printMarker" (func $printMarker))
(import "math" "ones" (func $ones_s (result f64)))
(import "math" "zeros" (func $zeros_s (result f64)))
(import "math" "rand" (func $rand_S (result f64)))
(import "math" "randn" (func $randn_S (result f64)))
(import "math" "randi" (func $randi_S (param f64) (result f64)))
(import "math" "isnan" (func $isnan (param f64) (result i32)))
(import "math" "power" (func $power_SS (param f64)(param f64) (result f64)))
(import "math" "sin" (func $sin_S (param f64)(result f64)))
(import "math" "cos" (func $cos_S (param f64)(result f64)))
(import "math" "tan" (func $tan_S (param f64)(result f64)))
(import "math" "exp" (func $exp_S (param f64)(result f64)))
(import "math" "log" (func $log_S (param f64)(result f64)))
(import "math" "log10" (func $log10_S (param f64)(result f64)))
(import "math" "log2" (func $log2_S (param f64)(result f64)))
(import "math" "pi" (func $pi (result f64)))
(import "math" "e" (func $e (result f64)))
(export "___errno_location" (func $___errno_location))  
(export "_sbrk" (func $sbrk))
(export "_free" (func $free))
(export "_malloc" (func $malloc))

(export "sin_S" (func $sin_S))
(export "cos_S" (func $cos_S))
(export "tan_S" (func $tan_S))
(export "exp_S" (func $exp_S))
(export "log_S" (func $log_S))
(export "log10_S" (func $log10_S))
(export "log2_S" (func $log2_S))
(export "pi" (func $pi))
(export "e" (func $e))


;; Mutable globals
(global $DYNAMICTOP_PTR (mut i32) (get_global $2))
(global $STACKTOP (mut i32) (get_global $5))
(global $STACK_MAX (mut i32) (get_global $6))
(export "get_stack_top" (func $get_stack_top))
(func  $get_stack_top (result i32)
    get_global $STACKTOP
)

(global $TIC_TIME (mut f64) (f64.const 0))
(table $tab 256 anyfunc)

;; (elem $tab (i32.const 10) )
(export "mem" (memory $mem))
(export "tab" (table $tab))

(data $mem (i32.const 2560) "Out-of-memory, trying to allocate a larger memory than available\n\00\00\00\00\00\00\00\00Error: Negative length is not allowed in this context\n")
(data $mem (i32.const 2696) "Index out-of-bound\n\00\00\00\00\00\00")
(data $mem (i32.const 2720) "Index exceeds matrix dimensions\00\00")
(data $mem (i32.const 2758) "Subscript indices must either be real positive integers or logicals\00\00\00\00\00\00")
(data $mem (i32.const 2832) "Size vector should be a row vector with real elements.")
(data $mem (i32.const 2888) "Not enough input arguments.")
(data $mem (i32.const 2920) "Too many input arguments.")    
(data $mem (i32.const 2944) "To RESHAPE the number of elements must not change.")
(data $mem (i32.const 3000) "Subscripted assignment dimension mismatch.")
(data $mem (i32.const 3048) "Dynamic array growth is currently not supported in set function.")
(data $mem (i32.const 3112) "Dimensions of matrices being concatenated are not consistent.")
(data $mem (i32.const 3176) "Concatanating dimension must be larger than 0")
(data $mem (i32.const 3232) "Concatanating dimension larger than the input arguments dimensions.")
(data $mem (i32.const 3304) "Matrix dimensions must agree.")
(data $mem (i32.const 3336) "Dimension argument must be a positive integer scalar within indexing range.")
(data $mem (i32.const 3416) "Input must have array class.")
(data $mem (i32.const 3448) "Arguments must be 2-D, or at least one argument must be scalar.")
(data $mem (i32.const 3512) "Inner matrix dimensions must agree.")
(data $mem (i32.const 3552) "N-dimensional arrays are not supported.")
(data $mem (i32.const 3592) "Result array must be the same dimensions as the input.") 
(data $mem (i32.const 3648) "Unspecified library error in function.");; ()38 
(data $mem (i32.const 3688) "Size input must be scalar.");; 26 
(data $mem (i32.const 3720) "Error: src, and dest array must have the same number of values") ;; 62, 3782, 3784
;; New Errors: (data $mem (i32.const 3592) "Result array must be the same dimensions as the input.") 
(func $throwError (param $error i32)
    (local $offset i32)(local $length i32)
    (;
        Errors:
            0: "Allocating larger memory than expected"
            1: "Negative length is not allowed in this context"
            2: "Index out-of-bounds"
    ;)
    block block  block  block block block block block block block block block block block block block block block block block block block block block block
        get_local $error
        br_table 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23
        end
            (set_local $offset (i32.const 2560))
            (set_local $length (i32.const 65))
            br 23
        end
            (set_local $offset (i32.const 2640))
            (set_local $length (i32.const 47))
            br 22
        end 
            (set_local $offset (i32.const 2696))
            (set_local $length (i32.const 10)) 
            br 21
        end
            (set_local $offset (i32.const 2720))
            (set_local $length (i32.const 31)) 
            br 20
        end
            (set_local $offset (i32.const 2758))
            (set_local $length (i32.const 67)) 
            br 19
        end
            (set_local $offset (i32.const 2832))
            (set_local $length (i32.const 54)) 
            br 18
        end
            (set_local $offset (i32.const 2888))
            (set_local $length (i32.const 27)) 
            br 17
        end
            (set_local $offset (i32.const 2920))
            (set_local $length (i32.const 24)) 
            br 16
        end
            (set_local $offset (i32.const 2944))
            (set_local $length (i32.const 50)) ;; 434 
            br 15
        end
            (set_local $offset (i32.const 3000))
            (set_local $length (i32.const 42)) 
            br 14
        end 
            (set_local $offset (i32.const 3048))
            (set_local $length (i32.const 64))
            br 13
        end 
            (set_local $offset (i32.const 3112))
            (set_local $length (i32.const 61))
            br 12
        end 
            (set_local $offset (i32.const 3176))
            (set_local $length (i32.const 45))
            br 11
        end 
            (set_local $offset (i32.const 3232))
            (set_local $length (i32.const 67))
            br 10
        end 
            (set_local $offset (i32.const 3304))
            (set_local $length (i32.const 29))
            br 9
        end 
            (set_local $offset (i32.const 3336))
            (set_local $length (i32.const 75))
            br 8
        end 
            (set_local $offset (i32.const 3416))
            (set_local $length (i32.const 28))
            br 7
        end 
            (set_local $offset (i32.const 3448))
            (set_local $length (i32.const 63))
            br 6
        end 
            (set_local $offset (i32.const 3512))
            (set_local $length (i32.const 35))
            br 5
        end 
            (set_local $offset (i32.const 3552))
            (set_local $length (i32.const 39))
            br 4
        end 
            (set_local $offset (i32.const 3592))
            (set_local $length (i32.const 54))
            br 3
        end 
            (set_local $offset (i32.const 3512))
            (set_local $length (i32.const 35))
            br 2
        end 
            (set_local $offset (i32.const 3552))
            (set_local $length (i32.const 39))
            br 1
        end 
            (set_local $offset (i32.const 3592))
            (set_local $length (i32.const 54))
            br 0
    end 
    get_local $offset
    get_local $length
    call $printError
    drop
    unreachable
)
(export "tic" (func $tic)) 
(func $tic (result f64)
    call $time
    set_global $TIC_TIME
    get_global $TIC_TIME
)
(export "toc" (func $toc))
(func $toc (result f64)
    call $time
    get_global $TIC_TIME
    f64.sub
    f64.const 1000
    f64.div
    ;; call $printTime

)
(func $get_heap_top_ptr (export "get_heap_top_ptr" ) (result i32)
    get_global $DYNAMICTOP_PTR
)

;; Returns __errno_location in memory
(func $___errno_location (type $2) (result i32)
    (local i32 i32)
    get_global $STACKTOP
    set_local 1
    i32.const 1520 ;;TODO(dherre3): Constant to be replaced with largest static address
return)
(;
    sbrk system call emulator
;)
(func $sbrk (type $1) (param i32) (result i32)
    (local i32)
    get_local 0
    i32.const 0
    i32.gt_s
    get_global $DYNAMICTOP_PTR
    i32.load
    tee_local 1
    get_local 0
    i32.add
    tee_local 0
    get_local 1
    i32.lt_s
    i32.and
    get_local 0
    i32.const 0
    i32.lt_s
    i32.or
    if  ;; label = @1
        call $abortOnCannotGrowMemory
        drop
        i32.const 12
        call $___setErrNo
        i32.const -1
        return
    end
    get_global $DYNAMICTOP_PTR
    get_local 0
    i32.store
    get_local 0
    call $getTotalMemory
    i32.gt_s
    if  ;; label = @1
        call $enlargeMemory
        i32.eqz
        if  ;; label = @2
            get_global $DYNAMICTOP_PTR
            get_local 1
            i32.store
            i32.const 12
            call $___setErrNo
            i32.const -1
            return
        end
    end
    get_local 1)
(func $malloc (type $1) (param i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    ;; %gc_malloc 
    get_global  $STACKTOP
    set_local 10
    get_global  $STACKTOP
    i32.const 16
    i32.add
    set_global  $STACKTOP
    get_local 10
    set_local 8
    block  ;; label = @1
    get_local 0
    i32.const 245
    i32.lt_u
    if  ;; label = @2
        get_local 0
        i32.const 11
        i32.add
        i32.const -8
        i32.and
        set_local 3
        i32.const 1024
        i32.load
        tee_local 6
        get_local 0
        i32.const 11
        i32.lt_u
        if (result i32)  ;; label = @3
        i32.const 16
        tee_local 3
        else
        get_local 3
        end
        i32.const 3
        i32.shr_u
        tee_local 0
        i32.shr_u
        tee_local 1
        i32.const 3
        i32.and
        if  ;; label = @3
        get_local 1
        i32.const 1
        i32.and
        i32.const 1
        i32.xor
        get_local 0
        i32.add
        tee_local 0
        i32.const 3
        i32.shl
        i32.const 1064
        i32.add
        tee_local 1
        i32.const 8
        i32.add
        tee_local 5
        i32.load
        tee_local 3
        i32.const 8
        i32.add
        tee_local 4
        i32.load
        tee_local 2
        get_local 1
        i32.eq
        if  ;; label = @4
            i32.const 1024
            get_local 6
            i32.const 1
            get_local 0
            i32.shl
            i32.const -1
            i32.xor
            i32.and
            i32.store
        else
            get_local 2
            get_local 1
            i32.store offset=12
            get_local 5
            get_local 2
            i32.store
        end
        get_local 3
        get_local 0
        i32.const 3
        i32.shl
        tee_local 0
        i32.const 3
        i32.or
        i32.store offset=4
        get_local 3
        get_local 0
        i32.add
        i32.const 4
        i32.add
        tee_local 0
        get_local 0
        i32.load
        i32.const 1
        i32.or
        i32.store
        get_local 10
        set_global  $STACKTOP
        get_local 4
        return
        end
        get_local 3
        i32.const 1032
        i32.load
        tee_local 9
        i32.gt_u
        if  ;; label = @3
        get_local 1
        if  ;; label = @4
            get_local 1
            get_local 0
            i32.shl
            i32.const 2
            get_local 0
            i32.shl
            tee_local 0
            i32.const 0
            get_local 0
            i32.sub
            i32.or
            i32.and
            tee_local 0
            i32.const 0
            get_local 0
            i32.sub
            i32.and
            i32.const -1
            i32.add
            tee_local 1
            i32.const 12
            i32.shr_u
            i32.const 16
            i32.and
            set_local 0
            get_local 1
            get_local 0
            i32.shr_u
            tee_local 1
            i32.const 5
            i32.shr_u
            i32.const 8
            i32.and
            tee_local 2
            get_local 0
            i32.or
            get_local 1
            get_local 2
            i32.shr_u
            tee_local 0
            i32.const 2
            i32.shr_u
            i32.const 4
            i32.and
            tee_local 1
            i32.or
            get_local 0
            get_local 1
            i32.shr_u
            tee_local 0
            i32.const 1
            i32.shr_u
            i32.const 2
            i32.and
            tee_local 1
            i32.or
            get_local 0
            get_local 1
            i32.shr_u
            tee_local 0
            i32.const 1
            i32.shr_u
            i32.const 1
            i32.and
            tee_local 1
            i32.or
            get_local 0
            get_local 1
            i32.shr_u
            i32.add
            tee_local 2
            i32.const 3
            i32.shl
            i32.const 1064
            i32.add
            tee_local 0
            i32.const 8
            i32.add
            tee_local 4
            i32.load
            tee_local 1
            i32.const 8
            i32.add
            tee_local 7
            i32.load
            tee_local 5
            get_local 0
            i32.eq
            if  ;; label = @5
            i32.const 1024
            get_local 6
            i32.const 1
            get_local 2
            i32.shl
            i32.const -1
            i32.xor
            i32.and
            tee_local 0
            i32.store
            else
            get_local 5
            get_local 0
            i32.store offset=12
            get_local 4
            get_local 5
            i32.store
            get_local 6
            set_local 0
            end
            get_local 1
            get_local 3
            i32.const 3
            i32.or
            i32.store offset=4
            get_local 1
            get_local 3
            i32.add
            tee_local 4
            get_local 2
            i32.const 3
            i32.shl
            tee_local 2
            get_local 3
            i32.sub
            tee_local 5
            i32.const 1
            i32.or
            i32.store offset=4
            get_local 1
            get_local 2
            i32.add
            get_local 5
            i32.store
            get_local 9
            if  ;; label = @5
            i32.const 1044
            i32.load
            set_local 2
            get_local 9
            i32.const 3
            i32.shr_u
            tee_local 3
            i32.const 3
            i32.shl
            i32.const 1064
            i32.add
            set_local 1
            get_local 0
            i32.const 1
            get_local 3
            i32.shl
            tee_local 3
            i32.and
            if (result i32)  ;; label = @6
                get_local 1
                i32.const 8
                i32.add
                tee_local 3
                i32.load
            else
                i32.const 1024
                get_local 0
                get_local 3
                i32.or
                i32.store
                get_local 1
                i32.const 8
                i32.add
                set_local 3
                get_local 1
            end
            set_local 0
            get_local 3
            get_local 2
            i32.store
            get_local 0
            get_local 2
            i32.store offset=12
            get_local 2
            get_local 0
            i32.store offset=8
            get_local 2
            get_local 1
            i32.store offset=12
            end
            i32.const 1032
            get_local 5
            i32.store
            i32.const 1044
            get_local 4
            i32.store
            get_local 10
            set_global  $STACKTOP
            get_local 7
            return
        end
        i32.const 1028
        i32.load
        tee_local 12
        if  ;; label = @4
            get_local 12
            i32.const 0
            get_local 12
            i32.sub
            i32.and
            i32.const -1
            i32.add
            tee_local 1
            i32.const 12
            i32.shr_u
            i32.const 16
            i32.and
            set_local 0
            get_local 1
            get_local 0
            i32.shr_u
            tee_local 1
            i32.const 5
            i32.shr_u
            i32.const 8
            i32.and
            tee_local 2
            get_local 0
            i32.or
            get_local 1
            get_local 2
            i32.shr_u
            tee_local 0
            i32.const 2
            i32.shr_u
            i32.const 4
            i32.and
            tee_local 1
            i32.or
            get_local 0
            get_local 1
            i32.shr_u
            tee_local 0
            i32.const 1
            i32.shr_u
            i32.const 2
            i32.and
            tee_local 1
            i32.or
            get_local 0
            get_local 1
            i32.shr_u
            tee_local 0
            i32.const 1
            i32.shr_u
            i32.const 1
            i32.and
            tee_local 1
            i32.or
            get_local 0
            get_local 1
            i32.shr_u
            i32.add
            i32.const 2
            i32.shl
            i32.const 1328
            i32.add
            i32.load
            tee_local 2
            i32.load offset=4
            i32.const -8
            i32.and
            get_local 3
            i32.sub
            set_local 1
            get_local 2
            i32.const 16
            i32.add
            get_local 2
            i32.load offset=16
            i32.eqz
            i32.const 2
            i32.shl
            i32.add
            i32.load
            tee_local 0
            if  ;; label = @5
            loop  ;; label = @6
                get_local 0
                i32.load offset=4
                i32.const -8
                i32.and
                get_local 3
                i32.sub
                tee_local 5
                get_local 1
                i32.lt_u
                tee_local 4
                if  ;; label = @7
                get_local 5
                set_local 1
                end
                get_local 4
                if  ;; label = @7
                get_local 0
                set_local 2
                end
                get_local 0
                i32.const 16
                i32.add
                get_local 0
                i32.load offset=16
                i32.eqz
                i32.const 2
                i32.shl
                i32.add
                i32.load
                tee_local 0
                br_if 0 (;@6;)
                get_local 1
                set_local 5
            end
            else
            get_local 1
            set_local 5
            end
            get_local 2
            get_local 3
            i32.add
            tee_local 11
            get_local 2
            i32.gt_u
            if  ;; label = @5
            get_local 2
            i32.load offset=24
            set_local 8
            block  ;; label = @6
                get_local 2
                i32.load offset=12
                tee_local 0
                get_local 2
                i32.eq
                if  ;; label = @7
                get_local 2
                i32.const 20
                i32.add
                tee_local 1
                i32.load
                tee_local 0
                i32.eqz
                if  ;; label = @8
                    get_local 2
                    i32.const 16
                    i32.add
                    tee_local 1
                    i32.load
                    tee_local 0
                    i32.eqz
                    if  ;; label = @9
                    i32.const 0
                    set_local 0
                    br 3 (;@6;)
                    end
                end
                loop  ;; label = @8
                    get_local 0
                    i32.const 20
                    i32.add
                    tee_local 4
                    i32.load
                    tee_local 7
                    if  ;; label = @9
                    get_local 7
                    set_local 0
                    get_local 4
                    set_local 1
                    br 1 (;@8;)
                    end
                    get_local 0
                    i32.const 16
                    i32.add
                    tee_local 4
                    i32.load
                    tee_local 7
                    if  ;; label = @9
                    get_local 7
                    set_local 0
                    get_local 4
                    set_local 1
                    br 1 (;@8;)
                    end
                end
                get_local 1
                i32.const 0
                i32.store
                else
                get_local 2
                i32.load offset=8
                tee_local 1
                get_local 0
                i32.store offset=12
                get_local 0
                get_local 1
                i32.store offset=8
                end
            end
            block  ;; label = @6
                get_local 8
                if  ;; label = @7
                get_local 2
                get_local 2
                i32.load offset=28
                tee_local 1
                i32.const 2
                i32.shl
                i32.const 1328
                i32.add
                tee_local 4
                i32.load
                i32.eq
                if  ;; label = @8
                    get_local 4
                    get_local 0
                    i32.store
                    get_local 0
                    i32.eqz
                    if  ;; label = @9
                    i32.const 1028
                    get_local 12
                    i32.const 1
                    get_local 1
                    i32.shl
                    i32.const -1
                    i32.xor
                    i32.and
                    i32.store
                    br 3 (;@6;)
                    end
                else
                    get_local 8
                    i32.const 16
                    i32.add
                    get_local 8
                    i32.load offset=16
                    get_local 2
                    i32.ne
                    i32.const 2
                    i32.shl
                    i32.add
                    get_local 0
                    i32.store
                    get_local 0
                    i32.eqz
                    br_if 2 (;@6;)
                end
                get_local 0
                get_local 8
                i32.store offset=24
                get_local 2
                i32.load offset=16
                tee_local 1
                if  ;; label = @8
                    get_local 0
                    get_local 1
                    i32.store offset=16
                    get_local 1
                    get_local 0
                    i32.store offset=24
                end
                get_local 2
                i32.load offset=20
                tee_local 1
                if  ;; label = @8
                    get_local 0
                    get_local 1
                    i32.store offset=20
                    get_local 1
                    get_local 0
                    i32.store offset=24
                end
                end
            end
            get_local 5
            i32.const 16
            i32.lt_u
            if  ;; label = @6
                get_local 2
                get_local 5
                get_local 3
                i32.add
                tee_local 0
                i32.const 3
                i32.or
                i32.store offset=4
                get_local 2
                get_local 0
                i32.add
                i32.const 4
                i32.add
                tee_local 0
                get_local 0
                i32.load
                i32.const 1
                i32.or
                i32.store
            else
                get_local 2
                get_local 3
                i32.const 3
                i32.or
                i32.store offset=4
                get_local 11
                get_local 5
                i32.const 1
                i32.or
                i32.store offset=4
                get_local 11
                get_local 5
                i32.add
                get_local 5
                i32.store
                get_local 9
                if  ;; label = @7
                i32.const 1044
                i32.load
                set_local 4
                get_local 9
                i32.const 3
                i32.shr_u
                tee_local 1
                i32.const 3
                i32.shl
                i32.const 1064
                i32.add
                set_local 0
                get_local 6
                i32.const 1
                get_local 1
                i32.shl
                tee_local 1
                i32.and
                if (result i32)  ;; label = @8
                    get_local 0
                    i32.const 8
                    i32.add
                    tee_local 3
                    i32.load
                else
                    i32.const 1024
                    get_local 6
                    get_local 1
                    i32.or
                    i32.store
                    get_local 0
                    i32.const 8
                    i32.add
                    set_local 3
                    get_local 0
                end
                set_local 1
                get_local 3
                get_local 4
                i32.store
                get_local 1
                get_local 4
                i32.store offset=12
                get_local 4
                get_local 1
                i32.store offset=8
                get_local 4
                get_local 0
                i32.store offset=12
                end
                i32.const 1032
                get_local 5
                i32.store
                i32.const 1044
                get_local 11
                i32.store
            end
            get_local 10
            set_global  $STACKTOP
            get_local 2
            i32.const 8
            i32.add
            return
            else
            get_local 3
            set_local 0
            end
        else
            get_local 3
            set_local 0
        end
        else
        get_local 3
        set_local 0
        end
    else
        get_local 0
        i32.const -65
        i32.gt_u
        if  ;; label = @3
        i32.const -1
        set_local 0
        else
        get_local 0
        i32.const 11
        i32.add
        tee_local 0
        i32.const -8
        i32.and
        set_local 2
        i32.const 1028
        i32.load
        tee_local 5
        if  ;; label = @4
            get_local 0
            i32.const 8
            i32.shr_u
            tee_local 0
            if (result i32)  ;; label = @5
            get_local 2
            i32.const 16777215
            i32.gt_u
            if (result i32)  ;; label = @6
                i32.const 31
            else
                get_local 2
                i32.const 14
                get_local 0
                get_local 0
                i32.const 1048320
                i32.add
                i32.const 16
                i32.shr_u
                i32.const 8
                i32.and
                tee_local 0
                i32.shl
                tee_local 1
                i32.const 520192
                i32.add
                i32.const 16
                i32.shr_u
                i32.const 4
                i32.and
                tee_local 3
                get_local 0
                i32.or
                get_local 1
                get_local 3
                i32.shl
                tee_local 0
                i32.const 245760
                i32.add
                i32.const 16
                i32.shr_u
                i32.const 2
                i32.and
                tee_local 1
                i32.or
                i32.sub
                get_local 0
                get_local 1
                i32.shl
                i32.const 15
                i32.shr_u
                i32.add
                tee_local 0
                i32.const 7
                i32.add
                i32.shr_u
                i32.const 1
                i32.and
                get_local 0
                i32.const 1
                i32.shl
                i32.or
            end
            else
            i32.const 0
            end
            set_local 9
            i32.const 0
            get_local 2
            i32.sub
            set_local 3
            block  ;; label = @5
            block  ;; label = @6
                get_local 9
                i32.const 2
                i32.shl
                i32.const 1328
                i32.add
                i32.load
                tee_local 0
                if  ;; label = @7
                i32.const 25
                get_local 9
                i32.const 1
                i32.shr_u
                i32.sub
                set_local 4
                i32.const 0
                set_local 1
                get_local 2
                get_local 9
                i32.const 31
                i32.eq
                if (result i32)  ;; label = @8
                    i32.const 0
                else
                    get_local 4
                end
                i32.shl
                set_local 7
                i32.const 0
                set_local 4
                loop  ;; label = @8
                    get_local 0
                    i32.load offset=4
                    i32.const -8
                    i32.and
                    get_local 2
                    i32.sub
                    tee_local 6
                    get_local 3
                    i32.lt_u
                    if  ;; label = @9
                    get_local 6
                    if  ;; label = @10
                        get_local 0
                        set_local 1
                        get_local 6
                        set_local 3
                    else
                        i32.const 0
                        set_local 3
                        get_local 0
                        set_local 1
                        br 4 (;@6;)
                    end
                    end
                    get_local 0
                    i32.load offset=20
                    tee_local 6
                    i32.eqz
                    get_local 6
                    get_local 0
                    i32.const 16
                    i32.add
                    get_local 7
                    i32.const 31
                    i32.shr_u
                    i32.const 2
                    i32.shl
                    i32.add
                    i32.load
                    tee_local 0
                    i32.eq
                    i32.or
                    i32.eqz
                    if  ;; label = @9
                    get_local 6
                    set_local 4
                    end
                    get_local 7
                    get_local 0
                    i32.eqz
                    tee_local 6
                    i32.const 1
                    i32.xor
                    i32.shl
                    set_local 7
                    get_local 6
                    i32.eqz
                    br_if 0 (;@8;)
                    get_local 1
                    set_local 0
                end
                else
                i32.const 0
                set_local 0
                end
                get_local 4
                get_local 0
                i32.or
                if (result i32)  ;; label = @7
                get_local 4
                else
                get_local 5
                i32.const 2
                get_local 9
                i32.shl
                tee_local 0
                i32.const 0
                get_local 0
                i32.sub
                i32.or
                i32.and
                tee_local 0
                i32.eqz
                if  ;; label = @8
                    get_local 2
                    set_local 0
                    br 7 (;@1;)
                end
                get_local 0
                i32.const 0
                get_local 0
                i32.sub
                i32.and
                i32.const -1
                i32.add
                tee_local 4
                i32.const 12
                i32.shr_u
                i32.const 16
                i32.and
                set_local 1
                i32.const 0
                set_local 0
                get_local 4
                get_local 1
                i32.shr_u
                tee_local 4
                i32.const 5
                i32.shr_u
                i32.const 8
                i32.and
                tee_local 7
                get_local 1
                i32.or
                get_local 4
                get_local 7
                i32.shr_u
                tee_local 1
                i32.const 2
                i32.shr_u
                i32.const 4
                i32.and
                tee_local 4
                i32.or
                get_local 1
                get_local 4
                i32.shr_u
                tee_local 1
                i32.const 1
                i32.shr_u
                i32.const 2
                i32.and
                tee_local 4
                i32.or
                get_local 1
                get_local 4
                i32.shr_u
                tee_local 1
                i32.const 1
                i32.shr_u
                i32.const 1
                i32.and
                tee_local 4
                i32.or
                get_local 1
                get_local 4
                i32.shr_u
                i32.add
                i32.const 2
                i32.shl
                i32.const 1328
                i32.add
                i32.load
                end
                tee_local 1
                br_if 0 (;@6;)
                get_local 0
                set_local 4
                br 1 (;@5;)
            end
            loop  ;; label = @6
                get_local 1
                i32.load offset=4
                i32.const -8
                i32.and
                get_local 2
                i32.sub
                tee_local 4
                get_local 3
                i32.lt_u
                tee_local 7
                if  ;; label = @7
                get_local 4
                set_local 3
                end
                get_local 7
                if  ;; label = @7
                get_local 1
                set_local 0
                end
                get_local 1
                i32.const 16
                i32.add
                get_local 1
                i32.load offset=16
                i32.eqz
                i32.const 2
                i32.shl
                i32.add
                i32.load
                tee_local 1
                br_if 0 (;@6;)
                get_local 0
                set_local 4
            end
            end
            get_local 4
            if  ;; label = @5
            get_local 3
            i32.const 1032
            i32.load
            get_local 2
            i32.sub
            i32.lt_u
            if  ;; label = @6
                get_local 4
                get_local 2
                i32.add
                tee_local 8
                get_local 4
                i32.le_u
                if  ;; label = @7
                get_local 10
                set_global  $STACKTOP
                i32.const 0
                return
                end
                get_local 4
                i32.load offset=24
                set_local 9
                block  ;; label = @7
                get_local 4
                i32.load offset=12
                tee_local 0
                get_local 4
                i32.eq
                if  ;; label = @8
                    get_local 4
                    i32.const 20
                    i32.add
                    tee_local 1
                    i32.load
                    tee_local 0
                    i32.eqz
                    if  ;; label = @9
                    get_local 4
                    i32.const 16
                    i32.add
                    tee_local 1
                    i32.load
                    tee_local 0
                    i32.eqz
                    if  ;; label = @10
                        i32.const 0
                        set_local 0
                        br 3 (;@7;)
                    end
                    end
                    loop  ;; label = @9
                    get_local 0
                    i32.const 20
                    i32.add
                    tee_local 7
                    i32.load
                    tee_local 6
                    if  ;; label = @10
                        get_local 6
                        set_local 0
                        get_local 7
                        set_local 1
                        br 1 (;@9;)
                    end
                    get_local 0
                    i32.const 16
                    i32.add
                    tee_local 7
                    i32.load
                    tee_local 6
                    if  ;; label = @10
                        get_local 6
                        set_local 0
                        get_local 7
                        set_local 1
                        br 1 (;@9;)
                    end
                    end
                    get_local 1
                    i32.const 0
                    i32.store
                else
                    get_local 4
                    i32.load offset=8
                    tee_local 1
                    get_local 0
                    i32.store offset=12
                    get_local 0
                    get_local 1
                    i32.store offset=8
                end
                end
                block  ;; label = @7
                get_local 9
                if (result i32)  ;; label = @8
                    get_local 4
                    get_local 4
                    i32.load offset=28
                    tee_local 1
                    i32.const 2
                    i32.shl
                    i32.const 1328
                    i32.add
                    tee_local 7
                    i32.load
                    i32.eq
                    if  ;; label = @9
                    get_local 7
                    get_local 0
                    i32.store
                    get_local 0
                    i32.eqz
                    if  ;; label = @10
                        i32.const 1028
                        get_local 5
                        i32.const 1
                        get_local 1
                        i32.shl
                        i32.const -1
                        i32.xor
                        i32.and
                        tee_local 0
                        i32.store
                        br 3 (;@7;)
                    end
                    else
                    get_local 9
                    i32.const 16
                    i32.add
                    get_local 9
                    i32.load offset=16
                    get_local 4
                    i32.ne
                    i32.const 2
                    i32.shl
                    i32.add
                    get_local 0
                    i32.store
                    get_local 0
                    i32.eqz
                    if  ;; label = @10
                        get_local 5
                        set_local 0
                        br 3 (;@7;)
                    end
                    end
                    get_local 0
                    get_local 9
                    i32.store offset=24
                    get_local 4
                    i32.load offset=16
                    tee_local 1
                    if  ;; label = @9
                    get_local 0
                    get_local 1
                    i32.store offset=16
                    get_local 1
                    get_local 0
                    i32.store offset=24
                    end
                    get_local 4
                    i32.load offset=20
                    tee_local 1
                    if (result i32)  ;; label = @9
                    get_local 0
                    get_local 1
                    i32.store offset=20
                    get_local 1
                    get_local 0
                    i32.store offset=24
                    get_local 5
                    else
                    get_local 5
                    end
                else
                    get_local 5
                end
                set_local 0
                end
                block  ;; label = @7
                get_local 3
                i32.const 16
                i32.lt_u
                if  ;; label = @8
                    get_local 4
                    get_local 3
                    get_local 2
                    i32.add
                    tee_local 0
                    i32.const 3
                    i32.or
                    i32.store offset=4
                    get_local 4
                    get_local 0
                    i32.add
                    i32.const 4
                    i32.add
                    tee_local 0
                    get_local 0
                    i32.load
                    i32.const 1
                    i32.or
                    i32.store
                else
                    get_local 4
                    get_local 2
                    i32.const 3
                    i32.or
                    i32.store offset=4
                    get_local 8
                    get_local 3
                    i32.const 1
                    i32.or
                    i32.store offset=4
                    get_local 8
                    get_local 3
                    i32.add
                    get_local 3
                    i32.store
                    get_local 3
                    i32.const 3
                    i32.shr_u
                    set_local 1
                    get_local 3
                    i32.const 256
                    i32.lt_u
                    if  ;; label = @9
                    get_local 1
                    i32.const 3
                    i32.shl
                    i32.const 1064
                    i32.add
                    set_local 0
                    i32.const 1024
                    i32.load
                    tee_local 3
                    i32.const 1
                    get_local 1
                    i32.shl
                    tee_local 1
                    i32.and
                    if (result i32)  ;; label = @10
                        get_local 0
                        i32.const 8
                        i32.add
                        tee_local 3
                        i32.load
                    else
                        i32.const 1024
                        get_local 3
                        get_local 1
                        i32.or
                        i32.store
                        get_local 0
                        i32.const 8
                        i32.add
                        set_local 3
                        get_local 0
                    end
                    set_local 1
                    get_local 3
                    get_local 8
                    i32.store
                    get_local 1
                    get_local 8
                    i32.store offset=12
                    get_local 8
                    get_local 1
                    i32.store offset=8
                    get_local 8
                    get_local 0
                    i32.store offset=12
                    br 2 (;@7;)
                    end
                    get_local 3
                    i32.const 8
                    i32.shr_u
                    tee_local 1
                    if (result i32)  ;; label = @9
                    get_local 3
                    i32.const 16777215
                    i32.gt_u
                    if (result i32)  ;; label = @10
                        i32.const 31
                    else
                        get_local 3
                        i32.const 14
                        get_local 1
                        get_local 1
                        i32.const 1048320
                        i32.add
                        i32.const 16
                        i32.shr_u
                        i32.const 8
                        i32.and
                        tee_local 1
                        i32.shl
                        tee_local 2
                        i32.const 520192
                        i32.add
                        i32.const 16
                        i32.shr_u
                        i32.const 4
                        i32.and
                        tee_local 5
                        get_local 1
                        i32.or
                        get_local 2
                        get_local 5
                        i32.shl
                        tee_local 1
                        i32.const 245760
                        i32.add
                        i32.const 16
                        i32.shr_u
                        i32.const 2
                        i32.and
                        tee_local 2
                        i32.or
                        i32.sub
                        get_local 1
                        get_local 2
                        i32.shl
                        i32.const 15
                        i32.shr_u
                        i32.add
                        tee_local 1
                        i32.const 7
                        i32.add
                        i32.shr_u
                        i32.const 1
                        i32.and
                        get_local 1
                        i32.const 1
                        i32.shl
                        i32.or
                    end
                    else
                    i32.const 0
                    end
                    tee_local 1
                    i32.const 2
                    i32.shl
                    i32.const 1328
                    i32.add
                    set_local 2
                    get_local 8
                    get_local 1
                    i32.store offset=28
                    get_local 8
                    i32.const 16
                    i32.add
                    tee_local 5
                    i32.const 0
                    i32.store offset=4
                    get_local 5
                    i32.const 0
                    i32.store
                    get_local 0
                    i32.const 1
                    get_local 1
                    i32.shl
                    tee_local 5
                    i32.and
                    i32.eqz
                    if  ;; label = @9
                    i32.const 1028
                    get_local 0
                    get_local 5
                    i32.or
                    i32.store
                    get_local 2
                    get_local 8
                    i32.store
                    get_local 8
                    get_local 2
                    i32.store offset=24
                    get_local 8
                    get_local 8
                    i32.store offset=12
                    get_local 8
                    get_local 8
                    i32.store offset=8
                    br 2 (;@7;)
                    end
                    get_local 2
                    i32.load
                    set_local 0
                    i32.const 25
                    get_local 1
                    i32.const 1
                    i32.shr_u
                    i32.sub
                    set_local 2
                    get_local 3
                    get_local 1
                    i32.const 31
                    i32.eq
                    if (result i32)  ;; label = @9
                    i32.const 0
                    else
                    get_local 2
                    end
                    i32.shl
                    set_local 1
                    block  ;; label = @9
                    loop  ;; label = @10
                        get_local 0
                        i32.load offset=4
                        i32.const -8
                        i32.and
                        get_local 3
                        i32.eq
                        br_if 1 (;@9;)
                        get_local 1
                        i32.const 1
                        i32.shl
                        set_local 2
                        get_local 0
                        i32.const 16
                        i32.add
                        get_local 1
                        i32.const 31
                        i32.shr_u
                        i32.const 2
                        i32.shl
                        i32.add
                        tee_local 1
                        i32.load
                        tee_local 5
                        if  ;; label = @11
                        get_local 2
                        set_local 1
                        get_local 5
                        set_local 0
                        br 1 (;@10;)
                        end
                    end
                    get_local 1
                    get_local 8
                    i32.store
                    get_local 8
                    get_local 0
                    i32.store offset=24
                    get_local 8
                    get_local 8
                    i32.store offset=12
                    get_local 8
                    get_local 8
                    i32.store offset=8
                    br 2 (;@7;)
                    end
                    get_local 0
                    i32.const 8
                    i32.add
                    tee_local 1
                    i32.load
                    tee_local 3
                    get_local 8
                    i32.store offset=12
                    get_local 1
                    get_local 8
                    i32.store
                    get_local 8
                    get_local 3
                    i32.store offset=8
                    get_local 8
                    get_local 0
                    i32.store offset=12
                    get_local 8
                    i32.const 0
                    i32.store offset=24
                end
                end
                get_local 10
                set_global  $STACKTOP
                get_local 4
                i32.const 8
                i32.add
                return
            else
                get_local 2
                set_local 0
            end
            else
            get_local 2
            set_local 0
            end
        else
            get_local 2
            set_local 0
        end
        end
    end
    end
    i32.const 1032
    i32.load
    tee_local 3
    get_local 0
    i32.ge_u
    if  ;; label = @1
        i32.const 1044
        i32.load
        set_local 1
        get_local 3
        get_local 0
        i32.sub
        tee_local 2
        i32.const 15
        i32.gt_u
        if  ;; label = @2
            i32.const 1044
            get_local 1
            get_local 0
            i32.add
            tee_local 5
            i32.store
            i32.const 1032
            get_local 2
            i32.store
            get_local 5
            get_local 2
            i32.const 1
            i32.or
            i32.store offset=4
            get_local 1
            get_local 3
            i32.add
            get_local 2
            i32.store
            get_local 1
            get_local 0
            i32.const 3
            i32.or
            i32.store offset=4
        else
            i32.const 1032
            i32.const 0
            i32.store
            i32.const 1044
            i32.const 0
            i32.store
            get_local 1
            get_local 3
            i32.const 3
            i32.or
            i32.store offset=4
            get_local 1
            get_local 3
            i32.add
            i32.const 4
            i32.add
            tee_local 0
            get_local 0
            i32.load
            i32.const 1
            i32.or
            i32.store
        end
        get_local 10
        set_global  $STACKTOP
        get_local 1
        i32.const 8
        i32.add
        return
    end
    i32.const 1036
    i32.load
    tee_local 3
    get_local 0
    i32.gt_u
    if  ;; label = @1
        i32.const 1036
        get_local 3
        get_local 0
        i32.sub
        tee_local 3
        i32.store
        i32.const 1048
        i32.const 1048
        i32.load
        tee_local 1
        get_local 0
        i32.add
        tee_local 2
        i32.store
        get_local 2
        get_local 3
        i32.const 1
        i32.or
        i32.store offset=4
        get_local 1
        get_local 0
        i32.const 3
        i32.or
        i32.store offset=4
        get_local 10
        set_global  $STACKTOP
        get_local 1
        i32.const 8
        i32.add
        return
    end
    i32.const 1496
    i32.load
    if (result i32)  ;; label = @1
        i32.const 1504
        i32.load
        else
        i32.const 1504
        i32.const 4096
        i32.store
        i32.const 1500
        i32.const 4096
        i32.store
        i32.const 1508
        i32.const -1
        i32.store
        i32.const 1512
        i32.const -1
        i32.store
        i32.const 1516
        i32.const 0
        i32.store
        i32.const 1468
        i32.const 0
        i32.store
        i32.const 1496
        get_local 8
        i32.const -16
        i32.and
        i32.const 1431655768
        i32.xor
        i32.store
        i32.const 4096
    end
    tee_local 1
    get_local 0
    i32.const 47
    i32.add
    tee_local 4
    i32.add
    tee_local 7
    i32.const 0
    get_local 1
    i32.sub
    tee_local 6
    i32.and
    tee_local 5
    get_local 0
    i32.le_u
    if  ;; label = @1
    get_local 10
    set_global  $STACKTOP
    i32.const 0
    return
    end
    i32.const 1464
    i32.load
    tee_local 1
    if  ;; label = @1
    i32.const 1456
    i32.load
    tee_local 2
    get_local 5
    i32.add
    tee_local 8
    get_local 2
    i32.le_u
    get_local 8
    get_local 1
    i32.gt_u
    i32.or
    if  ;; label = @2
        get_local 10
        set_global  $STACKTOP
        i32.const 0
        return
    end
    end
    get_local 0
    i32.const 48
    i32.add
    set_local 8
    block  ;; label = @1
    block  ;; label = @2
        i32.const 1468
        i32.load
        i32.const 4
        i32.and
        if  ;; label = @3
        i32.const 0
        set_local 3
        else
        block  ;; label = @4
            block  ;; label = @5
            block  ;; label = @6
                i32.const 1048
                i32.load
                tee_local 1
                i32.eqz
                br_if 0 (;@6;)
                i32.const 1472
                set_local 2
                loop  ;; label = @7
                block  ;; label = @8
                    get_local 2
                    i32.load
                    tee_local 9
                    get_local 1
                    i32.le_u
                    if  ;; label = @9
                    get_local 9
                    get_local 2
                    i32.const 4
                    i32.add
                    tee_local 9
                    i32.load
                    i32.add
                    get_local 1
                    i32.gt_u
                    br_if 1 (;@8;)
                    end
                    get_local 2
                    i32.load offset=8
                    tee_local 2
                    br_if 1 (;@7;)
                    br 2 (;@6;)
                end
                end
                get_local 7
                get_local 3
                i32.sub
                get_local 6
                i32.and
                tee_local 3
                i32.const 2147483647
                i32.lt_u
                if  ;; label = @7
                get_local 3
                call $sbrk
                tee_local 1
                get_local 2
                i32.load
                get_local 9
                i32.load
                i32.add
                i32.eq
                if  ;; label = @8
                    get_local 1
                    i32.const -1
                    i32.ne
                    br_if 6 (;@2;)
                else
                    br 3 (;@5;)
                end
                else
                i32.const 0
                set_local 3
                end
                br 2 (;@4;)
            end
            i32.const 0
            call $sbrk
            tee_local 1
            i32.const -1
            i32.eq
            if  ;; label = @6
                i32.const 0
                set_local 3
            else
                i32.const 1500
                i32.load
                tee_local 2
                i32.const -1
                i32.add
                tee_local 7
                get_local 1
                tee_local 3
                i32.add
                i32.const 0
                get_local 2
                i32.sub
                i32.and
                get_local 3
                i32.sub
                set_local 2
                get_local 7
                get_local 3
                i32.and
                if (result i32)  ;; label = @7
                get_local 2
                else
                i32.const 0
                end
                get_local 5
                i32.add
                tee_local 3
                i32.const 1456
                i32.load
                tee_local 7
                i32.add
                set_local 2
                get_local 3
                get_local 0
                i32.gt_u
                get_local 3
                i32.const 2147483647
                i32.lt_u
                i32.and
                if  ;; label = @7
                i32.const 1464
                i32.load
                tee_local 6
                if  ;; label = @8
                    get_local 2
                    get_local 7
                    i32.le_u
                    get_local 2
                    get_local 6
                    i32.gt_u
                    i32.or
                    if  ;; label = @9
                    i32.const 0
                    set_local 3
                    br 5 (;@4;)
                    end
                end
                get_local 3
                call $sbrk
                tee_local 2
                get_local 1
                i32.eq
                br_if 5 (;@2;)
                get_local 2
                set_local 1
                br 2 (;@5;)
                else
                i32.const 0
                set_local 3
                end
            end
            br 1 (;@4;)
            end
            get_local 8
            get_local 3
            i32.gt_u
            get_local 3
            i32.const 2147483647
            i32.lt_u
            get_local 1
            i32.const -1
            i32.ne
            i32.and
            i32.and
            i32.eqz
            if  ;; label = @5
            get_local 1
            i32.const -1
            i32.eq
            if  ;; label = @6
                i32.const 0
                set_local 3
                br 2 (;@4;)
            else
                br 4 (;@2;)
            end
            unreachable
            end
            get_local 4
            get_local 3
            i32.sub
            i32.const 1504
            i32.load
            tee_local 2
            i32.add
            i32.const 0
            get_local 2
            i32.sub
            i32.and
            tee_local 2
            i32.const 2147483647
            i32.ge_u
            br_if 2 (;@2;)
            i32.const 0
            get_local 3
            i32.sub
            set_local 4
            get_local 2
            call $sbrk
            i32.const -1
            i32.eq
            if  ;; label = @5
            get_local 4
            call $sbrk
            drop
            i32.const 0
            set_local 3
            else
            get_local 2
            get_local 3
            i32.add
            set_local 3
            br 3 (;@2;)
            end
        end
        i32.const 1468
        i32.const 1468
        i32.load
        i32.const 4
        i32.or
        i32.store
        end
        get_local 5
        i32.const 2147483647
        i32.lt_u
        if  ;; label = @3
        get_local 5
        call $sbrk
        tee_local 1
        i32.const 0
        call $sbrk
        tee_local 2
        i32.lt_u
        get_local 1
        i32.const -1
        i32.ne
        get_local 2
        i32.const -1
        i32.ne
        i32.and
        i32.and
        set_local 5
        get_local 2
        get_local 1
        i32.sub
        tee_local 2
        get_local 0
        i32.const 40
        i32.add
        i32.gt_u
        tee_local 4
        if  ;; label = @4
            get_local 2
            set_local 3
        end
        get_local 1
        i32.const -1
        i32.eq
        get_local 4
        i32.const 1
        i32.xor
        i32.or
        get_local 5
        i32.const 1
        i32.xor
        i32.or
        i32.eqz
        br_if 1 (;@2;)
        end
        br 1 (;@1;)
    end
    i32.const 1456
    i32.const 1456
    i32.load
    get_local 3
    i32.add
    tee_local 2
    i32.store
    get_local 2
    i32.const 1460
    i32.load
    i32.gt_u
    if  ;; label = @2
        i32.const 1460
        get_local 2
        i32.store
    end
    block  ;; label = @2
        i32.const 1048
        i32.load
        tee_local 4
        if  ;; label = @3
        i32.const 1472
        set_local 2
        block  ;; label = @4
            block  ;; label = @5
            loop  ;; label = @6
                get_local 1
                get_local 2
                i32.load
                tee_local 5
                get_local 2
                i32.const 4
                i32.add
                tee_local 7
                i32.load
                tee_local 6
                i32.add
                i32.eq
                br_if 1 (;@5;)
                get_local 2
                i32.load offset=8
                tee_local 2
                br_if 0 (;@6;)
            end
            br 1 (;@4;)
            end
            get_local 2
            i32.load offset=12
            i32.const 8
            i32.and
            i32.eqz
            if  ;; label = @5
            get_local 1
            get_local 4
            i32.gt_u
            get_local 5
            get_local 4
            i32.le_u
            i32.and
            if  ;; label = @6
                get_local 7
                get_local 6
                get_local 3
                i32.add
                i32.store
                i32.const 1036
                i32.load
                get_local 3
                i32.add
                set_local 3
                i32.const 0
                get_local 4
                i32.const 8
                i32.add
                tee_local 2
                i32.sub
                i32.const 7
                i32.and
                set_local 1
                i32.const 1048
                get_local 4
                get_local 2
                i32.const 7
                i32.and
                if (result i32)  ;; label = @7
                get_local 1
                else
                i32.const 0
                tee_local 1
                end
                i32.add
                tee_local 2
                i32.store
                i32.const 1036
                get_local 3
                get_local 1
                i32.sub
                tee_local 1
                i32.store
                get_local 2
                get_local 1
                i32.const 1
                i32.or
                i32.store offset=4
                get_local 4
                get_local 3
                i32.add
                i32.const 40
                i32.store offset=4
                i32.const 1052
                i32.const 1512
                i32.load
                i32.store
                br 4 (;@2;)
            end
            end
        end
        get_local 1
        i32.const 1040
        i32.load
        i32.lt_u
        if  ;; label = @4
            i32.const 1040
            get_local 1
            i32.store
        end
        get_local 1
        get_local 3
        i32.add
        set_local 5
        i32.const 1472
        set_local 2
        block  ;; label = @4
            block  ;; label = @5
            loop  ;; label = @6
                get_local 2
                i32.load
                get_local 5
                i32.eq
                br_if 1 (;@5;)
                get_local 2
                i32.load offset=8
                tee_local 2
                br_if 0 (;@6;)
                i32.const 1472
                set_local 2
            end
            br 1 (;@4;)
            end
            get_local 2
            i32.load offset=12
            i32.const 8
            i32.and
            if  ;; label = @5
            i32.const 1472
            set_local 2
            else
            get_local 2
            get_local 1
            i32.store
            get_local 2
            i32.const 4
            i32.add
            tee_local 2
            get_local 2
            i32.load
            get_local 3
            i32.add
            i32.store
            i32.const 0
            get_local 1
            i32.const 8
            i32.add
            tee_local 3
            i32.sub
            i32.const 7
            i32.and
            set_local 2
            i32.const 0
            get_local 5
            i32.const 8
            i32.add
            tee_local 7
            i32.sub
            i32.const 7
            i32.and
            set_local 9
            get_local 1
            get_local 3
            i32.const 7
            i32.and
            if (result i32)  ;; label = @6
                get_local 2
            else
                i32.const 0
            end
            i32.add
            tee_local 8
            get_local 0
            i32.add
            set_local 6
            get_local 5
            get_local 7
            i32.const 7
            i32.and
            if (result i32)  ;; label = @6
                get_local 9
            else
                i32.const 0
            end
            i32.add
            tee_local 5
            get_local 8
            i32.sub
            get_local 0
            i32.sub
            set_local 7
            get_local 8
            get_local 0
            i32.const 3
            i32.or
            i32.store offset=4
            block  ;; label = @6
                get_local 4
                get_local 5
                i32.eq
                if  ;; label = @7
                i32.const 1036
                i32.const 1036
                i32.load
                get_local 7
                i32.add
                tee_local 0
                i32.store
                i32.const 1048
                get_local 6
                i32.store
                get_local 6
                get_local 0
                i32.const 1
                i32.or
                i32.store offset=4
                else
                i32.const 1044
                i32.load
                get_local 5
                i32.eq
                if  ;; label = @8
                    i32.const 1032
                    i32.const 1032
                    i32.load
                    get_local 7
                    i32.add
                    tee_local 0
                    i32.store
                    i32.const 1044
                    get_local 6
                    i32.store
                    get_local 6
                    get_local 0
                    i32.const 1
                    i32.or
                    i32.store offset=4
                    get_local 6
                    get_local 0
                    i32.add
                    get_local 0
                    i32.store
                    br 2 (;@6;)
                end
                get_local 5
                i32.load offset=4
                tee_local 0
                i32.const 3
                i32.and
                i32.const 1
                i32.eq
                if (result i32)  ;; label = @8
                    get_local 0
                    i32.const -8
                    i32.and
                    set_local 9
                    get_local 0
                    i32.const 3
                    i32.shr_u
                    set_local 3
                    block  ;; label = @9
                    get_local 0
                    i32.const 256
                    i32.lt_u
                    if  ;; label = @10
                        get_local 5
                        i32.load offset=12
                        tee_local 0
                        get_local 5
                        i32.load offset=8
                        tee_local 1
                        i32.eq
                        if  ;; label = @11
                        i32.const 1024
                        i32.const 1024
                        i32.load
                        i32.const 1
                        get_local 3
                        i32.shl
                        i32.const -1
                        i32.xor
                        i32.and
                        i32.store
                        else
                        get_local 1
                        get_local 0
                        i32.store offset=12
                        get_local 0
                        get_local 1
                        i32.store offset=8
                        end
                    else
                        get_local 5
                        i32.load offset=24
                        set_local 4
                        block  ;; label = @11
                        get_local 5
                        i32.load offset=12
                        tee_local 0
                        get_local 5
                        i32.eq
                        if  ;; label = @12
                            get_local 5
                            i32.const 16
                            i32.add
                            tee_local 1
                            i32.const 4
                            i32.add
                            tee_local 3
                            i32.load
                            tee_local 0
                            if  ;; label = @13
                            get_local 3
                            set_local 1
                            else
                            get_local 1
                            i32.load
                            tee_local 0
                            i32.eqz
                            if  ;; label = @14
                                i32.const 0
                                set_local 0
                                br 3 (;@11;)
                            end
                            end
                            loop  ;; label = @13
                            get_local 0
                            i32.const 20
                            i32.add
                            tee_local 3
                            i32.load
                            tee_local 2
                            if  ;; label = @14
                                get_local 2
                                set_local 0
                                get_local 3
                                set_local 1
                                br 1 (;@13;)
                            end
                            get_local 0
                            i32.const 16
                            i32.add
                            tee_local 3
                            i32.load
                            tee_local 2
                            if  ;; label = @14
                                get_local 2
                                set_local 0
                                get_local 3
                                set_local 1
                                br 1 (;@13;)
                            end
                            end
                            get_local 1
                            i32.const 0
                            i32.store
                        else
                            get_local 5
                            i32.load offset=8
                            tee_local 1
                            get_local 0
                            i32.store offset=12
                            get_local 0
                            get_local 1
                            i32.store offset=8
                        end
                        end
                        get_local 4
                        i32.eqz
                        br_if 1 (;@9;)
                        block  ;; label = @11
                        get_local 5
                        i32.load offset=28
                        tee_local 1
                        i32.const 2
                        i32.shl
                        i32.const 1328
                        i32.add
                        tee_local 3
                        i32.load
                        get_local 5
                        i32.eq
                        if  ;; label = @12
                            get_local 3
                            get_local 0
                            i32.store
                            get_local 0
                            br_if 1 (;@11;)
                            i32.const 1028
                            i32.const 1028
                            i32.load
                            i32.const 1
                            get_local 1
                            i32.shl
                            i32.const -1
                            i32.xor
                            i32.and
                            i32.store
                            br 3 (;@9;)
                        else
                            get_local 4
                            i32.const 16
                            i32.add
                            get_local 4
                            i32.load offset=16
                            get_local 5
                            i32.ne
                            i32.const 2
                            i32.shl
                            i32.add
                            get_local 0
                            i32.store
                            get_local 0
                            i32.eqz
                            br_if 3 (;@9;)
                        end
                        end
                        get_local 0
                        get_local 4
                        i32.store offset=24
                        get_local 5
                        i32.const 16
                        i32.add
                        tee_local 3
                        i32.load
                        tee_local 1
                        if  ;; label = @11
                        get_local 0
                        get_local 1
                        i32.store offset=16
                        get_local 1
                        get_local 0
                        i32.store offset=24
                        end
                        get_local 3
                        i32.load offset=4
                        tee_local 1
                        i32.eqz
                        br_if 1 (;@9;)
                        get_local 0
                        get_local 1
                        i32.store offset=20
                        get_local 1
                        get_local 0
                        i32.store offset=24
                    end
                    end
                    get_local 5
                    get_local 9
                    i32.add
                    set_local 0
                    get_local 9
                    get_local 7
                    i32.add
                else
                    get_local 5
                    set_local 0
                    get_local 7
                end
                set_local 5
                get_local 0
                i32.const 4
                i32.add
                tee_local 0
                get_local 0
                i32.load
                i32.const -2
                i32.and
                i32.store
                get_local 6
                get_local 5
                i32.const 1
                i32.or
                i32.store offset=4
                get_local 6
                get_local 5
                i32.add
                get_local 5
                i32.store
                get_local 5
                i32.const 3
                i32.shr_u
                set_local 1
                get_local 5
                i32.const 256
                i32.lt_u
                if  ;; label = @8
                    get_local 1
                    i32.const 3
                    i32.shl
                    i32.const 1064
                    i32.add
                    set_local 0
                    i32.const 1024
                    i32.load
                    tee_local 3
                    i32.const 1
                    get_local 1
                    i32.shl
                    tee_local 1
                    i32.and
                    if (result i32)  ;; label = @9
                    get_local 0
                    i32.const 8
                    i32.add
                    tee_local 3
                    i32.load
                    else
                    i32.const 1024
                    get_local 3
                    get_local 1
                    i32.or
                    i32.store
                    get_local 0
                    i32.const 8
                    i32.add
                    set_local 3
                    get_local 0
                    end
                    set_local 1
                    get_local 3
                    get_local 6
                    i32.store
                    get_local 1
                    get_local 6
                    i32.store offset=12
                    get_local 6
                    get_local 1
                    i32.store offset=8
                    get_local 6
                    get_local 0
                    i32.store offset=12
                    br 2 (;@6;)
                end
                block (result i32)  ;; label = @8
                    get_local 5
                    i32.const 8
                    i32.shr_u
                    tee_local 0
                    if (result i32)  ;; label = @9
                    i32.const 31
                    get_local 5
                    i32.const 16777215
                    i32.gt_u
                    br_if 1 (;@8;)
                    drop
                    get_local 5
                    i32.const 14
                    get_local 0
                    get_local 0
                    i32.const 1048320
                    i32.add
                    i32.const 16
                    i32.shr_u
                    i32.const 8
                    i32.and
                    tee_local 0
                    i32.shl
                    tee_local 1
                    i32.const 520192
                    i32.add
                    i32.const 16
                    i32.shr_u
                    i32.const 4
                    i32.and
                    tee_local 3
                    get_local 0
                    i32.or
                    get_local 1
                    get_local 3
                    i32.shl
                    tee_local 0
                    i32.const 245760
                    i32.add
                    i32.const 16
                    i32.shr_u
                    i32.const 2
                    i32.and
                    tee_local 1
                    i32.or
                    i32.sub
                    get_local 0
                    get_local 1
                    i32.shl
                    i32.const 15
                    i32.shr_u
                    i32.add
                    tee_local 0
                    i32.const 7
                    i32.add
                    i32.shr_u
                    i32.const 1
                    i32.and
                    get_local 0
                    i32.const 1
                    i32.shl
                    i32.or
                    else
                    i32.const 0
                    end
                end
                tee_local 1
                i32.const 2
                i32.shl
                i32.const 1328
                i32.add
                set_local 0
                get_local 6
                get_local 1
                i32.store offset=28
                get_local 6
                i32.const 16
                i32.add
                tee_local 3
                i32.const 0
                i32.store offset=4
                get_local 3
                i32.const 0
                i32.store
                i32.const 1028
                i32.load
                tee_local 3
                i32.const 1
                get_local 1
                i32.shl
                tee_local 2
                i32.and
                i32.eqz
                if  ;; label = @8
                    i32.const 1028
                    get_local 3
                    get_local 2
                    i32.or
                    i32.store
                    get_local 0
                    get_local 6
                    i32.store
                    get_local 6
                    get_local 0
                    i32.store offset=24
                    get_local 6
                    get_local 6
                    i32.store offset=12
                    get_local 6
                    get_local 6
                    i32.store offset=8
                    br 2 (;@6;)
                end
                get_local 0
                i32.load
                set_local 0
                i32.const 25
                get_local 1
                i32.const 1
                i32.shr_u
                i32.sub
                set_local 3
                get_local 5
                get_local 1
                i32.const 31
                i32.eq
                if (result i32)  ;; label = @8
                    i32.const 0
                else
                    get_local 3
                end
                i32.shl
                set_local 1
                block  ;; label = @8
                    loop  ;; label = @9
                    get_local 0
                    i32.load offset=4
                    i32.const -8
                    i32.and
                    get_local 5
                    i32.eq
                    br_if 1 (;@8;)
                    get_local 1
                    i32.const 1
                    i32.shl
                    set_local 3
                    get_local 0
                    i32.const 16
                    i32.add
                    get_local 1
                    i32.const 31
                    i32.shr_u
                    i32.const 2
                    i32.shl
                    i32.add
                    tee_local 1
                    i32.load
                    tee_local 2
                    if  ;; label = @10
                        get_local 3
                        set_local 1
                        get_local 2
                        set_local 0
                        br 1 (;@9;)
                    end
                    end
                    get_local 1
                    get_local 6
                    i32.store
                    get_local 6
                    get_local 0
                    i32.store offset=24
                    get_local 6
                    get_local 6
                    i32.store offset=12
                    get_local 6
                    get_local 6
                    i32.store offset=8
                    br 2 (;@6;)
                end
                get_local 0
                i32.const 8
                i32.add
                tee_local 1
                i32.load
                tee_local 3
                get_local 6
                i32.store offset=12
                get_local 1
                get_local 6
                i32.store
                get_local 6
                get_local 3
                i32.store offset=8
                get_local 6
                get_local 0
                i32.store offset=12
                get_local 6
                i32.const 0
                i32.store offset=24
                end
            end
            get_local 10
            set_global  $STACKTOP
            get_local 8
            i32.const 8
            i32.add
            return
            end
        end
        loop  ;; label = @4
            block  ;; label = @5
            get_local 2
            i32.load
            tee_local 5
            get_local 4
            i32.le_u
            if  ;; label = @6
                get_local 5
                get_local 2
                i32.load offset=4
                i32.add
                tee_local 8
                get_local 4
                i32.gt_u
                br_if 1 (;@5;)
            end
            get_local 2
            i32.load offset=8
            set_local 2
            br 1 (;@4;)
            end
        end
        i32.const 0
        get_local 8
        i32.const -47
        i32.add
        tee_local 2
        i32.const 8
        i32.add
        tee_local 5
        i32.sub
        i32.const 7
        i32.and
        set_local 7
        get_local 2
        get_local 5
        i32.const 7
        i32.and
        if (result i32)  ;; label = @4
            get_local 7
        else
            i32.const 0
        end
        i32.add
        tee_local 2
        get_local 4
        i32.const 16
        i32.add
        tee_local 12
        i32.lt_u
        if (result i32)  ;; label = @4
            get_local 4
            tee_local 2
        else
            get_local 2
        end
        i32.const 8
        i32.add
        set_local 6
        get_local 2
        i32.const 24
        i32.add
        set_local 5
        get_local 3
        i32.const -40
        i32.add
        set_local 9
        i32.const 0
        get_local 1
        i32.const 8
        i32.add
        tee_local 11
        i32.sub
        i32.const 7
        i32.and
        set_local 7
        i32.const 1048
        get_local 1
        get_local 11
        i32.const 7
        i32.and
        if (result i32)  ;; label = @4
            get_local 7
        else
            i32.const 0
            tee_local 7
        end
        i32.add
        tee_local 11
        i32.store
        i32.const 1036
        get_local 9
        get_local 7
        i32.sub
        tee_local 7
        i32.store
        get_local 11
        get_local 7
        i32.const 1
        i32.or
        i32.store offset=4
        get_local 1
        get_local 9
        i32.add
        i32.const 40
        i32.store offset=4
        i32.const 1052
        i32.const 1512
        i32.load
        i32.store
        get_local 2
        i32.const 4
        i32.add
        tee_local 7
        i32.const 27
        i32.store
        get_local 6
        i32.const 1472
        i64.load align=4
        i64.store align=4
        get_local 6
        i32.const 1480
        i64.load align=4
        i64.store offset=8 align=4
        i32.const 1472
        get_local 1
        i32.store
        i32.const 1476
        get_local 3
        i32.store
        i32.const 1484
        i32.const 0
        i32.store
        i32.const 1480
        get_local 6
        i32.store
        get_local 5
        set_local 1
        loop  ;; label = @4
            get_local 1
            i32.const 4
            i32.add
            tee_local 3
            i32.const 7
            i32.store
            get_local 1
            i32.const 8
            i32.add
            get_local 8
            i32.lt_u
            if  ;; label = @5
            get_local 3
            set_local 1
            br 1 (;@4;)
            end
        end
        get_local 2
        get_local 4
        i32.ne
        if  ;; label = @4
            get_local 7
            get_local 7
            i32.load
            i32.const -2
            i32.and
            i32.store
            get_local 4
            get_local 2
            get_local 4
            i32.sub
            tee_local 7
            i32.const 1
            i32.or
            i32.store offset=4
            get_local 2
            get_local 7
            i32.store
            get_local 7
            i32.const 3
            i32.shr_u
            set_local 3
            get_local 7
            i32.const 256
            i32.lt_u
            if  ;; label = @5
            get_local 3
            i32.const 3
            i32.shl
            i32.const 1064
            i32.add
            set_local 1
            i32.const 1024
            i32.load
            tee_local 2
            i32.const 1
            get_local 3
            i32.shl
            tee_local 3
            i32.and
            if (result i32)  ;; label = @6
                get_local 1
                i32.const 8
                i32.add
                tee_local 2
                i32.load
            else
                i32.const 1024
                get_local 2
                get_local 3
                i32.or
                i32.store
                get_local 1
                i32.const 8
                i32.add
                set_local 2
                get_local 1
            end
            set_local 3
            get_local 2
            get_local 4
            i32.store
            get_local 3
            get_local 4
            i32.store offset=12
            get_local 4
            get_local 3
            i32.store offset=8
            get_local 4
            get_local 1
            i32.store offset=12
            br 3 (;@2;)
            end
            get_local 7
            i32.const 8
            i32.shr_u
            tee_local 1
            if (result i32)  ;; label = @5
            get_local 7
            i32.const 16777215
            i32.gt_u
            if (result i32)  ;; label = @6
                i32.const 31
            else
                get_local 7
                i32.const 14
                get_local 1
                get_local 1
                i32.const 1048320
                i32.add
                i32.const 16
                i32.shr_u
                i32.const 8
                i32.and
                tee_local 1
                i32.shl
                tee_local 3
                i32.const 520192
                i32.add
                i32.const 16
                i32.shr_u
                i32.const 4
                i32.and
                tee_local 2
                get_local 1
                i32.or
                get_local 3
                get_local 2
                i32.shl
                tee_local 1
                i32.const 245760
                i32.add
                i32.const 16
                i32.shr_u
                i32.const 2
                i32.and
                tee_local 3
                i32.or
                i32.sub
                get_local 1
                get_local 3
                i32.shl
                i32.const 15
                i32.shr_u
                i32.add
                tee_local 1
                i32.const 7
                i32.add
                i32.shr_u
                i32.const 1
                i32.and
                get_local 1
                i32.const 1
                i32.shl
                i32.or
            end
            else
            i32.const 0
            end
            tee_local 3
            i32.const 2
            i32.shl
            i32.const 1328
            i32.add
            set_local 1
            get_local 4
            get_local 3
            i32.store offset=28
            get_local 4
            i32.const 0
            i32.store offset=20
            get_local 12
            i32.const 0
            i32.store
            i32.const 1028
            i32.load
            tee_local 2
            i32.const 1
            get_local 3
            i32.shl
            tee_local 5
            i32.and
            i32.eqz
            if  ;; label = @5
            i32.const 1028
            get_local 2
            get_local 5
            i32.or
            i32.store
            get_local 1
            get_local 4
            i32.store
            get_local 4
            get_local 1
            i32.store offset=24
            get_local 4
            get_local 4
            i32.store offset=12
            get_local 4
            get_local 4
            i32.store offset=8
            br 3 (;@2;)
            end
            get_local 1
            i32.load
            set_local 1
            i32.const 25
            get_local 3
            i32.const 1
            i32.shr_u
            i32.sub
            set_local 2
            get_local 7
            get_local 3
            i32.const 31
            i32.eq
            if (result i32)  ;; label = @5
            i32.const 0
            else
            get_local 2
            end
            i32.shl
            set_local 3
            block  ;; label = @5
            loop  ;; label = @6
                get_local 1
                i32.load offset=4
                i32.const -8
                i32.and
                get_local 7
                i32.eq
                br_if 1 (;@5;)
                get_local 3
                i32.const 1
                i32.shl
                set_local 2
                get_local 1
                i32.const 16
                i32.add
                get_local 3
                i32.const 31
                i32.shr_u
                i32.const 2
                i32.shl
                i32.add
                tee_local 3
                i32.load
                tee_local 5
                if  ;; label = @7
                get_local 2
                set_local 3
                get_local 5
                set_local 1
                br 1 (;@6;)
                end
            end
            get_local 3
            get_local 4
            i32.store
            get_local 4
            get_local 1
            i32.store offset=24
            get_local 4
            get_local 4
            i32.store offset=12
            get_local 4
            get_local 4
            i32.store offset=8
            br 3 (;@2;)
            end
            get_local 1
            i32.const 8
            i32.add
            tee_local 3
            i32.load
            tee_local 2
            get_local 4
            i32.store offset=12
            get_local 3
            get_local 4
            i32.store
            get_local 4
            get_local 2
            i32.store offset=8
            get_local 4
            get_local 1
            i32.store offset=12
            get_local 4
            i32.const 0
            i32.store offset=24
        end
        else
        i32.const 1040
        i32.load
        tee_local 2
        i32.eqz
        get_local 1
        get_local 2
        i32.lt_u
        i32.or
        if  ;; label = @4
            i32.const 1040
            get_local 1
            i32.store
        end
        i32.const 1472
        get_local 1
        i32.store
        i32.const 1476
        get_local 3
        i32.store
        i32.const 1484
        i32.const 0
        i32.store
        i32.const 1060
        i32.const 1496
        i32.load
        i32.store
        i32.const 1056
        i32.const -1
        i32.store
        i32.const 1076
        i32.const 1064
        i32.store
        i32.const 1072
        i32.const 1064
        i32.store
        i32.const 1084
        i32.const 1072
        i32.store
        i32.const 1080
        i32.const 1072
        i32.store
        i32.const 1092
        i32.const 1080
        i32.store
        i32.const 1088
        i32.const 1080
        i32.store
        i32.const 1100
        i32.const 1088
        i32.store
        i32.const 1096
        i32.const 1088
        i32.store
        i32.const 1108
        i32.const 1096
        i32.store
        i32.const 1104
        i32.const 1096
        i32.store
        i32.const 1116
        i32.const 1104
        i32.store
        i32.const 1112
        i32.const 1104
        i32.store
        i32.const 1124
        i32.const 1112
        i32.store
        i32.const 1120
        i32.const 1112
        i32.store
        i32.const 1132
        i32.const 1120
        i32.store
        i32.const 1128
        i32.const 1120
        i32.store
        i32.const 1140
        i32.const 1128
        i32.store
        i32.const 1136
        i32.const 1128
        i32.store
        i32.const 1148
        i32.const 1136
        i32.store
        i32.const 1144
        i32.const 1136
        i32.store
        i32.const 1156
        i32.const 1144
        i32.store
        i32.const 1152
        i32.const 1144
        i32.store
        i32.const 1164
        i32.const 1152
        i32.store
        i32.const 1160
        i32.const 1152
        i32.store
        i32.const 1172
        i32.const 1160
        i32.store
        i32.const 1168
        i32.const 1160
        i32.store
        i32.const 1180
        i32.const 1168
        i32.store
        i32.const 1176
        i32.const 1168
        i32.store
        i32.const 1188
        i32.const 1176
        i32.store
        i32.const 1184
        i32.const 1176
        i32.store
        i32.const 1196
        i32.const 1184
        i32.store
        i32.const 1192
        i32.const 1184
        i32.store
        i32.const 1204
        i32.const 1192
        i32.store
        i32.const 1200
        i32.const 1192
        i32.store
        i32.const 1212
        i32.const 1200
        i32.store
        i32.const 1208
        i32.const 1200
        i32.store
        i32.const 1220
        i32.const 1208
        i32.store
        i32.const 1216
        i32.const 1208
        i32.store
        i32.const 1228
        i32.const 1216
        i32.store
        i32.const 1224
        i32.const 1216
        i32.store
        i32.const 1236
        i32.const 1224
        i32.store
        i32.const 1232
        i32.const 1224
        i32.store
        i32.const 1244
        i32.const 1232
        i32.store
        i32.const 1240
        i32.const 1232
        i32.store
        i32.const 1252
        i32.const 1240
        i32.store
        i32.const 1248
        i32.const 1240
        i32.store
        i32.const 1260
        i32.const 1248
        i32.store
        i32.const 1256
        i32.const 1248
        i32.store
        i32.const 1268
        i32.const 1256
        i32.store
        i32.const 1264
        i32.const 1256
        i32.store
        i32.const 1276
        i32.const 1264
        i32.store
        i32.const 1272
        i32.const 1264
        i32.store
        i32.const 1284
        i32.const 1272
        i32.store
        i32.const 1280
        i32.const 1272
        i32.store
        i32.const 1292
        i32.const 1280
        i32.store
        i32.const 1288
        i32.const 1280
        i32.store
        i32.const 1300
        i32.const 1288
        i32.store
        i32.const 1296
        i32.const 1288
        i32.store
        i32.const 1308
        i32.const 1296
        i32.store
        i32.const 1304
        i32.const 1296
        i32.store
        i32.const 1316
        i32.const 1304
        i32.store
        i32.const 1312
        i32.const 1304
        i32.store
        i32.const 1324
        i32.const 1312
        i32.store
        i32.const 1320
        i32.const 1312
        i32.store
        get_local 3
        i32.const -40
        i32.add
        set_local 2
        i32.const 0
        get_local 1
        i32.const 8
        i32.add
        tee_local 5
        i32.sub
        i32.const 7
        i32.and
        set_local 3
        i32.const 1048
        get_local 1
        get_local 5
        i32.const 7
        i32.and
        if (result i32)  ;; label = @4
            get_local 3
        else
            i32.const 0
            tee_local 3
        end
        i32.add
        tee_local 5
        i32.store
        i32.const 1036
        get_local 2
        get_local 3
        i32.sub
        tee_local 3
        i32.store
        get_local 5
        get_local 3
        i32.const 1
        i32.or
        i32.store offset=4
        get_local 1
        get_local 2
        i32.add
        i32.const 40
        i32.store offset=4
        i32.const 1052
        i32.const 1512
        i32.load
        i32.store
        end
    end
    i32.const 1036
    i32.load
    tee_local 1
    get_local 0
    i32.gt_u
    if  ;; label = @2
        i32.const 1036
        get_local 1
        get_local 0
        i32.sub
        tee_local 3
        i32.store
        i32.const 1048
        i32.const 1048
        i32.load
        tee_local 1
        get_local 0
        i32.add
        tee_local 2
        i32.store
        get_local 2
        get_local 3
        i32.const 1
        i32.or
        i32.store offset=4
        get_local 1
        get_local 0
        i32.const 3
        i32.or
        i32.store offset=4
        get_local 10
        set_global  $STACKTOP
        get_local 1
        i32.const 8
        i32.add
        return
    end
    end
    i32.const 1520
    i32.const 12
    i32.store
    get_local 10
    set_global  $STACKTOP
    i32.const 0)
(func $free (type $3) (param i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32)
    ;; %gc_free 
    get_local 0
    i32.eqz
    if  ;; label = @1
    return
    end
    i32.const 1040
    i32.load
    set_local 4
    get_local 0
    i32.const -8
    i32.add
    tee_local 1
    get_local 0
    i32.const -4
    i32.add
    i32.load
    tee_local 0
    i32.const -8
    i32.and
    tee_local 3
    i32.add
    set_local 5
    block (result i32)  ;; label = @1
    get_local 0
    i32.const 1
    i32.and
    if (result i32)  ;; label = @2
        get_local 1
        set_local 0
        get_local 1
    else
        get_local 1
        i32.load
        set_local 2
        get_local 0
        i32.const 3
        i32.and
        i32.eqz
        if  ;; label = @3
        return
        end
        get_local 1
        get_local 2
        i32.sub
        tee_local 0
        get_local 4
        i32.lt_u
        if  ;; label = @3
        return
        end
        get_local 2
        get_local 3
        i32.add
        set_local 3
        i32.const 1044
        i32.load
        get_local 0
        i32.eq
        if  ;; label = @3
        get_local 0
        get_local 5
        i32.const 4
        i32.add
        tee_local 2
        i32.load
        tee_local 1
        i32.const 3
        i32.and
        i32.const 3
        i32.ne
        br_if 2 (;@1;)
        drop
        i32.const 1032
        get_local 3
        i32.store
        get_local 2
        get_local 1
        i32.const -2
        i32.and
        i32.store
        get_local 0
        get_local 3
        i32.const 1
        i32.or
        i32.store offset=4
        get_local 0
        get_local 3
        i32.add
        get_local 3
        i32.store
        return
        end
        get_local 2
        i32.const 3
        i32.shr_u
        set_local 4
        get_local 2
        i32.const 256
        i32.lt_u
        if  ;; label = @3
        get_local 0
        i32.load offset=12
        tee_local 2
        get_local 0
        i32.load offset=8
        tee_local 1
        i32.eq
        if  ;; label = @4
            i32.const 1024
            i32.const 1024
            i32.load
            i32.const 1
            get_local 4
            i32.shl
            i32.const -1
            i32.xor
            i32.and
            i32.store
            get_local 0
            br 3 (;@1;)
        else
            get_local 1
            get_local 2
            i32.store offset=12
            get_local 2
            get_local 1
            i32.store offset=8
            get_local 0
            br 3 (;@1;)
        end
        unreachable
        end
        get_local 0
        i32.load offset=24
        set_local 7
        block  ;; label = @3
        get_local 0
        i32.load offset=12
        tee_local 2
        get_local 0
        i32.eq
        if  ;; label = @4
            get_local 0
            i32.const 16
            i32.add
            tee_local 1
            i32.const 4
            i32.add
            tee_local 4
            i32.load
            tee_local 2
            if  ;; label = @5
            get_local 4
            set_local 1
            else
            get_local 1
            i32.load
            tee_local 2
            i32.eqz
            if  ;; label = @6
                i32.const 0
                set_local 2
                br 3 (;@3;)
            end
            end
            loop  ;; label = @5
            get_local 2
            i32.const 20
            i32.add
            tee_local 4
            i32.load
            tee_local 6
            if  ;; label = @6
                get_local 6
                set_local 2
                get_local 4
                set_local 1
                br 1 (;@5;)
            end
            get_local 2
            i32.const 16
            i32.add
            tee_local 4
            i32.load
            tee_local 6
            if  ;; label = @6
                get_local 6
                set_local 2
                get_local 4
                set_local 1
                br 1 (;@5;)
            end
            end
            get_local 1
            i32.const 0
            i32.store
        else
            get_local 0
            i32.load offset=8
            tee_local 1
            get_local 2
            i32.store offset=12
            get_local 2
            get_local 1
            i32.store offset=8
        end
        end
        get_local 7
        if (result i32)  ;; label = @3
        get_local 0
        i32.load offset=28
        tee_local 1
        i32.const 2
        i32.shl
        i32.const 1328
        i32.add
        tee_local 4
        i32.load
        get_local 0
        i32.eq
        if  ;; label = @4
            get_local 4
            get_local 2
            i32.store
            get_local 2
            i32.eqz
            if  ;; label = @5
            i32.const 1028
            i32.const 1028
            i32.load
            i32.const 1
            get_local 1
            i32.shl
            i32.const -1
            i32.xor
            i32.and
            i32.store
            get_local 0
            br 4 (;@1;)
            end
        else
            get_local 7
            i32.const 16
            i32.add
            get_local 7
            i32.load offset=16
            get_local 0
            i32.ne
            i32.const 2
            i32.shl
            i32.add
            get_local 2
            i32.store
            get_local 0
            get_local 2
            i32.eqz
            br_if 3 (;@1;)
            drop
        end
        get_local 2
        get_local 7
        i32.store offset=24
        get_local 0
        i32.const 16
        i32.add
        tee_local 4
        i32.load
        tee_local 1
        if  ;; label = @4
            get_local 2
            get_local 1
            i32.store offset=16
            get_local 1
            get_local 2
            i32.store offset=24
        end
        get_local 4
        i32.load offset=4
        tee_local 1
        if (result i32)  ;; label = @4
            get_local 2
            get_local 1
            i32.store offset=20
            get_local 1
            get_local 2
            i32.store offset=24
            get_local 0
        else
            get_local 0
        end
        else
        get_local 0
        end
    end
    end
    set_local 2
    get_local 0
    get_local 5
    i32.ge_u
    if  ;; label = @1
    return
    end
    get_local 5
    i32.const 4
    i32.add
    tee_local 4
    i32.load
    tee_local 1
    i32.const 1
    i32.and
    i32.eqz
    if  ;; label = @1
    return
    end
    get_local 1
    i32.const 2
    i32.and
    if  ;; label = @1
    get_local 4
    get_local 1
    i32.const -2
    i32.and
    i32.store
    get_local 2
    get_local 3
    i32.const 1
    i32.or
    i32.store offset=4
    get_local 0
    get_local 3
    i32.add
    get_local 3
    i32.store
    else
    i32.const 1048
    i32.load
    get_local 5
    i32.eq
    if  ;; label = @2
        i32.const 1036
        i32.const 1036
        i32.load
        get_local 3
        i32.add
        tee_local 0
        i32.store
        i32.const 1048
        get_local 2
        i32.store
        get_local 2
        get_local 0
        i32.const 1
        i32.or
        i32.store offset=4
        get_local 2
        i32.const 1044
        i32.load
        i32.ne
        if  ;; label = @3
        return
        end
        i32.const 1044
        i32.const 0
        i32.store
        i32.const 1032
        i32.const 0
        i32.store
        return
    end
    i32.const 1044
    i32.load
    get_local 5
    i32.eq
    if  ;; label = @2
        i32.const 1032
        i32.const 1032
        i32.load
        get_local 3
        i32.add
        tee_local 3
        i32.store
        i32.const 1044
        get_local 0
        i32.store
        get_local 2
        get_local 3
        i32.const 1
        i32.or
        i32.store offset=4
        get_local 0
        get_local 3
        i32.add
        get_local 3
        i32.store
        return
    end
    get_local 1
    i32.const -8
    i32.and
    get_local 3
    i32.add
    set_local 7
    get_local 1
    i32.const 3
    i32.shr_u
    set_local 4
    block  ;; label = @2
        get_local 1
        i32.const 256
        i32.lt_u
        if  ;; label = @3
        get_local 5
        i32.load offset=12
        tee_local 3
        get_local 5
        i32.load offset=8
        tee_local 1
        i32.eq
        if  ;; label = @4
            i32.const 1024
            i32.const 1024
            i32.load
            i32.const 1
            get_local 4
            i32.shl
            i32.const -1
            i32.xor
            i32.and
            i32.store
        else
            get_local 1
            get_local 3
            i32.store offset=12
            get_local 3
            get_local 1
            i32.store offset=8
        end
        else
        get_local 5
        i32.load offset=24
        set_local 8
        block  ;; label = @4
            get_local 5
            i32.load offset=12
            tee_local 3
            get_local 5
            i32.eq
            if  ;; label = @5
            get_local 5
            i32.const 16
            i32.add
            tee_local 1
            i32.const 4
            i32.add
            tee_local 4
            i32.load
            tee_local 3
            if  ;; label = @6
                get_local 4
                set_local 1
            else
                get_local 1
                i32.load
                tee_local 3
                i32.eqz
                if  ;; label = @7
                i32.const 0
                set_local 3
                br 3 (;@4;)
                end
            end
            loop  ;; label = @6
                get_local 3
                i32.const 20
                i32.add
                tee_local 4
                i32.load
                tee_local 6
                if  ;; label = @7
                get_local 6
                set_local 3
                get_local 4
                set_local 1
                br 1 (;@6;)
                end
                get_local 3
                i32.const 16
                i32.add
                tee_local 4
                i32.load
                tee_local 6
                if  ;; label = @7
                get_local 6
                set_local 3
                get_local 4
                set_local 1
                br 1 (;@6;)
                end
            end
            get_local 1
            i32.const 0
            i32.store
            else
            get_local 5
            i32.load offset=8
            tee_local 1
            get_local 3
            i32.store offset=12
            get_local 3
            get_local 1
            i32.store offset=8
            end
        end
        get_local 8
        if  ;; label = @4
            get_local 5
            i32.load offset=28
            tee_local 1
            i32.const 2
            i32.shl
            i32.const 1328
            i32.add
            tee_local 4
            i32.load
            get_local 5
            i32.eq
            if  ;; label = @5
            get_local 4
            get_local 3
            i32.store
            get_local 3
            i32.eqz
            if  ;; label = @6
                i32.const 1028
                i32.const 1028
                i32.load
                i32.const 1
                get_local 1
                i32.shl
                i32.const -1
                i32.xor
                i32.and
                i32.store
                br 4 (;@2;)
            end
            else
            get_local 8
            i32.const 16
            i32.add
            get_local 8
            i32.load offset=16
            get_local 5
            i32.ne
            i32.const 2
            i32.shl
            i32.add
            get_local 3
            i32.store
            get_local 3
            i32.eqz
            br_if 3 (;@2;)
            end
            get_local 3
            get_local 8
            i32.store offset=24
            get_local 5
            i32.const 16
            i32.add
            tee_local 4
            i32.load
            tee_local 1
            if  ;; label = @5
            get_local 3
            get_local 1
            i32.store offset=16
            get_local 1
            get_local 3
            i32.store offset=24
            end
            get_local 4
            i32.load offset=4
            tee_local 1
            if  ;; label = @5
            get_local 3
            get_local 1
            i32.store offset=20
            get_local 1
            get_local 3
            i32.store offset=24
            end
        end
        end
    end
    get_local 2
    get_local 7
    i32.const 1
    i32.or
    i32.store offset=4
    get_local 0
    get_local 7
    i32.add
    get_local 7
    i32.store
    get_local 2
    i32.const 1044
    i32.load
    i32.eq
    if  ;; label = @2
        i32.const 1032
        get_local 7
        i32.store
        return
    else
        get_local 7
        set_local 3
    end
    end
    get_local 3
    i32.const 3
    i32.shr_u
    set_local 1
    get_local 3
    i32.const 256
    i32.lt_u
    if  ;; label = @1
    get_local 1
    i32.const 3
    i32.shl
    i32.const 1064
    i32.add
    set_local 0
    i32.const 1024
    i32.load
    tee_local 3
    i32.const 1
    get_local 1
    i32.shl
    tee_local 1
    i32.and
    if (result i32)  ;; label = @2
        get_local 0
        i32.const 8
        i32.add
        tee_local 1
        i32.load
    else
        i32.const 1024
        get_local 3
        get_local 1
        i32.or
        i32.store
        get_local 0
        i32.const 8
        i32.add
        set_local 1
        get_local 0
    end
    set_local 3
    get_local 1
    get_local 2
    i32.store
    get_local 3
    get_local 2
    i32.store offset=12
    get_local 2
    get_local 3
    i32.store offset=8
    get_local 2
    get_local 0
    i32.store offset=12
    return
    end
    get_local 3
    i32.const 8
    i32.shr_u
    tee_local 0
    if (result i32)  ;; label = @1
    get_local 3
    i32.const 16777215
    i32.gt_u
    if (result i32)  ;; label = @2
        i32.const 31
    else
        get_local 3
        i32.const 14
        get_local 0
        get_local 0
        i32.const 1048320
        i32.add
        i32.const 16
        i32.shr_u
        i32.const 8
        i32.and
        tee_local 0
        i32.shl
        tee_local 1
        i32.const 520192
        i32.add
        i32.const 16
        i32.shr_u
        i32.const 4
        i32.and
        tee_local 4
        get_local 0
        i32.or
        get_local 1
        get_local 4
        i32.shl
        tee_local 0
        i32.const 245760
        i32.add
        i32.const 16
        i32.shr_u
        i32.const 2
        i32.and
        tee_local 1
        i32.or
        i32.sub
        get_local 0
        get_local 1
        i32.shl
        i32.const 15
        i32.shr_u
        i32.add
        tee_local 0
        i32.const 7
        i32.add
        i32.shr_u
        i32.const 1
        i32.and
        get_local 0
        i32.const 1
        i32.shl
        i32.or
    end
    else
    i32.const 0
    end
    tee_local 1
    i32.const 2
    i32.shl
    i32.const 1328
    i32.add
    set_local 0
    get_local 2
    get_local 1
    i32.store offset=28
    get_local 2
    i32.const 0
    i32.store offset=20
    get_local 2
    i32.const 0
    i32.store offset=16
    block  ;; label = @1
    i32.const 1028
    i32.load
    tee_local 4
    i32.const 1
    get_local 1
    i32.shl
    tee_local 6
    i32.and
    if  ;; label = @2
        get_local 0
        i32.load
        set_local 0
        i32.const 25
        get_local 1
        i32.const 1
        i32.shr_u
        i32.sub
        set_local 4
        get_local 3
        get_local 1
        i32.const 31
        i32.eq
        if (result i32)  ;; label = @3
        i32.const 0
        else
        get_local 4
        end
        i32.shl
        set_local 1
        block  ;; label = @3
        loop  ;; label = @4
            get_local 0
            i32.load offset=4
            i32.const -8
            i32.and
            get_local 3
            i32.eq
            br_if 1 (;@3;)
            get_local 1
            i32.const 1
            i32.shl
            set_local 4
            get_local 0
            i32.const 16
            i32.add
            get_local 1
            i32.const 31
            i32.shr_u
            i32.const 2
            i32.shl
            i32.add
            tee_local 1
            i32.load
            tee_local 6
            if  ;; label = @5
            get_local 4
            set_local 1
            get_local 6
            set_local 0
            br 1 (;@4;)
            end
        end
        get_local 1
        get_local 2
        i32.store
        get_local 2
        get_local 0
        i32.store offset=24
        get_local 2
        get_local 2
        i32.store offset=12
        get_local 2
        get_local 2
        i32.store offset=8
        br 2 (;@1;)
        end
        get_local 0
        i32.const 8
        i32.add
        tee_local 3
        i32.load
        tee_local 1
        get_local 2
        i32.store offset=12
        get_local 3
        get_local 2
        i32.store
        get_local 2
        get_local 1
        i32.store offset=8
        get_local 2
        get_local 0
        i32.store offset=12
        get_local 2
        i32.const 0
        i32.store offset=24
    else
        i32.const 1028
        get_local 4
        get_local 6
        i32.or
        i32.store
        get_local 0
        get_local 2
        i32.store
        get_local 2
        get_local 0
        i32.store offset=24
        get_local 2
        get_local 2
        i32.store offset=12
        get_local 2
        get_local 2
        i32.store offset=8
    end
    end
    i32.const 1056
    i32.const 1056
    i32.load
    i32.const -1
    i32.add
    tee_local 0
    i32.store
    get_local 0
    if  ;; label = @1
    return
    else
    i32.const 1480
    set_local 0
    end
    loop  ;; label = @1
    get_local 0
    i32.load
    tee_local 3
    i32.const 8
    i32.add
    set_local 0
    get_local 3
    br_if 0 (;@1;)
    end
    i32.const 1056
    i32.const -1
    i32.store)
    


(export "mxarray_core_get_array_ptr" (func $mxarray_core_get_array_ptr))
(func $mxarray_core_get_array_ptr (param $arr_header i32)(result i32)
    get_local $arr_header
    i32.load offset=8 align=4
    return
)
(export "get_mxarray_dimension_number" (func $get_mxarray_dimension_number))
(func $get_mxarray_dimension_number (param $dim_array_ptr i32) (result i32)
    (local $dim_number i32)(local $i i32)(local $temp f64)(local $one_index i32)(local $input_dim_array_ptr i32)
    (local $dim_array_elem_byte_size i32)
    (;Finds out real dimension number for mxArray;)
    (set_local $one_index (i32.const 2))
    (set_local $dim_array_elem_byte_size (call $get_array_byte_size (get_local $dim_array_ptr)))
    (set_local $dim_number (i32.load offset=4 align=4 (get_local $dim_array_ptr)))
    (set_local $input_dim_array_ptr (call $mxarray_core_get_array_ptr (get_local $dim_array_ptr)))
    (i32.lt_s (get_local $i)(get_local $dim_number)) 
    if
        loop
            (i32.and  
                    (f64.eq (f64.const 1) 
                        (tee_local $temp (f64.load (i32.add (get_local $input_dim_array_ptr) 
                            (i32.mul (get_local $dim_array_elem_byte_size)(get_local $i))))))
                    (i32.ge_s (get_local $i)(i32.const 2)))
            if
                get_local $one_index                
                i32.eqz
                if
                    (set_local $one_index (get_local $i))
                end
            else
                (set_local $one_index (i32.const 0))
            end
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $dim_number)))
        end
    end
    get_local $one_index
    i32.eqz
    if (result i32)
        get_local $dim_number
    else
        get_local $one_index
    end
)
(func $set_mxarray_dimensions (param $dim_array i32) (param $loop_dim_number i32) (result i32)
    (local $dim_number i32)(local $array_length f64)(local $input_dim_array_byte_size i32)
    (local $loop_dim_number i32)(local $temp f64)(local $i i32)(local $dim_array_ptr i32)
    (local $input_dim_array_ptr i32)
    ;; Get length dimension 
    (set_local $input_dim_array_byte_size (call $get_array_byte_size (get_local $dim_array)))
    (set_local $input_dim_array_ptr (call $mxarray_core_get_array_ptr (get_local $dim_array)))
    get_local $loop_dim_number
    i32.const 1
    i32.eq
    if
        (set_local $dim_number (i32.add (i32.const 1)(get_local $loop_dim_number)))
    else 
        (set_local $dim_number (get_local $loop_dim_number))
    end
    ;; Allocate dimensions
    (call $malloc (i32.add (i32.mul (get_local $dim_number)(i32.const 8))(i32.const 8)))
    i32.const 8
    i32.add
    tee_local $dim_array_ptr
        
    ;; Set dimensions and calculate array length
    (set_local $array_length (f64.const 1))        
    loop
        block ;; array iteration
        (i32.eq (get_local $i)(get_local $loop_dim_number))
        br_if 0
        (tee_local $temp (f64.load (i32.add (get_local $input_dim_array_ptr) ;;(poly) This line needs to change to accomodate for other simple classes as input
                (i32.mul (get_local $input_dim_array_byte_size)(get_local $i)))))
        f64.const 0
        f64.le
        if ;; If dimension is less than or equal 0
            (set_local $temp (f64.const 0))
        end
        
        ;; Get Array length
        (set_local $array_length 
            (f64.mul (get_local $array_length) (get_local $temp)))
        ;; Set dimension in dimension array
        (f64.store (i32.add (get_local $dim_array_ptr) (i32.mul 
                    (get_local $input_dim_array_byte_size)(get_local $i)))(get_local $temp))
        (set_local $i (i32.add (get_local $i)(i32.const 1))) ;; Increase loop counter
        br 1
        end
    end
)
(export "copy_mxarray_header" (func $copy_mxarray_header))
(func $copy_mxarray_header (param $mxarray i32)(result i32)
    (local $i i32)(local $return_mxarray i32)
    i32.const 26
    call $malloc
    tee_local $return_mxarray
    loop
        (i32.store (i32.add (get_local $return_mxarray)(get_local $i))
            (i32.load (i32.add (get_local $mxarray)(get_local $i))))
        (set_local $i (i32.add (get_local $i)(i32.const 4)))
        (br_if 0  (i32.lt_s (get_local $i)(i32.const 24)))
    end    
    i32.const 0
    i32.store16 offset=24 align=2
    get_local $return_mxarray
)
    (export "copy_mxarray_structure" (func $copy_mxarray_structure))
(func $copy_mxarray_structure (param $mxarray i32)(result i32)
    (local $i i32)(local $len i32)(local $return_mxarray i32)
    (local $step i32)(local $ptr_data i32)(local $dim_len i32)
    i32.const 26
    call $malloc
    set_local $return_mxarray
    loop
        (i32.store (i32.add (get_local $return_mxarray)(get_local $i))
            (i32.load (i32.add (get_local $mxarray)(get_local $i))))
        (set_local $i (i32.add (get_local $i)(i32.const 4)))
        (br_if 0  (i32.lt_s (get_local $i)(i32.const 24)))
    end 
    ;; Allocate data 
    get_local $mxarray  
    i32.load offset=8 align=4
    i32.const 0 
    i32.gt_s
    if
        get_local $mxarray  
        i32.load offset=4 align=4
        i32.const 3
        i32.shl
        i32.const 8
        i32.add
        tee_local $len
        call $malloc
        tee_local $i
        get_local $len
        i32.store offset=4 align=4
        get_local $return_mxarray
        get_local $i
        i32.const 8
        i32.add
        i32.store offset=8 align=4
    end
    ;; Dimension allocation
    get_local $mxarray
    i32.load offset=12 align=4
    i32.const 3
    i32.shl
    i32.const 8
    i32.add
    tee_local $len 
    call $malloc
    tee_local $i
    get_local $len 
    i32.store offset=4 align=4
    get_local $return_mxarray
    get_local $i
    i32.const 8
    i32.add
    tee_local $i
    i32.store offset=16 align=4

    (set_local $dim_len (i32.sub (get_local $len)(i32.const 8)))

    (set_local $ptr_data (i32.load offset=16 align=4 (get_local $mxarray)))
    loop
        get_local $i
        get_local $step
        i32.add 

        get_local $ptr_data
        get_local $step
        i32.add
        f64.load offset=0 align=8  

        f64.store offset=0 align=8
        (set_local $step (i32.add (get_local $step)(i32.const 8)))
        (br_if 0 (i32.lt_s (get_local $step)(get_local $dim_len)))
    end
    ;; Stride allocation and setting
    get_local $len      
    call $malloc
    tee_local $i
    get_local $len 
    i32.store offset=4 align=4
    get_local $return_mxarray
    get_local $i
    i32.const 8
    i32.add
    tee_local $i
    i32.store offset=20 align=4
    (set_local $ptr_data (i32.load offset=20 align=4 (get_local $mxarray)))
    (set_local $step (i32.const 0))
    loop
        get_local $i
        get_local $step
        i32.add 

        get_local $ptr_data
        get_local $step
        i32.add
        f64.load offset=0 align=8   
                
        f64.store offset=0 align=8
        (set_local $step (i32.add (get_local $step)(i32.const 8)))
        (br_if 0 (i32.lt_s (get_local $step)(get_local $dim_len)))
    end
    
    get_local $return_mxarray
    i32.const 0
    i32.store16 offset=24 align=2
    get_local $return_mxarray
)
(export "create_mxarray_2D" (func $create_mxarray_2D))
(func $create_mxarray_2D (param $dim1 f64)(param $dim2 f64)(result i32)
    (local $header_pointer i32)(local $i i32)(local $dimension_ptr i32)(local $strides_ptr i32)
    (local $len i32)

    get_local $dim1
    f64.const 0
    f64.lt
    if
        (set_local $dim1 (f64.const 0))
    end
    get_local $dim2
    f64.const 0
    f64.lt
    if
        (set_local $dim2 (f64.const 0))
    end

    i32.const 26
    call $malloc
    ;; Set type attribute
    tee_local $header_pointer
    i32.const 0
    i32.const 8
    i32.const 0
    call $mxarray_core_set_type_attribute


    ;; call set size of array
    get_local $header_pointer
    get_local $dim1
    get_local $dim2
    f64.mul
    i32.trunc_u/f64
    tee_local $len
    i32.store offset=4 align=4 ;; Store pointer to array

    ;; Allocating array
    get_local $header_pointer
    get_local $len
    i32.const 3
    i32.shl
    i32.const 8
    i32.add
    tee_local $dimension_ptr ;; Use as register temporarily
    call $malloc
    tee_local $strides_ptr
    i32.const 8
    i32.add
    i32.store offset=8 align=4
    
    ;; Set capacity in array data
    get_local $strides_ptr
    get_local $dimension_ptr
    i32.store offset=4 align=4
    

    ;; Set number of dimensions
    get_local $header_pointer
    i32.const 2
    i32.store offset=12 align=4 ;; Store 
    
    ;; Allocating dimensions
    get_local $header_pointer
    i32.const 24
    call $malloc
    tee_local $dimension_ptr
    i32.const 8
    i32.add
    i32.store offset=16 align=4
    ;; Set capacity in dimensions array
    get_local $dimension_ptr
    i32.const 16
    i32.store offset=4 align=4
    ;; Setting strides
    get_local $header_pointer
    i32.const 24
    call $malloc
    tee_local $strides_ptr
    i32.const 8
    i32.add
    i32.store offset=20 align=4
    ;; Set capacity in strides array
    get_local $strides_ptr
    i32.const 16
    i32.store offset=4 align=4
    ;; Set dimensions 
    (tee_local $dimension_ptr (i32.add (get_local $dimension_ptr)(i32.const 8)))
    get_local $dim1
    f64.store offset=0 align=8
    get_local $dimension_ptr
    get_local $dim2
    f64.store offset=8 align=8 

    ;; Set strides 
    (tee_local $strides_ptr (i32.add (get_local $strides_ptr)(i32.const 8)))
    f64.const 1
    f64.store offset=0 align=8
    get_local $strides_ptr
    get_local $dim1
    f64.store offset=8 align=8 
    ;; RC count and flag
    get_local $header_pointer     
    i32.const 0
    i32.store16 offset=24 align=2
    get_local $header_pointer   


)
    (export "isequaln" (func $isequaln))
(func $isequaln (param $input_vec i32) (result i32)
    (local $len i32)(local $i i32)(local $prev i32)(local $input_vec_data i32)
    (tee_local $len (i32.load offset=4 align=4 (get_local $input_vec)))
    i32.const 2
    i32.ge_s
    if
        (set_local $input_vec_data (i32.load offset=8 align=4 (get_local $input_vec)))
        (set_local $prev (i32.load offset=0 align=4 (get_local $input_vec_data)))
        loop
            (call $isequaln_two (i32.load offset=0 align=4 (get_local $input_vec_data))(get_local $prev))
            i32.eqz
            if
                i32.const 0
                return
            end
            (set_local $i (i32.add (i32.const 1)(get_local $i)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
        i32.const 1
        return
    end
    i32.const 6
    call $throwError
    unreachable
)
;; IsEqual, two implementations, general one, two arrays. Use to arrays for the general one.
(export "isequaln_two" (func $isequaln_two))
(func $isequaln_two (param $arr1 i32) (param $arr2 i32) (result i32)
    (local $arr1_data i32) (local $arr2_data i32)(local $step i32)(local $len i32)(local $val1 f64)(local $val2 f64)
    ;; Assume the two inputs are MachArrays which is checked at a higher level.
    (call $mxarrays_have_same_dimensions (get_local $arr1)(get_local $arr2))
    if 
        (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1))(i32.const 3)))
        (set_local $arr1_data (i32.load offset=8 align=4 (get_local $arr1)))
        (set_local $arr2_data (i32.load offset=8 align=4 (get_local $arr2)))
        (i32.lt_s (get_local $step)(get_local $len))
        if 
            loop
                get_local $arr1_data
                get_local $step
                i32.add
                f64.load offset=0 align=8
                tee_local $val1
                call $isnan
                if
                    br 0 ;; continue
                end                    
                get_local $arr2_data
                get_local $step
                i32.add
                f64.load offset=0 align=8 
                tee_local $val1
                call $isnan
                if
                    br 0 ;; continue
                end 

                get_local $val1
                get_local $val2
                f64.eq
                i32.eqz
                if
                    i32.const 0
                    return
                end
                (set_local $step (i32.add (get_local $step)(i32.const 8)))
                (br_if 0 (i32.lt_s (get_local $step)(get_local $len))) 
            end
            i32.const 1
            return
        end
    end
    i32.const 0
)
(export "isequal" (func $isequal))
(func $isequal (param $input_vec i32) (result i32)
    (local $len i32)(local $i i32)(local $prev i32)(local $input_vec_data i32)
    (tee_local $len (i32.load offset=4 align=4 (get_local $input_vec)))
    i32.const 2
    i32.ge_s
    if
        (set_local $input_vec_data (i32.load offset=8 align=4 (get_local $input_vec)))
        (set_local $prev (i32.load offset=0 align=4 (get_local $input_vec_data)))
        loop
            (call $isequal_two (i32.load offset=0 align=4 (get_local $input_vec_data))(get_local $prev))
            i32.eqz
            if
                i32.const 0
                return
            end
            (set_local $i (i32.add (i32.const 1)(get_local $i)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
        i32.const 1
        return
    end
    i32.const 6
    call $throwError
    unreachable
)
;; IsEqual, two implementations, general one, two arrays. Use to arrays for the general one.
(export "isequal_two" (func $isequal_two))
(func $isequal_two (param $arr1 i32) (param $arr2 i32) (result i32)
    (local $arr1_data i32) (local $arr2_data i32)(local $step i32)(local $len i32)
    ;; Assume the two inputs are MachArrays which is checked at a higher level.
    (call $mxarrays_have_same_dimensions (get_local $arr1)(get_local $arr2))
    if 
        (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1))(i32.const 3)))
        (set_local $arr1_data (i32.load offset=8 align=4 (get_local $arr1)))
        (set_local $arr2_data (i32.load offset=8 align=4 (get_local $arr2)))
        (i32.lt_s (get_local $step)(get_local $len))
        if 
            loop
                get_local $arr1_data
                get_local $step
                i32.add
                f64.load offset=0 align=8                    
                get_local $arr2_data
                get_local $step
                i32.add
                f64.load offset=0 align=8 
                f64.eq
                i32.eqz
                if
                    i32.const 0
                    return
                end
                (set_local $step (i32.add (get_local $step)(i32.const 8)))
                (br_if 0 (i32.lt_s (get_local $step)(get_local $len))) 
            end
            i32.const 1
            return
        end
    end
    i32.const 0
)
(export "create_mxarray_ND" (func $create_mxarray_ND))
(func $create_mxarray_ND  (param $dim_array i32)(param $class i32) (param $simple_class i32)
(param $empty_data_ptr i32)
(result i32)
    (;
        Order: 0 = Column-major, 1=Row-major
    ;)
    (local $dim_number i32)(local $array_length f64)(local $array_length_i32 i32)(local $loop_dim_number i32)(local $byte_size_elem i32)
    (local $i i32) (local $input_dim_array_ptr i32) (local $temp f64) (local $header_size i32) (local $array_size i32)
    (local $array_data_ptr i32)(local $array_header_ptr i32)(local $dim_array_ptr i32)(local $input_dim_array_byte_size i32)
    (local $stride_array_ptr i32)(local $offset_arr_index i32)
    ;; Get the size of bytes for type
    ;; ()
    get_local $dim_array
    call $is_null
    get_local $dim_array
    i32.load offset=4 align=4 ;; Check length
    i32.eqz
    i32.or
    get_local $dim_array
    call $isrow
    i32.eqz
    i32.or 
    if
        i32.const 5
        call $throwError
    end

    ;; Get pointer to dim_array_data
    (set_local $input_dim_array_ptr (i32.load offset=8 align=4 (get_local $dim_array)))
    ;; Setting the size of elements
    get_local $class
    i32.eqz
    if
        (set_local $byte_size_elem (call $mxarray_core_get_simple_class_byte_size (get_local $simple_class)))
    else
        (set_local $byte_size_elem (i32.const 4))
    end

    ;; Allocate header
    i32.const 26
    call $malloc
    set_local  $array_header_ptr
    
    ;; Get length dimension 
    (set_local $input_dim_array_byte_size (call $get_array_byte_size (get_local $dim_array)))
    (tee_local $loop_dim_number (call $get_mxarray_dimension_number (get_local $dim_array)))
    i32.const 1
    i32.eq
    if
        (set_local $dim_number (i32.add (i32.const 1)(get_local $loop_dim_number)))
    else 
        (set_local $dim_number (get_local $loop_dim_number))
    end
    ;; Allocate dimensions
    get_local $array_header_ptr
    (call $malloc (i32.add (i32.mul (get_local $dim_number)(i32.const 8))(i32.const 8)))
    i32.const 8
    i32.add
    tee_local $dim_array_ptr
    i32.store offset=16 align=4

    ;; Allocate strides
    get_local $array_header_ptr
    (call $malloc (i32.add (i32.mul (get_local $dim_number)(i32.const 8))(i32.const 8)))
    i32.const 8
    i32.add
    tee_local $stride_array_ptr
    i32.store offset=20 align=4


    ;; Set dimensions and calculate array length
    (set_local $array_length (f64.const 1))        
    (i32.lt_s (get_local $i)(get_local $loop_dim_number)) 
    if
        ;; (set_local $offset_arr_index (get_local $input_dim_array_ptr))
        loop
            (tee_local $temp 
                (f64.load (i32.add (get_local $offset_arr_index)(get_local $input_dim_array_ptr))))
            f64.const 0
            f64.lt
            if ;; If dimension is less than or equal 0
                (set_local $temp (f64.const 0))
            end
            ;; Set strides if greater than second dim, the other two strides depend on the Order of the array.
            get_local $i
            i32.const 1
            i32.gt_s
            if
                (f64.store (i32.add (get_local $stride_array_ptr)(get_local $offset_arr_index))
                    (get_local $array_length)) 
            end
            ;; Get Array length
            (set_local $array_length 
                (f64.mul (get_local $array_length) (get_local $temp)))
            ;; Set dimension in dimension array
            (f64.store (i32.add (get_local $dim_array_ptr)(get_local $offset_arr_index)) (get_local $temp))

        ;; Augment ptr
        (set_local $offset_arr_index  ;; Increase array ptr
            (i32.add (get_local $input_dim_array_byte_size)(get_local $offset_arr_index)))
        (set_local $i (i32.add (get_local $i)(i32.const 1))) ;; Increase loop counter
        (br_if 0 (i32.lt_s (get_local $i)(get_local $loop_dim_number))) 
        end
    end

    ;; Convert the length into an i32
    (set_local $array_length_i32 (i32.trunc_s/f64 (get_local $array_length )))

    ;; Check if length is one, if it is set the other dimension to get an square matrix
    (i32.eq ( get_local $loop_dim_number)(i32.const 1))
    if
        (f64.store offset=8 align=8 (get_local $dim_array_ptr)(get_local $temp))
        (set_local $array_length_i32 (i32.mul (get_local $array_length_i32)(get_local $array_length_i32))) 
        ;; Column major 
            (f64.store offset=0 align=8 (get_local $stride_array_ptr)(f64.const 1))
            (f64.store offset=8 align=8 (get_local $stride_array_ptr)(get_local $temp))
    else
            (f64.store offset=0 align=8 (get_local $stride_array_ptr)(f64.const 1))
            (f64.store offset=8 align=8 (get_local $stride_array_ptr)(f64.load offset=0 align=8 (get_local $dim_array_ptr)))
    end
    
    ;; Setting capacity array dim
    get_local $dim_array_ptr
    i32.const 4
    i32.sub
    (i32.mul (get_local $dim_number)(i32.const 8))
    i32.store offset=0 align=4
    ;; Setting capacity array stride 
    get_local $stride_array_ptr
    i32.const 4
    i32.sub
    (i32.mul (get_local $dim_number)(i32.const 8))
    i32.store offset=0 align=4  

    ;; Setting type attribute
    get_local $array_header_ptr
    get_local $class
    get_local $byte_size_elem
    get_local $simple_class
    call $mxarray_core_set_type_attribute
    ;; Setting length
    get_local $array_header_ptr
    get_local $array_length_i32
    i32.store offset=4 align=4
    get_local $empty_data_ptr
    get_local $array_length_i32
    i32.eqz
    i32.or
    if
        get_local $array_header_ptr
        i32.const 0
        i32.store offset=8 align=4
    else
        ;;Setting Array data ptr, add 8 bytes, 4 for the capacity of the array, 4 for alignment
        get_local $array_header_ptr
        (call $malloc 
            (tee_local $array_size (i32.add (i32.const 8)
                (i32.mul (get_local $array_length_i32)(get_local $byte_size_elem)))));; We use byte_size here because it may be a cell-array
        tee_local $array_data_ptr
        i32.const 8
        i32.add 
        i32.store offset=8 align=4
        ;; Set array capacity
        get_local $array_data_ptr
        get_local $array_size
        i32.store offset=4 align=4
    end

        ;; Setting array dim_number
    get_local $array_header_ptr
    get_local $dim_number
    i32.store offset=12 align=4

    get_local $array_header_ptr
    i32.const 0
    i32.store16 offset=24 align=2
    get_local $array_header_ptr
)

(export "get_array_index_i8" (func $get_array_index_i8))
(func $get_array_index_i8 (param $array_ptr i32)(param $i i32)(result i32)
    (local $elem i32)
    get_local $array_ptr
    call $mxarray_core_get_array_length
    get_local $i
    i32.lt_s
    if
        i32.const 3
        call $throwError
    end
    (set_local $i (i32.sub (get_local $i)(i32.const 1)))
    get_local $i
    i32.const 0
    i32.lt_s
    if
        i32.const 4
        call $throwError
    end
    get_local $array_ptr
    call $get_array_byte_size
    get_local $i
    i32.mul
    get_local $array_ptr
    call $mxarray_core_get_array_ptr
    i32.add
    set_local $elem
    get_local $array_ptr
    call $is_signed
    if (result i32)
        get_local $elem
        i32.load8_s offset=0 align=1
    else 
        get_local $elem
        i32.load8_u offset=0 align=1
    end
)
(export "set_array_index_i8" (func $set_array_index_i8))
(func $set_array_index_i8 (param $array_ptr i32)(param $i i32)(param $value i32)
    
    get_local $array_ptr
    call $mxarray_core_get_array_ptr
    i32.const 8
    i32.sub
    i32.load offset=0 align=4
    get_local $i
    i32.lt_s
    if
        i32.const 10
        call $throwError
    end
    (set_local $i (i32.sub (get_local $i)(i32.const 1)))
    get_local $i
    i32.const 0
    i32.lt_s
    if
        i32.const 4
        call $throwError
    end
    
    get_local $array_ptr
    call $get_array_byte_size
    get_local $i
    i32.mul
    get_local $array_ptr
    call $mxarray_core_get_array_ptr
    i32.add
    get_local $array_ptr
    call $is_signed
    if (result i32)
        get_local $value
        i32.const 127
        i32.gt_s
        if (result i32)
            i32.const 127
        else 
            get_local $value
            i32.const -128
            i32.lt_s
            if (result i32)
                i32.const -128
            else 
                get_local $value
            end
        end
    else 
        get_local $value
        i32.const 255
        i32.gt_s
        if (result i32)
            i32.const 255
        else 
            get_local $value
            i32.const 0
            i32.lt_s
            if (result i32)
                i32.const 0
            else 
                get_local $value                    
            end
        end
    end
    i32.store8 offset=0 align=1
)

(export "get_array_index_i32" (func $get_array_index_i32))
(func $get_array_index_i32 (param $array_ptr i32)(param $i i32)(result i32)
    get_local $array_ptr
    i32.load offset=4 align=4 
    get_local $i
    i32.lt_s
    if
        i32.const 3
        call $throwError
    end
    (tee_local $i (i32.sub (get_local $i)(i32.const 1)))
    i32.const 0
    i32.lt_s
    if
        i32.const 4
        call $throwError
    end
    get_local $array_ptr
    call $get_array_byte_size
    get_local $i
    i32.mul
    get_local $array_ptr
    call $mxarray_core_get_array_ptr
    i32.add
    i32.load offset=0 align=4
    return
)
(export "set_array_index_i32" (func $set_array_index_i32))
(func $set_array_index_i32 (param $array_ptr i32)(param $i i32)(param $value i32)
    
    get_local $array_ptr
    call $mxarray_core_get_array_ptr
    i32.const 4
    i32.sub
    i32.load offset=0 align=4
    get_local $i
    i32.lt_s
    if
        i32.const 3
        call $throwError
    end
    (set_local $i (i32.sub (get_local $i)(i32.const 1)))
    get_local $i
    i32.const 0
    i32.lt_s
    if
        i32.const 4
        call $throwError
    end
    get_local $array_ptr
    call $get_array_byte_size
    get_local $i
    i32.mul
    get_local $array_ptr
    call $mxarray_core_get_array_ptr
    i32.add
    get_local $array_ptr
    call $is_signed
    if (result i32)
        get_local $value
        i32.const 2147483647
        i32.gt_s
        if (result i32)
            i32.const 2147483647
        else 
            get_local $value
            i32.const -2147483648
            i32.lt_s
            if (result i32)
                i32.const -2147483648
            else 
                get_local $value
            end
        end
    else 
        get_local $value
        i32.const 2147483647
        i32.gt_s
        if (result i32)
            i32.const 2147483647
        else 
            get_local $value
            i32.const 0
            i32.lt_s
            if (result i32)
                i32.const 0
            else 
                get_local $value                    
            end
        end
    end
    i32.store offset=0 align=4
)
(export "create_mxarray_empty" (func $create_mxarray_empty))
(func $create_mxarray_empty (param $dim_num i32)(param $simple_class i32)(param $class i32)(result i32)
    (local $header_pointer i32)(local $i i32)(local $dimension_ptr i32)
    
    get_local $dim_num
    i32.const 0
    i32.lt_s
    if
        i32.const 4
        call $throwError
    end
    get_local $dim_num
    i32.const 1
    i32.eq
    if
        (set_local $dim_num (i32.const 2))
    end
    i32.const 26
    call $malloc
        ;; Allocate array memory or return -1 if size is 0
    tee_local $header_pointer
    i32.const 0
    i32.store offset=8 align=4 ;; Store pointer to array

    ;; Allocate array memory or return -1 if size is 0
    get_local $header_pointer
    i32.const 0
    i32.store offset=20 align=4 ;; empty strides

    ;; Set type attribute
    get_local $header_pointer
    get_local $class
    i32.const 8
    get_local $simple_class
    call $mxarray_core_set_type_attribute
    


    ;; call set size of array
    get_local $header_pointer
    i32.const 0
    i32.store offset=4 align=4 ;; Store pointer to array
    ;; Set number of dimensions
    get_local $header_pointer
    get_local $dim_num
    i32.store offset=12 align=4 ;; Store 
    get_local $header_pointer
    get_local $dim_num
    i32.const 8
    i32.mul
    i32.const 8
    i32.add
    call $malloc
    tee_local $dimension_ptr
    i32.const 8
    i32.add
    i32.store offset=16 align=4
    ;; Set capacity in dimensions array
    get_local $dimension_ptr
    get_local $dim_num
    i32.const 8
    i32.mul
    i32.store offset=4 align=4
    
    ;; Set dimensions in dimension array
    (set_local $dimension_ptr (i32.add (get_local $dimension_ptr)(i32.const 8)))
    loop
        block
        (br_if 0 (i32.ge_s (get_local $i)(get_local $dim_num)))
            get_local $dimension_ptr
            i32.const 8
            get_local $i
            i32.mul
            i32.add
            f64.const 0
            f64.store offset=0 align=8
        (set_local $i (i32.add (i32.const 1)(get_local $i)))
        br 1
        end
    end

    ;; Set strides array
        ;; Set strides
    get_local $header_pointer
    get_local $dim_num
    i32.const 3
    i32.shl
    i32.const 8
    i32.add
    call $malloc
    tee_local $dimension_ptr
    i32.const 8
    i32.add
    i32.store offset=20 align=4
    ;; Set capacity in strides array
    get_local $dimension_ptr
    get_local $dim_num
    i32.const 8
    i32.mul
    i32.store offset=4 align=4

    (set_local $dimension_ptr (i32.add (get_local $dimension_ptr)(i32.const 8)))
    (set_local $i (i32.const 0))
    loop
        block
        (br_if 0 (i32.ge_s (get_local $i)(get_local $dim_num)))
            get_local $dimension_ptr
            i32.const 8
            get_local $i
            i32.mul
            i32.add
            f64.const 1
            f64.store offset=0 align=8
        (set_local $i (i32.add (i32.const 1)(get_local $i)))
        br 1
        end
    end
    get_local $header_pointer
    i32.const 0
    i32.store16 offset=24 align=2
    get_local $header_pointer        
)
(export "create_mxmatrix" (func $create_mxmatrix))
(func $create_mxmatrix (param $dim1 i32)(param $dim2 i32)(result i32)
    (local $dim_ptr i32)(local $header_ptr i32)(local $numel i32)
    ;; Check dimensions, set to 0 if negative.
    get_local $dim1
    i32.const 0 
    i32.lt_s
    if
        (set_local $dim1 (i32.const 0))
    end
    get_local $dim2
    i32.const 0 
    i32.lt_s
    if
        (set_local $dim2 (i32.const 0))
    end
    ;; Calculating total array length
    (set_local $numel 
        (i32.mul 
            (get_local $dim1)
            (get_local $dim2)))
    
    i32.const 26 ;; TypeAttr, numel, data_ptr, ndims, dim_ptr, stride,  
    call $malloc
    tee_local $header_ptr
    i32.const 0
    i32.const 8
    i32.const 0
    call $mxarray_core_set_type_attribute
    ;; Set number of elements
    
    get_local $numel
    i32.eqz
    if
        get_local $header_ptr
        i32.const 0
        i32.store offset=4 align=4
        get_local $header_ptr
        i32.const 0
        i32.store offset=8 align=4
    else
        get_local $header_ptr
        get_local $numel
        i32.store offset=4 align=4
        get_local $header_ptr
        ;; Allocate size
        get_local $numel
        i32.const 3
        i32.shl
        i32.const 8
        i32.add ;; Add 8 for capacity
        call $malloc
        tee_local $dim_ptr
        i32.const 8
        i32.add
        i32.store offset=8 align=4 

        ;; Store capacity
        get_local $dim_ptr
        get_local $numel
        i32.store offset=4 align=4 
    end
    ;; Allocate dimensions
    i32.const 24
    call $malloc
    tee_local $dim_ptr
    i32.const 16
    i32.store offset=4 align=4
    get_local $dim_ptr
    i32.const 8
    i32.add
    tee_local $dim_ptr
    get_local $dim1
    i32.store offset=0 align=4     
    get_local $dim_ptr
    get_local $dim2
    i32.store offset=8 align=4  
    ;; Saving dim_ptr  
    get_local $header_ptr    
    i32.const 2
    i32.store offset=12 align=4 
    get_local $header_ptr
    get_local $dim_ptr
    i32.store offset=16 align=4 

    

    ;; Allocate strides
    i32.const 24
    call $malloc
    tee_local $dim_ptr
    i32.const 16
    i32.store offset=4 align=4
    get_local $dim_ptr
    i32.const 8
    i32.add
    set_local $dim_ptr
    get_local $dim_ptr
    f64.const 1
    f64.store  offset=0 align=8
    get_local $dim_ptr
    get_local $dim1
    f64.convert_s/i32
    f64.store offset=8 align=8
    ;; saving strides
    get_local $header_ptr
    get_local $dim_ptr
    i32.store offset=20 align=4 

    get_local $header_ptr
    i32.const 0
    i32.store16 offset=24 align=2
    ;; return
    get_local $header_ptr
)
(export "create_mxvector" (func $create_mxvector))
(func $create_mxvector 
    (param $n i32)(param $simple_class i32)(param $class i32)(param $column i32)(result i32)
    (local $array_size i32)(local $elem_size i32)(local $strides_ptr i32)
    (local $header_pointer i32)(local $array_pointer i32)(local $dimension_ptr i32)

    ;; Check size and if its smaller or equal to 0, set to 0;
    (i32.lt_s (get_local $n) (i32.const 0))
    if
        (set_local $n (i32.const 0))
    end

    get_local $class
    i32.eqz
    if
        (set_local $elem_size (call $mxarray_core_get_simple_class_byte_size (get_local $simple_class)))
    else
        (set_local $elem_size (i32.const 4)) ;; For cell_array, string, function_handle, struct
    end

    (set_local $array_size 
        (i32.mul 
            (get_local $elem_size)
            (get_local $n)))
    ;; Allocate header memory
    ;; 4 for type attribute, 4 for number of elements,  4 for array pointer,  4 for number of dimensions, 4 for dimension pointer , 4 attributes, 2 GC
    i32.const 26
    call $malloc
    tee_local $header_pointer
    ;; Allocate array memory or return -1 if size is 0
    get_local $array_size
    i32.eqz
    if  (result i32)
        i32.const 0 
        tee_local $array_pointer
    else
        get_local $array_size
        i32.const 8 ;; For capacity
        i32.add
        call $malloc
        tee_local $array_pointer
        get_local $array_size
        i32.store offset=4 align=4
        get_local $array_pointer
        i32.const 8
        i32.add
    end
    i32.store offset=8 align=4 ;; Store pointer to array



    ;; Set type attribute
    get_local $header_pointer
    get_local $class
    get_local $elem_size
    get_local $simple_class
    call $mxarray_core_set_type_attribute
    
    ;; call set size of array
    get_local $header_pointer
    get_local $n
    i32.store offset=4 align=4 ;; Store pointer to array
    ;; Set number of dimensions
    get_local $header_pointer
    i32.const 2
    i32.store offset=12 align=4 ;; Store 
    get_local $header_pointer
    i32.const  24;; ((8*2) bytes for each dimension +  8 capacity)
    call $malloc
    tee_local $dimension_ptr
    i32.const 8
    i32.add
    i32.store offset=16 align=4

    ;; Set  strides of array
    get_local $header_pointer
    i32.const  24;; ((8*2) bytes for each dimension +  8 capacity)
    call $malloc
    tee_local $strides_ptr
    i32.const 8
    i32.add
    i32.store offset=20 align=4
    ;; Set capacity in dimensions array
    get_local $strides_ptr
    i32.const 16
    i32.store offset=4 align=4
    ;; Set dimensions in dimension array
    get_local $column
    i32.const 1
    i32.eq
    if
        get_local $dimension_ptr
        get_local $n
        f64.convert_s/i32
        f64.store offset=8 align=8
        get_local $dimension_ptr
        f64.const 1
        f64.store offset=16 align=8

        get_local $strides_ptr
        f64.const 1
        f64.store offset=8 align=8
        get_local $strides_ptr
        get_local $n
        f64.convert_s/i32
        f64.store offset=16 align=8 
    else
        get_local $dimension_ptr
        f64.const 1
        f64.store offset=8 align=8
        get_local $dimension_ptr
        get_local $n
        f64.convert_s/i32
        f64.store offset=16 align=8

        get_local $strides_ptr
        f64.const 1
        f64.store offset=8 align=8
        get_local $strides_ptr
        f64.const 1
        f64.store offset=16 align=8 
    end
    get_local $header_pointer
    i32.const 0
    i32.store16 offset=24 align=2
    get_local $header_pointer
)


;; Array Operations
;; TEMPLATES
;;Array get
(export "set_array_index_i32_no_check" (func $set_array_index_i32_no_check))
(func $set_array_index_i32_no_check (param $array_ptr i32)(param $i i32)(param $value i32)
    get_local $array_ptr
    i32.load offset=8 align=4
    i32.const 4
    get_local $i
    i32.mul
    i32.add
    get_local $value
    i32.store offset=0 align=4
)
(export "get_array_index_i32_no_check" (func $get_array_index_i32_no_check))
(func $get_array_index_i32_no_check (param $array_ptr i32)(param $i i32)(result i32)
    get_local $array_ptr
    i32.load offset=8 align=4
    i32.const 4
    get_local $i
    i32.mul
    i32.add
    i32.load offset=0 align=4
)
(export "set_array_index_f64_no_check" (func $set_array_index_f64_no_check))
(func $set_array_index_f64_no_check (param $array_ptr i32)(param $i i32)(param $value f64)
    get_local $array_ptr
    i32.load offset=8 align=4
    i32.const 8
    get_local $i
    i32.mul
    i32.add
    get_local $value
    f64.store offset=0 align=8
)
(export "get_array_index_f64_no_check" (func $get_array_index_f64_no_check))
(func $get_array_index_f64_no_check (param $array_ptr i32)(param $i i32)(result f64)
    get_local $array_ptr
    i32.load offset=8 align=4
    get_local $i
    i32.const 3
    i32.shl
    i32.add
    f64.load offset=0 align=8
)
(export "get_array_index_f64" (func $get_array_index_f64))
(func $get_array_index_f64 (param $array_ptr i32)(param $i i32)(result f64)
    get_local $array_ptr
    i32.load offset=4 align=4
    get_local $i
    i32.lt_s
    if
        i32.const 3
        call $throwError
    end
    (set_local $i (i32.sub (get_local $i)(i32.const 1)))
    get_local $i
    i32.const 0
    i32.lt_s
    if
        i32.const 4
        call $throwError
    end
    i32.const 8
    get_local $i
    i32.mul
    get_local $array_ptr
    i32.load offset=8 align=4
    i32.add
    f64.load offset=0 align=4
    return
)

(export "set_array_index_f64" (func $set_array_index_f64))
(func $set_array_index_f64 (param $array_ptr i32)(param $i i32)(param $value f64)
    get_local $i
    get_local $array_ptr
    i32.load offset=4 align=4
    i32.gt_s
    if
        i32.const 10
        call $throwError
    end
    (set_local $i (i32.sub (get_local $i)(i32.const 1)))
    get_local $i
    i32.const 0
    i32.lt_s
    if
        i32.const 4
        call $throwError
    end
    get_local $array_ptr
    i32.load offset=8 align=4
    get_local $i
    i32.const 3
    i32.shl
    i32.add
    get_local $value
    f64.store offset=0 align=4
)
(; 

        Matrix Allocators  

;)


(export "mxarray_core_set_type_attribute" (func $mxarray_core_set_type_attribute))
(func $mxarray_core_set_type_attribute
    (param $address i32)
    (param $class i32)
    (param $elem_size i32)
    (param $simple_class i32)
    get_local $address
    get_local $class
    i32.store8 offset=0 align=1
    get_local $address
    get_local $elem_size
    i32.store8 offset=1 align=1
    get_local $address
    get_local $simple_class
    i32.store8 offset=2 align=1
)
;; Helpers
(export "mxarray_core_get_simple_class_byte_size" (func $mxarray_core_get_simple_class_byte_size))
(func $mxarray_core_get_simple_class_byte_size (param $simple_class i32) (result i32)
    block  block block block
        block
            get_local 0
            i32.const 4
            i32.rem_s
            br_table 0 1 2 3
        end
            i32.const 8
            return
        end
            i32.const 4
            return
        end
            i32.const 2
            return
        end
            i32.const 1
            return
    end        
    i32.const -1
    return
)
;; Test & Debug
(export "mxarray_core_get_mclass" (func $mxarray_core_get_mclass))
(func $mxarray_core_get_mclass (param $type_attr_address i32)(result i32)
    get_local $type_attr_address
    i32.load8_u offset=0 align=1
)

(export "mxarray_core_get_simple_class" (func $mxarray_core_get_simple_class))
(func $mxarray_core_get_simple_class (param $arr_ptr i32)(result i32)
    get_local $arr_ptr
    i32.load8_u offset=2 align=1
)
(export "get_array_byte_size" (func $get_array_byte_size))
(func $get_array_byte_size (param $array_ptr i32)(result i32)
    get_local $array_ptr 
    call $get_elem_byte_size
)
    (export "mxarray_core_get_array_length" (func $mxarray_core_get_array_length))
(func $mxarray_core_get_array_length (param $arr_ptr i32) (result i32)
    ;; @name array_length#memory 
    ;; @param $array i32, Pointer to array whose dimensions will be returned 
    ;; @return i32, Array length
    ;; @description
    ;;      Gets the array "total" number of items, or length
    (i32.load offset=4 align=4 (get_local $arr_ptr))
    return
)
    (export "get_elem_byte_size" (func $get_elem_byte_size))
(func $get_elem_byte_size (param $type_attr_address i32)(result i32)
    get_local $type_attr_address
    i32.load8_u offset=1 align=1
)
    ;; Test & Debug
(export "is_real" (func $is_real))
(func $is_real (param $arr i32)(result i32)
    get_local $arr
    i32.load8_u offset=21 align=1
    i32.const 1
    i32.and
)
(;
    Array Properties: is_scalar, numel, size, stride, dims, get_colon
    get_index, set_colon, set_index, compute_indeces
;)
(export "is_signed" (func $is_signed))
(func $is_signed (param $arr_ptr i32) (result i32)
    get_local $arr_ptr
    i32.load8_s offset=2 align=1
    i32.const 6 ;; All unsigned are after 6 enum
    i32.lt_s
    return
)
(export "numel" (func $numel))
(func $numel (param $arr_ptr i32) (result f64)
    get_local $arr_ptr
    i32.const -1
    i32.eq
    if
        i32.const 6
        call $throwError 
    end
    get_local $arr_ptr
    i32.load offset=4 align=4
    f64.convert_s/i32
)
(export "size_S" (func $size_S))
(func $size_S (param $arr_ptr f64)(param $target_num i32)(result i32)
    get_local $target_num
    i32.const 1
    i32.lt_s
    if
        (set_local $target_num (i32.const 2))
    end
    (return (call $fill (call $create_mxvector (get_local $target_num)(i32.const 0)(i32.const 0)(i32.const 0))(f64.const 1)))
)

(export "size_M" (func $size_M))
(func $size_M (param $arr_ptr i32)(param $target_num i32)(result i32)
    (local $i i32)(local $res_ptr i32)(local $acc f64)(local $res_ptr_data i32)
    (local $ndim i32)(local $temp i32)
    (;Contract: Assumes $target_num > 0;)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        (call $throwError (i32.const 4))
    end
    (get_local $target_num)
    i32.const 1
    i32.eq
    if
        (return (call $size (get_local $arr_ptr)))
    end
    (set_local $ndim (i32.load offset=12 align=4 (get_local $arr_ptr)))
    (tee_local $res_ptr
        (call $create_mxvector (get_local $target_num)(i32.const 0)(i32.const 0)(i32.const 0)))
    ;; Re-use register
    (set_local $arr_ptr (i32.load offset=16 align=4 (get_local $arr_ptr)))
    (set_local $res_ptr_data (i32.load offset=8 align=4 (get_local $res_ptr)))
    (i32.gt_s (get_local $target_num)(get_local $ndim))
    if

        loop 
            (set_local $temp (i32.shl (get_local $i)(i32.const 3)))
            (i32.lt_s (get_local $i)(get_local $ndim))
            if
                ;; Store dim
                (f64.store (i32.add (get_local $res_ptr_data)(get_local $temp))
                    (f64.load
                        (i32.add (get_local $arr_ptr)(get_local $temp))))
            else
                ;; store 1
                (f64.store (i32.add (get_local $res_ptr_data)(get_local $temp))(f64.const 1))
            end
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
        (br_if 0 (i32.lt_s (get_local $i)(get_local $target_num)))
        end
    else
        (i32.lt_s (get_local $target_num)(get_local $ndim))
        if
            ;; Set to the last dimension pointed to by target_num
            (set_local $acc
                (f64.load (i32.add (get_local $arr_ptr)
                        (i32.shl (i32.sub (get_local $target_num)(i32.const 1))(i32.const 3)))))
            loop
                (set_local $temp (i32.shl (get_local $i)(i32.const 3)))
                (i32.lt_s (get_local $i)(get_local $target_num))
                if
                    ;; Store dim
                    (f64.store (i32.add (get_local $res_ptr_data)(get_local $temp))(f64.load
                     (i32.add (get_local $arr_ptr)(get_local $temp))))

                else
                        (set_local $acc
                            (f64.mul (get_local $acc)
                            (f64.load (i32.add (get_local $arr_ptr)(get_local $temp)))))

                    ;; store 1
                end
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $ndim)))
            end
            (f64.store (i32.add (get_local $res_ptr_data)
                    (i32.shl (i32.sub (get_local $target_num)(i32.const 1))(i32.const 3)))(get_local $acc))
        else
            loop 
                (set_local $temp (i32.shl (get_local $i)(i32.const 3))) 
                ;; Store dim
                (f64.store (i32.add (get_local $res_ptr_data)(get_local $temp))(f64.load
                     (i32.add (get_local $arr_ptr)(get_local $temp))))
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $ndim)))
            end
        end
    end
)
(export "size" (func $size))
(func $size (param $arr_ptr i32)(result i32)
    (local $new_ptr i32)(local $i i32)(local $dim_number i32) (local $dim_ptr i32)
    (;Contract: Assumes that arr_ptr is really a MachArray ;)
    (get_local $arr_ptr)
    call $is_array
    i32.eqz
    if
        (call $throwError (i32.const 6))
    end
    (set_local $dim_number (i32.load offset=12 align=4 (get_local $arr_ptr)))
    (set_local $dim_ptr (i32.load offset=16 align=4 (get_local $arr_ptr )))
        ;; Get Dimensions
    (tee_local $new_ptr
        (call $create_mxvector 
            (get_local $dim_number)
            (i32.const 0)
            (i32.const 0)(i32.const 0)))
    
    (i32.lt_s (get_local $i)(get_local $dim_number))
    if
        (set_local $arr_ptr (i32.load offset=8 align=4 (get_local $new_ptr)))
        (set_local $i (get_local $dim_ptr))
        (set_local $dim_number (i32.add (get_local $dim_ptr)(i32.shl (get_local $dim_number)(i32.const 3))))
        loop
            (f64.store align=8 (get_local $arr_ptr)
                (f64.load align=8 (get_local $i)))
            (set_local $arr_ptr (i32.add (i32.const 8)(get_local $arr_ptr)))
            (set_local $i (i32.add (i32.const 8)(get_local $i)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $dim_number))) 
        end
    end
)
(func $mxarray_core_get_number_of_dimensions (param $arr_ptr i32) (result i32)
    (i32.load offset=12 align=4 (get_local $arr_ptr))    
)
(export "mxarray_core_get_dimensions_ptr" (func $mxarray_core_get_dimensions_ptr))
(func $mxarray_core_get_dimensions_ptr (param $arr_ptr i32) (result i32)
    (i32.load offset=16 align=4 (get_local $arr_ptr))    
)
(export "ndims" (func $ndims))
(func $ndims (param $arr_ptr i32) (result f64)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 5
        call $throwError
    end
    get_local $arr_ptr
    i32.load offset=12 align=4
    f64.convert_u/i32 
)

(export "isscalar" (func $isscalar))
(func $isscalar (param $arr_ptr i32) (result i32)
    get_local $arr_ptr
    i32.load offset=4 align=4
    i32.const 1
    i32.eq   
)

(export "ones_S" (func $ones_S))
(func $ones_S (result f64)
    f64.const 1
)
(export "length" (func $length))
(func $length (param $arr_ptr i32) (result f64)
    (local $data_ptr i32)(local $i i32)(local $ndim i32) (local $dim_ptr i32)
    (local $max f64)(local $temp f64)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        i32.const 6
        call $throwError 
    end

    (set_local $ndim (i32.load offset=12 align=4 (get_local $arr_ptr)))
    (set_local $dim_ptr (i32.load offset=16 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $ndim))
    if
        (set_local $data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
        loop
            get_local $max
            (f64.load offset=0 align=8 
                (i32.add (get_local $dim_ptr)
                (i32.shl (get_local $i)(i32.const 3)))) 
            f64.max
            set_local $max
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $ndim)))
        end
    end
    get_local $max
)

(func $is_null (param $arr_ptr i32) (result i32)
    get_local $arr_ptr
    i32.eqz
    if (result i32)
        i32.const 1
    else 
        get_local $arr_ptr
        i32.const -1
        i32.eq
        if (result i32)
            i32.const 1
        else
            i32.const 0
        end
    end
)
(export "ismatrix" (func $ismatrix))
(func $ismatrix (param $arr_ptr i32)(result i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $arr_ptr
    i32.load offset=12 align=4
    i32.const 2
    i32.eq
)
    (export "isempty" (func $isempty))
(func $isempty (param $arr_ptr i32)(result i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $arr_ptr
    i32.load offset=4 align=4
    i32.eqz
)

    (export "isrow" (func $isrow))
(func $isrow (param $arr_ptr i32)(result i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $arr_ptr
    call $mxarray_core_get_number_of_dimensions
    i32.const 2
    i32.eq

    if (result i32)
        get_local $arr_ptr
        call $mxarray_core_get_dimensions_ptr
        f64.load offset=0 align=8
        f64.const 1
        f64.eq
    else
        i32.const 0
    end
)
(export "iscolumn" (func $iscolumn))
(func $iscolumn (param $arr_ptr i32)(result i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $arr_ptr
    call $mxarray_core_get_number_of_dimensions
    i32.const 2
    i32.eq
    if (result i32)
        get_local $arr_ptr
        call $mxarray_core_get_dimensions_ptr
        f64.load offset=8 align=8
        f64.const 1
        f64.eq
    else
        i32.const 0
    end
)
    (export "isvector" (func $isvector))
(func $isvector (param $arr_ptr i32)(result i32)
    get_local $arr_ptr
    call $iscolumn
    get_local $arr_ptr
    call $isrow
    i32.or
)
    (;
    Matrix constructors
;)

(export "linespace_SSS" (func $linespace_three))
(func $linespace_three (param $low f64)(param $high f64)(param $n f64)(result i32)
    (local $step f64)(local $i f64)(local $iter i32)(local $res_ptr i32)
    (local $res_ptr_data i32)(local $n_i32 i32)
    get_local $n
    f64.const 1
    f64.lt
    if
        (call $create_mxvector (i32.const 0)
            (i32.const 0)
            (i32.const 0)
            (i32.const 0)) 
        return
    end
    (set_local $n_i32 (i32.trunc_s/f64 (get_local $n)))
    (set_local $res_ptr 
        (call $create_mxvector (get_local $n_i32)
            (i32.const 0)
            (i32.const 0)
            (i32.const 0)))
    (set_local $res_ptr_data (i32.load offset=8 align=4 (get_local $res_ptr)))

    get_local $low
    get_local $high
    f64.eq
    if
        loop
            get_local $res_ptr_data
            get_local $iter
            i32.const 3
            i32.shl
            i32.add
            get_local $low
            f64.store offset=0 align=8
            (set_local $iter (i32.add (get_local $iter)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $iter)(get_local $n_i32)))
        end  
    else
        (set_local $step 
        (f64.div 
            (f64.sub (get_local $high)(get_local $low))
            (f64.sub (get_local $i)(f64.const 1))))
        (set_local $i (get_local $low))
        loop
            get_local $res_ptr_data
            get_local $iter
            i32.const 3
            i32.shl
            i32.add
            get_local $i
            f64.store offset=0 align=8

            (set_local $iter (i32.add (get_local $iter)(i32.const 1)))
            (set_local $i (f64.add (get_local $i)(get_local $step)))
            (br_if 0 (f64.le (get_local $i)(get_local $high)))
        end
    end
    get_local $res_ptr
)
(export "linespace_SS" (func $linespace_two))
(func $linespace_two (param $low f64)(param $high f64)(result i32)
    (local $step f64)(local $i f64)(local $iter i32)(local $res_ptr i32)
    (local $res_ptr_data i32)
    (set_local $res_ptr 
        (call $create_mxvector (i32.const 100)
            (i32.const 0)
            (i32.const 0)
            (i32.const 0)))
    (set_local $res_ptr_data (i32.load offset=8 align=4 (get_local $res_ptr)))

    get_local $low
    get_local $high
    f64.eq
    if
      loop
        get_local $res_ptr_data
        get_local $iter
        i32.const 3
        i32.shl
        i32.add
        get_local $low
        f64.store offset=0 align=8
        (set_local $iter (i32.add (get_local $iter)(i32.const 1)))
        (br_if 0 (i32.lt_s (get_local $iter)(i32.const 100)))
        end  
    else 
        (set_local $step 
            (f64.div 
                (f64.sub (get_local $high)(get_local $low))
                (f64.const 99)))
        (set_local $i (get_local $low))
        loop
            get_local $res_ptr_data
            get_local $iter
            i32.const 3
            i32.shl
            i32.add
            get_local $i
            f64.store offset=0 align=8
            (set_local $iter (i32.add (get_local $iter)(i32.const 1)))
            (set_local $i (f64.add (get_local $i)(get_local $step)))
            (br_if 0 (f64.le (get_local $i)(get_local $high)))
        end
    end
    get_local $res_ptr
)
(export "colon_two" (func $colon_two))
(func $colon_two (param $i f64)(param $j f64)(result i32)
    (local $colon_ptr i32)(local $top f64)(local $i_loop f64)
        get_local $i
        f64.const 0
        f64.eq
        get_local $j
        f64.const 0
        f64.eq
        if
            (call $create_mxvector (i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
            return
        end
        get_local $i   
        get_local $j
        f64.le
        if
            get_local $i
            get_local $j
            f64.eq
            if
                ;; Return  1x1s
                (tee_local $colon_ptr 
                    (call $create_mxvector (i32.const 1)(i32.const 0)(i32.const 0)(i32.const 0)))
                i32.const 1
                get_local $i
                call $set_array_index_f64
                get_local $colon_ptr
                return
            else 
                ;; set fix(j-i)
                (set_local  $top (f64.trunc (f64.sub (get_local $j)(get_local $i))))                          
                (call $create_mxvector (i32.trunc_u/f64 (f64.add (get_local $top)(f64.const 1)))(i32.const 0)(i32.const 0)(i32.const 0))
                tee_local $colon_ptr
                ;; Enter loop
                (set_local $top (f64.add (get_local $top)(get_local $i)))
                (set_local $i_loop (get_local $i))                            
                (set_local $i (f64.sub (get_local $i)(f64.const 1)))
                loop
                    block
                    (f64.gt (get_local $i_loop)(get_local $top))
                    br_if 0
                        (call $set_array_index_f64 
                            (get_local $colon_ptr)(i32.trunc_s/f64 (f64.sub (get_local $i_loop)(get_local $i)))(get_local $i_loop))
                        (set_local $i_loop (f64.add (get_local $i_loop)(f64.const 1)))
                    br 1
                    end
                end
                return
            end
        else ;;return 1x0
            (call $create_mxvector (i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
            return
        end
)
(export "colon_three" (func $colon_three))
(func $colon_three (param $i f64)(param $j f64) (param $k f64)(result i32)
    (local $top i32)(local $colon_ptr i32)(local $i_loop i32)
            ;; If j = 0
            get_local $j
            f64.const 0
            f64.eq
            if 
                i32.const 0
                i32.const 0
                i32.const 0
                i32.const 0
                call $create_mxvector
                return
            end
            ;; if k > i
            get_local $k
            get_local $i
            f64.gt
            if (result i32)
                get_local $j
                f64.const 0
                f64.lt
                if (result i32)
                    i32.const 0 
                    i32.const 0
                    i32.const 0
                    i32.const 0
                    call $create_mxvector
                else
                    
                    ;;actual increasing order
                    ;; set fix((k-i)/j)
                    (f64.trunc (f64.div (f64.sub (get_local $k)(get_local $i))(get_local $j)))
                    tee_local $k
                    f64.const 1
                    f64.add
                    i32.trunc_u/f64                             
                    i32.const 0
                    i32.const 0
                    i32.const 0
                    call $create_mxvector
                    tee_local $colon_ptr
                    ;; Enter loop
                    (set_local $i_loop (i32.load offset=8 align=4 (get_local $colon_ptr)))     
                    (set_local $top (i32.add (get_local $i_loop)(i32.shl (i32.trunc_s/f64 (get_local $k))(i32.const 3))))
                    loop
                        (f64.store offset=0 align=8 (get_local $i_loop)(get_local $i))
                        (set_local $i_loop (i32.add (get_local $i_loop)(i32.const 8)))
                        (set_local $i (f64.add (get_local $j)(get_local $i)))
                        (br_if 0 (i32.le_s (get_local $i_loop)(get_local $top))) 
                    end 
                end
            else
                get_local $i
                get_local $k
                f64.eq
                if (result i32)
                    ;; Return 1x1 
                    i32.const 1
                    i32.const 0
                    i32.const 0
                    i32.const 0
                    call $create_mxvector
                    tee_local $colon_ptr
                    i32.const 1
                    get_local $i
                    call $set_array_index_f64
                    get_local $colon_ptr
                else
                    ;; k < i
                    ;; decreasing order 
                    get_local $j
                    f64.const 0
                    f64.gt
                    if (result i32)
                        i32.const 0 
                        i32.const 0
                        i32.const 0
                        i32.const 0
                        call $create_mxvector
                    else
                        ;; actual decreasing order
                        ;; set fix((k-i)/j)
                        (f64.trunc (f64.div (f64.sub (get_local $k)(get_local $i))(get_local $j)))
                        tee_local $k
                        f64.const 1
                        f64.add
                        i32.trunc_u/f64                             
                        i32.const 0
                        i32.const 0
                        i32.const 0
                        call $create_mxvector
                        tee_local $colon_ptr
                        ;; Enter loop
                        (set_local $i_loop (i32.load offset=8 align=4 (get_local $colon_ptr)))                            
                        (set_local $top (i32.add (get_local $i_loop)(i32.shl (i32.trunc_s/f64 (get_local $k))(i32.const 3))))
                        ;; (set_local $top (f64.add (get_local $i)(f64.mul (get_local $top)(get_local $j))))
                        ;; (set_local $i (f64.const 1))
                        ;; (set_local $i_loop (get_local $i))                            
                        loop
                            ;; (f64.gt (get_local $top)(get_local $i_loop))
                            ;; br_if 0
                                (f64.store offset=0 align=8 (get_local $i_loop)(get_local $i)) 
                                ;; (call $set_array_index_f64 
                                ;;     (get_local $colon_ptr)(i32.trunc_s/f64 (get_local $i))(get_local $i_loop))
                                (set_local $i (f64.add (get_local $i)(get_local $j)))
                                (set_local $i_loop (i32.add (get_local $i_loop)(i32.const 8)))
                            (br_if 0 (i32.le_s (get_local $i_loop)(get_local $top)))
                        end   
                    end
                end
            end
)
(export "free_macharray" (func $free_macharray))
(func $free_macharray (param $arr_ptr i32)
    (local $ptr i32)
    ;; %gc-time-start
    get_local $arr_ptr
    call $is_null
    i32.eqz
    if
        ;; %gc-macharray-allocation
        ;; Free data ptr
        get_local $arr_ptr
        i32.load offset=8 align=4

        tee_local $ptr
        call $is_null
        i32.eqz
        if
            get_local $ptr
            i32.const 8
            i32.sub
            call $free
        end
        
        ;; Free Dimensions ptr
        get_local $arr_ptr
        i32.load offset=16 align=4

        tee_local $ptr
        call $is_null
        i32.eqz
        if
            get_local $ptr
            i32.const 8
            i32.sub
            call $free
        end
        ;; Free Strides ptr
        get_local $arr_ptr
        i32.load offset=20 align=4

        tee_local $ptr
        call $is_null
        i32.eqz
        if
            get_local $ptr
            i32.const 8
            i32.sub
            call $free
        end
        ;; Free header
        get_local $arr_ptr
        call $free    
    end
    ;; %gc-time-end
)
(export "colon" (func $colon))
(func $colon (param $parameters i32)(result i32)
    (local $length i32)(local $i f64)(local $j f64)(local $k f64)
    (local $i_ptr i32)(local $j_ptr i32)(local $k_ptr i32)(local $colon_ptr i32)
    (local $top f64)(local $i_loop f64)
    get_local $parameters
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $parameters
    i32.load offset=4 align=4
    tee_local $length
    i32.const 2
    i32.lt_s
    if
        i32.const 6
        call $throwError
    end
    get_local $length
    i32.const 3
    i32.gt_s
    if
        i32.const 7
        call $throwError
    end

    get_local $parameters
    i32.const 1
    call $get_array_index_i32
    tee_local $i_ptr
    ;; Check if length is 0 for first parameter
    i32.load offset=4 align=4
    i32.eqz
    if
        i32.const 0
        i32.const 0
        i32.const 0
        i32.const 0
        call $create_mxvector
        return    
        ;; Return 0x1
    else
        ;; Check if length is 0 for second parameter
        get_local $parameters
        i32.const 2
        call $get_array_index_i32
        tee_local $j_ptr
        i32.load offset=4 align=4
        i32.eqz
        if
            i32.const 0
            i32.const 0
            i32.const 0
            i32.const 0
            call $create_mxvector
            return                ;; Return 0x1
        else
            ;; Set i and j
            get_local $i_ptr
            i32.const 1
            call $get_array_index_f64                    
            set_local $i
            get_local $j_ptr
            i32.const 1
            call $get_array_index_f64 
            set_local $j
        end
    end
    ;; Check if there are two parameters
    get_local $length
    i32.const 2
    i32.eq
    if
        get_local $i   
        get_local $j
        f64.le
        if
            get_local $i
            get_local $j
            f64.eq
            if
                ;; Return  1x1
                i32.const 1
                i32.const 0
                i32.const 0
                i32.const 0
                call $create_mxvector
                tee_local $colon_ptr
                i32.const 1
                get_local $i
                call $set_array_index_f64
                get_local $colon_ptr
                return
            else 
                ;; set fix(j-i)
                (f64.trunc (f64.sub (get_local $j)(get_local $i)))
                tee_local $top
                f64.const 1
                f64.add
                i32.trunc_u/f64                             
                i32.const 0
                i32.const 0
                i32.const 0
                call $create_mxvector
                tee_local $colon_ptr
                ;; Enter loop
                (set_local $top (f64.add (get_local $top)(get_local $i)))
                (set_local $i_loop (get_local $i))                            
                (set_local $i (f64.sub (get_local $i)(f64.const 1)))
                loop
                    block
                    (f64.gt (get_local $i_loop)(get_local $top))
                    br_if 0
                        (call $set_array_index_f64 
                            (get_local $colon_ptr)(i32.trunc_s/f64 (f64.sub (get_local $i_loop)(get_local $i)))(get_local $i_loop))
                        (set_local $i_loop (f64.add (get_local $i_loop)(f64.const 1)))
                    br 1
                    end
                end
                return
            end
        else ;;return 0x1
            i32.const 0
            i32.const 0
            i32.const 0
            i32.const 0
            call $create_mxvector
            return
        end
    else
        get_local $parameters
        i32.const 3                
        call $get_array_index_i32
        tee_local $k_ptr
        ;; Check if length is 0 for first parameter
        i32.load offset=4 align=4
        i32.eqz
        if
            i32.const 0
            i32.const 0
            i32.const 0
            i32.const 0
            call $create_mxvector
            return    
            ;; Return 0x1
        else
            
            get_local $k_ptr
            i32.const 1
            call $get_array_index_f64                    
            tee_local $k
            ;; If j = 0
            get_local $j
            f64.const 0
            f64.eq
            if
                i32.const 0
                i32.const 0
                i32.const 0
                i32.const 0
                call $create_mxvector
                return    
            end
            ;; if k > i
            get_local $i
            f64.gt
            if
                get_local $j
                f64.const 0
                f64.lt
                if
                    i32.const 0 
                    i32.const 0
                    i32.const 0
                    i32.const 0
                    call $create_mxvector
                    return
                else
                    
                    ;;actual increasing order
                    ;; set fix((k-i)/j)
                    (f64.trunc (f64.div (f64.sub (get_local $k)(get_local $i))(get_local $j)))
                    tee_local $top
                    f64.const 1
                    f64.add
                    i32.trunc_u/f64                             
                    i32.const 0
                    i32.const 0
                    i32.const 0
                    call $create_mxvector
                    tee_local $colon_ptr
                    ;; Enter loop
                    (set_local $top (f64.add (get_local $i)(f64.mul (get_local $top)(get_local $j))))
                    (set_local $i_loop (get_local $i))                            
                    (set_local $i (f64.const 1))
                    loop
                        block
                        (f64.gt (get_local $i_loop)(get_local $top))
                        br_if 0
                            (call $set_array_index_f64 
                                (get_local $colon_ptr)(i32.trunc_s/f64 (get_local $i))(get_local $i_loop))
                            (set_local $i_loop (f64.add (get_local $i_loop)(get_local $j)))
                            (set_local $i (f64.add (get_local $i)(f64.const 1)))
                        br 1
                        end
                    end
                    return    
                end
            else
                get_local $i
                get_local $k
                f64.eq
                if
                    ;; Return 1x1 
                    i32.const 1
                    i32.const 0
                    i32.const 0
                    i32.const 0
                    call $create_mxvector
                    tee_local $colon_ptr
                    i32.const 1
                    get_local $i
                    call $set_array_index_f64
                    get_local $colon_ptr
                    return
                else
                    ;; k < i
                    ;; decreasing order 
                    get_local $j
                    f64.const 0
                    f64.gt
                    if
                        i32.const 0 
                        i32.const 0
                        i32.const 0
                        i32.const 0
                        call $create_mxvector
                        return
                    else
                        ;; actual decreasing order
                        ;; set fix((k-i)/j)
                        (f64.trunc (f64.div (f64.sub (get_local $k)(get_local $i))(get_local $j)))
                        tee_local $top
                        f64.const 1
                        f64.add
                        i32.trunc_u/f64                             
                        i32.const 0
                        i32.const 0
                        i32.const 0
                        call $create_mxvector
                        tee_local $colon_ptr
                        ;; Enter loop
                        (set_local $top (f64.add (get_local $i)(f64.mul (get_local $top)(get_local $j))))
                        (set_local $i_loop (get_local $i))                            
                        (set_local $i (f64.const 1))
                        loop
                            block
                            (f64.gt (get_local $top)(get_local $i_loop))
                            br_if 0
                                (call $set_array_index_f64 
                                    (get_local $colon_ptr)(i32.trunc_s/f64 (get_local $i))(get_local $i_loop))
                                (set_local $i_loop (f64.add (get_local $i_loop)(get_local $j)))
                                (set_local $i (f64.add (get_local $i)(f64.const 1)))
                            br 1
                            end
                        end
                        return     
                    end
                end
            end
        end
    end
    i32.const 0
    return

)
(export "clone" (func $clone))
(func $clone (param $arr_ptr i32) (result i32)
    (local $arr_ptr_data i32)(local $new_arr_ptr i32)
    (local $new_arr_ptr_data i32)(local $i i32)(local $len i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    (set_local $new_arr_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    (set_local $new_arr_ptr_data (i32.load offset=8 align=4 (get_local $new_arr_ptr)))
    (set_local $arr_ptr_data (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $new_arr_ptr_data
            get_local $i
            i32.add

            get_local $arr_ptr_data
            get_local $i
            i32.add
            f64.load offset=0 align=8
            
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end 
    end
    get_local $new_arr_ptr
    ;; get_local $arr_ptr
    ;; i32.const 0
    ;; call $size
    ;; i32.const 0
    ;; i32.const 0
    ;; i32.const 0
    ;; call $create_mxarray_ND
    ;; tee_local $new_arr_ptr
    ;; loop
    ;;     block
    ;;     (i32.gt_s (get_local $i)(get_local $len))
    ;;     br_if 0

    ;;         (call $set_array_index_f64 (get_local $new_arr_ptr)(get_local $i)
    ;;             (call $get_array_index_f64 (get_local $arr_ptr)(get_local $i)))
    ;;         (set_local $i (i32.add (get_local $i) (i32.const 1)))
    ;;     br 1
    ;;     end
    ;; end
)
(export "set_array_value_multiple_indeces_f64" (func $set_array_value_multiple_indeces_f64))
(func $set_array_value_multiple_indeces_f64 (param $arr_ptr i32) (param $indices_ptr i32)(param $val f64) 
    get_local $arr_ptr
    (call $calculate_index_array_offset_f64 (get_local $arr_ptr)(get_local $indices_ptr))
    i32.const 1
    i32.add
    get_local $val
    call $set_array_index_f64
)
(export "get_array_value_multiple_indeces_f64" (func $get_array_value_multiple_indeces_f64))
(func $get_array_value_multiple_indeces_f64 (param $arr_ptr i32) (param $indices_ptr i32)(result f64)
    get_local $arr_ptr
    (call $calculate_index_array_offset_f64 (get_local $arr_ptr)(get_local $indices_ptr))
    i32.const 1
    i32.add
    call $get_array_index_f64
)
(func $calculate_index_array_offset_f64 (param $arr_ptr i32)(param $indices_ptr i32)(result i32)
    (local $shape_ptr i32)(local $len_shape i32)(local $len_indeces i32)(local $i i32)(local $offset i32)
    (local $stride f64)
    (set_local $shape_ptr (call $mxarray_core_get_dimensions_ptr (get_local $arr_ptr)))
    ;; (set_local $len_shape (i32.load offset=4 align=4 (get_local $indices_ptr)))
    (set_local $len_indeces (i32.load offset=4 align=4 (get_local $indices_ptr)))
    (set_local $stride (f64.const 1))
    (set_local $offset (i32.sub (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $indices_ptr)(i32.const 1)))(i32.const 1)))
    (set_local $i (i32.const 2))
    loop
        block
            (br_if 0 (i32.gt_u (get_local $i)(get_local $len_indeces)))
            (set_local $stride (f64.mul 
                    (f64.load offset=0 align=8 (i32.add (get_local $shape_ptr)(i32.mul 
                                (i32.const 8)
                                (i32.sub (get_local $i)(i32.const 2)))))
                    (get_local $stride)))
            (set_local $offset 
                (i32.add 
                    (get_local $offset)
                    (i32.trunc_s/f64 (f64.mul 
                        (get_local $stride)
                        (f64.sub (call $get_array_index_f64 (get_local $indices_ptr)(get_local $i))(f64.const 1))
                    ))
                )
            )
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1
        end
    end
    get_local $offset
)

(export "get_f64" (func $get_f64))
(func $get_f64 (param $array_ptr i32)(param $indices i32)(result i32)
    (local $shape_ptr i32)(local $res_ptr i32)(local $dim_ptr i32)
    (;TODO(dherre3): Check that input is actually an mxarray ;)
    (;TODO(dherre3): If input is only one array, the dimensions are the dimensions of the input array;)
    get_local $array_ptr
    call $is_null
    get_local $indices
    call $is_null
    i32.or
    if
        i32.const 6
        call $throwError
    end
;;    i32.const 0
    get_local $array_ptr 
    get_local $array_ptr

    call $size
    tee_local $shape_ptr
    get_local $indices

    call $verify_and_get_dimensions
    set_local $dim_ptr
    ;; Create 
    (call $create_mxarray_ND (get_local $dim_ptr)(i32.const 0)(i32.const 0)(i32.const 0))

    ;; Calling get colon
    tee_local $res_ptr
    get_local $array_ptr
    get_local $shape_ptr
    get_local $indices
    i32.const 1 ;; dim
    i32.const 1 ;; stride
    i32.const 0 ;; offset
    i32.const 1 ;; multi_ind
    i32.const 0 ;; offset_ind
    i32.const 0 ;; is_set_colon
    call $get_or_set_colon

    (call $free_macharray (get_local $shape_ptr))
    (call $free_macharray (get_local $dim_ptr))
    get_local $res_ptr
)
;; [1,2,3,4], spread
(;
    ;; Size of input index.
    loop
    

    end

;)


(export "get_f64_one_index_SM" (func $get_f64_one_index_SM))
(func $get_f64_one_index_SM (param $scalar f64)(param $index i32)(result i32)
    (local $res_arr i32)(local $arr_data_ptr i32)
    (local $step i32)(local $len i32)
    get_local $index
    call $is_array
    i32.eqz 
    if
        i32.const 6
        call $throwError
    end 
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $index)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $index))(i32.const 3)))
    (i32.lt_s (get_local $step)(get_local $len))
    if
        loop
            (f64.load (i32.add (get_local $arr_data_ptr)(get_local $step)))
            f64.const 1
            f64.ne
            if
                i32.const 4
                call $throwError
            end
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    (set_local $res_arr (call $copy_mxarray_structure (get_local $index)))
    (call $fill (get_local $res_arr)(get_local $scalar))
)
(export "get_f64_one_index_colon" (func $get_f64_one_index_colon))
(func $get_f64_one_index_colon (param $array_ptr i32)(param $colon_ptr i32)

)
(export "get_f64_one_index_spread" (func $get_f64_one_index_spread ))

(; Function represents the get_spread for one index ;)
(func $get_f64_one_index_spread (param $array_ptr i32)(result i32)
    (local $res_ptr i32)(local $res_data_ptr i32)(local $array_data_ptr i32)(local $i i32)(local $len i32)
    get_local $array_ptr
    call $is_array
    i32.eqz 
    if
        i32.const 6
        call $throwError
    end
    (set_local $len (i32.load offset=4 align=4 (get_local $array_ptr)))
    (set_local $res_ptr (call $create_mxvector (get_local $len)(i32.const 0)(i32.const 0)(i32.const 0)))
    (set_local $res_data_ptr (i32.load offset=8 align=4 (get_local $res_ptr)))

    (set_local $array_data_ptr (i32.load offset=8 align=4 (get_local $array_ptr)))    

    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $res_data_ptr
            get_local $i
            i32.const 3
            i32.shl
            i32.add

            get_local $array_data_ptr
            get_local $i
            i32.const 3
            i32.shl
            i32.add
            f64.load offset=0 align=4

           f64.store offset=0 align=4 
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $res_ptr
)

(export "set_f64" (func $set_f64))
(func $set_f64 (param $array_ptr i32)(param $indices_ptr i32)(param $values_ptr i32)
    (local $shape_ptr i32)(local $dim_ptr i32)(local $dim_change i32)


    get_local $array_ptr
    call $is_null
    get_local $indices_ptr
    call $is_null
    i32.or
    get_local $values_ptr
    call $is_null
    i32.or 
    if
        i32.const 6
        call $throwError
    end

    get_local $array_ptr 
    get_local $array_ptr

    call $size
    tee_local $shape_ptr
    get_local $indices_ptr
    get_local $values_ptr
    call $verify_set_dimensions_and_values ;; Verify values
    

    tee_local $dim_change
    i32.const 1
    i32.eq
    if
        get_local $array_ptr
        call $clone
        return
    else
        get_local $dim_change
        i32.const 1
        i32.gt_s
        if
            (set_local $values_ptr (get_local $dim_change))
            (set_local $dim_change (i32.const -2))
        end
    end

;;    get_local $array_ptr
    get_local $values_ptr
    get_local $array_ptr
    get_local $shape_ptr
    get_local $indices_ptr
    i32.const 1 ;; dim
    i32.const 1 ;; stride
    i32.const 0 ;; offset
    i32.const 1 ;; stride_ind
    i32.const 0 ;; offset_ind
    i32.const 1 ;; is_set_colon
    call $get_or_set_colon

    get_local $dim_change
    i32.const -2
    i32.eq
    if
        (call $free_macharray (get_local $values_ptr))
    end
    (call $free_macharray (get_local $shape_ptr))
  
)
(func $create_colon_obj_1d (param $arr_ptr i32)(param $dim i32)(param $num i32)(param $low f64)(param $high f64) (param $step f64)(result i32)
    (local $shape_data_ptr i32)
    (;
        dim: Expects the dim to be zeroed index
        num:
            0.    :
            1. low:high
            2. low:step:high
            3. low:end
            4. low:step:end
    ;)

    (set_local $shape_data_ptr (i32.load offset=16 align=4 (get_local $arr_ptr)))

    block block block block block block
        get_local $num
        br_table 0 1 2 3 4 5 
        (;case : ;)
        (set_local $low (f64.const 0))
        (set_local $step (f64.const 1))
        (set_local $high 
            (f64.sub (f64.load offset=4 align=4 (get_local $arr_ptr))(f64.const 1)))

        end
            (;case low:high ;)
            (set_local $step (f64.const 1))
        end
            (; Case low:step:high;)
            ;; Nothing to do
        end
            (; Case low:high ;)
            (set_local $step (f64.const 1))
            (set_local $high 
                (f64.sub (f64.load offset=0 align=4 
                    (i32.add (get_local $shape_data_ptr)
                    (i32.shl (get_local $dim)(i32.const 3))))(f64.const 1)))
        end
            (; Case low:end ;)
            (set_local $step (f64.const 1))
            (set_local $high 
                (f64.sub (f64.load offset=0 align=4 
                    (i32.add (get_local $shape_data_ptr)
                    (i32.shl (get_local $dim)(i32.const 3))))(f64.const 1)))
        end
            (; Case low:step:end;)
            (set_local $high 
                (f64.sub (f64.load offset=0 align=4 
                    (i32.add (get_local $shape_data_ptr)
                (i32.shl (get_local $dim)(i32.const 3))))(f64.const 1)))
    end

    (set_local $shape_data_ptr (call $create_meta_input_vec (i32.const 1)(i32.const 3)(i32.const 0)))
    (f64.store offset=8 align=8 (get_local $shape_data_ptr)(get_local $low))
    (f64.store offset=16 align=8 (get_local $shape_data_ptr)(get_local $step))
    (f64.store offset=24 align=8 (get_local $shape_data_ptr)(get_local $high))
    get_local $shape_data_ptr 


)
(func $create_colon_obj_dim (param $arr_ptr i32)(param $dim i32)(param $num i32)(param $low f64)(param $high f64) (param $step f64)(result i32)
    (local $shape_data_ptr i32)
    (;
        dim: Expects the dim to be zeroed index
        num:
            0.    :
            1. low:high
            2. low:step:high
            3. low:end
            4. low:step:end
    ;)
    (i32.gt_s (i32.load offset=12 align=4 (get_local $arr_ptr))(get_local $dim))
    if
        (call $throwError (i32.const 16))
    end

    (set_local $shape_data_ptr (i32.load offset=16 align=4 (get_local $arr_ptr)))

    block block block block block block
        get_local $num
        br_table 0 1 2 3 4 5 
        (;case : ;)
        (set_local $low (f64.const 0))
        (set_local $step (f64.const 1))
        (set_local $high 
            (f64.sub (f64.load offset=0 align=4 
                (i32.add (get_local $shape_data_ptr)
                (i32.shl (get_local $dim)(i32.const 3))))(f64.const 1)))

        end
            (;case low:high ;)
            (set_local $step (f64.const 1))
        end
            (; Case low:step:high;)
            ;; Nothing to do
        end
            (; Case low:high ;)
            (set_local $step (f64.const 1))
            (set_local $high 
                (f64.sub (f64.load offset=0 align=4 
                    (i32.add (get_local $shape_data_ptr)
                    (i32.shl (get_local $dim)(i32.const 3))))(f64.const 1)))
        end
            (; Case low:end ;)
            (set_local $step (f64.const 1))
            (set_local $high 
                (f64.sub (f64.load offset=0 align=4 
                    (i32.add (get_local $shape_data_ptr)
                    (i32.shl (get_local $dim)(i32.const 3))))(f64.const 1)))
        end
            (; Case low:step:end;)
            (set_local $high 
                (f64.sub (f64.load offset=0 align=4 
                    (i32.add (get_local $shape_data_ptr)
                (i32.shl (get_local $dim)(i32.const 3))))(f64.const 1)))
    end

    (set_local $shape_data_ptr (call $create_meta_input_vec (i32.const 1)(i32.const 3)(i32.const 0)))
    (f64.store offset=8 align=8 (get_local $shape_data_ptr)(get_local $low))
    (f64.store offset=16 align=8 (get_local $shape_data_ptr)(get_local $step))
    (f64.store offset=24 align=8 (get_local $shape_data_ptr)(get_local $high))
    get_local $shape_data_ptr
)
(func $create_meta_input_vec (param $type i32)(param $number i32)(param $is_vec_input i32)(result i32)
    (local $input_vec i32)
    (;
        Layout: type:1byte,is_4:1byte,empty_byte,empty_byte,num:number,data.
        Controls meta-information for matwably (Modified: 15/03/19)
        type:
            0: Input/outputs
            1: Colon
            2: MachArray
            3: Scalar
    ;)
    get_local $is_vec_input
    if (result i32)
        get_local $number
        i32.const 2
        i32.shl
    else
        get_local $number
        i32.const 3
        i32.shl
    end
    i32.const 8 
    i32.add
    call $malloc
    tee_local $input_vec
    get_local $type
    i32.store8
    get_local $input_vec
    get_local $is_vec_input
    i32.store8 offset=1
    get_local $input_vec
    get_local $number
    i32.store offset=4
    get_local $input_vec
)

(export "get_stride_f64_all_M" (func $get_stride_f64_all))
(func $get_stride_f64_all (param $arr_ptr i32)(result i32)
    (local $res_ptr i32)(local $len i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        (call $throwError (i32.const 5))
    end
    (tee_local $res_ptr (call $create_mxvector (tee_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))(i32.const 0)(i32.const 0)(i32.const 1)))
    (call $memcpy (i32.load offset=8 align=4 (get_local $arr_ptr))(i32.load offset=8 align=4 (get_local $res_ptr))(i32.shl (get_local $len)(i32.const 3)))
)

(export "set_stride_f64_all_MS" (func $set_stride_f64_all_MS))
(func $set_stride_f64_all_MS (param $arr_ptr i32)(param $value f64)
    (local $len i32)(local $values_len i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        (call $throwError (i32.const 18))
    end
    (call $fill (get_local $arr_ptr)(get_local $value))
    drop
      
)
(export "set_stride_f64_all_MM" (func $set_stride_f64_all_MM))
(func $set_stride_f64_all_MM (param $arr_ptr i32)(param $values_ptr i32)
    (local $len i32)(local $values_len i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
     get_local $values_ptr
    call $is_array
    i32.eqz
    i32.or
    if
        (call $throwError (i32.const 18))
    end
    (tee_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (tee_local $values_len (i32.load offset=4 align=4 (get_local $values_ptr)))
    i32.eq
    if
        (call $memcpy (i32.load offset=8 align=4 (get_local $values_ptr))(i32.load offset=8 align=4 (get_local $arr_ptr))(i32.shl (get_local $len)(i32.const 3)))
    else
        get_local $len
        i32.const 0
        i32.gt_s
        get_local $values_len
        i32.const 1
        i32.eq
        i32.and
        if
            (call $fill (get_local $arr_ptr)(f64.load offset=0 align=8 (i32.load offset=8 align=4 (get_local $values_ptr))))
            drop
        else
            (call $throwError (i32.const 4)) ;; Error: src, and dest array must have the same number of values
        end        
    end
)
(export "memcpy" (func $memcpy))
(func $memcpy (param $src i32)(param $dest i32)(param $len i32)
   (local $rem i32)
    (set_local $rem (i32.rem_u (get_local $len)(i32.const 8)))
    get_local $len
    get_local $rem
    i32.sub
    i32.const 0
    i32.gt_s
    if
        (set_local $len (i32.add (get_local $src)(get_local $len)))
        loop
            (i64.store (get_local $dest)(i64.load (get_local $src)))
            (set_local $src (i32.add (get_local $src)(i32.const 8)))
            (set_local $dest (i32.add (get_local $dest)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $src)(get_local $len)))
        end
    end

    get_local $rem
    if
        (set_local $rem (i32.add (get_local $rem)(get_local $src)))
        loop
            (i32.store8 (get_local $dest)(i32.load8_u (get_local $src)))
            (set_local $src (i32.add (get_local $src)(i32.const 1)))
            (set_local $dest (i32.add (get_local $dest)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $src)(get_local $rem)))
        end
    end
)
(func $get_strides_f64_2D (param $arr_ptr i32)(param $dim1_ptr i32)(param $dim2_ptr i32) (result i32)
   (local $low i32)(local $step i32)(local $high i32)(local $low2 i32)(local $step2 i32)(local $high2 i32)
   (local $i i32)(local $j i32)(local $offsetSoFar i32)(local $offsetResSoFar i32)(local $arr_strides_dim1 i32)(local $arr_strides_dim2 i32)
   (local $res_strides_dim1 i32)(local $res_strides_dim2 i32)
   (local $i_res i32)(local $j_res i32)(local $arr_dim_ptr i32)(local $arr_data_ptr i32)(local $res_data_ptr i32)(local $res_ptr i32)
    (local $res_dim1 f64)(local $res_dim2 f64)
   get_local $arr_ptr
   call $is_array
   i32.eqz
   if
        (call $throwError (i32.const 7))
   end

   (set_local $arr_dim_ptr (i32.load offset=16 align=4 (get_local $arr_ptr)))
   (set_local $low (i32.trunc_s/f64 (f64.load offset=16 (get_local $dim1_ptr))))
   (i32.eq (i32.load offset=8 (get_local $dim1_ptr))(i32.const 2))
   if
        (set_local $step (i32.const 1))
        (set_local $high (i32.trunc_s/f64 (f64.load offset=16 (get_local $dim1_ptr))))
   else
        (set_local $step (i32.trunc_s/f64 (f64.load offset=16 (get_local $dim1_ptr))))
        (set_local $high (i32.trunc_s/f64 (f64.load offset=24 (get_local $dim1_ptr))))
   end

    (set_local $low2 (i32.trunc_s/f64 (f64.load offset=16 (get_local $dim2_ptr))))
   (i32.eq (i32.load offset=8 (get_local $dim2_ptr)(i32.const 2)))
   if
        (set_local $step2 (i32.const 1))
        (set_local $high2 (i32.trunc_s/f64 (f64.load offset=16 (get_local $dim2_ptr))))
   else
        (set_local $step2 (i32.trunc_s/f64 (f64.load offset=16 (get_local $dim2_ptr))))
        (set_local $high2 (i32.trunc_s/f64 (f64.load offset=24 (get_local $dim2_ptr))))
   end

    (tee_local $res_dim1 (f64.convert_s/i32 (i32.add (i32.div_s (i32.sub (get_local $high)(get_local $low))(get_local $step))(i32.const 1))))
    f64.const 0
    f64.lt
    if
        (set_local $res_dim1 (f64.const 0)) 
    end
    (tee_local $res_dim2 (f64.convert_s/i32 (i32.add (i32.div_s (i32.sub (get_local $high2)(get_local $low2))(get_local $step2))(i32.const 1))))
    f64.const 0
    f64.lt
    if
        (set_local $res_dim2 (f64.const 0)) 
    end

    (i32.lt_s (get_local $step)(i32.const 0))
    if
            (return (call $create_mxvector (i32.const 1)(i32.const 0)(i32.const 0)(i32.const 0)))
    else
       (i32.lt_s (get_local $high)(i32.const 0)) 
        if
            (return (call $create_mxvector (i32.const 1)(i32.const 0)(i32.const 0)(i32.const 0)))
        end
    end 
    (if (i32.lt_s (get_local $low)(i32.const -1)) (then (call $throwError (i32.const 4))))
    (if (i32.lt_s (get_local $high)(i32.sub (i32.trunc_s/f64 (f64.load (get_local $arr_dim_ptr)))(i32.const 1))) (then (call $throwError (i32.const 4))))

    (i32.lt_s (get_local $step2)(i32.const 0))
    if
        (return (call $create_mxarray_2D (get_local $res_dim1)(f64.const 0)))   
    else
       (i32.lt_s (get_local $high2)(i32.const 0)) 
        if
                (return (call $create_mxarray_2D (get_local $res_dim1)(f64.const 0)))   
        end
    end
   (if (i32.lt_s (get_local $low2)(i32.const -1)) (then (call $throwError (i32.const 4))))
   (if (i32.lt_s (get_local $high2)(i32.sub (i32.trunc_s/f64 (f64.load offset=8 align=8 (get_local $arr_dim_ptr)))(i32.const 1))) (then (call $throwError (i32.const 4)))) 
    
   (set_local $res_ptr (call $create_mxarray_2D (get_local $res_dim1)(get_local $res_dim2)))
   (set_local $res_data_ptr (i32.load offset=8 align=4 (get_local $res_ptr)))
   (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
   (tee_local $i (get_local $low))
   (get_local $high)
   i32.le_s
   if

        (set_local $offsetSoFar (i32.load offset=20 align=4 (get_local $arr_ptr)))
        (set_local $arr_strides_dim1 (i32.trunc_s/f64 (f64.load offset=0 align=8 (get_local $offsetSoFar))))
        (set_local $arr_strides_dim1 (i32.trunc_s/f64 (f64.load offset=8 align=8 (get_local $offsetSoFar))))
        (set_local $offsetResSoFar (i32.load offset=20 align=4 (get_local $res_ptr)))
        (set_local $res_strides_dim1 (i32.trunc_s/f64 (f64.load offset=0 align=8 (get_local $offsetResSoFar))))
        (set_local $res_strides_dim2 (i32.trunc_s/f64 (f64.load offset=8 align=8 (get_local $offsetResSoFar))))
        loop
            (set_local $offsetSoFar (i32.mul (get_local $i)(get_local $arr_strides_dim1)))
            (set_local $offsetResSoFar (i32.mul (get_local $i)(get_local $res_strides_dim1)))
            (set_local $j_res (i32.const 0))
            (tee_local $j (get_local $low2))
            (get_local $high2)
            i32.le_s
            if
                loop
                    (f64.store offset=0 align=8 
                        (i32.add (get_local $res_data_ptr)
                            (i32.shl (i32.add (get_local $offsetResSoFar)
                            (i32.mul (get_local $j)(get_local $res_strides_dim2)))(i32.const 3)))
                        (f64.load offset=0 align=8
                            (i32.add (get_local $arr_data_ptr)(i32.shl (i32.add (get_local $offsetSoFar)
                            (i32.mul (get_local $j)(get_local $arr_strides_dim2)))(i32.const 3))))) 
                    (set_local $j_res (i32.add (get_local $j_res)(i32.const 1)))
                    (set_local $j (i32.add (get_local $j)(get_local $step2)))
                    (br_if 0 (i32.le_s (get_local $j)(get_local $high2)))
                end
            end
            (set_local $i_res (i32.add (get_local $i_res)(i32.const 1)))
            (set_local $i (i32.add (get_local $i)(get_local $step)))
            (br_if 0 (i32.le_s (get_local $i)(get_local $high)))
        end
   end
   (get_local $res_ptr) 
)
(;values/*:Array<number>*/, target/*:Array<number>*/,shape/*:Array<number>*/,
        indeces/*:Array<Array<number>>*/,dim/*:number*/, mult/*:number*/, offset/*:number*/
        ,mult_ind/*:number*/, offset_ind/*:number*/;)
(func $get_or_set_colon (param $values_ptr i32)(param $arr_ptr i32)(param $shape_ptr i32)
    (param $indices_ptr i32)(param $dim i32)(param $mult i32)(param $offset i32)
    (param $mult_ind i32)(param $offset_ind i32)(param $is_set_colon i32)
    (local $dim_length i32)(local $ind i32)(local $new_mult i32)(local $dim_ptr i32)
    (local $new_offset i32)(local $new_mult_ind i32)(local $new_offset_ind i32)
    (set_local $dim_ptr (call $get_array_index_i32 (get_local $indices_ptr)(get_local $dim)))
    (set_local $dim_length (i32.load offset=4 align=4 (get_local $dim_ptr)))
    loop
        block
        (i32.ge_s (get_local $ind)(get_local $dim_length))
        br_if 0
            (set_local $new_offset (i32.add (get_local $offset)
                (i32.mul (get_local $mult)(i32.sub (i32.trunc_s/f64 
                    (call $get_array_index_f64 (get_local $dim_ptr)
                        (i32.add (i32.const 1)(get_local $ind))))(i32.const 1)))))
            (set_local $new_mult (i32.mul (get_local $mult)(i32.trunc_s/f64 
                (call $get_array_index_f64 (get_local $shape_ptr)(get_local $dim)))))
            (set_local $new_offset_ind (i32.add (get_local $offset_ind)
                (i32.mul (get_local $mult_ind)(get_local $ind))))
            (set_local $new_mult_ind (i32.mul (get_local $mult_ind)(i32.load offset=4 align=4 (get_local $dim_ptr))))
            get_local $dim
            (i32.load offset=4 align=4 (get_local $indices_ptr))
            i32.eq
            if
                get_local $is_set_colon
                if
                    get_local $values_ptr
                    drop
                    get_local $new_offset_ind
                    drop
                    get_local $arr_ptr
                    get_local $new_offset
                    i32.const 1
                    i32.add
                    (call $get_array_index_f64 (get_local $values_ptr)(i32.add (i32.const 1)(get_local $new_offset_ind)))
                    call $set_array_index_f64
                else
                    get_local $values_ptr
                    get_local $new_offset_ind
                    i32.const 1
                    i32.add
                    (call $get_array_index_f64 (get_local $arr_ptr)(i32.add (i32.const 1)(get_local $new_offset)))
                    call $set_array_index_f64
                end
            else
                (call $get_or_set_colon (get_local $values_ptr)(get_local $arr_ptr)(get_local $shape_ptr)
                    (get_local $indices_ptr)(i32.add (get_local $dim)(i32.const 1))
                        (get_local $new_mult)(get_local $new_offset)(get_local $new_mult_ind)
                            (get_local $new_offset_ind)(get_local $is_set_colon))
            end
            (set_local $ind (i32.add (get_local $ind)(i32.const 1)))
        br 1
        end
    end  
)
(func $get_real_mxarray_dim_number_input_vector (param $indices_ptr i32) (result i32)
    (local $index_value i32)
    (;
            Checks vector the size of the get_indices values. Finds where the last index that is not 1 is and returns the right size vector
        ;;  Requires: $indices_ptr is not null and is a cell_vector of heterogenous arrays.
    ;)
    
    (call $get_mxarray_dimension_number (get_local $indices_ptr))
    tee_local $index_value
    i32.const 1
    i32.eq
    if (result i32)
        get_local $index_value
        i32.const 1
        i32.add
    else
        get_local $index_value
    end
)
(func $verify_and_get_dimensions (param $arr_ptr i32) (param $shape_ptr i32)
        (param $indices_ptr i32)(result i32)
    (local $len_arr i32)(local $dim_ptr i32)(local $total_elem i32)(local $i i32)(local $j i32)(local $iarr i32)
    (local $length_indices i32)(local $length_dim i32)(local $current_dim i32)(local $ind f64)(local $jarr i32)
    (local $multi_index_len i32)
    ;; Get total array length
    (set_local $len_arr (i32.load offset=4 align=4 (get_local $arr_ptr)))

    ;; Get length of indices
    (tee_local $length_indices (i32.load offset=4 align=4 (get_local $indices_ptr)))
    i32.const 1
    i32.gt_s
    if
        (set_local $multi_index_len (call $get_real_mxarray_dim_number_input_vector (get_local $indices_ptr)))
        (set_local $dim_ptr (call $create_mxvector (get_local $multi_index_len)(i32.const 0)(i32.const 0)(i32.const 0)))
    else
        (set_local $dim_ptr (call $create_mxvector (get_local $length_indices)(i32.const 0)(i32.const 0)(i32.const 0)))   
    end
    (set_local $iarr (i32.const 1))
    (set_local $jarr (i32.const 1))
    loop
        block
            (i32.ge_s (get_local $i)(get_local $length_indices))
            br_if 0
                (tee_local $current_dim (call $get_array_index_i32 (get_local $indices_ptr)
                        (get_local $iarr)))
                ;; Only save into dim_ptr when the mult_index_len which represents when the inputs a larger than 2 is 0, which means there is one input
                ;; or when the iarr is smaller than the multi_index_len, this prevents creating a new array with size [2,4,1,1,1,1] for instance. 
                ;; In this case mult_index_len would point to 2. Making new array dimensions really [2,4] 
                (set_local $length_dim (i32.load offset=4 align=4 (get_local $current_dim)))
                get_local $multi_index_len
                i32.const 0
                i32.eq
                get_local $iarr
                get_local $multi_index_len
                i32.le_s
                i32.or 
                if         
                    (call $set_array_index_f64 (get_local $dim_ptr)(get_local $iarr)(f64.convert_s/i32 (get_local $length_dim)))
                end
                loop
                    block
                        (i32.ge_s (get_local $j)(get_local $length_dim))
                        br_if 0                            
                            (tee_local $ind (call $get_array_index_f64 (get_local $current_dim)(get_local $jarr)))
                            f64.const 0
                            f64.le
                            if
                                ;; throw error
                                i32.const 4
                                call $throwError
                            end
                            get_local $length_indices
                            i32.const 1
                            i32.gt_u
                            get_local $ind
                            get_local $shape_ptr
                            get_local $iarr
                            call $get_array_index_f64
                            f64.gt
                            i32.and
                            if
                                i32.const 3
                                call $throwError
                            end

                            get_local $len_arr
                            i32.const 1
                            i32.eq
                            if
                                get_local $ind
                                i32.trunc_s/f64
                                get_local $len_arr
                                i32.gt_s
                                if
                                    i32.const 3
                                    call $throwError
                                end
                            end
                            (set_local $jarr (i32.add (get_local $jarr)(i32.const 1)))
                            (set_local $j (i32.add (get_local $j)(i32.const 1)))
                        br 1    
                    end
                end
                (set_local $j (i32.const 0))
                (set_local $jarr (i32.const 1))
                (set_local $iarr (i32.add (get_local $iarr)(i32.const 1)))
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1    
        end
    end
    get_local $length_indices
    i32.const 1
    i32.eq
    if 
        (set_local $dim_ptr (call $size (call $get_array_index_i32 (get_local $indices_ptr)(i32.const 1))))
            get_local $arr_ptr
            call $iscolumn
            if
            ;; Swap dimensions                
            (call $set_array_index_f64 (get_local $dim_ptr) 
                (i32.const 1)
                (call $get_array_index_f64 (get_local $dim_ptr)(i32.const 2))
            )
            (call $set_array_index_f64 (get_local $dim_ptr) 
                (i32.const 2)(f64.const 1))
            end   
    end
    get_local $dim_ptr
)
(func $verify_set_dimensions_and_values (param $arr_ptr i32) (param $shape_ptr i32)
        (param $indices_ptr i32)(param $values_ptr i32)(result i32)
        (local $len_arr i32)(local $total_elem i32)(local $i i32)(local $j i32)
        (local $length_indices i32)(local $length_dim i32)(local $total_length i32)(local $current_dim i32)(local $ind f64)
        (local $value_dim_ptr i32)(local $value_dim_index i32)(local $value_length i32)(local $temp_val_arr i32)

    (set_local $len_arr (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $total_length (i32.const 0))
    (set_local $length_indices (i32.load offset=4 align=4 (get_local $indices_ptr)))
    (set_local $value_dim_ptr (i32.load offset=16 align=4 (get_local $values_ptr)))
    (set_local $value_dim_index (i32.const 1))

    loop
        block
            (i32.ge_s (get_local $i)(get_local $length_indices))
            br_if 0    
                (tee_local $current_dim (call $get_array_index_i32 (get_local $indices_ptr)
                    (i32.add (i32.const 1)(get_local $i))))            
                call $isrow                    
                i32.eqz
                get_local $length_indices
                i32.const 1
                i32.gt_s
                i32.and
                if
                    i32.const 5
                    call $throwError
                end

                get_local $i
                i32.eqz
                if
                    i32.const 1
                    set_local $total_length
                end
                (tee_local $length_dim (i32.load offset=4 align=4 (get_local $current_dim)))

                i32.const 1
                i32.gt_s
                get_local $length_indices
                i32.const 1
                i32.gt_s
                i32.and
                if
                    ;; (call $get_array_index_f64 (get_local $value_dim_ptr)(get_local $value_dim_index))
                    get_local $value_dim_ptr
                    get_local $value_dim_index
                    i32.const 1
                    i32.sub
                    i32.const 3
                    i32.shl
                    i32.add
                    f64.load offset=0 align=8 
                    i32.trunc_s/f64
                    tee_local $value_length
                    get_local $length_dim
                    i32.ne
                    if 
                        ;; TODO(dherre3): Make this efficient, instead of allocating another array with the value
                        ;; Call a function that just sets given indices with a predetermined value.
                        get_local $value_length
                        i32.const 1
                        i32.ne
                        if
                            i32.const 9
                            call $throwError
                        end
                    end
                    (set_local $value_dim_index (i32.add (get_local $value_dim_index)(i32.const 1))) 
                end
                (set_local $total_length (i32.mul (get_local $length_dim)(get_local $total_length)))
                
                loop
                    block
                        (i32.ge_s (get_local $j)(get_local $length_dim))
                        br_if 0
                            (set_local $ind (call $get_array_index_f64 (get_local $current_dim)
                                (i32.add (i32.const 1)(get_local $j))))
                    
                            (f64.le (get_local $ind)(f64.const 0))
                            if
                                ;; throw error
                                i32.const 4
                                call $throwError
                            end

                            get_local $length_indices
                            i32.const 1
                            i32.gt_u
                                                                                            
                            get_local $ind
                            get_local $shape_ptr
                            get_local $i
                            i32.const 1
                            i32.add
                            call $get_array_index_f64
                            f64.gt
                            i32.and
                            if
                                i32.const 3     
                                call $throwError
                            end
                            get_local $len_arr
                            i32.const 1
                            i32.eq
                            if
                                get_local $ind
                                i32.trunc_s/f64
                                get_local $len_arr
                                i32.gt_s
                                if
                                    i32.const 3
                                    call $throwError
                                end
                            end
                            (set_local $j (i32.add (get_local $j)(i32.const 1)))
                        br 1    
                    end
                end
                (set_local $j (i32.const 0 ))
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1    
        end
    end
    ;; Throw error if the total length does not much array length
    get_local $total_length
    (tee_local $value_length (i32.load offset=4 align=4 (get_local $values_ptr)))
    i32.ne 
    if
        get_local $total_length
        i32.const 1
        i32.gt_s
        get_local $value_length
        i32.const 1
        i32.eq
        i32.and
        if
            (set_local $temp_val_arr
                (call $create_mxvector (get_local $total_length)(i32.const 0)(i32.const 0)(i32.const 0))
            )
            (call $fill (get_local $temp_val_arr)(f64.load offset=0 align=8 (i32.load offset=8 align=4 (get_local $values_ptr))))
            return
        end
        ;; TODO(fix to make result row vector!!!!)
        get_local $total_length
        i32.const 1
        i32.eq
        get_local $i
        i32.eqz
        i32.and
        get_local $value_length
        i32.eqz
        i32.and
        i32.eqz
        if
            i32.const 9
            call $throwError
        else
            i32.const 1
            return
        end
    end
    i32.const 0
)

(export "reshape" (func $reshape))
(func $reshape (param $arr_ptr i32)(param $dim_array i32) (result i32)
    (local $dim_number i32)(local $array_length f64)(local $input_dim_array_byte_size i32)
    (local $loop_dim_number i32)(local $temp f64)(local $i i32)(local $dim_array_ptr i32)
    (local $input_dim_array_ptr i32)
    get_local $arr_ptr
    call $is_null
    get_local $dim_array
    call $is_null
    i32.or
    if
        i32.const 6
        call $throwError
    end
    get_local $dim_array
    call $isrow
    i32.eqz
    if
        i32.const 5
        call $throwError
    end
    ;; Get length dimension 
    (set_local $input_dim_array_byte_size (call $get_array_byte_size (get_local $dim_array)))
    (set_local $input_dim_array_ptr (call $mxarray_core_get_array_ptr (get_local $dim_array)))
    (tee_local $loop_dim_number (call $get_mxarray_dimension_number (get_local $dim_array)))
    i32.const 1
    i32.eq
    if
        (set_local $dim_number (i32.add (i32.const 1)(get_local $loop_dim_number)))
    else 
        (set_local $dim_number (get_local $loop_dim_number))
    end   
    
    ;; Allocate dimensions
    (call $malloc (i32.add (i32.mul (get_local $dim_number)(i32.const 8))(i32.const 8)))
    i32.const 8
    i32.add
    set_local $dim_array_ptr

    ;; Set dimensions and calculate array length
    (set_local $array_length (f64.const 1))
            
    loop
        block ;; array iteration
        (i32.eq (get_local $i)(get_local $loop_dim_number))
        br_if 0
        (tee_local $temp (f64.load (i32.add (get_local $input_dim_array_ptr) ;;(poly) This line needs to change to accomodate for other simple classes as input
                (i32.mul (get_local $input_dim_array_byte_size)(get_local $i)))))
        f64.const 0
        f64.le
        if ;; If dimension is less than or equal 0
            (set_local $temp (f64.const 0))
        end
        
        ;; Get Array length
        (set_local $array_length 
            (f64.mul (get_local $array_length) (get_local $temp)))
        ;; Set dimension in dimension array
        (f64.store (i32.add (get_local $dim_array_ptr) (i32.mul 
                    (get_local $input_dim_array_byte_size)(get_local $i)))(get_local $temp))
        (set_local $i (i32.add (get_local $i)(i32.const 1))) ;; Increase loop counter
        br 1
        end
    end
    get_local $array_length
    i32.trunc_u/f64
    get_local $arr_ptr
    i32.load offset=4 align=4
    i32.eq
    if
        get_local $arr_ptr
        get_local $dim_array_ptr
        i32.store offset=16 align=4
        get_local $arr_ptr
        get_local $dim_number
        i32.store offset=12 align=4
    else
        ;; Length of reshape not the same
        i32.const 8
        call $throwError
    end
    get_local $arr_ptr

)



;; Concatanation
(export "verify_input_and_instantiate_result_concatation" (func $verify_input_and_instantiate_result_concatation))
(func $verify_input_and_instantiate_result_concatation (param $dim i32)(param $input_arrays i32)(result i32)
    (local $length_input i32) (local $first_array_ptr i32)(local $shape_len i32)(local $shape_first i32)
    (local $new_length_column f64)(local $i i32)(local $j i32)(local $input_vector_data i32)
    (local $shape_first_data i32)
    
    (local $temp_mat_ptr i32)
    (local $temp_mat_shape_ptr i32)
    ;; Requires $dim to be 1 or greater
    get_local $dim
    i32.const 1
    i32.lt_s
    if
        i32.const 12
        call $throwError
    end
    get_local $input_arrays
    call $is_null
    get_local $input_arrays
    i32.load offset=4 align=4
    tee_local $length_input
    i32.eqz
    i32.or
    if
        (call $create_mxarray_empty (i32.const 2)
            (i32.const 0)(i32.const 0))
        return
    end
    (set_local $input_vector_data (i32.load offset=8 align=4 (get_local $input_arrays)))
    (set_local $first_array_ptr 
        (i32.load offset=0 align=4  (get_local $input_vector_data)))
    
    (tee_local $shape_len (
        i32.load offset=12 align=4 (get_local $first_array_ptr)))
    get_local $dim
    i32.lt_s
    if
        i32.const 13
        call $throwError
    end

    (set_local $shape_first 
        (call $size (get_local $first_array_ptr)))
    
    (set_local $shape_first_data (i32.load offset=8 align=4 (get_local $shape_first)))
    ;; Access length along concataation dimension from the first array 
    (set_local $new_length_column (f64.load offset=0 align=8 
        (i32.add (get_local $shape_first_data)
            (i32.mul (i32.sub (get_local $dim)(i32.const 1))(i32.const 8)))))
    
    (set_local $i (i32.const 2))

    ;; Loop and checks the shapes of each of the inputs
    ;; The must have the same length in each dimension for all except
    ;; the concatanating dimension
    loop
        block
            (br_if 0 (i32.gt_s (get_local $i)(get_local $length_input)))

            (set_local $temp_mat_ptr 
                (call $get_array_index_i32 
                    (get_local $input_arrays)(get_local $i)))
            (tee_local $temp_mat_shape_ptr 
                (call $size (get_local $temp_mat_ptr)))
            i32.load offset=4 align=4
            get_local $shape_len
            i32.ne
            ;; Throw error when they do not have the same number of dimensions
            if
                (call $free_macharray (get_local $temp_mat_shape_ptr))
                (call $free_macharray (get_local $shape_first))
                i32.const 11
                call $throwError
            end
            (set_local $j (i32.const 1))
            ;; Second loop, checks all the dimensions
            loop
                block
                    (br_if 0 (i32.gt_s (get_local $j)(get_local $shape_len)))
                    ;; First condition $j != dim
                    (i32.and 
                        (i32.ne (get_local $dim)(get_local $j))
                        (f64.ne 
                            (call $get_array_index_f64 
                                (get_local $shape_first)(get_local $j))
                            (call $get_array_index_f64 
                                (get_local $temp_mat_shape_ptr)(get_local $j))))
                    if
                        i32.const 11
                        call $throwError
                    end
                (set_local $j (i32.add (get_local $j)(i32.const 1)))
                br 1
                end
            end
            (set_local $new_length_column 
                (f64.add (get_local $new_length_column)
                        (call $get_array_index_f64 (get_local $temp_mat_shape_ptr)(get_local $dim))))
            (call $free_macharray (get_local $temp_mat_shape_ptr))
            (set_local $i (i32.add (get_local $i) (i32.const 1)))
            br 1
        end
    end 
    ;; Use the size of first as shape, change length of 
    ;; concatanating dimension.
    
    (call $set_array_index_f64 
        (get_local $shape_first)(get_local $dim)(get_local $new_length_column))
    (call $create_mxarray_ND 
        (get_local $shape_first)
        (i32.const 0)
        (i32.const 0)
        (i32.const 0)
    ) 
    (call $free_macharray (get_local $shape_first))
    return 
)
(export "cat_S" (func $cat_S))
(func $cat_S (param $dim f64)(result i32)
    (return (call $create_mxarray_empty (i32.const 2)(i32.const 0)(i32.const 0)))
)
(func $cat_M (param $dim i32)(result i32)
    (return (call $create_mxarray_empty (i32.const 2)(i32.const 0)(i32.const 0)))
)
(export "cat_M" (func $cat_M))
(export "cat_SM" (func $cat_SM))
(func $cat_SM (param $concat_dim f64)(param $input_matrices i32)(result i32)
    (return (call $concat (i32.trunc_s/f64 (get_local $concat_dim))(get_local $input_matrices)))
)
(export "cat_MM" (func $cat_MM))
(func $cat_MM (param $dim i32)(param $arrs_input i32)(result i32)
    (;TODO: Unit Tests function errors ;)
    get_local $dim
    call $isscalar
    i32.eqz
    if
        (call $throwError (i32.const 15))
        unreachable
    end
    (f64.load offset=0 align=8 (i32.load offset=8 align=4 (get_local $dim)))
    i32.trunc_s/f64
    get_local $arrs_input
    call $concat
)
(export "concat" (func $concat))
(func $concat (param $concat_dim i32)(param $input_matrices i32)(result i32)
    (local $res_arr i32)
    (tee_local $res_arr  (call $verify_input_and_instantiate_result_concatation (get_local $concat_dim)(get_local $input_matrices)))
    i32.load offset=4 align=4 
    i32.eqz
    if 
        get_local $res_arr 
        return
    end
    (call $concat_into_result_matrix (get_local $concat_dim)(get_local $res_arr)(get_local $input_matrices))
    get_local $res_arr 
)
    (func $concat_into_result_matrix (param $concat_dim i32)(param $result_matrix_ptr i32)(param $input_matrices i32)
    (; Concanatanates each input matrix into result matrix , 
        $size_prev represents the offset of the current matrix in terms of the concatanating dimension
        calls traverse_concat, which recursively traverses a given matrix setting the result of the concatanating matrix ;)
    (local $concat_dim_length i32) (local $result_matrix_shape_ptr i32)
    (local $i i32)(local $size_prev i32)(local $curr_mat_ptr i32)(local $length_input i32)
    (local $curr_mat_shape_ptr i32)
    (set_local $concat_dim_length 
        (i32.trunc_s/f64 
            (f64.load 
                (i32.add 
                    (i32.load offset=16 align=4 (get_local $result_matrix_ptr))
                    (i32.mul (i32.sub (get_local $concat_dim)(i32.const 1))(i32.const 8))))))
    (set_local $length_input (i32.load offset=4 align=4 (get_local $input_matrices)))
    (set_local $i (i32.const 1))
    loop
        block
        (br_if 0 (i32.gt_s (get_local $i)(get_local $length_input)))
            (set_local $curr_mat_ptr (call $get_array_index_i32 (get_local $input_matrices)(get_local $i)))
            (set_local $curr_mat_shape_ptr (call $size (get_local $curr_mat_ptr)))
            (call $traverse_concat 
                (get_local $concat_dim)
                (get_local $result_matrix_ptr)
                (get_local $concat_dim_length)
                (get_local $curr_mat_ptr)
                (get_local $curr_mat_shape_ptr)
                (get_local $size_prev)
                (i32.const 1) ;; First dim
                (i32.const 0)(i32.const 1)(i32.const 0)(i32.const 1)
            )
            (set_local $size_prev (i32.add (get_local $size_prev)
                (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $curr_mat_shape_ptr)(get_local $concat_dim)))))
            (call $free_macharray (get_local $curr_mat_shape_ptr))
        (set_local $i (i32.add (get_local $i)(i32.const 1)))
        br 1
        end
    end
)

(func $traverse_concat (param $concat_dim i32)(param $total_ptr i32)(param $total_new_dim_size i32)(param $mat_ptr i32)
    (; Traverses a current matrix completely and sets the right indices on the result matrix;)
    (param $mat_shape_ptr i32)(param $size_prev i32)(param $curr_dim i32)(param $offset i32)
    (param $mult i32)(param $offset_tot i32)(param $mult_tot i32)
    (local $i i32)(local $shape_dim_len i32)(local $shape_len i32)(local $new_offset i32)(local $new_mult i32)
    (local $new_offset_tot i32)(local $new_mult_tot i32)(local $i_index i32)

    (set_local $shape_dim_len (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $mat_shape_ptr)(get_local $curr_dim))))
    (set_local $shape_len (i32.load offset=4 align=4 (get_local $mat_shape_ptr)))
    (set_local $i (i32.const 1))
    loop
        block
            (br_if 0 (i32.gt_s (get_local $i)(get_local $shape_dim_len)))
            (set_local $i_index (i32.sub (get_local $i)(i32.const 1)))
            (set_local $new_offset 
                (i32.add (get_local $offset)
                    (i32.mul (get_local $mult)(get_local $i_index))))
            (set_local $new_mult 
                (i32.mul (get_local $mult)(get_local $shape_dim_len)))
            get_local $curr_dim
            get_local $concat_dim
            i32.eq
            if
                (set_local $new_offset_tot 
                    (i32.add (get_local $offset_tot)
                    (i32.mul (get_local $mult_tot)(i32.add (get_local $size_prev)(get_local $i_index)))))
                (set_local $new_mult_tot 
                    (i32.mul (get_local $mult_tot)(get_local $total_new_dim_size)))
            else
                (set_local $new_offset_tot 
                    (i32.add 
                        (get_local $offset_tot)
                        (i32.mul (get_local $mult_tot)(get_local $i_index))))
                (set_local $new_mult_tot 
                    (i32.mul (get_local $mult_tot)(get_local $shape_dim_len)))
            end
            get_local $curr_dim
            get_local $shape_len
            i32.eq
            if
                (call $set_array_index_f64 (get_local $total_ptr)(i32.add (get_local $new_offset_tot)(i32.const 1))
                    (call $get_array_index_f64 (get_local $mat_ptr)(i32.add (get_local $new_offset)(i32.const 1))))
            else
                (call $traverse_concat 
                    (get_local $concat_dim)
                    (get_local $total_ptr)
                    (get_local $total_new_dim_size)
                    (get_local $mat_ptr)
                    (get_local $mat_shape_ptr)
                    (get_local $size_prev)
                    (i32.add (get_local $curr_dim)(i32.const 1))
                    (get_local $new_offset)(get_local $new_mult)(get_local $new_offset_tot)(get_local $new_mult_tot)
                )
            end
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1
        end
    end    
)

(export "vertcat" (func $vertcat))
(func $vertcat (param $input_matrices i32) (result i32)
    (call $concat (i32.const 1)(get_local $input_matrices))
)
(export "horzcat" (func $horzcat))
(func $horzcat (param $input_matrices i32) (result i32)
    (call $concat (i32.const 2)(get_local $input_matrices))
)
(export "horzcat_corder" (func $horzcat_corder))
(func $horzcat_corder (param $input_matrices i32) (result i32)
    ;; Take advantage of a column order layout
    (local $res_arr i32)(local $offset_res_mat i32)(local $i i32)(local $arr_num i32)(local $len i32)(local $j i32)
    (local $curr_mat i32)(local $curr_len i32)
    (tee_local $res_arr  (call $verify_input_and_instantiate_result_concatation (i32.const 2)(get_local $input_matrices)))
    i32.load offset=4 align=4
    tee_local $len
    i32.eqz
    if 
        get_local $res_arr 
        return
    end
    (set_local $i (i32.load offset=8 align=4 (get_local $input_matrices)))
    (set_local $arr_num 
        (i32.add (get_local $i)
        (i32.shl (i32.load offset=4 align=4 (get_local $input_matrices))(i32.const 2))))
    (set_local $offset_res_mat (i32.load offset=8 align=4 (get_local $res_arr)))
    loop
        (set_local $curr_mat (i32.load offset=0 align=4 (get_local $i)))
        (set_local $j (i32.load offset=8 align=4 (get_local $curr_mat)))
        (set_local $curr_len 
            (i32.add (get_local $j)
            (i32.shl (i32.load offset=4 align=4 (get_local $curr_mat))(i32.const 3))))
        loop
            (f64.store offset=0 align=8 
                (get_local $offset_res_mat)
                (f64.load offset=0 align=8 (get_local $j)))
            (set_local $offset_res_mat (i32.add (get_local $offset_res_mat)(i32.const 8)))
            
            (set_local $j (i32.add (get_local $j)(i32.const 8))) 
            (br_if 0 (i32.lt_s (get_local $j)(get_local $curr_len))) 
        end
        (set_local $i (i32.add (get_local $i)(i32.const 4)))
        (br_if 0 (i32.lt_s (get_local $i)(get_local $arr_num)))
    end
    get_local $res_arr
    ;; (call $concat (i32.const 2)(get_local $input_matrices))
)
(export "eye_2D" (func $eye_2D))
(func $eye_2D (param $dim1 f64)(param $dim2 f64)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr (call $zeros_2D (get_local $dim1)(get_local $dim2)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (f64.store offset=0 align=4 (get_local $arr_data_ptr)(f64.const 1))
    (f64.store offset=0 align=4 
        (i32.add (get_local $arr_data_ptr)
        (i32.add (i32.const 1)(i32.trunc_s/f64 (get_local $dim2))))
        (f64.const 1))
    get_local $arr_ptr
)
(export "eye" (func $eye))
(func $eye (param $dim_ptr i32)(result i32)
    (local $i i32)(local $n i32)(local $m i32)
    (local $out_ptr i32)(local $dim_len i32)(local $out_data_ptr i32)
    (local $dim_data_ptr i32)
    get_local $dim_ptr
    call $is_null
    if
        i32.const 5
        call $throwError
    end
    get_local $dim_ptr
    call $isrow
    i32.eqz
    if  
        i32.const 5
        call $throwError
    end
    ;; Obtain dimension length and check that is not greater than 2
    (tee_local $dim_len (i32.load offset=4 align=4 (get_local $dim_ptr)))
    i32.const 2
    i32.gt_s
    if
        i32.const 19
        call $throwError
    end
    get_local $dim_len
    i32.const 1
    i32.lt_s
    if
        i32.const 5
        call $throwError
    end
    (set_local $dim_data_ptr (i32.load offset=8 align=4 (get_local $dim_ptr)))
    (tee_local $n (i32.trunc_s/f64 (f64.load offset=0 align=8 (get_local $dim_data_ptr))))
    i32.const 0
    i32.le_s
    if
        (set_local $n (i32.const 0))
        (f64.store offset=0 align=8 (get_local $dim_data_ptr)(f64.const 0))
    end
    (tee_local $m (i32.trunc_s/f64 (f64.load offset=8 align=8 (get_local $dim_data_ptr))))
    i32.const 0
    i32.le_s
    if
        (set_local $m (i32.const 0))
        (f64.store offset=8 align=8 (get_local $dim_data_ptr)(f64.const 0))
    end
    (tee_local $out_ptr (call $zeros (get_local $dim_ptr)))       
    i32.load offset=4 align=4
    i32.const 0
    i32.gt_s
    if
        (set_local $out_data_ptr (i32.load offset=8 align=4 (get_local $out_ptr)))
        ;; (set_local $n (i32.shl (get_local $n)(i32.const 3)))
        loop
            (f64.store offset=0 align=8 
                (i32.add (get_local $out_data_ptr)
                    (i32.shl (i32.add (get_local $i)(i32.mul (get_local $i)(get_local $n)))
                        (i32.const 3)))
                (f64.const 1))
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $n)))
        end
    end
    get_local $out_ptr
)
    (export "transpose_S" (func $transpose_S))
(func $transpose_S (param f64)(result f64)
    get_local 0
)
(export "transpose_M" (func $transpose_M))
(func $transpose_M (param $arr_ptr i32) (result i32)
    (local $i i32)(local $j i32)(local $n i32)(local $m i32)(local $dim_ptr i32)
    (local $out_ptr i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if  
        i32.const 16
        call $throwError
    end
    (set_local $dim_ptr (call $size (get_local $arr_ptr)))
    (call $mxarray_core_get_number_of_dimensions (get_local $arr_ptr))
    i32.const 2
    i32.ne
    if
        i32.const 19
        call $throwError
    end
    (set_local $n (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $dim_ptr)(i32.const 1))))
    (set_local $m (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $dim_ptr)(i32.const 2))))
    (call $set_array_index_f64 (get_local $dim_ptr)(i32.const 1)(f64.convert_s/i32 (get_local $m)))
    (call $set_array_index_f64 (get_local $dim_ptr)(i32.const 2)(f64.convert_s/i32 (get_local $n)))
    (tee_local $out_ptr (call $create_mxarray_ND (get_local $dim_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))  
    (call $free_macharray (get_local $dim_ptr))
    loop
        block
        (br_if 0 (i32.ge_s (get_local $i)(get_local $n)))
            (set_local $j (i32.const 0))
            loop
                block
                (br_if 0 (i32.ge_s (get_local $j)(get_local $m)))
                    (call $set_array_index_f64 (get_local $out_ptr)
                        (i32.add (i32.const 1)(i32.add (get_local $j)(i32.mul (get_local $i)(get_local $n))))
                        (call $get_array_index_f64 (get_local $arr_ptr)
                            (i32.add (i32.const 1)(i32.add (get_local $i)(i32.mul (get_local $j)(get_local $n)))))
                    )
                (set_local $j (i32.add (get_local $j)(i32.const 1)))
                br 1
                end
            end
        (set_local $i (i32.add (get_local $i)(i32.const 1)))
        br 1
        end
    end

)
(export "rand" (func $rand)) 
(func $rand (param $size_ptr i32)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr 
        (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            call $rand_S
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
(export "rand_2D" (func $rand_2D))
(func $rand_2D (param $dim1 f64)(param $dim2 f64)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr (call $create_mxarray_2D (get_local $dim1)(get_local $dim2)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            call $rand_S
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)


(export "randn_2D" (func $randn_2D))
(func $randn_2D (param $dim1 f64)(param $dim2 f64)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr (call $create_mxarray_2D (get_local $dim1)(get_local $dim2)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            call $randn_S
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
(export "end" (func $end))
(func $end (param $arr_ptr i32)(param $num_dim f64)(param $dim f64)(result f64)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        (call $throwError (i32.const 4))
    end
    get_local $dim
    i32.trunc_s/f64
    get_local $arr_ptr
    i32.load offset=12 align=4
    i32.gt_s
    if
        (call $throwError (i32.const 2))
    end
    (f64.load (i32.add (i32.load offset=16 align=4 (get_local $arr_ptr))
        (i32.shl (i32.sub (i32.trunc_s/f64 (get_local $dim))(i32.const 1))(i32.const 3))))
)
(export "zeros_2D" (func $zeros_2D))
(func $zeros_2D (param $dim1 f64)(param $dim2 f64)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr (call $create_mxarray_2D (get_local $dim1)(get_local $dim2)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 3
            i32.shl
            i32.add
            f64.const 0
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
(export "ones_2D" (func $ones_2D))
(func $ones_2D (param $dim1 f64)(param $dim2 f64)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr (call $create_mxarray_2D (get_local $dim1)(get_local $dim2)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            f64.const 1
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
(export "ones" (func $ones)) 
(func $ones (param $size_ptr i32)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr 
        (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            f64.const 1
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
(export "ones_experimental" (func $ones_experimental)) 
(func $ones_experimental (param $size_ptr i32)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr 
        (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            call $ones_s
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
(export "ones_experimental2" (func $ones_experimental2)) 
(func $ones_experimental2 (param $size_ptr i32)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr 
        (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            call $ones_S
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)

(export "zeros" (func $zeros)) 
(func $zeros (param $size_ptr i32)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr 
        (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            f64.const 0
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
;; Array creation
(export "fill" (func $fill))
(func $fill (param $arr_ptr i32)(param $val f64)(result i32)
    (local $len i32)(local $step i32)(local $arr_data_ptr i32)
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (if (i32.lt_s (get_local $step)(get_local $len))
        (then
        (set_local $len (i32.add (get_local $len)(get_local $arr_data_ptr)))
        (set_local $step (get_local $arr_data_ptr))
        loop
            (f64.store offset=0 align=8 (get_local $step)(get_local $val))
            (set_local $step (i32.add (get_local $step)(i32.const 8)))  
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end))
    get_local $arr_ptr
)


;; ;; Array creation
;;  (export "zeros" (func $zeros))
;; (func $zeros (param $size_ptr i32)(result i32)
;;     (local $arr_ptr i32)
;;     get_local $size_ptr
;;     i32.const 0
;;     i32.const 0
;;     i32.const 1
;;     i32.const 0
;;     call $create_mxarray_ND
;;     i32.const 0
;;     call $elementwise_constructor
;; )
;; (export "ones" (func $ones) )
;; (func $ones (param $size_ptr i32)(result i32)
;;     (local $arr_ptr i32)
;;     get_local $size_ptr
;;     i32.const 0
;;     i32.const 0
;;     i32.const 1
;;     i32.const 8
;;     call $create_mxarray_ND
;;     i32.const 1
;;     call $elementwise_constructor
;; )
;; (export "rand" (func $rand) )
;; (func $rand (param $size_ptr i32)(result i32)
;;     (local $arr_ptr i32)
;;     get_local $size_ptr
;;     i32.const 0
;;     i32.const 0
;;     i32.const 1
;;     i32.const 8
;;     call $create_mxarray_ND
;;     i32.const 2
;;     call $elementwise_constructor
;; )
;; (export "randn" (func $randn) )
;; ( func $randn (param $size_ptr i32)(result i32)
;;     (local $arr_ptr i32)
;;     get_local $size_ptr
;;     i32.const 0
;;     i32.const 0
;;     i32.const 0
;;     i32.const 0
;;     call $create_mxarray_ND
;;     i32.const 3
;;     call $elementwise_constructor
;; )
(export "randn" (func $randn))
(func $randn (param $size_ptr i32)(result i32)
        (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
        (set_local $arr_ptr
            (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))
        (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
        (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
        (i32.lt_s (get_local $i)(get_local $len))
        if
            loop
                get_local $arr_data_ptr
                get_local $i
                i32.const 8
                i32.mul
                i32.add
                call $randn_S
                f64.store offset=0 align=8
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
                (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
            end
        end
        get_local $arr_ptr
)

(func $size_dim (param $arr_ptr i32)(param $i i32)(result f64)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $i
    get_local $arr_ptr
    i32.load offset=12 align=4
    i32.gt_s
    if
        f64.const 1
        return
    end
    get_local $i
    i32.const 0
    i32.le_s
    if
        i32.const 15 
        call $throwError
    end
    get_local $arr_ptr
    i32.load offset=16 align=4
    get_local $i
    i32.const 1
    i32.sub
    i32.const 3
    i32.shl
    i32.add
    f64.load offset=0 align=8
)
(export "size_MS" (func $size_MS))
(func $size_MS (param $arr_ptr i32)(param $i f64)(result f64)
    (local $i_i32 i32)
    get_local $arr_ptr
    get_local $i
    i32.trunc_s/f64
    call $size_dim
)
(func $size_MM (param $arr_ptr i32)(param $dim i32)(result f64)
    get_local $dim
    i32.load offset=4 align=4
    i32.const 1
    i32.eq
    i32.eqz
    if
        (call $throwError (i32.const 15))
    end
    get_local $arr_ptr
    i32.eqz
    if
        (call $throwError (i32.const 6))
    end
    get_local $arr_ptr
    i32.load offset=16 align=4
    ;; load scalar dim and convert it to i32
    (i32.trunc_s/f64 (f64.load (i32.load offset=8 align=4 (get_local $dim))))
    i32.const 1
    i32.sub
    i32.const 3
    i32.shl
    i32.add
    f64.load offset=0 align=8
)
(func $size_SM (param $val f64)(param $dim i32)(result f64)
    get_local $dim
    i32.eqz
    if
        (call $throwError (i32.const 6))
    end
    get_local $dim
    i32.load offset=4 align=4
    i32.const 1 
    i32.eq
    i32.eqz
    get_local $dim 
    i32.load offset=16 align=4
    f64.load offset=0 align=8
    f64.const 1
    f64.lt
    i32.or
    if
        (call $throwError (i32.const 15))
    end 
    f64.const 1
)
(export "create_mxarray_with_initial_value" (func $create_mxarray_with_initial_value) )
(func $create_mxarray_with_initial_value (param $size_ptr i32)(param $initial_value f64)(result i32)
    (local $arr_ptr i32)
    get_local $size_ptr
    i32.const 0
    i32.const 0
    i32.const 0
    call $create_mxarray_ND
    get_local $initial_value
    i32.const 35
    call $elementwise_constructor_one_input
)
(export "randi_2D" (func $randn_2D))
(func $randi_2D (param $max f64)(param $dim1 f64)(param $dim2 f64)(result i32)
    (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
    (set_local $arr_ptr (call $create_mxarray_2D (get_local $dim1)(get_local $dim2)))
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $arr_data_ptr
            get_local $i
            i32.const 8
            i32.mul
            i32.add
            get_local $max
            call $randi_S
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))  
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $arr_ptr
)
(export "randi" (func $randi) )
(func $randi (param $max f64)(param $size_ptr i32)(result i32)
        (local $arr_data_ptr i32)(local $arr_ptr i32)(local $len i32)(local $i i32)
        (set_local $arr_ptr
            (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0)(i32.const 0)(i32.const 0)))
        (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
        (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
        (i32.lt_s (get_local $i)(get_local $len))
        if
            loop
                get_local $arr_data_ptr
                get_local $i
                i32.const 8
                i32.mul
                i32.add
                get_local $max
                call $randi_S
                f64.store offset=0 align=8
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
                (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
            end
        end
        get_local $arr_ptr
)

;; Constructors
(type $func_constructor_type (func (result f64)))
(type $func_constructor_type_one_onput (func (param f64)(result f64)))
(func $elementwise_constructor (param $arr_ptr i32)(param $func_ptr i32)(result i32)
    (local $len i32)(local $i i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $i (i32.const 1))
    loop
        block
        (i32.gt_s (get_local $i)(get_local $len))
        br_if 0
            ;; get arr_ptr
            get_local $arr_ptr
            get_local $i
            get_local $func_ptr
            call_indirect (type $func_constructor_type)
            call $set_array_index_f64
        (set_local $i (i32.add (get_local $i)(i32.const 1)))  
        br 1                
        end
    end
    get_local $arr_ptr
)
(func $elementwise_constructor_one_input (param $arr_ptr i32)(param $arg1 f64)(param $funct_ptr i32)(result i32)
    (local $len i32)(local $i i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $i (i32.const 1))
    loop
        block
        (i32.gt_s (get_local $i)(get_local $len))
        br_if 0
            ;; get arr_ptr
            get_local $arr_ptr
            get_local $i
            get_local $arg1
            get_local $funct_ptr
            call_indirect (type $type_unary_op_f64)
            call $set_array_index_f64
        (set_local $i (i32.add (get_local $i)(i32.const 1)))  
        br 1                
        end
    end
    get_local $arr_ptr
)

(;
    $elementwise_mapping_noallocation runs the elementwise_mapping but stores result in res_ptr instead of allocating
    a new array.
    @param $arr_ptr 
;)
( func $elementwise_mapping_noallocation (param $arr_ptr i32)(param $res_ptr i32)(param $funct_ptr i32)(result i32)
    (local $len i32)(local $step i32)(local $new_arr_ptr i32)(local $new_arr_data_ptr i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        i32.const 6
        call $throwError
    end
    
    (call $is_array (get_local $res_ptr))
    (call $mxarrays_have_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
    i32.and
    if
        (set_local $new_arr_data_ptr (i32.load offset=8 align=4 (get_local $res_ptr)))
    else
        i32.const 18 ;; Result array must have the same length
        call $throwError
    end
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    (set_local $arr_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        (set_local $step (get_local $new_arr_data_ptr))
        (set_local $len (i32.add (get_local $new_arr_data_ptr)(get_local $len)))
        loop                
            get_local $step
            get_local $arr_ptr
            f64.load offset=0 align=8
            get_local $funct_ptr
            call_indirect (type $type_unary_op_f64) 
            f64.store offset=0 align=8
            (set_local $arr_ptr (i32.add (get_local $arr_ptr)(i32.const 8))) 
            (set_local $step (i32.add (get_local $step)(i32.const 8)))  
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))            
        end
    end
    get_local $new_arr_ptr
)
( func $elementwise_mapping (param $arr_ptr i32)(param $funct_ptr i32)(param $new_arr_ptr i32)(result i32)
    (local $len i32)(local $step i32)(local $new_arr_data_ptr i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        i32.const 6
        call $throwError
    end
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    get_local $new_arr_ptr
    call $is_null
    if
        (set_local $new_arr_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
    else
        (call $mxarrays_have_same_dimensions (get_local $new_arr_ptr)(get_local $arr_ptr))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
    end
    (set_local $new_arr_data_ptr (i32.load offset=8 align=4 (get_local $new_arr_ptr)))
    (set_local $arr_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    get_local $len
    i32.const 0
    i32.gt_s
    if
        (set_local $step (get_local $new_arr_data_ptr))
        (set_local $len (i32.add (get_local $new_arr_data_ptr)(get_local $len)))
        loop                
            get_local $step
            get_local $arr_ptr
            f64.load offset=0 align=8
            get_local $funct_ptr
            call_indirect (type $type_unary_op_f64) 
            f64.store offset=0 align=8
            (set_local $arr_ptr (i32.add (get_local $arr_ptr)(i32.const 8))) 
            (set_local $step (i32.add (get_local $step)(i32.const 8)))  
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))            
        end
    end
    get_local $new_arr_ptr
)
(func $elementwise_mapping_one_input (param $arr_ptr i32)(param $arg1 f64)(param $res_ptr i32)(param $scalar_first i32)(param $funct_ptr i32)
    (result i32)
    (local $len i32)(local $i i32)(local $new_arr_ptr i32)(local $size_ptr i32)
    (local $res_data_ptr i32)(local $arr_data_ptr i32)(local $temp i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $res_ptr
    call $is_array
    i32.eqz
    if
        
        (set_local $size_ptr (call $size (get_local $arr_ptr)))
        (set_local $res_ptr (call $create_mxarray_ND (get_local $size_ptr)(i32.const 0 )
            (i32.const 0)(i32.const 0)))
    else
        ;; verify dims of res_ptr
        (call $verify_matrix_matching_dimensions
            (i32.load offset=16 align=4 (get_local $arr_ptr))(i32.load offset=12 align=4 (get_local $arr_ptr))
            (i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
        )
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
    end
    
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (i32.lt_s (get_local $i)(get_local $len))
    if
        (set_local $res_data_ptr (i32.load offset=8 align=4 (get_local $res_ptr)))
        (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
        loop
            (set_local $temp (i32.shl (get_local $i)(i32.const 3)))
            (i32.add (get_local $res_data_ptr)(get_local $temp))
            get_local $scalar_first
            if (result f64)
                (call_indirect (type $type_binary_op_f64)
                    (get_local $arg1)
                    (f64.load (i32.add (get_local $arr_data_ptr)(get_local $temp)))
                    (get_local $funct_ptr)
                )
            else
                
                (call_indirect (type $type_binary_op_f64)
                    (f64.load (i32.add (get_local $arr_data_ptr)(get_local $temp)))
                    (get_local $arg1)
                    (get_local $funct_ptr)
                )
            end
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1))) 
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    (call $free_macharray (get_local $size_ptr))
    get_local $res_ptr
)

(type $type_binary_op_f64 (func (param f64 f64)(result f64)))
(type $type_binary_op_i32 (func (param f64 f64)(result i32)))

(type $type_unary_op_f64 (func (param f64)(result f64)))
(func $unary_map (param $out_ptr i32) (param $arr_ptr i32)(param $func_ptr i32)
    (local $len i32)(local $i i32)
    (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $i (i32.const 1))
    loop
        block
        (i32.gt_s (get_local $i) (get_local $len))
        br_if 0
            (call $set_array_index_f64 
            (get_local $out_ptr)(get_local $i)
                (call_indirect (type $type_unary_op_f64)
                    (call $get_array_index_f64 (get_local $arr_ptr)(get_local $i))
                    (get_local $func_ptr)))
        (set_local $i (i32.add (get_local $i)(i32.const 1)))
        br 1
        end
    end
)
(func $binary_op_MM_not_supported (param i32 i32)(result f64)
    i32.const 16
    call $throwError
    f64.const nan

)
(func $binary_op_SM_not_supported (param f64 i32)(result f64)
    i32.const 16
    call $throwError
    f64.const nan
)

(export "mod_SS" (func $mod_SS))  
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
(export "rem_SS" (func $rem_SS))
(func $rem_SS (param f64 f64)(result f64) 
    get_local 0
    i64.trunc_s/f64 
    get_local 1
    i64.trunc_s/f64 
    i64.rem_s
    f64.convert_s/i64
)
(export "times_SS" (func $times_SS))
(func $times_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.mul
)

(export "mpower_SS" (func $mpower_SS))
(func $mpower_SS (param f64 f64)(result f64)
    get_local 0
    get_local 1
    call $power_SS
)
(export "mpower_MM" (func $binary_op_MM_not_supported))
(export "mpower_MS" (func $binary_op_SM_not_supported))
(export "mpower_SM" (func $binary_op_SM_not_supported))
(export "mrdivide_SS" (func $rdivide_SS))
(export "mrdivide_SM" (func $rdivide_SM))
(export "mrdivide_MS" (func $rdivide_MS))
(export "mldivide_SS" (func $ldivide_SS))
(export "mldivide_SM" (func $ldivide_SM))
(export "mldivide_MS" (func $ldivide_MS))
(export "mrdivide_MM" (func $binary_op_SM_not_supported))
(export "mldivide_MM" (func $binary_op_SM_not_supported))

(export "rdivide_SS" (func $rdivide_SS))
(func $rdivide_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.div
)
(export "ldivide_SS" (func $ldivide_SS))
(func $ldivide_SS (param f64 f64)(result f64) 
    get_local 1
    get_local 0
    f64.div
)
(export "le_SS" (func $le_SS))
(func $le_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.le
    f64.convert_s/i32 

)
(export "lt_SS" (func $lt_SS))
(func $lt_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.lt
    f64.convert_s/i32 

)
(export "ge_SS" (func $ge_SS))
(func $ge_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.ge
    f64.convert_s/i32 

)
(export "gt_SS" (func $gt_SS))
(func $gt_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.gt
    f64.convert_s/i32 

)
(export "eq_SS" (func $eq_SS))
(func $eq_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.eq
    f64.convert_s/i32 

)

(export "and_SS" (func $and_SS))
(func $and_SS (param f64 f64)(result f64) 
    get_local 0
    f64.const 0
    f64.ne
    if (result i32)
        get_local 1
        f64.const 0
        f64.ne
        if (result i32)
            i32.const 1                
        else
            i32.const 0
        end
    else
        i32.const 0
    end
    f64.convert_s/i32 
)
(export "or_SS" (func $or_SS))
(func $or_SS (param f64 f64)(result f64) 
    get_local 0
    f64.const 0
    f64.ne
    if (result i32)
        i32.const 1
    else
        get_local 1
        f64.const 0
        f64.ne
        if (result i32)
            i32.const 1                
        else
            i32.const 0
        end
    end
    f64.convert_s/i32
)
(export "ne_SS" (func $ne_SS))
(func $ne_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.ne
    f64.convert_s/i32

)
;; TABLE DEFINITIONS
(elem $tab (i32.const 0) $zeros_s $ones_s $rand_S $randn_S $randi_S)
(elem $tab (i32.const 5) $plus_SS $minus_SS $rem_SS $mod_SS $times_SS $rdivide_SS $power_SS)
(elem $tab (i32.const 37) $ldivide_SS)
(elem $tab (i32.const 12) $le_SS $lt_SS $ge_SS $gt_SS $eq_SS)
(elem $tab (i32.const 17) $and_SS $or_SS $ne_SS)


    
(export "disp_M" (func $disp_M))
(func $disp_M (param $arr_ptr i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        i32.const 16
        call $throwError
    end
    (call $print_array_f64 (call $mxarray_core_get_array_ptr (get_local $arr_ptr))(i32.load offset=4 align=4 (get_local $arr_ptr)))
)
(export "disp_S" (func $disp_S))
(func $disp_S (param $val f64)
    get_local $val
    call $printDoubleNumber
    drop
)
(export "plus_SS" (func $plus_SS))
(func $plus_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.add
)

(export "plus2_SM" (func $plus2_SM))
(func $plus2_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 5))
)
    (export "plus_MS" (func $plus_MS))
(func $plus_MS (param $arr_ptr i32)(param $scalar f64)(param $res_ptr i32)(result i32)
    (call $plus_SM (get_local $scalar)(get_local $arr_ptr)(get_local  $res_ptr))
)


(export "minus_SS" (func $minus_SS))
(func $minus_SS (param f64 f64)(result f64) 
    get_local 0
    get_local 1
    f64.sub
)
(export "minus_MM" (func $minus_MM))
(func $minus_MM 
(param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $minus_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $minus_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $minus_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $minus_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end 
)
(func $minus_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 6)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)

(func $minus_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            f64.sub 

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)


(export "minus_SM" (func $minus_SM))
(func $minus_SM (param $x f64)(param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 6))
)
(export "minus_MS" (func $minus_MS))
(func $minus_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 6))
)
(export "times_MS" (func $times_MS))
(func $times_MS (param $arr_ptr i32)(param $scalar1 f64)(param $res_ptr i32)(result i32)
    (local $result_arr_data i32)(local $step i32)(local $result_arr i32)(local $len i32)
    (local $arr_ptr_data i32)
    ;; (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 1)(i32.const 9)))
    get_local $res_ptr
    call $is_null
    if 
        (tee_local $res_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
        i32.load offset=8 align=4 
        set_local $result_arr_data
    else
        (call $mxarrays_have_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
        get_local $res_ptr
        i32.load offset=8 align=4 
        set_local $result_arr_data
    end
    (set_local $arr_ptr_data (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    
    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $scalar1
            f64.mul

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $res_ptr
    ;; (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 0)(i32.const 9)))
)
(export "plus_SM" (func $plus_SM))
(func $plus_SM (param $scalar1 f64) (param $arr_ptr i32)(param $result_arr i32)(result i32)
    (local $result_arr_data i32)(local $step i32)(local $len i32)
    (local $arr_ptr_data i32)
    ;; (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 1)(i32.const 9)))
    (set_local $arr_ptr_data (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    get_local $result_arr
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr_ptr)))
    else
        (call $mxarrays_have_same_dimensions (get_local $result_arr)(get_local $arr_ptr))
        i32.eqz
        if
            ;; throw error
            (call $throwError (i32.const 20))
        end
    end
    

    get_local $result_arr
    i32.load offset=8 align=4 
    set_local $result_arr_data
    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $scalar1
            get_local $arr_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            f64.add
            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr
)
(export "times_SM" (func $times_SM))
(func $times_SM (param $scalar1 f64) (param $arr_ptr i32)(param $res_ptr i32)(result i32)
    (local $result_arr_data i32)(local $step i32)(local $result_arr i32)(local $len i32)
    (local $arr_ptr_data i32)
    ;; (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 1)(i32.const 9)))
    get_local $res_ptr
    call $is_null
    if 
        (tee_local $res_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
        i32.load offset=8 align=4 
        set_local $result_arr_data
    else
        (call $mxarrays_have_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
        get_local $res_ptr
        i32.load offset=8 align=4 
        set_local $result_arr_data
    end
    
    (set_local $arr_ptr_data (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))

    
    
    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $scalar1
            get_local $arr_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            f64.mul

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $res_ptr
)
(export "times2_SM" (func $times2_SM))
(func $times2_SM (param $scalar1 f64) (param $m1_ptr i32)(param $res_ptr i32)(result i32)
    (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(get_local $res_ptr)(i32.const 1)(i32.const 9)))
)
(func $times_broadcasting_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 9)))
)
(export "rem_SM" (func $rem_SM))
(func $rem_SM (param $x f64)(param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 7))
)
(export "rem_MS" (func $rem_MS))
(func $rem_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 7))
)
(export "rem_MM" (func $rem_MM))
(func $rem_MM 
(param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $rem_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $rem_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $rem_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $rem_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end 
)
(func $rem_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 7)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)

(func $rem_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)(local $arg0 f64)(local $arg1 f64)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            set_local $arg0

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            set_local $arg1

            get_local $arg0
            i64.trunc_s/f64 
            get_local $arg1 
            i64.trunc_s/f64 
            i64.rem_s
            f64.convert_s/i64

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)

(export "mod_SM" (func $mod_SM))
(func $mod_SM (param $x f64)(param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 8))
)
(export "mod_MS" (func $mod_MS))
(func $mod_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 8))
)
(export "mod_MM" (func $mod_MM))
(func $mod_MM 
(param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $mod_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $mod_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $mod_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $mod_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end 
)
(func $mod_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 8)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)

(func $mod_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)(local $arg0 f64)(local $arg1 f64)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            set_local $arg0

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            set_local $arg1

            get_local $arg0
            get_local $arg1
            get_local $arg0 
            get_local $arg1
            f64.div
            f64.floor
            f64.mul
            f64.sub 

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)
;; (export "mod_MM" (func $mod_MM))
;; (func $mod_MM (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
;;     (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 8)))
;; )

(export "rdivide_SM" (func $rdivide_SM))
(func $rdivide_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 10))
)
(export "rdivide_MS" (func $rdivide_MS))
(func $rdivide_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (local $len i32)(local $i i32)(local $arr_data_ptr i32)(local $res_data_ptr i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    get_local $res_ptr
    call $is_null
    if
        (set_local $res_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
    else
        (call $mxarrays_have_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $res_data_ptr (i32.load offset=8 align=4 (get_local $res_ptr))) 
    (i32.lt_s (get_local $i)(get_local $len))
    if
        loop
            get_local $res_data_ptr
            get_local $i
            i32.add

            get_local $arr_data_ptr
            get_local $i
            i32.add 
            f64.load offset=0 align=8
            get_local $x
            call $rdivide_SS
            f64.store offset=0 align=8
            (set_local $i (i32.add (get_local $i)(i32.const 8))) 
        (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    get_local $res_ptr
)
(export "rdivide_MM" (func $rdivide_MM))
(func $rdivide_MM 
(param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $ldivide_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $ldivide_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $rdivide_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $rdivide_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end 
)
(func $rdivide_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 10)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)

(func $rdivide_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8            

            f64.div

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)

(export "ldivide_SM" (func $ldivide_SM))
(func $ldivide_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 10))
)
(export "ldivide_MS" (func $ldivide_MS))
(func $ldivide_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 10))
)
(export "ldivide_MM" (func $ldivide_MM))
(func $ldivide_MM 
(param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $ldivide_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $ldivide_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $ldivide_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $ldivide_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end 
)
(func $ldivide_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 37)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)

(func $ldivide_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            f64.div

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)

(export "power_MS" (func $power_MS))
(func $power_MS (param $arr_ptr i32)(param $scalar1 f64)(param $res_ptr i32)(result i32)
    (local $result_arr_data i32)(local $step i32)(local $result_arr i32)(local $len i32)
    (local $arr_ptr_data i32)
    ;; (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 1)(i32.const 9)))
    get_local $res_ptr
    call $is_null
    if 
        (tee_local $res_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
        i32.load offset=8 align=4 
        set_local $result_arr_data
    else
        (call $mxarrays_have_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
        get_local $res_ptr
        i32.load offset=8 align=4 
        set_local $result_arr_data
    end
    (set_local $arr_ptr_data (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    
    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $scalar1
            call $power_SS

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $res_ptr
    ;; (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 0)(i32.const 9)))
)
(export "power_SM" (func $power_SM))
(func $power_SM (param $scalar1 f64) (param $arr_ptr i32)(param $result_arr i32)(result i32)
    (local $result_arr_data i32)(local $step i32)(local $len i32)
    (local $arr_ptr_data i32)
    ;; (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 1)(i32.const 9)))
    (set_local $arr_ptr_data (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    get_local $result_arr
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr_ptr)))
    else
        (call $mxarrays_have_same_dimensions (get_local $result_arr)(get_local $arr_ptr))
        i32.eqz
        if
            ;; throw error
            (call $throwError (i32.const 20))
        end
    end
    

    get_local $result_arr
    i32.load offset=8 align=4 
    set_local $result_arr_data
    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $scalar1
            get_local $arr_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            
            call $power_SS
            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr
)
(export "power_MM" (func $power_MM))
(func $power_MM (param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $power_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $power_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $power_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $power_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end 

    ;; (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 11)))
)

  (export "mxarray_size_MS_nocheck" (func $mxarray_size_MS_nocheck))
    (func $mxarray_size_MS_nocheck (param $arr_ptr i32)(param $dim f64)(result f64)
        get_local $arr_ptr
        i32.load offset=16 align=4
        get_local $dim
        i32.trunc_s/f64
        i32.const 3
        i32.shl

        i32.add
        f64.load
    )
  (export "mxarray_stride_MS_nocheck" (func $mxarray_stride_MS_nocheck))
    (func $mxarray_stride_MS_nocheck (param $arr_ptr i32)(param $dim f64)(result f64)
        get_local $arr_ptr
        i32.load offset=20 align=4
        
        get_local $dim
        i32.trunc_s/f64
        i32.const 3
        i32.shl

        i32.add
        f64.load
    )
    
(func $power_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            call $power_SS

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)
(func $power_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 11)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)
    (export "le_SM" (func $le_SM))
(func $le_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 12))
)
(export "le_MS" (func $le_MS))
(func $le_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 12))
)
    (export "le_MM" (func $le_MM))
(func $le_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 12)))
)
;; (func $)

    (export "lt_SM" (func $lt_SM))
(func $lt_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 13))
)
(export "lt_MS" (func $lt_MS))
(func $lt_MS (param $arr_ptr i32)(param $x f64) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 13))
)
    (export "lt_MM" (func $lt_MM))
(func $lt_MM (param $m1_ptr i32)(param $m2_ptr i32)(result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 13)))
)

(export "ge_SM" (func $ge_SM))
(func $ge_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 14))
)
(export "ge_MS" (func $ge_MS))
(func $ge_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 14))
)
    (export "ge_MM" (func $ge_MM))
(func $ge_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 14)))
)
(export "gt_SM" (func $gt_SM))
(func $gt_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 15))
)
(export "gt_MS" (func $gt_MS))
(func $gt_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 15))
)
    (export "gt_MM" (func $gt_MM))
(func $gt_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 15)))
)
(export "eq_SM" (func $eq_SM))
(func $eq_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 16))
)
(export "eq_MS" (func $eq_MS))
(func $eq_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 16))
)
    (export "eq_MM" (func $eq_MM))
(func $eq_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 16)))
)
(export "and_SM" (func $and_SM))
(func $and_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 17))
)
(export "and_MS" (func $and_MS))
(func $and_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 17))
)
(export "and_MM" (func $and_MM))
(func $and_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 17)))
)
(export "or_SM" (func $or_SM))
(func $or_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 18))
)
(export "or_MS" (func $or_MS))
(func $or_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 18))
)
(export "or_MM" (func $or_MM))
(func $or_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 18)))
)

(export "ne_SM" (func $ne_SM))
(func $ne_SM (param $x f64)(param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 1)(i32.const 19))
)
(export "ne_MS" (func $ne_MS))
(func $ne_MS (param $arr_ptr i32)(param $x f64)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(get_local $res_ptr)(i32.const 0)(i32.const 19))
)
(export "ne_MM" (func $ne_MM))
(func $ne_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
    (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 19)))
)
(export "times_MM" (func $times_MM))
(func $times_MM (param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $times_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $times_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $times_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $times_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end 
)
(func $times_MM_samesize  (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            f64.mul

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)
(func $times_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 9)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)
(export "plus_MM_broadcasting" (func $plus_MM_broadcasting))
(export "power_MM_broadcasting" (func $power_MM_broadcasting))
(export "times_MM_broadcasting" (func $times_MM_broadcasting))
(func $plus_MM_broadcasting (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(get_local $res_ptr))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (i32.const 5)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)
(export "plus_MM" (func $plus_MM))
(func $plus_MM  (param $arr1_ptr i32)(param $arr2_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr1_ptr
    call $isscalar
    if
        (call $plus_SM (f64.load (i32.load offset=8 align=4 (get_local $arr1_ptr)))(get_local $arr2_ptr)(get_local $res_ptr)) 
        return
    end
    get_local $arr2_ptr
    call $isscalar
    if
        (call $plus_MS (get_local $arr1_ptr)(f64.load (i32.load offset=8 align=4 (get_local $arr2_ptr)))(get_local $res_ptr)) 
        return
    end

    (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
    if (result i32)
        (call $plus_MM_samesize (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    else        
        (call $plus_MM_broadcasting (get_local $arr1_ptr)(get_local $arr2_ptr)(get_local $res_ptr))
    end     
)
;; General plus_MM, samesize_noallocation, samesize, broadcasting, broadcasting_noallocation


(func $plus_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(param $result_arr i32)(result i32)
    (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)
    (local $result_arr_data i32)
    (get_local $result_arr)
    call $is_null
    if
        (set_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
    else 
        (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $result_arr))
        i32.eqz
        if
            i32.const 20
            call $throwError
        end
    end 

    (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
    (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
    (set_local $result_arr_data (i32.load offset=8 align=4  (get_local $result_arr)))

    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop
            get_local $result_arr_data
            get_local $step
            i32.add

            get_local $arr1_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8

            get_local $arr2_ptr_data
            get_local $step
            i32.add
            f64.load offset=0 align=8
            f64.add 

            f64.store offset=0 align=8
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
    get_local $result_arr 
)


;; (func $plus_MM_samesize (param $arr1_ptr i32)(param $arr2_ptr i32)(result i32)
;;     (local $step i32)(local $len i32)(local $arr1_ptr_data i32)(local $arr2_ptr_data i32)(local $result_arr i32)
;;     (local $result_arr_data i32)
;;     (call $mxarrays_have_same_dimensions (get_local $arr1_ptr)(get_local $arr2_ptr))
;;     if 
;;         (set_local $arr1_ptr_data (i32.load offset=8 align=4 (get_local $arr1_ptr)))
;;         (set_local $arr2_ptr_data (i32.load offset=8 align=4 (get_local $arr2_ptr)))
;;         (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr1_ptr))(i32.const 3)))
;;         (tee_local $result_arr (call $copy_mxarray_structure (get_local $arr1_ptr)))
;;         i32.load offset=8 align=4 
;;         set_local $result_arr_data
;;         loop
;;             get_local $result_arr_data
;;             get_local $step
;;             i32.add

;;             get_local $arr1_ptr_data
;;             get_local $step
;;             i32.add
;;             f64.load offset=0 align=8

;;             get_local $arr2_ptr_data
;;             get_local $step
;;             i32.add
;;             f64.load offset=0 align=8
;;             f64.add 

;;             f64.store offset=0 align=8
;;             (set_local $step (i32.add (get_local $step)(i32.const 8)))
;;             (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
;;         else
;;             i32.const 19
;;             call $throwError 
;;         end
;;     end
;;     get_local $result_arr
;; )

(func $mxarrays_have_same_dimensions (param $arr1_ptr i32)(param $arr2_ptr i32)(result i32)
    (local $dim_length i32)(local $arr1_dim_ptr i32)(local $arr2_dim_ptr i32)
    (local $temp i32)
    ;; Load lengths if not equal return false. 
    get_local $arr1_ptr
    i32.load offset=4 align=4
    get_local $arr2_ptr
    i32.load offset=4 align=4
    i32.eq
    i32.eqz
    ;; If number of dimensions does not match 
    get_local $arr1_ptr
    i32.load offset=12 align=4
    tee_local $dim_length 
    get_local $arr2_ptr
    i32.load offset=12 align=4
    i32.eq
    i32.eqz
    i32.or
    if (result i32)
        i32.const 0
    else
        (set_local $dim_length (i32.shl (get_local $dim_length)(i32.const 3)))
        (set_local $arr1_dim_ptr (i32.load offset=16 align=4 (get_local $arr1_ptr)))
        (set_local $arr2_dim_ptr (i32.load offset=16 align=4 (get_local $arr2_ptr)))
        loop
            get_local $arr1_dim_ptr
            get_local $temp
            i32.add
            f64.load offset=0 align=8
            get_local $arr2_dim_ptr
            get_local $temp
            i32.add
            f64.load offset=0 align=8
            f64.ne
            if
                ;; Return 0 if either dimension is not equal
                i32.const 0
                return
            end
            (set_local $temp (i32.add (get_local $temp)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $temp)(get_local $dim_length)))
        end
        ;; Otherwise return 1
        i32.const 1
    end
    
)
(func $pairwise (param $m1_ptr i32)(param $m2_ptr i32)(param $func_ptr i32)(result i32)
    (local $res_ptr i32)
    (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 0))
    (tee_local $res_ptr)
    i32.load offset=4 align=4 
    i32.const 0
    i32.gt_s
    if 
        (call $traverse_pairwise 
            (get_local $res_ptr)(i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=20 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
            (get_local $m1_ptr)(i32.load offset=16 align=4 (get_local $m1_ptr))(i32.load offset=20 align=4 (get_local $m1_ptr))(i32.load offset=12 align=4 (get_local $m1_ptr))
            (get_local $m2_ptr)(i32.load offset=16 align=4 (get_local $m2_ptr))(i32.load offset=20 align=4 (get_local $m2_ptr))(i32.load offset=12 align=4 (get_local $m2_ptr))
            (get_local $func_ptr)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    end
    get_local $res_ptr
)
(export "verify_pairwise" (func $verify_pairwise))
(func $verify_pairwise (param $m1_ptr i32)(param $m2_ptr i32)(param $res_ptr i32)(result i32)
    (local $bshape_ptr i32)(local $ashape_ptr i32)(local $len_a i32)
    (local $len_b i32)(local $numDim i32)(local $new_shape_ptr i32)
    (local $i i32)(local $curr_adim f64)(local $curr_bdim f64)
    get_local $m1_ptr
    call $is_null
    get_local $m2_ptr
    call $is_null
    i32.or
    if
        i32.const 5
        call $throwError
    end
    (set_local $ashape_ptr (call $size (get_local $m1_ptr)))
    (set_local $bshape_ptr (call $size (get_local $m2_ptr)))
    (set_local $len_a (i32.load offset=4 align=4 (get_local $ashape_ptr)))
    (set_local $len_b (i32.load offset=4 align=4 (get_local $bshape_ptr)))

    ;; set total numDim
    (set_local $numDim 
        (select (get_local $len_a)(get_local $len_b)
            (i32.gt_s (get_local $len_a)(get_local $len_b))))
            
    (set_local $new_shape_ptr 
        (call $create_mxvector (get_local $numDim)
            (i32.const 0)(i32.const 0)(i32.const 0)))
    (set_local $i (i32.const 1))
    loop
        block
        (br_if 0 (i32.gt_s (get_local $i)(get_local $numDim)))
            get_local $i
            get_local $len_a
            i32.gt_s
            if
            (set_local $curr_bdim 
                (call $get_array_index_f64  
                            (get_local $bshape_ptr)
                            (get_local $i)))
                (call $set_array_index_f64 
                                (get_local $new_shape_ptr)
                                (get_local $i)(get_local $curr_bdim))
            else
            
                get_local $i
                get_local $len_b
                i32.gt_s
                if 
                    (set_local $curr_adim 
                        (call $get_array_index_f64  
                            (get_local $ashape_ptr)
                            (get_local $i)))
                    (call $set_array_index_f64 
                            (get_local $new_shape_ptr)
                            (get_local $i)(get_local $curr_adim))
                else
                    
                    (tee_local $curr_adim 
                        (call $get_array_index_f64  
                            (get_local $ashape_ptr)
                            (get_local $i)))
                    (tee_local $curr_bdim 
                        (call $get_array_index_f64  
                            (get_local $bshape_ptr)
                            (get_local $i)))

                    f64.eq
                    if
                        (call $set_array_index_f64 
                                (get_local $new_shape_ptr)
                                (get_local $i)(get_local $curr_adim))
                    else
                        get_local $curr_adim
                        f64.const 1
                        f64.eq
                        get_local $curr_bdim
                        f64.const 1
                        f64.ne
                        i32.and
                        if
                            (call $set_array_index_f64 
                                (get_local $new_shape_ptr)
                                (get_local $i)(get_local $curr_bdim))
                        else
                            get_local $curr_bdim
                            f64.const 1
                            f64.eq
                            get_local $curr_adim
                            f64.const 1
                            f64.ne
                            i32.and
                            if
                                (call $set_array_index_f64 
                                    (get_local $new_shape_ptr)
                                    (get_local $i)(get_local $curr_adim))
                            else
                                i32.const 14
                                call $throwError
                            end
                        end
                    end
                end
            end
        (set_local $i (i32.add (get_local $i)(i32.const 1)))
        br 1
        end
    end
    get_local $res_ptr
    call $is_null
    if (result i32)
        (call $create_mxarray_ND (get_local $new_shape_ptr) (i32.const 0)(i32.const 0)(i32.const 0))
    else
        (call $mxarrays_have_same_dimensions (get_local $new_shape_ptr)(get_local $res_ptr))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
        get_local $res_ptr
    end 
)
(func $traverse_pairwise 
    (param $total_ptr i32)(param $total_shape_ptr i32)(param $total_stride_ptr i32)(param $total_dim_num i32)
    (param $a_ptr i32)(param $a_shape_ptr i32)(param $a_stride_ptr i32)(param $a_dim_num i32)
    (param $b_ptr i32)(param $b_shape_ptr i32)(param $b_stride_ptr i32)(param $b_dim_num i32)
    (param $func_ptr i32)(param $curr_dim i32)
    (param $offset_tot i32)(param $offset_a i32) (param $offset_b i32)
    (local $len_dim i32)(local $i i32)
    (local $new_offset_tot i32)(local $new_offset_a i32)(local $new_offset_b i32)
    (local $total_data_ptr i32)(local $a_data_ptr i32)(local $b_data_ptr i32)
    (local $curr_stride_tot i32)(local $curr_stride_a i32)(local $curr_stride_b i32)
    (local $curr_shape_a i32)(local $curr_shape_b i32)
    (set_local $len_dim 
        (i32.trunc_s/f64 (f64.load offset=0 align=8 (i32.add (get_local $total_shape_ptr)
            (i32.mul (i32.const 8)(get_local $curr_dim))))))
    (set_local $total_data_ptr (i32.load offset=8 align=4 (get_local $total_ptr)))
    (set_local $a_data_ptr (i32.load offset=8 align=4 (get_local $a_ptr)))
    (set_local $b_data_ptr (i32.load offset=8 align=4 (get_local $b_ptr)))
    ;; (set_local $total_dim_num (i32.load offset=4 align=4 (get_local $total_shape_ptr)))
    (i32.lt_s (get_local $i)(get_local $len_dim))
    if   
        (set_local $curr_stride_tot (i32.trunc_s/f64 (f64.load offset=0 align=8 
                    (i32.add (get_local $total_stride_ptr)(i32.mul (i32.const 8)(get_local $curr_dim))))))
        ;; Check if a enough dimensions, save a stride before loop
        get_local $a_dim_num
        get_local $curr_dim
        i32.gt_s
        if
            (set_local $curr_stride_a (i32.trunc_s/f64 
                (f64.load offset=0 align=8 (i32.add (get_local $a_stride_ptr)
                    (i32.mul (i32.const 8)(get_local $curr_dim))))))
            (set_local $curr_shape_a (i32.trunc_s/f64 
                (f64.load offset=0 align=8 (i32.add (get_local $a_shape_ptr)
                    (i32.mul (i32.const 8)(get_local $curr_dim))))))
        end
        ;; Check if b enough dimensions, save b stride before loop
        get_local $b_dim_num
        get_local $curr_dim
        i32.gt_s
        if
            (set_local $curr_stride_b (i32.trunc_s/f64 
                (f64.load offset=0 align=8 (i32.add (get_local $b_stride_ptr)
                    (i32.mul (i32.const 8)(get_local $curr_dim))))))
            (set_local $curr_shape_b (i32.trunc_s/f64 
                (f64.load offset=0 align=8 (i32.add (get_local $b_shape_ptr)
                    (i32.mul (i32.const 8)(get_local $curr_dim))))))
        end                       
        loop
            ;; Get if a has enough dimensions and that the dimension is not larger than the current_dim.
            (set_local $new_offset_tot (i32.add (get_local $offset_tot)
                (i32.mul (get_local $i)(get_local $curr_stride_tot))))
            
            (i32.ge_s (get_local $curr_dim)(get_local $a_dim_num))
            if                    
                (set_local $new_offset_a (get_local $offset_a))
            else
                (i32.ge_s (get_local $i)(get_local $curr_shape_a))
                if
                    (set_local $new_offset_a (get_local $offset_a))
                else
                    (set_local $new_offset_a (i32.add (get_local $offset_a)
                        (i32.mul (get_local $i)(get_local $curr_stride_a))))
                end 
            end
            ;; idx > bshape.length-1 || i > bshape[idx]-1
            get_local $curr_dim
            get_local $b_dim_num
            i32.ge_s
            if      
                (set_local $new_offset_b (get_local $offset_b))              
            else 
                (i32.ge_s (get_local $i)(get_local $curr_shape_b))
                if
                    (set_local $new_offset_b (get_local $offset_b))
                else
                    (set_local $new_offset_b (i32.add (get_local $offset_b)
                        (i32.mul (get_local $i)(get_local $curr_stride_b)))) 
                end 
            end
            get_local $curr_dim
            get_local $total_dim_num
            i32.const 1
            i32.sub
            i32.eq
            if
                (f64.store offset=0 align=8
                    (i32.add (get_local $total_data_ptr)(i32.mul (i32.const 8)(get_local $new_offset_tot)))
                    (call_indirect (type $type_binary_op_f64) 
                        (f64.load offset=0 align=8 
                            (i32.add (get_local $a_data_ptr)
                                (i32.mul (i32.const 8)(get_local $new_offset_a))))
                        (f64.load offset=0 align=8 
                            (i32.add (get_local $b_data_ptr)
                                (i32.mul (i32.const 8)(get_local $new_offset_b))))
                        (get_local $func_ptr)))
            else
                (call $traverse_pairwise 
                    (get_local $total_ptr)(get_local $total_shape_ptr)(get_local $total_stride_ptr)(get_local $total_dim_num)
                    (get_local $a_ptr)(get_local $a_shape_ptr)(get_local $a_stride_ptr)(get_local $a_dim_num)
                    (get_local $b_ptr)(get_local $b_shape_ptr)(get_local $b_stride_ptr)(get_local $b_dim_num)
                    (get_local $func_ptr)(i32.add (get_local $curr_dim)(i32.const 1))
                    (get_local $new_offset_tot)(get_local $new_offset_a)(get_local $new_offset_b))
            end         
        (set_local $i (i32.add (i32.const 1)(get_local $i)))
        (br_if 0 (i32.lt_s (get_local $i)(get_local $len_dim)))
        end
    end 


    ;; loop
    ;;     block
    ;;     (br_if 0 (i32.ge_s (get_local $i)(get_local $len_dim)))
    ;;     ;; Total calculation
    ;;     (set_local $new_offset_tot 
    ;;         (i32.add (get_local $offset_tot)
    ;;                  (i32.mul (get_local $i)(get_local $mult_tot))))
            
    ;;     (set_local $new_mult_tot 
    ;;         (i32.mul (get_local $mult_tot)
    ;;             (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $total_shape_ptr)
    ;;                 (get_local $curr_dim)))))

    ;;     ;; A Calculation
    ;;     (set_local $ashape_dim (i32.trunc_s/f64 
    ;;         (call $get_array_index_f64 (get_local $a_shape_ptr)
    ;;             (get_local $curr_dim))))
    ;;     get_local $curr_dim
    ;;     get_local $a_shape_ptr
    ;;     i32.load offset=4 align=4
    ;;     i32.gt_s
    ;;     get_local $i
    ;;     get_local $ashape_dim
    ;;     i32.const 1
    ;;     i32.sub
    ;;     i32.gt_s
    ;;     i32.or
    ;;     if
    ;;         (set_local $new_offset_a (get_local $offset_a))
    ;;         (set_local $new_mult_a (get_local $mult_a))
    ;;     else
    ;;         (set_local $new_offset_a
    ;;                         (i32.add (get_local $offset_a)
    ;;                                 (i32.mul (get_local $i)(get_local $mult_a))))
    ;;         (set_local $new_mult_a
    ;;             (i32.mul (get_local $mult_a)
    ;;                (get_local $ashape_dim)))
    ;;     end
        
    ;;     ;; B calculation
    ;;     (set_local $bshape_dim (i32.trunc_s/f64 
    ;;         (call $get_array_index_f64 (get_local $b_shape_ptr)
    ;;             (get_local $curr_dim))))
    ;;     get_local $curr_dim
    ;;     get_local $b_shape_ptr
    ;;     i32.load offset=4 align=4
    ;;     i32.gt_s
    ;;     get_local $i
    ;;     get_local $bshape_dim
    ;;     i32.const 1
    ;;     i32.sub
    ;;     i32.gt_s
    ;;     i32.or
    ;;     if
    ;;         (set_local $new_offset_b (get_local $offset_b))
    ;;         (set_local $new_mult_b (get_local $mult_b))
    ;;     else
    ;;         (set_local $new_offset_b
    ;;                         (i32.add (get_local $offset_b)
    ;;                                 (i32.mul (get_local $i)(get_local $mult_b))))
    ;;         (set_local $new_mult_b
    ;;             (i32.mul (get_local $mult_b)
    ;;                (get_local $bshape_dim)))
    ;;     end

    ;;     get_local $curr_dim
    ;;     get_local $total_dim_num
    ;;     i32.eq
    ;;     if
    ;;         ;; get_local $res_type
    ;;         ;; i32.eqz
    ;;         ;; if
    ;;         ( call $set_array_index_f64
    ;;             (get_local $total_ptr)
    ;;             (i32.add (i32.const 1)(get_local $new_offset_tot))
    ;;             (call_indirect (type $type_binary_op_f64) 
    ;;                 (call $get_array_index_f64 (get_local $a_ptr)
    ;;                     (i32.add (i32.const 1)(get_local $new_offset_a)))
    ;;                 (call $get_array_index_f64 (get_local $b_ptr)
    ;;                     (i32.add (i32.const 1)(get_local $new_offset_b)))
    ;;                 (get_local $func_ptr)))
    ;;         ;; else
    ;;         ;;     ( call $set_array_index_f64
    ;;         ;;         (get_local $total_ptr)
    ;;         ;;         (i32.add (i32.const 1)(get_local $new_offset_tot))
    ;;         ;;         (f64.convert_s/i32 (call_indirect (type $type_binary_op_i32) 
    ;;         ;;             (call $get_array_index_f64 (get_local $a_ptr)
    ;;         ;;                 (i32.add (i32.const 1)(get_local $new_offset_a)))
    ;;         ;;             (call $get_array_index_f64 (get_local $b_ptr)
    ;;         ;;                 (i32.add (i32.const 1)(get_local $new_offset_b)))
    ;;         ;;             (get_local $func_ptr))))
    ;;         ;; end
    ;;     else
    ;;         (call $traverse_pairwise 
    ;;             (get_local $total_ptr)(get_local $total_shape_ptr)
    ;;             (get_local $a_ptr)(get_local $a_shape_ptr)
    ;;             (get_local $b_ptr)(get_local $b_shape_ptr)
    ;;             (get_local $func_ptr)
    ;;             (i32.add (get_local $curr_dim)(i32.const 1))
    ;;             (get_local $new_offset_tot)(get_local $new_mult_tot)
    ;;             (get_local $new_offset_a)(get_local $new_mult_a)
    ;;             (get_local $new_offset_b)(get_local $new_mult_b))
    ;;     end
    ;;     (set_local $i (i32.add (i32.const 1)(get_local $i)))
    ;;     br 1
    ;;     end
    ;; end
)


(func $is_array (param $arr_ptr i32)(result i32)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 0
        return
    end
    get_local $arr_ptr
    i32.load8_u offset=0 align=1
    i32.const 4
    i32.gt_u
    if
        i32.const 0
        return
    end
    i32.const 1
)
(func $findNonSingletonDimension (param $dim_ptr i32)(param $dim_size i32)(result i32)
    (local $i i32)(local $curr_dim f64)
    (set_local $dim_size (i32.shl (get_local $dim_size)(i32.const 3)))
    (i32.lt_s (get_local $i)(get_local $dim_size))
    if
        loop
            get_local $dim_ptr
            get_local $i
            i32.add
            f64.load offset=0 align=4
            f64.const 1
            f64.gt
            if
                get_local $i
                i32.const 8
                i32.div_s
                i32.const 1 ;; Backwards compatibility with bad decision to support
                i32.add ;; 1-based indexing scheme
                return
            end
            (set_local $i (i32.add (get_local $i)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $dim_size)))
        end
    end
    ;; loop
    ;;     block
    ;;         (br_if 0 (i32.gt_s (get_local $i)(get_local $len)))
    ;;         (tee_local $curr_dim (call $get_array_index_f64 (get_local $size_ptr)(get_local $i)))
    ;;         f64.const 1
    ;;         f64.gt
    ;;         if      
    ;;             get_local $i
    ;;             return
    ;;         end
    ;;     (set_local $i (i32.add (i32.const 1)(get_local $i)))
    ;;     br 1
    ;;     end
    ;; end
    i32.const -1
)
(func $reduction (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(param $res_ptr i32)(param $init_to i32)(param $binary_op_flag i32)(param $func_ptr i32)
    (result i32)
    (local $size_new_arr i32)(local $new_arr_ptr i32)(local $size_arr i32)
    ;; Check for null
    ;; Check for array input
    get_local $arr_ptr
    call $is_null
    if
        i32.const 5
        call $throwError
    end
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        i32.const 16
        call $throwError
    end
    get_local $dim
    i32.const 0
    i32.lt_s
    if
        i32.const 15 
        call $throwError
    end
    
    (set_local $size_new_arr (call $size (get_local $arr_ptr)))
    get_local $dim
    i32.eqz
    if
        (set_local $dim (call $findNonSingletonDimension 
            (i32.load offset=16 align=4 (get_local $arr_ptr)) ;;Get dim ptr
            (i32.load offset=12 align=4 (get_local $arr_ptr)))) ;; Get dim number
    end
    ;; If the dimension is greater than the length of the array or the dim is -1
    ;; (Non-singleton dimension was not found.) Then clone the array, or shape[dim]  == 1 
    (i32.or 
        (i32.gt_s (get_local $dim)(i32.load offset=12 align=4 (get_local $arr_ptr)))
        (i32.eq (i32.const -1)(get_local $dim)))
    if (result i32)
        get_local $res_ptr
        call $is_null
        i32.eqz
        if
        (call $mxarrays_have_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
        if
            (call $mxarray_copy_contents_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
            get_local $res_ptr
            return 
        end
        end
        ;; TODO(dherre3): Remove hardcoded function pointer for any and all
        get_local $func_ptr
        i32.const 17
        i32.eq
        get_local $func_ptr
        i32.const 18
        i32.eq
        i32.or
        if (result i32)
        f64.const 0
        get_local $arr_ptr
        i32.const 0
        call $ne_SM
        else 
            get_local $arr_ptr
            call $clone
        end
    
    else
        (set_local $size_arr 
            (call $clone (get_local $size_new_arr)))
        (call $set_array_index_f64 
            (get_local $size_new_arr)
            (get_local $dim)(f64.const 1))
        get_local $init_to
        i32.eqz
        if (result i32)

            get_local $res_ptr
            call $is_null
            if (result i32)
                ;; Initialize to zeros or ones
                (tee_local $new_arr_ptr 
                    (call $zeros (get_local $size_new_arr)))
            else
                (call $verify_matrix_matching_dimensions 
                    (i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
                    (i32.load offset=8 align=4 (get_local $size_new_arr))(i32.const 2))
                if (result i32)
                    (tee_local $new_arr_ptr 
                        (call $fill (get_local $res_ptr)(f64.const 0)))
                else
                    (call $throwError (i32.const 20))
                    i32.const 0
                end
            end 
        else
            get_local $res_ptr
            call $is_null
            if (result i32)
                ;; Initialize to zeros or ones
                (tee_local $new_arr_ptr 
                    (call $ones (get_local $size_new_arr)))
            else
                (call $verify_matrix_matching_dimensions 
                    (i32.load offset=16 align=4 (get_local $res_ptr))(i32.load offset=12 align=4 (get_local $res_ptr))
                    (i32.load offset=8 align=4 (get_local $size_new_arr))(i32.const 2))
                
                if (result i32)
                    (tee_local $new_arr_ptr 
                        (call $fill (get_local $res_ptr)(f64.const 1)))
                else
                    (call $throwError (i32.const 20))
                    i32.const 0
                end
            end 
        end
        
        
        (call $traverse_reduction 
            (get_local $new_arr_ptr)(get_local $arr_ptr)
            (get_local $size_arr)(get_local $dim)
            (get_local $nanFlag)
            (get_local $binary_op_flag)
            (get_local $func_ptr)
            (i32.const 1)(i32.const 0)(i32.const 1)(i32.const 0)(i32.const 1))
        (call $free_macharray (get_local $size_arr))   
    end
    (call $free_macharray (get_local $size_new_arr))
)
(func $verify_matrix_matching_dimensions (param $dim_ptr i32)(param $ndim i32)(param $dim2_ptr i32)(param $ndim2 i32)(result i32)
    (local $i i32)
    get_local $ndim
    get_local $ndim2
    i32.eq
    i32.eqz
    if
        (call $throwError (i32.const 14))
    end
    (i32.lt_s (get_local $i )(get_local $ndim))
    if
        (set_local $ndim (i32.shl (get_local $ndim)(i32.const 3)))
        loop
            get_local $dim_ptr
            get_local $i
            i32.add
            f64.load offset=0 align=8
            get_local $dim2_ptr
            get_local $i
            i32.add
            f64.load offset=0 align=8
            f64.ne
            if
                i32.const 0
                return
            end 
            (set_local $i (i32.add (get_local $i)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $i )(get_local $ndim)))
        end
    end  
    i32.const 1      
)
(func $mxarray_copy_contents_same_dimensions (param $source_arr_ptr i32)(param $dest_arr_ptr i32)
    (local $step i32)(local $len i32)(local $source_data_ptr i32)(local $dest_data_ptr i32)
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $source_arr_ptr))(i32.const 3)))
    (set_local $source_data_ptr (i32.load offset=8 align=4 (get_local $source_arr_ptr)))
    (set_local $dest_data_ptr (i32.load offset=8 align=4 (get_local $source_arr_ptr)))
    (i32.lt_s (get_local $step)(get_local $len))
    if
        loop
            get_local $dest_data_ptr
            get_local $step
            i32.add

            get_local $source_data_ptr
            get_local $step
            i32.add 
            f64.load offset=0 align=8                 

            f64.store offset=0 align=8

            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end
    end
)
(export "sum_S" (func $identity_f64))
(func $identity_f64 (param $arg f64) (result f64)
    get_local $arg
)
(export "prod_M" (func $prod_M))
(func $prod_M (param $arr_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr_ptr 
    i32.const 0
    i32.const 0
    get_local $res_ptr
    call $prod
)
(export "sum_M" (func $sum_M))
(func $sum_M (param $arr_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr_ptr 
    i32.const 0
    i32.const 0
    get_local $res_ptr
    call $sum
)
(export "sum_all_M" (func $sum_all_M))
(func $sum_all_M (param $arr_ptr i32)(param $nanFlag i32)(result f64)
    (local $len i32)(local $i i32)(local $res f64)(local $temp f64)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    (set_local $i (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.add (get_local $i)(i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))        (i32.const 3))))
    get_local $i
    get_local $len
    i32.eq
    if
        (return (f64.const 0))
    end
    get_local $nanFlag
    if
        
        loop
             (tee_local $temp (f64.load offset=8 align=8 (get_local $i)))
             call $isnan
             i32.eqz
             if
                (set_local $res 
                    (f64.add (get_local $res)(get_local $temp))
                )
             end
        (set_local $i (i32.add (get_local $i)(i32.const 8)))
        (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    else
        loop
            (set_local $res 
                (f64.add (get_local $res)  (f64.load offset=8 align=8 (get_local $i)))
            )
            (set_local $i (i32.add (get_local $i)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    
    (return (get_local $res))
)
(export "prod_all_M" (func $prod_all_M))
(func $prod_all_M (param $arr_ptr i32)(param $nanFlag i32)(result f64)
    (local $len i32)(local $i i32)(local $res f64)(local $temp f64)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end
    (set_local $i (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.add (get_local $i)(i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))        (i32.const 3))))
    get_local $i
    get_local $len
    i32.eq
    if
        (return (f64.const 0))
    end
    (set_local $res (f64.const 1))
    get_local $nanFlag
    if
        
        loop
             (tee_local $temp (f64.load offset=8 align=8 (get_local $i)))
             call $isnan
             i32.eqz
             if
                (set_local $res 
                    (f64.mul (get_local $res)(get_local $temp))
                )
             end
        (set_local $i (i32.add (get_local $i)(i32.const 8)))
        (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
    end
    else
        loop
            (set_local $res 
                (f64.mul (get_local $res)  (f64.load offset=8 align=8 (get_local $i)))
            )
            (set_local $i (i32.add (get_local $i)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    
    (return (get_local $res))
)
(export "mean_all_M" (func $mean_all_M))
(func $mean_all_M  (param $arr_ptr i32) (param $nanFlag i32)(result f64) 
    (local $len i32)(local $i i32)(local $res f64)(local $numel i32)(local $temp f64)
    get_local $arr_ptr
    call $is_null
    if
        i32.const 6
        call $throwError
    end 
    (set_local $numel (i32.load offset=4 align=4 (get_local $arr_ptr)))
    (set_local $i (i32.load offset=8 align=4 (get_local $arr_ptr)))
    (set_local $len (i32.add (get_local $i)(i32.shl (get_local $numel)(i32.const 3))))
    get_local $i
    get_local $len
    i32.eq
    if
        (return (f64.const 0))
    end
    get_local $nanFlag
    if      
        loop
             (tee_local $temp (f64.load offset=8 align=8 (get_local $i)))
             call $isnan
             i32.eqz
             if
                (set_local $res 
                    (f64.add (get_local $res)(get_local $temp))
                )
             end
        (set_local $i (i32.add (get_local $i)(i32.const 8)))
        (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    else
        loop
            (set_local $res 
                (f64.add (get_local $res)  (f64.load offset=8 align=8 (get_local $i)))
            )
            (set_local $i (i32.add (get_local $i)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $i)(get_local $len)))
        end
    end
    (return (f64.div (get_local $res)(f64.convert_s/i32 (get_local $numel))))
)  

    
(export "prod_MM" (func $prod_MM))
(func $prod_MM (param i32 i32 i32) (result i32)
    get_local 0
    get_local 1
    i32.load offset=4 align=4
    i32.const 1
    i32.eq
    i32.eqz
    ;; If not a scalar, throw error
    if
        (call $throwError (i32.const 15))
    end
    ;; Load only value
    get_local 0
    (f64.load (i32.load offset=8 align=4 (get_local 1)))
    i32.trunc_s/f64 
    get_local 2
    call $prod
)
(export "sum_MM" (func $sum_MM))
(func $sum_MM (param i32 i32 i32) (result i32)
    get_local 0
    get_local 1
    i32.load offset=4 align=4
    i32.const 1
    i32.eq
    i32.eqz
    ;; If not a scalar, throw error
    if
        (call $throwError (i32.const 15))
    end
    ;; Load only value
    get_local 0
    (f64.load (i32.load offset=8 align=4 (get_local 1)))
    i32.trunc_s/f64
    get_local 2
    call $sum
)
(export "prod_MS" (func $prod_MS))
(func $prod_MS (param i32 f64 i32) (result i32)
    get_local 0
    get_local 1
    i32.trunc_s/f64
    i32.const 0
    get_local 2
    call $prod
)
(export "sum_MS" (func $sum_MS))
(func $sum_MS (param i32 f64 i32) (result i32)
    get_local 0
    get_local 1
    i32.trunc_s/f64
    i32.const 0
    get_local 2
    call $sum
)
;;TODO(dherre3) Check for $mean_MSS for compilers
(export "mean_MSS" (func $mean_MSS))
(func $mean_MSS (param $arr_ptr i32)(param $dim f64)(param $nanFlag f64)(param $res_ptr i32)(result i32)
    get_local $arr_ptr
    get_local $dim
    i32.trunc_s/f64
    get_local $nanFlag
    i32.trunc_s/f64
    get_local $res_ptr
    call $mean
)
(export "mean_MS" (func $mean_MS))
(func $mean_MS (param $arr_ptr i32)(param $dim f64)(param $res_ptr i32)(result i32)
    get_local $arr_ptr
    get_local $dim
    i32.trunc_s/f64
    i32.const 0
    get_local $res_ptr
    call $mean
)
(export "mean_M" (func $mean_M))
(func $mean_M (param $arr_ptr i32)(param $res_ptr i32)(result i32)
    get_local $arr_ptr
    i32.const 0
    i32.const 0
    get_local $res_ptr
    call $mean
)
(export "mean" (func $mean))
(func $mean (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(param $res_ptr i32)(result i32)
    (local $result_arr i32)(local $dim_num i32)
    (set_local $dim_num (i32.load offset=12 align=4 (get_local $arr_ptr)))
    (call $sum (get_local $arr_ptr)(get_local $dim)(get_local $nanFlag)(get_local $res_ptr))
    set_local $result_arr
    get_local $dim
    i32.eqz        
    if 
        (call $findNonSingletonDimension (i32.load offset=16 align=4 (get_local $arr_ptr))(get_local $dim_num)) ;; Get first-non-singleton
        tee_local $dim
        i32.const -1 
        i32.eq
        if
            get_local $result_arr
            return
        end
    else 
        (i32.or 
            (i32.gt_s (get_local $dim)(get_local $dim_num))
            (i32.lt_s (get_local $dim)(i32.const 0)))
        if
            get_local $result_arr
            return
        end
    end
    get_local $result_arr
    (call $size_dim (get_local $arr_ptr)(get_local $dim)) ;; Get length of given dimension
    get_local $result_arr
    call $rdivide_MS
)
;; (export "std" (func $std))
;; (func $std (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(result i32)
;;     (call $rdivide_MS 
;;         ())
;;     (call $power_MS 
;;         (call $minus_MM 
;;         (get_local $arr_ptr)
;;         (call $mean (get_local $arr_ptr)(get_local $dim)(get_local $nanFlag)))
;;         (i32.const 2))
    
;;     (call $sum (get_local $arr_ptr)(get_local $dim)(get_local $nanFlag))
;;     get_local $dim
;;     i32.eqz
;;     if (result f64)
;;         (call $array_dim_non_singleton (get_local $arr_ptr)) ;; Get first-non-singleton
;;     else 
;;         (call $size_dim (get_local $arr_ptr)(get_local $dim)) ;; Get length of given dimension
;;     end
;;     call $rdivide_MS
;; )
(func $array_dim_non_singleton (param $arr_ptr i32)(result f64)
    (local $i i32)(local $dim_ptr i32)(local $dim_value f64)(local $len_dims i32)
    ;; $array_dim_non_singleton Finds the length of the first non-singleton dimension
    ;; (param $arr_ptr i32) Array ptr. 
    ;; Contract: This function assumes the arr_ptr is an actual array.
    ;; Returns f64 value of the first non-singleton dimension, or 0 otherwise.
    get_local $arr_ptr
    call $mxarray_core_get_dimensions_ptr
    set_local $dim_ptr
    get_local $arr_ptr
    call $mxarray_core_get_number_of_dimensions
    set_local $len_dims
    loop
        block
        (br_if 0 (i32.ge_s (get_local $i)(get_local $len_dims)))
        (f64.load (i32.add (get_local $dim_ptr)(i32.mul (get_local $i)(i32.const 8))))
        tee_local $dim_value
        f64.const 1
        f64.gt
        if
            get_local $dim_value
            return
        end
        (set_local $i (i32.add (get_local $i)(i32.const 1)))
        br 1
        end
    end
    f64.const 0
)
(export "sum" (func $sum))
(func $sum (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(param $res_ptr i32)(result i32)

    (call $reduction 
        (get_local $arr_ptr)(get_local $dim)
        (get_local $nanFlag)(get_local $res_ptr)(i32.const 0)(i32.const 1)(i32.const 5))
)


(export "any" (func $any))
(func $any (param $arr_ptr i32)(param $dim i32)(param $res_ptr i32)(result i32)
    (call $reduction 
        (get_local $arr_ptr)(get_local $dim)
        (i32.const 1)(get_local $res_ptr)(i32.const 0)(i32.const 1)(i32.const 18))
)
(func $dynamic_loop_two (param $a f64)(param $b f64)(result i32)
    get_local $a
    get_local $b
    f64.eq
    if 
        i32.const 0
        return
    end
    get_local $a
    get_local $b
    f64.gt
    if (result i32)
        i32.const 1
    else
        i32.const 2
    end
)
(func $dynamic_loop_three (param $a f64)(param $b f64)(param $c f64)(result i32)
    get_local $a
    get_local $c
    f64.eq
    if 
        i32.const 0
        return
    end
    get_local $a
    get_local $c
    f64.gt
    get_local $b
    f64.const 0
    f64.lt
    i32.and
    if 
        i32.const 1
        return
    end
    get_local $a
    get_local $c
    f64.lt
    get_local $b
    f64.const 0
    f64.gt
    i32.and
    if
        i32.const 2
        return
    end
    i32.const 0
)
(export "all_nonzero_reduction" (func $all_nonzero_reduction))
(func $all_nonzero_reduction (param $arr_ptr i32)(result i32)
    (local $step i32)(local $len i32)
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    (set_local $arr_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop               
            get_local $arr_ptr
            get_local $step
            i32.add
            f64.load offset=0 align=8
            f64.const 0
            f64.eq
            if
                i32.const 0
                return
            end
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end 
    end
    i32.const 1
)
(export "any_nonzero_reduction" (func $any_nonzero_reduction))
(func $any_nonzero_reduction (param $arr_ptr i32)(result i32)
    (local $step i32)(local $len i32)
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    (set_local $arr_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    get_local $len
    i32.const 0
    i32.gt_s
    if
        loop                 
            get_local $arr_ptr
            get_local $step
            i32.add
            f64.load offset=0 align=8
            i32.trunc_s/f64
            i32.eqz
            if
                i32.const 1 
                return
            end
            (set_local $step (i32.add (get_local $step)(i32.const 8)))
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))
        end 
    end
    i32.const 0
)
(export "all" (func $all))
(func $all (param $arr_ptr i32)(param $dim i32)(param $res_ptr i32)(result i32)
    (call $reduction 
        (get_local $arr_ptr)(get_local $dim)
        (i32.const 1)(get_local $res_ptr)(i32.const 1)(i32.const 1)(i32.const 17))
)

(export "prod" (func $prod))
(func $prod (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(param $res_ptr i32)(result i32)
    (call $reduction 
        (get_local $arr_ptr)(get_local $dim)
        (get_local $nanFlag)(get_local $res_ptr)(i32.const 1)(i32.const 1)(i32.const 9))
)
;; MATJUICE
(export "convert_scalar_to_mxarray" (func $convert_scalar_to_mxarray))
(func $convert_scalar_to_mxarray (param $scalar f64) (result i32)
    (local $out_ptr i32)
    (tee_local $out_ptr 
        (call $create_mxvector (i32.const 1)(i32.const 0)(i32.const 0)(i32.const 0)))
    i32.const 1
    get_local $scalar
    call $set_array_index_f64
    get_local $out_ptr
)

;; Used for sum, prod, etc.
(func $traverse_reduction (param $total_ptr i32)(param $mat_ptr i32)
    (; Traverses a current matrix completely and sets the right indices on the result matrix;)
    (param $mat_shape_ptr i32)(param $dim i32)(param $nanFlag i32)(param $binary_op_flag i32)(param $func_ptr i32)(param $curr_dim i32)(param $offset i32)
    (param $mult i32)(param $offset_tot i32)(param $mult_tot i32)
    (local $i i32)(local $shape_dim_len i32)(local $shape_len i32)(local $new_offset i32)(local $new_mult i32)
    (local $new_offset_tot i32)(local $new_mult_tot i32)(local $i_index i32)
    (local $value f64)
    (set_local $shape_dim_len (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $mat_shape_ptr)(get_local $curr_dim))))
    (set_local $shape_len (i32.load offset=4 align=4 (get_local $mat_shape_ptr)))
    (set_local $i (i32.const 1))
    loop
        block
            (br_if 0 (i32.gt_s (get_local $i)(get_local $shape_dim_len)))
            (set_local $i_index (i32.sub (get_local $i)(i32.const 1)))
            (set_local $new_offset 
                (i32.add (get_local $offset)
                    (i32.mul (get_local $mult)(get_local $i_index))))
            (set_local $new_mult 
                (i32.mul (get_local $mult)(get_local $shape_dim_len)))
            get_local $curr_dim
            get_local $dim
            i32.eq
            if
                (set_local $new_offset_tot (get_local $offset_tot))
                (set_local $new_mult_tot (get_local $mult_tot))
            else
                (set_local $new_offset_tot 
                    (i32.add 
                        (get_local $offset_tot)
                        (i32.mul (get_local $mult_tot)(get_local $i_index))))
                (set_local $new_mult_tot 
                    (i32.mul (get_local $mult_tot)(get_local $shape_dim_len)))
            end
            get_local $curr_dim
            get_local $shape_len
            i32.eq
            if
                (set_local $value
                    (call $get_array_index_f64 
                        (get_local $mat_ptr)
                        (i32.add (get_local $new_offset)(i32.const 1))))
                get_local $nanFlag
                i32.const 1
                i32.eq
                if
                    get_local $value
                    call $isnan
                    if
                        br 2
                    end
                end
                (set_local $new_offset_tot (i32.add (get_local $new_offset_tot)(i32.const 1)))

                get_local $binary_op_flag
                if
                    (call $set_array_index_f64 (get_local $total_ptr)(get_local $new_offset_tot)
                    (call_indirect (type $type_binary_op_f64)
                        ( call $get_array_index_f64 (get_local $total_ptr)(get_local $new_offset_tot))
                        (get_local $value)
                        (get_local $func_ptr)
                        ))
                else
                    (call $set_array_index_f64 (get_local $total_ptr)(get_local $new_offset_tot)
                        (call_indirect (type $type_unary_op_f64)
                            (call $get_array_index_f64 (get_local $total_ptr)(get_local $new_offset_tot))
                            (get_local $func_ptr)
                        ))
                end
                
            else
                (call $traverse_reduction
                    (get_local $total_ptr)
                    (get_local $mat_ptr)
                    (get_local $mat_shape_ptr)
                    (get_local $dim)
                    (get_local $nanFlag)
                    (get_local $binary_op_flag)
                    (get_local $func_ptr)
                    (i32.add (get_local $curr_dim)(i32.const 1))
                    (get_local $new_offset)(get_local $new_mult)(get_local $new_offset_tot)(get_local $new_mult_tot)
                )
            end
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1
        end
    end    
)
(func $check_boxed_scalar_value (param $boxed_scalar i32)(result f64)
    get_local $boxed_scalar
    i32.load offset=4 align=4
    i32.const 1
    i32.eq
    if
        (return (f64.load (i32.load offset=8 align=4 (get_local $boxed_scalar))))
    else
        (call $throwError (i32.const 23))
        unreachable
    end
    f64.const 0
)
(export "mtimes_SM" (func $times_SM))
(export "mtimes_MS" (func $times_MS))
(export "mtimes_MM" (func $mtimes_MM))

(func $mtimes_MM (param $m1_ptr i32)(param $m2_ptr i32)(param $out_ptr i32)(result i32)
    (local $m1_shape_ptr i32)(local $m2_shape_ptr i32)
    (local $rows_m1 i32)(local $rows_m2 i32)
    (local $cols_m1 i32)(local $cols_m2 i32)
    (local $size_out_ptr i32)(local $out_ptr i32)
    (local $i i32)(local $j i32)(local $k i32)
    (local $acc f64)
    
    get_local $m1_ptr
    call $ismatrix
    get_local $m2_ptr
    call $ismatrix
    i32.and
    i32.eqz
    if
        i32.const 17
        call $throwError
    end
    
    (tee_local $cols_m1 (i32.trunc_s/f64 (f64.load offset=8 align=8 (i32.load offset=16 align=4 (get_local $m1_ptr)))))
    (set_local $rows_m1 (i32.trunc_s/f64 (f64.load offset=0 align=8 (i32.load offset=16 align=4 (get_local $m1_ptr)))))
    (set_local $cols_m2 (i32.trunc_s/f64 (f64.load offset=8 align=8 (i32.load offset=16 align=4 (get_local $m2_ptr)))))
    (tee_local $rows_m2 (i32.trunc_s/f64 (f64.load offset=0 align=8 (i32.load offset=16 align=4 (get_local $m2_ptr)))))
    i32.ne
    if
        i32.const 18
        call $throwError
    end

    get_local $out_ptr
    call $is_array
    if
        (f64.load offset=0  align=8 (i32.load offset=16 align=4 (get_local $out_ptr)))
        i32.trunc_s/f64
        get_local $rows_m1
        i32.eq
        (f64.load offset=8 align=8 (i32.load offset=16 align=4 (get_local $out_ptr)))        
        i32.trunc_s/f64
        get_local $cols_m2
        i32.eq
        i32.and
        ;; (call $verify_matrix_matching_dimensions (i32.load offset=8 align=4 (get_local $size_out_ptr))(i32.const 2)(i32.load offset=16 align=4 (get_local $out_ptr))(i32.load offset=12 align=4 (get_local $out_ptr)))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
    else
        (set_local $out_ptr (call $zeros_2D (f64.convert_s/i32 (get_local $rows_m1))(f64.convert_s/i32 (get_local $cols_m2))))
    end

    loop
        block 
        (br_if 0 (i32.ge_s (get_local $i)(get_local $rows_m1)))
            (set_local $j (i32.const 0))
            loop
            block 
            (br_if 0 (i32.ge_s (get_local $j)(get_local $cols_m2)))
            (set_local $acc (f64.const 0))
            (set_local $k (i32.const 0))                
            loop
                block 
                (br_if 0 (i32.ge_s (get_local $k)(get_local $rows_m2)))
                    (set_local $acc (f64.add 
                        (get_local $acc)
                        (f64.mul 
                            (call $get_array_index_f64_no_check (get_local $m1_ptr)
                                    (i32.add (get_local $i)
                                        (i32.mul (get_local $rows_m1)
                                                (get_local $k))))
                            (call $get_array_index_f64_no_check (get_local $m2_ptr)
                                    (i32.add  (get_local $k)
                                        (i32.mul (get_local $rows_m2)
                                        (get_local $j))))
                    )))
                    (set_local $k (i32.add (i32.const 1)(get_local $k)))
                br 1
                end
                end 
                (call $set_array_index_f64_no_check 
                    (get_local $out_ptr)
                        (i32.add (get_local $i)
                                    (i32.mul (get_local $rows_m1)
                                        (get_local $j)))
                    (get_local $acc))
                (set_local $j (i32.add (i32.const 1)(get_local $j)))
            br 1
            end
            end
        (set_local $i (i32.add (i32.const 1)(get_local $i)))
        br 1
        end
    end
    get_local $out_ptr
)
;; UNARY OPS
(elem $tab (i32.const 20) $round_S $ceil_S $sqrt_S $uminus_S $uplus_S $abs_S $not_S $fix_S 
    $sin_S $cos_S $tan_S $exp_S $log_S $log10_S $log2_S $floor_S $identity_f64)

(export "floor_S" (func $floor_S))
(func $floor_S (param f64) (result f64)
    get_local 0
    f64.floor
)

(export "round_S" (func $round_S))
(func $round_S (param f64) (result f64)
    get_local 0
    f64.nearest
)
(export "round_M" (func $round_M))
(func $round_M (param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 20)(get_local  $res_ptr))
)
(export "ceil_S" (func $ceil_S))
(func $ceil_S (param f64) (result f64)
    get_local 0
    f64.ceil
)
(export "sqrt_S" (func $sqrt_S))
(func $sqrt_S (param f64) (result f64)
    get_local 0
    f64.sqrt
)
    (export "uminus_S" (func $uminus_S))
(func $uminus_S (param f64) (result f64)
    get_local 0
    f64.const -1
    f64.mul
)
(export "uplus_S" (func $uplus_S))
(func $uplus_S (param f64) (result f64)
    get_local 0
    f64.const -1
    f64.mul
)
    (export "abs_S" (func $abs_S))
(func $abs_S (param f64) (result f64)
    get_local 0
    f64.abs
)
(export "fix_S" (func $fix_S))
(func $fix_S (param f64) (result f64)
    get_local 0
    f64.trunc
)
(export "sqrt_M" (func $sqrt_M))
(func $sqrt_M (param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 22)(get_local $res_ptr))
)

(export "ceil_M" (func $ceil_M))
(func $ceil_M (param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 21)(get_local $res_ptr))
)

(export "uminus_M" (func $uminus_M))
(func $uminus_M (param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 23)(get_local $res_ptr))
)

(export "uplus_M" (func $uplus_M))
(func $uplus_M (param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 24)(get_local $res_ptr))
)

(export "abs_M" (func $abs_M))
(func $abs_M (param $arr_ptr i32)(param $res_ptr i32)(result i32)
    (local $len i32)(local $step i32)(local $new_arr_data_ptr i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        i32.const 6
        call $throwError
    end
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    get_local $res_ptr
    call $is_null
    if
        (set_local $res_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
    else
        (call $mxarrays_have_same_dimensions (get_local $res_ptr)(get_local $arr_ptr))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
    end
    (set_local $new_arr_data_ptr (i32.load offset=8 align=4 (get_local $res_ptr)))
    (set_local $arr_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    get_local $len
    i32.const 0
    i32.gt_s
    if
        (set_local $step (get_local $new_arr_data_ptr))
        (set_local $len (i32.add (get_local $new_arr_data_ptr)(get_local $len)))
        loop                
            get_local $step
            get_local $arr_ptr
            f64.load offset=0 align=8
            f64.abs
            f64.store offset=0 align=8
            (set_local $arr_ptr (i32.add (get_local $arr_ptr)(i32.const 8))) 
            (set_local $step (i32.add (get_local $step)(i32.const 8)))  
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))            
        end
    end
    get_local $res_ptr
    ;; (call $elementwise_mapping (get_local $arr_ptr)(i32.const 25)(get_local $res_ptr))
)

(export "not_S" (func $not_S))
(func $not_S (param f64) (result f64)
    get_local 0
    f64.const 0
    f64.eq
    f64.convert_s/i32
)
(export "not_M" (func $not_M))
(func $not_M (param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 26)(get_local $res_ptr))
)

(export "fix_M" (func $fix_M))
(func $fix_M (param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 27)(get_local $res_ptr))
)
(export "sin_M" (func $sin_M))
(func $sin_M (param $arr_ptr i32) (param $new_arr_ptr i32)(result i32)
        (local $len i32)(local $step i32)(local $new_arr_data_ptr i32)
    get_local $arr_ptr
    call $is_array
    i32.eqz
    if
        i32.const 6
        call $throwError
    end

    get_local $new_arr_ptr
    call $is_null
    if
        (set_local $new_arr_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
    else
        (call $mxarrays_have_same_dimensions (get_local $new_arr_ptr)(get_local $arr_ptr))
        i32.eqz
        if
            (call $throwError (i32.const 20))
        end
    end
    (set_local $len (i32.shl (i32.load offset=4 align=4 (get_local $arr_ptr))(i32.const 3)))
    (set_local $new_arr_ptr (call $copy_mxarray_structure (get_local $arr_ptr)))
    (set_local $new_arr_data_ptr (i32.load offset=8 align=4 (get_local $new_arr_ptr)))
    (set_local $arr_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
    get_local $len
    i32.const 0
    i32.gt_s
    if
        (set_local $step (get_local $new_arr_data_ptr))
        (set_local $len (i32.add (get_local $new_arr_data_ptr)(get_local $len)))
        loop                
            get_local $step
            get_local $arr_ptr
            f64.load offset=0 align=8
            call $sin_S
            f64.store offset=0 align=8
            (set_local $arr_ptr (i32.add (get_local $arr_ptr)(i32.const 8))) 
            (set_local $step (i32.add (get_local $step)(i32.const 8)))  
            (br_if 0 (i32.lt_s (get_local $step)(get_local $len)))            
        end
    end
    get_local $new_arr_ptr
)

(export "sin_M_noallocation" (func $sin_M_noallocation))
(func $sin_M_noallocation (param $arr_ptr i32) (param $res_ptr i32)(result i32) 
    (call $elementwise_mapping_noallocation (get_local $arr_ptr)(get_local $res_ptr)(i32.const 28))
)
(export "cos_M" (func $cos_M))
(func $cos_M (param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 29)(get_local $res_ptr ))
)
(export "tan_M" (func $tan_M))
(func $tan_M (param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 30)(get_local $res_ptr ))
)
(export "exp_M" (func $exp_M))
(func $exp_M (param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 31)(get_local $res_ptr ))
)
(export "log_M" (func $log_M))
(func $log_M (param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 32)(get_local $res_ptr ))
)
(export "log10_M" (func $log10_M))
(func $log10_M (param $arr_ptr i32)(param $res_ptr i32) (result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 33)(get_local $res_ptr ))
)
(export "log2_M" (func $log2_M))
(func $log2_M (param $arr_ptr i32) (param $res_ptr i32)(result i32)
    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 34)(get_local $res_ptr))
)
(export "floor_M" (func $floor_M))
(func $floor_M (param $arr_ptr i32)(param $res_ptr i32) (result i32)

    (call $elementwise_mapping (get_local $arr_ptr)(i32.const 35)(get_local $res_ptr ))
)

(export "gcCheckExternalToIncreaseRCSite" (func $gcCheckExternalToIncreaseRCSite))
(func $gcCheckExternalToIncreaseRCSite (param i32)
    ;; Only process if not null
    ;; %gc-time-start
    get_local 0
    i32.eqz
    i32.eqz
    if
        ;; Get the external flag, shift the reference counter number
        get_local 0
        i32.load16_u offset=24 align=2
        i32.const 8
        i32.shr_u
        i32.eqz
        if
            get_local 0
            get_local 0
            i32.load8_u offset=24 align=1
            i32.const 1
            i32.add
            i32.store8 offset=24 align=1
        end
    end
    ;; %gc-time-end
)
(export "gcGetExternalFlag" (func $gcGetExternalFlag))
(func $gcGetExternalFlag (param i32)(result i32)
;; Only process if not null
    ;; %gc-time-start
    get_local 0
    i32.eqz
    i32.eqz
    if (result i32)
        ;; Get the external flag, shift the reference counter number
        get_local 0
        i32.load16_u offset=24 align=2
        i32.const 8
        i32.shr_u
        i32.const 1
        i32.and
    else
        i32.const 0
    end
    ;; %gc-time-end
)
(export "gcSetExternalFlag" (func $gcSetExternalFlag))
(func $gcSetExternalFlag (param $flag i32)(param $ptr i32)
    ;; %gc-time-start
    get_local $ptr 
    i32.eqz
    i32.eqz
    if
        ;; Get the external flag, shift the reference counter number
        get_local $ptr
        get_local $flag
        i32.const 1
        i32.and
        i32.store8 offset=25
    end
    ;; %gc-time-end
)
(export "gcCheckExternalToDecreaseRCSite" (func $gcCheckExternalToDecreaseRCSite))
(func $gcCheckExternalToDecreaseRCSite (param i32)
    (local $temp i32)
    ;; %gc-time-start
   ;; Only process if not null
    get_local 0
    i32.eqz
    i32.eqz
    if 
        ;; Get the external flag, shift the reference counter number
        get_local 0
        i32.load8_u offset=25 align=1
        i32.const 1
        i32.and
        i32.eqz
        if
            get_local 0
            i32.load8_u offset=24 align=1
            i32.const 1
            i32.sub
            (tee_local $temp)
            i32.eqz
            if
                (call $free_macharray (get_local 0))
            else
                get_local 0
                get_local $temp
                i32.store8 offset=24 align=1
            end
        end
    end
    ;; %gc-time-end
)
(export "gcGetRC" (func $gcGetRC))
(func $gcGetRC (param i32)(result i32)
    ;; %gc-time-start
   ;; Only process if not null
    get_local 0
    if
        ;; Get the external flag, shift the reference counter number
        get_local 0
        i32.load8_u offset=24 align=1
        return
    else
        (call $throwError (i32.const 6))
        unreachable
    end
    i32.const 0
    ;; %gc-time-end
)
(export "gcCheckExternalToSetReturnFlagAndSetRCZero" (func $gcCheckExternalToSetReturnFlagAndSetRCZero))
(func $gcCheckExternalToSetReturnFlagAndSetRCZero (param i32)
    ;; %gc-time-start
   ;; Only process if not null
    get_local 0
    if 
        ;; Get the external flag, shift the reference counter number
        get_local 0
        i32.load8_u offset=25 align=1
        i32.const 1
        i32.and
        i32.eqz        
        if
            ;; Store reference
            get_local 0
            i32.const 0
            i32.store8 offset=24 align=1
            ;; Store return flag 10=2, for 1 return, 0 for external
            get_local 0
            i32.const 2
            i32.store8 offset=25 align=1
        end
    end
    ;; %gc-time-end
)

(export "gcCheckExternalAndReturnFlagToFreeSite" (func $gcCheckExternalAndReturnFlagToFreeSite))
(func $gcCheckExternalAndReturnFlagToFreeSite (param i32 i32)(result i32)
    (local $flags i32)(local $i i32)(local $top i32)
    ;; %gc-time-start
    get_local 0
    get_local 1
    i32.and
    if
        get_local 0
        i32.load offset=0 align=4
        tee_local $top
        if
            loop
                get_local 0
                get_local $i
                i32.const 2
                i32.shl
                i32.add
                i32.load offset=4 align=4
                get_local 1
                i32.eq
                if
                    i32.const 0
                    ;; %gc-time-end
                    return
                end
                (set_local $i (i32.add (i32.const 1)
                    (get_local $i)))
                (br_if 0 (i32.lt_s 
                    (get_local $i)
                    (get_local $top)))
            end
        end 
        ;; Get the external flag
        get_local 1
        i32.load8_u offset=25 align=1
        tee_local $flags
        i32.const 1
        i32.and
        i32.eqz
        if
            ;; Get return flag
            get_local $flags
            i32.const 1
            i32.shr_u
            i32.eqz
            if
                ;; Store site
                get_local 0
                get_local $top
                i32.const 2
                i32.shl
                i32.add
                get_local 1
                i32.store offset=4 align=4
                ;; Store length
                get_local 0
                get_local $top
                i32.const 1
                i32.add
                i32.store offset=0 align=4

                (call $free_macharray (get_local 1))
                i32.const 1 
                ;; %gc-time-end
                return 
            end
        end    
    end
    i32.const 0
    ;; %gc-time-end
    return
)
(export "gcCheckExternalToResetReturnFlag" (func $gcCheckExternalToResetReturnFlag))
(func $gcCheckExternalToResetReturnFlag (param $site i32)
    ;; %gc-time-start
    get_local 0
    if 
        ;; Get the external flag 
        get_local 0
        i32.load8_u offset=25 align=1
        i32.const 1
        i32.and
        i32.eqz
        if
            get_local 0
            i32.const 0
            i32.store8 offset=25 align=1
        end
    end
    ;; %gc-time-end
 ) 
(export "gcInitiateRC" (func $gcInitiateRC))
(func $gcInitiateRC (param $arr i32)(param $rc i32)
    ;; %gc-time-start
    get_local $arr
    if
        get_local $arr
        get_local $rc
        i32.store8 offset=24 align=1
    end
    ;; %gc-time-end
)
(export "gcDecreaseRCSite" (func $gcDecreaseRCSite))
(func $gcDecreaseRCSite (param $arr i32)
    (local $temp  i32)
    ;; %gc-time-start
    get_local 0
    if
        get_local 0
        i32.load8_u offset=24 align=1
        i32.const 1
        i32.sub
        (tee_local $temp)
        i32.eqz
        if
            (call $free_macharray (get_local 0))
        else
            get_local 0
            get_local $temp
            i32.store8 offset=24 align=1
        end
    end
    ;; %gc-time-end
)
(export "gcIncreaseRCSite" (func $gcIncreaseRCSite))
(func $gcIncreaseRCSite (param $arr i32)
    ;; %gc-time-start
    get_local 0
    if
        ;; %gc-macharray-allocation
        get_local 0
        get_local 0
        i32.load8_u offset=24 align=1
        i32.const 1
        i32.add
        i32.store8 offset=24 align=1
    end
    ;; %gc-time-end
)
(export "gcSetReturnFlagAndSetRCToZero" (func $gcSetReturnFlagAndSetRCToZero))
(func $gcSetReturnFlagAndSetRCToZero (param i32)
   ;; Only process if not null
   ;; %gc-time-start
    get_local 0
    if 
        ;; Store reference
        get_local 0
        i32.const 0
        i32.store8 offset=24 align=1
        ;; Store return flag 10=2, for 1 return, 0 for external
        get_local 0
        i32.const 2
        i32.store8 offset=25 align=1
    end
    ;; %gc-time-end
)

(export "gcSetRCToZero" (func $gcSetRCToZero))
(func $gcSetRCToZero (param i32)
   ;; Only process if not null
   ;; %gc-time-start
    get_local 0
    if 
        ;; Store reference
        get_local 0
        i32.const 0
        i32.store8 offset=24 align=1
    end
    ;; %gc-time-end
)
(export "gcCheckReturnFlagToFreeSite" (func $gcCheckReturnFlagToFreeSite))
(func $gcCheckReturnFlagToFreeSite (param i32 i32)(result i32)
    (local $i i32)(local $top i32)
    ;; %gc-time-start
    get_local 0
    get_local 1
    i32.and
    if 
        get_local 0
        i32.load offset=0 align=4
        tee_local $top
        if
            loop
                get_local 0
                get_local $i
                i32.const 2
                i32.shl
                i32.add
                i32.load offset=4 align=4
                get_local 1
                i32.eq
                if
                    ;; If site found return!
                    i32.const 0
                    ;; %gc-time-end
                    return
                end
                (set_local $i (i32.add (i32.const 1)
                    (get_local $i)))
                (br_if 0 (i32.lt_s 
                    (get_local $i)
                    (get_local $top)))
            end
        end 
        
        get_local 1
        i32.load8_u offset=25 align=1
        i32.const 1
        i32.shr_u
        i32.eqz
        if
           ;; Store site
            get_local 0
            get_local $top
            i32.const 2
            i32.shl
            i32.add
            get_local 1
            i32.store offset=4 align=4
            ;; Store length
            get_local 0
            get_local $top
            i32.const 1
            i32.add
            i32.store offset=0 align=4 
            (call $free_macharray (get_local 1))
            i32.const 1 
            ;; %gc-time-end
            return 
        end
    end   
    i32.const 0
    ;; %gc-time-end
)
(export "gcFreeSite" (func $gcFreeSite))
(func $gcFreeSite (param i32)(result i32)
    ;; %gc-time-start
    get_local 0
    if 
        (call $free_macharray (get_local 0))
        i32.const 1
        ;; %gc-time-end
        return
    end
    i32.const 0
    ;; %gc-time-end
)

(export "gcGetReturnFlag" (func $gcGetReturnFlag))
(func $gcGetReturnFlag (param $site i32)(result i32)
    ;; %gc-time-start
    get_local 0
    if 
        get_local 0
        i32.load8_s offset=25 align=1
        i32.const 1
        i32.shr_u
        ;; %gc-time-end
        return
    else
        ;; %gc-time-end
        (call $throwError (i32.const 6))
        unreachable
    end
    i32.const 0
    ;; %gc-time-end
 )
(export "gcResetReturnFlag" (func $gcResetReturnFlag))
(func $gcResetReturnFlag (param $site i32)
    ;; %gc-time-start
    get_local 0
    if 
        get_local 0
        i32.const 0
        i32.store8 offset=25 align=1
    end
    ;; %gc-time-end
 )

(global $TOTAL_ALLOCATION (mut i64) (i64.const 0))
(global $TOTAL_ALLOCATED_MACHARRAYS (mut i32) (i32.const 0))
(global $TOTAL_FREEING (mut i64) (i64.const 0))
(global $TOTAL_DEALLOCATED_OBJECTS (mut i32) (i32.const 0))
(global $TOTAL_ALLOCATED_OBJECTS (mut i32) (i32.const 0))
(global $GC_NUMBER_OF_CALLS (mut i32) (i32.const 0))
(global $GC_TOTAL_TIME (mut f64) (f64.const 0))
(global $GC_TIC (mut f64) (f64.const 0))

(func $gcTic 
    get_global $GC_NUMBER_OF_CALLS
    i32.const 1
    i32.add
    set_global $GC_NUMBER_OF_CALLS
    call $time
    set_global $GC_TIC
)

( func $gcToc
    call $time
    get_global $GC_TIC
    f64.sub
    f64.const 1000
    f64.div
    get_global $GC_TOTAL_TIME
    f64.add
    set_global $GC_TOTAL_TIME 
)
(func $gcPrintMemoryUsageInformation
    (local $temp i32)
    get_global $TOTAL_ALLOCATED_OBJECTS
    get_global $TOTAL_DEALLOCATED_OBJECTS
    get_global $TOTAL_ALLOCATED_MACHARRAYS
    get_global $TOTAL_ALLOCATION
    get_global $TOTAL_FREEING
    i64.sub
    i32.wrap/i64
     
    get_global $TOTAL_ALLOCATED_OBJECTS 
    tee_local $temp
    if (result i32)
        get_global $TOTAL_ALLOCATION
        get_local $temp
        i64.extend_u/i32
        i64.div_s 
        i32.wrap/i64
    else
        i32.const 0
    end
    get_global $GC_NUMBER_OF_CALLS
    get_global $GC_TOTAL_TIME
    call $gcPrintMemoryUsage
)

(func $gcRecordAllocation (param i32)
    get_global $TOTAL_ALLOCATED_OBJECTS
    i32.const 1
    i32.add
    set_global $TOTAL_ALLOCATED_OBJECTS
    get_global $TOTAL_ALLOCATION
    get_local 0
    i64.extend_u/i32
    i64.add
    set_global $TOTAL_ALLOCATION
)
;; We know its a brand new macharray if the RC is 0 so far.
(func $gcMachArrayAllocation (param i32)
    get_local 0 
    call $gcGetRC
    i32.eqz
    if
        get_global $TOTAL_ALLOCATED_MACHARRAYS
        i32.const 1
        i32.add
        set_global $TOTAL_ALLOCATED_MACHARRAYS
    end
)

(func $gcRecordFreeing (param i32)
    (local $temp i64)
    get_global $TOTAL_DEALLOCATED_OBJECTS
    i32.const 1
    i32.add
    set_global $TOTAL_DEALLOCATED_OBJECTS
    get_global $TOTAL_FREEING
    get_local 0
    i32.const 4
    i32.sub
    i32.load
    i64.extend_u/i32
    i64.const 8
    i64.sub
    tee_local $temp
    get_local $temp
    i64.const 8
    i64.rem_u
    i64.sub
    i64.add
    set_global $TOTAL_FREEING
    
)