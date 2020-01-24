if(typeof module == "undefined"){
  module = {};
  module.exports = {};
}

if (typeof TextDecoder === "undefined"){
  function TextDecoder() {
  }
  TextDecoder.prototype.decode = function (octets) {
    var string = "";
    var i = 0;
    while (i < octets.length) {
      var octet = octets[i];
      var bytesNeeded = 0;
      var codePoint = 0;
      if (octet <= 0x7F) {
        bytesNeeded = 0;
        codePoint = octet & 0xFF;
      } else if (octet <= 0xDF) {
        bytesNeeded = 1;
        codePoint = octet & 0x1F;
      } else if (octet <= 0xEF) {
        bytesNeeded = 2;
        codePoint = octet & 0x0F;
      } else if (octet <= 0xF4) {
        bytesNeeded = 3;
        codePoint = octet & 0x07;
      }
      if (octets.length - i - bytesNeeded > 0) {
        var k = 0;
        while (k < bytesNeeded) {
          octet = octets[i + k + 1];
          codePoint = (codePoint << 6) | (octet & 0x3F);
          k += 1;
        }
      } else {
        codePoint = 0xFFFD;
        bytesNeeded = octets.length - i;
      }
      string += String.fromCodePoint(codePoint);
      i += bytesNeeded + 1;
    }
    return string
  };
}
// Object to fit to WebAssembly Runtime
let Module = {};

if(typeof console === "undefined" ) {
    console = {};
    console.log = print;
    console.warn = print;
}else if(typeof console.log === "undefined"){
    console.log = print;
}else if(typeof console.warn === "undefined"){
	console.warn = print;
}
// Polyfill
if(typeof performance === "undefined" ) {
    performance = {};
    performance.now = Date.now;
}
if(typeof performance.now === "undefined"){
    performance.now = Date.now;
}
/**
 * MEMORY CONSTANTS
 */
const WASM_PAGE_SIZE = 65536;
const MIN_TOTAL_MEMORY = 16777216;
let TOTAL_STACK = 5242880; 
let TOTAL_MEMORY = 1073676288; // 

let HEAP,
/** @type {ArrayBuffer} */
  buffer,
/** @type {Int8Array} */
  HEAP8,
/** @type {Uint8Array} */
  HEAPU8,
/** @type {Int16Array} */
  HEAP16,
/** @type {Uint16Array} */
  HEAPU16,
/** @type {Int32Array} */
  HEAP32,
/** @type {Uint32Array} */
  HEAPU32,
/** @type {Float32Array} */
  HEAPF32,
/** @type {Float64Array} */
  HEAPF64;

// Memory management values
let STATIC_BASE, STATICTOP, staticSealed; // static area
let STACK_BASE, STACKTOP, STACK_MAX; // stack area
let DYNAMIC_BASE, DYNAMICTOP_PTR; // dynamic area handled by sbrk
// Initializing memory constants
STATIC_BASE = STATICTOP = STACK_BASE = STACKTOP = STACK_MAX = DYNAMIC_BASE = DYNAMICTOP_PTR = 0;
staticSealed = false;



// Set the reallocBuffer function used by `growMemory` which is called my _sbrk

Module['reallocBuffer'] = wasmReallocBuffer;
if (TOTAL_MEMORY < TOTAL_STACK) 
    console.warn('TOTAL_MEMORY should be larger than TOTAL_STACK, was '
        + TOTAL_MEMORY + '! (TOTAL_STACK=' + TOTAL_STACK + ')');

// Initializing buffer
// Use a provided buffer, if there is one, or else allocate a new one
  // Use a WebAssembly memory where available
assert(TOTAL_MEMORY % WASM_PAGE_SIZE === 0);
Module['wasmMemory'] = new WebAssembly.Memory({ 'initial': TOTAL_MEMORY / WASM_PAGE_SIZE});
buffer = Module['wasmMemory'].buffer;
assert(buffer.byteLength === TOTAL_MEMORY);
Module['buffer'] = buffer;
updateGlobalBufferViews();

/**
 * Memory constants
 */
