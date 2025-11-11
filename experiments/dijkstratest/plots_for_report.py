import pandas as pd
import matplotlib as mpl
import matplotlib.pyplot as plt
import numpy as np

# =========================================
#   1. Configure matplotlib for latex
# =========================================

mpl.use("pgf")  # Use PGF backend for LaTeX export

mpl.rcParams.update({
    "pgf.rcfonts": False,      # don't override LaTeX fonts
    "text.usetex": True,       # use LaTeX for all text
    "font.family": "serif",    # match LaTeX document font
    "font.size": 16,           
    "axes.labelweight": "bold",
    "axes.linewidth": 1.0,
    "xtick.direction": "in",
    "ytick.direction": "in",
    "grid.linestyle": "--",
    "grid.alpha": 0.4,
})


# ==============================
#   2. Define colours
# ==============================

colors = [
    "#2580B7", # Blue
    "#179E86", # Dark Green
    "#9EBE5B", # Light Green
    "#F59B11", # Yellow
    "#C03B26", # Red
    "#633248", # Brown
    "#6F747B", # Gray
]


# ==============================
#   3. Load and combine the data in a pandas dataframe
# ==============================

files = {
    "Dijkstra": "experiments/dijkstratest/results/standard_1000_results.csv",
    "Bidirectional": "experiments/dijkstratest/results/bidirectional_1000_results.csv",
    "CH": "experiments/dijkstratest/results_augmented/bidirectional_1000_results.csv"
}

dfs = []
for algo, path in files.items():
    df = pd.read_csv(path)
    df["algorithm"] = algo
    dfs.append(df)

df_all = pd.concat(dfs, ignore_index=True)


# ==============================
#   4. Plots
# ==============================

