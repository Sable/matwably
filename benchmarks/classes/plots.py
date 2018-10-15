import matplotlib
import matplotlib.pyplot as plt
import numpy as np
from collections import Counter
from sklearn.preprocessing import normalize


class Plots:
    def __init__(self, mat):
        self._mat = mat

    def plot_scatter(self, category, limit):
        mat = self._mat
        limit = 200
        Plots.plot((mat[category][mat["Lateness"] < limit])[mat["Lateness"] > -limit],
                           (mat["Lateness"][mat["Lateness"] < limit])[mat["Lateness"] > -limit],
                           title="age_vs_lateness", x_label=category, y_label="Lateness")

    @staticmethod
    def prepare_geomean_plot(arr):
        return 0
    @staticmethod
    def plot(x, y, title="", x_label="", y_label=""):
        plt.figure(figsize=(10, 8))
        plt.plot(x, y, ".b")
        plt.xlabel(x_label)
        plt.ylabel(y_label)
        plt.title(title)
        plt.savefig("./plots/" + title.lower().replace(" ", "_") + ".png")
        plt.show()

    def plot_category_hist(self, category=None, category_labels=None,
                           lateness=None, lateness_legends=None, title="", y_label="", norm=False):
        unique_cat = np.unique(category)
        unique_late = np.unique(lateness)
        init = []
        for name in unique_cat:
            cat_values = np.zeros((len(unique_late),))
            late_cat = lateness[category == name]
            counter = Counter(late_cat)
            for key in counter: cat_values[int(key)] = counter[key]
            if norm: cat_values = normalize([cat_values])[0]
            init.append(cat_values.T)
        init = np.array(init).T
        self.plot_category_hist_helper(init, x_tick=category_labels, title=title,
                                       legends=lateness_legends, random_colors=True,
                                       y_label=y_label)

    @staticmethod
    def plot_category_hist_helper(y_freq, y_error=None,
                                  x_tick=None, title="", legends=None,
                                  random_colors=False, y_label="Speedup", y_lim=None):
        BENCHMARK_NUMBER = len(x_tick)
        NUMBER_ARTIFACTS = len(legends)
        # Constants for plot
        if x_tick is None:
            x_tick = []
        N = BENCHMARK_NUMBER
        width = 0.10  # width of bars
        fig, ax = plt.subplots(figsize=(10,8))  # Create subplots figure
        ind = np.arange(N)

        # Initializing colors
        if random_colors:
            colors = []
            for h in range(NUMBER_ARTIFACTS): colors.append(np.random.rand(3, ))
        else:
            colors = ['g', 'r', 'c', 'm', 'y', '#7293cb', '#e1974c', '#7b8585']
        rects = ()  # Subplots 'array'
        # Plotting
        for h in range(NUMBER_ARTIFACTS):
            re = ax.bar(ind + h * width, tuple(y_freq[h]), width, color=colors[h])
            rects = rects + (re[0],)

        ax.set_ylabel(y_label)
        ax.set_title(title)
        ax.set_xticks(ind + width * 2)
        ax.set_xticklabels(tuple(x_tick))
        ax.legend(rects, tuple(legends), loc="upper right")
        if relative:
            filename = "./plots/bar-normed-" + title.lower().replace(" ", "_") + ".png"
        else:
            filename = "./plots/bar-" + title.lower().replace(" ", "_") + ".png"
        plt.savefig(filename)
        plt.show()

        return 0
    @staticmethod
    def plot_v2(y, y_error=None,output_file=None, x_tick=None, title=None, legends=None,
                                  random_colors=False,width = None,
                                y_label="Speedup", y_lim=None, speedup=True,
                                geo_mean=True, move_xtick = 0,baseline_off=2,move=None,
                font_size=12,q2b=False, q3a=False, q2a=False, q2c=False, format="png"):
        GROUP_NUMBER = len(x_tick)
        print(GROUP_NUMBER)
        MEMBERS_GROUP = len(legends)
        matplotlib.rcParams.update({'font.size': font_size})

        print(MEMBERS_GROUP)
        N = GROUP_NUMBER
        if geo_mean:
            x_tick.append("geomean")
            N += 1
        # Constants for plot


        if width is None:
            if MEMBERS_GROUP > 6:
                width = 0.15  # width of bars
            else:
                width = 0.20
        fig, ax = plt.subplots(figsize=(15, 8))  # Create subplots figure
        if GROUP_NUMBER < 6:
            ind = np.arange(N) * 2
        else:
            ind = np.arange(N) * 1.5
        print(ind)
        # Initializing colors
        if random_colors:
            colors = []
            for h in range(MEMBERS_GROUP - 1): colors.append(np.random.rand(3, ))
        else:
            colors = ['g', 'r', 'c', 'm', 'y', '#7293cb', '#e1974c', '#7b8585']
        patterns = ["/", "\\", ".", "x", "o", "+", "|", "*", "0"]
        rects = ()  # Subplots 'array'
        # Plotting
        if speedup:
            length_rects = MEMBERS_GROUP - 1
        else:
            length_rects = MEMBERS_GROUP
        for h in range(length_rects):
            pos = ind + h * width + move_xtick
            if geo_mean: pos[ind.size - 1] = pos[ind.size - 1] + 0.5
            if y_error is not None:
                re = ax.bar(pos, tuple(list(y[h])), width, color=colors[h],hatch=patterns[h], yerr=tuple(list(y_error[h])))
            else:
                if q3a and h == 3:
                    pos[6] = pos[6]- (h * width + move_xtick) +  (h-1)*width + move_xtick
                    print(h, pos)
                if q2a and h == 3:
                    pos[7] = pos[7] - (h * width + move_xtick) + (h - 1) * width + move_xtick
                    print(h, pos)
                if q2c and h == 3:
                    print(h,pos)
                    print(y[h])
                    pos[1] = pos[1] - (h * width + move_xtick) +  (h-1)*width + move_xtick
                    print(h,pos)

                re = ax.bar(pos, tuple(y[h]), width, color=colors[h], hatch=patterns[h])
            rects = rects + (re[0],)
        if speedup:
            baseline = ax.plot(np.arange(N+baseline_off), np.ones(N+baseline_off), "k--", linewidth=2)
            rects = rects + (baseline[0],)
        ax.set_ylabel(y_label, fontsize=20)
        if title is not None:
            ax.set_title(title)

        ind = ind + width + move_xtick
        if move is not None:
            ind[ind.size - 2] = ind[len(ind) - 2] + move
            ind[ind.size - 1] = ind[len(ind) - 1] + move
        if geo_mean:
            ind[ind.size - 1] = ind[len(ind) - 1] + 0.5
        if q2b:
            ind[0] = ind[0] - 0.1
            ind[1] = ind[1] - 0.1
            ind[ind.size - 1] = ind[len(ind) - 1] - 0.5
            ind[ind.size - 2] = ind[len(ind) - 2] -  0.5
        ax.set_xticks(ind)
        ax.set_xticklabels(tuple(x_tick), fontsize=20)
        plt.legend(rects, tuple(legends), loc="upper left", fontsize=20)
        if y_lim is not None:
            plt.ylim(y_lim)
        if output_file is not None: plt.savefig(output_file, bbox_inches='tight', format=format)
        plt.show()
