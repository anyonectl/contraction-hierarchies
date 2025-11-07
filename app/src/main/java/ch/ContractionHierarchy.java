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
            
            System.out.println("\n" + "=".repeat(60));
            
            // Create and run contraction hierarchy preprocessing
            ContractionHierarchy ch = new ContractionHierarchy(g);
            ch.preprocess();
            
            System.out.println("=".repeat(60));
            
            // Display some statistics about the final graph
            System.out.println("\nFinal Statistics:");
            System.out.println("  Original + Shortcut edges: " + g.getM());
            
            // Show ranks of first few vertices (for small test graphs)
            if (g.getN() <= 20) {
                System.out.println("\nVertex Ranks:");
                List<Long> vertexIds = new ArrayList<>(g.getVertexIds());
                Collections.sort(vertexIds);
                for (long v : vertexIds) {
                    System.out.println("  Vertex " + v + ": rank = " + g.getVertex(v).getRank());
                }
            }

            // Store graph
            if (g.getN() > 100) {
                System.out.println("Storing graph...");
                storeGraph(g, "output/denmarkAugmented.graph");
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