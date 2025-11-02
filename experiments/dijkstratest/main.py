import random
import numpy as np
from pathlib import Path

# --- Local imports ---
from query_runner import run_queries, summarize_results, print_summary, save_to_csv
from plot_results import plot_runtime_histogram
from compare import plot_comparison


# --- Configuration ---
GRAPH_FILE = Path("denmark.graph")
JAR_FILE = Path("app/build/libs/app.jar")
NUM_QUERIES = 1000
SEED = 42
OUTPUT_DIR = Path("experiments/dijkstratest/results")


# --- Helpers ---
def get_vertex_ids(graph_file: Path) -> np.ndarray:
    """Load vertex IDs from the graph file."""
    with open(graph_file, "r") as f:
        n, _ = map(int, f.readline().split())
        ids = [int(f.readline().split()[0]) for _ in range(n)]
    return np.array(ids, dtype=np.int64)


def run_experiment(bidirectional: bool, vertex_ids: np.ndarray) -> Path:
    """
    Run Dijkstra or Bidirectional Dijkstra experiment and return path to CSV.

    Args:
        bidirectional: Whether to use the bidirectional implementation.
        vertex_ids: Array of vertex IDs.

    Returns:
        Path to the saved CSV results file.
    """
    algo = "bidirectional" if bidirectional else "standard"
    print(f"\n=== Running {algo.capitalize()} Dijkstra ===")

    jar_args = f"dijkstratest{'_bidirectional' if bidirectional else ''}"
    jar_cmd = ["java", "-jar", str(JAR_FILE), jar_args, str(GRAPH_FILE)]

    pairs = np.column_stack([
        np.random.choice(vertex_ids, NUM_QUERIES),
        np.random.choice(vertex_ids, NUM_QUERIES)
    ])
    print(f"Generated {NUM_QUERIES} random (s, t) pairs")

    # Run the Java program
    results = run_queries(pairs, jar_cmd)

    # Output paths
    csv_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_results.csv"
    hist_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_histogram.png"

    # Summarize + save + plot
    summary = summarize_results(results)
    print_summary(summary)
    save_to_csv(results, csv_path)
    plot_runtime_histogram(csv_path, hist_path, bidirectional)

    return csv_path


def update_plots(bidirectional: bool):
    """ Update the plots without running new experiment"""
    algo = "bidirectional" if bidirectional else "standard"
    csv_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_results.csv"
    hist_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_histogram.png"
    plot_runtime_histogram(csv_path, hist_path, bidirectional)


def update_comparison_plot():
    """ Update the comparison plot without running new experiment """
    print("\nGenerating comparison plot...")
    csv_standard_path = OUTPUT_DIR / f"standard_{NUM_QUERIES}_results.csv"
    csv_bidirectional_path = OUTPUT_DIR / f"bidirectional_{NUM_QUERIES}_results.csv"

    results_standard = np.loadtxt(csv_standard_path, delimiter=",", skiprows=1)
    results_bidirectional = np.loadtxt(csv_bidirectional_path, delimiter=",", skiprows=1)
    plot_comparison(NUM_QUERIES, results_standard, results_bidirectional)


def run_complete_test():
    random.seed(SEED)
    np.random.seed(SEED)
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    print(f"Loading vertex IDs from {GRAPH_FILE} ...")
    vertex_ids = get_vertex_ids(GRAPH_FILE)

    # Run both experiments
    csv_standard = run_experiment(bidirectional=False, vertex_ids=vertex_ids)
    csv_bidirectional = run_experiment(bidirectional=True, vertex_ids=vertex_ids)

    # --- Compare results ---
    print("\nGenerating comparison plot...")
    
    # Load CSVs for comparison
    results_standard = np.loadtxt(csv_standard, delimiter=",", skiprows=1)
    results_bidirectional = np.loadtxt(csv_bidirectional, delimiter=",", skiprows=1)
    plot_comparison(NUM_QUERIES, results_standard, results_bidirectional)


# --- Main entry point ---
if __name__ == "__main__":
    run_complete_test()

    #update_plots(bidirectional=True)
    #update_plots(bidirectional=False)
    #update_comparison_plot()