# -*- coding: utf-8 -*-

import glob
import time
import subprocess
import os
import pandas as pd
import numpy as np
import datetime
N = 10

BENCHMARKS = [
    "drv_clos",
    "drv_collatz",
    "drv_crni",
    "drv_dich",
    "drv_fdtd",
    "drv_fft",
    "drv_fiff",
    "drv_lgdr",
    "drv_matmul_p",
    "drv_mcpi_p",
    "drv_prime",
    "drv_babai",
    "drv_bubble",
    "drv_capr"
]
ts = pd.to_datetime(str(np.datetime64(datetime.datetime.now())))
d = ts.strftime('%Y.%m.%d')
dt = pd.DataFrame([],columns=["benchmark","mean","std","kernel_time_mean","kernel_time_std"])
dt.to_csv("./results-js-wasm-"+d+".csv", index=False)
BUILD_DIRS = [ "_BUILD_CI"]
TOTAL = []
for b in BENCHMARKS:
    LINE = [b]
    times = [0 for i in range(N)]
    kernel_times = [0 for i in range(N)]
    copy_counts = -1
    for dir in BUILD_DIRS:
        os.chdir(dir)
        for i in range(N):
            print(i)
            t1 = time.time()
            output = subprocess.check_output(["node", b + ".js"], stderr=subprocess.STDOUT)
            t2 = time.time()
            times[i] = t2 - t1
            lines = output.split()
            lines_float = [float(line) for line in lines]
            kernel_times[i] = lines_float
            copy_counts = float(lines[-1])
        LINE.append(np.round(np.mean(times), decimals=4))
        LINE.append(np.round(np.std(times), decimals=4))
        LINE.append(np.round(np.mean(kernel_times),decimals=4))
        LINE.append(np.round(np.std(kernel_times),decimals=4))
        os.chdir("..")
        TOTAL.append(LINE)
        dt = pd.DataFrame([LINE],columns=["benchmark","mean","std","kernel_time_mean","kernel_time_std"])
        dt.to_csv("./results-js-wasm-"+d+".csv", index=False, header=False, mode="a")
        print(LINE)
# dt = pd.DataFrame(TOTAL,columns=["benchmark","mean","std","kernel_time_mean","kernel_time_std"])
# dt.to_csv("./results.csv", index=False)
    # print(r"%s & %.2f $\pm$ %.2f & %f & %.2f $\pm$ %.2f & %f \\" % tuple(LINE))
