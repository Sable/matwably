import pandas as pd
import numpy as np
from analysis.code.classes.plots import Plots
from analysis.code.classes.speedup import histogram_plot
from analysis.code.classes.geomeans import get_geo_means
import matplotlib
import matplotlib.pyplot as plt


# histogram_plot("./data/browsers-history.csv",
#                "../../plots/browsers-new-engine-chrome.png",
#                12, 11, name_plot="New Browser Engine Chrome - Ubuntu-Deer", include_name=False, y_lim=[0, 6]
#                ,legends=["Chromium38-js","Chromium51-js","Chromium57-js","Chromium59-js","Chromium63-js",
#                          "Firefox39-js","Firefox44-js","Firefox49-js","Firefox53-js","Firefox57-js","C"])

dt = pd.read_csv('./data/browsers-history.csv')

dt = dt.query("environment!='firefox59'")
# print(dt)
dt = dt.query("environment!='chrome66'")
geomeans, errors = get_geo_means(dt["benchmark"],dt["mean"],dt["std"],index_baseline=0,speedup=True)

print(geomeans)
N = np.arange(0,24,2)
N[5:12] = N[5:12] + 3
print(N)
fig, ax = plt.subplots()
legends = ["Chromium38-js","Chromium51-js","Chromium57-js","Chromium59-js","Chromium63-js",
          "Firefox39-js","Firefox44-js","Firefox49-js","Firefox53-js","Firefox57-js"]

colors = ['g', 'r', 'c', 'm', 'y','g', 'r', 'c', 'm', 'y','gold','pink']
rects = ()
patterns = ["/", "\\", ".", "x", "o", "+", "|", "*", "0"]

for i in range(len(legends)):
    re = ax.bar((N[i],), (geomeans[i],), 2, color=colors[i], align='center', hatch=patterns[i%5])
    rects = rects + (re[0],)
baseline = ax.plot(np.arange(-0.5,2*len(legends)+2.5,0.5), np.ones(4*len(legends)+6), "k--", linewidth=2)
rects = rects + (baseline[0],)

# ax.set_xticks([1,5.5])
# ax.set_xticklabels(tuple([ "Chrome","Firefox"]), fontsize=14)

plt.xticks(N,  ["Chromium38","Chromium51","Chromium57","Chromium59",
                "Chromium63", "Firefox39","Firefox44","Firefox49",
                "Firefox53","Firefox57"])
locs, labels = plt.xticks()
plt.setp(labels, rotation=80, fontsize=10)
plt.ylim([0,1.25])
ax.set_ylabel("Speedup (relative to C)", fontsize=14)
plt.subplots_adjust( wspace=0, hspace=5)
fig.tight_layout(h_pad=10)
plt.savefig("../../plots/browser-history.pdf",format="pdf")
plt.show()

#
# histogram_plot("./data/browsers-latest-may2018.csv",
#                "../../plots/browsers-latest-may2018.pdf",
#                12, 5, name_plot="New Browser Engine Chrome - Ubuntu-Deer", include_name=False, y_lim=[0, 6]
#                ,legends=["Chromium66-js","Chromium66-js", "Firefox59-js", "Firefox59-wasm","Native-c"])