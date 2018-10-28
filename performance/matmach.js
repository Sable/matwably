async function getModule() {
    let wi = await WebAssembly.instantiate(fs.readFileSync("./builtins.wasm"),libjs);
    let mod = wi.instance.exports;
    return mod;
}
module.exports.instantiateWasm = getModule;