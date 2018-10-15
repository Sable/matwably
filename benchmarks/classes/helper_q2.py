

import pandas as pd
import numpy as np

def get_speed_up_info(data):
    wasm = data.query("implementation == 'c' or implementation == 'cpp'")
    js = data.query("implementation == 'js'")
    speedup = np.divide(js["mean"].values,wasm["mean"].values)
    speedup_std = np.multiply(np.sqrt(np.add((wasm["std"] / wasm["mean"]) ** 2 , (js["std"] / js["mean"]) ** 2)),speedup)

    df = pd.DataFrame({"benchmark": wasm["benchmark"].values, "speedup":speedup,"speedup_std":speedup_std,
                       "environment":wasm["environment"].values})
    return df

def get_speed_up_info_native(name, data):
    native = data.query("environment=='native'")
    if name == 'ubuntu-deer':
        repeat = 2
    else:
        repeat = 3
    native_mean = np.repeat(native["mean"].values,repeat)
    native_std = np.repeat(native["std"].values,repeat)

    rest = data.query("environment != 'native'")
    rest_mean = rest["mean"].values
    rest_std = rest["std"].values

    speedup = np.divide(native_mean, rest_mean)
    speedup_std = np.multiply(np.sqrt(np.add((rest_std / rest_mean) ** 2 , (native_std / native_mean) ** 2)),speedup)

    df = pd.DataFrame({"benchmark": rest["benchmark"].values, "speedup":speedup,"speedup_std":speedup_std,
                       "environment":rest["environment"].values})
    return df