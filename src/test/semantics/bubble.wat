(module

    (func $bubble (param $A i32) (result i32)
        (local $n i32)(local $i i32) (local $j i32) 
        (set_local $n (call $array_length (get_local $A)))
        i32.const 1
        set_local $i
        
        loop
            block
               i32.const 1
               set_local $j 
               loop
                    block
                        
                        (i64.load align=3 i32.add (i32.mul (i32.sub (get_local i)(i32.const 1)) (call $get_array_type_size (call $get_type_array (get_local $A))))(get_local $A) ))
                        (i64.load align=3 (i32.add (i32.mul (i32.sub (get_local i)(i32.const 1)) (call $get_array_type_size (call $get_type_array (get_local $A))))(get_local $A) ))
 
                    end
                end 
            end
        end
    
    )
    (func $array_length (param $array i32) (result i32)

    )


)