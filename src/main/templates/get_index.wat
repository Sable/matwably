 (func $get_index (param $array_ptr i32)(param $i i32)(result %s)

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
        %s
        return
)