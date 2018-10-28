#!/usr/bin/env bash

rm *.wasm *.wat
gradle make_jar 1> /dev/null

COUNTER=0
TOTAL=0
for file in `ls ./src/test/features/**/*.m`; do
    TOTAL=$[$TOTAL +1]
    fname=$(basename $file)
    matfile=${file%.m}
    fbname=${fname%.*}
    echo "PROGRAM: $fbname"
    res="./runner.sh $file -o ./$fbname.wat -a \"[DOUBLE&1*1&REAL]\""
#    mat="/Applications/MATLAB_R2017b.app/bin/matlab -nodesktop -nodisplay -nosplash -r \"try;run $matfile(10);catch;end;exit\";"
    ans=$(eval $res) &> /dev/null
    if [ $? -eq 1 ]; then
        echo "Error: $ans"
        COUNTER=$[$COUNTER +1]
    fi
#    a=$(eval $mat)
#    b=$(echo "$a" | tail -n +10 | tr -d '\n' | sed -e "s/ /,/g" -e "s/,[,]*/,/g" -e "s/^,//")
#    echo "$b"
done