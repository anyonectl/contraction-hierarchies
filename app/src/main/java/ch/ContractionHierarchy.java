package ch;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Implements the preprocessing phase of Contraction Hierarchies using the
 * edge difference heuristic with lazy updates.
 */
public class ContractionHierarchy {
    private Graph graph;
    
    /**
     * Constructs a ContractionHierarchy for the given graph.
     * 
     * @param graph the road network graph to preprocess
     */
    public ContractionHierarchy(Graph graph) {
        this.graph = graph;
    }
    
    /**
     * Performs the preprocessing phase of Contraction Hierarchies.
     * Contracts all vertices in order of their edge difference using lazy updates.
     */
    public void preprocess() {
        System.out.println("Starting Contraction Hierarchy preprocessing...");
        System.out.println("Graph has " + graph.getN() + " vertices and " + graph.getM() + " edges");
        
        long startTime = System.currentTimeMillis();
        
        // Initialize priority queue with all vertices and their edge differences
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Map<Long, Integer> edgeDifferences = new HashMap<>();
        
        System.out.println("Computing initial edge differences...");
        int progress = 0;
        int totalVertices = graph.getN();
        
        for (long v : graph.getVertexIds()) {
            int edgeDiff = graph.getEdgeDifference(v);
            edgeDifferences.put(v, edgeDiff);
            pq.offer(new PQElem(edgeDiff, v));
            
            progress++;
            if (progress % 10000 == 0) {
                System.out.println("  Computed edge differences for " + progress + "/" + totalVertices + " vertices");
            }
        }
        
        System.out.println("Initial edge differences computed.");
        System.out.println("Beginning contraction phase...");
        
        long rank = 0;
        int contracted = 0;
        
        while (!pq.isEmpty()) {
            PQElem elem = pq.poll();
            long v = elem.v;
            int oldEdgeDiff = elem.key;
            
            // Check if vertex is already contracted
            if (graph.getVertex(v).getRank() != -1) {
                continue;
            }
            
            // Lazy update: recompute edge difference
            int newEdgeDiff = graph.getEdgeDifference(v);
            
            // If edge difference has changed significantly, re-insert into queue
            if (newEdgeDiff > oldEdgeDiff) {
                edgeDifferences.put(v, newEdgeDiff);
                pq.offer(new PQElem(newEdgeDiff, v));
                continue;
            }
            
            // Contract the vertex
            graph.contract(v, rank);
            rank++;
            contracted++;
            
            // Progress update
            if (contracted % 1000 == 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                double percent = (contracted * 100.0) / totalVertices;
                System.out.printf("  Contracted %d/%d vertices (%.2f%%) - Time: %.2fs - Current edge diff: %d%n",
                    contracted, totalVertices, percent, elapsed / 1000.0, newEdgeDiff);
            }
        }
        
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        
        System.out.println("\nPreprocessing complete!");
        System.out.println("Total vertices contracted: " + contracted);
        System.out.println("Final graph has " + graph.getM() + " edges (including shortcuts)");
        System.out.println("Total preprocessing time: " + String.format("%.2f", totalTime) + " seconds");
        
        // Verify all vertices have ranks
        verifyRanks();
    }
    
    /**
     * Verifies that all vertices have been assigned a rank during preprocessing.
     */
    private void verifyRanks() {
        int unranked = 0;
        for (long v : graph.getVertexIds()) {
            if (graph.getVertex(v).getRank() == -1) {
                unranked++;
            }
        }
        
        if (unranked > 0) {
            System.err.println("WARNING: " + unranked + " vertices were not assigned a rank!");
        } else {
            System.out.println("Verification: All vertices successfully ranked.");
        }
    }
    
    /**
     * Gets the preprocessed graph.
     * 
     * @return the graph with contraction hierarchy
     */
    public Graph getGraph() {
        return graph;
    }
}