#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Wed Sep 27 00:02:47 2017

@author: David Herrera
"""

import pandas as pd
import numpy as np
import matplotlib
matplotlib.rcParams.update({'font.size': 15})
import matplotlib.pyplot as plt
import decimal as de
import math
from scipy.stats.mstats import gmean


# ==============================================================================
# Helper Functions
# ==============================================================================

def nthRoot(n, x):
    """ Returns the nth root of a number """
    seq_start = 1.0  # sequence starting value
    counter = 0  # initialize the generator counter to zero

    if x < 0:
        raise ValueError("Cannot compute a Square root on a negative number")
    elif n == 0:
        raise ValueError(" Cannot compute 0 root of a number")

    while counter < 700:
        yield seq_start  # return nthRoot(x)
        # compute the next sequence term (Xn+1)
        seq_start = 1 / float(n) * ((n - 1) * float(seq_start) + x / (float(seq_start) ** (n - 1)))
        counter += 1


def initializeHistogramData(numberOfArtifacts):
    init = []
    err = []
    for i in range(numberOfArtifacts):
        init.append([])
        err.append([])
    return init, err

seq_list = []
def findRoot(nth, number):
    for i in nthRoot(nth, number):
        seq_list.append(i)  # append the list

        if seq_list.count(i) > 1:
            seq_list.remove(i)
    if number == 0:
        return int(de.Decimal(repr(i)).normalize())
    else:
        # Display the nth root (x)
        return de.Decimal(repr(i)).normalize()


err2D = lambda z, x, y, errx, erry: math.fabs(z) * math.sqrt(
    (errx / x) ** 2 + (erry / y) ** 2)  # Calculates error of 2D vectors

# ==============================================================================
# Plotting Script
# ==============================================================================
"""
    Input is a csv file that contains the same format as the wu-wei benchmark report,
    except for the std, where it is actually is the absolute std rather than the relative
    one. 
    The csv is sorted by the first column, then the second column within the first column 
    group etc. Lastly, the script assumes the baseline configuration is in the 0th row and 
    then in the multiples of that zeroth configuration.
"""

def histogram_plot(input_file, output_file, bench_number, artifact_number, random_colors=False,
                   name_plot="",include_name=False,error_bars=True,y_lim=None, geo_mean = True, legend_labels = []):
    # ==============================================================================
    # Parameters
    # ==============================================================================
    # FILEPATH = '../clean-data/browsers-ubuntu-deer.csv'
    # BENCHMARK_NUMBER = 12
    # NUMBER_ARTIFACTS = 7  # Number of configurations, i.e. c-wasm-native, c-wasm-chrome etc.
    # # NAME_PLOT = ""  # Title for plot
    # OUTPUT_FILENAME = '../../plots/browsers-ubuntu-deer.png'
    FILEPATH = input_file
    BENCHMARK_NUMBER = bench_number
    NUMBER_ARTIFACTS = artifact_number  # Number of configurations, i.e. c-wasm-native, c-wasm-chrome etc.
    NAME_PLOT = name_plot  # Title for plot
    OUTPUT_FILENAME = output_file

    # ==============================================================================
    # Start of plot
    # ==============================================================================
    df = pd.read_csv(FILEPATH)  # Accepts csv file of wu-wei output
    init, err = initializeHistogramData(NUMBER_ARTIFACTS)
    legends = []
    xtick = []
    seq_list = []

    # ith variable always points to baseline
    # Preparing points for histogram
    for i in range(df.shape[0]):
        i *= NUMBER_ARTIFACTS # Since we advance k in an inner loop, i.e. loops per each benchmark
        if i < NUMBER_ARTIFACTS:
            # Initialize legends, one for each configuration
            for j in range(NUMBER_ARTIFACTS - 1):
                legends.append(
                    df["environment"][i + j + 1] + "-" + df["compiler"][i + j + 1] + "-" + df["implementation"][i + j + 1])
            legends.append(df["environment"][i] + "-" + df["compiler"][i] + "-" + df["implementation"][i])
        if i < BENCHMARK_NUMBER * NUMBER_ARTIFACTS - NUMBER_ARTIFACTS + 1:
            # Input means and absolute errors calculated by using err2D() function
            for k in range(NUMBER_ARTIFACTS - 1):
                init[k].append(df["mean"][i] / df["mean"][i + k + 1])
                err[k].append(err2D(df["mean"][i] / df["mean"][i + k + 1],
                                    df["mean"][i + k + 1], df["mean"][i],
                                    df["std"][i + k + 1], df["std"][i]))
            xtick.append(df["benchmark"][i + 1]) # Determines x-tick labels
    N = BENCHMARK_NUMBER
    if geo_mean:
        # Adding geo mean
        for h in range(NUMBER_ARTIFACTS - 1):
            init[h].append(gmean(init[h]))
            err[h].append(0)
        xtick.append("geomean")
        N += 1
    # Constants for plot
    print("INIT")
    print(init)

    legends = ["Chromium38-js","Firefox39-js","Chrome63-js","Firefox57-js","C"]

    # legends = ["Chrome63-wasm","Firefox57-wasm","Safari11-wasm","C"]
    if len(legend_labels) > 0: legends = legend_labels
    if NUMBER_ARTIFACTS > 6:
        width = 0.08 # width of bars
    else:
        width = 0.20
    fig, ax = plt.subplots(figsize=(15, 10))  # Create subplots figure
    ind = np.arange(N)
    # Initializing colors
    if random_colors:
        colors = []
        for h in range(NUMBER_ARTIFACTS - 1): colors.append(np.random.rand(3, ))
    else: colors = ['g', 'r', 'c', 'm', 'y', 'b', 'k', 'maroon','lightpink','teal']
    # colors = ['darkorchid','y','darkorange' ,'deepskyblue', 'deeppink',  'gold']
    patterns = ["/", "\\", ".", "x", "o","+", "|", "*", "0"]
    rects = () # Subplots 'array'
    # Plotting
    for h in range(NUMBER_ARTIFACTS - 1):
        pos = ind + h * width
        if geo_mean: pos[ind.size - 1] = pos[ind.size - 1] + 0.5
        if error_bars:
            re = ax.bar(pos, tuple(init[h]), width, color=colors[h],hatch=patterns[h],  yerr=tuple(err[h]))
        else:
            re = ax.bar(pos, tuple(init[h]), width, color=colors[h], hatch=patterns[h])
        rects = rects + (re[0],)
    baseline = ax.plot(np.arange(N + 2), np.ones(N + 2), "k--", linewidth=2)
    rects = rects + (baseline[0],)
    ax.set_ylabel('Speedup (relative to C)',fontsize=20)
    if include_name:
        ax.set_title(NAME_PLOT)
    print()
    ind = ind + width * 2 - 0.1
    ind[ind.size-1] = ind[len(ind)-1] + 0.5
    ax.set_xticks(ind)
    ax.set_xticklabels(tuple(xtick), fontsize=18, rotation=40)
    ax.legend(rects, tuple(legends), loc="upper right",fontsize=20)

    if y_lim is not None:
        plt.ylim(y_lim)
    plt.savefig(OUTPUT_FILENAME, bbox_inches='tight' ,format="pdf")
    plt.show()
