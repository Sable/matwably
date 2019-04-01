const fs = require("fs");
const file = fs.readFileSync("./%s"); // compiled wasm

// Start of compilation and program

// Copy here all the Module definition.
if(typeof Module === "undefined") Module = {};
Module.initiated = false;

async function loader() {
    let wi;
    try {
        wi = await WebAssembly.instantiate(file, Module);
    } catch (err) {
        throw err;
    }
    Module.initiated = true;
    Module.exports =  wi.instance.exports;
    Module.memory = wi.mem;
    wi = wi.instance.exports;
    return Module;
}

module.exports.matlabModule = loader;

