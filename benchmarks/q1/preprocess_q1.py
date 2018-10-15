import pandas as pd
import numpy as np
from classes.preprocess import new_column
import subprocess
# constants for clean up
ubuntu_deer = False
mbp2013 = False
mbp2018 = True

column_names_clean_data = ['benchmark', 'implementation', 'compiler', 'environment', 'mean', 'std',
                           'min', 'max', 'repetitions']
#============================================
# UBUNTU DEER BROWSERS
#============================================
order_columns_environment = ["native","chromium38", "firefox39","chrome63","firefox57","safari11","chromium63"]
def sorting_order_env(env):
    return order_columns_environment.index(env)

order_columns_compilers = ["gcc","g++","browserify","wasm"]
def sorting_order_comp(name):
    return order_columns_compilers.index(name)


# Find files
# Set up find command
if ubuntu_deer:
    # Constants
    OUTPUT_FILE_OLD_WASM_JS = "./data/browsers-old-wasm-js-ubuntu-deer.csv"
    OUTPUT_FILE_OLD_JS = "./data/browsers-old-js-ubuntu-deer.csv"
    OUTPUT_FILE_ALL_BROWSER_DATA = "./data/all-browsers-ubuntu-deer.csv"

    findCMD = 'find /Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer -name "*.csv"'

    out = subprocess.Popen(findCMD, shell=True, stdin=subprocess.PIPE,
                           stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    # Get standard out and error
    (stdout, stderr) = out.communicate()

    # Save found files to list
    filelist = stdout.decode().split()
    print(filelist[0])
    print(filelist[2])
    print(filelist[3])
    print(filelist[4])
    print(filelist[8])
    print(filelist[19])
    print(filelist[12])
    # Preparing c native
    dt_native = pd.read_csv(filelist[0])
    dt_native = new_column(dt_native, size=dt_native["benchmark"].size, position=3, column_name="environment",name="native")
    # print(dt_native)
    # #
    # Preparing Node
    dt_node = pd.read_csv(filelist[2])
    dt_node = new_column(dt_node, size=dt_node["benchmark"].size, position=3, column_name="environment",name="node")
    # print(dt_node)
    # # # Preparing Chromium38
    dt_chromium38 = pd.read_csv(filelist[3])
    dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=1, column_name="implementation",name="js")
    dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=2, column_name="compiler",name="browserify")
    dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=3, column_name="environment",name="chromium38")
    # print(dt_chromium38)
    # Chromium63
    dt_chromium63 = pd.read_csv(filelist[4])
    dt_chromium63 = new_column(dt_chromium63, size=dt_chromium63["benchmark"].size, position=3, column_name="environment",name="chromium63")
    # print(dt_chromium63)

    # Chrome63
    dt_chrome63 = pd.read_csv(filelist[8])
    dt_chrome63 = new_column(dt_chrome63, size=dt_chromium63["benchmark"].size, position=3, column_name="environment",name="chrome63")
    # print(dt_chrome63)

    # Firefox39
    dt_firefox39 = pd.read_csv(filelist[19])
    dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=1, column_name="implementation",name="js")
    dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=2, column_name="compiler",name="browserify")
    dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=3, column_name="environment",name="firefox39")
    # print(dt_firefox39)
    # # Firefox57
    dt_firefox57 = pd.read_csv(filelist[12])
    dt_firefox57 = new_column(dt_firefox57, size=dt_firefox57["benchmark"].size, position=3, column_name="environment",name="firefox57")
    # dt_firefox57 = new_column(dt_firefox57, size=dt_firefox57["benchmark"].size, position=1, column_name="implementation",name="js")
    # dt_firefox57 = new_column(dt_firefox57, size=dt_firefox57["benchmark"].size, position=2, column_name="compiler",name="browserify")
    # print(dt_firefox57)

    column_names_clean_data = ['benchmark', 'implementation', 'compiler', 'environment', 'mean', 'std',
           'min', 'max', 'repetitions']
    dataframes_q1 = [dt_native[column_names_clean_data], dt_chrome63[column_names_clean_data],
                     dt_chromium38[column_names_clean_data], dt_firefox39[column_names_clean_data]
                    , dt_firefox57[column_names_clean_data]]

    #
    # # Saving all browser + native data
    df_total = pd.concat(dataframes_q1)
    df_total["order_env"] = df_total["environment"].apply(sorting_order_env)
    df_total["order_impl"] = df_total["compiler"].apply(sorting_order_comp)
    sorting_order = ["benchmark", "order_env", "order_impl"]
    df_total = df_total.sort_values(by=sorting_order, axis=0).drop(["order_env","order_impl"])

    # print(df_total)
    # df_total.to_csv(OUTPUT_FILE_ALL_BROWSER_DATA, index=False)
    #
    # # Saving old browser comparison data, with webassembly
    df_total_old_browsers = df_total.query("environment != 'chromium63'")
    # df_total_old_browsers = df_total_old_browsers.sort_values(by=['benchmark','implementation','compiler','environment'], axis=0)
    # df_total_old_browsers.to_csv(OUTPUT_FILE_OLD_WASM_JS, index=False)
    #
    # df_total_old_browsers_firefox = df_total_old_browsers.query("environment != 'chrome63' "
    #                                                             "and environment != 'chromium63' "
    #                                                             "and environment != 'chromium38'")
    # df_total_old_browsers_chromium = df_total_old_browsers.query("environment != 'firefox57' and environment != 'firefox39'")
    # #
    # # # Without web assembly
    # # df_total_old_browsers_firefox.to_csv(OUTPUT_FILE_OLD_WASM_JS.replace(".csv","") + "_firefox.csv", index=False)
    # # df_total_old_browsers_chromium.to_csv(OUTPUT_FILE_OLD_WASM_JS.replace(".csv","") + "_chrome.csv", index=False)
    # #
    #
    #
    # # Clean up wasm for old browser purposes
    #
    df_total_old_browsers_no_wasm = df_total_old_browsers.query("compiler != 'wasm'")
    # df_total_old_browsers_no_wasm = df_total_old_browsers_no_wasm.sort_values(by=['benchmark','implementation','compiler','environment'], axis=0)
    # df_total_old_browsers_no_wasm_firefox = df_total_old_browsers_no_wasm.query(
    #     "environment != 'chrome63' and environment != 'chromium38'")
    # df_total_old_browsers_no_wasm_chromium = df_total_old_browsers_no_wasm.query(
    #     "environment != 'firefox57' and environment != 'firefox39'")
    print(OUTPUT_FILE_OLD_JS, df_total_old_browsers_no_wasm)
    df_total_old_browsers_no_wasm.to_csv(OUTPUT_FILE_OLD_JS, index=False)
    # df_total_old_browsers_no_wasm_firefox.to_csv(OUTPUT_FILE_OLD_JS.replace(".csv", "") + "_firefox.csv", index=False)
    # df_total_old_browsers_no_wasm_chromium.to_csv(OUTPUT_FILE_OLD_JS.replace(".csv", "") + "_chrome.csv", index=False)

