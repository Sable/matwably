"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// file initiates the wasm run-time
const matmachjs_lib_1 = require("../native/matmachjs-lib");
const matmachjs_wasm_1 = require("../native/matmachjs-wasm");
class MatmachWasmRuntime {
    MachWasmRuntime() { }
    static async getMatmachWasmInstance() {
        if (typeof MatmachWasmRuntime._wasm_instance == "undefined") {
            MatmachWasmRuntime._wasm_instance = await WebAssembly.instantiate(this.hexStringByteArray(matmachjs_wasm_1.MatMachNativeModule), matmachjs_lib_1.MatMachNativeLib);
        }
        return MatmachWasmRuntime._wasm_instance;
    }
    static hexStringByteArray(str) {
        let a = [];
        for (let i = 0, len = str.length; i < len; i += 2) {
            a.push(parseInt(str.substr(i, 2), 16));
        }
        return new Uint8Array(a);
    }
}
exports.default = MatmachWasmRuntime;
