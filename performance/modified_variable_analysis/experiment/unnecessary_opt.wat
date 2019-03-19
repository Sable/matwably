(func (param i32)(result i32)
    (local $i i32)(local $temp i32)
    loop
        get_local $i
        i32.const 5
        i32.add
        set_local $temp
        (set_local $i (i32.add (get_local $i)(i32.const 1)))
        (br_if 0 (i32.lt_s (get_local $i)(get_local 0)))
    end
    i32.const 3
)
(export "func" (func 0))