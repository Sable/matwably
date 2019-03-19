#!/usr/bin/env bash
MATLAB_COMMAND="/Applications/MATLAB_R2017b.app/bin/matlab"
file=$1
if [ $2 -eq 0 ]
  then
  $2 = 10
fi
matfile=${file%.m}
mat="/Applications/MATLAB_R2017b.app/bin/matlab -nodesktop -nodisplay -nosplash -r \"try;run $matfile($2);catch;end;exit\";"
echo "$mat"
res=`eval ${mat} 2>&1`
if [ $? -eq 1 ]; then
    echo "Error:${res}"
else
    echo "${res}"
fi