# Notes on implementation

# Using a fixed seed, choose 1 000 random (s, t) pairs in the Denmark graph. 
# Report on the average running time and the average number of relaxed edges when searching the 1 000 (s, t) pairs, 
# both for the provided Dijkstra implementation and your bidirectional version. 

# Find a suitable presentation to compare these two implementations with each other.


# fixed seed pick 1_000 random pairs (s,t) from 569586 vertexes (n in file denmark.graph) 
# call dijkstra file (uses scanner to parse input)
# pass denmark.graph as st.out
# call shortestPath(Graph g, long start, long target) with Graph denmark.graph
# collect output: running time, and average number of relaxed edges 
# postprocess the result into something useful (graph or table..)

import subprocess
import random
import numpy as np
import matplotlib.pyplot as plt
from pathlib import Path

# --- Configuration ---
BIDIRECTIONAL = True
GRAPH_FILE = Path("denmark.graph")
JAR_FILE = "app/build/libs/app.jar"
JAR_ARGS = f"dijkstratest{'_bidirectional' if BIDIRECTIONAL else ''}"
JAR_CMD = ["java", "-jar", str(JAR_FILE), str(JAR_ARGS), str(GRAPH_FILE)]  # adjust if you use a .jar or different package
NUM_QUERIES = 10
#N_VERTICES = 569_586
SEED = 1
OUTPUT_CSV = Path(f"experiments/dijkstratest/{'bidirectional_' if BIDIRECTIONAL else ''}dijkstra_results.csv")
OUTPUT_HIST = Path(f"experiments/dijkstratest/{'bidirectional_' if BIDIRECTIONAL else ''}dijkstra_histogram.png")

# --- Load graph vertex id's --- 
def get_vertex_ids(graph_file):
    with open(graph_file, "r") as f:
        n, _ = map(int, f.readline().split())
        ids = [int(f.readline().split()[0]) for _ in range(n)]
    return np.array(ids, dtype=np.int64)

# --- Generate random (s, t) pairs ---
# pairs = [(random.randint(1, N_VERTICES), random.randint(1, N_VERTICES))
#          for _ in range(NUM_QUERIES)]

# --- Run dijkstra for pairs ---
def run_queries(pairs):
    # Prepare the input as a single string for stdin
    input_data = "\n".join(f"{s} {t}" for s, t in pairs) + "\n"
    
    # Run the Java program with the pairs piped to stdin
    proc = subprocess.run(
        JAR_CMD,
        input=input_data,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    
    if proc.returncode != 0:
        print("Error running Java program:")
        print(proc.stderr)
        raise RuntimeError("Java process failed")

    # Parse output lines: s t dist relaxed time_ns
    results = []
    for line in proc.stdout.strip().splitlines():
        parts = line.split()
        if len(parts) != 5:
            continue
        s, t, dist, relaxed, time_ns = map(int, parts)
        results.append((s, t, dist, relaxed, time_ns))

    return np.array(results, dtype=np.int64)


# --- postprocess ---
def analyze_and_save(results):
    avg_time_ms = np.mean(results[:, 4]) / 1e6
    avg_relaxed = np.mean(results[:, 3])
    reachable = np.sum(results[:, 2] >= 0)

    print(f"\n=== Benchmark Summary ===")
    print(f"Queries:       {len(results)}")
    print(f"Reachable:     {reachable} / {len(results)}")
    print(f"Avg time:      {avg_time_ms:.3f} ms")
    print(f"Avg relaxed:   {avg_relaxed:.1f}")

    # Make dir if not exist 
    OUTPUT_CSV.parent.mkdir(parents=True, exist_ok=True)

    # Save CSV
    np.savetxt(
        OUTPUT_CSV,
        results,
        delimiter=",",
        header="start,target,distance,relaxed,time_ns",
        fmt="%d",
        comments=""
    )
    print(f"Saved results to {OUTPUT_CSV.resolve()}")

    # Plot and save histogram
    plt.figure(figsize=(8, 5))
    plt.hist(results[:, 4] / 1e6, bins=50, color="skyblue", edgecolor="black")
    #plt.xlim(0, np.percentile(results[:, 4] / 1e6, 99))  # focus on 99th percentile
    plt.xlabel("Runtime (ms)")
    plt.ylabel("Frequency")
    plt.title(f"{'Bidirectional ' if BIDIRECTIONAL else ''}Dijkstra Runtime Distribution")
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(OUTPUT_HIST, dpi=300)
    plt.close()
    print(f"Saved histogram to {OUTPUT_HIST.resolve()}")

def terminal_benchmark(results):
    avg_time_ms = np.mean(results[:, 4]) / 1e6
    avg_relaxed = np.mean(results[:, 3])
    reachable = np.sum(results[:, 2] >= 0)

    print(f"\n=== Benchmark Summary ===")
    print(f"Queries:       {len(results)}")
    print(f"Reachable:     {reachable} / {len(results)}")
    print(f"Avg time:      {avg_time_ms:.3f} ms")
    print(f"Avg relaxed:   {avg_relaxed:.1f}")

if __name__ == "__main__":
    print(f"Testing {'Bidirectional ' if BIDIRECTIONAL else ''}Dijkstra")
    print(f"Loading vertex IDs from {GRAPH_FILE} ...")
    vertex_ids = get_vertex_ids(GRAPH_FILE)
    random.seed(SEED)
    np.random.seed(SEED)

    pairs = np.column_stack([
        np.random.choice(vertex_ids, NUM_QUERIES),
        np.random.choice(vertex_ids, NUM_QUERIES)
    ])

    print(f"Generated {NUM_QUERIES} random (s, t) pairs")

    print("Running Java program...")
    results = run_queries(pairs)

    # print("Analyzing results...")
    # analyze_and_save(results)
    terminal_benchmark(results)