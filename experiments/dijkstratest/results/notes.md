=== Running Standard Dijkstra ===
Generated 1000 random (s, t) pairs
Running: java -jar app/build/libs/app.jar run_queries original standard denmark.graph

=== Benchmark Summary ===
Queries:       1000
Reachable:     1000 / 1000
Avg time:      153.001 ms
Avg relaxed:   593374.0
Saved results to /Users/sofiemeta/Courses/apal/contraction-hierarchies/experiments/dijkstratest/results/standard_1000_results.csv

=== Running Bidirectional Dijkstra ===
Generated 1000 random (s, t) pairs
Running: java -jar app/build/libs/app.jar run_queries original bidirectional denmark.graph

=== Benchmark Summary ===
Queries:       1000
Reachable:     1000 / 1000
Avg time:      127.485 ms
Avg relaxed:   476718.2
Saved results to /Users/sofiemeta/Courses/apal/contraction-hierarchies/experiments/dijkstratest/results/bidirectional_1000_results.csv

=== Running Bidirectional Dijkstra ===
Generated 1000 random (s, t) pairs
Running: java -jar app/build/libs/app.jar run_queries augmented bidirectional output/augmentedDenmark.graph

=== Benchmark Summary ===
Queries:       1000
Reachable:     1000 / 1000
Avg time:      0.377 ms
Avg relaxed:   5354.2