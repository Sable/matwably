// import {IMXObject} from "../interfaces/IMXObject";
// declare var memory:any;
// // declare var this.wi:any;
// export class NdArray extends Float64Array implements IMXObject{
//     private wi: any;
//     public arr_ptr: number;
//     private _buffer:Buffer;
//     constructor(wi:any, mxarray: number){
//         super(wi.memory.buffer, wi.mxarray_core_get_array_ptr(mxarray),wi.numel(mxarray));
//         this._buffer = wi.memory.buffer;
//         this.wi = wi;
//         this.arr_ptr = mxarray;
//     }
//     public get_indices(indices:Array<Array<number>>|number) {
//         if(this.isArrPtr(indices)){
//             return new NdArray(this.wi, this.wi.get_f64(this.arr_ptr,indices));
//         }else{
//             let indices_arr_ptr = this.wi.create_mxvector(indices.length, 5);// Create mxvector this.with int type
//             indices.forEach((dimArr,indDim)=>{
//                 let index_arr_ptr = this.wi.create_mxvector(dimArr.length);
//                 dimArr.forEach((val, indVal)=>{
//                     this.wi.set_array_index_f64(index_arr_ptr, indVal+1, val);
//                 });
//                 this.wi.set_array_index_i32(indices_arr_ptr, indDim+1, index_arr_ptr);
//                 return new NdArray(this.wi, this.wi.get_f64(this.arr_ptr,indices_arr_ptr));
//             });
//         }
//     }
//     public set_indices(indices:Array<Array<number>>|number,values:Array<number>|number) {
//         let indices_arr_ptr: number;
//         let indices_val_arr_ptr: number;
//         if (this.isArrPtr(indices)){
//             indices_arr_ptr = indices;
//         }else{
//             indices_arr_ptr = this.wi.create_mxvector(indices.length, 5);// Create mxvector this.with int type
//             indices.forEach((dimArr,indDim)=>{
//                 let index_arr_ptr = this.wi.create_mxvector(dimArr.length);
//                 this.wi.set_array_index_i32(indices_arr_ptr,indDim+1, index_arr_ptr);
//                 dimArr.forEach((val, indVal)=>{
//                     this.wi.set_array_index_f64(index_arr_ptr, indVal+1, val);
//                 });
//             });
//         }
//         if(this.isArrPtr(values)){
//             indices_val_arr_ptr = values;
//         }else{
//             indices_val_arr_ptr = this.wi.create_mxvector(values.length);
//             values.forEach((val, ind)=>{
//                 this.wi.set_array_index_f64(indices_val_arr_ptr,ind+1,val );
//             });
//         }
//         this.wi.set_f64(this.arr_ptr, indices_arr_ptr, indices_val_arr_ptr);
//     }
//     numel(): number {
//         return this.wi.numel(this.arr_ptr);
//     }
//     ndims(): number {
//         return this.wi.ndims(this.arr_ptr);
//     }
//     isrow(): boolean {
//         return this.wi.isrow(this.arr_ptr);
//     }
//     iscolumn(): boolean {
//         return this.wi.iscolumn(this.arr_ptr);
//     }
//     ismatrix(): boolean {
//         return this.wi.ismatrix(this.arr_ptr);
//     }
//     isvector(): boolean {
//         return this.wi.isvector(this.arr_ptr);
//     }
//     isempty(): boolean {
//         return this.wi.isempty(this.arr_ptr);
//     }
//     isArrPtr(x: any): x is number {
//         return typeof x === "number";
//     }
//     isArrayVector(x: any): x is number[][]{
//         return Array.isArray(x) && ((x.length>0&&Array.isArray(x[0]))|| true);
//     }
//     isArrayNumber(x: any): x is number[]{
//         return Array.isArray(x) && ((x.length>0&&typeof x[0] === 'number')|| true);
//     }
// }
