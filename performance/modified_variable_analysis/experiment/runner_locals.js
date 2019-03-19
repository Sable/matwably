const fs = require("fs");
const mathjs = require("mathjs");
const file = fs.readFileSync("./unnecessary_locals.wasm");
const file_no_set_local = fs.readFileSync("./unnecessary_locals_no_set_local.wasm");

async function loader() {
    let Module = {};
    let Module2 = {};
    let wi,wi2;

    try {
        wi = await WebAssembly.instantiate(file, {});
        wi2 = await WebAssembly.instantiate(file_no_set_local, {});
    } catch (err) {
        throw err;
    }
    Module.exports =  wi.instance.exports;
    Module2.exports =  wi2.instance.exports;
    return [Module, Module2];
}

loader().then(([mod,mod2])=>{
    let start = process.hrtime.bigint();
    mod.exports.func(10000);
    let end = process.hrtime.bigint();
    console.log(`Benchmark took ${end - start} nanoseconds`);
    let start2 = process.hrtime.bigint();
    mod2.exports.func(10000);
    let end2 = process.hrtime.bigint();
    console.log(`Benchmark took ${end2 - start2} nanoseconds`);
});
function mean_bigint(arr) {
    if(arr.constructor !== Array) throw new Error("Incorrect type");
    if(arr.length === 0) throw  new Error("Cannot return mean of empty array");
    let sum = BigInt(0);
    arr.forEach((num)=>{
        if(typeof num !== "bigint") throw new Error("Type of elements must be 'bigint'");
        sum+=num;
    });
    return sum / BigInt(arr.length);
}
let no_set_local = [4857,5306,4843,4890,5184,5003,5036,5427,5658,5806];
let set_local = [15777,21986,16905,17289,16273,20259,16813,17399,17053,17571];
console.log(mathjs.mean(set_local),mathjs.std(set_local))
console.log(mathjs.mean(no_set_local),mathjs.std(no_set_local))
