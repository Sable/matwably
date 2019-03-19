const fs = require("fs");
const shelljs = require("shelljs");
const file = fs.readFileSync(__dirname+"/drv_collatz-opt.wasm");
const time_mod = require("../../timing");

let memory =  new WebAssembly.Memory({initial:32767});
const { TextDecoder,TextEncoder } = require('util');
function printError(offset, length) {
    let bytes = new Uint8Array(libjs.js.mem.buffer, offset, length);
    let string = new TextDecoder('utf8').decode(bytes);
    throw new Error(string);
}

function printString(offset, length) {
    let bytes = new Uint8Array(libjs.js.mem.buffer, offset, length);
    let string = new TextDecoder('utf8').decode(bytes);
    console.log(string);
}
function printWhos(size, bytes, class_type)
{
    let name_class = '';
    switch(class_name)
    {
        case 0:
            name_class = "double";
    }
}
function printInt(number)
{
    console.log(number);
    return number;
}
function printDouble(number)
{
    console.log(number);
    return number;
}
let libjs = {
    "js":{
        "mem":memory,
        "printTime":printTime,
        "printError":printError,
        "printWho":printWhos,
        "printString":printString,
        "printDouble":printInt,
        "printDoubleNumber":printDouble,
        "printMarker":()=>console.log("MARKER"),
        "assert_header":1,
        "print_array_f64":printArrayDouble,
        "time":()=>Date.now()
    },
    "math":{
        ones:() => 1,
        rand:() => Math.random(),
        randn:() => randn_s(),
        randi:(max) => Math.ceil(max*Math.random()),
        zeros:()=> 0,
        pi:()=>Math.PI,
        e:()=>Math.E,
        isnan: isNaN,
        power:Math.pow,
        sin: Math.sin,
        cos: Math.cos,
        tan: Math.tan,
        exp: Math.exp,
        log: Math.log,
        log2: Math.log2,
        log10: Math.log10
    },
    "test":{
        "assert":assert
    }
};

function printArrayDouble(arr_ptr, length) {
    let arr = new Float64Array(memory.buffer, arr_ptr, length);
    console.log(arr);
}

function assert(condition, error_number) {
    let errors = {
        "0":"Invalid Assertion: class number is incorrect in function $mxarray_core_get_mclass",
        "1":"Invalid Assertion: elem_size number is incorrect in function $mxarray_core_set_type_attribute",
        "2":"Invalid Assertion: simple_class number is incorrect in function $mxarray_core_set_type_attribute",
        "3":"Invalid Assertion: complex number is incorrect in function $mxarray_core_set_type_attribute",
        "4":"Invalid Assertion: operation only valid for array type"
    };
    if(!condition)
    {
        throw new Error(errors[error_number]);
    }
}

function randn_s() {
    let rand = 0;

    for (let i = 0; i < 10; i += 1) {
        rand += Math.random();
    }
    return rand / 10;
}


function printTime(time){
    console.log(`Elapsed time is ${time/1000} seconds.`);
    return time;
}
// Start of compilation and program
Module = {};
Module.initiated = false;

async function loader() {
    let wi;
    try {
        wi = await WebAssembly.instantiate(file, libjs);
    } catch (err) {
        throw err;
    }
    Module.initiated = true;
    Module.exports =  wi.instance.exports;
    Module.memory = wi.mem;
    wi = wi.instance.exports;
    return Module;
}
module.exports.runSingleIteration = ()=>{
    return loader().then((mod)=> {
        let arr_wasm = [];
        arr_wasm.push(BigInt(mod.exports.drv_collatz_S(1000000)*1000));
        console.log(JSON.stringify({output:{wasm: String(time_mod.mean_bigint(arr_wasm))}}));
        return [mod.exports.drv_collatz_S(1000000)];
    });
};
module.exports.runJitted = ()=>{
    return loader().then((mod)=> {
        for(let i =0 ; i < 5;i++){
            mod.exports.drv_collatz_S(1000000)
        }
        let arr_wasm = [];
        for(let i =0 ; i < 10;i++){
            arr_wasm.push(BigInt(mod.exports.drv_collatz_S(1000000)*1000));
        }
        console.log(JSON.stringify({output:{wasm: String(time_mod.mean_bigint(arr_wasm))}}));
        return [mod.exports.drv_collatz_S(1000000)];
    });
};
loader().then((mod)=> {
		console.log(mod.exports.drv_collatz_S(1000000));

});
