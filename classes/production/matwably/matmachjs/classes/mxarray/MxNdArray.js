"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MxArray_1 = require("./MxArray");
const MxVector_1 = require("./MxVector");
class MxNDArray extends MxArray_1.MxArray {
    constructor(wi, mxarray, class_type = 0, simple_type = 0, complex = false, column = 0, byte_size = 8) {
        super();
        this._wi = wi; // Refers to module exports
        if (typeof wi === "undefined") {
            throw Error("Error: WebAssembly Matlab module must be defined");
        }
        if (typeof mxarray == 'number') {
            this._arr_ptr = mxarray;
        }
        else if (mxarray instanceof MxNDArray) {
            this._arr_ptr = this._wi.clone(mxarray._arr_ptr);
        }
        else if (mxarray instanceof MxVector_1.MxVector) {
            this._arr_ptr = this._wi.create_mxarray_ND(mxarray.arr_ptr, class_type, simple_type, 0, 0);
        }
        else {
            let input_ptr = this._wi.create_mxvector(mxarray.length, simple_type, class_type, 0, 0);
            mxarray.forEach((val, idx) => {
                this._wi.set_array_index_f64(input_ptr, idx + 1, val);
            });
            this._arr_ptr = this._wi.create_mxarray_ND(input_ptr, class_type, simple_type, 0, 0);
        }
    }
    reshape(new_dimensions) {
        let dim_ptr = this._wi.create_mxvector(new_dimensions.length);
        new_dimensions.forEach((item, idx) => {
            this._wi.set_array_index_f64(dim_ptr, idx + 1, item);
        });
        return new MxNDArray(this._wi, this._wi.reshape(this._arr_ptr, dim_ptr));
    }
    set_indices(indices, values) {
        let indices_arr_ptr = this._wi.create_mxvector(indices.length, 5); // Create mxvector with int type
        indices.forEach((dimArr, indDim) => {
            if (dimArr instanceof MxNDArray) {
                this._wi.set_array_index_i32(indices_arr_ptr, indDim + 1, dimArr.arr_ptr);
            }
            else {
                let index_arr_ptr = this._wi.create_mxvector(dimArr.length);
                this._wi.set_array_index_i32(indices_arr_ptr, indDim + 1, index_arr_ptr);
                dimArr.forEach((val, indVal) => {
                    this._wi.set_array_index_f64(index_arr_ptr, indVal + 1, val);
                });
            }
        });
        let indices_val_arr_ptr;
        if (values instanceof MxNDArray) {
            indices_val_arr_ptr = values.arr_ptr;
        }
        else {
            indices_val_arr_ptr = this._wi.create_mxvector(values.length);
            values.forEach((val, ind) => {
                this._wi.set_array_index_f64(indices_val_arr_ptr, ind + 1, val);
            });
        }
        this._wi.set_f64(this._arr_ptr, indices_arr_ptr, indices_val_arr_ptr);
    }
    size() {
        return new MxNDArray(this._wi, super.size());
    }
    set_index(ind = -1, val = NaN) {
        return this._wi.set_array_index_f64(this._arr_ptr, ind, val);
    }
    get_index(ind = -1) {
        return this._wi.get_array_index_f64(this._arr_ptr, ind);
    }
    get_indices(indices) {
        let indices_arr_ptr = this._wi.create_mxvector(indices.length, 5); // Create mxvector with int type
        indices.forEach((dimArr, indDim) => {
            let index_arr_ptr = this._wi.create_mxvector(dimArr.length);
            dimArr.forEach((val, indVal) => {
                this._wi.set_array_index_f64(index_arr_ptr, indVal + 1, val);
            });
            this._wi.set_array_index_i32(indices_arr_ptr, indDim + 1, index_arr_ptr);
        });
        return this._wi.get_f64(this._arr_ptr, indices_arr_ptr);
    }
    get(indices) {
        if (typeof indices == 'number') {
            return this.get_index(indices);
        }
        return new MxNDArray(this._wi, super.get_indices(indices));
    }
    clone() {
        return new MxNDArray(this._wi, this);
    }
}
exports.MxNDArray = MxNDArray;
