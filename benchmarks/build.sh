#!/bin/bash

RUNNER=../run.sh
BUILD_DIR="_NEW_BUILD"

if [ ! -z $BUILD_DIR ];then
    rm  $BUILD_DIR
fi

mkdir $BUILD_DIR

HTML_TEMPLATE=`cat <<EOF
<html>
  <head>
    <script type="text/javascript" src="SOURCE_FILE"></script>
  </head>
</html>
EOF`


BENCHMARKS=(
    bbai/drv_babai.m
    bubble/drv_bubble.m
    capr/drv_capr.m
    clos/drv_clos.m
    collatz/drv_collatz.m
    createlhs/drv_createlhs.m
    crni/drv_crni.m
    dich/drv_dich.m
    fdtd/drv_fdtd.m
    fft/drv_fft.m
    fiff/drv_fiff.m
    lgdr/drv_lgdr.m
    make_change_dyn/drv_make_change.m
    matmul/drv_matmul_p.m
    mcpi/drv_mcpi_p.m
    nb1d/drv_nb1d.m
    numprime/drv_prime.m
)
#rm -rf $BUILD_DIR

mkdir -p $BUILD_DIR

for b in ${BENCHMARKS[@]}; do
    basefile=$(basename $b .m)
    jsdrv=$basefile"-wably".js
    outfile=$basefile.js
    htmlfile=$basefile.html
    echo -n "$b... "
    $RUNNER  $b -a "\"[DOUBLE&1*1&REAL]\"" -o $BUILD_DIR/$outfile &> /dev/null
    if [ $? -eq 0 ]; then
        echo "OK"
        echo "$HTML_TEMPLATE" > $BUILD_DIR/$htmlfile
        temp=$BUILD_DIR/$htmlfile
        echo "sed -i 's/SOURCE_FILE/$outfile/' $BUILD_DIR/$htmlfile"
    else
        echo "FAIL"
    fi
done
