
const shelljs = require("shelljs");
const timing = require("./timing");
const fs = require("fs");
const yargs = require("yargs")
    .option("verbose",{optional:true, alias:"v",describe:"Verbose"})
    .option("jit",{optional:true, alias:"j",describe:"Whether to run jitted option," +
        " if not then single iteration is ran"})
    .option("output-file",{optional:true, alias:"o",
        describe:"Output file, defaults to benchmark results",
        default:"benchmark_results.csv"}).argv;
const jitted = (yargs.jit)?"jit":"single";
let benchmarks = ["./*.bench.js","./**/**/*.bench.js"];
const benchmark_iterations = 10;

async function benchmarkRunner() {
    return new Promise((resolve, reject)=>{
        let benchmark_results = [];
        shelljs.ls(["./benchmarks/**/*.bench.js","./benchmarks/*.bench.js"]).forEach((benchmark_path,j)=>{
            if(yargs.verbose) console.log(`RUNNING BENCHMARK: ${benchmark_path}`);
            let bench_res_wasm = [];
            let bench_res_js = [];
            for(let i = 0;i<benchmark_iterations;i++){
                // let bench = shelljs.exec(`node ${benchmark_path}`)
                // let bench = require(benchmark_path);
                let res = shelljs.exec(`node ./runner --benchmark=${benchmark_path} --${jitted}`,
                    {async:false,
                        silent:!yargs.verbose});
                let {wasm,js} = JSON.parse(res.stdout).output;
                if(wasm) bench_res_wasm.push(BigInt(wasm));
                if(js) bench_res_js.push(BigInt(js));
                if(bench_res_wasm.length === benchmark_iterations) {
                    benchmark_results.push([bench_res_wasm, bench_res_js]);
                    if(benchmark_results.length === j) resolve(benchmark_results);
                    j++;
                }

            }
        });
    });
}
benchmarkRunner().then((benchmark_results)=>{
    let total_res = "benchmark,language,type,mean(ns)\n";
    let bool_res = false;
    benchmark_results.forEach(([wasm,js], i)=>{
        let wasm_time = timing.mean_bigint(wasm);
        let js_time = timing.mean_bigint(js);
        console.log(`Benchmark: ${benchmarks[i]}:\n wasm:`+
            ` ${timing.mean_bigint(wasm)}ns\n js: ${timing.mean_bigint(js)}ns`);
        total_res+=`${benchmarks[i]},wasm,${jitted},${wasm_time}\n`;
        total_res+=`${benchmarks[i]},js,${jitted},${js_time}\n`;
        bool_res = true;
    });
    if(bool_res) fs.writeFileSync(yargs.o, total_res); 
});
