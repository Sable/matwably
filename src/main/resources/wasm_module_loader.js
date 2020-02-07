const fs = require("fs");
const file = fs.readFileSync("./%s"); // compiled wasm

// Start of compilation and program

// Copy here all the Module definition.
if(typeof exports === "undefined") var exports = {};
if(typeof exports.MatMachNativeLib === "undefined") exports.MatMachNativeLib = {};
exports.MatMachNativeLib.initiated = false;
exports.MatMachNativeLib.matlabModule = loader;
async function loader() {
    let wi;
    try {
        wi = await WebAssembly.instantiate(file, exports.MatMachNativeLib );
    } catch (err) {
        throw err;
    }
    exports.MatMachNativeLib.initiated = true;
    exports.MatMachNativeLib.exports =  wi.instance.exports;
    exports.MatMachNativeLib.memory = wi.mem;
    wi = wi.instance.exports;
    return exports.MatMachNativeLib;
}



