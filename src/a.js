function compareTriplets(a, b) {
    return a.reduce((acc, aScore, idx)=>{
        if(aScore > b[idx]) acc[0]+=1;
        else if(aScore < b[idx]) acc[1]+=1;
        return acc;
    },[0,0]);
}

console.log(compareTriplets([1,2,3],[3,2,1]));