staticSealed = false;
const GLOBAL_BASE = 1024;
const ERROR_LOCATION_WASM = 1520; // This location was generated dynamically by the Emscripten compiler
                                // (Check the `__errno_location()` function in Wasm)
                                // Inside the Wasm code, it contains certain globals and static information
                                // to run malloc/free, stackAlloc, stackRestore. What is saved there exactly,
                                // Since we are only really interested in using the stack and heap, we just have
                                // to make sure we write using malloc/free/stackAlloc/stackRestore.

const STATICBUMP = 1536; // Static size coming from the wasm module memory set-up from Emscripten
const STACK_ALIGN = 16; // Stack is 16 byte aligned as the meaning allocation is normally 16 bytes for mallocs.
STATIC_BASE = GLOBAL_BASE; // Global_base is at 1024, I think in wasm there is some pointers "hardcoded"
                           // in those addresses.
STATICTOP = STATIC_BASE + STATICBUMP; // STATIC END

staticAlloc(1224); // This manually adds the static offset occupied by my WebAssembly module string errors.
                    // Check the data segments in my `matmachjs.wat` module. 
DYNAMICTOP_PTR = staticAlloc(4); // Allocate address to store DYNAMIC_PTR 

STACK_BASE = STACKTOP = alignMemory(STATICTOP);
STACK_MAX = STACK_BASE + TOTAL_STACK;
DYNAMIC_BASE = alignMemory(STACK_MAX);

staticSealed = true; // seal the static portion of memory

HEAP32[DYNAMICTOP_PTR>>2] = DYNAMIC_BASE;

// Sanity check for dynamic base.
assert(DYNAMIC_BASE < TOTAL_MEMORY, "TOTAL_MEMORY not big enough for stack");

/**
 * Aligns memory given a factor, if the factor is not defined, it uses STACK_ALIGN
 * @param {number} size 
 * @param {number} factor 
 */
function alignMemory(size, factor) {
  if (!factor) factor = STACK_ALIGN; // stack alignment (16-byte) by default
  var ret = size = Math.ceil(size / factor) * factor;
  return ret;
}
/**
 * Allocates a given size on the global static, makes sure it is 16 byte aligned
 * @param {number} size 
 */
function staticAlloc(size) {
  assert(!staticSealed);
  var ret = STATICTOP;
  STATICTOP = (STATICTOP + size + 15) & -16;
  return ret;
}


/**
 * Fails with stack overflow error
 * @param {size} allocSize 
 */
function abortStackOverflow(allocSize) {
  abort('Stack overflow! Attempted to allocate ' + allocSize + ' bytes on the stack, but stack has only ' + (STACK_MAX - stackSave() + allocSize) + ' bytes available!');
}
/**
 * Abort function from Emscripten
 * Modified(dherr3): I have removed the printing with decoractors
 * part of it and simple now it simple throws an error.
 * @param {string} what 
 */
function abort(what) {
  if (what !== undefined) {
    console.log(what);
    console.warn(what); // Replace
    what = JSON.stringify(what)
  } else {
    what = '';
  }

  ABORT = true;
  EXITSTATUS = 1;
  const output = new Error(what);
  throw output;
}
/**
 * Assert function used throughout the module
 * @param {boolean} condition 
 * @param {string} text 
 */
function assert(condition, text) {
  if (!condition) {
    abort('Assertion failed: ' + text);
  }
}
/**
 *  Error thrown by Wasm's `_sbrk()`
 */
function abortOnCannotGrowMemory() {
  abort('Out-of-memory: Cannot enlarge memory arrays.');
}

// __errno_location imported from the wasm module. I used `ERROR_LOCATION_WASM` since it is already
// to this value in my wasm module 
Module["___errno_location"] = ERROR_LOCATION_WASM;

/**
 * Sets the error, this is call in 
 * TODO (dherre3): Still need to understand how this works
 * @param {number} value 
 */
function ___setErrNo(value) {
    if (Module['___errno_location']) HEAP32[((Module['___errno_location']())>>2)]=value;
    else console.warn('failed to set errno from JS');
    return value;
} 

