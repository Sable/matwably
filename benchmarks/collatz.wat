(func $collatz_S (param $n_f64 f64)(result f64)
		(local $mc_t10_f64 f64)(local $mc_t13_f64 f64)(local $n_f64 f64)(local $mc_t8_f64 f64)(local $y_f64 f64)(local $mc_t11_f64 f64)(local $mc_t6_f64 f64)(local $mc_t14_f64 f64)(local $mc_t3_f64 f64)(local $mc_t12_f64 f64)(local $mc_t9_f64 f64)(local $mc_t7_f64 f64)(local $mc_t5_f64 f64)
		f64.const 0.0
		set_local $y_f64
		f64.const 1.0
		set_local $mc_t14_f64
		get_local $n_f64
		get_local $mc_t14_f64
		f64.ne
		f64.convert_u/i32
		set_local $mc_t13_f64
		loop $loop_mc_t15
			block $block_mc_t16
				get_local $mc_t13_f64
				f64.const 0.0
				f64.eq
				br_if $block_mc_t16
				f64.const 2.0
				set_local $mc_t6_f64
				get_local $n_f64
				get_local $mc_t6_f64
				call $mod_SS
				set_local $mc_t3_f64
				f64.const 0.0
				set_local $mc_t11_f64
				get_local $mc_t3_f64
				get_local $mc_t11_f64
				f64.eq
				f64.convert_u/i32
				set_local $mc_t10_f64
				get_local $mc_t10_f64
				f64.const 0.0
				f64.eq
				i32.eqz
				if
					f64.const 2.0
					set_local $mc_t7_f64
					get_local $n_f64
					get_local $mc_t7_f64
					f64.div
					set_local $n_f64
				else
					f64.const 3.0
					set_local $mc_t8_f64
					get_local $mc_t8_f64
					get_local $n_f64
					f64.mul
					set_local $mc_t5_f64
					f64.const 1.0
					set_local $mc_t9_f64
					get_local $mc_t5_f64
					get_local $mc_t9_f64
					f64.add
					set_local $n_f64
				end

				f64.const 1.0
				set_local $mc_t12_f64
				get_local $y_f64
				get_local $mc_t12_f64
				f64.add
				set_local $y_f64
				f64.const 1.0
				set_local $mc_t14_f64
				get_local $n_f64
				get_local $mc_t14_f64
				f64.ne
				f64.convert_u/i32
				set_local $mc_t13_f64
				br $loop_mc_t15
			end
		end

		get_local $y_f64
)
(func $drv_collatz_S (param $scale_f64 f64)
    (local $mc_t1_f64 f64)(local $t_f64 f64)(local $length_f64 f64)
    call $tic
    drop
    f64.const 1.0E9
    set_local $mc_t1_f64
    get_local $mc_t1_f64
    call $collatz_S
    set_local $length_f64
    call $toc
    set_local $t_f64
    get_local $t_f64
    call $disp_S
)