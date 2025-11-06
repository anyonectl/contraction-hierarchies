package ch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import ch.Graph.Edge;
import ch.Graph.Vertex;

public class ContractionHierarchy {

    Graph original; // so we can compare before/after
    Graph augmented;
    PriorityQueue<PQElem> toBeContracted;
    Set<Long> contracted;

    public ContractionHierarchy(Graph original, boolean debug) {
        this.augmented = original;
        this.original = original.copy();
        toBeContracted = new PriorityQueue<>();
        contracted = new HashSet<>();
        if (debug)
            preprocessDebug();
        else
            preprocess();
    }

    private void preprocess() {
        // order vertices by edge difference
        for (long v : augmented.getVertexIds()) {
            int edgeDiff = augmented.getEdgeDifference(v);
            toBeContracted.add(new PQElem(edgeDiff, v)); // O(log(n))
        }

        // contract vertices in order - update edge difference along the way
        int rank = 0;
        while (!toBeContracted.isEmpty()) { // maybe this should be while contracted.size() < original.n or something?
            // get vertex to be contracted
            PQElem current = toBeContracted.poll(); // O(log(n))
            if (contracted.contains(current.v)) continue;

            // check and update edge difference (lazy updates)
            int edgeDiff = augmented.getEdgeDifference(current.v);
            if (current.key < edgeDiff) {
                toBeContracted.add(new PQElem(edgeDiff, current.v)); // O(log(n))
                continue; // edge diff was updated, so skip contraction
            }

            // contract and set rank
            augmented.contract(current.v);
            augmented.getVertex(current.v).setRank(rank);
            contracted.add(current.v);
            rank++;

            // // update neighbours (is this necessary though?)
            // if (augmented.hasNeighbours(current.v)) {
            //     for (Graph.Edge e : augmented.getNeighbours(current.v)) {
            //         long neighbour = e.to;
            //         if (!contracted.contains(neighbour)) {
            //             PQElem elem = new PQElem(augmented.getEdgeDifference(neighbour), neighbour);
            //             // should at least have check here!
            //             toBeContracted.add(elem);
            //         }
            //     }
            // }
        }
    }

    private void preprocessDebug() {
        // for debugging/reporting on progress
        String title =
            "╔════════════════════════════════════╗\n" +
            "║         PREPROCESSING PHASE        ║\n" +
            "╚════════════════════════════════════╝";
        int V = augmented.n;
        int skippedDuplicates = 0;
        int lazyUpdates = 0;
        System.out.printf("\n%s\n", title);
        System.out.printf("Total vertices to order and contract: %d\n\n", V);
        System.err.println("=== ORDERING VERTICES ===\n");
        System.out.println("Processing...");
        long startTime = System.currentTimeMillis();

        // order vertices by edge difference
        for (long v : augmented.getVertexIds()) {
            int edgeDiff = augmented.getEdgeDifference(v);
            toBeContracted.add(new PQElem(edgeDiff, v)); // O(log(n))
        }

        // for debugging/progress reporting
        long orderingElapsed = System.currentTimeMillis() - startTime;
        System.out.printf("\nInitial ordering complete! Time spent: %f\n", orderingElapsed / 1000.0);
        System.out.println("\n=== CONTRACTING VERTICES ===\n");
        System.out.println("Processing...\n");

        // contract vertices in order - update edge difference along the way
        int rank = 0;
        while (!toBeContracted.isEmpty()) { // maybe this should be while contracted.size() < original.n or something?
            // get vertex to be contracted
            PQElem current = toBeContracted.poll(); // O(log(n))
            if (contracted.contains(current.v)) {
                skippedDuplicates++; // for debugging
                continue;
            }

            // check and update edge difference (lazy updates)
            int edgeDiff = augmented.getEdgeDifference(current.v);
            if (current.key < edgeDiff) {
                toBeContracted.add(new PQElem(edgeDiff, current.v)); // O(log(n))
                lazyUpdates++; // for debugging
                continue; // edge diff was updated, so skip contraction
            }

            // contract and set rank
            augmented.contract(current.v);
            augmented.getVertex(current.v).setRank(rank);
            contracted.add(current.v);
            rank++;

            // // update neighbours (is this necessary though?)
            // if (augmented.hasNeighbours(current.v)) {
            //     for (Graph.Edge e : augmented.getNeighbours(current.v)) {
            //         long neighbour = e.to;
            //         if (!contracted.contains(neighbour)) {
            //             PQElem elem = new PQElem(augmented.getEdgeDifference(neighbour), neighbour);
            //             // should at least have check here!
            //             toBeContracted.add(elem);
            //         }
            //     }
            // }

            // for reporting on progress / debugging
            if (rank % 1000 == 0 || rank == V - 1) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = (rank * 100.0) / V;
                int queueSize = toBeContracted.size();
                int shortcutsAdded = (augmented.getM() - original.getM()) / 2;
                System.out.printf("Progress: %d/%d (%.1f%%) | Time: %.2fs | Queue size: %d | Shortcuts added: %d\n",
                    rank, V, progress, elapsed / 1000.0, queueSize, shortcutsAdded);
            }
        }
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        System.out.printf("\n=== PREPROCESSING COMPLETE ===\n\n");
        System.out.printf("Total time: %.2f seconds\n", totalTime);
        System.out.printf("Vertices contracted: %d\n", rank);
        System.out.printf("Lazy updates: %d\n", lazyUpdates);
        System.out.printf("Skipped duplicates: %d\n", skippedDuplicates);
    }

    // not sure about this - seems to have something to do with reporting on the performance of a search??
    public Result<Integer> query(long s, long t) {
        
        // To be filled out

        return new Result<Integer>(0,0,0); 
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

    private static Graph readGraph(Scanner sc) {
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

    
    public static void main(String[] args) throws FileNotFoundException {
        // read graph from file
        File file = new File("denmark.graph");
        Scanner sc = new Scanner(file);
        Graph g = readGraph(sc);
        sc.close();        

        // run preprocessing phase
        ContractionHierarchy ch = new ContractionHierarchy(g, true);

        int originalEdges = ch.original.getM();
        int augmentedEdges = ch.augmented.getM();
        int shortcutsAdded = augmentedEdges - originalEdges;

        System.out.println();
        System.out.println(String.format("Number of edges in original graph: %d", originalEdges));
        System.out.println(String.format("Number of edges in new graph: %d", augmentedEdges));
        System.out.println(String.format("Shortcuts added: %d", shortcutsAdded));
        System.out.println();

        System.out.println("Storing graph...");
        storeGraph(ch.augmented, "output/augmentedWithoutNeighbour.graph");
        System.out.println("Graph stored!");
    }
}
