import numpy as np

def get_timing_data(benchmarks, data, error,samsung_internet=False):
    name_benchmarks = np.unique(benchmarks)
    total = []
    total_error = []
    for name in name_benchmarks:
        bench_results = data[benchmarks == name].values
        bench_errors = error[benchmarks == name].values
        if name == 'bfs' and samsung_internet:
            bench_results = np.append(bench_results, [0])
            bench_errors = np.append(bench_errors, [0])
        print(bench_results)
        total.append(bench_results)
        total_error.append(bench_errors)
    total_error = np.array(total_error)
    total = np.array(total)
    return total.T, total_error.T