"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class MachUtil {
    static createFloat64ArrayFromPtr(wi, ptr) {
        return new Float64Array(wi.mem.buffer, wi.mxarray_core_get_array_ptr(ptr), wi.numel(ptr));
    }
    static isAligned(offset, alignment) {
        return offset % alignment === 0;
    }
}
exports.MachUtil = MachUtil;
