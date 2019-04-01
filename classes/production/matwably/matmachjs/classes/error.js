"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class MatmachError extends Error {
    constructor(msg) {
        super(`Matmach Error: ${msg}`);
    }
}
exports.MatmachError = MatmachError;
class ValueTypeError extends MatmachError {
    constructor(source, expected) {
        super(`Invalid value type, expected: ${expected} got: ${source}`);
    }
}
exports.ValueTypeError = ValueTypeError;
class ArrayValueTypeError extends MatmachError {
    constructor(source, expected) {
        super(`Invalid array type, expected: ${expected} got: ${source}`);
    }
}
exports.ArrayValueTypeError = ArrayValueTypeError;