#============================================
# MacBookPro 2013 BROWSERS
#============================================
if mbp2013:
    # Constants
    OUTPUT_FILE_OLD_WASM_JS = "./data_v2/browsers-old-wasm-js-mbp2013.csv"
    OUTPUT_FILE_OLD_JS = "./data_v2/browsers-old-js-mbp2013.csv"
    OUTPUT_FILE_ALL_BROWSER_DATA= "./data_v2/all-browsers-mbp2013.csv"

    # Set up find command
    findCMD = 'find /Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/mbp2013 -name "*.csv"'
    out = subprocess.Popen(findCMD, shell=True, stdin=subprocess.PIPE,
                           stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    # Get standard out and error
    (stdout, stderr) = out.communicate()

    # Save found files to list
    filelist = stdout.decode().split()
    # Preparing c native
    dt_native = pd.read_csv(filelist[0])
    dt_native = new_column(dt_native, size=dt_native["benchmark"].size, position=3, column_name="environment",name="native")
    # All browsers are together
    dt_all_browsers = pd.read_csv(filelist[2])
    dt_total = pd.concat([dt_native[column_names_clean_data], dt_all_browsers[column_names_clean_data]])
    dt_total["order_env"] = dt_total["environment"].apply(sorting_order_env)
    dt_total["order_impl"] = dt_total["compiler"].apply(sorting_order_comp)
    sorting_order = ["benchmark", "order_env", "order_impl"]
    dt_total = dt_total.sort_values(by=sorting_order, axis=0).drop(["order_env", "order_impl"])
    # dt_total = dt_total.sort_values(by=['benchmark','implementation','compiler','environment'], axis=0)
    dt_total.to_csv(OUTPUT_FILE_ALL_BROWSER_DATA, index=False)

    dt_total_old_browsers = dt_total.query("environment != 'safari11' and environment != 'chromium63'")
    # dt_total_old_browsers = dt_total_old_browsers.sort_values(by=['benchmark','implementation','compiler','environment'], axis=0)
    df_total_old_browsers_firefox = dt_total_old_browsers.query("environment != 'chrome63' and environment != 'chromium38'")
    df_total_old_browsers_chromium = dt_total_old_browsers.query("environment != 'firefox57' and environment != 'firefox39'")

    dt_total_old_browsers.to_csv(OUTPUT_FILE_OLD_WASM_JS, index=False)
    # df_total_old_browsers_firefox.to_csv(OUTPUT_FILE_OLD_WASM_JS.replace(".csv","") + "_firefox.csv", index=False)
    # df_total_old_browsers_chromium.to_csv(OUTPUT_FILE_OLD_WASM_JS.replace(".csv","") + "_chrome.csv", index=False)

    # Clean up wasm for old browser purposes

    df_total_old_browsers_no_wasm = dt_total_old_browsers.query("compiler != 'wasm' ")
    # df_total_old_browsers_no_wasm = df_total_old_browsers_no_wasm.sort_values(by=['benchmark','implementation','compiler','environment'], axis=0)
    df_total_old_browsers_no_wasm_firefox = df_total_old_browsers_no_wasm.query(
        "environment != 'chrome63' and environment != 'chromium38'")
    df_total_old_browsers_no_wasm_chromium = df_total_old_browsers_no_wasm.query(
        "environment != 'firefox57' and environment != 'firefox39'")

    df_total_old_browsers_no_wasm.to_csv(OUTPUT_FILE_OLD_JS, index=False)
    # df_total_old_browsers_no_wasm_firefox.to_csv(OUTPUT_FILE_OLD_JS.replace(".csv", "") + "_firefox.csv", index=False)
    # df_total_old_browsers_no_wasm_chromium.to_csv(OUTPUT_FILE_OLD_JS.replace(".csv", "") + "_chrome.csv", index=False)
#============================================
# MacBookPro 2013 BROWSERS
#============================================
if mbp2018:
    # Constants
    OUTPUT_FILE_OLD_WASM_JS = "./data/browsers-old-wasm-js-mbp2018.csv"
    OUTPUT_FILE_OLD_JS = "./data/browsers-old-js-mbp2018.csv"
    OUTPUT_FILE_ALL_BROWSER_DATA= "./data/all-browsers-mbp2018.csv"

    # Set up find command
    findCMD = 'find /Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/mbp2018 -name "*.csv"'
    out = subprocess.Popen(findCMD, shell=True, stdin=subprocess.PIPE,
                           stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    # Get standard out and error
    (stdout, stderr) = out.communicate()

    # Save found files to list
    filelist = stdout.decode().split()
    print(filelist)
    # Preparing c native
    dt_native = pd.read_csv(filelist[0])
    dt_native = new_column(dt_native, size=dt_native["benchmark"].size, position=3, column_name="environment",name="native")

    dt_chrome63 = pd.read_csv(filelist[1])
    dt_chrome63 = new_column(dt_chrome63, size=dt_chrome63["benchmark"].size, position=3, column_name="environment",
                           name="chrome63")
    # print(dt_chrome63)
    #
    print(filelist[2])
    dt_chromium38 = pd.read_csv(filelist[2])
    dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=1,
                               column_name="implementation",
                               name="js")
    dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=2,
                               column_name="compiler",
                               name="browserify")
    dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=3, column_name="environment",
                             name="chromium38")
    # print(filelist[5])
    dt_firefox39 = pd.read_csv(filelist[5])
    dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=1,
                               column_name="implementation",
                               name="js")
    dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=2,
                               column_name="compiler",
                               name="browserify")
    dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=3, column_name="environment",
                             name="firefox39")


    print(filelist[6])
    dt_firefox57 = pd.read_csv(filelist[6])
    dt_firefox57 = new_column(dt_firefox57, size=dt_firefox57["benchmark"].size, position=3, column_name="environment",
                              name="firefox57")

    dt_total = pd.concat([dt_native[column_names_clean_data], dt_firefox39[column_names_clean_data]
                          ,dt_chrome63[column_names_clean_data],dt_firefox57[column_names_clean_data],
                          dt_chromium38[column_names_clean_data]])
    dt_total["order_env"] = dt_total["environment"].apply(sorting_order_env)
    dt_total["order_impl"] = dt_total["compiler"].apply(sorting_order_comp)
    sorting_order = ["benchmark", "order_env", "order_impl"]
    dt_total = dt_total.sort_values(by=sorting_order, axis=0).drop(["order_env", "order_impl"])


    df_total_old_browsers_no_wasm = dt_total.query("compiler != 'wasm' ")
    print(df_total_old_browsers_no_wasm)

    df_total_old_browsers_no_wasm.to_csv(OUTPUT_FILE_OLD_JS, index=False)
