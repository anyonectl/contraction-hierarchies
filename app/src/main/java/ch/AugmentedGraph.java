package ch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AugmentedGraph extends Graph {

    public class AugmentedEdge extends Edge {
        public long contracted;  // only used by contraction hierarchy, marks the vertex from which this edge resulted.

        public AugmentedEdge(long to, int weight, long contracted) {
            super(to, weight);
            this.contracted = contracted; 
        }
    }

    public class AugmentedVertex extends Vertex {
        public int rank;
        public AugmentedVertex(float x, float y, int rank) {
            super(x, y);
            this.rank = rank;
        }
    }

    public AugmentedGraph() {
        super();
    }

    // Checks type and returns augmented vertex, else null
    public AugmentedVertex getAugmentedVertex(long id) {
        Vertex v = this.vertices.get(id);
        if (v instanceof AugmentedVertex) 
            return (AugmentedVertex) v;
        return null;
    }

    public void addEdge(long from, long to, long contracted, int weight) {
        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new ArrayList<>());
        }
        this.edges.get(from).add(new AugmentedEdge(to, weight, contracted));
        this.m++;
    }

    @Override
    public void addEdge(long from, long to, int weight) {
        addEdge(from, to, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
    }

    public void addUndirectedEdge(long u, long v, long contracted, int weight) {
        addEdge(u, v, contracted, weight);
        addEdge(v, u, contracted, weight);
    }

    @Override
    public void addUndirectedEdge(long u, long v, int weight) {
        addUndirectedEdge(u, v, -1, weight); // -1 indicates that the edge is part of the original graph and not a shortcut created during contraction
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

                // if path through v is a uniqe shortest path, add shortcut
                if (witness == -1 || witness > distThroughV) {
                    addEdge(u, w, v, distThroughV);
                    shortcutsAdded++;
                }
            }
        }
        return shortcutsAdded;
    }

    // TODO Implement part 4.1 'getEdgeDifference'
    // Takes the identifier of a vertex v and returns the edge difference after contraction, i.e., computes the change in edges by contracting v
    // https://jlazarsfeld.github.io/ch.150.project/sections/12-node-order/
    public int getEdgeDifference(long v) {
        // To be filled out
        return 0; // Should return something
    }

    // public static void main(String[] args) {
    //     AugmentedGraph g = new AugmentedGraph();
    //     // Star graph with center vertex 5
    //     g.addVertex(1, new Graph.Vertex(0, 0));
    //     g.addVertex(2, new Graph.Vertex(1, 0));
    //     g.addVertex(3, new Graph.Vertex(2, 0));
    //     g.addVertex(5, new Graph.Vertex(1, 1));
        
    //     g.addUndirectedEdge(1, 5, 2);
    //     g.addUndirectedEdge(2, 5, 3);
    //     g.addUndirectedEdge(3, 5, 4);
        
    //     // Contract center vertex 5
    //     // Should add shortcuts: 1-2, 1-3, 2-3 (all pairs)
    //     int shortcutsAdded = g.contract(5);
        
    //     // 3 neighbors, so 3*2 = 6 directed edges (or 3 pairs * 2 directions each)
    //     System.out.println(String.format("Expected %d, actual %d",6, shortcutsAdded));
    // }
}
