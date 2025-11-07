package ch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        this.augmented = graph;
        this.original = graph.copy();
        preprocess();
    }
    
    /**
     * Performs the preprocessing phase of Contraction Hierarchies.
     * Contracts all vertices in order of their edge difference using lazy updates.
     */
    public void preprocess() {
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
            if (augmented.getVertex(v).getRank() != -1) continue; // skip if v has already been contracted
            
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
    
    /**
     * Verifies that all vertices have been assigned a rank during preprocessing.
     */
    private void verifyRanks() {
        int unranked = 0;
        for (long v : augmented.getVertexIds()) {
            if (augmented.getVertex(v).getRank() == -1) {
                unranked++;
            }
        }
        
        if (unranked > 0) {
            System.err.println("WARNING: " + unranked + " vertices were not assigned a rank!");
        } else {
            System.out.println("All vertices were successfully ranked.");
        }
    }

    public static Graph readGraph(Scanner sc) {
        int n = sc.nextInt(); // number of vertices from input
        int m = sc.nextInt(); // number of edges from input

        Graph g = new Graph(); // create empty graph

        long id;
        float x, y;
        long[] ids = new long[n]; // array to store vertex ids

        // read vertices from input
        // the following n lines each represents a vertex and contains 3 numbers: vertex id (long), vertex longitude (float) and vertex latitude (float)
        for (int i = 0; i < n; i++) { 
            id = sc.nextLong(); // vertex id

            ids[i] = id; // store id in the array of vertex ids at the current index
            x = Float.parseFloat(sc.next()); // longitude of this vertex
            y = Float.parseFloat(sc.next()); // latitude of this vertex

            g.addVertex(id, new Graph.Vertex(x, y)); // add the vertex from this line of input to the graph
        }

        long from, to;
        int weight;

        // read edges from input
        // the following m lines each represents an edge and contains 3 numbers: id of source vertex (long), id of destination vertex (long) and weight/cost of the edge (integer)
        for (int i = 0; i < m; i++) {
            from = sc.nextLong(); // id of source vertex
            to = sc.nextLong(); // id of destination vertex
            weight = sc.nextInt(); // weight/cost of the edge
            g.addUndirectedEdge(from, to, weight); // add the (undirected) edge from this line of input to the graph
        }
        return g; // return the built graph
    }

    public static void storeGraph(Graph g, String fileName) {
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(fileName));
            int n = g.getN(), m = g.getM();
            w.write( n + " " + m ); // number of vertices and edges
            for(Map.Entry<Long,Vertex> entry : g.getVertices().entrySet()){
                long id = entry.getKey(), rank = entry.getValue().getRank();
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

    public static void main(String[] args) {
        try {
            // read graph from file
            File file = new File("denmark.graph");
            Scanner sc = new Scanner(file);
            Graph g = readGraph(sc);
            sc.close();        
                        
            // run contraction hierarchy preprocessing and check all vertices have ranks
            ContractionHierarchy ch = new ContractionHierarchy(g);
            ch.verifyRanks();
                                    
            // show ranks of first few vertices (for small test graphs)
            if (g.getN() <= 20) {
                System.out.println("\nVertex Ranks:");
                List<Long> vertexIds = new ArrayList<>(g.getVertexIds());
                Collections.sort(vertexIds);
                for (long v : vertexIds) {
                    System.out.println("  Vertex " + v + ": rank = " + g.getVertex(v).getRank());
                }
            }

            // store graph
            if (g.getN() > 100) {
                System.out.println("\nStoring graph...");
                storeGraph(g, "output/testDelete.graph");
                System.out.println("Graph stored!");
            }
            
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error during preprocessing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}