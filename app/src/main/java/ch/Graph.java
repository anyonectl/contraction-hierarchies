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
        private long rank;

        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
            rank = -1; // TODO check that no rank is -1 after preprocessing
        }

        public void setRank(long rank) {
            this.rank = rank;
        }

        public long getRank() {
            return rank;
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

    public void addEdge(long from, long to, int weight) {
        addEdge(from, to, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
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

    // returns the outdegree of vertex v (number of outgoing edges from v) 
    public int degree(long v) {
        return getNeighbours(v) == null ? 0 : this.edges.get(v).size();
    }

    /**
     * Contracts vertex v from the graph by adding necessary shortcuts and marking it as contracted.
     * A shortcut from u to w is added if there is a path u→v→w and there is no witness path from u to w
     * (excluding v) with distance less than or equal to dist(u,v) + dist(v,w).
     *
     * @param v The identifier of the vertex to be contracted.
     * @return  The number of shortcut edges added (counting both directions in undirected graph).
     */
    // https://jlazarsfeld.github.io/ch.150.project/sections/9-adding-shortcuts/
    public int contract(long v) {
        // special case if v has no neighbours
        if (!hasNeighbours(v)) return 0;

        // find neighbours (adjacent vertices) and edge weights
        Map<Long, Integer> adjacent = new HashMap<>();
        for (Edge e : getNeighbours(v)) {
            adjacent.put(e.to, e.weight);
        }

        // for each pair of adjacent vertices (u, w), check if shortcut is needed
        int shortcutsAdded = 0;
        for (long u : adjacent.keySet()) {
            int distVtoU = adjacent.get(u);
            for (long w : adjacent.keySet()) {
                if (u == w) continue;
                int distVtoW = adjacent.get(w);
                int distThroughV = distVtoU + distVtoW;

                // find shortest witness path between u and w
                int witness = Dijkstra.shortestPathOnSubgraph(this, u, w, v, distThroughV);

                // if path through v is a unique shortest path, add shortcut
                if (witness == -1 || witness > distThroughV) {
                    addEdge(u, w, v, distThroughV);
                    shortcutsAdded++;
                }
            }
        }
        return shortcutsAdded;
    }

    // BASICALLY: SIMULATE CONTRACTION
    // Takes the identifier of a vertex v and returns the edge difference after contraction, i.e., computes the change in edges by contracting v
    // From paper: "The change in the number of edges caused by a node contraction 
    // is called edge difference. It is arguably the most important priority term 
    // and we calculate it with a simulated contraction of node u."
    // https://jlazarsfeld.github.io/ch.150.project/sections/12-node-order/
    public int getEdgeDifference(long v) {
        // special case if v has no neighbours
        if (!hasNeighbours(v)) return 0;
                
        // find neighbours (adjacent vertices) and edge weights (same logic as contract)
        Map<Long, Integer> adjacent = new HashMap<>();
        for (Edge e : getNeighbours(v)) {
            adjacent.put(e.to, e.weight);
        }
        
        // for each pair of adjacent vertices (u, w), check if shortcut is needed
        int shortcutsAdded = 0;
        for (long u : adjacent.keySet()) {
            int distVtoU = adjacent.get(u);
            for (long w : adjacent.keySet()) {
                if (u == w) continue;
                int distVtoW = adjacent.get(w);
                int distThroughV = distVtoU + distVtoW;
                
                // find shortest witness path between u and w
                int witness = Dijkstra.shortestPathOnSubgraph(this, u, w, v, distThroughV);
                
                // if path through v is a unique shortest path, increment counter (do not add shortcut)
                if (witness == -1 || witness > distThroughV) {
                    shortcutsAdded++;
                }
            }
        }
        // degree(v) is the number of edges 'removed' (degree(v) counts one direction in undirected graph, but we 'remove' both edges)
        return shortcutsAdded - degree(v);
    }
}
