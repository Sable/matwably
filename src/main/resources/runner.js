(async ()=>{
    let mod = await loader();
    // console.log(mod.exports);
    mod.exports.%s(%s);
})();