package ch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

import ch.Graph.Edge;
import ch.Graph.Vertex;

/**
 * Implements the preprocessing phase of Contraction Hierarchies using the
 * edge difference heuristic with lazy updates.
 */
public class ContractionHierarchy {
    private Graph augmented;
    private Graph original; // so we can compare
    
    /**
     * Constructs a ContractionHierarchy for the given graph.
     * 
     * @param graph the road network graph to preprocess
     */
    public ContractionHierarchy(Graph graph) {
        this.augmented = graph.copy();
        this.original = graph;
        preprocessInfo();
    }

    // getters return copies
    public Graph getAugmented() { return augmented.copy(); }
    public Graph getOriginal() { return original.copy(); }

    /**
     * Performs the preprocessing phase of Contraction Hierarchies.
     * Contracts all vertices in order of their edge difference using lazy updates.
     */
    public void preprocess() {   
        // initialise priority queue with all vertices and their edge differences
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Map<Long, Integer> edgeDifferences = new HashMap<>();
        
        // order vertices by edge difference
        for (long v : augmented.getVertexIds()) {
            int edgeDiff = augmented.getEdgeDifference(v);
            edgeDifferences.put(v, edgeDiff);
            pq.offer(new PQElem(edgeDiff, v));
        }

        // contract vertices in order - update edge difference along the way
        long rank = 0;
        while (!pq.isEmpty()) {

            // get vertex to be contracted
            PQElem elem = pq.poll();
            long v = elem.v;
            int oldEdgeDiff = elem.key;
            if (augmented.getVertex(v).rank != -1) continue; // skip if v has already been contracted
            
            // check and update edge difference (lazy updates)
            int newEdgeDiff = augmented.getEdgeDifference(v);
            if (newEdgeDiff > oldEdgeDiff) {
                edgeDifferences.put(v, newEdgeDiff);
                pq.offer(new PQElem(newEdgeDiff, v));
                continue; // edge diff was updated, so skip contraction
            }
            
            // contract and set rank
            augmented.contract(v, rank);
            rank++;
        }
    }

    
    public void preprocessInfo() {
        // for debugging/reporting on progress
        String title =
            "╔════════════════════════════════════╗\n" +
            "║         PREPROCESSING PHASE        ║\n" +
            "╚════════════════════════════════════╝";
        int V = augmented.getN();
        int lazyUpdates = 0;
        System.out.printf("\n%s\n", title);
        System.out.printf("Input graph has %d vertices and %d edges\n\n", V, augmented.getM());
        System.err.println("=== ORDERING VERTICES ===\n");
        System.out.println("Processing...");
        long startTime = System.currentTimeMillis();
                
        // initialise priority queue with all vertices and their edge differences
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Map<Long, Integer> edgeDifferences = new HashMap<>();
        
        // order vertices by edge difference
        int progress = 0; // debugging
        for (long v : augmented.getVertexIds()) {
            int edgeDiff = augmented.getEdgeDifference(v);
            edgeDifferences.put(v, edgeDiff);
            pq.offer(new PQElem(edgeDiff, v));
            progress++; // debugging
            if (progress % 10000 == 0) {
                System.out.println("  Computed edge differences for " + progress + "/" + V + " vertices");
            }
        }

        // for debugging/reporting on progress
        long orderingElapsed = System.currentTimeMillis() - startTime;
        System.out.printf("\nInitial ordering complete! Time spent: %f\n", orderingElapsed / 1000.0);
        System.out.println("\n=== CONTRACTING VERTICES ===\n");
        System.out.println("Processing...\n");
        
        // contract vertices in order - update edge difference along the way
        long rank = 0;
        while (!pq.isEmpty()) {

            // get vertex to be contracted
            PQElem elem = pq.poll();
            long v = elem.v;
            int oldEdgeDiff = elem.key;
            if (augmented.getVertex(v).rank != -1) continue; // skip if v has already been contracted
            
            // check and update edge difference (lazy updates)
            int newEdgeDiff = augmented.getEdgeDifference(v);
            if (newEdgeDiff > oldEdgeDiff) {
                edgeDifferences.put(v, newEdgeDiff);
                pq.offer(new PQElem(newEdgeDiff, v));
                lazyUpdates++; // for debugging
                continue; // edge diff was updated, so skip contraction
            }
            
            // contract and set rank
            augmented.contract(v, rank);
            rank++;
            
            // for debugging/reporting on progress
            if (rank % 1000 == 0 || rank == V - 1) {
                long elapsed = System.currentTimeMillis() - startTime;
                double percent = (rank * 100.0) / V;
                int queueSize = pq.size();
                int shortcutsAdded = (augmented.getM() - original.getM()) / 2;
                System.out.printf("Progress: %d/%d (%.1f%%) | Time: %.2fs | Queue size: %d | Shortcuts added: %d | Current edgediff: %d\n",
                    rank, V, percent, elapsed / 1000.0, queueSize, shortcutsAdded, newEdgeDiff);
            }
        }
        
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        System.out.printf("\n=== PREPROCESSING COMPLETE ===\n\n");
        System.out.printf("Total time: %.2f seconds\n", totalTime);
        System.out.printf("Vertices contracted: %d\n", rank);
        System.out.printf("Lazy updates: %d\n", lazyUpdates);
        System.out.printf("Shortcuts added: %d\n", (augmented.getM() - original.getM()) / 2);
        System.out.printf("Total (undirected) edges in final graph: %d\n\n", augmented.getM() / 2);        
    }

    public void storeGraph(Graph g, String fileName) {
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(fileName));
            int n = g.getN(), m = g.getM();
            w.write( n + " " + m ); // number of vertices and edges
            for(Map.Entry<Long,Vertex> entry : g.getVertices().entrySet()){
                long id = entry.getKey(), rank = entry.getValue().rank;
                float x = entry.getValue().x, y = entry.getValue().y;
                w.newLine();
                w.write(String.format(Locale.US, "%d %f %f %d", id, x, y, rank));
            }
            for(Map.Entry<Long, List<Edge>> entry : g.getOutEdges().entrySet()) {
                long from = entry.getKey();
                for (Edge e : entry.getValue()) {
                    long to = e.to, contracted = e.contracted;
                    int weight = e.weight;
                    w.newLine();
                    w.write(String.format("%d %d %d %d", from, to, weight, contracted));
                }
            }
            w.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace(); // Print error details
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("small.graph"));
        Graph small = GraphReader.readGraph(sc);
        sc.close();

        ContractionHierarchy ch = new ContractionHierarchy(small);
        ch.storeGraph(small, "smallaugmented.graph");
    }
}