def scatter_distance_runtime_wide(path, filename):
    plt.figure(figsize=(7, 5))
    algorithms = list(files.keys())
    for i, algo in enumerate(algorithms):
        subset = df_all[df_all["algorithm"] == algo]
        plt.scatter(
            subset["distance"], subset["time_ns"],
            label=algo,
            s=40,
            alpha=0.6,
            edgecolor=colors[-1],
            linewidths=0.6,
            c=colors[i]
        )
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel(r"Path distance")
    plt.ylabel(r"Runtime (ns)")
    plt.title("Algorithm performance on 1000 shortest path queries", pad=12)
    plt.legend(frameon=False)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def scatter_distance_runtime_square(path, filename):
    plt.figure(figsize=(7, 7))
    algorithms = list(files.keys())
    for i, algo in enumerate(algorithms):
        subset = df_all[df_all["algorithm"] == algo]
        plt.scatter(
            subset["distance"], subset["time_ns"],
            label=algo,
            s=40,
            alpha=0.6,
            edgecolor=colors[-1],
            linewidths=0.6,
            c=colors[i]
        )
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel(r"Path distance")
    plt.ylabel(r"Runtime (ns)")
    plt.title("Algorithm performance on 1000 shortest path queries", pad=12)
    plt.legend(frameon=False)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def scatter_distance_relaxed_wide(path, filename):
    plt.figure(figsize=(7, 5))
    algorithms = list(files.keys())
    for i, algo in enumerate(algorithms):
        subset = df_all[df_all["algorithm"] == algo]
        plt.scatter(
            subset["distance"], subset["relaxed"],
            label=algo,
            s=40,
            alpha=0.6,
            edgecolor=colors[-1],
            linewidths=0.6,
            c=colors[i]
        )
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel(r"Path distance")
    plt.ylabel(r"Relaxed edges")
    plt.title("Algorithm performance on 1000 shortest path queries", pad=12)
    plt.legend(frameon=False)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def histogram_runtime(path, filename):
    df_subset = df_all[df_all["algorithm"].isin(["Dijkstra", "Bidirectional"])]
    dijkstra_times = df_subset[df_subset["algorithm"] == "Dijkstra"]["time_ns"] / 1e6
    bidir_times = df_subset[df_subset["algorithm"] == "Bidirectional"]["time_ns"] / 1e6
    
    plt.figure(figsize=(7, 5))
    bins = 50
    plt.hist(
        dijkstra_times,
        bins=bins,
        alpha=0.6,
        label="Dijkstra",
        color=colors[0],
        # edgecolor=colors[-1],
        linewidth=0.6
    )
    plt.hist(
        bidir_times,
        bins=bins,
        alpha=0.6,
        label="Bidirectional",
        color=colors[1],
        # edgecolor=colors[-1],
        linewidth=0.6
    )
    plt.xlabel(r"Runtime (ms)")
    plt.ylabel(r"Frequency")
    plt.title("Runtime distribution", pad=12)
    plt.legend(frameon=False)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def histogram_runtime_aligned(path, filename):
    df_subset = df_all[df_all["algorithm"].isin(["Dijkstra", "Bidirectional"])]
    dijkstra_times = df_subset[df_subset["algorithm"] == "Dijkstra"]["time_ns"] / 1e6
    bidir_times = df_subset[df_subset["algorithm"] == "Bidirectional"]["time_ns"] / 1e6
    
    plt.figure(figsize=(7, 5))
    
    # Create shared bins based on the combined data range
    all_times = pd.concat([dijkstra_times, bidir_times])
    bins = np.linspace(all_times.min(), all_times.max(), 51)
    
    plt.hist(
        dijkstra_times,
        bins=bins,
        alpha=0.6,
        label="Dijkstra",
        color=colors[0],
        # edgecolor=colors[-1],
        linewidth=0.6
    )
    
    plt.hist(
        bidir_times,
        bins=bins,
        alpha=0.6,
        label="Bidirectional",
        color=colors[1],
        # edgecolor=colors[-1],
        linewidth=0.6
    )
    
    plt.xlabel(r"Runtime (ms)")
    plt.ylabel(r"Frequency")
    plt.title("Runtime distribution", pad=12)
    plt.legend(frameon=False)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def histogram_relaxed_aligned(path, filename):
    df_subset = df_all[df_all["algorithm"].isin(["Dijkstra", "Bidirectional"])]
    dijkstra_relaxed = df_subset[df_subset["algorithm"] == "Dijkstra"]["relaxed"]
    bidir_relaxed = df_subset[df_subset["algorithm"] == "Bidirectional"]["relaxed"]
    
    plt.figure(figsize=(7, 5))
    
    # Create shared bins based on the combined data range
    both_relaxed = pd.concat([dijkstra_relaxed, bidir_relaxed])
    bins = np.linspace(both_relaxed.min(), both_relaxed.max(), 51)
    
    plt.hist(
        dijkstra_relaxed,
        bins=bins,
        alpha=0.6,
        label="Dijkstra",
        color=colors[0],
        # edgecolor=colors[-1],
        linewidth=0.6
    )
    
    plt.hist(
        bidir_relaxed,
        bins=bins,
        alpha=0.6,
        label="Bidirectional",
        color=colors[1],
        # edgecolor=colors[-1],
        linewidth=0.6
    )
    
    plt.xlabel(r"Relaxed edges")
    plt.ylabel(r"Frequency")
    plt.title("Relaxed edges distribution", pad=12)
    plt.legend(frameon=False)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def scatter_paired(path, filename):
    # merge datasets on start and target to get paired comparisons
    dijkstra_df = df_all[df_all["algorithm"] == "Dijkstra"][["start", "target", "time_ns"]].rename(columns={"time_ns": "dijkstra_time"})
    bidir_df = df_all[df_all["algorithm"] == "Bidirectional"][["start", "target", "time_ns"]].rename(columns={"time_ns": "bidir_time"})
    paired = pd.merge(dijkstra_df, bidir_df, on=["start", "target"])

    # convert to ms
    paired["dijkstra_time"] = paired["dijkstra_time"] / 1e6
    paired["bidir_time"] = paired["bidir_time"] / 1e6

    plt.figure(figsize=(7, 7))
    plt.scatter(
        paired["bidir_time"],
        paired["dijkstra_time"],
        s=40,
        alpha=0.6,
        edgecolor=colors[-1],
        linewidth=0.6,
        c=colors[3],
        label="Query"
    )
    
    # add diagonal line y=x
    max_val = max(paired["dijkstra_time"].max(), paired["bidir_time"].max())
    min_val = min(paired["dijkstra_time"].min(), paired["bidir_time"].min())
    plt.plot([min_val, max_val], [min_val, max_val], 'k--', alpha=0.5, linewidth=1, label="Equal performance")
    
    # force same ticks for both axes
    ax = plt.gca()
    ax.set_xticks(ax.get_yticks())

    plt.xlabel(r"Bidirectional runtime (ms)")
    plt.ylabel(r"Dijkstra runtime (ms)")
    plt.title("Bidirectional Dijkstra vs. Dijkstra on 1000 queries", pad=12)
    plt.legend(frameon=False,loc='upper left')
    plt.axis('equal')
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def scatter_paired_relaxed(path, filename):
    # merge datasets on start and target to get paired comparisons
    dijkstra_df = df_all[df_all["algorithm"] == "Dijkstra"][["start", "target", "relaxed"]].rename(columns={"relaxed": "dijkstra_relaxed"})
    bidir_df = df_all[df_all["algorithm"] == "Bidirectional"][["start", "target", "relaxed"]].rename(columns={"relaxed": "bidir_relaxed"})
    paired = pd.merge(dijkstra_df, bidir_df, on=["start", "target"])

    plt.figure(figsize=(7, 7))
    plt.scatter(
        paired["bidir_relaxed"],
        paired["dijkstra_relaxed"],
        s=40,
        alpha=0.6,
        edgecolor=colors[-1],
        linewidth=0.6,
        c=colors[3],
        label="Query"
    )
    
    # add diagonal line y=x
    max_val = max(paired["dijkstra_relaxed"].max(), paired["bidir_relaxed"].max())
    min_val = min(paired["dijkstra_relaxed"].min(), paired["bidir_relaxed"].min())
    plt.plot([min_val, max_val], [min_val, max_val], 'k--', alpha=0.5, linewidth=1, label="Equal performance")
    
    # force same ticks for both axes
    ax = plt.gca()
    ax.set_xticks(ax.get_yticks())

    plt.xlabel(r"Bidirectional relaxed edges")
    plt.ylabel(r"Dijkstra relaxed edges")
    plt.title("Bidirectional Dijkstra vs. Dijkstra on 1000 queries", pad=12)
    plt.legend(frameon=False,loc='upper left')
    plt.axis('equal')
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def boxplot_runtime(path, filename):
    df_subset = df_all[df_all["algorithm"].isin(["Dijkstra", "Bidirectional"])]
    df_subset = df_subset.copy()
    df_subset["time_ms"] = df_subset["time_ns"] / 1e6
    
    plt.figure(figsize=(7, 5))
    data_to_plot = [
        df_subset[df_subset["algorithm"] == "Dijkstra"]["time_ms"],
        df_subset[df_subset["algorithm"] == "Bidirectional"]["time_ms"]
    ]
    bp = plt.boxplot(
        data_to_plot,
        tick_labels=["Dijkstra", "Bidirectional"],
        patch_artist=True,
        widths=0.6
    )
    for patch, color in zip(bp['boxes'], [colors[0], colors[1]]):
        patch.set_facecolor(color)
        patch.set_alpha(0.6)
        patch.set_edgecolor(colors[-1])
        patch.set_linewidth(0.6)
    # style whiskers, caps, and medians
    for whisker in bp['whiskers']:
        whisker.set_color(colors[-1])
        whisker.set_linewidth(1)
    for cap in bp['caps']:
        cap.set_color(colors[-1])
        cap.set_linewidth(1)
    for median in bp['medians']:
        median.set_color(colors[-1])
        median.set_linewidth(1.5)
    plt.ylabel(r"Runtime (ms)")
    plt.title("Runtime distribution (boxplot)", pad=12)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()

