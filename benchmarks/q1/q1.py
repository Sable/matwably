import pandas as pd
import numpy as np
from classes.plots import Plots
from classes.speedup import histogram_plot
from scipy.stats.mstats import gmean
include_name = False
individual_plots =  True
geo_mean_analysis =  False
#============================================
# UBUNTU DEER BROWSERS
#============================================
if individual_plots:
    # Individual plot of ubuntu-deer browsers
    # histogram_plot("../clean-data/browsers-old-wasm-js-ubuntu-deer.csv",
    #                "../../plots/browsers-old-wasm-js-ubuntu-deer.png",
    #                12, 7,name_plot="Browser Performance - Ubuntu-Deer", include_name=include_name,y_lim=[0,5])
    histogram_plot("./data/browsers-old-js-ubuntu-deer.csv",
                   "../../plots/browsers-old-js-ubuntu-deer.pdf",
                   12, 5,name_plot="Browser Performance JS - Ubuntu-Deer", include_name=include_name,y_lim=[0,2])
    # # Individual plot of # Firefox and Chrome plots-deer browsers
    # histogram_plot("../clean-data/browsers-old-wasm-js-ubuntu-deer_chrome.csv",
    #                "../../plots/browsers-old-wasm-js-ubuntu-deer_chrome.png",
    #                12, 4,name_plot="Browser Performance Chrome - Ubuntu-Deer", include_name=include_name,y_lim=[0,5])
    # histogram_plot("../clean-data/browsers-old-wasm-js-ubuntu-deer_firefox.csv",
    #                "../../plots/browsers-old-wasm-js-ubuntu-deer_firefox.png",
    #                12, 4, name_plot="Browser Performance Firefox - Ubuntu-Deer", include_name=include_name,y_lim=[0,5])

    #============================================
    # MacBookPro 2013 BROWSERS
    #============================================
    # Invidivudal plot for mbp2013 browsers
    # histogram_plot("../clean-data/browsers-old-wasm-js-mbp2013.csv",
    #                "../../plots/browsers-old-wasm-js-mbp2013.png",
    #                12, 7, name_plot="Browser Performance - mbp-2013", include_name=include_name,y_lim=[0,2])
    # histogram_plot("./data/browsers-old-js-mbp2013.csv",
    #                "../../plots/browsers-old-js-mbp2013.png",
    #                12, 5,name_plot="Browser Performance JS - mbp-2013", include_name=include_name,y_lim=[0,2])
    # # Firefox and Chrome plots for mbp2013 browsers
    # histogram_plot("../clean-data/browsers-old-wasm-js-mbp2013_chrome.csv",
    #                "../../plots/browsers-old-wasm-js-mbp2013_chrome.png",
    #                12, 4,name_plot="Browser Performance Chrome - mbp-2013", include_name=include_name,y_lim=[0,2])
    # histogram_plot("../clean-data/browsers-old-wasm-js-mbp2013_firefox.csv",
    #                "../../plots/browsers-old-wasm-js-mbp2013_firefox.png",
    #                12, 4, name_plot="Browser Performance Firefox - mbp-2013", include_name=include_name,y_lim=[0,2])
    #============================================
    # MacBookPro 2018 BROWSERS
    #============================================
    histogram_plot("./data/browsers-old-js-mbp2018.csv",
                   "../../plots/browsers-old-js-mbp2018.pdf",
                   12, 5,name_plot="Browser Performance JS - mbp-2018", include_name=include_name,y_lim=[0,2])
def gmean_error(geo, data, error, number_benchmarks):
    error_geomean_temp =  np.multiply(np.prod(data,axis=0), np.sqrt(np.sum(np.square(np.divide(error, data)), axis=0)))
    error_geomean = (1. / float(number_benchmarks)) * (
    geo ** ((1. / float(number_benchmarks)) - 1)) * error_geomean_temp
    return error_geomean


def get_geo_means(benchmarks,environment, data, error, index_baseline = None,speedup=True):
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
        print(data)
        if environment[environment == 'chrome63'].size > 0:
            bench_results = np.append( bench_results,np.array([0, 0]))
            bench_errors = np.append( bench_errors,np.array([0, 0]))

        else:
            bench_results = np.append(np.array([0, 0]), bench_results)
            bench_errors = np.append(np.array([0, 0]), bench_errors)
        total.append(bench_results)
        total_error.append(bench_errors)
    # Calculating error

    total_error = np.array(total_error)
    total = np.array(total)
    geo_mean = gmean(total,axis=0)
    geo_mean_error = gmean_error(geo_mean, total, total_error, number_benchmarks)
    return geo_mean, geo_mean_error

