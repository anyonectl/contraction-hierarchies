package ch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Graph {
    int n; // number of vertices
    int m; // number of edges
    Map<Long, List<Edge>> edges; // outgoing edges
    Map<Long, List<Edge>> incomingEdges; // incoming edges (NEW!)
    private Map<Long, Vertex> vertices; // maps vertex id to Vertex object
    
    // inner class representing vertices
    public static class Vertex {
        float x, y; // coordinates
        private long rank;
        
        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
            rank = -1;
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
        this.incomingEdges = new HashMap<>();
        this.vertices = new HashMap<>();
    }
    
    public int getN() {
        return n;
    }
    
    public int getM() {
        return m;
    }
    
    public void addVertex(long id, Vertex v) {
        this.vertices.put(id, v);
        this.n++;
    }
    
    public void addEdge(long from, long to, long contracted, int weight) {
        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new ArrayList<>());
        }
        this.edges.get(from).add(new Edge(to, weight, contracted));
        
        // Also add to incoming edges
        if (!this.incomingEdges.containsKey(to)) {
            this.incomingEdges.put(to, new ArrayList<>());
        }
        this.incomingEdges.get(to).add(new Edge(from, weight, contracted));
        
        this.m++;
    }
    
    public void addEdge(long from, long to, int weight) {
        addEdge(from, to, -1, weight);
    }
    
    public void addUndirectedEdge(long u, long v, long contracted, int weight) {
        addEdge(u, v, contracted, weight);
        addEdge(v, u, contracted, weight);
    }
    
    public void addUndirectedEdge(long u, long v, int weight) {
        addUndirectedEdge(u, v, -1, weight);
    }
    
    public List<Edge> getNeighbours(long u) {
        return this.edges.get(u);
    }
    
    public List<Edge> getIncomingNeighbours(long u) {
        return this.incomingEdges.get(u);
    }
    
    public boolean hasNeighbours(long u) {
        return getNeighbours(u) != null;
    }
    
    public Vertex getVertex(long id) {
        return this.vertices.get(id);
    }
    
    public Set<Long> getVertexIds() {
        return vertices.keySet();
    }
    
    public Map<Long, Vertex> getVertices() {
        return vertices;
    }
    
    public int degree(long v) {
        return getNeighbours(v) == null ? 0 : this.edges.get(v).size();
    }
    
    /**
     * Computes the edge difference for contracting vertex v.
     * Edge difference = (number of shortcuts added) - (number of edges removed)
     * 
     * @param v the vertex id to compute edge difference for
     * @return the edge difference after contracting v
     */
    public int getEdgeDifference(long v) {
        List<Edge> outEdges = getNeighbours(v);
        List<Edge> inEdges = getIncomingNeighbours(v);
        
        if ((outEdges == null || outEdges.isEmpty()) && (inEdges == null || inEdges.isEmpty())) {
            return 0;
        }
        
        // Get incoming and outgoing neighbors (excluding contracted vertices)
        List<Long> inNeighbors = new ArrayList<>();
        List<Long> outNeighbors = new ArrayList<>();
        
        if (outEdges != null) {
            for (Edge e : outEdges) {
                if (getVertex(e.to).getRank() == -1) { // not yet contracted
                    outNeighbors.add(e.to);
                }
            }
        }
        
        if (inEdges != null) {
            for (Edge e : inEdges) {
                if (getVertex(e.to).getRank() == -1) { // not yet contracted
                    inNeighbors.add(e.to);
                }
            }
        }
        
        // Count edges that will be removed (edges incident to v)
        int edgesRemoved = inNeighbors.size() + outNeighbors.size();
        
        // Count shortcuts needed
        int shortcutsAdded = 0;
        
        for (long u : inNeighbors) {
            for (long w : outNeighbors) {
                if (u == w) continue;
                
                // Find distance u -> v -> w
                int distUV = getEdgeWeight(u, v);
                int distVW = getEdgeWeight(v, w);
                
                if (distUV == -1 || distVW == -1) continue;
                
                int distThroughV = distUV + distVW;
                
                // Check if there's a shorter path from u to w without using v
                if (needsShortcut(u, w, v, distThroughV)) {
                    shortcutsAdded++;
                }
            }
        }
        
        return shortcutsAdded - edgesRemoved;
    }
    
    /**
     * Gets the weight of the edge from u to v, or -1 if no such edge exists.
     */
    private int getEdgeWeight(long u, long v) {
        List<Edge> neighbors = getNeighbours(u);
        if (neighbors == null) return -1;
        
        for (Edge e : neighbors) {
            if (e.to == v) {
                return e.weight;
            }
        }
        return -1;
    }
    
    /**
     * Determines if a shortcut is needed from u to w when contracting v.
     * A shortcut is needed if there's no path from u to w (avoiding v) that is
     * shorter than or equal to maxDist.
     */
    private boolean needsShortcut(long u, long w, long v, int maxDist) {
        // Witness search: limited Dijkstra from u to w avoiding v
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Map<Long, Integer> dist = new HashMap<>();
        
        dist.put(u, 0);
        pq.offer(new PQElem(0, u));
        
        while (!pq.isEmpty()) {
            PQElem current = pq.poll();
            long node = current.v;
            int d = current.key;
            
            if (d > maxDist) break; // No need to search further
            
            if (node == w) {
                return false; // Found a witness path
            }
            
            if (dist.get(node) < d) continue;
            
            List<Edge> neighbors = getNeighbours(node);
            if (neighbors == null) continue;
            
            for (Edge e : neighbors) {
                if (e.to == v) continue; // Avoid the vertex being contracted
                if (getVertex(e.to).getRank() != -1) continue; // Avoid already contracted vertices
                
                int newDist = d + e.weight;
                if (newDist <= maxDist && (!dist.containsKey(e.to) || newDist < dist.get(e.to))) {
                    dist.put(e.to, newDist);
                    pq.offer(new PQElem(newDist, e.to));
                }
            }
        }
        
        return true; // No witness path found, shortcut needed
    }
    
    /**
     * Contracts vertex v by:
     * 1. Adding shortcut edges for all pairs of neighbors that need them
     * 2. Marking v as contracted by setting its rank
     * 
     * @param v the vertex id to contract
     * @param rank the rank to assign to this vertex
     */
    public void contract(long v, long rank) {
        List<Edge> outEdges = getNeighbours(v);
        List<Edge> inEdges = getIncomingNeighbours(v);
        
        if ((outEdges == null || outEdges.isEmpty()) && (inEdges == null || inEdges.isEmpty())) {
            getVertex(v).setRank(rank);
            return;
        }
        
        // Get incoming and outgoing neighbors
        List<Long> inNeighbors = new ArrayList<>();
        List<Long> outNeighbors = new ArrayList<>();
        
        if (outEdges != null) {
            for (Edge e : outEdges) {
                if (getVertex(e.to).getRank() == -1) {
                    outNeighbors.add(e.to);
                }
            }
        }
        
        if (inEdges != null) {
            for (Edge e : inEdges) {
                if (getVertex(e.to).getRank() == -1) {
                    inNeighbors.add(e.to);
                }
            }
        }
        
        // Add shortcuts
        for (long u : inNeighbors) {
            for (long w : outNeighbors) {
                if (u == w) continue;
                
                int distUV = getEdgeWeight(u, v);
                int distVW = getEdgeWeight(v, w);
                
                if (distUV == -1 || distVW == -1) continue;
                
                int distThroughV = distUV + distVW;
                
                if (needsShortcut(u, w, v, distThroughV)) {
                    // Add shortcut edge from u to w
                    addEdge(u, w, v, distThroughV);
                }
            }
        }
        
        // Mark vertex as contracted
        getVertex(v).setRank(rank);
    }
}