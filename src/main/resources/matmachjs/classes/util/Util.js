class Util {
    static createFloat64ArrayFromPtr(wi, ptr) {
        return new Float64Array(wi.mem.buffer, wi.mxarray_core_get_array_ptr(ptr), wi.numel(ptr));
    }
}
