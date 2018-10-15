#!/usr/bin/env bash
MATJUICE=../run.sh
BUILD_DIR=_BUILD_CI

# Build project
cd ..
./build.sh
cd benchmarks


BENCHMARKS=(
    bbai/drv_babai.m
    bubble/drv_bubble.m
    capr/drv_capr.m
    clos/drv_clos.m
    collatz/drv_collatz.m
    crni/drv_crni.m
    dich/drv_dich.m
    fdtd/drv_fdtd.m
    fft/drv_fft.m
    fiff/drv_fiff.m
    lgdr/drv_lgdr.m
    make_change/drv_make_change.m
    matmul/drv_matmul_p.m
    mcpi/drv_mcpi_p.m
    nb1d/drv_nb1d.m
    numprime/drv_prime.m
)
rm -rf $BUILD_DIR

mkdir -p $BUILD_DIR

for b in ${BENCHMARKS[@]}; do
    basefile=$(basename $b .m)
    jsdrv=$basefile"-wably".js
    htmlfile=$basefile.html
    echo -n "$b... "
#    echo "$MATJUICE  $b  -a \"[DOUBLE&1*1&REAL]\" -o $BUILD_DIR/$basefile.js"
    $MATJUICE  $b  -a "[DOUBLE&1*1&REAL]" -o $BUILD_DIR/$basefile.js > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "OK"
    else
        echo "FAIL"
    fi
done

