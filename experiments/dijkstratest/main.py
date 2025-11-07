import random
import numpy as np
from pathlib import Path

# --- Local imports ---
from query_runner import run_queries, summarize_results, print_summary, save_to_csv
from plot_results import plot_runtime_histogram
from compare import plot_comparison


# --- Configuration ---
GRAPH_FILE = Path("output/augmentedDenmark.graph")
JAR_FILE = Path("app/build/libs/app.jar")
NUM_QUERIES = 1000
SEED = 42
OUTPUT_DIR = Path("experiments/dijkstratest/results_augmented")


# --- Helpers ---
def get_vertex_ids(graph_file: Path) -> np.ndarray:
    """Load vertex IDs from the graph file."""
    with open(graph_file, "r") as f:
        n, _ = map(int, f.readline().split())
        ids = [int(f.readline().split()[0]) for _ in range(n)]
    return np.array(ids, dtype=np.int64)


def generate_random_pairs(vertex_ids: np.ndarray, num_pairs: int) -> np.ndarray:
    """Generate random (source, target) vertex ID pairs."""
    return np.column_stack([
        np.random.choice(vertex_ids, num_pairs),
        np.random.choice(vertex_ids, num_pairs)
    ])


def build_jar_command(contracted: bool, bidirectional: bool) -> list[str]:
    """
    Construct the Java command for running the Dijkstra test.
    Usage: java -jar app.jar <use> <augmented> <bidirectional> <graphfile>
    """
    return [
        "java", "-jar", str(JAR_FILE),
        "run_queries",
        "augmented" if contracted else "original",
        "bidirectional" if bidirectional else "standard",
        str(GRAPH_FILE),
    ]

def run_experiment(contracted: bool, bidirectional: bool, vertex_ids: np.ndarray) -> Path:
    """Run Dijkstra or Bidirectional Dijkstra experiment and return path to CSV."""
    algo = "bidirectional" if bidirectional else "standard"
    print(f"\n=== Running {algo.capitalize()} Dijkstra ===")

    pairs = generate_random_pairs(vertex_ids, NUM_QUERIES)
    print(f"Generated {NUM_QUERIES} random (s, t) pairs")

    # Run Java experiment
    results = run_queries(pairs, build_jar_command(contracted, bidirectional))

    # Prepare output paths
    csv_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_results.csv"
    hist_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_histogram.png"

    # Summarize and save results
    summary = summarize_results(results)
    print_summary(summary)
    save_to_csv(results, csv_path)
    plot_runtime_histogram(csv_path, hist_path, bidirectional)

    return csv_path


def update_plots(bidirectional: bool):
    """Regenerate the plot using existing CSVs."""
    algo = "bidirectional" if bidirectional else "standard"
    csv_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_results.csv"
    hist_path = OUTPUT_DIR / f"{algo}_{NUM_QUERIES}_histogram.png"
    plot_runtime_histogram(csv_path, hist_path, bidirectional)


def update_comparison_plot():
    """Regenerate the comparison plot using existing CSVs."""
    print("\nGenerating comparison plot...")

    csv_standard_path = OUTPUT_DIR / f"standard_{NUM_QUERIES}_results.csv"
    csv_bidirectional_path = OUTPUT_DIR / f"bidirectional_{NUM_QUERIES}_results.csv"

    results_standard = np.loadtxt(csv_standard_path, delimiter=",", skiprows=1)
    results_bidirectional = np.loadtxt(csv_bidirectional_path, delimiter=",", skiprows=1)
    plot_comparison(NUM_QUERIES, results_standard, results_bidirectional)


def run_complete_test(contracted: bool = False) -> None:
    """Run both Dijkstra and Bidirectional Dijkstra experiments and compare results."""
    random.seed(SEED)
    np.random.seed(SEED)
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    print(f"Loading vertex IDs from {GRAPH_FILE} ...")
    vertex_ids = get_vertex_ids(GRAPH_FILE)

    # Run both experiments
    csv_standard = run_experiment(contracted, bidirectional=False, vertex_ids=vertex_ids)
    csv_bidirectional = run_experiment(contracted, bidirectional=True, vertex_ids=vertex_ids)

    # Compare and plot
    print("\nGenerating comparison plot...")
    results_standard = np.loadtxt(csv_standard, delimiter=",", skiprows=1)
    results_bidirectional = np.loadtxt(csv_bidirectional, delimiter=",", skiprows=1)
    plot_comparison(NUM_QUERIES, results_standard, results_bidirectional)


def run_terminal_benchmark_test(contracted: bool = False, bidirectional: bool = False) -> None:
    """Run a simplified terminal benchmark (no file output)."""
    random.seed(SEED)
    np.random.seed(SEED)

    print(f"Loading vertex IDs from {GRAPH_FILE} ...")
    vertex_ids = get_vertex_ids(GRAPH_FILE)

    algo = "bidirectional" if bidirectional else "standard"
    print(f"\n=== Running {algo.capitalize()} Dijkstra (Benchmark Mode) ===")

    pairs = generate_random_pairs(vertex_ids, NUM_QUERIES)
    results = run_queries(pairs, build_jar_command(contracted, bidirectional))

    summary = summarize_results(results)
    print_summary(summary)


# --- Main entry point ---
if __name__ == "__main__":
    # Uncomment if you want to regenerate plots without re-running experiments
    run_terminal_benchmark_test(contracted=True, bidirectional=True)
    # run_complete_test(contracted=True)
    # update_plots(bidirectional=True)
    # update_plots(bidirectional=False)
    # update_comparison_plot()