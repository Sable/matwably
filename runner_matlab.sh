#!/usr/bin/env bash
# $1 file
# $2 Input
file=$(ls $1)
matfile=${file%.m}
#echo "$matfile"
#
mat="/Applications/MATLAB_R2017b.app/bin/matlab -nodesktop -nodisplay -nosplash -r \"try;run $matfile($2);catch;end;exit\";"
#echo "$mat"
a=$(eval $mat)
b=$(echo "$a" | tail -n +10 | tr -d '\n' | sed -e "s/ /,/g" -e "s/,[,]*/,/g" -e "s/^,//")
echo "$b"
