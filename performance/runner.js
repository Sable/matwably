const args = require("yargs").demandOption("benchmark").argv;
const bench = require(args.benchmark);

if(args.jit){
    if(!bench.hasOwnProperty("runJitted")) throw new Error(`Specified benchmark [${args.benchmark}], must export
    'runJitted' function`);
    bench.runJitted();
}else{
    if(!bench.hasOwnProperty("runSingleIteration")) throw new Error(`Specified benchmark [${args.benchmark}], must export
    'runSingleIteration' function`);
    bench.runSingleIteration();
}