import random
import numpy as np
from pathlib import Path

# --- Local imports ---
from query_runner import run_queries, summarize_results, print_summary, save_to_csv
from preprocess import run_preprocess
from config import JAR_FILE, NUM_QUERIES, SEED, AUGMENTED_GRAPH_FILE, ORIGINAL_GRAPH_FILE, AUGMENTED_OUTPUT_DIR, ORIGINAL_OUTPUT_DIR


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


def build_jar_command(augmented: bool, bidirectional: bool) -> list[str]:
    """
    Construct the Java command for running the Dijkstra test.
    Usage: java -jar app.jar <use> <augmented> <bidirectional> <graphfile>
    """
    return [
        "java", "-jar", str(JAR_FILE),
        "run_queries",
        "augmented" if augmented else "original",
        "bidirectional" if bidirectional else "standard",
        str(AUGMENTED_GRAPH_FILE) if augmented else str(ORIGINAL_GRAPH_FILE),
    ]

# --- Experiments... ---
def run_experiment(augmented: bool, bidirectional: bool, pairs: np.ndarray) -> Path:
    """Run Dijkstra or Bidirectional Dijkstra experiment and return path to CSV."""
    algo = "bidirectional" if bidirectional else "standard"
    print(f"\n=== Running {algo.capitalize()} Dijkstra ===")
    print(f"Using pre-generated {len(pairs)} (s, t) pairs")

    # Run Java experiment
    jar_command = build_jar_command(augmented, bidirectional)
    results = run_queries(pairs, jar_command)

    # Prepare output paths
    base_dir = AUGMENTED_OUTPUT_DIR if augmented else ORIGINAL_OUTPUT_DIR
    csv_path = base_dir / f"{algo}_{NUM_QUERIES}_results.csv"
    hist_path = base_dir / f"{algo}_{NUM_QUERIES}_histogram.png"

    # Summarize and save results
    summary = summarize_results(results)
    print_summary(summary)
    save_to_csv(results, csv_path)

    return csv_path

# --- Mock test (simple summary in terminal, no csv) ---
def run_terminal_benchmark_test(augmented: bool = False, bidirectional: bool = False) -> None:
    """Run a simplified terminal benchmark (no file output)."""
    print(f"Loading vertex IDs from {AUGMENTED_GRAPH_FILE if augmented else ORIGINAL_GRAPH_FILE} ...")
    vertex_ids = get_vertex_ids(AUGMENTED_GRAPH_FILE if augmented else ORIGINAL_GRAPH_FILE)

    algo = "bidirectional" if bidirectional else "standard"
    print(f"\n=== Running {algo.capitalize()} Dijkstra (no CSV) ===")

    pairs = generate_random_pairs(vertex_ids, NUM_QUERIES)
    results = run_queries(pairs, build_jar_command(augmented, bidirectional))

    summary = summarize_results(results)
    print_summary(summary)

# --- Setup before running tests ---
def setup() -> None:
    random.seed(SEED)
    np.random.seed(SEED)
    AUGMENTED_OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    ORIGINAL_OUTPUT_DIR.mkdir(parents=True, exist_ok=True)


# --- Main entry point ---
if __name__ == "__main__":
    setup()
    # Generate pairs (s,t) so all experiments run on same pairs
    vertex_ids = get_vertex_ids(ORIGINAL_GRAPH_FILE) # should be same vertex ids for both graphs
    pairs = generate_random_pairs(vertex_ids, NUM_QUERIES)

    # Standard dijkstra on Original Graph (CSV)
    run_experiment(augmented=False, bidirectional=False, pairs=pairs)
    
    # Bidirectional Dijkstra on Original Graph (CSV)
    run_experiment(augmented=False, bidirectional=True, pairs=pairs)

    # Takes the original graph and preprocesses it
    run_preprocess()

    # Bidirectional Search on Augmented Graph (CSV)
    run_experiment(augmented=True, bidirectional=True, pairs=pairs)

    # "Quick" experimentation can be done in the terminal using this - does not overwrite the csv files
    # Tip: Adjust the NUM_QUERIES in the config to run smaller tests ...
    # run_terminal_benchmark_test(augmented=True, bidirectional=True)