package ch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Graph {
    private int n; // number of vertices
    private int m; // number of edges
    private Map<Long, Vertex> vertices; // maps vertex id to Vertex object
    private Map<Long, List<Edge>> outEdges; // maps vertex id to all its outgoing edges
    private Map<Long, List<Edge>> inEdges; // maps vertex id to all its incoming edges
    
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
        this.outEdges = new HashMap<>();
        this.inEdges = new HashMap<>();
        this.vertices = new HashMap<>();
    }
    
    public int getN() {
        return n;
    }
    
    public int getM() {
        return m;
    }
    
    public Map<Long, List<Edge>> getOutEdges() {
        return outEdges;
    }

    public void addVertex(long id, Vertex v) {
        this.vertices.put(id, v);
        this.n++;
    }
    
    public void addEdge(long from, long to, long contracted, int weight) {
        // outgoing
        if (!this.outEdges.containsKey(from)) {
            this.outEdges.put(from, new ArrayList<>()); // only create a new, empty list of outgoing edges if the 'from' vertex isn't already in the map
        }
        this.outEdges.get(from).add(new Edge(to, weight, contracted));
        
        // incoming
        if (!this.inEdges.containsKey(to)) {
            this.inEdges.put(to, new ArrayList<>()); // only create a new, empty list of incoming edges if the 'to' vertex isn't already in the map
        }
        this.inEdges.get(to).add(new Edge(from, weight, contracted));
        
        this.m++;
    }
    
    public void addEdge(long from, long to, int weight) {
        addEdge(from, to, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
    }
    
    public void addUndirectedEdge(long u, long v, long contracted, int weight) {
        addEdge(u, v, contracted, weight);
        addEdge(v, u, contracted, weight);
    }
    
    public void addUndirectedEdge(long u, long v, int weight) {
        addUndirectedEdge(u, v, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
    }
    
    public List<Edge> getNeighbours(long u) {
        return this.outEdges.get(u);
    }
    
    public List<Edge> getInNeighbours(long u) {
        return this.inEdges.get(u);
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
    
    // returns the outdegree of vertex v (number of outgoing edges from u)
    public int outdegree(long u) {
        return !hasNeighbours(u) ? 0 : this.outEdges.get(u).size();
    }

    // returns the indegree of vertex v (number of incoming edges to u)
    public int indegree(long u) {
        return getInNeighbours(u) == null ? 0 : this.inEdges.get(u).size();
    }

    // gets the weight of the edge from u to v, or -1 if no such edge exists.
    private int getEdgeWeight(long u, long v) {
        List<Edge> neighbours = getNeighbours(u);
        if (neighbours == null) return -1;
        for (Edge e : neighbours) {
            if (e.to == v) {
                return e.weight;
            }
        }
        return -1;
    }

    public Graph copy() {
        Graph g = new Graph();
        // Copy vertices
        for (Map.Entry<Long, Vertex> entry : this.vertices.entrySet()) {
            Vertex v = entry.getValue();
            g.addVertex(entry.getKey(), new Vertex(v.x, v.y));
        }
        // Copy edges
        for (Map.Entry<Long, List<Edge>> entry : this.outEdges.entrySet()) {
            long from = entry.getKey();
            for (Edge e : entry.getValue()) {
                g.addEdge(from, e.to, e.weight);
            }
        }
        return g;
    }
    
    /**
     * Computes the edge difference (change in edges) by contracting vertex v.
     * Edge difference = (number of shortcuts added) - (number of edges removed)
     * 
     * @param v the vertex id to compute edge difference for
     * @return the edge difference after contracting v
     */
    public int getEdgeDifference(long v) {
        List<Edge> outEdges = getNeighbours(v); 
        List<Edge> inEdges = getInNeighbours(v);

        // special case if v has no neighbours
        if ((outEdges == null || outEdges.isEmpty()) && (inEdges == null || inEdges.isEmpty())) {
            return 0;
        }
        
        // find neighbours that aren't contracted yet
        List<Long> outNeighbours = new ArrayList<>();
        List<Long> inNeighbours = new ArrayList<>();
        if (outEdges != null) {
            for (Edge e : outEdges) {
                if (getVertex(e.to).getRank() == -1) { // not yet contracted
                    outNeighbours.add(e.to);
                }
            }
        }
        if (inEdges != null) {
            for (Edge e : inEdges) {
                if (getVertex(e.to).getRank() == -1) { // not yet contracted
                    inNeighbours.add(e.to);
                }
            }
        }        
        
        // for each pair of v-adjacent vertices (u, w), check if shortcut is needed
        int edgesRemoved = inNeighbours.size() + outNeighbours.size();
        int shortcutsAdded = 0;
        for (long u : inNeighbours) {
            for (long w : outNeighbours) {
                if (u == w) continue;
                int distUtoV = getEdgeWeight(u, v); // TODO should this be in the outer loop?
                int distVtoW = getEdgeWeight(v, w);
                if (distUtoV == -1 || distVtoW == -1) continue; // skip if one of the edges don't exist
                int distThroughV = distUtoV + distVtoW;
                
                // check if there's a shorter path from u to w without using v (witness path search)
                if (needsShortcut(u, w, v, distThroughV)) {
                    shortcutsAdded++;
                }
            }
        }
        return shortcutsAdded - edgesRemoved;
    }
    
    /**
     * Performs a search for witness paths using limited Dijkstra from to w avoiding v
     * and thereby determines if a shortcut is needed from u to w when contracting v.
     * A shortcut is needed if there's no path from u to w (avoiding v) that is
     * shorter than or equal to maxDist (witness path).
     */
    private boolean needsShortcut(long u, long w, long v, int maxDist) {
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Map<Long, Integer> dists = new HashMap<>();
        
        dists.put(u, 0);
        pq.offer(new PQElem(0, u));
        
        while (!pq.isEmpty()) {
            PQElem current = pq.poll();
            long node = current.v;
            int dist = current.key;
            if (dist > maxDist) break; // maxDist reached, next dists will be larger, so no need to search further
            if (node == w) return false; // we found a witness path, stop search
            if (dists.get(node) < dist) continue; // TODO what happens here
            List<Edge> neighbours = getNeighbours(node);
            if (neighbours == null) continue; // skip if current node has no neighbours
            
            // relax edges
            for (Edge e : neighbours) {
                if (e.to == v) continue; // exclude the vertex being contracted
                if (getVertex(e.to).getRank() != -1) continue; // exclude vertices that have already been contracted
                
                int newDist = dist + e.weight;
                // only consider paths with dist smaller than maxDistance
                if (newDist <= maxDist && (!dists.containsKey(e.to) || newDist < dists.get(e.to))) {
                    dists.put(e.to, newDist);
                    pq.offer(new PQElem(newDist, e.to));
                }
            }
        }
        return true; // no witness path found, shortcut needed
    }
    
    /**
     * Contracts vertex v by:
     * 1. Adding shortcut edges for all pairs of neighbours that need them
     * 2. Marking v as contracted by setting its rank
     * 
     * @param v the vertex id to contract
     * @param rank the rank to assign to this vertex
     */
    public void contract(long v, long rank) {
        List<Edge> outEdges = getNeighbours(v);
        List<Edge> inEdges = getInNeighbours(v);
        
        // special case if v has no neighbours 
        if ((outEdges == null || outEdges.isEmpty()) && (inEdges == null || inEdges.isEmpty())) {
            getVertex(v).setRank(rank);
            return;
        }
        
        // find neighbours that aren't contracted yet
        List<Long> inNeighbours = new ArrayList<>();
        List<Long> outNeighbours = new ArrayList<>();
        if (outEdges != null) {
            for (Edge e : outEdges) {
                if (getVertex(e.to).getRank() == -1) { // not yet contracted
                    outNeighbours.add(e.to);
                }
            }
        }
        if (inEdges != null) {
            for (Edge e : inEdges) {
                if (getVertex(e.to).getRank() == -1) { // not yet contracted
                    inNeighbours.add(e.to);
                }
            }
        }
        
        // search for witness paths and add shortcut if needed
        for (long u : inNeighbours) {
            for (long w : outNeighbours) {
                if (u == w) continue;
                int distUtoV = getEdgeWeight(u, v); // TODO should this be in the outer loop?
                int distVtoW = getEdgeWeight(v, w);
                if (distUtoV == -1 || distVtoW == -1) continue; // skip if one of the edges don't exist
                int distThroughV = distUtoV + distVtoW;
                
                // witness path search
                if (needsShortcut(u, w, v, distThroughV)) {
                    addEdge(u, w, v, distThroughV); // only add shortcut if u->v->w (path through v) is a unique shortest path
                }
            }
        }
        // mark v as contracted by setting the rank
        getVertex(v).setRank(rank);
    }
}