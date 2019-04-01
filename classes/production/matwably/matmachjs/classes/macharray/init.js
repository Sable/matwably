"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MachRuntime_1 = require("./MachRuntime");
(async () => {
    let mr = await MachRuntime_1.MachRuntime.initializeRuntime();
})();

console.log("Error: src, and dest array must have the same number of values".length);