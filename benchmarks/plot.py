import pandas as pd
import numpy as np
from classes.speedup import histogram_plot
from classes.preprocess import new_column
import matplotlib.pyplot as plt
from  scipy.stats.mstats import gmean


dt = pd.read_csv("./results-v8.9.4.csv")
dt = new_column(dt, size=dt["benchmark"].size, position=2, column_name="environment",
                           name="node")
dt = new_column(dt, size=dt["benchmark"].size, position=3, column_name="compiler",
                           name="Matjuice-v2")
print(dt)
js = dt.query("implementation=='js'")
wasm = dt.query("implementation=='wasm'")
mean_JS = np.array(js["mean"])
mean_WASM = np.array(wasm["mean"])
res = np.divide(mean_JS, mean_WASM)
print(res, np.mean(res))
geo = gmean(res)
res = np.append(res,geo)
ind = np.arange(12)
# wasm speed up over js
print( np.unique(dt["benchmark"]))
print(np.array(dt["benchmark"]))
xtick= ['drv_clos', 'drv_collatz', 'drv_fdtd',
 'drv_fft',  'drv_fiff', 'drv_matmul_p',
 'drv_mcpi_p' , 'drv_prime' ,'drv_babai','drv_bubble', 'drv_capr']
xtick2 = np.array(['drv_collatz' , 'drv_fdtd' ,'drv_fft',  'drv_fiff' , 'drv_matmul_p',
 'drv_mcpi_p' , 'drv_prime', 'drv_babai', 'drv_bubble','drv_capr' ,'drv_clos','geomean'])
colors = ['g', 'r', 'c', 'm', 'y', 'b', 'k', 'maroon', 'lightpink', 'teal']
fig, ax = plt.subplots(figsize=(15, 10))
re1 = ax.bar(np.arange(12),res)
i = 0
for re in re1:
    re.set_facecolor(colors[i%10])
    i+=1
baseline = ax.plot(np.arange(13), np.ones(13), "k--", linewidth=2)

rects = ()  # Subplots 'array'

rects = rects + (re1[0],)
rects = rects + (baseline[0],)
ax.set_ylabel('Speedup of wasm (relative to JS)', fontsize=20)
plt.xticks(ind, xtick,rotation=80)
plt.savefig("./speedup-v8.9.4.pdf",bbox_inches='tight', format="pdf")
plt.show()
