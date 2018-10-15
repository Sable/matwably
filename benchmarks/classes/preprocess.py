import pandas as pd
import numpy as np


def new_column(df, size, position=0, column_name="", name=""):
    charar = [name for _ in range(size)]
    df[column_name] = charar
    cols = df.columns.tolist()
    cols = cols[0:position] + [cols[-1]] + cols[position:-1]
    return df.reindex(columns=cols)


def new_column_files(filename,out, size, position=0, column_name="", name=""):
    df = pd.read_csv(filename)
    charar = [name for _ in range(size)]
    df[column_name] = charar
    cols = df.columns.tolist()
    cols = cols[0:position] + [cols[-1]] + cols[position:-1]
    df = df[cols]
    df.to_csv(out,index=False)
    return pd.read_csv(out)


def new_columns(df, size, arr):
    cols = []
    for column in arr:
        charar = np.chararray((size,))
        charar[:] = column["name"]
        df[column["column_name"]] = charar
        cols = df.columns.tolist()
        cols = cols[0:column["pos"]] + [cols[-1]] + cols[column["pos"]:-1]
    return df[cols]