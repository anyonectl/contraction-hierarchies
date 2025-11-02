from pathlib import Path
from typing import Union
import numpy as np
import matplotlib.pyplot as plt


def plot_runtime_histogram(
    results_or_csv: Union[np.ndarray, Path],
    hist_path: Path,
    bidirectional: bool
) -> None:
    """ Generate and save a histogram of runtime distributions. """
    hist_path.parent.mkdir(parents=True, exist_ok=True)

    # --- Load from CSV if needed ---
    if isinstance(results_or_csv, Path):
        if not results_or_csv.exists():
            raise FileNotFoundError(f"CSV file not found: {results_or_csv}")
        print(f"Loading results from {results_or_csv} ...")
        results = np.loadtxt(results_or_csv, delimiter=",", skiprows=101)
    else:
        results = results_or_csv

    # --- Compute runtimes ---
    runtimes_ms = results[:, 4] / 1e6  # Convert ns → ms

    # --- Plot ---
    plt.figure(figsize=(8, 5))
    plt.hist(runtimes_ms, bins=50, color="skyblue", edgecolor="black")
    plt.xlabel("Runtime (ms)")
    plt.ylabel("Frequency")
    plt.title(f"{'Bidirectional ' if bidirectional else ''}Dijkstra Runtime Distribution")
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(hist_path, dpi=300)
    plt.close()
    print(f"Saved histogram to {hist_path.resolve()}")
