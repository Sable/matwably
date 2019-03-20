"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class MachUtil {
    static createFloat64ArrayFromPtr(wi, ptr) {
        return new Float64Array(wi.mem.buffer, wi.mxarray_core_get_array_ptr(ptr), wi.numel(ptr));
    }
    static free_input_memory(wi, arrs) {
        arrs.forEach((arr) => wi.free_macharray(arrs));
    }
    static isAligned(offset, alignment) {
        return offset % alignment === 0;
    }
}
exports.MachUtil = MachUtil;
