

module.exports = {
    mean_bigint
};

function mean_bigint(arr) {
    if(arr.constructor !== Array) throw new Error("Incorrect type");
    if(arr.length === 0) throw  new Error("Cannot return mean of empty array");
    let sum = BigInt(0);
    arr.forEach((num)=>{
        if(typeof num !== "bigint") throw new Error("Type of elements must be 'bigint'");
        sum+=num;
    });
    return sum / BigInt(arr.length);
}

