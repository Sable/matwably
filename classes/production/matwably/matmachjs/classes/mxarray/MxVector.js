"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MxArray_1 = require("./MxArray");
const MxNdArray_1 = require("./MxNdArray");
class MxVector extends MxArray_1.MxArray {
    constructor(wi, array, simple_type = 0, class_type = 0, column = false, order = 0) {
        super();
        this._wi = wi;
        if (typeof array != "undefined") {
            if (typeof array == 'number') {
                this._arr_ptr = this._wi.create_mxvector(array, simple_type, class_type, column, order);
            }
            else if (array instanceof MxVector) {
                this._arr_ptr = this._wi.clone(array._arr_ptr);
            }
            else {
                // if ( array.length == 1 ) {
                //     this._arr_ptr = this._wi.create_mxvector(array.length, simple_type, class_type, complex, column, byte_size);
                // }
                this._arr_ptr = this._wi.create_mxvector(array.length, simple_type, class_type, column, order);
                array.forEach((val, idx) => {
                    this._wi.set_array_index_f64(this._arr_ptr, idx + 1, val);
                });
            }
        }
    }
    get_indices(indices) {
        return new MxVector(this._wi, super.get_indices(indices));
    }
    get(indices) {
        return new MxVector(this._wi, super.get(indices));
    }
    clone() {
        return new MxVector(this._wi, this);
    }
    reshape(new_dimensions) {
        let dim_ptr = this._wi.create_mxvector(new_dimensions.length);
        new_dimensions.forEach((item, idx) => {
            this._wi.set_array_index_f64(dim_ptr, idx + 1, item);
        });
        return new MxNdArray_1.MxNDArray(this._wi, this._wi.reshape(this._arr_ptr, dim_ptr));
    }
    size() {
        return new MxNdArray_1.MxNDArray(this._wi, super.size());
    }
}
exports.MxVector = MxVector;
