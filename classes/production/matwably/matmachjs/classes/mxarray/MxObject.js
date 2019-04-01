"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class MxObject {
    get arr_ptr() {
        return this._arr_ptr;
    }
    /**
     * Gets the contents in Float64Array
     * @deprecated
     * @param start
     * @param length
     */
    getContents(start = 0, length = this.numel()) {
        if (length < 0 || start < 0)
            throw new Error("View indices must be positive");
        if (length > this.numel() - start)
            throw new Error("Invalid length, index out-of-bounds");
        if (this.numel() > 0)
            return new Float64Array(this._wi.mem.buffer, this._wi.mxarray_core_get_array_ptr(this.arr_ptr) + start, length);
        else {
            return new Float64Array(0);
        }
    }
    size() {
        return this._wi.size(this.arr_ptr);
    }
    numel() {
        return this._wi.numel(this.arr_ptr);
    }
    ndims() {
        return this._wi.ndims(this.arr_ptr);
    }
    length() {
        return this._wi.length_M(this.arr_ptr);
    }
    isrow() {
        return this._wi.isrow(this.arr_ptr) === 1;
    }
    iscolumn() {
        return this._wi.isvector(this.arr_ptr) === 1;
    }
    ismatrix() {
        return this._wi.ismatrix(this.arr_ptr) === 1;
    }
    isvector() {
        return this._wi.isvector(this.arr_ptr) === 1;
    }
    isempty() {
        return this._wi.isempty(this.arr_ptr) === 1;
    }
}
exports.MxObject = MxObject;
