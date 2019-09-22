

let file = hexStringByteArray(wasmModuleHexString);
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

Module.matlabModule = loader;
function hexStringByteArray(str){
    let a = [];
    for (let i = 0, len = str.length; i < len; i+=2)
        a.push(parseInt(str.substr(i,2),16));
    return new Uint8Array(a);
}
// (async ()=>{
//     let mod = await loader();
//     mod.exports.%s(1000);
// })();

