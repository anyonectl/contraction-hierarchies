from pathlib import Path

# --- Configuration ---
JAR_FILE = Path("app/build/libs/app.jar")
NUM_QUERIES = 1000
SEED = 42

AUGMENTED_GRAPH_FILE = Path("output/augmentedDenmark.graph")
ORIGINAL_GRAPH_FILE = Path("denmark.graph")

AUGMENTED_OUTPUT_DIR = Path("experiments/dijkstratest/results_augmented")
ORIGINAL_OUTPUT_DIR = Path("experiments/dijkstratest/results")

