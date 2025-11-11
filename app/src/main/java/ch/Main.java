package ch;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import ch.Graph.Edge;
import ch.Graph.Vertex;

import java.io.File;
import java.io.PrintWriter;

class Main {

    // Read (s, t) pairs from stdin
    private static void query(Scanner queryScanner, boolean bidirectional, boolean augmented, Graph g) {
            while (queryScanner.hasNextLong()) {
                long s = queryScanner.nextLong();
                if (!queryScanner.hasNextLong()) break;
                long t = queryScanner.nextLong();
                Result<Integer> res = null;

                if (augmented) {
                    res = BidirectionalDijkstra.bidirectionalSearch(g, s, t);
                } else {
                    res = bidirectional ? BidirectionalDijkstra.shortestPath(g, s, t) : Dijkstra.shortestPath(g, s, t);
                }

                System.out.printf("%d %d %d %d %d%n",
                                  s, t, res.result, res.relaxed, res.time);
            }
    }

    // prints graph in terminal (accessible from all classes)
    public static void printGraph(Graph g) {
        PrintWriter w = new PrintWriter(System.out, true);
        int n = g.getN(), m = g.getM();

        w.printf("%d %d%n", n, m);

        for (Map.Entry<Long, Vertex> entry : g.getVertices().entrySet()) {
            long id = entry.getKey();
            Vertex v = entry.getValue();
            long rank = v.rank;
            float x = v.x, y = v.y;

            w.printf(Locale.US, "%d %f %f %d%n", id, x, y, rank);
        }

        for (Map.Entry<Long, List<Edge>> entry : g.getOutEdges().entrySet()) {
            long from = entry.getKey();
            for (Edge e : entry.getValue()) {
                long to = e.to, contracted = e.contracted;
                int weight = e.weight;
                w.printf("%d %d %d %d%n", from, to, weight, contracted);
            }
        }

        w.flush();
    }

    public static void main(String[] args) throws Exception {
        
        // Gather args 
        String use = args[0]; // run_queries, process_graph 
        
        if (use.equals("run_queries")) { // Usage: java -jar app.jar <use> <augmented> <bidirectional> <graphfile>
            boolean augmented = args[1].equals("augmented"); // augmented or original
            boolean bidirectional = args[2].equals("bidirectional"); // bidirectional or standard
            File graphFile = new File(args[3]);
    
            // Scan graph
            Scanner graphScanner = new Scanner(graphFile);
            Graph g = augmented ? GraphReader.readAugmentedGraph(graphScanner) : GraphReader.readGraph(graphScanner);
            graphScanner.close();
    
            // Query graph
            Scanner queryScanner = new Scanner(System.in);
            query(queryScanner, bidirectional, augmented, g);
            queryScanner.close();
        } 
        else if (use.equals("process_graph")) { // Usage: java -jar app.jar <use> <graphfile> <outputpath>
            File graphFile = new File(args[1]);
            String outputPath = args[2];

            Scanner graphScanner = new Scanner(graphFile);
            Graph g = GraphReader.readGraph(graphScanner);
            graphScanner.close();

            System.out.println("Starting preprocess...");
            ContractionHierarchy ch = new ContractionHierarchy(g);
            System.out.println("Finished preprocessing!\n");

            System.out.println("\nStoring graph...");
            ch.storeGraph(ch.getAugmented(), outputPath);
            System.out.println("Graph stored!");
        } else {
            System.out.println("Argument not recognised.");
        }
    }
}