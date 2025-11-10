from config import NUM_QUERIES, ORIGINAL_OUTPUT_DIR, AUGMENTED_OUTPUT_DIR
from plot_results import plot_runtime_histogram
from compare import plot_comparison
import numpy as np

# --- Update the plots  ---
def update_plot(augmented: bool, bidirectional: bool):
    """Regenerate the plot using existing CSVs."""
    algo = "bidirectional" if bidirectional else "standard"
    base_dir = AUGMENTED_OUTPUT_DIR if augmented else ORIGINAL_OUTPUT_DIR

    csv_path = base_dir / f"{algo}_{NUM_QUERIES}_results.csv"
    hist_path = base_dir / f"{algo}_{NUM_QUERIES}_histogram.png"

    plot_runtime_histogram(csv_path, hist_path, bidirectional)


def update_comparison_plot(): # only makes sense for the initial tests (not with augmented)
    """Generate the comparison plot using existing CSVs."""
    print("\nGenerating comparison plot...")

    csv_standard_path = ORIGINAL_OUTPUT_DIR / f"standard_{NUM_QUERIES}_results.csv"
    csv_bidirectional_path = ORIGINAL_OUTPUT_DIR / f"bidirectional_{NUM_QUERIES}_results.csv"

    results_standard = np.loadtxt(csv_standard_path, delimiter=",", skiprows=1)
    results_bidirectional = np.loadtxt(csv_bidirectional_path, delimiter=",", skiprows=1)
    plot_comparison(NUM_QUERIES, results_standard, results_bidirectional)


if __name__ == "__main__":
    # Update the visualisation of our experiments based on CSV files

    # Standard Dijkstra on Original Graph
    update_plot(augmented=False, bidirectional=False) 
    
    # Bidirectional Dijkstra on Original Graph
    update_plot(augmented=False, bidirectional=True) 

    # Bidirectional Search on Augmented Graph
    update_plot(augmented=True, bidirectional=True) 

    # Update comparison plot
    # Updates the comparison between Standard and Bidirectional Dijkstra on Original Graph
    update_comparison_plot() 