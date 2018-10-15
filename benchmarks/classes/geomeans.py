import pandas as pd
import numpy as np
from analysis.code.classes.plots import Plots
from analysis.code.classes.speedup import histogram_plot, findRoot
from scipy.stats.mstats import gmean


def gmean_error(geo, data, error, number_benchmarks):
    error_geomean_temp =  np.multiply(np.prod(data,axis=0), np.sqrt(np.sum(np.square(np.divide(error, data)), axis=0)))
    return error_geomean_temp

def get_geo_means(benchmarks, data, error, index_baseline = None,speedup=True):
    name_benchmarks = np.unique(benchmarks)
    number_benchmarks = len(name_benchmarks)
    total = []
    total_error = []
    for name in name_benchmarks:
        bench_results = data[benchmarks == name].values
        bench_errors = error[benchmarks == name].values
        if index_baseline is not None:
            baseline = bench_results[index_baseline]
            baseline_err = bench_errors[index_baseline]
            bench_results = np.hstack([bench_results[:index_baseline], bench_results[(index_baseline+1):]])
            bench_errors = np.hstack([bench_errors[:index_baseline], bench_errors[(index_baseline+1):]])
            bench_errors = np.multiply(np.sqrt(np.add((bench_errors / bench_results) ** 2,
                                                        (baseline_err / baseline) ** 2)),bench_results)
            if speedup:
                bench_results = baseline / bench_results
            else:
                bench_results =  bench_results / baseline
        total.append(bench_results)
        total_error.append(bench_errors)
    # Calculating error

    total_error = np.array(total_error)
    total = np.array(total)
    geo_mean = gmean(total,axis=0)
    geo_mean_error = gmean_error(geo_mean, total, total_error, number_benchmarks)
    return geo_mean, geo_mean_error

def get_geo_mean_array( arr):
    geo_arr = []
    get_arr_err = []
    for df in arr:
        geo, err = get_geo_means(df["benchmark"],df["mean"],df["std"],index_baseline=0)
        geo_arr.append(geo)
        get_arr_err.append(err)
    return np.array(geo_arr).T, np.array(get_arr_err).T

