"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MachTypes_1 = require("./MachTypes");
const MachRuntime_1 = require("./MachRuntime");
class MachArray {
    get _order() {
        return (this._attributes[0] === 0) ? "C" : "R";
    }
    instantiateDataView() {
        let header = this.header;
        let mclass = this._type_attribute[0];
        let number_class = this._type_attribute[2];
        switch (mclass) {
            case MachTypes_1.MatClass.CellArray:
            case MachTypes_1.MatClass.FunctionHandle:
            case MachTypes_1.MatClass.String:
                return new Int32Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
            default: // Array Class
                switch (number_class) {
                    case MachTypes_1.MClass.float64:
                        return (header[1] == 0) ? new Float64Array(0)
                            : new Float64Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.float32:
                        return (header[1] == 0) ? new Float32Array(0)
                            : new Float32Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.int16:
                        return (header[1] == 0) ? new Int16Array(0)
                            : new Int16Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.int8:
                        return (header[1] == 0) ? new Int8Array(0)
                            : new Int8Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.int64:
                        throw new Error("Int64 MachArray class not supported by the interface ");
                    case MachTypes_1.MClass.int32:
                        return (header[1] == 0) ? new Int32Array(0)
                            : new Int32Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.uint16:
                        return (header[1] == 0) ? new Uint16Array(0)
                            : new Uint16Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.uint8:
                        return (header[1] == 0) ? new Uint8Array(0)
                            : new Uint8Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.uint64:
                        throw new Error("UInt64 MachArray class not supported by the interface ");
                    case MachTypes_1.MClass.uint32:
                        return (header[1] == 0) ? new Uint32Array(0)
                            : new Uint32Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    case MachTypes_1.MClass.char:
                        return (header[1] == 0) ? new Uint8Array(0)
                            : new Uint8Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                    default:
                        return (header[1] == 0) ? new Uint8Array(0)
                            : new Uint8Array(MachRuntime_1._wi.mem.buffer, header[2], header[1]);
                }
        }
    }
    constructor(arr_ptr) {
        this.header = new Int32Array(MachRuntime_1._wi.mem.buffer, arr_ptr, 7);
        this._type_attribute = new Uint8Array(MachRuntime_1._wi.mem.buffer, arr_ptr, 4);
        this._data = this.instantiateDataView();
        this._attributes = new Uint8Array(MachRuntime_1._wi.mem.buffer, arr_ptr + 24, 4);
        this._numel = this.header[1];
        this._ndims = this.header[3];
        this._headerOffset = arr_ptr;
        this._shape = new Float64Array(MachRuntime_1._wi.mem.buffer, this.header[4], this._ndims);
        this._strides = new Float64Array(MachRuntime_1._wi.mem.buffer, this.header[5], this._ndims);
        this.BYTES_PER_ELEMENT = this._data.BYTES_PER_ELEMENT;
        this._mclass = MachTypes_1.MClass[this._type_attribute[2]];
        this._mat_class = MachTypes_1.MatClass[this._type_attribute[0]];
        this._gc_info = new Uint8Array(MachRuntime_1._wi.mem.buffer, arr_ptr + 24, 2);
    }
    getGCInfo() {
        return this._gc_info;
    }
    clone() {
        let new_arr_ptr = MachRuntime_1._wi.clone(this._headerOffset);
        return new MachArray(new_arr_ptr);
    }
    get_index(...args) {
        let index = this.index(...args);
        if (index >= this._numel)
            throw new Error("Index exceeds matrix dimensions.");
        return this._data[index];
    }
    set_index(args, value) {
        return this._data[this.index(...args)] = value;
    }
    /**
     * Returns the a mapping from a multi-dimensional index to a single index
     * @param {number} args
     * @returns {number}
     */
    index(...args) {
        if (args.length == 0)
            throw new Error("Must provide at least one index.");
        if (args.length == 1)
            return args[0];
        return args.reduce((acc, val, i) => { return acc + val * this._strides[i]; }, 0);
    }
    /**
     *
     * @param { number[] | MachArray | number} args
     * @returns { MachArray }
     */
    get(...args) {
        if (args.length == 0)
            return new MachArray(MachRuntime_1._wi.create_mxvector(0, 0, 0, 0));
        let ptrs_to_free = [];
        let input_vector_ptr = MachRuntime_1._wi.create_mxvector(args.length, 5, 0, 0);
        ptrs_to_free.push(input_vector_ptr);
        let vector_input = new Uint32Array(MachRuntime_1._wi.mem.buffer, MachRuntime_1._wi.mxarray_core_get_array_ptr(input_vector_ptr), args.length);
        args.forEach((dim_arr, dim_arr_ind) => {
            if (typeof dim_arr == 'number') {
                vector_input[dim_arr_ind] = MachRuntime_1._wi.convert_scalar_to_mxarray(dim_arr);
            }
            else if (Array.isArray(dim_arr)) {
                let dim_input_ptr = MachRuntime_1._wi.create_mxvector(dim_arr.length, 0, 0, 0);
                let dim_input = new Float64Array(MachRuntime_1._wi.mem.buffer, MachRuntime_1._wi.mxarray_core_get_array_ptr(dim_input_ptr), dim_arr.length);
                dim_arr.forEach((dim, dim_ind) => {
                    dim_input[dim_ind] = dim;
                });
                vector_input[dim_arr_ind] = dim_input.byteOffset;
                ptrs_to_free.push(dim_input_ptr);
            }
            else {
                vector_input[dim_arr_ind] = dim_arr._headerOffset;
            }
        });
        let ret = new MachArray(MachRuntime_1._wi.get_f64(this._headerOffset, input_vector_ptr));
        ptrs_to_free.forEach((arr) => MachRuntime_1._wi.free_macharray(arr));
        return ret;
    }
    set(args, values) {
        if (args.length == 0)
            return;
        let ptrs_to_free = [];
        let input_vector_ptr = MachRuntime_1._wi.create_mxvector(args.length, 5, 0, 0);
        ptrs_to_free.push(input_vector_ptr);
        let vector_input = new Uint32Array(MachRuntime_1._wi.mem.buffer, MachRuntime_1._wi.mxarray_core_get_array_ptr(input_vector_ptr), args.length);
        args.forEach((dim_arr, dim_arr_ind) => {
            if (typeof dim_arr == 'number') {
                vector_input[dim_arr_ind] = MachRuntime_1._wi.convert_scalar_to_mxarray(dim_arr);
            }
            else if (Array.isArray(dim_arr)) {
                let dim_input_ptr = MachRuntime_1._wi.create_mxvector(dim_arr.length, 0, 0, 0);
                let dim_input = new Float64Array(MachRuntime_1._wi.mem.buffer, MachRuntime_1._wi.mxarray_core_get_array_ptr(dim_input_ptr), dim_arr.length);
                dim_arr.forEach((dim, dim_ind) => {
                    dim_input[dim_ind] = dim;
                });
                vector_input[dim_arr_ind] = dim_input_ptr;
                ptrs_to_free.push(dim_input_ptr);
            }
            else {
                vector_input[dim_arr_ind] = dim_arr._headerOffset;
            }
        });
        let input_values_ptr;
        let input_values;
        if (typeof values == 'number') {
            input_values_ptr = MachRuntime_1._wi.convert_scalar_to_mxarray(values);
        }
        else if (Array.isArray(values)) {
            input_values_ptr = MachRuntime_1._wi.create_mxvector(values.length, 0, 0, 0);
            ptrs_to_free.push(input_values_ptr);
            input_values = new Float64Array(MachRuntime_1._wi.mem.buffer, MachRuntime_1._wi.mxarray_core_get_array_ptr(input_values_ptr), values.length);
            values.forEach((val, index) => {
                input_values[index] = val;
            });
        }
        else {
            input_values_ptr = values._headerOffset;
        }
        MachRuntime_1._wi.set_f64(this._headerOffset, input_vector_ptr, input_values_ptr);
        ptrs_to_free.forEach((arr) => MachRuntime_1._wi.free_macharray(arr));
    }
    numel() {
        return this._numel;
    }
    size() {
        return new Float64Array(this._shape);
    }
    ndims() {
        return this._shape.length;
    }
    dim_length() {
        let max = -Infinity;
        return this._shape.reduce((val) => (val > max) ? val : max, 0);
    }
    is_scalar() {
        return (this._numel === 1);
    }
    isrow() {
        return MachRuntime_1._wi.isrow(this._headerOffset) === 1;
    }
    iscolumn() {
        return MachRuntime_1._wi.iscolumn(this._headerOffset) === 1;
    }
    ismatrix() {
        return MachRuntime_1._wi.ismatrix(this._headerOffset) === 1;
    }
    isvector() {
        return MachRuntime_1._wi.isvector(this._headerOffset) === 1;
    }
    isempty() {
        return this._numel === 0;
    }
    reshape(newshape) {
        let that = this;
        if (newshape.length < 2)
            throw new Error("Size vector must have at least two elements.");
        let [newShape, totalVals, numDim] = newshape.reduce((acc, val, i) => {
            if (val < 0)
                throw new Error("Size vector elements should be nonnegative.");
            if (val > 1) {
                if (i != acc[2]) {
                    for (let k = acc[2]; k < i; k++)
                        acc[0].push(1);
                    acc[2] = i + 1;
                }
                else {
                    acc[2]++;
                }
                acc[1] *= val;
                acc[0].push(val);
            }
            return acc;
        }, [[], 1, 0]);
        if (totalVals != this._numel)
            throw new Error("New shape must have the same number of elements.");
        if (newShape.length == 1)
            newShape.push(1);
        that._ndims = newShape.length;
        MachRuntime_1._wi._free(that._shape.byteOffset);
        MachRuntime_1._wi._free(that._strides.byteOffset);
        that.header[4] = MachRuntime_1._wi.create_mxvector(newShape.length, 0, 0, 0);
        that.header[5] = MachRuntime_1._wi.create_mxvector(newShape.length, 0, 0, 0);
        that._shape = new Float64Array(MachRuntime_1._wi.mem.buffer, this.header[4], that._ndims);
        that._strides = new Float64Array(MachRuntime_1._wi.mem.buffer, this.header[5], that._ndims);
        newShape.forEach((dim, i) => {
            that._shape[i] = dim;
        });
        if (that._order == "C") {
            that._strides[0] = 1;
            for (let strideIndex = 1; strideIndex < that._ndims; strideIndex++) {
                that._strides[strideIndex] = that._strides[strideIndex - 1] * that._shape[strideIndex];
            }
        }
        else {
            that._strides[that._ndims - 1] = 1;
            for (let strideIndex = that._ndims - 2; strideIndex >= 0; strideIndex--) {
                that._strides[strideIndex] = that._strides[strideIndex + 1] * that._shape[strideIndex];
            }
        }
        return that;
    }
    /**
     *
     * @param args
     */
    stride() {
        return new Float64Array(this._strides);
    }
    reorder(order) {
        let that = new MachArray(MachRuntime_1._wi.copy_mxarray_header(this._headerOffset));
        if (order === that._order)
            return that;
        if (order === "C") {
            that._strides[0] = 1;
            for (let strideIndex = 1; strideIndex < that._ndims; strideIndex++) {
                that._strides[strideIndex] = that._strides[strideIndex - 1] * that._shape[strideIndex];
            }
        }
        else if (order === "R") {
            that._strides[that._ndims - 1] = 1;
            for (let strideIndex = that._ndims - 2; strideIndex >= 0; strideIndex--) {
                that._strides[strideIndex] = that._strides[strideIndex + 1] * that._shape[strideIndex];
            }
        }
        else {
            throw new Error(`Invalid new matrix order: ${order}, order must be either 'R' or 'C'`);
        }
        return that;
    }
    fill(value) {
        this._data.fill(value);
    }
    free() {
        MachRuntime_1._wi.free_macharray(this._headerOffset);
    }
}
exports.MachArray = MachArray;
