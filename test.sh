#!/usr/bin/env bash
# Build and instantiate gen_dir
gradle make_jar 1> /dev/null
gen_dir="./test_gen"
rm -rf ${gen_dir}
mkdir ${gen_dir}
mkdir "${gen_dir}/gen"
mkdir "${gen_dir}/log"
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Constants
CORRECT=()
INCORRECT=()
CORRECT_COUNTER=0
INCORRECT_COUNTER=0
COUNTER=0
TOTAL=`ls -1q ./src/test/features/**/*.m | wc -l`
TOTAL=`echo $TOTAL |sed -e "s/[\t]*//g"`
errorMessage=""
for file in `ls ./src/test/features/**/*.m`; do
    COUNTER=$[$COUNTER+1]
    fname=$(basename $file)
    matfile=${file%.m}
    fbname=${fname%.*}
    res="./runner.sh $file -o ./$gen_dir/gen/$fbname.wat -a \"[DOUBLE&1*1&REAL]\""
#    mat="/Applications/MATLAB_R2017b.app/bin/matlab -nodesktop -nodisplay -nosplash -r \"try;run $matfile(10);catch;end;exit\";"
    ans=`${res} 2>&1`
#    echo "Error: $ans"
    if [ $? -eq 1 ]; then
        log_dirname="${gen_dir}/log/${fname}.log"
        echo "Filename:${fname}\n" > "$log_dirname"
        echo "Benchmark:\n" >> "$log_dirname"
        cat "$file" >> "$log_dirname"
        echo "\nCompile Output:\n" >> "$log_dirname"
        echo "$ans" >> "$log_dirname"
        echo -e "${RED}($COUNTER/$TOTAL) $fbname${NC}"
        INCORRECT[$INCORRECT_COUNTER]=${fname}
        INCORRECT_COUNTER=$[$INCORRECT_COUNTER +1]
    else
        CORRECT[$CORRECT_COUNTER]=${fname}
        echo -e "($COUNTER/$TOTAL) $fbname"
        CORRECT_COUNTER=$[$CORRECT_COUNTER+1]
    fi
done
TOTAL=$[$INCORRECT_COUNTER+$CORRECT_COUNTER]
echo -e "${RED}Incorrect:(${INCORRECT_COUNTER}/${TOTAL})${NC}"
printf 'Benchmarks:%s\n' "${INCORRECT[*]}"
echo -e "${GREEN}Correct:(${CORRECT_COUNTER}/${TOTAL})${NC}"
printf 'Benchmarks:%s\n' "${CORRECT[*]}"
