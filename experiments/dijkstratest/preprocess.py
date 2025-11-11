from pathlib import Path
from config import JAR_FILE, ORIGINAL_GRAPH_FILE, AUGMENTED_GRAPH_FILE
import subprocess

def build_jar_command(info: bool) -> list[str]:
    """
    Construct the Java command for running the preprocessing step.
    Equivalent to: java -jar app.jar process_graph <input> <output>
    """
    return [
        "java",
        "-jar",
        str(JAR_FILE),
        "process_graph",
        str(ORIGINAL_GRAPH_FILE),
        str(AUGMENTED_GRAPH_FILE),
        "info" if info else "nothing" 
    ]


def run_preprocess(info: bool) -> None:
    # Ensure output directory exists
    AUGMENTED_GRAPH_FILE.parent.mkdir(parents=True, exist_ok=True)

    jar_cmd = build_jar_command(info)
    print(f"Running: {' '.join(jar_cmd)}")

    proc = subprocess.run(
        jar_cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )

    if proc.returncode != 0:
        print("Error running Java program:")
        print(proc.stderr)
        raise RuntimeError("Java process failed")

    print("Java process completed successfully.")
    print(proc.stdout)


# --- Main entry point ---
if __name__ == "__main__":
    # Takes the original graph and preprocesses it
    run_preprocess(info=True)