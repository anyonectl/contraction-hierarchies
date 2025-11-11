# Contraction Hierarchies Assignment

## Build

The project uses gradle. Carry out the tests using `gradle test` and build the jar file using `gradle jar`. 

## Python testing

For benchmarking and comparing the implementations of the algorithms, run the experiment `dijkstratest/main.py`:
```
python3 experiments/dijkstratest/main.py
```

All configuration is in the `main.py`. For running smaller experiments, and not overwriting the CSV files see the function `run_terminal_benchmark_test()` in `main.py`.

If you only want to run the preprocessing of the original graph, run:
```
python3 experiments/dijkstratest/preprocess.py
```

To generate initial plots, run:
```
python3 experiments/dijkstratest/postprocess.py
```

## Preprocessing outside of Python

To start the preprocessing phase and build the augmented Denmark graph outside of the Python experiments, run:

```
gradle jar
java -jar app/build/libs/app.jar process_graph denmark.graph output/augmentedDenmark.graph
```

## Plots and tables for the report

To generate the plots (pgf and png) for the report, run:
```
python3 experiments/dijkstratest/plots_for_report.py
```

To see summary statistics of the experiments and generate the Latex tabulars, see the notebook `experiments/dijkstratest/latex_stats.ipynb`.