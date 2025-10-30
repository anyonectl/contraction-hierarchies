package ch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Graph {
    int n; // number of vertices
    int m; // number of edges

    // inner class representing vertices
    public static class Vertex {
        float x, y; // coordinates

        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    // inner class representing edges
    public class Edge {
        long to; // id of destination vertex
        int weight;
        long contracted; // only used by contraction hierarchy, marks the vertex from which this edge resulted.

        public Edge(long to, int weight, long contracted) {
            this.to = to;
            this.weight = weight;
            this.contracted = contracted;
        }
    }

    private Map<Long, List<Edge>> edges; // the long represents the 'from' vertex - and it is the key to a list of outgoing edges
    private Map<Long, Vertex> vertices; // maps vertex id to Vertex object

    // Creates an empty graph
    public Graph() {
        this.n = 0;
        this.m = 0;
        this.edges = new HashMap<>();
        this.vertices = new HashMap<>();
    }

    public void addVertex(long id, Vertex v) {
        this.vertices.put(id, v); // add vertex to the map with the given id
        this.n++; // increment number of vertices
    }

    public void addEdge(long from, long to, long contracted, int weight) {
        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new ArrayList<>()); // only create a new, empty list of outgoing edges if the 'from' vertex isn't already in the map of vertex-edge connections
        }
        this.edges.get(from).add(new Edge(to, weight, contracted)); // get the source vertex id's list of outgoing edges and add a new edge to it
        this.m++; // increment number of edges
    }

    // undirected edges are simulated by adding directed edges in both directions, so the count that is printed is twice as large as the number in the graph provided
    public void addUndirectedEdge(long u, long v, long contracted, int weight) {
        addEdge(u, v, contracted, weight);
        addEdge(v, u, contracted, weight);
    }

    public void addUndirectedEdge(long u, long v, int weight) {
        addUndirectedEdge(u, v, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
    }

    // returns the list of edges going out from vertex u
    public List<Edge> getNeighbours(long u) {
        return this.edges.get(u);
    }

    // returns the Vertex object with the given id
    public Vertex getVertex(long id) {
        return this.vertices.get(id);
    }

    // returns the degree of vertex v (number of outgoing edges from v)
    public int degree(long v) {
        return this.edges.get(v).size();
    }

    // TODO Implement part 4.2 'contract'
    // Takes the identifier of a vertex v as argument and carries out the contraction of v in the current graph
    public int contract(long v) {
        // To be filled out
        return 0; // Should return something
    }

    // TODO Implement part 4.1 'getEdgeDifference'
    // Takes the identifier of a vertex v and returns the edge difference after contraction, i.e., computes the change in edges by contracting v
    public int getEdgeDifference(long v) {
        // To be filled out
        return 0; // Should return something
    }
}
