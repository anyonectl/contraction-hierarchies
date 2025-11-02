import numpy as np
import matplotlib.pyplot as plt
from pathlib import Path
from typing import Optional

def plot_comparison(
    num_queries: int,
    results_standard: np.ndarray,
    results_bidirectional: np.ndarray,
    output_path: Optional[Path] = None
) -> None:
    """ Create a side-by-side comparison of Dijkstra and Bidirectional Dijkstra. """
    avg_time_std = np.mean(results_standard[:, 4]) / 1e6
    avg_time_bi = np.mean(results_bidirectional[:, 4]) / 1e6
    avg_relaxed_std = np.mean(results_standard[:, 3])
    avg_relaxed_bi = np.mean(results_bidirectional[:, 3])

    algorithms = ["Standard", "Bidirectional"]
    avg_times = [avg_time_std, avg_time_bi]
    avg_relaxed = [avg_relaxed_std, avg_relaxed_bi]

    if output_path is None:
        output_path = Path(f"experiments/dijkstratest/results/{num_queries}_comparison.png")
    output_path.parent.mkdir(parents=True, exist_ok=True)

    # --- Plot comparison ---
    plt.figure(figsize=(8, 5))
    plt.subplot(2, 1, 1)
    plt.bar(algorithms, avg_times)
    plt.ylabel("Avg Runtime (ms)")
    plt.title("Average Runtime Comparison")

    plt.subplot(2, 1, 2)
    plt.bar(algorithms, avg_relaxed)
    plt.ylabel("Avg Relaxed Edges")
    plt.title("Relaxed Edges Comparison")

    plt.tight_layout()
    plt.savefig(output_path, dpi=300)
    plt.close()

    print(f"Saved comparison plot to {output_path.resolve()}")
