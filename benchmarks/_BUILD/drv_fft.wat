(wasmModule
    (import "js" "mem" (memory $mem 1))
    (import "js" "printError" (func $printError (param i32 i32)(result i32)))
    (import "js" "print_array_f64" (func $print_array_f64 (param i32 i32)))
    (import "js" "printString" (func $printString (param i32 i32)(result i32)))
    (import "js" "printDouble" (func $printDouble (param i32)(result i32)))
    (import "js" "printMarker" (func $printMarker))
    (import "js" "printDoubleNumber" (func $printDoubleNumber (param f64) (result f64)))
    (import "js" "time" (func $time (result f64)))
    (import "js" "printTime" (func $printTime (param f64)(result f64)))
    (import "math" "ones" (func $ones_s (result f64)))
    (import "math" "zeros" (func $zeros_s (result f64)))
    (import "math" "rand" (func $rand_s (result f64)))
    (import "math" "randn" (func $randn_s (result f64)))
    (import "math" "randi" (func $randi_s (param f64) (result f64)))
    (import "math" "isnan" (func $isnan (param f64) (result i32)))
    (import "math" "power" (func $power_SS (param f64)(param f64) (result f64)))
    (import "math" "sin" (func $sin_S (param f64)(result f64)))
    (import "math" "cos" (func $cos_S (param f64) (result f64)))
    (import "math" "tan" (func $tan_S (param f64) (result f64)))
    (import "math" "exp" (func $exp_S (param f64) (result f64)))
    (import "math" "log" (func $log_S (param f64) (result f64)))
    (import "math" "log10" (func $log10_S (param f64)(result f64)))
    (import "math" "log2" (func $log2_S (param f64)(result f64)))
    (import "math" "pi" (func $pi (result f64)))
    (import "math" "e" (func $e (result f64)))
    (import "test" "assert" (func $assert (param i32 i32)))

    ;; ;; Dummy variables which will be commented out
    ;; (;dummy;)(func $printString (param i32 i32)(result i32) i32.const 1)
    ;; (;dummy;)(func $printMarker )
    ;; (;dummy;)(func $printError (param i32 i32)(result i32) i32.const 1)
    ;; (;dummy;)(func $printDouble (param i32)(result i32) i32.const 1)
    ;; (;dummy;)(func $printDoubleNumber (param f64) (result f64) f64.const 0)
    ;; (;dummy;)(func $assert (param i32 i32))
    ;; (;dummy;)(func $ones_s (result f64) f64.const 0)
    ;; (;dummy;)(func $zeros_s (result f64) f64.const 0)
    ;; (;dummy;)(func $rand_s (result f64) f64.const 0)
    ;; (;dummy;)(func $randn_s (result f64) f64.const 0)
    ;; (;dummy;)(func $randi_s (param f64)(result f64) f64.const 0)
    ;; (;dummy;)(func $isnan (param f64)(result i32) i32.const 0)
    ;; (;dummy;)(func $power_SS (param f64 f64)(result f64) f64.const 0)
    ;; (;dummy;)(func $print_array_f64 (param i32 i32) )
    ;; (;dummy;)(func $sin_S (param f64)(result f64)f64.const 0)
    ;; (;dummy;)(func $cos_S (param f64) (result f64)f64.const 0)
    ;; (;dummy;)(func $tan_S (param f64) (result f64)f64.const 0)
    ;; (;dummy;)(func $exp_S (param f64) (result f64)f64.const 0)
    ;; (;dummy;)(func $log_S (param f64) (result f64)f64.const 0)
    ;; (;dummy;)(func $log10_S (param f64)(result f64)f64.const 0)
    ;; (;dummy;)(func $log2_S (param f64)(result f64)f64.const 0)
    ;; (;dummy;)(func $pi (result f64) f64.const 0)
    ;; (;dummy;)(func $e (result f64) f64.const 0)
    ;; (;dummy;)(func $time (result f64) f64.const 0)
    ;; (;dummy;)(func $printTime (param f64)(result f64) f64.const 0)

    (export "sin_S" (func $sin_S))
    (export "cos_S" (func $cos_S))
    (export "tan_S" (func $tan_S))
    (export "exp_S" (func $exp_S))
    (export "log_S" (func $log_S))
    (export "log10_S" (func $log10_S))
    (export "log2_S" (func $log2_S))
    (export "pi" (func $pi))
    (export "e" (func $e))


    (global $ASSERT_HEADER_FLAG i32 (i32.const 1))
    (table $tab 100 anyfunc)
 (export "mem" (memory $mem))
    (export "tab" (table $tab))
    (global $HEAP_TOP (mut i32) (i32.const 32764)) ;; For off from alignment due to the footer/header size of four
    (global $HEAP_START (mut i32) (i32.const 32764))
    (global $PAGE_SIZE i32 (i32.const 65536))
    (global $FLAG_CHECK_SIZE_MEM (mut i32) (i32.const 1)) ;; Should be imported
    (global $COLON_TOKEN i32 (i32.const -1723))
    (global $TIC_TIME (mut f64) (f64.const 0))
    (data $mem (i32.const 0) "Out-of-memory, trying to allocate a larger memory than available\n\00\00\00\00\00\00\00\00Error: Negative length is not allowed in this context\n")
    (data $mem (i32.const 136) "Index out-of-bound\n\00\00\00\00\00\00")
    (data $mem (i32.const 160) "Index exceeds matrix dimensions\00\00")
    (data $mem (i32.const 198) "Subscript indices must either be real positive integers or logicals\00\00\00\00\00\00")
    (data $mem (i32.const 272) "Size vector should be a row vector with real elements.")
    (data $mem (i32.const 328) "Not enough input arguments.")
    (data $mem (i32.const 360) "Too many input arguments.")
    (data $mem (i32.const 384) "To RESHAPE the number of elements must not change.")
    (data $mem (i32.const 440) "Subscripted assignment dimension mismatch.")
    (data $mem (i32.const 488) "Dynamic array growth is currently not supported in set function.")
    (data $mem (i32.const 552) "Dimensions of matrices being concatenated are not consistent.")
    (data $mem (i32.const 616) "Concatanating dimension must be larger than 0")
    (data $mem (i32.const 672) "Concatanating dimension larger than the input arguments dimensions.")
    (data $mem (i32.const 744) "Matrix dimensions must agree.")
    (data $mem (i32.const 776) "Dimension argument must be a positive integer scalar within indexing range.")
    (data $mem (i32.const 856) "Input must have array class.")
    (data $mem (i32.const 888) "Arguments must be 2-D, or at least one argument must be scalar.")
    (data $mem (i32.const 952) "Inner matrix dimensions must agree.")
    (data $mem (i32.const 992) "N-dimensional arrays are not supported.")

    ;; (data $mem (i32.const 136) "\f3\e0\01\00")

    ;; (data  $mem (i32.const 80) "Error: Negative length is not allowed in this context\n")
    ;;
    (func $throwError (param $error i32)
        (local $offset i32)(local $length i32)
        (;
            Errors:
                0: "Allocating larger memory than expected"
                1: "Negative length is not allowed in this context"
                2: "Index out-of-bounds"
        ;)
        block  block  block block block block block block block block block block block block block block block block block block block
            get_local $error
            br_table 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19
            end
               (set_local $offset (i32.const 0))
               (set_local $length (i32.const 65))
                br 19
            end
               (set_local $offset (i32.const 80))
               (set_local $length (i32.const 47))
                br 18
            end
              (set_local $offset (i32.const 136))
              (set_local $length (i32.const 10))
                br 17
            end
                (set_local $offset (i32.const 160))
                (set_local $length (i32.const 31))
                br 16
            end
                (set_local $offset (i32.const 198))
                (set_local $length (i32.const 67))
                br 15
            end
                (set_local $offset (i32.const 272))
                (set_local $length (i32.const 54))
                br 14
            end
                (set_local $offset (i32.const 328))
                (set_local $length (i32.const 27))
                br 13
            end
                (set_local $offset (i32.const 360))
                (set_local $length (i32.const 24))
                br 12
            end
                (set_local $offset (i32.const 384))
                (set_local $length (i32.const 50)) ;; 434
                br 11
            end
                (set_local $offset (i32.const 440))
                (set_local $length (i32.const 42))
                br 10
            end
                (set_local $offset (i32.const 488))
                (set_local $length (i32.const 64))
                br 9
            end
                (set_local $offset (i32.const 552))
                (set_local $length (i32.const 61))
                br 8
            end
                (set_local $offset (i32.const 616))
                (set_local $length (i32.const 45))
                br 7
            end
                (set_local $offset (i32.const 672))
                (set_local $length (i32.const 67))
                br 6
            end
                (set_local $offset (i32.const 744))
                (set_local $length (i32.const 29))
                br 5
            end
                (set_local $offset (i32.const 776))
                (set_local $length (i32.const 75))
                br 4
            end
                (set_local $offset (i32.const 856))
                (set_local $length (i32.const 28))
                br 3
            end
                (set_local $offset (i32.const 888))
                (set_local $length (i32.const 63))
                br 2
            end
                (set_local $offset (i32.const 952))
                (set_local $length (i32.const 35))
                br 1
            end
                (set_local $offset (i32.const 992))
                (set_local $length (i32.const 39))
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

    (func $main
        ;; For testing purposes
    )
    (func $get_flag_check_size_mem (result i32)
        get_global $FLAG_CHECK_SIZE_MEM
    )
    (func $set_flag_check_size_mem (param i32) (result i32)
        get_local 0
        set_global $FLAG_CHECK_SIZE_MEM
        get_global $FLAG_CHECK_SIZE_MEM
    )

    (func  (param i32) (param i32)(result i32)
        i32.const 0
    )
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
        loop
            block
                (br_if 0 (i32.eq (get_local $i)(get_local $dim_number))) ;; condition
                (tee_local $temp (f64.load (i32.add (get_local $input_dim_array_ptr) (i32.mul (get_local $dim_array_elem_byte_size)(get_local $i)))))
                f64.const 1
                f64.eq
                get_local $i
                i32.const 2
                i32.ge_s
                i32.and
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
                br 1
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
    (export "create_mxarray_ND" (func $create_mxarray_ND))
    (func $create_mxarray_ND  (param $dim_array i32)(param $class i32) (param $simple_class i32)
    (param $complex i32)(param $byte_size_elem i32)
    (result i32)
    (;
        TODO(dherre3): This function assumes that the dimension array is a double.
        TODO(dherre3): Check that function handles empty input
        TODO(dherre3): Check capacity
        TODO(dherre3): Throw error if floating point input is not an integer
    ;)
        (local $dim_number i32)(local $array_length f64)(local $array_length_i32 i32)(local $loop_dim_number i32)
        (local $i i32) (local $input_dim_array_ptr i32) (local $temp f64) (local $header_size i32) (local $array_size i32)
        (local $array_data_ptr i32)(local $array_header_ptr i32)(local $dim_array_ptr i32)(local $input_dim_array_byte_size i32)
        ;; Get the size of bytes for type
        ;; ()
        get_local $dim_array
        call $mxarray_core_get_array_length
        i32.eqz
        get_local $dim_array
        call $isrow
        i32.eqz
        i32.or
        if
            i32.const 5
            call $throwError
        end

        ;; Get total array size
        (set_local $input_dim_array_ptr (call $mxarray_core_get_array_ptr (get_local $dim_array)))
        get_local $byte_size_elem
        i32.eqz
        if
            get_local $class
            i32.eqz
            if
                (set_local $byte_size_elem (call $mxarray_core_get_simple_class_byte_size (get_local $simple_class)))
            else
                (set_local $byte_size_elem (i32.const 4))
            end
        end
        ;; Allocate header
        i32.const 24
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
        ;; Get i32 length
        (set_local $array_length_i32 (i32.trunc_s/f64 (get_local $array_length )))
        ;; Check if length is one, if it is set the other dimension to get an square matrix
        get_local $loop_dim_number
        i32.const 1
        i32.eq
        if
            (f64.store (i32.add (get_local $dim_array_ptr) (i32.mul (get_local $input_dim_array_byte_size)(get_local $i)))(get_local $temp))
            (set_local $array_length_i32 (i32.mul (get_local $array_length_i32)(get_local $array_length_i32)))
        end
        ;; Setting capacity array dim
        get_local $dim_array_ptr
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
        get_local $array_length_i32
        i32.eqz
        if
            get_local $array_header_ptr
            i32.const -1
            i32.store offset=8 align=4
        else
            ;;Setting Array data ptr, add 8 bytes, 4 for the capacity of the array, 4 for alignment
            get_local $array_header_ptr
            (tee_local $array_size (i32.add (i32.const 8)(i32.mul (get_local $array_length_i32)(get_local $byte_size_elem))))
            call $malloc
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

        ;; Set other attributes
        get_local $array_header_ptr
        ;; isscalar attribute
        get_local $array_length_i32
        i32.const 1
        i32.eq
        i32.store8 offset=20 align=1
        get_local $array_header_ptr
        get_local $complex;; Complex
        i32.store8 offset=21 align=1
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
            ;; TODO(dherre3): Grow array to require size
            ;; i32.const 10
            ;; call $throwError
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
            ;; i32.const 10
            ;; call $throwError
            ;; TODO(dherre3): Grow array to require size
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
    (func $create_mxarray_empty (param $dim_num i32)(param $simple_class i32)(param $class i32)(param $complex i32) (result i32)
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
        i32.const 24
        call $malloc
         ;; Allocate array memory or return -1 if size is 0
        tee_local $header_pointer
        i32.const -1
        i32.store offset=8 align=4 ;; Store pointer to array

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


        ;; Set other attributes
        get_local $header_pointer
        get_local $complex
        i32.store8 offset=21 align=1
        get_local $header_pointer
        i32.const 0
        i32.store8 offset=20 align=1  ;; Scalar
        get_local $header_pointer

    )
    (export "create_mxvector" (func $create_mxvector))
    (func $create_mxvector
        (param $n i32)(param $simple_class i32)(param $class i32)(param $complex i32)(param $column i32)(param $elem_size i32) (result i32)
        (local $array_size i32)
        (local $header_pointer i32)(local $array_pointer i32)(local $dimension_ptr i32)
        (;

        ;)
        ;; Check size and if its smaller or equal to 0, set to 0;
        (i32.le_s (get_local $n) (i32.const 0))
        if
            (set_local $n (i32.const 0))
        end
        get_local $elem_size
        i32.eqz
        if
            get_local $class
            i32.eqz
            if
                (set_local $elem_size (call $mxarray_core_get_simple_class_byte_size (get_local $simple_class)))
            else
                (set_local $elem_size (i32.const 4)) ;; For cell_array, string, function_handle, struct
            end
        end
        (set_local $array_size
            (i32.mul
                (get_local $elem_size)
                (get_local $n)))
        ;; Allocate header memory
        ;; 4 for type attribute, 4 for number of elements,  4 for array pointer,  4 for number of dimensions, 4 for dimension pointer , 4 attributes
        i32.const 24
        call $malloc
        tee_local $header_pointer
        ;; Allocate array memory or return -1 if size is 0
        get_local $array_size
        i32.eqz
        if  (result i32)
            i32.const -1
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
        ;; Set capacity in dimensions array
        get_local $dimension_ptr
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
        else
            get_local $dimension_ptr
            f64.const 1
            f64.store offset=8 align=8
            get_local $dimension_ptr
            get_local $n
            f64.convert_s/i32
            f64.store offset=16 align=8
        end
        ;; Set other attributes
        get_local $header_pointer
        get_local $complex
        i32.store8 offset=21 align=1
        get_local $header_pointer
        get_local $n
        i32.const 1
        i32.eq
        i32.store8 offset=20 align=1  ;; Scalar
        get_local $header_pointer
    )






    (export "get_heap_top" (func $get_heap_top))
    (func $get_heap_top (result i32)
        ;; @name get_heap_top#memory
        ;; @return i32, returns the value for the top of the heap
        ;; @description
        ;;      Used for debugging and testing, returns the size of the top of heap
        get_global $HEAP_TOP
        return
    )

    (export "malloc" (func $malloc))
    (func $malloc (param $size i32) (result i32)
        (local $realsize i32) (local $end i32) (local $heap_offset i32)
        ;; @name malloc#memory
        ;; @param $size Size of the allocated payload
        ;; @return returns pointer to start of payload
        ;; @description
        ;;      Allocate a given payload based on provided size plus, alignment bits
        ;;      Save size, flag at beginning and end occupying 16bytes
        ;; TODO: Flag import, and memory import
        ;; TODO: Check the alignment with the new footer/header
        ;; Check for a positive size
        (i32.le_s (get_local $size) (i32.const 0))
        if
            i32.const  -1 ;; Error negative length
            return
        end

        ;; Add bytes to make allocation mod 64
        (tee_local $realsize (i32.rem_s (get_local $size) (i32.const 8)))
        if
            (set_local $realsize (i32.add (get_local $size) (i32.sub (i32.const 8)(get_local $realsize))))
        else
            (set_local $realsize (get_local $size))
        end


        ;; Grow memory if necessary, if unable to grow, throw trap
        (i32.add (i32.add (get_global $HEAP_TOP)(get_local $realsize)) (i32.const 16))
        (i32.mul (current_memory) (get_global $PAGE_SIZE))
        i32.sub
        tee_local $heap_offset
        i32.const 0
        i32.gt_s
        if
            get_local $heap_offset
            get_global $PAGE_SIZE
            i32.rem_s
            if (result i32)
                get_local $heap_offset
                get_global $PAGE_SIZE
                i32.div_s
                i32.const 1
                i32.add
            else
                get_local $heap_offset
                get_global $PAGE_SIZE
                i32.div_s
            end
            grow_memory
            i32.const -1
            i32.eq
            if
                i32.const 0
                call $throwError
            end
        end
        ;; Set the size, add 16 because of malloc bits
        (get_global $HEAP_TOP)
        (get_local $realsize)
        i32.const 1 ;; Free-bit
        i32.add ;; Free-bit
        (i32.store offset=0 align=4)

        ;;Add to end of block as well
        (get_global $HEAP_TOP)
        i32.const 4
        i32.add ;; 4 for header
        (get_local $realsize)
        i32.add ;; add size of payload
        (tee_local $end) ;; Set end
        (get_local $realsize) ;;Add 8 to allocated size to account for header and footer
        i32.const 1 ;; Free-bit
        i32.add ;; Free-bit
        (i32.store offset=0 align=4)
        ;; prepare return pointer value
        (i32.add (get_global $HEAP_TOP) (i32.const 4))
        ;; Update pointer value
        (set_global $HEAP_TOP (i32.add (get_global $HEAP_TOP)(i32.add (get_local $realsize)(i32.const 8))))
        ;; return pointer to start of payload
        return
    )

    ;;; HELPER FUNCTIONS

    (export "get_mem_free_bit" (func $get_free_bit_mem))
    (func $get_free_bit_mem (param $memory i32) (result i32)
        ;; @name get_mem_free_bit#memory
        ;; @param $array i32, Pointer to array whose dimensions will be returned
        ;; @return i32, Array length
        ;; @description
        ;;      Gets the array "total" number of items, or length
        get_local $memory
        i32.const 4
        i32.sub
        i32.load offset=0 align=4
        i32.const 1
        i32.and
    )


    (export "get_mem_payload_size" (func $get_mem_payload_size))
    (func $get_mem_payload_size (param $memory i32) (result i32)
        ;; @name get_mem_payload_size#memory
        ;; @param $memory i32, pointer to allocated memory by malloc
        ;; @return i32, Size of payload at header stored in size section by malloc
        ;; @description
        ;;      Gets the total size of the payload allocated by checking the header
        get_local $memory
        i32.const 4
        i32.sub
        i32.load offset=0 align=4
        i32.const 1
        i32.sub
    )


    (export "load_mem" (func $load_mem))
    (func $load_mem (param i32) (result i32)
        get_local 0
        i32.load
        return
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
        i32.const 8
        get_local $i
        i32.mul
        i32.add
        f64.load offset=0 align=8
    )
    (export "get_array_index_f64" (func $get_array_index_f64))
    (func $get_array_index_f64 (param $array_ptr i32)(param $i i32)(result f64)

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
        f64.load offset=0 align=4
        return
    )

    (export "set_array_index_f64" (func $set_array_index_f64))
    (func $set_array_index_f64 (param $array_ptr i32)(param $i i32)(param $value f64)
        (local $content_ptr i32)
        get_local $array_ptr
        call $mxarray_core_get_array_ptr
        tee_local $content_ptr
        i32.const 8
        i32.sub
        i32.load offset=0 align=4
        get_local $i
        i32.lt_s
        if
            ;; TODO(dherre3): Grow array to require size
            ;; i32.const 10
            ;; call $throwError
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
        get_local $content_ptr
        i32.add
        get_local $value
        f64.store offset=0 align=4
    )



    ;; DEBUG & TESTING

    (export "get_free_bit_from_array" (func $get_mem_free_bit_footer))
    (func $get_free_bit_from_array (param $array i32) (result i32)
        ;; @name get_free_bit_from_array#memory
        ;; @param $array i32, Get free bit from array
        ;; @return i32, Array length
        ;; @description
        ;;      Gets the array "total" number of items, or length
        ;; TODO Implement get_free_bit_from_array
        i32.const 0
    )

    (export "get_mem_free_bit_footer" (func $get_mem_free_bit_footer))
    (func $get_mem_free_bit_footer (param $memory i32) (result i32)
        ;; @name get_mem_free_bit#memory
        ;; @param $array i32, Pointer to array whose dimensions will be returned
        ;; @return i32, Array length
        ;; @description
        ;;      Gets the array "total" number of items, or length
        get_local $memory
        i32.const 4
        i32.sub
        i32.load offset=0 align=4
        i32.const 1
        i32.sub
        get_local $memory
        i32.add
        i32.load offset=0 align=4
        i32.const 1
        i32.and
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
        (call $assert (;Condition;)(i32.and (i32.ge_s (get_local $class)(i32.const 0))(i32.le_s (get_local $class)(i32.const 4)))
                    (;Error;)(i32.const 0))
        (call $assert (;Condition;)(i32.and (i32.ge_s (get_local $elem_size)(i32.const 0)) (i32.or (i32.eqz (i32.and (get_local $elem_size)(i32.const 1)))(i32.eq (get_local $elem_size)(i32.const 1)))
                    (;Error;)(i32.const 1)))
        (call $assert (;Condition;)(i32.and (i32.ge_s (get_local $simple_class)(i32.const -1)) (i32.le_s (get_local $simple_class)(i32.const 15)))
                    (;Error;)(i32.const 2))
        get_local $address
        get_local $class
        i32.store offset=0 align=1
        get_local $address
        get_local $elem_size
        i32.store offset=1 align=1
        get_local $address
        get_local $simple_class
        i32.store offset=2 align=1
        get_local $address
        get_local $simple_class
        i32.const 6
        i32.lt_u
        get_local $simple_class
        i32.const 9
        i32.gt_u
        i32.or
        i32.store offset=3 align=1

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
        (local $simple_class i32)
        get_local $arr_ptr
        i32.load8_u offset=3 align=1
        return
    )
    (export "numel" (func $numel))
    (func $numel (param $arr_ptr i32) (result i32)
        get_local $arr_ptr
        i32.const -1
        i32.eq
        if
            i32.const 6
            call $throwError
        end
        get_local $arr_ptr
        call $mxarray_core_get_array_length
    )
    (export "size" (func $size))
    (func $size (param $arr_ptr i32)(param $dim i32)(result i32)
        (; TODO(dherre3): Implement along dimension ;)
        (local $new_ptr i32)(local $i i32)(local $dim_number i32) (local $dim_ptr i32)
        get_local $arr_ptr
        i32.const -1
        i32.eq
        if
            i32.const 6
            call $throwError
        end
        (set_local $dim_number (call $mxarray_core_get_number_of_dimensions (get_local $arr_ptr )))
        (set_local $dim_ptr (call $mxarray_core_get_dimensions_ptr (get_local $arr_ptr )))

         ;; Get Dimensions
        (set_local $new_ptr
            (call $create_mxvector
                (get_local $dim_number)
                (i32.const 0)(i32.const 0)
                (i32.const 0)(i32.const 0)(i32.const 0)))
        loop
            block
            (i32.ge_s (get_local $i)(get_local $dim_number))
            br_if 0
            (f64.load offset=0 align=8 (i32.add (get_local $dim_ptr)(i32.mul (get_local $i)(i32.const 8))))
            (call $set_array_index_f64 (get_local $new_ptr)(i32.add (get_local $i)(i32.const 1))
                (f64.load offset=0 align=8 (i32.add (get_local $dim_ptr)(i32.mul (get_local $i)(i32.const 8)))))
            (set_local $i (i32.add (get_local $i)(i32.const 1))) ;; Increase loop counter
            br 1
            end
        end
        get_local $new_ptr
    )
    (func $mxarray_core_get_number_of_dimensions (param $arr_ptr i32) (result i32)
        (i32.load offset=12 align=4 (get_local $arr_ptr))
    )
    (func $mxarray_core_get_dimensions_ptr (param $arr_ptr i32) (result i32)
        (i32.load offset=16 align=4 (get_local $arr_ptr))
    )
    (export "ndims" (func $ndims))
    (func $ndims (param $arr_ptr i32) (result i32)
        get_local $arr_ptr
        call $is_null
        if
            i32.const 5
            call $throwError
        end
        get_local $arr_ptr
        call $mxarray_core_get_number_of_dimensions
    )

    (export "isscalar" (func $isscalar))
    (func $isscalar (param $arr_ptr i32) (result i32)
    (;TODO(dherre3): Check for null;)
        get_local $arr_ptr
        i32.load offset=4 align=4
        i32.const 1
        i32.eq
    )

    (export "length_S" (func $ones_S))
    (func $ones_S (param $args i32)(result f64)
        f64.const 1
    )
    (export "length_M" (func $length_M))
    (func $length_M (param $arr_ptr i32) (result f64)
        (local $new_ptr i32)(local $i i32)(local $dim_number i32) (local $dim_ptr i32)
        (local $max f64)(local $temp f64)
        get_local $arr_ptr
        call $is_null
        if
            i32.const 6
            call $throwError
        end
         (set_local $dim_number (call $mxarray_core_get_number_of_dimensions (get_local $arr_ptr )))
        (set_local $dim_ptr (call $mxarray_core_get_dimensions_ptr (get_local $arr_ptr )))
        loop
            block
            (i32.ge_s (get_local $i)(get_local $dim_number))
            br_if 0
                (tee_local $temp (f64.load offset=0 align=8 (i32.add (get_local $dim_ptr)(i32.mul (get_local $i)(i32.const 8)))))
                get_local $max
                f64.max
                set_local $max
                (set_local $i (i32.add (get_local $i)(i32.const 1))) ;; Increase loop counter
            br 1
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
        call $mxarray_core_get_number_of_dimensions
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
                (call $create_mxvector (i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 1))
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
                    ;; Return  1x1
                    (tee_local $colon_ptr
                        (call $create_mxvector (i32.const 1)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 1)(i32.const 0)))
                    i32.const 1
                    get_local $i
                    call $set_array_index_f64
                    get_local $colon_ptr
                    return
                else
                    ;; set fix(j-i)
                    (set_local  $top (f64.trunc (f64.sub (get_local $j)(get_local $i))))
                    (call $create_mxvector (i32.trunc_u/f64 (f64.add (get_local $top)(f64.const 1)))(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
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
                (call $create_mxvector (i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 1)(i32.const 0))
                return
            end
    )
    (export "colon_three" (func $colon_three))
    (func $colon_three (param $i f64)(param $j f64) (param $k f64)(result i32)
        (local $top f64)(local $colon_ptr i32)(local $i_loop f64)
                ;; If j = 0
                get_local $j
                f64.const 0
                f64.eq
                if
                    i32.const 0
                    i32.const 0
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
                        i32.const 0
                        i32.const 0
                        call $create_mxvector
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
                            i32.const 0
                            i32.const 0
                            call $create_mxvector
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
                        end
                    end
                end
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
        call $numel
        i32.eqz
        if
            i32.const 0
            i32.const 0
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
            call $numel
            i32.eqz
            if
                i32.const 0
                i32.const 0
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
            call $numel
            i32.eqz
            if
                i32.const 0
                i32.const 0
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
        (local $new_arr_ptr i32)(local $i i32)(local $len i32)
        ;; TODO (dherre3): Implement for different types and classes
        get_local $arr_ptr
        call $is_null
        if
            i32.const 6
			call $throwError
        end
        (set_local $len (call $numel (get_local $arr_ptr)))
        (set_local $i (i32.const 1))
        get_local $arr_ptr
        i32.const 0
        call $size
        i32.const 0
        i32.const 0
        i32.const 0
        i32.const 8
        call $create_mxarray_ND
        tee_local $new_arr_ptr
        loop
            block
            (i32.gt_s (get_local $i)(get_local $len))
            br_if 0

                (call $set_array_index_f64 (get_local $new_arr_ptr)(get_local $i)
                    (call $get_array_index_f64 (get_local $arr_ptr)(get_local $i)))
                (set_local $i (i32.add (get_local $i) (i32.const 1)))
            br 1
            end
        end
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
       i32.const 0
	   call $size
	   tee_local $shape_ptr
	   get_local $indices

	   call $verify_and_get_dimensions
       set_local $dim_ptr
       ;; Create
       (call $create_mxarray_ND (get_local $dim_ptr)(i32.const 0)(call $mxarray_core_get_simple_class (get_local $array_ptr))(i32.const 0)(i32.const 0))

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
        get_local $res_ptr
	)

    (export "set_f64" (func $set_f64))
    (func $set_f64 (param $array_ptr i32)(param $indices_ptr i32)(param $values_ptr i32)
       (local $shape_ptr i32)(local $dim_ptr i32)(local $dim_change i32)
       (;TODO(dherre3): Check that input is actually an mxarray ;)
	   get_local $array_ptr
	   call $is_null
	   get_local $indices_ptr
	   call $is_null
	   i32.or
	   if
			i32.const 6
			call $throwError
	   end

    ;;    i32.const 0
	   get_local $array_ptr
	   get_local $array_ptr
       i32.const 0
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
        (set_local $dim_length (call $numel (get_local $dim_ptr)))
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
                (set_local $new_mult_ind (i32.mul (get_local $mult_ind)(call $numel (get_local $dim_ptr))))
                get_local $dim
                (call $numel (get_local $indices_ptr))
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
        (set_local $len_arr (call $numel (get_local $arr_ptr)))

        ;; Get length of indices
		(tee_local $length_indices (call $numel (get_local $indices_ptr)))
        i32.const 1
        i32.gt_s
        if
            (set_local $multi_index_len (call $get_real_mxarray_dim_number_input_vector (get_local $indices_ptr)))
            (set_local $dim_ptr (call $create_mxvector (get_local $multi_index_len)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)))
        else
            (set_local $dim_ptr (call $create_mxvector (get_local $length_indices)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)))
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
                    (set_local $length_dim (call $numel (get_local $current_dim)))
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
            (set_local $dim_ptr (call $size (call $get_array_index_i32 (get_local $indices_ptr)(i32.const 1))(i32.const 0)))
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
		  (local $len_arr i32)(local $dim_ptr i32)(local $total_elem i32)(local $i i32)(local $j i32)
		  (local $length_indices i32)(local $length_dim i32)(local $total_length i32)(local $current_dim i32)(local $ind f64)
          (local $value_dim_ptr i32)(local $value_dim_index i32)(local $value_length i32)(local $temp_val_arr i32)
        (;TODO(dherre3): Verify type of values arrays in verify_set_dimensions_and_values;)
        (set_local $len_arr (call $numel (get_local $arr_ptr)))
        (set_local $total_length (i32.const 0))
		(set_local $length_indices (call $numel (get_local $indices_ptr)))
        (set_local $dim_ptr (call $create_mxvector (get_local $length_indices)(i32.const 0)
                                (i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)))
        (set_local $value_dim_ptr (call $size (get_local $values_ptr)(i32.const 0)))
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

                    (tee_local $length_dim (call $numel (get_local $current_dim)))

                    i32.const 1
                    i32.gt_s
                    get_local $length_indices
                    i32.const 1
                    i32.gt_s
                    i32.and
                    if
                        (call $get_array_index_f64 (get_local $value_dim_ptr)(get_local $value_dim_index))
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
        (tee_local $value_length (call $numel (get_local $values_ptr)))
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
                    (call $create_mxvector (i32.const 2)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
                    )
                (call $set_array_index_f64 (get_local $temp_val_arr)(i32.const 1)(f64.const 1))
                (call $set_array_index_f64 (get_local $temp_val_arr)(i32.const 2)(f64.convert_s/i32 (get_local $total_length)))
                (call $create_mxarray_with_initial_value (get_local $temp_val_arr)
                    (call $get_array_index_f64 (get_local $values_ptr)(i32.const 1)))
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
    ;; (func $equals_f64)
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
        call $numel
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
        (local $new_length_column f64)(local $i i32)(local $j i32)
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
        call $numel
        tee_local $length_input
        i32.eqz
        i32.or
        if
            (call $create_mxarray_empty (i32.const 2)
                (i32.const 0)(i32.const 0)(i32.const 0))
            return
        end
        (set_local $first_array_ptr
            (call $get_array_index_i32 (get_local $input_arrays)(i32.const 1)))
        (set_local $shape_first
            (call $size (get_local $first_array_ptr)(i32.const 0)))
        (tee_local $shape_len
            (call $numel (get_local $shape_first)))
        get_local $dim
        i32.lt_s
        if
            i32.const 13
            call $throwError
        end
        (set_local $new_length_column
            (call $get_array_index_f64 (get_local $shape_first)(get_local $dim)))
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
                    (call $size (get_local $temp_mat_ptr)(i32.const 0)))
                call $numel
                get_local $shape_len
                i32.ne
                ;; Throw error when they do not have the same number of dimensions
                if
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
            (i32.const 0)
        )
        return
    )
    (export "concat" (func $concat))
    (func $concat (param $concat_dim i32)(param $input_matrices i32)(result i32)
        (local $res_arr i32)
        (tee_local $res_arr  (call $verify_input_and_instantiate_result_concatation (get_local $concat_dim)(get_local $input_matrices)))
        call $numel
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
        (set_local $concat_dim_length (i32.trunc_s/f64 (call $get_array_index_f64  (call $size (get_local $result_matrix_ptr)(i32.const 0))(get_local $concat_dim))))
        (set_local $length_input (call $numel (get_local $input_matrices)))
        (set_local $i (i32.const 1))
        loop
            block
            (br_if 0 (i32.gt_s (get_local $i)(get_local $length_input)))
                (set_local $curr_mat_ptr (call $get_array_index_i32 (get_local $input_matrices)(get_local $i)))
                (set_local $curr_mat_shape_ptr (call $size (get_local $curr_mat_ptr)(i32.const 0)))
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
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1
            end
        end
    )
    (export "COLON_TOKEN" (func $colon_token))
    (func $colon_token (result i32)
        (local $arr_ptr i32)
        i32.const 4
        call $malloc
        tee_local $arr_ptr
        get_global $COLON_TOKEN
        i32.store
        get_local $arr_ptr
    )
    (func $traverse_concat (param $concat_dim i32)(param $total_ptr i32)(param $total_new_dim_size i32)(param $mat_ptr i32)
        (; Traverses a current matrix completely and sets the right indices on the result matrix;)
        (param $mat_shape_ptr i32)(param $size_prev i32)(param $curr_dim i32)(param $offset i32)
        (param $mult i32)(param $offset_tot i32)(param $mult_tot i32)
        (local $i i32)(local $shape_dim_len i32)(local $shape_len i32)(local $new_offset i32)(local $new_mult i32)
        (local $new_offset_tot i32)(local $new_mult_tot i32)(local $i_index i32)

        (set_local $shape_dim_len (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $mat_shape_ptr)(get_local $curr_dim))))
        (set_local $shape_len (call $numel (get_local $mat_shape_ptr)))
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
    (export "eye_S" (func $eye_S))
    (func $eye_S (result f64)
        f64.const 1
    )
    (export "eye_M" (func $eye_M))
    (func $eye_M (param $dim_ptr i32) (result i32)
        (local $i i32)(local $n i32)(local $m i32)
        (local $out_ptr i32)(local $dim_len i32)
        get_local $dim_ptr
        call $is_null
        if
            ;; TODO (dherre3): Create a 1x1 vector even though that's not how we represent scalars
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
        (tee_local $dim_len (call $mxarray_core_get_array_length (get_local $dim_ptr)))
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

        (tee_local $n (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $dim_ptr)(i32.const 1))))
        i32.const 0
        i32.le_s
        if
            (set_local $n (i32.const 0))
            (call $set_array_index_f64 (get_local $dim_ptr)(i32.const 1)(f64.const 0))
        end
        get_local $dim_len
        i32.const 2
        i32.eq
        if  (result i32)
            (tee_local $m (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $dim_ptr)(i32.const 2))))
        else
            (tee_local $m (get_local $n))
        end
        i32.const 0
        i32.le_s
        if
            (set_local $m (i32.const 0))
            (call $set_array_index_f64 (get_local $dim_ptr)(i32.const 2)(f64.const 0))
        end
        (tee_local $out_ptr (call $zeros (get_local $dim_ptr)))
        call $numel
        i32.const 1
        i32.ge_s
        if
            loop
                block
                (br_if 0 (i32.ge_s (get_local $i)(get_local $n)))
                    (call $set_array_index_f64 (get_local $out_ptr)
                        (i32.add (i32.const 1)(i32.add (get_local $i)(i32.mul (get_local $i)(get_local $n))))
                        (f64.const 1)
                    )
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
                br 1
                end
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
        (set_local $dim_ptr (call $size (get_local $arr_ptr)(i32.const 0)))
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
        (tee_local $out_ptr (call $create_mxarray_ND (get_local $dim_ptr)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)))

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


    ;; Array creation
     (export "zeros" (func $zeros))
    (func $zeros (param $size_ptr i32)(result i32)
        (local $arr_ptr i32)
        get_local $size_ptr
        i32.const 0
        i32.const 0
        i32.const 1
        i32.const 0
        call $create_mxarray_ND
        i32.const 0
        call $elementwise_constructor
    )
    (export "ones" (func $ones) )
    (func $ones (param $size_ptr i32)(result i32)
        (local $arr_ptr i32)
        get_local $size_ptr
        i32.const 0
        i32.const 0
        i32.const 1
        i32.const 8
        call $create_mxarray_ND
        i32.const 1
        call $elementwise_constructor
    )
    (export "rand" (func $rand) )
    (func $rand (param $size_ptr i32)(result i32)
        (local $arr_ptr i32)
        get_local $size_ptr
        i32.const 0
        i32.const 0
        i32.const 1
        i32.const 8
        call $create_mxarray_ND
        i32.const 2
        call $elementwise_constructor
    )
    (export "randn" (func $randn) )
    ( func $randn (param $size_ptr i32)(result i32)
        (local $arr_ptr i32)
        get_local $size_ptr
        i32.const 0
        i32.const 0
        i32.const 0
        i32.const 0
        call $create_mxarray_ND
        i32.const 3
        call $elementwise_constructor
    )
    (export "randn2" (func $randn2))
    (func $randn2 (param $size_ptr i32)(result i32)
        (local $len i32)(local $i i32)(local $arr_ptr i32)(local $arr_data_ptr i32)
        get_local $size_ptr
        i32.const 0
        i32.const 0
        i32.const 0
        i32.const 0
        call $create_mxarray_ND
        tee_local $arr_ptr
        (set_local $len (i32.load offset=4 align=4 (get_local $arr_ptr)))
        (set_local $arr_data_ptr (i32.load offset=8 align=4 (get_local $arr_ptr)))
        loop
            block
            (i32.ge_s (get_local $i)(get_local $len))
            br_if 0
                ;; get arr_ptr
                (i32.add (get_local $arr_data_ptr)
                    (i32.mul (get_local $i)(i32.const 8)))
                call $randn_s
                f64.store offset=0  align=8
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1
            end
        end
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
            i32.const 3
            call $throwError
        end
        get_local $arr_ptr
        i32.load offset=16 align=4
        get_local $i
        i32.const 1
        i32.sub
        i32.const 8
        i32.mul
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
    (export "size_M" (func $size_M))
        (func $size_M (param $arr_ptr i32)(result i32)
            get_local $arr_ptr
            i32.const 0
            call $size
        )
    (export "create_mxarray_with_initial_value" (func $create_mxarray_with_initial_value) )
    (func $create_mxarray_with_initial_value (param $size_ptr i32)(param $initial_value f64)(result i32)
        (local $arr_ptr i32)
        get_local $size_ptr
        i32.const 0
        i32.const 0
        i32.const 0
        i32.const 0
        call $create_mxarray_ND
        get_local $initial_value
        i32.const 35
        call $elementwise_constructor_one_input
    )
    (export "randi" (func $randi) )
    (func $randi (param $max f64)(param $size_ptr i32)(result i32)
        (local $arr_ptr i32)
        get_local $size_ptr
        i32.const 0
        i32.const 0
        i32.const 0
        i32.const 0
        call $create_mxarray_ND
        get_local $max
        i32.const 4
        call $elementwise_constructor_one_input
    )
    (export "get_array_stride" (func $get_array_stride))
    (func $get_array_stride (param $arr_ptr i32)(param $dim f64)(result f64)
        get_local $arr_ptr
        i32.load offset=16 align=4
        i32.const 8
        get_local $dim
        i32.trunc_s/f64
        i32.const 1
        i32.sub
        i32.mul
        i32.add
        f64.load
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
        (set_local $len (call $numel (get_local $arr_ptr)))
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
        (set_local $len (call $numel (get_local $arr_ptr)))
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
    ( func $elementwise_mapping (param $arr_ptr i32)(param $funct_ptr i32)(result i32)
        (local $len i32)(local $i i32)(local $new_arr_ptr i32)
        get_local $arr_ptr
        call $is_null
        i32.const 1
        i32.eq
        get_local $arr_ptr
        call $is_array
        i32.eqz
        i32.or
        if
            i32.const 6
            call $throwError
        end
        (set_local $new_arr_ptr (call $zeros (call $size (get_local $arr_ptr)(i32.const 0))))
        (set_local $len (call $numel (get_local $arr_ptr)))
        (set_local $i (i32.const 1))
        loop
            block
            (i32.gt_s (get_local $i)(get_local $len))
            br_if 0
                get_local $new_arr_ptr
                get_local $i
                (call_indirect (type $type_unary_op_f64)
                    (call $get_array_index_f64 (get_local $arr_ptr)(get_local $i))
                   (get_local $funct_ptr)
                )
                call $set_array_index_f64
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1
            end
        end
        get_local $new_arr_ptr
    )
    (func $elementwise_mapping_one_input (param $arr_ptr i32)(param $arg1 f64)(param $scalar_first i32)(param $funct_ptr i32)(result i32)
        (local $len i32)(local $i i32)(local $new_arr_ptr i32)
        get_local $arr_ptr
        call $is_null
        if
            i32.const 6
            call $throwError
        end
        (set_local $new_arr_ptr (call $zeros (call $size (get_local $arr_ptr)(i32.const 0))))
        (set_local $len (call $numel (get_local $arr_ptr)))
        (set_local $i (i32.const 1))
        loop
            block
            (i32.gt_s (get_local $i)(get_local $len))
            br_if 0
                ;; get arr_ptr
                get_local $new_arr_ptr
                get_local $i
                get_local $scalar_first
                if (result f64)
                    (call_indirect (type $type_binary_op_f64)
                        (get_local $arg1)
                        (call $get_array_index_f64 (get_local $arr_ptr)(get_local $i))
                        (get_local $funct_ptr)
                    )
                else
                    (call_indirect (type $type_binary_op_f64)
                        (call $get_array_index_f64 (get_local $arr_ptr)(get_local $i))
                        (get_local $arg1)
                        (get_local $funct_ptr)
                    )
                end
                call $set_array_index_f64
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br 1
            end
        end
        get_local $new_arr_ptr
    )

    (type $type_binary_op_f64 (func (param f64 f64)(result f64)))
    (type $type_binary_op_i32 (func (param f64 f64)(result i32)))

    (type $type_unary_op_f64 (func (param f64)(result f64)))
    (func $unary_map (param $out_ptr i32) (param $arr_ptr i32)(param $func_ptr i32)
        (local $len i32)(local $i i32)
        (set_local $len (call $numel (get_local $arr_ptr)))
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
    (elem $tab (i32.const 0) $zeros_s $ones_s $rand_s $randn_s $randi_s)
    (elem $tab (i32.const 5) $plus_SS $minus_SS $rem_SS $mod_SS $times_SS $rdivide_SS $power_SS)
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
        (call $print_array_f64 (call $mxarray_core_get_array_ptr (get_local $arr_ptr))(call $numel (get_local $arr_ptr)))
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
    (export "plus_MM" (func $plus_MM))
    (func $plus_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 5)))
    )
    (export "plus_SM" (func $plus_SM))
    (func $plus_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 5))
    )
     (export "plus_MS" (func $plus_MS))
    (func $plus_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 5))
    )


    (export "minus_SS" (func $minus_SS))
    (func $minus_SS (param f64 f64)(result f64)
        get_local 0
        get_local 1
        f64.sub
    )
    (export "minus_MM" (func $minus_MM))
    (func $minus_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 6)))
    )

    (export "minus_SM" (func $minus_SM))
    (func $minus_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 6))
    )
    (export "minus_MS" (func $minus_MS))
    (func $minus_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 6))
    )
    (export "times_MS" (func $times_MS))
    (func $times_MS (param $m1_ptr i32)(param $scalar1 f64) (result i32)
        (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 0)(i32.const 9)))
    )
    (export "times_SM" (func $times_SM))
    (func $times_SM (param $scalar1 f64) (param $m1_ptr i32)(result i32)
        (return (call $elementwise_mapping_one_input (get_local $m1_ptr)(get_local $scalar1)(i32.const 1)(i32.const 9)))
    )
    (export "times_MM" (func $times_MM))
    (func $times_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 9)))
    )
    (export "rem_SM" (func $rem_SM))
    (func $rem_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 7))
    )
    (export "rem_MS" (func $rem_MS))
    (func $rem_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 7))
    )
    (export "rem_MM" (func $rem_MM))
    (func $rem_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 7)))
    )
    (export "mod_SM" (func $mod_SM))
    (func $mod_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 8))
    )
    (export "mod_MS" (func $mod_MS))
    (func $mod_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 8))
    )
    (export "mod_MM" (func $mod_MM))
    (func $mod_MM (param $m1_ptr i32)(param $m2_ptr i32)(result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 8)))
    )

    (export "rdivide_SM" (func $rdivide_SM))
    (func $rdivide_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 10))
    )
    (export "rdivide_MS" (func $rdivide_MS))
    (func $rdivide_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 10))
    )
    (export "rdivide_MM" (func $rdivide_MM))
    (func $rdivide_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 10)))
    )

   (export "ldivide_SM" (func $ldivide_SM))
    (func $ldivide_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 10))
    )
    (export "ldivide_MS" (func $ldivide_MS))
    (func $ldivide_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 10))
    )
     (export "ldivide_MM" (func $ldivide_MM))
    (func $ldivide_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m2_ptr)(get_local $m1_ptr)(i32.const 10)))
    )


   (export "power_SM" (func $power_SM))
    (func $power_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 11))
    )
    (export "power_MS" (func $power_MS))
    (func $power_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 11))
    )
     (export "power_MM" (func $power_MM))
    (func $power_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 11)))
    )

       (export "le_SM" (func $le_SM))
    (func $le_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 12))
    )
    (export "le_MS" (func $le_MS))
    (func $le_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 12))
    )
     (export "le_MM" (func $le_MM))
    (func $le_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 12)))
    )

       (export "lt_SM" (func $lt_SM))
    (func $lt_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 13))
    )
    (export "lt_MS" (func $lt_MS))
    (func $lt_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 13))
    )
     (export "lt_MM" (func $lt_MM))
    (func $lt_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 13)))
    )

    (export "ge_SM" (func $ge_SM))
    (func $ge_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 14))
    )
    (export "ge_MS" (func $ge_MS))
    (func $ge_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 14))
    )
     (export "ge_MM" (func $ge_MM))
    (func $ge_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 14)))
    )
    (export "gt_SM" (func $gt_SM))
    (func $gt_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 15))
    )
    (export "gt_MS" (func $gt_MS))
    (func $gt_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 15))
    )
     (export "gt_MM" (func $gt_MM))
    (func $gt_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 15)))
    )
    (export "eq_SM" (func $eq_SM))
    (func $eq_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 16))
    )
    (export "eq_MS" (func $eq_MS))
    (func $eq_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 16))
    )
     (export "eq_MM" (func $eq_MM))
    (func $eq_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 16)))
    )
    (export "and_SM" (func $and_SM))
    (func $and_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 17))
    )
    (export "and_MS" (func $and_MS))
    (func $and_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 17))
    )
    (export "and_MM" (func $and_MM))
    (func $and_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 17)))
    )
    (export "or_SM" (func $or_SM))
    (func $or_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 18))
    )
    (export "or_MS" (func $or_MS))
    (func $or_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 18))
    )
    (export "or_MM" (func $or_MM))
    (func $or_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 18)))
    )

    (export "ne_SM" (func $ne_SM))
    (func $ne_SM (param $x f64)(param $arr_ptr i32) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 1)(i32.const 19))
    )
    (export "ne_MS" (func $ne_MS))
    (func $ne_MS (param $arr_ptr i32)(param $x f64) (result i32)
        (call $elementwise_mapping_one_input (get_local $arr_ptr)(get_local $x)(i32.const 0)(i32.const 19))
    )
    (export "ne_MM" (func $ne_MM))
    (func $ne_MM (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
        (return (call $pairwise (get_local $m1_ptr)(get_local $m2_ptr)(i32.const 19)))
    )
    (func $pairwise (param $m1_ptr i32)(param $m2_ptr i32)(param $func_ptr i32)(result i32)
        (local $res_ptr i32)
        (call $verify_pairwise (get_local $m1_ptr)(get_local $m2_ptr))
        (tee_local $res_ptr)
        call $numel
        i32.const 0
        i32.gt_s
        if
            (call $traverse_pairwise
                (get_local $res_ptr)(call $size (get_local $res_ptr)(i32.const 0))
                (get_local $m1_ptr)(call $size (get_local $m1_ptr)(i32.const 0))
                (get_local $m2_ptr)(call $size (get_local $m2_ptr)(i32.const 0))
                (get_local $func_ptr)(i32.const 1)
                (i32.const 0)(i32.const 1)
                (i32.const 0)(i32.const 1)
                (i32.const 0)(i32.const 1))
        end
        get_local $res_ptr
        return
    )
    (export "verify_pairwise" (func $verify_pairwise))
    (func $verify_pairwise (param $m1_ptr i32)(param $m2_ptr i32) (result i32)
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
        (set_local $ashape_ptr (call $size (get_local $m1_ptr)(i32.const 0)))
        (set_local $bshape_ptr (call $size (get_local $m2_ptr)(i32.const 0)))
        (set_local $len_a (call $numel (get_local $ashape_ptr)))
        (set_local $len_b (call $numel (get_local $bshape_ptr)))

        ;; set total numDim
        (set_local $numDim
            (select (get_local $len_a)(get_local $len_b)
                (i32.gt_s (get_local $len_a)(get_local $len_b))))
        (set_local $new_shape_ptr
            (call $create_mxvector (get_local $numDim)
                (i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)))
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
        (call $create_mxarray_ND (get_local $new_shape_ptr) (i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0))
    )
    (func $traverse_pairwise (param $total_ptr i32)
        (param $total_shape_ptr i32)(param $a_ptr i32)(param $a_shape_ptr i32)
        (param $b_ptr i32)(param $b_shape_ptr i32)(param $func_ptr i32)
        (param $curr_dim i32)(param $offset_tot i32)(param $mult_tot i32)
        (param $offset_a i32)(param $mult_a i32)(param $offset_b i32)(param $mult_b i32)
        ;; (param $res_type i32)
        (local $len_dim i32)(local $i i32)(local $new_offset_tot i32)
        (local $new_mult_tot i32)(local $new_offset_a i32)
        (local $new_mult_a i32)(local $new_offset_b i32)
        (local $new_mult_b i32)(local $bshape_dim i32)
        (local $ashape_dim i32)(local $total_dim_num i32)
        (set_local $len_dim
            (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $total_shape_ptr)
                (get_local $curr_dim))))

        (set_local $total_dim_num (call $numel (get_local $total_shape_ptr)))
        loop
            block
            (br_if 0 (i32.ge_s (get_local $i)(get_local $len_dim)))
            ;; Total calculation
            (set_local $new_offset_tot
                (i32.add (get_local $offset_tot)
                         (i32.mul (get_local $i)(get_local $mult_tot))))

            (set_local $new_mult_tot
                (i32.mul (get_local $mult_tot)
                    (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $total_shape_ptr)
                        (get_local $curr_dim)))))

            ;; A Calculation
            (set_local $ashape_dim (i32.trunc_s/f64
                (call $get_array_index_f64 (get_local $a_shape_ptr)
                    (get_local $curr_dim))))
            get_local $curr_dim
            get_local $a_shape_ptr
            call $numel
            i32.gt_s
            get_local $i
            get_local $ashape_dim
            i32.const 1
            i32.sub
            i32.gt_s
            i32.or
            if
                (set_local $new_offset_a (get_local $offset_a))
                (set_local $new_mult_a (get_local $mult_a))
            else
                (set_local $new_offset_a
                                (i32.add (get_local $offset_a)
                                        (i32.mul (get_local $i)(get_local $mult_a))))
                (set_local $new_mult_a
                    (i32.mul (get_local $mult_a)
                       (get_local $ashape_dim)))
            end

            ;; B calculation
             (set_local $bshape_dim (i32.trunc_s/f64
                (call $get_array_index_f64 (get_local $b_shape_ptr)
                    (get_local $curr_dim))))
            get_local $curr_dim
            get_local $b_shape_ptr
            call $numel
            i32.gt_s
            get_local $i
            get_local $bshape_dim
            i32.const 1
            i32.sub
            i32.gt_s
            i32.or
            if
                (set_local $new_offset_b (get_local $offset_b))
                (set_local $new_mult_b (get_local $mult_b))
            else
                (set_local $new_offset_b
                                (i32.add (get_local $offset_b)
                                        (i32.mul (get_local $i)(get_local $mult_b))))
                (set_local $new_mult_b
                    (i32.mul (get_local $mult_b)
                       (get_local $bshape_dim)))
            end

            get_local $curr_dim
            get_local $total_dim_num
            i32.eq
            if
                ;; get_local $res_type
                ;; i32.eqz
                ;; if
                ( call $set_array_index_f64
                    (get_local $total_ptr)
                    (i32.add (i32.const 1)(get_local $new_offset_tot))
                    (call_indirect (type $type_binary_op_f64)
                        (call $get_array_index_f64 (get_local $a_ptr)
                            (i32.add (i32.const 1)(get_local $new_offset_a)))
                        (call $get_array_index_f64 (get_local $b_ptr)
                            (i32.add (i32.const 1)(get_local $new_offset_b)))
                        (get_local $func_ptr)))
                ;; else
                ;;     ( call $set_array_index_f64
                ;;         (get_local $total_ptr)
                ;;         (i32.add (i32.const 1)(get_local $new_offset_tot))
                ;;         (f64.convert_s/i32 (call_indirect (type $type_binary_op_i32)
                ;;             (call $get_array_index_f64 (get_local $a_ptr)
                ;;                 (i32.add (i32.const 1)(get_local $new_offset_a)))
                ;;             (call $get_array_index_f64 (get_local $b_ptr)
                ;;                 (i32.add (i32.const 1)(get_local $new_offset_b)))
                ;;             (get_local $func_ptr))))
                ;; end
            else
                (call $traverse_pairwise
                    (get_local $total_ptr)(get_local $total_shape_ptr)
                    (get_local $a_ptr)(get_local $a_shape_ptr)
                    (get_local $b_ptr)(get_local $b_shape_ptr)
                    (get_local $func_ptr)
                    (i32.add (get_local $curr_dim)(i32.const 1))
                    (get_local $new_offset_tot)(get_local $new_mult_tot)
                    (get_local $new_offset_a)(get_local $new_mult_a)
                    (get_local $new_offset_b)(get_local $new_mult_b))
            end
            (set_local $i (i32.add (i32.const 1)(get_local $i)))
            br 1
            end
        end
    )


    (func $is_array (param $arr_ptr i32)(result i32)
        get_local $arr_ptr
        call $is_null
        if
            i32.const 0
            return
        end
        get_local $arr_ptr
        call $mxarray_core_get_mclass
        i32.const 0
        i32.ne
        if
            i32.const 0
            return
        end
        i32.const 1
    )
    (func $findNonSingletonDimension (param $size_ptr i32)(result i32)
        (local $i i32)(local $len i32)(local $curr_dim f64)
        (set_local $len (call $numel (get_local $size_ptr)))
        (set_local $i (i32.const 1))
        loop
            block
                (br_if 0 (i32.gt_s (get_local $i)(get_local $len)))
                (tee_local $curr_dim (call $get_array_index_f64 (get_local $size_ptr)(get_local $i)))
                f64.const 1
                f64.gt
                if
                    get_local $i
                    return
                end
            (set_local $i (i32.add (i32.const 1)(get_local $i)))
            br 1
            end
        end
        i32.const -1
    )
    (func $reduction (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(param $init_to i32)(param $binary_op_flag i32)(param $func_ptr i32)(result i32)
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
        (set_local $size_new_arr (call $size (get_local $arr_ptr)(i32.const 0)))
        get_local $dim
        i32.eqz
        if
            (set_local $dim (call $findNonSingletonDimension (get_local $size_new_arr)))
        end

        ;; (i32.gt_s (get_local $dim)(call $numel (get_local $size_new_arr)))
        ;; call $printDouble
        ;; drop
        (i32.or
            (i32.gt_s (get_local $dim)(call $numel (get_local $size_new_arr)))
            (i32.eq (i32.const -1)(get_local $dim)))
        i32.eqz
        if (result i32)
            i32.const 0
            ;; (f64.eq (call $get_array_index_f64 (get_local $size_new_arr)(get_local $dim))(f64.const 1))
        else
            i32.const 1
        end
        ;; call $printDouble
        if (result i32)
            get_local $arr_ptr
            call $clone
        else
            (set_local $size_arr
                (call $clone (get_local $size_new_arr)))


            (call $set_array_index_f64
                (get_local $size_new_arr)
                (get_local $dim)(f64.const 1))
            get_local $init_to
            i32.eqz
            if (result i32)
                ;; Initialize to zeros or ones
                (tee_local $new_arr_ptr
                    (call $zeros (get_local $size_new_arr)))
            else
                (tee_local $new_arr_ptr
                (call $ones (get_local $size_new_arr)))
            end
            (call $traverse_reduction
                (get_local $new_arr_ptr)(get_local $arr_ptr)
                (get_local $size_arr)(get_local $dim)(get_local $nanFlag)
                (get_local $func_ptr)(get_local $binary_op_flag)
                (i32.const 1)(i32.const 0)(i32.const 1)(i32.const 0)(i32.const 1))
        end
    )
    (export "sum_S" (func $identity_f64))
    (func $identity_f64 (param $arg f64) (result f64)
        get_local $arg
    )
    (export "sum_M" (func $sum_M))
    (func $sum_M (param $arr_ptr i32) (result i32)
        get_local $arr_ptr
        i32.const 0
        i32.const 0
        call $sum
    )
    (export "sum_MS" (func $sum_MS))
    (func $sum_MS (param  i32 f64) (result i32)
        get_local 0
        get_local 1
        i32.trunc_s/f64
        i32.const 0
        call $sum
    )
    (export "mean_MSS" (func $mean_MSS))
    (func $mean_MSS (param $arr_ptr i32)(param $dim f64)(param $nanFlag f64)(result i32)
        get_local $arr_ptr
        get_local $dim
        i32.trunc_s/f64
        get_local $nanFlag
        i32.trunc_s/f64
        call $mean
    )
    (export "mean_MS" (func $mean_MS))
    (func $mean_MS (param $arr_ptr i32)(param $dim f64)(result i32)
        get_local $arr_ptr
        get_local $dim
        i32.trunc_s/f64
        i32.const 0
        call $mean
    )
    (export "mean_M" (func $mean_M))
    (func $mean_M (param $arr_ptr i32)(result i32)
        get_local $arr_ptr
        i32.const 0
        i32.const 0
        call $mean
    )
    (export "mean" (func $mean))
    (func $mean (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(result i32)
        (call $sum (get_local $arr_ptr)(get_local $dim)(get_local $nanFlag))
        get_local $dim
        i32.eqz
        if (result f64)
            (call $array_dim_non_singleton (get_local $arr_ptr)) ;; Get first-non-singleton
        else
            (call $size_dim (get_local $arr_ptr)(get_local $dim)) ;; Get length of given dimension
        end
        call $rdivide_MS
    )
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
    (func $sum (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(result i32)
        (call $reduction
            (get_local $arr_ptr)(get_local $dim)
            (get_local $nanFlag)(i32.const 0)(i32.const 1)(i32.const 5))
    )


    (export "any" (func $any))
    (func $any (param $arr_ptr i32)(param $dim i32)(result i32)
        (call $reduction
            (get_local $arr_ptr)(get_local $dim)
            (i32.const 1)(i32.const 0)(i32.const 1)(i32.const 18))
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
        (local $i i32)
        loop $start
            block $end
            (br_if $end (i32.ge_s (get_local $i)(call $mxarray_core_get_array_length (get_local $arr_ptr))))
                get_local $arr_ptr
                get_local $i
                call $get_array_index_f64_no_check
                i32.trunc_s/f64
                i32.eqz
                if
                    i32.const 0
                    return
                end
            (set_local $i (i32.add (get_local $i)(i32.const 1)))
            br $start
            end $end
        end
        i32.const 1
    )
    (export "all" (func $all))
    (func $all (param $arr_ptr i32)(param $dim i32)(result i32)
        (call $reduction
            (get_local $arr_ptr)(get_local $dim)
            (i32.const 1)(i32.const 1)(i32.const 1)(i32.const 17))
    )

    (export "prod" (func $prod))
    (func $prod (param $arr_ptr i32)(param $dim i32)(param $nanFlag i32)(result i32)
        (call $reduction
            (get_local $arr_ptr)(get_local $dim)
            (get_local $nanFlag)(i32.const 1)(i32.const 1)(i32.const 9))
    )
    ;; MATJUICE
    (export "convert_scalar_to_mxarray" (func $convert_scalar_to_mxarray))
    (func $convert_scalar_to_mxarray (param $scalar f64) (result i32)
        (local $out_ptr i32)
        (tee_local $out_ptr
            (call $create_mxvector (i32.const 1)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)))
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
        (set_local $shape_len (call $numel (get_local $mat_shape_ptr)))
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
                        (get_local $func_ptr)
                        (get_local $binary_op_flag)
                        (i32.add (get_local $curr_dim)(i32.const 1))
                        (get_local $new_offset)(get_local $new_mult)(get_local $new_offset_tot)(get_local $new_mult_tot)
                    )
                end
                (set_local $i (i32.add (get_local $i)(i32.const 1)))
                br 1
            end
        end
    )
    (export "mtimes_SM" (func $times_SM))
    (export "mtimes_MS" (func $times_MS))
    (export "mtimes_MM" (func $mtimes_MM))
    (func $mtimes_MM (param $m1_ptr i32)(param $m2_ptr i32)(result i32)
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
        (set_local $m1_shape_ptr (call $size (get_local $m1_ptr)(i32.const 0)))
        (set_local $m2_shape_ptr (call $size (get_local $m2_ptr)(i32.const 0)))
        (set_local $rows_m1 (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $m1_shape_ptr)(i32.const 1))))
        (set_local $cols_m2 (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $m2_shape_ptr)(i32.const 2))))
        (tee_local $rows_m2 (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $m2_shape_ptr)(i32.const 1))))
        (tee_local $cols_m1 (i32.trunc_s/f64 (call $get_array_index_f64 (get_local $m1_shape_ptr)(i32.const 2))))
        i32.ne
        if
            i32.const 18
            call $throwError
        end
        (set_local $size_out_ptr (call $create_mxvector (i32.const 2)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)(i32.const 0)))
        (call $set_array_index_f64 (get_local $size_out_ptr)(i32.const 1)(f64.convert_s/i32 (get_local $rows_m1)))
        (call $set_array_index_f64 (get_local $size_out_ptr)(i32.const 2)(f64.convert_s/i32 (get_local $cols_m2)))
        (set_local $out_ptr (call $zeros (get_local $size_out_ptr)))
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
                                    (i32.add (i32.const 1)
                                        (i32.add (get_local $i)
                                            (i32.mul (get_local $rows_m1)
                                                    (get_local $k)))))
                                (call $get_array_index_f64_no_check (get_local $m2_ptr)
                                    (i32.add (i32.const 1)
                                        (i32.add  (get_local $k)
                                            (i32.mul (get_local $rows_m2)
                                            (get_local $j)))))
                        )))
                        (set_local $k (i32.add (i32.const 1)(get_local $k)))
                    br 1
                    end
                    end
                    (call $set_array_index_f64_no_check
                        (get_local $out_ptr)
                        (i32.add (i32.const 1)
                            (i32.add (get_local $i)
                                        (i32.mul (get_local $rows_m1)
                                            (get_local $j))))
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
    (func $round_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 20))
    )
    (export "ceil_S" (func $ceil_S))
    (func $ceil_S (param f64) (result f64)
        get_local 0
        f64.ceil
    )
    (export "ceil_M" (func $ceil_M))
    (func $ceil_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 21))
    )
    (export "sqrt_S" (func $sqrt_S))
    (func $sqrt_S (param f64) (result f64)
        get_local 0
        f64.sqrt
    )
    (export "sqrt_M" (func $sqrt_M))
    (func $sqrt_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 22))
    )
    (export "uminus_S" (func $uminus_S))
    (func $uminus_S (param f64) (result f64)
        get_local 0
        f64.const -1
        f64.mul
    )
    (export "uminus_M" (func $uminus_M))
    (func $uminus_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 23))
    )
      (export "uplus_S" (func $uplus_S))
    (func $uplus_S (param f64) (result f64)
        get_local 0
        f64.const -1
        f64.mul
    )
    (export "uplus_M" (func $uplus_M))
    (func $uplus_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 24))
    )
    (export "abs_S" (func $abs_S))
    (func $abs_S (param f64) (result f64)
        get_local 0
        f64.abs
    )
    (export "abs_M" (func $abs_M))
    (func $abs_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 25))
    )

    (export "not_S" (func $not_S))
    (func $not_S (param f64) (result f64)
        get_local 0
        f64.const 0
        f64.eq
        f64.convert_s/i32
    )
    (export "not_M" (func $not_M))
    (func $not_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 26))
    )
    (export "fix_S" (func $fix_S))
    (func $fix_S (param f64) (result f64)
        get_local 0
        f64.trunc
    )
    (export "fix_M" (func $fix_M))
    (func $fix_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 27))
    )
    (export "sin_M" (func $sin_M))
    (func $sin_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 28))
    )
    (export "cos_M" (func $cos_M))
    (func $cos_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 29))
    )
    (export "tan_M" (func $tan_M))
    (func $tan_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 30))
    )
    (export "exp_M" (func $exp_M))
    (func $exp_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 31))
    )
    (export "log_M" (func $log_M))
    (func $log_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 32))
    )
    (export "log10_M" (func $log10_M))
    (func $log10_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 33))
    )
    (export "log2_M" (func $log2_M))
    (func $log2_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 34))
    )
     (export "floor_M" (func $floor_M))
    (func $floor_M (param $arr_ptr i32) (result i32)
        (call $elementwise_mapping (get_local $arr_ptr)(i32.const 35))
    )
	(func $fft_four1_MSS (param $data_i32 i32)(param $n_f64 f64)(param $isign_f64 f64)(result i32)
		(local $mc_t83_f64 f64)(local $mc_t58_f64 f64)(local $mc_t40_f64 f64)(local $mc_t15_f64 f64)(local $mc_t24_f64 f64)(local $mc_t67_f64 f64)(local $mmax_f64 f64)(local $wpr_f64 f64)(local $tempi_f64 f64)(local $result_i32 i32)(local $mc_t31_f64 f64)(local $mc_t59_f64 f64)(local $wpi_f64 f64)(local $mc_t74_f64 f64)(local $mc_t76_f64 f64)(local $mc_t57_f64 f64)(local $mc_t41_f64 f64)(local $mc_t14_f64 f64)(local $mc_t66_f64 f64)(local $mc_t84_f64 f64)(local $mc_t32_f64 f64)(local $mc_t23_f64 f64)(local $mc_t75_f64 f64)(local $mc_t49_f64 f64)(local $istep_f64 f64)(local $mc_t51_f64 f64)(local $mc_t64_f64 f64)(local $mc_t81_f64 f64)(local $mc_t48_f64 f64)(local $mc_t18_f64 f64)(local $mc_t78_f64 f64)(local $mc_t50_f64 f64)(local $mc_t20_f64 f64)(local $mc_t22_f64 f64)(local $mc_t35_f64 f64)(local $mc_t33_f64 f64)(local $mc_t82_f64 f64)(local $mc_t52_f64 f64)(local $wi_f64 f64)(local $mc_t46_f64 f64)(local $mc_t65_f64 f64)(local $mc_t16_f64 f64)(local $wtemp_f64 f64)(local $mc_t29_f64 f64)(local $mc_t17_f64 f64)(local $i_f64 f64)(local $mc_t77_f64 f64)(local $mc_t47_f64 f64)(local $mc_t34_f64 f64)(local $mc_t21_f64 f64)(local $mc_t10_f64 f64)(local $mc_t70_f64 f64)(local $wr_f64 f64)(local $mc_t45_f64 f64)(local $theta_f64 f64)(local $mc_t28_f64 f64)(local $mc_t53_f64 f64)(local $mc_t61_f64 f64)(local $nn_f64 f64)(local $mc_t11_f64 f64)(local $j_f64 f64)(local $mc_t37_f64 f64)(local $mc_t87_f64 f64)(local $mc_t27_f64 f64)(local $mc_t71_f64 f64)(local $mc_t63_f64 f64)(local $mc_t19_f64 f64)(local $data_i32 i32)(local $mc_t44_f64 f64)(local $t_f64 f64)(local $mc_t80_f64 f64)(local $mc_t54_f64 f64)(local $mc_t36_f64 f64)(local $mc_t79_f64 f64)(local $mc_t62_f64 f64)(local $mc_t26_f64 f64)(local $mc_t39_f64 f64)(local $mc_t56_f64 f64)(local $mc_t13_f64 f64)(local $mc_t69_f64 f64)(local $mc_t85_f64 f64)(local $mc_t42_f64 f64)(local $mc_t72_f64 f64)(local $tempr_f64 f64)(local $mc_t38_f64 f64)(local $mc_t68_f64 f64)(local $mc_t55_f64 f64)(local $mc_t73_f64 f64)(local $mc_t12_f64 f64)(local $mc_t43_f64 f64)(local $mc_t25_f64 f64)(local $mc_t30_f64 f64)(local $mc_t86_f64 f64)(local $m_f64 f64)(local $mc_t60_f64 f64)(local $mc_t88_i32 i32)(local $mc_t92_i32 i32)(local $mc_t97_i32 i32)(local $mc_t104_i32 i32)(local $mc_t106_i32 i32)(local $mc_t108_i32 i32)(local $mc_t111_i32 i32)(local $mc_t114_i32 i32)(local $mc_t117_i32 i32)(local $mc_t119_i32 i32)(local $mc_t122_i32 i32)(local $mc_t125_i32 i32)(local $mc_t128_i32 i32)(local $mc_t130_i32 i32)(local $mc_t133_i32 i32)
		f64.const 2.0
		set_local $mc_t64_f64
		get_local $mc_t64_f64
		get_local $n_f64
		f64.mul
		set_local $nn_f64
		f64.const 2.0
		set_local $j_f64
		f64.const 2.0
		set_local $mc_t73_f64
		f64.const 2.0
		set_local $mc_t74_f64
		get_local $mc_t88_i32
		i32.eqz
		if 
			block 
				block 
					block 
						block 
							get_local $mc_t73_f64
							get_local $mc_t74_f64
							get_local $nn_f64
							call $dynamic_loop_three
							br_table 0 1 2 
						end

						block $block_mc_t89
							get_local $mc_t73_f64
							set_local $i_f64
							get_local $j_f64
							get_local $i_f64
							f64.gt
							f64.convert_u/i32
							set_local $mc_t69_f64
							get_local $mc_t69_f64
							f64.const 0.0
							f64.eq
							i32.eqz
							if 
								f64.const 1.0
								set_local $mc_t65_f64
								get_local $j_f64
								get_local $mc_t65_f64
								f64.sub
								set_local $mc_t21_f64
								get_local $data_i32
								get_local $mc_t21_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $t_f64
								f64.const 1.0
								set_local $mc_t66_f64
								get_local $i_f64
								get_local $mc_t66_f64
								f64.sub
								set_local $mc_t22_f64
								get_local $data_i32
								get_local $mc_t22_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $mc_t10_f64
								f64.const 1.0
								set_local $mc_t67_f64
								get_local $j_f64
								get_local $mc_t67_f64
								f64.sub
								set_local $mc_t16_f64
								get_local $data_i32
								get_local $mc_t16_f64
								i32.trunc_s/f64
								get_local $mc_t10_f64
								call $set_array_index_f64
								f64.const 1.0
								set_local $mc_t68_f64
								get_local $i_f64
								get_local $mc_t68_f64
								f64.sub
								set_local $mc_t17_f64
								get_local $data_i32
								get_local $mc_t17_f64
								i32.trunc_s/f64
								get_local $t_f64
								call $set_array_index_f64
								get_local $data_i32
								get_local $j_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $t_f64
								get_local $data_i32
								get_local $i_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $mc_t11_f64
								get_local $data_i32
								get_local $j_f64
								i32.trunc_s/f64
								get_local $mc_t11_f64
								call $set_array_index_f64
								get_local $data_i32
								get_local $i_f64
								i32.trunc_s/f64
								get_local $t_f64
								call $set_array_index_f64
							end

							get_local $n_f64
							set_local $m_f64
							f64.const 2.0
							set_local $mc_t70_f64
							get_local $m_f64
							get_local $mc_t70_f64
							f64.ge
							f64.convert_u/i32
							set_local $mc_t23_f64
							get_local $mc_t23_f64
							f64.const 0.0
							f64.eq
							i32.eqz
							if 
								get_local $j_f64
								get_local $m_f64
								f64.gt
								f64.convert_u/i32
								set_local $mc_t23_f64
							else
								f64.const 0.0
								set_local $mc_t23_f64
							end

							get_local $mc_t23_f64
							set_local $mc_t20_f64
							loop $loop_mc_t90
								block $block_mc_t91
									get_local $mc_t20_f64
									f64.const 0.0
									f64.eq
									br_if $block_mc_t91
									get_local $j_f64
									get_local $m_f64
									f64.sub
									set_local $j_f64
									f64.const 2.0
									set_local $mc_t71_f64
									get_local $m_f64
									get_local $mc_t71_f64
									f64.div
									set_local $m_f64
									f64.const 2.0
									set_local $mc_t72_f64
									get_local $m_f64
									get_local $mc_t72_f64
									f64.ge
									f64.convert_u/i32
									set_local $mc_t24_f64
									get_local $mc_t24_f64
									f64.const 0.0
									f64.eq
									i32.eqz
									if 
										get_local $j_f64
										get_local $m_f64
										f64.gt
										f64.convert_u/i32
										set_local $mc_t24_f64
									else
										f64.const 0.0
										set_local $mc_t24_f64
									end

									get_local $mc_t24_f64
									set_local $mc_t20_f64
									br $loop_mc_t90
								end

							end

							get_local $j_f64
							get_local $m_f64
							f64.add
							set_local $j_f64
						end

						br 2
					end

					loop $loop_mc_t93
						get_local $mc_t92_i32
						if 
							get_local $i_f64
							get_local $mc_t74_f64
							f64.add
							set_local $i_f64
						else
							i32.const 1
							set_local $mc_t92_i32
							get_local $mc_t73_f64
							set_local $i_f64
						end

						block $block_mc_t94
							get_local $i_f64
							get_local $nn_f64
							f64.lt
							br_if $block_mc_t94
							get_local $j_f64
							get_local $i_f64
							f64.gt
							f64.convert_u/i32
							set_local $mc_t69_f64
							get_local $mc_t69_f64
							f64.const 0.0
							f64.eq
							i32.eqz
							if 
								f64.const 1.0
								set_local $mc_t65_f64
								get_local $j_f64
								get_local $mc_t65_f64
								f64.sub
								set_local $mc_t21_f64
								get_local $data_i32
								get_local $mc_t21_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $t_f64
								f64.const 1.0
								set_local $mc_t66_f64
								get_local $i_f64
								get_local $mc_t66_f64
								f64.sub
								set_local $mc_t22_f64
								get_local $data_i32
								get_local $mc_t22_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $mc_t10_f64
								f64.const 1.0
								set_local $mc_t67_f64
								get_local $j_f64
								get_local $mc_t67_f64
								f64.sub
								set_local $mc_t16_f64
								get_local $data_i32
								get_local $mc_t16_f64
								i32.trunc_s/f64
								get_local $mc_t10_f64
								call $set_array_index_f64
								f64.const 1.0
								set_local $mc_t68_f64
								get_local $i_f64
								get_local $mc_t68_f64
								f64.sub
								set_local $mc_t17_f64
								get_local $data_i32
								get_local $mc_t17_f64
								i32.trunc_s/f64
								get_local $t_f64
								call $set_array_index_f64
								get_local $data_i32
								get_local $j_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $t_f64
								get_local $data_i32
								get_local $i_f64
								i32.trunc_s/f64
								call $get_array_index_f64
								set_local $mc_t11_f64
								get_local $data_i32
								get_local $j_f64
								i32.trunc_s/f64
								get_local $mc_t11_f64
								call $set_array_index_f64
								get_local $data_i32
								get_local $i_f64
								i32.trunc_s/f64
								get_local $t_f64
								call $set_array_index_f64
							end

							get_local $n_f64
							set_local $m_f64
							f64.const 2.0
							set_local $mc_t70_f64
							get_local $m_f64
							get_local $mc_t70_f64
							f64.ge
							f64.convert_u/i32
							set_local $mc_t23_f64
							get_local $mc_t23_f64
							f64.const 0.0
							f64.eq
							i32.eqz
							if 
								get_local $j_f64
								get_local $m_f64
								f64.gt
								f64.convert_u/i32
								set_local $mc_t23_f64
							else
								f64.const 0.0
								set_local $mc_t23_f64
							end

							get_local $mc_t23_f64
							set_local $mc_t20_f64
							loop $loop_mc_t95
								block $block_mc_t96
									get_local $mc_t20_f64
									f64.const 0.0
									f64.eq
									br_if $block_mc_t96
									get_local $j_f64
									get_local $m_f64
									f64.sub
									set_local $j_f64
									f64.const 2.0
									set_local $mc_t71_f64
									get_local $m_f64
									get_local $mc_t71_f64
									f64.div
									set_local $m_f64
									f64.const 2.0
									set_local $mc_t72_f64
									get_local $m_f64
									get_local $mc_t72_f64
									f64.ge
									f64.convert_u/i32
									set_local $mc_t24_f64
									get_local $mc_t24_f64
									f64.const 0.0
									f64.eq
									i32.eqz
									if 
										get_local $j_f64
										get_local $m_f64
										f64.gt
										f64.convert_u/i32
										set_local $mc_t24_f64
									else
										f64.const 0.0
										set_local $mc_t24_f64
									end

									get_local $mc_t24_f64
									set_local $mc_t20_f64
									br $loop_mc_t95
								end

							end

							get_local $j_f64
							get_local $m_f64
							f64.add
							set_local $j_f64
							br $loop_mc_t93
						end

					end

					i32.const 0
					set_local $mc_t92_i32
					br 1
				end

				loop $loop_mc_t98
					get_local $mc_t97_i32
					if 
						get_local $i_f64
						get_local $mc_t74_f64
						f64.add
						set_local $i_f64
					else
						i32.const 1
						set_local $mc_t97_i32
						get_local $mc_t73_f64
						set_local $i_f64
					end

					block $block_mc_t99
						get_local $i_f64
						get_local $nn_f64
						f64.gt
						br_if $block_mc_t99
						get_local $j_f64
						get_local $i_f64
						f64.gt
						f64.convert_u/i32
						set_local $mc_t69_f64
						get_local $mc_t69_f64
						f64.const 0.0
						f64.eq
						i32.eqz
						if 
							f64.const 1.0
							set_local $mc_t65_f64
							get_local $j_f64
							get_local $mc_t65_f64
							f64.sub
							set_local $mc_t21_f64
							get_local $data_i32
							get_local $mc_t21_f64
							i32.trunc_s/f64
							call $get_array_index_f64
							set_local $t_f64
							f64.const 1.0
							set_local $mc_t66_f64
							get_local $i_f64
							get_local $mc_t66_f64
							f64.sub
							set_local $mc_t22_f64
							get_local $data_i32
							get_local $mc_t22_f64
							i32.trunc_s/f64
							call $get_array_index_f64
							set_local $mc_t10_f64
							f64.const 1.0
							set_local $mc_t67_f64
							get_local $j_f64
							get_local $mc_t67_f64
							f64.sub
							set_local $mc_t16_f64
							get_local $data_i32
							get_local $mc_t16_f64
							i32.trunc_s/f64
							get_local $mc_t10_f64
							call $set_array_index_f64
							f64.const 1.0
							set_local $mc_t68_f64
							get_local $i_f64
							get_local $mc_t68_f64
							f64.sub
							set_local $mc_t17_f64
							get_local $data_i32
							get_local $mc_t17_f64
							i32.trunc_s/f64
							get_local $t_f64
							call $set_array_index_f64
							get_local $data_i32
							get_local $j_f64
							i32.trunc_s/f64
							call $get_array_index_f64
							set_local $t_f64
							get_local $data_i32
							get_local $i_f64
							i32.trunc_s/f64
							call $get_array_index_f64
							set_local $mc_t11_f64
							get_local $data_i32
							get_local $j_f64
							i32.trunc_s/f64
							get_local $mc_t11_f64
							call $set_array_index_f64
							get_local $data_i32
							get_local $i_f64
							i32.trunc_s/f64
							get_local $t_f64
							call $set_array_index_f64
						end

						get_local $n_f64
						set_local $m_f64
						f64.const 2.0
						set_local $mc_t70_f64
						get_local $m_f64
						get_local $mc_t70_f64
						f64.ge
						f64.convert_u/i32
						set_local $mc_t23_f64
						get_local $mc_t23_f64
						f64.const 0.0
						f64.eq
						i32.eqz
						if 
							get_local $j_f64
							get_local $m_f64
							f64.gt
							f64.convert_u/i32
							set_local $mc_t23_f64
						else
							f64.const 0.0
							set_local $mc_t23_f64
						end

						get_local $mc_t23_f64
						set_local $mc_t20_f64
						loop $loop_mc_t100
							block $block_mc_t101
								get_local $mc_t20_f64
								f64.const 0.0
								f64.eq
								br_if $block_mc_t101
								get_local $j_f64
								get_local $m_f64
								f64.sub
								set_local $j_f64
								f64.const 2.0
								set_local $mc_t71_f64
								get_local $m_f64
								get_local $mc_t71_f64
								f64.div
								set_local $m_f64
								f64.const 2.0
								set_local $mc_t72_f64
								get_local $m_f64
								get_local $mc_t72_f64
								f64.ge
								f64.convert_u/i32
								set_local $mc_t24_f64
								get_local $mc_t24_f64
								f64.const 0.0
								f64.eq
								i32.eqz
								if 
									get_local $j_f64
									get_local $m_f64
									f64.gt
									f64.convert_u/i32
									set_local $mc_t24_f64
								else
									f64.const 0.0
									set_local $mc_t24_f64
								end

								get_local $mc_t24_f64
								set_local $mc_t20_f64
								br $loop_mc_t100
							end

						end

						get_local $j_f64
						get_local $m_f64
						f64.add
						set_local $j_f64
						br $loop_mc_t98
					end

				end

				i32.const 0
				set_local $mc_t97_i32
				br 0
			end

		end

		f64.const 2.0
		set_local $mmax_f64
		get_local $nn_f64
		get_local $mmax_f64
		f64.gt
		f64.convert_u/i32
		set_local $mc_t87_f64
		loop $loop_mc_t102
			block $block_mc_t103
				get_local $mc_t87_f64
				f64.const 0.0
				f64.eq
				br_if $block_mc_t103
				f64.const 2.0
				set_local $mc_t75_f64
				get_local $mmax_f64
				get_local $mc_t75_f64
				f64.mul
				set_local $istep_f64
				get_local $isign_f64
				set_local $mc_t25_f64
				f64.const 6.28318530717959
				set_local $mc_t76_f64
				get_local $mc_t76_f64
				get_local $mmax_f64
				f64.div
				set_local $mc_t26_f64
				get_local $mc_t25_f64
				get_local $mc_t26_f64
				f64.mul
				set_local $theta_f64
				f64.const 0.5
				set_local $mc_t77_f64
				get_local $mc_t77_f64
				get_local $theta_f64
				f64.mul
				set_local $mc_t27_f64
				get_local $mc_t27_f64
				call $sin_S
				set_local $wtemp_f64
				f64.const 2.0
				set_local $mc_t78_f64
				get_local $mc_t78_f64
				f64.neg
				set_local $mc_t30_f64
				get_local $wtemp_f64
				set_local $mc_t31_f64
				get_local $mc_t30_f64
				get_local $mc_t31_f64
				f64.mul
				set_local $mc_t28_f64
				get_local $wtemp_f64
				set_local $mc_t29_f64
				get_local $mc_t28_f64
				get_local $mc_t29_f64
				f64.mul
				set_local $wpr_f64
				get_local $theta_f64
				call $sin_S
				set_local $wpi_f64
				f64.const 1.0
				set_local $wr_f64
				f64.const 0.0
				set_local $wi_f64
				f64.const 2.0
				set_local $mc_t85_f64
				f64.const 2.0
				set_local $mc_t86_f64
				get_local $mc_t104_i32
				i32.eqz
				if 
					block 
						block 
							block 
								block 
									get_local $mc_t85_f64
									get_local $mc_t86_f64
									get_local $mmax_f64
									call $dynamic_loop_three
									br_table 0 1 2 
								end

								block $block_mc_t105
									get_local $mc_t85_f64
									set_local $m_f64
									get_local $mc_t106_i32
									i32.eqz
									if 
										block 
											block 
												block 
													block 
														get_local $m_f64
														get_local $istep_f64
														get_local $nn_f64
														call $dynamic_loop_three
														br_table 0 1 2 
													end

													block $block_mc_t107
														get_local $m_f64
														set_local $i_f64
														get_local $i_f64
														get_local $mmax_f64
														f64.add
														set_local $j_f64
														get_local $wr_f64
														set_local $mc_t36_f64
														f64.const 1.0
														set_local $mc_t79_f64
														get_local $j_f64
														get_local $mc_t79_f64
														f64.sub
														set_local $mc_t38_f64
														get_local $data_i32
														get_local $mc_t38_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t37_f64
														get_local $mc_t36_f64
														get_local $mc_t37_f64
														f64.mul
														set_local $mc_t32_f64
														get_local $wi_f64
														set_local $mc_t34_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t35_f64
														get_local $mc_t34_f64
														get_local $mc_t35_f64
														f64.mul
														set_local $mc_t33_f64
														get_local $mc_t32_f64
														get_local $mc_t33_f64
														f64.sub
														set_local $tempr_f64
														get_local $wr_f64
														set_local $mc_t44_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t45_f64
														get_local $mc_t44_f64
														get_local $mc_t45_f64
														f64.mul
														set_local $mc_t39_f64
														get_local $wi_f64
														set_local $mc_t41_f64
														f64.const 1.0
														set_local $mc_t80_f64
														get_local $j_f64
														get_local $mc_t80_f64
														f64.sub
														set_local $mc_t43_f64
														get_local $data_i32
														get_local $mc_t43_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t42_f64
														get_local $mc_t41_f64
														get_local $mc_t42_f64
														f64.mul
														set_local $mc_t40_f64
														get_local $mc_t39_f64
														get_local $mc_t40_f64
														f64.add
														set_local $tempi_f64
														f64.const 1.0
														set_local $mc_t81_f64
														get_local $i_f64
														get_local $mc_t81_f64
														f64.sub
														set_local $mc_t48_f64
														get_local $data_i32
														get_local $mc_t48_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t46_f64
														get_local $tempr_f64
														set_local $mc_t47_f64
														get_local $mc_t46_f64
														get_local $mc_t47_f64
														f64.sub
														set_local $mc_t12_f64
														f64.const 1.0
														set_local $mc_t82_f64
														get_local $j_f64
														get_local $mc_t82_f64
														f64.sub
														set_local $mc_t18_f64
														get_local $data_i32
														get_local $mc_t18_f64
														i32.trunc_s/f64
														get_local $mc_t12_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t49_f64
														get_local $tempi_f64
														set_local $mc_t50_f64
														get_local $mc_t49_f64
														get_local $mc_t50_f64
														f64.sub
														set_local $mc_t13_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														get_local $mc_t13_f64
														call $set_array_index_f64
														f64.const 1.0
														set_local $mc_t83_f64
														get_local $i_f64
														get_local $mc_t83_f64
														f64.sub
														set_local $mc_t53_f64
														get_local $data_i32
														get_local $mc_t53_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t51_f64
														get_local $tempr_f64
														set_local $mc_t52_f64
														get_local $mc_t51_f64
														get_local $mc_t52_f64
														f64.add
														set_local $mc_t14_f64
														f64.const 1.0
														set_local $mc_t84_f64
														get_local $i_f64
														get_local $mc_t84_f64
														f64.sub
														set_local $mc_t19_f64
														get_local $data_i32
														get_local $mc_t19_f64
														i32.trunc_s/f64
														get_local $mc_t14_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t54_f64
														get_local $tempi_f64
														set_local $mc_t55_f64
														get_local $mc_t54_f64
														get_local $mc_t55_f64
														f64.add
														set_local $mc_t15_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														get_local $mc_t15_f64
														call $set_array_index_f64
													end

													br 2
												end

												loop $loop_mc_t109
													get_local $mc_t108_i32
													if 
														get_local $i_f64
														get_local $istep_f64
														f64.add
														set_local $i_f64
													else
														i32.const 1
														set_local $mc_t108_i32
														get_local $m_f64
														set_local $i_f64
													end

													block $block_mc_t110
														get_local $i_f64
														get_local $nn_f64
														f64.lt
														br_if $block_mc_t110
														get_local $i_f64
														get_local $mmax_f64
														f64.add
														set_local $j_f64
														get_local $wr_f64
														set_local $mc_t36_f64
														f64.const 1.0
														set_local $mc_t79_f64
														get_local $j_f64
														get_local $mc_t79_f64
														f64.sub
														set_local $mc_t38_f64
														get_local $data_i32
														get_local $mc_t38_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t37_f64
														get_local $mc_t36_f64
														get_local $mc_t37_f64
														f64.mul
														set_local $mc_t32_f64
														get_local $wi_f64
														set_local $mc_t34_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t35_f64
														get_local $mc_t34_f64
														get_local $mc_t35_f64
														f64.mul
														set_local $mc_t33_f64
														get_local $mc_t32_f64
														get_local $mc_t33_f64
														f64.sub
														set_local $tempr_f64
														get_local $wr_f64
														set_local $mc_t44_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t45_f64
														get_local $mc_t44_f64
														get_local $mc_t45_f64
														f64.mul
														set_local $mc_t39_f64
														get_local $wi_f64
														set_local $mc_t41_f64
														f64.const 1.0
														set_local $mc_t80_f64
														get_local $j_f64
														get_local $mc_t80_f64
														f64.sub
														set_local $mc_t43_f64
														get_local $data_i32
														get_local $mc_t43_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t42_f64
														get_local $mc_t41_f64
														get_local $mc_t42_f64
														f64.mul
														set_local $mc_t40_f64
														get_local $mc_t39_f64
														get_local $mc_t40_f64
														f64.add
														set_local $tempi_f64
														f64.const 1.0
														set_local $mc_t81_f64
														get_local $i_f64
														get_local $mc_t81_f64
														f64.sub
														set_local $mc_t48_f64
														get_local $data_i32
														get_local $mc_t48_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t46_f64
														get_local $tempr_f64
														set_local $mc_t47_f64
														get_local $mc_t46_f64
														get_local $mc_t47_f64
														f64.sub
														set_local $mc_t12_f64
														f64.const 1.0
														set_local $mc_t82_f64
														get_local $j_f64
														get_local $mc_t82_f64
														f64.sub
														set_local $mc_t18_f64
														get_local $data_i32
														get_local $mc_t18_f64
														i32.trunc_s/f64
														get_local $mc_t12_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t49_f64
														get_local $tempi_f64
														set_local $mc_t50_f64
														get_local $mc_t49_f64
														get_local $mc_t50_f64
														f64.sub
														set_local $mc_t13_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														get_local $mc_t13_f64
														call $set_array_index_f64
														f64.const 1.0
														set_local $mc_t83_f64
														get_local $i_f64
														get_local $mc_t83_f64
														f64.sub
														set_local $mc_t53_f64
														get_local $data_i32
														get_local $mc_t53_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t51_f64
														get_local $tempr_f64
														set_local $mc_t52_f64
														get_local $mc_t51_f64
														get_local $mc_t52_f64
														f64.add
														set_local $mc_t14_f64
														f64.const 1.0
														set_local $mc_t84_f64
														get_local $i_f64
														get_local $mc_t84_f64
														f64.sub
														set_local $mc_t19_f64
														get_local $data_i32
														get_local $mc_t19_f64
														i32.trunc_s/f64
														get_local $mc_t14_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t54_f64
														get_local $tempi_f64
														set_local $mc_t55_f64
														get_local $mc_t54_f64
														get_local $mc_t55_f64
														f64.add
														set_local $mc_t15_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														get_local $mc_t15_f64
														call $set_array_index_f64
														br $loop_mc_t109
													end

												end

												i32.const 0
												set_local $mc_t108_i32
												br 1
											end

											loop $loop_mc_t112
												get_local $mc_t111_i32
												if 
													get_local $i_f64
													get_local $istep_f64
													f64.add
													set_local $i_f64
												else
													i32.const 1
													set_local $mc_t111_i32
													get_local $m_f64
													set_local $i_f64
												end

												block $block_mc_t113
													get_local $i_f64
													get_local $nn_f64
													f64.gt
													br_if $block_mc_t113
													get_local $i_f64
													get_local $mmax_f64
													f64.add
													set_local $j_f64
													get_local $wr_f64
													set_local $mc_t36_f64
													f64.const 1.0
													set_local $mc_t79_f64
													get_local $j_f64
													get_local $mc_t79_f64
													f64.sub
													set_local $mc_t38_f64
													get_local $data_i32
													get_local $mc_t38_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t37_f64
													get_local $mc_t36_f64
													get_local $mc_t37_f64
													f64.mul
													set_local $mc_t32_f64
													get_local $wi_f64
													set_local $mc_t34_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t35_f64
													get_local $mc_t34_f64
													get_local $mc_t35_f64
													f64.mul
													set_local $mc_t33_f64
													get_local $mc_t32_f64
													get_local $mc_t33_f64
													f64.sub
													set_local $tempr_f64
													get_local $wr_f64
													set_local $mc_t44_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t45_f64
													get_local $mc_t44_f64
													get_local $mc_t45_f64
													f64.mul
													set_local $mc_t39_f64
													get_local $wi_f64
													set_local $mc_t41_f64
													f64.const 1.0
													set_local $mc_t80_f64
													get_local $j_f64
													get_local $mc_t80_f64
													f64.sub
													set_local $mc_t43_f64
													get_local $data_i32
													get_local $mc_t43_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t42_f64
													get_local $mc_t41_f64
													get_local $mc_t42_f64
													f64.mul
													set_local $mc_t40_f64
													get_local $mc_t39_f64
													get_local $mc_t40_f64
													f64.add
													set_local $tempi_f64
													f64.const 1.0
													set_local $mc_t81_f64
													get_local $i_f64
													get_local $mc_t81_f64
													f64.sub
													set_local $mc_t48_f64
													get_local $data_i32
													get_local $mc_t48_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t46_f64
													get_local $tempr_f64
													set_local $mc_t47_f64
													get_local $mc_t46_f64
													get_local $mc_t47_f64
													f64.sub
													set_local $mc_t12_f64
													f64.const 1.0
													set_local $mc_t82_f64
													get_local $j_f64
													get_local $mc_t82_f64
													f64.sub
													set_local $mc_t18_f64
													get_local $data_i32
													get_local $mc_t18_f64
													i32.trunc_s/f64
													get_local $mc_t12_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t49_f64
													get_local $tempi_f64
													set_local $mc_t50_f64
													get_local $mc_t49_f64
													get_local $mc_t50_f64
													f64.sub
													set_local $mc_t13_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													get_local $mc_t13_f64
													call $set_array_index_f64
													f64.const 1.0
													set_local $mc_t83_f64
													get_local $i_f64
													get_local $mc_t83_f64
													f64.sub
													set_local $mc_t53_f64
													get_local $data_i32
													get_local $mc_t53_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t51_f64
													get_local $tempr_f64
													set_local $mc_t52_f64
													get_local $mc_t51_f64
													get_local $mc_t52_f64
													f64.add
													set_local $mc_t14_f64
													f64.const 1.0
													set_local $mc_t84_f64
													get_local $i_f64
													get_local $mc_t84_f64
													f64.sub
													set_local $mc_t19_f64
													get_local $data_i32
													get_local $mc_t19_f64
													i32.trunc_s/f64
													get_local $mc_t14_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t54_f64
													get_local $tempi_f64
													set_local $mc_t55_f64
													get_local $mc_t54_f64
													get_local $mc_t55_f64
													f64.add
													set_local $mc_t15_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													get_local $mc_t15_f64
													call $set_array_index_f64
													br $loop_mc_t112
												end

											end

											i32.const 0
											set_local $mc_t111_i32
											br 0
										end

									end

									get_local $wr_f64
									set_local $wtemp_f64
									get_local $wtemp_f64
									get_local $wpr_f64
									f64.mul
									set_local $mc_t58_f64
									get_local $wi_f64
									get_local $wpi_f64
									f64.mul
									set_local $mc_t59_f64
									get_local $mc_t58_f64
									get_local $mc_t59_f64
									f64.sub
									set_local $mc_t56_f64
									get_local $wr_f64
									set_local $mc_t57_f64
									get_local $mc_t56_f64
									get_local $mc_t57_f64
									f64.add
									set_local $wr_f64
									get_local $wi_f64
									get_local $wpr_f64
									f64.mul
									set_local $mc_t62_f64
									get_local $wtemp_f64
									get_local $wpi_f64
									f64.mul
									set_local $mc_t63_f64
									get_local $mc_t62_f64
									get_local $mc_t63_f64
									f64.add
									set_local $mc_t60_f64
									get_local $wi_f64
									set_local $mc_t61_f64
									get_local $mc_t60_f64
									get_local $mc_t61_f64
									f64.add
									set_local $wi_f64
								end

								br 2
							end

							loop $loop_mc_t115
								get_local $mc_t114_i32
								if 
									get_local $m_f64
									get_local $mc_t86_f64
									f64.add
									set_local $m_f64
								else
									i32.const 1
									set_local $mc_t114_i32
									get_local $mc_t85_f64
									set_local $m_f64
								end

								block $block_mc_t116
									get_local $m_f64
									get_local $mmax_f64
									f64.lt
									br_if $block_mc_t116
									get_local $mc_t117_i32
									i32.eqz
									if 
										block 
											block 
												block 
													block 
														get_local $m_f64
														get_local $istep_f64
														get_local $nn_f64
														call $dynamic_loop_three
														br_table 0 1 2 
													end

													block $block_mc_t118
														get_local $m_f64
														set_local $i_f64
														get_local $i_f64
														get_local $mmax_f64
														f64.add
														set_local $j_f64
														get_local $wr_f64
														set_local $mc_t36_f64
														f64.const 1.0
														set_local $mc_t79_f64
														get_local $j_f64
														get_local $mc_t79_f64
														f64.sub
														set_local $mc_t38_f64
														get_local $data_i32
														get_local $mc_t38_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t37_f64
														get_local $mc_t36_f64
														get_local $mc_t37_f64
														f64.mul
														set_local $mc_t32_f64
														get_local $wi_f64
														set_local $mc_t34_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t35_f64
														get_local $mc_t34_f64
														get_local $mc_t35_f64
														f64.mul
														set_local $mc_t33_f64
														get_local $mc_t32_f64
														get_local $mc_t33_f64
														f64.sub
														set_local $tempr_f64
														get_local $wr_f64
														set_local $mc_t44_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t45_f64
														get_local $mc_t44_f64
														get_local $mc_t45_f64
														f64.mul
														set_local $mc_t39_f64
														get_local $wi_f64
														set_local $mc_t41_f64
														f64.const 1.0
														set_local $mc_t80_f64
														get_local $j_f64
														get_local $mc_t80_f64
														f64.sub
														set_local $mc_t43_f64
														get_local $data_i32
														get_local $mc_t43_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t42_f64
														get_local $mc_t41_f64
														get_local $mc_t42_f64
														f64.mul
														set_local $mc_t40_f64
														get_local $mc_t39_f64
														get_local $mc_t40_f64
														f64.add
														set_local $tempi_f64
														f64.const 1.0
														set_local $mc_t81_f64
														get_local $i_f64
														get_local $mc_t81_f64
														f64.sub
														set_local $mc_t48_f64
														get_local $data_i32
														get_local $mc_t48_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t46_f64
														get_local $tempr_f64
														set_local $mc_t47_f64
														get_local $mc_t46_f64
														get_local $mc_t47_f64
														f64.sub
														set_local $mc_t12_f64
														f64.const 1.0
														set_local $mc_t82_f64
														get_local $j_f64
														get_local $mc_t82_f64
														f64.sub
														set_local $mc_t18_f64
														get_local $data_i32
														get_local $mc_t18_f64
														i32.trunc_s/f64
														get_local $mc_t12_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t49_f64
														get_local $tempi_f64
														set_local $mc_t50_f64
														get_local $mc_t49_f64
														get_local $mc_t50_f64
														f64.sub
														set_local $mc_t13_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														get_local $mc_t13_f64
														call $set_array_index_f64
														f64.const 1.0
														set_local $mc_t83_f64
														get_local $i_f64
														get_local $mc_t83_f64
														f64.sub
														set_local $mc_t53_f64
														get_local $data_i32
														get_local $mc_t53_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t51_f64
														get_local $tempr_f64
														set_local $mc_t52_f64
														get_local $mc_t51_f64
														get_local $mc_t52_f64
														f64.add
														set_local $mc_t14_f64
														f64.const 1.0
														set_local $mc_t84_f64
														get_local $i_f64
														get_local $mc_t84_f64
														f64.sub
														set_local $mc_t19_f64
														get_local $data_i32
														get_local $mc_t19_f64
														i32.trunc_s/f64
														get_local $mc_t14_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t54_f64
														get_local $tempi_f64
														set_local $mc_t55_f64
														get_local $mc_t54_f64
														get_local $mc_t55_f64
														f64.add
														set_local $mc_t15_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														get_local $mc_t15_f64
														call $set_array_index_f64
													end

													br 2
												end

												loop $loop_mc_t120
													get_local $mc_t119_i32
													if 
														get_local $i_f64
														get_local $istep_f64
														f64.add
														set_local $i_f64
													else
														i32.const 1
														set_local $mc_t119_i32
														get_local $m_f64
														set_local $i_f64
													end

													block $block_mc_t121
														get_local $i_f64
														get_local $nn_f64
														f64.lt
														br_if $block_mc_t121
														get_local $i_f64
														get_local $mmax_f64
														f64.add
														set_local $j_f64
														get_local $wr_f64
														set_local $mc_t36_f64
														f64.const 1.0
														set_local $mc_t79_f64
														get_local $j_f64
														get_local $mc_t79_f64
														f64.sub
														set_local $mc_t38_f64
														get_local $data_i32
														get_local $mc_t38_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t37_f64
														get_local $mc_t36_f64
														get_local $mc_t37_f64
														f64.mul
														set_local $mc_t32_f64
														get_local $wi_f64
														set_local $mc_t34_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t35_f64
														get_local $mc_t34_f64
														get_local $mc_t35_f64
														f64.mul
														set_local $mc_t33_f64
														get_local $mc_t32_f64
														get_local $mc_t33_f64
														f64.sub
														set_local $tempr_f64
														get_local $wr_f64
														set_local $mc_t44_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t45_f64
														get_local $mc_t44_f64
														get_local $mc_t45_f64
														f64.mul
														set_local $mc_t39_f64
														get_local $wi_f64
														set_local $mc_t41_f64
														f64.const 1.0
														set_local $mc_t80_f64
														get_local $j_f64
														get_local $mc_t80_f64
														f64.sub
														set_local $mc_t43_f64
														get_local $data_i32
														get_local $mc_t43_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t42_f64
														get_local $mc_t41_f64
														get_local $mc_t42_f64
														f64.mul
														set_local $mc_t40_f64
														get_local $mc_t39_f64
														get_local $mc_t40_f64
														f64.add
														set_local $tempi_f64
														f64.const 1.0
														set_local $mc_t81_f64
														get_local $i_f64
														get_local $mc_t81_f64
														f64.sub
														set_local $mc_t48_f64
														get_local $data_i32
														get_local $mc_t48_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t46_f64
														get_local $tempr_f64
														set_local $mc_t47_f64
														get_local $mc_t46_f64
														get_local $mc_t47_f64
														f64.sub
														set_local $mc_t12_f64
														f64.const 1.0
														set_local $mc_t82_f64
														get_local $j_f64
														get_local $mc_t82_f64
														f64.sub
														set_local $mc_t18_f64
														get_local $data_i32
														get_local $mc_t18_f64
														i32.trunc_s/f64
														get_local $mc_t12_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t49_f64
														get_local $tempi_f64
														set_local $mc_t50_f64
														get_local $mc_t49_f64
														get_local $mc_t50_f64
														f64.sub
														set_local $mc_t13_f64
														get_local $data_i32
														get_local $j_f64
														i32.trunc_s/f64
														get_local $mc_t13_f64
														call $set_array_index_f64
														f64.const 1.0
														set_local $mc_t83_f64
														get_local $i_f64
														get_local $mc_t83_f64
														f64.sub
														set_local $mc_t53_f64
														get_local $data_i32
														get_local $mc_t53_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t51_f64
														get_local $tempr_f64
														set_local $mc_t52_f64
														get_local $mc_t51_f64
														get_local $mc_t52_f64
														f64.add
														set_local $mc_t14_f64
														f64.const 1.0
														set_local $mc_t84_f64
														get_local $i_f64
														get_local $mc_t84_f64
														f64.sub
														set_local $mc_t19_f64
														get_local $data_i32
														get_local $mc_t19_f64
														i32.trunc_s/f64
														get_local $mc_t14_f64
														call $set_array_index_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														call $get_array_index_f64
														set_local $mc_t54_f64
														get_local $tempi_f64
														set_local $mc_t55_f64
														get_local $mc_t54_f64
														get_local $mc_t55_f64
														f64.add
														set_local $mc_t15_f64
														get_local $data_i32
														get_local $i_f64
														i32.trunc_s/f64
														get_local $mc_t15_f64
														call $set_array_index_f64
														br $loop_mc_t120
													end

												end

												i32.const 0
												set_local $mc_t119_i32
												br 1
											end

											loop $loop_mc_t123
												get_local $mc_t122_i32
												if 
													get_local $i_f64
													get_local $istep_f64
													f64.add
													set_local $i_f64
												else
													i32.const 1
													set_local $mc_t122_i32
													get_local $m_f64
													set_local $i_f64
												end

												block $block_mc_t124
													get_local $i_f64
													get_local $nn_f64
													f64.gt
													br_if $block_mc_t124
													get_local $i_f64
													get_local $mmax_f64
													f64.add
													set_local $j_f64
													get_local $wr_f64
													set_local $mc_t36_f64
													f64.const 1.0
													set_local $mc_t79_f64
													get_local $j_f64
													get_local $mc_t79_f64
													f64.sub
													set_local $mc_t38_f64
													get_local $data_i32
													get_local $mc_t38_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t37_f64
													get_local $mc_t36_f64
													get_local $mc_t37_f64
													f64.mul
													set_local $mc_t32_f64
													get_local $wi_f64
													set_local $mc_t34_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t35_f64
													get_local $mc_t34_f64
													get_local $mc_t35_f64
													f64.mul
													set_local $mc_t33_f64
													get_local $mc_t32_f64
													get_local $mc_t33_f64
													f64.sub
													set_local $tempr_f64
													get_local $wr_f64
													set_local $mc_t44_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t45_f64
													get_local $mc_t44_f64
													get_local $mc_t45_f64
													f64.mul
													set_local $mc_t39_f64
													get_local $wi_f64
													set_local $mc_t41_f64
													f64.const 1.0
													set_local $mc_t80_f64
													get_local $j_f64
													get_local $mc_t80_f64
													f64.sub
													set_local $mc_t43_f64
													get_local $data_i32
													get_local $mc_t43_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t42_f64
													get_local $mc_t41_f64
													get_local $mc_t42_f64
													f64.mul
													set_local $mc_t40_f64
													get_local $mc_t39_f64
													get_local $mc_t40_f64
													f64.add
													set_local $tempi_f64
													f64.const 1.0
													set_local $mc_t81_f64
													get_local $i_f64
													get_local $mc_t81_f64
													f64.sub
													set_local $mc_t48_f64
													get_local $data_i32
													get_local $mc_t48_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t46_f64
													get_local $tempr_f64
													set_local $mc_t47_f64
													get_local $mc_t46_f64
													get_local $mc_t47_f64
													f64.sub
													set_local $mc_t12_f64
													f64.const 1.0
													set_local $mc_t82_f64
													get_local $j_f64
													get_local $mc_t82_f64
													f64.sub
													set_local $mc_t18_f64
													get_local $data_i32
													get_local $mc_t18_f64
													i32.trunc_s/f64
													get_local $mc_t12_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t49_f64
													get_local $tempi_f64
													set_local $mc_t50_f64
													get_local $mc_t49_f64
													get_local $mc_t50_f64
													f64.sub
													set_local $mc_t13_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													get_local $mc_t13_f64
													call $set_array_index_f64
													f64.const 1.0
													set_local $mc_t83_f64
													get_local $i_f64
													get_local $mc_t83_f64
													f64.sub
													set_local $mc_t53_f64
													get_local $data_i32
													get_local $mc_t53_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t51_f64
													get_local $tempr_f64
													set_local $mc_t52_f64
													get_local $mc_t51_f64
													get_local $mc_t52_f64
													f64.add
													set_local $mc_t14_f64
													f64.const 1.0
													set_local $mc_t84_f64
													get_local $i_f64
													get_local $mc_t84_f64
													f64.sub
													set_local $mc_t19_f64
													get_local $data_i32
													get_local $mc_t19_f64
													i32.trunc_s/f64
													get_local $mc_t14_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t54_f64
													get_local $tempi_f64
													set_local $mc_t55_f64
													get_local $mc_t54_f64
													get_local $mc_t55_f64
													f64.add
													set_local $mc_t15_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													get_local $mc_t15_f64
													call $set_array_index_f64
													br $loop_mc_t123
												end

											end

											i32.const 0
											set_local $mc_t122_i32
											br 0
										end

									end

									get_local $wr_f64
									set_local $wtemp_f64
									get_local $wtemp_f64
									get_local $wpr_f64
									f64.mul
									set_local $mc_t58_f64
									get_local $wi_f64
									get_local $wpi_f64
									f64.mul
									set_local $mc_t59_f64
									get_local $mc_t58_f64
									get_local $mc_t59_f64
									f64.sub
									set_local $mc_t56_f64
									get_local $wr_f64
									set_local $mc_t57_f64
									get_local $mc_t56_f64
									get_local $mc_t57_f64
									f64.add
									set_local $wr_f64
									get_local $wi_f64
									get_local $wpr_f64
									f64.mul
									set_local $mc_t62_f64
									get_local $wtemp_f64
									get_local $wpi_f64
									f64.mul
									set_local $mc_t63_f64
									get_local $mc_t62_f64
									get_local $mc_t63_f64
									f64.add
									set_local $mc_t60_f64
									get_local $wi_f64
									set_local $mc_t61_f64
									get_local $mc_t60_f64
									get_local $mc_t61_f64
									f64.add
									set_local $wi_f64
									br $loop_mc_t115
								end

							end

							i32.const 0
							set_local $mc_t114_i32
							br 1
						end

						loop $loop_mc_t126
							get_local $mc_t125_i32
							if 
								get_local $m_f64
								get_local $mc_t86_f64
								f64.add
								set_local $m_f64
							else
								i32.const 1
								set_local $mc_t125_i32
								get_local $mc_t85_f64
								set_local $m_f64
							end

							block $block_mc_t127
								get_local $m_f64
								get_local $mmax_f64
								f64.gt
								br_if $block_mc_t127
								get_local $mc_t128_i32
								i32.eqz
								if 
									block 
										block 
											block 
												block 
													get_local $m_f64
													get_local $istep_f64
													get_local $nn_f64
													call $dynamic_loop_three
													br_table 0 1 2 
												end

												block $block_mc_t129
													get_local $m_f64
													set_local $i_f64
													get_local $i_f64
													get_local $mmax_f64
													f64.add
													set_local $j_f64
													get_local $wr_f64
													set_local $mc_t36_f64
													f64.const 1.0
													set_local $mc_t79_f64
													get_local $j_f64
													get_local $mc_t79_f64
													f64.sub
													set_local $mc_t38_f64
													get_local $data_i32
													get_local $mc_t38_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t37_f64
													get_local $mc_t36_f64
													get_local $mc_t37_f64
													f64.mul
													set_local $mc_t32_f64
													get_local $wi_f64
													set_local $mc_t34_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t35_f64
													get_local $mc_t34_f64
													get_local $mc_t35_f64
													f64.mul
													set_local $mc_t33_f64
													get_local $mc_t32_f64
													get_local $mc_t33_f64
													f64.sub
													set_local $tempr_f64
													get_local $wr_f64
													set_local $mc_t44_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t45_f64
													get_local $mc_t44_f64
													get_local $mc_t45_f64
													f64.mul
													set_local $mc_t39_f64
													get_local $wi_f64
													set_local $mc_t41_f64
													f64.const 1.0
													set_local $mc_t80_f64
													get_local $j_f64
													get_local $mc_t80_f64
													f64.sub
													set_local $mc_t43_f64
													get_local $data_i32
													get_local $mc_t43_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t42_f64
													get_local $mc_t41_f64
													get_local $mc_t42_f64
													f64.mul
													set_local $mc_t40_f64
													get_local $mc_t39_f64
													get_local $mc_t40_f64
													f64.add
													set_local $tempi_f64
													f64.const 1.0
													set_local $mc_t81_f64
													get_local $i_f64
													get_local $mc_t81_f64
													f64.sub
													set_local $mc_t48_f64
													get_local $data_i32
													get_local $mc_t48_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t46_f64
													get_local $tempr_f64
													set_local $mc_t47_f64
													get_local $mc_t46_f64
													get_local $mc_t47_f64
													f64.sub
													set_local $mc_t12_f64
													f64.const 1.0
													set_local $mc_t82_f64
													get_local $j_f64
													get_local $mc_t82_f64
													f64.sub
													set_local $mc_t18_f64
													get_local $data_i32
													get_local $mc_t18_f64
													i32.trunc_s/f64
													get_local $mc_t12_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t49_f64
													get_local $tempi_f64
													set_local $mc_t50_f64
													get_local $mc_t49_f64
													get_local $mc_t50_f64
													f64.sub
													set_local $mc_t13_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													get_local $mc_t13_f64
													call $set_array_index_f64
													f64.const 1.0
													set_local $mc_t83_f64
													get_local $i_f64
													get_local $mc_t83_f64
													f64.sub
													set_local $mc_t53_f64
													get_local $data_i32
													get_local $mc_t53_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t51_f64
													get_local $tempr_f64
													set_local $mc_t52_f64
													get_local $mc_t51_f64
													get_local $mc_t52_f64
													f64.add
													set_local $mc_t14_f64
													f64.const 1.0
													set_local $mc_t84_f64
													get_local $i_f64
													get_local $mc_t84_f64
													f64.sub
													set_local $mc_t19_f64
													get_local $data_i32
													get_local $mc_t19_f64
													i32.trunc_s/f64
													get_local $mc_t14_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t54_f64
													get_local $tempi_f64
													set_local $mc_t55_f64
													get_local $mc_t54_f64
													get_local $mc_t55_f64
													f64.add
													set_local $mc_t15_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													get_local $mc_t15_f64
													call $set_array_index_f64
												end

												br 2
											end

											loop $loop_mc_t131
												get_local $mc_t130_i32
												if 
													get_local $i_f64
													get_local $istep_f64
													f64.add
													set_local $i_f64
												else
													i32.const 1
													set_local $mc_t130_i32
													get_local $m_f64
													set_local $i_f64
												end

												block $block_mc_t132
													get_local $i_f64
													get_local $nn_f64
													f64.lt
													br_if $block_mc_t132
													get_local $i_f64
													get_local $mmax_f64
													f64.add
													set_local $j_f64
													get_local $wr_f64
													set_local $mc_t36_f64
													f64.const 1.0
													set_local $mc_t79_f64
													get_local $j_f64
													get_local $mc_t79_f64
													f64.sub
													set_local $mc_t38_f64
													get_local $data_i32
													get_local $mc_t38_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t37_f64
													get_local $mc_t36_f64
													get_local $mc_t37_f64
													f64.mul
													set_local $mc_t32_f64
													get_local $wi_f64
													set_local $mc_t34_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t35_f64
													get_local $mc_t34_f64
													get_local $mc_t35_f64
													f64.mul
													set_local $mc_t33_f64
													get_local $mc_t32_f64
													get_local $mc_t33_f64
													f64.sub
													set_local $tempr_f64
													get_local $wr_f64
													set_local $mc_t44_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t45_f64
													get_local $mc_t44_f64
													get_local $mc_t45_f64
													f64.mul
													set_local $mc_t39_f64
													get_local $wi_f64
													set_local $mc_t41_f64
													f64.const 1.0
													set_local $mc_t80_f64
													get_local $j_f64
													get_local $mc_t80_f64
													f64.sub
													set_local $mc_t43_f64
													get_local $data_i32
													get_local $mc_t43_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t42_f64
													get_local $mc_t41_f64
													get_local $mc_t42_f64
													f64.mul
													set_local $mc_t40_f64
													get_local $mc_t39_f64
													get_local $mc_t40_f64
													f64.add
													set_local $tempi_f64
													f64.const 1.0
													set_local $mc_t81_f64
													get_local $i_f64
													get_local $mc_t81_f64
													f64.sub
													set_local $mc_t48_f64
													get_local $data_i32
													get_local $mc_t48_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t46_f64
													get_local $tempr_f64
													set_local $mc_t47_f64
													get_local $mc_t46_f64
													get_local $mc_t47_f64
													f64.sub
													set_local $mc_t12_f64
													f64.const 1.0
													set_local $mc_t82_f64
													get_local $j_f64
													get_local $mc_t82_f64
													f64.sub
													set_local $mc_t18_f64
													get_local $data_i32
													get_local $mc_t18_f64
													i32.trunc_s/f64
													get_local $mc_t12_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t49_f64
													get_local $tempi_f64
													set_local $mc_t50_f64
													get_local $mc_t49_f64
													get_local $mc_t50_f64
													f64.sub
													set_local $mc_t13_f64
													get_local $data_i32
													get_local $j_f64
													i32.trunc_s/f64
													get_local $mc_t13_f64
													call $set_array_index_f64
													f64.const 1.0
													set_local $mc_t83_f64
													get_local $i_f64
													get_local $mc_t83_f64
													f64.sub
													set_local $mc_t53_f64
													get_local $data_i32
													get_local $mc_t53_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t51_f64
													get_local $tempr_f64
													set_local $mc_t52_f64
													get_local $mc_t51_f64
													get_local $mc_t52_f64
													f64.add
													set_local $mc_t14_f64
													f64.const 1.0
													set_local $mc_t84_f64
													get_local $i_f64
													get_local $mc_t84_f64
													f64.sub
													set_local $mc_t19_f64
													get_local $data_i32
													get_local $mc_t19_f64
													i32.trunc_s/f64
													get_local $mc_t14_f64
													call $set_array_index_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													call $get_array_index_f64
													set_local $mc_t54_f64
													get_local $tempi_f64
													set_local $mc_t55_f64
													get_local $mc_t54_f64
													get_local $mc_t55_f64
													f64.add
													set_local $mc_t15_f64
													get_local $data_i32
													get_local $i_f64
													i32.trunc_s/f64
													get_local $mc_t15_f64
													call $set_array_index_f64
													br $loop_mc_t131
												end

											end

											i32.const 0
											set_local $mc_t130_i32
											br 1
										end

										loop $loop_mc_t134
											get_local $mc_t133_i32
											if 
												get_local $i_f64
												get_local $istep_f64
												f64.add
												set_local $i_f64
											else
												i32.const 1
												set_local $mc_t133_i32
												get_local $m_f64
												set_local $i_f64
											end

											block $block_mc_t135
												get_local $i_f64
												get_local $nn_f64
												f64.gt
												br_if $block_mc_t135
												get_local $i_f64
												get_local $mmax_f64
												f64.add
												set_local $j_f64
												get_local $wr_f64
												set_local $mc_t36_f64
												f64.const 1.0
												set_local $mc_t79_f64
												get_local $j_f64
												get_local $mc_t79_f64
												f64.sub
												set_local $mc_t38_f64
												get_local $data_i32
												get_local $mc_t38_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t37_f64
												get_local $mc_t36_f64
												get_local $mc_t37_f64
												f64.mul
												set_local $mc_t32_f64
												get_local $wi_f64
												set_local $mc_t34_f64
												get_local $data_i32
												get_local $j_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t35_f64
												get_local $mc_t34_f64
												get_local $mc_t35_f64
												f64.mul
												set_local $mc_t33_f64
												get_local $mc_t32_f64
												get_local $mc_t33_f64
												f64.sub
												set_local $tempr_f64
												get_local $wr_f64
												set_local $mc_t44_f64
												get_local $data_i32
												get_local $j_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t45_f64
												get_local $mc_t44_f64
												get_local $mc_t45_f64
												f64.mul
												set_local $mc_t39_f64
												get_local $wi_f64
												set_local $mc_t41_f64
												f64.const 1.0
												set_local $mc_t80_f64
												get_local $j_f64
												get_local $mc_t80_f64
												f64.sub
												set_local $mc_t43_f64
												get_local $data_i32
												get_local $mc_t43_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t42_f64
												get_local $mc_t41_f64
												get_local $mc_t42_f64
												f64.mul
												set_local $mc_t40_f64
												get_local $mc_t39_f64
												get_local $mc_t40_f64
												f64.add
												set_local $tempi_f64
												f64.const 1.0
												set_local $mc_t81_f64
												get_local $i_f64
												get_local $mc_t81_f64
												f64.sub
												set_local $mc_t48_f64
												get_local $data_i32
												get_local $mc_t48_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t46_f64
												get_local $tempr_f64
												set_local $mc_t47_f64
												get_local $mc_t46_f64
												get_local $mc_t47_f64
												f64.sub
												set_local $mc_t12_f64
												f64.const 1.0
												set_local $mc_t82_f64
												get_local $j_f64
												get_local $mc_t82_f64
												f64.sub
												set_local $mc_t18_f64
												get_local $data_i32
												get_local $mc_t18_f64
												i32.trunc_s/f64
												get_local $mc_t12_f64
												call $set_array_index_f64
												get_local $data_i32
												get_local $i_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t49_f64
												get_local $tempi_f64
												set_local $mc_t50_f64
												get_local $mc_t49_f64
												get_local $mc_t50_f64
												f64.sub
												set_local $mc_t13_f64
												get_local $data_i32
												get_local $j_f64
												i32.trunc_s/f64
												get_local $mc_t13_f64
												call $set_array_index_f64
												f64.const 1.0
												set_local $mc_t83_f64
												get_local $i_f64
												get_local $mc_t83_f64
												f64.sub
												set_local $mc_t53_f64
												get_local $data_i32
												get_local $mc_t53_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t51_f64
												get_local $tempr_f64
												set_local $mc_t52_f64
												get_local $mc_t51_f64
												get_local $mc_t52_f64
												f64.add
												set_local $mc_t14_f64
												f64.const 1.0
												set_local $mc_t84_f64
												get_local $i_f64
												get_local $mc_t84_f64
												f64.sub
												set_local $mc_t19_f64
												get_local $data_i32
												get_local $mc_t19_f64
												i32.trunc_s/f64
												get_local $mc_t14_f64
												call $set_array_index_f64
												get_local $data_i32
												get_local $i_f64
												i32.trunc_s/f64
												call $get_array_index_f64
												set_local $mc_t54_f64
												get_local $tempi_f64
												set_local $mc_t55_f64
												get_local $mc_t54_f64
												get_local $mc_t55_f64
												f64.add
												set_local $mc_t15_f64
												get_local $data_i32
												get_local $i_f64
												i32.trunc_s/f64
												get_local $mc_t15_f64
												call $set_array_index_f64
												br $loop_mc_t134
											end

										end

										i32.const 0
										set_local $mc_t133_i32
										br 0
									end

								end

								get_local $wr_f64
								set_local $wtemp_f64
								get_local $wtemp_f64
								get_local $wpr_f64
								f64.mul
								set_local $mc_t58_f64
								get_local $wi_f64
								get_local $wpi_f64
								f64.mul
								set_local $mc_t59_f64
								get_local $mc_t58_f64
								get_local $mc_t59_f64
								f64.sub
								set_local $mc_t56_f64
								get_local $wr_f64
								set_local $mc_t57_f64
								get_local $mc_t56_f64
								get_local $mc_t57_f64
								f64.add
								set_local $wr_f64
								get_local $wi_f64
								get_local $wpr_f64
								f64.mul
								set_local $mc_t62_f64
								get_local $wtemp_f64
								get_local $wpi_f64
								f64.mul
								set_local $mc_t63_f64
								get_local $mc_t62_f64
								get_local $mc_t63_f64
								f64.add
								set_local $mc_t60_f64
								get_local $wi_f64
								set_local $mc_t61_f64
								get_local $mc_t60_f64
								get_local $mc_t61_f64
								f64.add
								set_local $wi_f64
								br $loop_mc_t126
							end

						end

						i32.const 0
						set_local $mc_t125_i32
						br 0
					end

				end

				get_local $istep_f64
				set_local $mmax_f64
				get_local $nn_f64
				get_local $mmax_f64
				f64.gt
				f64.convert_u/i32
				set_local $mc_t87_f64
				br $loop_mc_t102
			end

		end

		get_local $data_i32
		call $clone
		set_local $result_i32
		get_local $result_i32
	)
	(func $drv_fft_S (param $scale_f64 f64)
		(local $mc_t0_f64 f64)(local $mc_t1_f64 f64)(local $data_i32 i32)(local $t_f64 f64)(local $mc_t3_f64 f64)(local $n_f64 f64)(local $mc_t8_f64 f64)(local $mc_t2_f64 f64)(local $mc_t4_f64 f64)(local $out_i32 i32)(local $mc_t5_f64 f64)(local $mc_t6_f64 f64)(local $mc_t136_i32 i32)
		get_local $scale_f64
		call $round_S
		set_local $mc_t1_f64
		f64.const 2.0
		set_local $mc_t3_f64
		get_local $mc_t3_f64
		get_local $mc_t1_f64
		call $mpower_SS
		set_local $mc_t0_f64
		f64.const 1024.0
		set_local $mc_t4_f64
		get_local $mc_t4_f64
		get_local $mc_t0_f64
		f64.mul
		set_local $n_f64
		f64.const 2.0
		set_local $mc_t5_f64
		get_local $mc_t5_f64
		get_local $n_f64
		f64.mul
		set_local $mc_t2_f64
		f64.const 1.0
		set_local $mc_t6_f64
		i32.const 2
		i32.const 0
		i32.const 0
		i32.const 0
		i32.const 0
		i32.const 0
		call $create_mxvector
		set_local $mc_t136_i32
		get_local $mc_t136_i32
		i32.const 0
		get_local $mc_t6_f64
		call $set_array_index_f64_no_check
		get_local $mc_t136_i32
		i32.const 1
		get_local $mc_t2_f64
		call $set_array_index_f64_no_check
		get_local $mc_t136_i32
		call $randn
		set_local $data_i32
		call $tic
		drop 
		f64.const 1.0
		set_local $mc_t8_f64
		get_local $data_i32
		get_local $n_f64
		get_local $mc_t8_f64
		call $fft_four1_MSS
		set_local $out_i32
		call $toc
		set_local $t_f64
		get_local $t_f64
		call $disp_S
	)
	(export "fft_four1_MSS" (func $fft_four1_MSS ) )
	(export "drv_fft_S" (func $drv_fft_S ) )
)