/**
 * Updating the big module buffer. Call by `enlargeMemory()`
 * @param {Array<byte>} buf 
 */
function updateGlobalBuffer(buf) {
    Module['buffer'] = buffer = buf;
}

/**
 * Gets total memory for the sytem. Called by wasm module
 */
function getTotalMemory() {
  return TOTAL_MEMORY;
}
/**
 * Updating global buffer views 
 */  
function updateGlobalBufferViews() {
    Module['HEAP8'] = HEAP8 = new Int8Array(buffer);
    Module['HEAP16'] = HEAP16 = new Int16Array(buffer);
    Module['HEAP32'] = HEAP32 = new Int32Array(buffer);
    Module['HEAPU8'] = HEAPU8 = new Uint8Array(buffer);
    Module['HEAPU16'] = HEAPU16 = new Uint16Array(buffer);
    Module['HEAPU32'] = HEAPU32 = new Uint32Array(buffer);
    Module['HEAPF32'] = HEAPF32 = new Float32Array(buffer);
    Module['HEAPF64'] = HEAPF64 = new Float64Array(buffer);
}
/**
 * Size to use vuffer and reallocate
 * @param {number} size 
 */
function wasmReallocBuffer(size) {
    // Align the size to be a WASM_PAGE_SIZE
    size = alignUp(size, WASM_PAGE_SIZE); // round up to wasm page size
    var old = Module['buffer'];
    var oldSize = old.byteLength;
    try {
        var result = Module['wasmMemory'].grow((size - oldSize) / WASM_PAGE_SIZE); // .grow() takes a delta compared to the previous size
        if (result !== (-1 | 0)) {
            // success in native wasm memory growth, get the buffer from the memory
            return Module['buffer'] = Module['wasmMemory'].buffer;
        } else {
            return null;
        }
    } catch(e) {
        console.error('Module.reallocBuffer: Attempted to grow from ' + oldSize  + ' bytes to ' + size + ' bytes, but got error: ' + e);
        return null;
    }
}
/**
 * Aligns memory given a factor and a size. 
 * @param {number} x 
 * @param {number} multiple 
 */
function alignUp(x, multiple) {
    if (x % multiple > 0) {
      x += multiple - (x % multiple);
    }
    return x;
  }
/**
 * Enlarges memory, used by the `_sbrk()` call in WebAssembly
 */
function enlargeMemory() {
    // TOTAL_MEMORY is the current size of the actual array, and DYNAMICTOP is the new top.
    assert(HEAP32[DYNAMICTOP_PTR>>2] > TOTAL_MEMORY); // This function should only ever be called after the ceiling of the dynamic heap has already been bumped to exceed the current total size of the asm.js heap.


    const PAGE_MULTIPLE =  WASM_PAGE_SIZE; // In wasm, heap size must be a multiple of 64KB. In asm.js, they need to be multiples of 16MB.
    const LIMIT = 2147483648 - PAGE_MULTIPLE; // We can do one page short of 2GB as theoretical maximum.

    if (HEAP32[DYNAMICTOP_PTR>>2] > LIMIT) {
        console.warn('Cannot enlarge memory, asked to go up to ' + HEAP32[DYNAMICTOP_PTR>>2] + ' bytes, but the limit is ' + LIMIT + ' bytes!');
        return false;
    }

    let OLD_TOTAL_MEMORY = TOTAL_MEMORY;
    TOTAL_MEMORY = Math.max(TOTAL_MEMORY, MIN_TOTAL_MEMORY); // So the loop below will not be infinite, and minimum asm.js memory size is 16MB.

    while (TOTAL_MEMORY < HEAP32[DYNAMICTOP_PTR>>2]) { // Keep incrementing the heap size as long as it's less than what is requested.
        if (TOTAL_MEMORY <= 536870912) {
            TOTAL_MEMORY = alignUp(2 * TOTAL_MEMORY, PAGE_MULTIPLE); // Simple heuristic: double until 1GB...
        } else {
            TOTAL_MEMORY = Math.min(alignUp((3 * TOTAL_MEMORY + 2147483648) / 4, PAGE_MULTIPLE), LIMIT); // ..., but after that, add smaller increments towards 2GB, which we cannot reach
        }
    }

    const start = Date.now();

    let replacement = Module['reallocBuffer'](TOTAL_MEMORY);
    if (!replacement || replacement.byteLength != TOTAL_MEMORY) {
    console.warn('Failed to grow the heap from ' + OLD_TOTAL_MEMORY + ' bytes to ' + TOTAL_MEMORY + ' bytes, not enough memory!');
    if (replacement) {
        console.warn('Expected to get back a buffer of size ' + TOTAL_MEMORY + ' bytes, but instead got back a buffer of size ' + replacement.byteLength);
    }
    // restore the state to before this call, we failed
    TOTAL_MEMORY = OLD_TOTAL_MEMORY;
    return false;
    }

    // everything worked
    updateGlobalBuffer(replacement);
    updateGlobalBufferViews();

    console.warn('enlarged memory arrays from ' + OLD_TOTAL_MEMORY + ' to ' + TOTAL_MEMORY + ', took ' + (Date.now() - start) + ' ms (has ArrayBuffer.transfer? ' + (!!ArrayBuffer.transfer) + ')');
    return true;
}

