
const fs = require("fs");
const libjs = require("./lib_wasm");
const time_mod = require("./timing");
const matmach = require("../matmach");

module.exports.runSingleIteration = ()=>{
    return matmach.instantiateWasm()
        .then((mod)=>{
            let mean_wasm = runWasmBenchmark(mod);
            let mean_js = runJSBenchmark();
            console.log(JSON.stringify({output:{wasm: String(mean_wasm),js:String(mean_js)}}));
            return [runWasmBenchmark(mod), runJSBenchmark()];
        }).catch((err)=>{
            throw new Error(err);
        });
};
module.exports.runJitted = ()=>{
    return matmach.instantiateWasm()
        .then((mod)=>{
            let arr_wasm = [];
            let arr_js = [];
            for(let i =0 ; i < 10;i++) {
                runWasmBenchmark(mod);
                runJSBenchmark();
            }
            for(let i =0 ; i < 10;i++){
                arr_wasm.push(runWasmBenchmark(mod));
                arr_js.push(runJSBenchmark());
            }
            const mean_wasm = time_mod.mean_bigint(arr_wasm);
            const mean_js = time_mod.mean_bigint(arr_js);
            console.log(JSON.stringify({output:{wasm: String(mean_wasm), js: String(mean_js)}}));
            return [mean_wasm, mean_js];
        }).catch((err)=>{
            throw new Error(err);
        });
};


function mc_mod_SS(x$2, y) {
    return x$2 -  Math.floor(x$2 / y) * y;
}

function runWasmBenchmark(mod) {
    let start = process.hrtime.bigint();
    // 191051479007711n
    mod.mod_SS(232,2);
    let end = process.hrtime.bigint();
    // console.log(`Benchmark took ${end - start} nanoseconds`);
    return end-start;
}
function runJSBenchmark() {
    let start = process.hrtime.bigint();
    // 191051479007711n
    mc_mod_SS(232,2);
    let end = process.hrtime.bigint();
    // console.log(`Benchmark took ${end - start} nanoseconds`);
    return end - start;
}