def get_geo_mean_array(arr):
    geo_arr = []
    get_arr_err = []
    for df in arr:
        geo, err = get_geo_means(df["benchmark"],df["environment"], df["mean"],df["std"],index_baseline=0)
        geo_arr.append(geo)
        get_arr_err.append(err)
    return np.array(geo_arr).T, np.array(get_arr_err).T


def get_legends(data):
    names = data["environment"] + "-" + data["compiler"]+ "-" + data["implementation"]
    names = names.apply(lambda a: a.replace("wasm-cpp","wasm").replace("gcc-c","c").replace("g++-cpp","c").replace("browserify-js","js")
                        .replace("wasm-c", "wasm"))
    return np.unique(names)
# Firefox and Chrome geo-means for both desktops
if geo_mean_analysis:
    df_firefox_ubuntu_deer = pd.read_csv("../clean-data/browsers-old-js-ubuntu-deer_firefox.csv")
    df_chrome_ubuntu_deer = pd.read_csv("../clean-data/browsers-old-js-ubuntu-deer_chrome.csv")

    df_firefox_mbp2013 = pd.read_csv("../clean-data/browsers-old-js-mbp2013_firefox.csv")
    df_chrome_mbp2013 = pd.read_csv("../clean-data/browsers-old-js-mbp2013_chrome.csv")
    geo_means_old_browsers, geo_mean_error_browsers_firefox = get_geo_mean_array(
        [df_chrome_ubuntu_deer, df_chrome_mbp2013,df_firefox_ubuntu_deer, df_firefox_mbp2013])
    # geo_means_old_browsers_firefox, geo_mean_error_browsers_firefox = get_geo_mean_array(
    #                                 [df_firefox_ubuntu_deer, df_firefox_mbp2013])
    # geo_means_old_browsers_chrome, geo_mean_error_browsers_chrome = get_geo_mean_array(
    #                                 [df_chrome_ubuntu_deer,df_chrome_mbp2013])
    print(geo_means_old_browsers)

    # x_ticks_firefox = ["firefox-ubuntu-deer", "firefox-mbp2013"]
    # x_ticks_chrome = ["chrome-ubuntu-deer", "chrome-mbp2013"]
    x_ticks = ["chrome-ubuntu-deer", "chrome-mbp2013","firefox-ubuntu-deer", "firefox-mbp2013"]
    legends_firefox = get_legends(df_firefox_ubuntu_deer)
    legends_chrome = get_legends(df_chrome_ubuntu_deer)
    legends = ["Chromium38-js","Chrome63-js","Firefox39-js","Firefox57-js","C"]


    # Plots.plot_v2(  y = geo_means_old_browsers_firefox,
    #                 y_error = geo_mean_error_browsers_firefox,
    #                 output_file="../../plots/geometric_mean_old_browsers_firefox.png",
    #                 x_tick=x_ticks_firefox, title="Geometric mean accross platforms - Firefox",
    #                 legends=legends_firefox,random_colors=False,
    #                 y_label="Speedup", y_lim=[0,2], geo_mean=False, move_xtick = 0.5)
    # Plots.plot_v2(y=geo_means_old_browsers_chrome,
    #               y_error=geo_mean_error_browsers_chrome,
    #               output_file="../../plots/geometric_mean_old_browsers_chrome.png",
    #               x_tick=x_ticks_chrome, title="Geometric mean accross platforms - Chrome",
    #               legends=legends_chrome, random_colors=False,
    #               y_label="Speedup", y_lim=[0, 2], geo_mean=False,move_xtick = 0.5)
    print(legends_firefox, geo_means_old_browsers)

    # Plots.plot_v2(y=geo_means_old_browsers,
    #               output_file="../../plots/geomean-old-browsers.png",
    #               x_tick=x_ticks, title=None,
    #               legends=legends, random_colors=False,width=0.3,font_size=15,
    #               y_label="Speedup (relative to c)", y_lim=[0, 2], geo_mean=False,move_xtick = 0.5,baseline_off=5, move=1,q2b=True)
    #
    # #


# histogram_plot("../clean-data/browsers-ubuntu-deer.csv", "../../plots/browsers-ubuntu-deer-no-error.png", 12, 7, error_bars=False)
# histogram_plot("../clean-data/old-browsers-ubuntu-deer.csv", "../../plots/old-browsers-ubuntu-deer-no-error.png", 12, 5, error_bars=False)