/////////////// RUN-TIME MATWABLY SUPPORT////////////////////
/**
 * Prints array of doubles, used by the `disp_M()` function 
 * @param {number} arr_ptr 
 * @param {number} length 
 */
function printArrayDouble(arr_ptr, length) {
    let arr = new Float64Array(Module.wasmMemory.buffer, arr_ptr, length);
    console.log(arr);
}
/**
 * Prints  WebAssembly error, used by the `MatmachJS` library
 * @param {number} offset 
 * @param {number} length 
 */
function printError(offset, length) {
    var bytes = new Uint8Array(Module.wasmMemory.buffer, offset, length);
    var string = new TextDecoder('utf8').decode(bytes);
    throw new Error(string);
}
// Prints a string in memory.
function printString(offset, length) {
    var bytes = new Uint8Array(Module.wasmMemory.buffer, offset, length);
    var string = new TextDecoder('utf8').decode(bytes);
    console.log(string);
}
/**
 * Generates a random normally distributed number, in the future, compile from C and extract wat function
 */
function randn() {
    var u = 0, v = 0;
    while(u === 0) u = Math.random(); //Converting [0,1) to (0,1)
    while(v === 0) v = Math.random();
    return Math.sqrt( -2.0 * Math.log( u ) ) * Math.cos( 2.0 * Math.PI * v );
}
/**
 * Prints a double number and returns.
 * @param {number} number 
 */
function printDouble(number)
{
	console.warn(number);
	return number;
}
/**
 * Helper function ressambling TIC()/TOC() in Matlab
 * @param {number} time 
 */
function printTime(time){
    console.warn(`Elapsed time is ${time/1000} seconds.`);
    return time;
}
/**
 * Helper function to print the header for a macharray.
 * @param {number} arr_ptr MachArray pointer
 * @param {boolean} flag_print_data Boolean indicating whether to print
 * array contents
 */
function printMachArrayHeader(arr_ptr, flag_print_data=false){
  if(arr_ptr!= null && arr_ptr!= 0 ){
    let arr = new Int32Array(Module.wasmMemory.buffer, arr_ptr, 6);
    let attribute = new Uint8Array(Module.wasmMemory.buffer, arr_ptr, 4);
    let length = arr[1];
    let data;
    if(flag_print_data){
      let data_ptr = arr[2];
      data = new Float64Array(Module.wasmMemory.buffer,
         data_ptr,
          length);
    }
    let dimensions = new Float64Array(Module.wasmMemory.buffer, arr[4],arr[3]);
    let strides = new Float64Array(Module.wasmMemory.buffer, arr[5],arr[3]); 
    let gc_info = new Uint8Array(Module.wasmMemory.buffer, arr_ptr+24, 2);
    console.log({
      "id":attribute,
      "offset":arr_ptr,
      "length":arr[1],
      "data":(flag_print_data)?data:"ommitted",
      "ndim":arr[3],
      "dims": dimensions,
      "strides": strides,
      "gc_info":gc_info
    });
  }
}
/**
 * Prints memory information about the program.
 * @param {number} alloc_number Total number of elements allocated
 * @param {number} dealloc_number Total number of elements deallocated
 * @param {number} alloc_memory Total memory requested to $malloc
 * @param {number} dealloc_memory Total memory freed by $free
 * @param {number} time_gc Total time spent by the GC
 * @param {number} total_calls_gc Total number of gc calls
 */