def boxplot_relaxed(path, filename):
    df_subset = df_all[df_all["algorithm"].isin(["Dijkstra", "Bidirectional"])]
    df_subset = df_subset.copy()
    
    plt.figure(figsize=(7, 5))
    data_to_plot = [
        df_subset[df_subset["algorithm"] == "Dijkstra"]["relaxed"],
        df_subset[df_subset["algorithm"] == "Bidirectional"]["relaxed"]
    ]
    bp = plt.boxplot(
        data_to_plot,
        tick_labels=["Dijkstra", "Bidirectional"],
        patch_artist=True,
        widths=0.6
    )
    for patch, color in zip(bp['boxes'], [colors[0], colors[1]]):
        patch.set_facecolor(color)
        patch.set_alpha(0.6)
        patch.set_edgecolor(colors[-1])
        patch.set_linewidth(0.6)
    # style whiskers, caps, and medians
    for whisker in bp['whiskers']:
        whisker.set_color(colors[-1])
        whisker.set_linewidth(1)
    for cap in bp['caps']:
        cap.set_color(colors[-1])
        cap.set_linewidth(1)
    for median in bp['medians']:
        median.set_color(colors[-1])
        median.set_linewidth(1.5)
    plt.ylabel(r"Relaxed edges")
    plt.title("Relaxed edges distribution (boxplot)", pad=12)
    plt.tight_layout()
    plt.savefig(f'{path}latex/{filename}.pgf')
    plt.savefig(f'{path}plots/{filename}.png', dpi=300)
    plt.close()


# =========================================
# 4. Save as PGF
# =========================================

output_dir = "report/"
scatter_distance_runtime_wide(output_dir, "scatter_all_wide")
scatter_distance_runtime_square(output_dir, "scatter_all_square")
scatter_distance_relaxed_wide(output_dir, "scatter_all_wide_relaxed")
histogram_runtime(output_dir, "histogram_runtime")
histogram_runtime_aligned(output_dir, "histogram_runtime_aligned")
histogram_relaxed_aligned(output_dir, "histogram_relaxed_aligned")
scatter_paired(output_dir, "scatter_paired_runtime")
scatter_paired_relaxed(output_dir, "scatter_paired_relaxed")
boxplot_runtime(output_dir, "boxplot_runtime")
boxplot_relaxed(output_dir, "boxplot_relaxed")
print(f"Plots saved as PNG and PGF under {output_dir}")
