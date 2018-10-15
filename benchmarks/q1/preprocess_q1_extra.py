import pandas as pd
import numpy as np
from analysis.code.classes.preprocess import new_column
import subprocess
# constants for clean up
ubuntu_deer = True
mbp2013 = True


#============================================
# UBUNTU DEER BROWSERS
#============================================
order_columns_environment = ["native","chromium38","chromium51", "chromium57","chromium59","chromium63","chrome66"
                             ,"firefox39","firefox44","firefox49","firefox53","firefox57","firefox59"]
def sorting_order_env(env):
    return order_columns_environment.index(env)

order_columns_compilers = ["gcc","g++","browserify","wasm"]
def sorting_order_comp(name):
    return order_columns_compilers.index(name)
# Constants

# input Filename
filename = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/browser/' \
           'implementationjscompilerbrowserifyplatformubuntu-deerinput-sizemedium (3).csv'
filename_c = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/native/' \
             'platform=ubuntu-deer,environment=native-ubuntu-deer,input-size=medium.csv'

filename_chromium38 = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/browser/' \
             'implementation=js,compiler=browserify,platform=ubuntu-deer,environment=chromium38,input-size=medium.csv'

filename_firefox = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/browser/' \
             'implementationjscompilerbrowserifyplatformubuntu-deerinput-sizemediumenvironmentsfirefox.csv'

filename_firefox57 = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/browser/' \
             'platform=ubuntu-deer,environment=firefox57,input-size=medium.csv'
filename_firefox39 = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/browser/' \
             'implementation=js,compiler=browserify,platform=ubuntu-deer,environment=firefox39,input-size=medium.csv'
# Output filename
OUTPUT_FILE_EXP = "./data/browsers-history.csv"
OUTPUT_FILE_LATEST = "./data/browsers-latest-may2018.csv"

# Preparing c native
dt_native = pd.read_csv(filename_c)
dt_native = new_column(dt_native, size=dt_native["benchmark"].size, position=3, column_name="environment",name="native")

dt_chromium = pd.read_csv(filename)
dt_chromium = new_column(dt_chromium, size=dt_chromium["benchmark"].size, position=1, column_name="implementation",name="js")
dt_chromium = new_column(dt_chromium, size=dt_chromium["benchmark"].size, position=2, column_name="compiler",name="browserify")

dt_chromium = dt_chromium.query("environment!='chromium60'")
dt_chromium = dt_chromium.query("environment!='chromium55'")
dt_chromium = dt_chromium.query("environment!='chromium61'")
dt_chromium = dt_chromium.query("environment!='chromium62'")


# chromium38
dt_chromium38 = pd.read_csv(filename_chromium38)
dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=1, column_name="implementation",name="js")
dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=2, column_name="compiler",name="browserify")
dt_chromium38 = new_column(dt_chromium38, size=dt_chromium38["benchmark"].size, position=2, column_name="environment",name="chromium38")


dt_firefox = pd.read_csv(filename_firefox)
dt_firefox = new_column(dt_firefox, size=dt_firefox["benchmark"].size, position=1, column_name="implementation",name="js")
dt_firefox = new_column(dt_firefox, size=dt_firefox["benchmark"].size, position=2, column_name="compiler",name="browserify")

dt_firefox57 = pd.read_csv(filename_firefox57)
dt_firefox57 = new_column(dt_firefox57, size=dt_firefox57["benchmark"].size, position=2, column_name="compiler",name="browserify")
dt_firefox57 = new_column(dt_firefox57, size=dt_firefox57["benchmark"].size, position=3, column_name="environment",name="firefox57")
dt_firefox57 = dt_firefox57.query("implementation == 'js'")

dt_firefox39 = pd.read_csv(filename_firefox39)
dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=1, column_name="implementation",name="js")
dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=2, column_name="compiler",name="browserify")
dt_firefox39 = new_column(dt_firefox39, size=dt_firefox39["benchmark"].size, position=3, column_name="environment",name="firefox39")


filename_firefox59 = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/browser/' \
             'platformubuntu-deerenvironmentfirefox59input-sizemedium.csv'
filename_chrome66 = '/Users/davidherrera/Documents/Research/ostrich-updated/ecoop18-ostrich/raw-data/ubuntu-deer/browser/' \
             'platformubuntu-deerenvironmentchrome66input-sizemedium.csv'

dt_firefox59 = pd.read_csv(filename_firefox59)
dt_firefox59 = new_column(dt_firefox59, size=dt_firefox59["benchmark"].size, position=2, column_name="environment",name="firefox59")

dt_chrome66 = pd.read_csv(filename_chrome66)
dt_chrome66 = new_column(dt_chrome66, size=dt_chrome66["benchmark"].size, position=2, column_name="environment",name="chrome66")

# Save file for latests browsers
column_names_clean_data = ['benchmark', 'implementation', 'compiler', 'environment', 'mean', 'std',
                           'min', 'max', 'repetitions']
dataframes_q1 = [dt_native[column_names_clean_data],dt_chrome66[column_names_clean_data], dt_firefox59[column_names_clean_data]]
df_total = pd.concat(dataframes_q1)
df_total["order_env"] = df_total["environment"].apply(sorting_order_env)
df_total["order_impl"] = df_total["compiler"].apply(sorting_order_comp)
sorting_order = ["benchmark", "order_env", "order_impl"]
df_total = df_total.sort_values(by=sorting_order, axis=0).drop(["order_env", "order_impl"])
df_total.to_csv(OUTPUT_FILE_LATEST, index=False)


# Saving file for browser comparison throughout
dt_firefox59 = dt_firefox59.query("implementation == 'js'")
dt_chrome66 = dt_chrome66.query("implementation == 'js'")
column_names_clean_data = ['benchmark', 'implementation', 'compiler', 'environment', 'mean', 'std',
                           'min', 'max', 'repetitions']
dataframes_q1 = [dt_native[column_names_clean_data], dt_firefox57[column_names_clean_data],
                 dt_firefox[column_names_clean_data],dt_firefox39[column_names_clean_data],
                 dt_chromium[column_names_clean_data],dt_chromium38[column_names_clean_data],
                 dt_firefox59[column_names_clean_data], dt_chrome66[column_names_clean_data]]

# Saving all browser + native data
df_total = pd.concat(dataframes_q1)
df_total["order_env"] = df_total["environment"].apply(sorting_order_env)
df_total["order_impl"] = df_total["compiler"].apply(sorting_order_comp)
sorting_order = ["benchmark", "order_env", "order_impl"]
df_total = df_total.sort_values(by=sorting_order, axis=0).drop(["order_env", "order_impl"])
df_total.to_csv(OUTPUT_FILE_EXP, index=False)