function gcPrintMemoryUsage(alloc_number=0, dealloc_number=0,
    macharray_alloc = 0, 
    total_memory_budget=0, avg_object_size = 0, total_calls_gc = 0,time_gc=0){
    console.warn(JSON.stringify({
      "total_objects_allocated":alloc_number,
      "total_macharray_allocation": macharray_alloc,
      "total_objects_deallocated":dealloc_number,
      "total_objects_not_freed":alloc_number-dealloc_number,
      "total_memory_budget":`${total_memory_budget}(+-${alloc_number}) bytes`,
      "avg_size_per_object": `${avg_object_size} bytes`,
      "gc_total_number_calls": `${total_calls_gc}`,
      "gc_total_time":`${time_gc}s`      
    }).replace("{","[").replace("}","]"));
}

/**
 * DEFINITION OF MODULE IMPORTS
 */
// Helper functions for wasm code
Module.js = {
    "printTime":printTime,
    "printError":printError,
    "printString":printString,
    "printMachArrayHeader":printMachArrayHeader,
    "printDouble":printDouble,
    "printDoubleNumber":printDouble,
    "assert_header":1,
    "print_array_f64":printArrayDouble,
    "time": ()=> performance.now()
};


// Debugging calls.
Module.debug = {
    printMarker:()=>console.log("MARKER")
};
// Helper math functions
Module.math = {
        ones:() => 1,
        rand:() => Math.random(),
        randn:() => randn(),
        randi:(max) => Math.ceil(max*Math.random()),
        zeros:()=> 0,
        isnan: isNaN,
        power:Math.pow,
        sin: Math.sin,
        cos: Math.cos,
        tan: Math.tan,
        sinh: Math.sinh,
        cosh: Math.cosh,
        tanh: Math.tanh,
        exp: Math.exp,
        log: Math.log,
        log2: Math.log2,
        log10: Math.log10,
        pi:()=>Math.PI,
        e:()=>Math.E
};
// Used by memory management in the wasm module
Module.env = { 
        "DYNAMICTOP_PTR": DYNAMICTOP_PTR,
        "STACKTOP": STACKTOP,
        "STACK_MAX": STACK_MAX,
        "memoryBase": STATIC_BASE, // Not necessary for malloc, but may be useful
        "abort": abort, 
        "assert": assert,
        "memory":Module["wasmMemory"],
        "enlargeMemory": enlargeMemory,
        "getTotalMemory": getTotalMemory,
        "abortOnCannotGrowMemory": abortOnCannotGrowMemory,
        "abortStackOverflow": abortStackOverflow,
        "___setErrNo": ___setErrNo,
        "gcPrintMemoryUsage":gcPrintMemoryUsage
};

module.exports = Module;const fs = require("fs");
const file = fs.readFileSync("./drv_make_change.wasm"); // compiled wasm

// Start of compilation and program

// Copy here all the Module definition.
if(typeof Module === "undefined") Module = {};
Module.initiated = false;

async function loader() {
    let wi;
    try {
        wi = await WebAssembly.instantiate(file, Module);
    } catch (err) {
        throw err;
    }
    Module.initiated = true;
    Module.exports =  wi.instance.exports;
    Module.memory = wi.mem;
    wi = wi.instance.exports;
    return Module;
}

Module.matlabModule = loader;
(async ()=>{
    let mod = await loader();
    // console.log(mod.exports);
    mod.exports.drv_make_change_S(100);
})();
