package ch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import ch.Graph.Edge;
import ch.Graph.Vertex;

public class Main {

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
            int n = g.n, m = g.m;
            w.write( n + " " + m ); // number of vertices and edges
            for(Map.Entry<Long,Vertex> entry : g.getVertices().entrySet()){
                long id = entry.getKey(), rank = entry.getValue().getRank();
                float x = entry.getValue().x, y = entry.getValue().y;
                w.newLine();
                w.write(String.format(Locale.US, "%d %f %f %d", id, x, y, rank));
            }
            for(Map.Entry<Long, List<Edge>> entry : g.edges.entrySet()) {
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
