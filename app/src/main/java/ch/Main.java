package ch;

import java.util.Scanner;

import ch.Graph.Vertex;

import java.io.File;

class Main {

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

    private static Graph readAugmentedGraph(Scanner sc) {
        int n = sc.nextInt();
        int m = sc.nextInt();

        Graph ag = new Graph();

        // Read vertices (with rank)
        long id;
        float x, y;
        int rank;
        long[] ids = new long[n];
        for (int i = 0; i < n; i++) {
            id = sc.nextLong();

            ids[i] = id;
            x = Float.parseFloat(sc.next());
            y = Float.parseFloat(sc.next());
            rank = sc.nextInt();
            ag.addVertex(id, new Graph.Vertex(x, y, rank));
        }

        // Read edges
        long from, to, contracted;
        int weight;
        for (int i = 0; i < m; i++) {
            from = sc.nextLong();
            to = sc.nextLong();
            weight = sc.nextInt();
            contracted = sc.nextLong();
            ag.addEdge(from, to, contracted, weight);
        }

        return ag;
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Usage: java -jar app.jar <mode> <graphfile>");
            System.exit(1);
        }
        
        String mode = args[0];
        String graphFile = args[1];

        if (mode.startsWith("dijkstratest")) {
            // Read graph from file
            File file = new File(graphFile);
            Scanner graphScanner = new Scanner(file);
            Graph g = readGraph(graphScanner);
            graphScanner.close();

            // Read (s, t) pairs from stdin
            Scanner queryScanner = new Scanner(System.in);
            while (queryScanner.hasNextLong()) {
                long s = queryScanner.nextLong();
                if (!queryScanner.hasNextLong()) break;
                long t = queryScanner.nextLong();

                Result<Integer> res = mode.endsWith("_bidirectional") ? BidirectionalDijkstra.shortestPath(g, s, t) : Dijkstra.shortestPath(g, s, t);

                System.out.printf("%d %d %d %d %d%n",
                        s, t, res.result, res.relaxed, res.time);
            }
            queryScanner.close();
        }


    }
}