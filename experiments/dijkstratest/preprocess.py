from pathlib import Path
import subprocess


# --- Configuration ---
GRAPH_FILE = Path("denmark.graph")
JAR_FILE = Path("app/build/libs/app.jar")
OUTPUT_FILE = Path("output/augmentedDenmark.graph")


def build_jar_command() -> list[str]:
    """
    Construct the Java command for running the preprocessing step.
    Equivalent to: java -jar app.jar process_graph <input> <output>
    """
    return [
        "java",
        "-jar",
        str(JAR_FILE),
        "process_graph",
        str(GRAPH_FILE),
        str(OUTPUT_FILE)
    ]


def run_preprocess() -> None:
    # Ensure output directory exists
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)

    jar_cmd = build_jar_command()
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


if __name__ == "__main__":
    run_preprocess()