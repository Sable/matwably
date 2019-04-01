"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MxVector_1 = require("./mxarray/MxVector");
exports.MxVector = MxVector_1.MxVector;
const MxNdArray_1 = require("./mxarray/MxNdArray");
exports.MxNDArray = MxNdArray_1.MxNDArray;
class MatlabRuntime {
    constructor(wis) {
        this.started = false;
        this.wasm_exports = wis;
        this.started = true;
    }
    checkForStartedRuntime() {
        if (!this.started) {
            throw new Error("Please initialize Matlab Runtime");
        }
    }
    transpose(arr) {
        if (typeof arr === 'number') {
            return arr;
        }
        else {
            return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.transpose_M(arr.arr_ptr));
        }
    }
    lit(arr) {
        if (typeof arr === 'undefined' || arr === null)
            this.wasm_exports.create_mxarray_empty(0, 0, 0, 0);
        if (arr.length == 0) {
            // create an empty array
            return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.create_mxarray_empty(2, 0, 0, 0));
        }
        else if (arr.length > 0 && typeof arr[0] === 'number') {
            let arr_ = arr;
            let vals = this.wasm_exports.create_mxvector(arr.length);
            arr_.forEach((val, idx) => {
                this.wasm_exports.set_array_index_f64(vals, idx + 1, val);
            });
            return new MxNdArray_1.MxNDArray(this.wasm_exports, vals);
        }
        else {
            let arr_ = arr;
            let rows = arr_.length;
            let cols = arr_[0].length;
            let dimArr = new MxVector_1.MxVector(this.wasm_exports, [rows, cols]);
            let resArr = new MxNdArray_1.MxNDArray(this.wasm_exports, dimArr);
            // create ndarray
            arr_.forEach((dimArr, idxRow) => {
                if (!(dimArr instanceof Array) || dimArr.length !== cols) {
                    throw new Error("Dimensions of matrices being concatenated are not consistent.");
                }
                dimArr.forEach((elem, idxCol) => {
                    resArr.set_index((idxRow + rows * idxCol) + 1, elem);
                });
            });
            return resArr;
        }
    }
    ones(shape) {
        if (shape == undefined)
            return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.ones((new MxVector_1.MxVector(this.wasm_exports, 1).arr_ptr)));
        if (typeof shape === "number") {
            let vec = new MxVector_1.MxVector(this.wasm_exports, [shape]);
            return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.ones(vec.arr_ptr));
        }
        else {
            let input = shape;
            let vec = new MxVector_1.MxVector(this.wasm_exports, input);
            return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.ones(vec.arr_ptr));
        }
    }
    randn(...arg) {
        if (arg.length == 0)
            return 1;
        else {
            if (typeof arg[0] == 'number') {
                let input = arg;
                let vec = new MxVector_1.MxVector(this.wasm_exports, input);
                return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.randn(vec.arr_ptr));
            }
            else {
                let input = arg;
                if (input.length > 1) {
                    throw new Error("Only arrays of array of one dimension accepted in this context");
                }
                let vec = new MxVector_1.MxVector(this.wasm_exports, input[0]);
                return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.randn(vec.arr_ptr));
            }
        }
    }
    isRuntimeStarted() {
        return this.started;
    }
    horzcat(args) {
        return this.concat(2, args);
    }
    vertcat(args) {
        return this.concat(1, args);
    }
    concat(dim, args) {
        this.checkForStartedRuntime();
        let input_vec = this.wasm_exports.create_mxvector(args.length, 5);
        args.forEach((arr, idx) => {
            this.wasm_exports.set_array_index_i32(input_vec, idx + 1, arr.arr_ptr);
        });
        return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.concat(dim, input_vec));
    }
    reshape(arr, dims) {
        return arr.reshape(dims);
    }
    colon(start, stepEnd, end) {
        this.checkForStartedRuntime();
        let input_vec;
        if (typeof end == "undefined") {
            let dim_1 = this.wasm_exports.create_mxvector(1);
            let dim_2 = this.wasm_exports.create_mxvector(1);
            input_vec = this.wasm_exports.create_mxvector(2, 5);
            this.wasm_exports.set_array_index_f64(dim_1, 1, start);
            this.wasm_exports.set_array_index_f64(dim_2, 1, stepEnd);
            this.wasm_exports.set_array_index_i32(input_vec, 1, dim_1);
            this.wasm_exports.set_array_index_i32(input_vec, 2, dim_2);
        }
        else {
            let dim_1 = this.wasm_exports.create_mxvector(1);
            let dim_2 = this.wasm_exports.create_mxvector(1);
            let dim_3 = this.wasm_exports.create_mxvector(1);
            input_vec = this.wasm_exports.create_mxvector(3, 5);
            ;
            this.wasm_exports.set_array_index_f64(dim_1, 1, start);
            this.wasm_exports.set_array_index_f64(dim_2, 1, stepEnd);
            this.wasm_exports.set_array_index_f64(dim_3, 1, end);
            this.wasm_exports.set_array_index_i32(input_vec, 1, dim_1);
            this.wasm_exports.set_array_index_i32(input_vec, 2, dim_2);
            this.wasm_exports.set_array_index_i32(input_vec, 3, dim_3);
        }
        return new MxNdArray_1.MxNDArray(this.wasm_exports, this.wasm_exports.colon(input_vec));
    }
    size(arr) {
        this.checkForStartedRuntime();
        return arr.size();
    }
    numel(arr) {
        this.checkForStartedRuntime();
        return arr.numel();
    }
    length(arr) {
        this.checkForStartedRuntime();
        return arr.length();
    }
    isrow(arr) {
        this.checkForStartedRuntime();
        return arr.isrow();
    }
    iscolumn(arr) {
        this.checkForStartedRuntime();
        return arr.iscolumn();
    }
    ismatrix(arr) {
        this.checkForStartedRuntime();
        return arr.ismatrix();
    }
    isvector(arr) {
        this.checkForStartedRuntime();
        return arr.isvector();
    }
    isempty(arr) {
        this.checkForStartedRuntime();
        return arr.isempty();
    }
    clone(arr) {
        this.checkForStartedRuntime();
        return arr.clone();
    }
}
exports.MatlabRuntime = MatlabRuntime;
