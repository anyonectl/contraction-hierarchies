This is a fork of 
https://github.itu.dk/algorithms/contraction-hierarchies-template

This projects works on.. 
```
sdk use java 21.0.8-tem    
sdk use gradle 9.0.0
```

Example run: 
```
gradle jar
java -jar app/build/libs/app.jar < test.graph
```

Test file local URL
file:///Users/sofiemeta/Courses/APAL-25/contraction-hierarchies-template/app/build/reports/tests/test/index.html

test.graph
```
6 5     // n m/2
1 0 0   // n lines of [id, long, lat]
2 0 0
3 0 0
4 0 0
5 0 0
6 0 0
2 6 2   // m lines of [source v, destination v, weight e]
6 1 3
1 4 2
4 3 1
3 5 5
```

Notes: 
In every single step you want to produce as little shortcuts as possible.
The highest rank ..
Multiple shortest paths? Run only on 50 or so edges..? 

## Reading through the code.. 

[Graph.java](app/src/main/java/ch/Graph.java): line 27 
```java
        long contracted; // only used by contraction hierachy, marks the vertex from which this edge resulted.
```
Is this this an ID of the vertex from which this edge resulted? 



## Online resources:

[Contraction Hierarchies: An Illustrative Guide](https://jlazarsfeld.github.io/ch.150.project/sections/6-bidirectional-search/)

## Testing

We can compare the amount of relaxed edges Dijkstra and Bidiractional -latter should relax fewer in theory.

    Nodes with no edges.
    A single node.
    Graphs with multiple paths having the same length.
    Disconnected graphs.
