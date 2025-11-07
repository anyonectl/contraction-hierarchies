from pathlib import Path
from typing import Dict, Any
import subprocess
import numpy as np


def run_queries(pairs, jar_cmd) -> np.array:
    """Run Java Dijkstra or bidirectional Dijkstra on (s,t) pairs."""
    input_data = "\n".join(f"{s} {t}" for s, t in pairs) + "\n"
    
    proc = subprocess.run(
        jar_cmd,
        input=input_data,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    
    if proc.returncode != 0:
        print("Error running Java program:")
        print(proc.stderr)
        raise RuntimeError("Java process failed")

    results = []
    for line in proc.stdout.strip().splitlines():
        parts = line.split()
        if len(parts) != 5:
            continue
        s, t, dist, relaxed, time_ns = map(int, parts)
        results.append((s, t, dist, relaxed, time_ns))
    
    return np.array(results, dtype=np.int64)

def summarize_results(results: np.ndarray) -> Dict[str, Any]:
    """ Compute summary statistics for Dijkstra experiment results. """
    avg_time_ms: float = np.mean(results[:, 4]) / 1e6
    avg_relaxed: float = np.mean(results[:, 3])
    reachable: int = int(np.sum(results[:, 2] >= 0))
    return {
        "queries": int(len(results)),
        "reachable": reachable,
        "avg_time_ms": avg_time_ms,
        "avg_relaxed": avg_relaxed,
    }


def print_summary(summary: Dict[str, Any]) -> None:
    """ Print benchmark summary statistics to the terminal. """
    print(f"\n=== Benchmark Summary ===")
    print(f"Queries:       {summary['queries']}")
    print(f"Reachable:     {summary['reachable']} / {summary['queries']}")
    print(f"Avg time:      {summary['avg_time_ms']:.3f} ms")
    print(f"Avg relaxed:   {summary['avg_relaxed']:.1f}")


def save_to_csv(results: np.ndarray, csv_path: Path) -> None:
    """ Save experiment results to a CSV file. """
    csv_path.parent.mkdir(parents=True, exist_ok=True)
    np.savetxt(
        csv_path,
        results,
        delimiter=",",
        header="start,target,distance,relaxed,time_ns",
        fmt="%d",
        comments=""
    )
    print(f"Saved results to {csv_path.resolve()}")
