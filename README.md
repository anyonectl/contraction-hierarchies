# Template Contraction Hierarchies Assignment

This repository presents a Java-based template to start with the contraction hierarchies assignment in Applied algorithms. 

## Build

The project uses gradle. Carry out the tests using `gradle test` and build the jar file using `gradle jar`. 

The graph is read from standard input and a test graph `test.graph` is provided in this repository. 

Basic file reading is already implemented in the Main class. An example run is 

```
gradle jar
java -jar app/build/libs/app.jar < test.graph
```

(Note that we simulate undirected edges by adding directed edges in both directions, so the count that is printed is twice as large as the number in the graph provided.)

## Python testing

For benchmarking and comparing the implementation of Dijkstra and Bidirectional Dijkstra run the project `dijkstratest`.
```
python3 dijkstratest/main.py 
```

