package ch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    int n; // number of vertices
    int m; // number of edges
    Map<Long, List<Edge>> edges; // the long represents the 'from' vertex - and it is the key to a list of outgoing edges
    private Map<Long, Vertex> vertices; // maps vertex id to Vertex object

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

        public Edge(long to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

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

    public void addEdge(long from, long to, int weight) {
        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new ArrayList<>()); // only create a new, empty list of outgoing edges if the 'from' vertex isn't already in the map of vertex-edge connections
        }
        this.edges.get(from).add(new Edge(to, weight)); // get the source vertex id's list of outgoing edges and add a new edge to it
        this.m++; // increment number of edges
    }

    // public void addEdge(long from, long to, int weight) {
    //     addEdge(from, to, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
    // }


    // undirected edges are simulated by adding directed edges in both directions, so the count that is printed is twice as large as the number in the graph provided
    public void addUndirectedEdge(long u, long v, int weight) {
        addEdge(u, v, weight);
        addEdge(v, u, weight);
    }

    // public void addUndirectedEdge(long u, long v, int weight) {
    //     addUndirectedEdge(u, v, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
    // }

    // returns the list of edges going out from vertex u
    public List<Edge> getNeighbours(long u) {
        return this.edges.get(u);
    }

    // returns false if vertex u has no outgoing edges
    public boolean hasNeighbours(long u) {
        return getNeighbours(u) != null;
    }

    // returns the Vertex object with the given id
    public Vertex getVertex(long id) {
        return this.vertices.get(id);
    }

    public Set<Long> getVertexIds() {
        return vertices.keySet();
    }

    public Map<Long, Vertex> getVertices() {
        return vertices;
    }

    // returns the outdegree of vertex v (number of outgoing edges from v), 
    public int degree(long v) {
        return getNeighbours(v) == null ? 0 : this.edges.get(v).size();
    }
}
