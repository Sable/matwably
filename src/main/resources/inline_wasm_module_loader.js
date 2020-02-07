

let file = hexStringByteArray(wasmModuleHexString);
if(typeof exports === "undefined") var exports = {};
if(typeof exports.MatMachNativeLib === "undefined") exports.MatMachNativeLib  = {};
exports.MatMachNativeLib.initiated = false;
exports.MatMachNativeLib.matlabModule = loader;

async function loader() {
    let wi;
    try {
        wi = await WebAssembly.instantiate(file, exports.MatMachNativeLib);
    } catch (err) {
        throw err;
    }
    exports.MatMachNativeLib.initiated = true;
    exports.MatMachNativeLib.exports =  wi.instance.exports;
    exports.MatMachNativeLib.memory = wi.mem;
    return exports.MatMachNativeLib;
}


function hexStringByteArray(str){
    let a = [];
    for (let i = 0, len = str.length; i < len; i+=2)
        a.push(parseInt(str.substr(i,2),16));
    return new Uint8Array(a);
}

