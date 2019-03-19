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
    // module1
    mod.exports.func(1000000);
    let total = [];
    for(let i  = 0;i<10;i++){
        let start = process.hrtime.bigint();
        mod.exports.func(1000000);
        let end = process.hrtime.bigint();
        total.push(end-start);
    }
    console.log(`Benchmark took ${mean_bigint(total)/BigInt(10)} nanoseconds`);

    // module2
    mod2.exports.func(1000000);
    let total2 = [];
    for(let i  = 0;i<10;i++){
        let start2 = process.hrtime.bigint();
        mod2.exports.func(1000000);
        let end2 = process.hrtime.bigint();
        total2.push(end2-start2);
    }
    console.log(`Benchmark took ${mean_bigint(total2)/BigInt(10)} nanoseconds`);

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
let set_local = [3972,4469,3972,3972,4797,4801,3621,4430,3978,4380];
let no_set_local = [3630,3644,3965,3627,3963,4192,3972,3975,3976,3971];
console.log(mathjs.mean(set_local),mathjs.std(set_local))
console.log(mathjs.mean(no_set_local),mathjs.std(no_set_local))
