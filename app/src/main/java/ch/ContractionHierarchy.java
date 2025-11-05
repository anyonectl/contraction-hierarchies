package ch;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class ContractionHierarchy {

    PriorityQueue<PQElem> toBeContracted; // key is edge difference
    Set<Long> contracted;


    public ContractionHierarchy(AugmentedGraph g) {
        toBeContracted = new PriorityQueue<>();
        contracted = new HashSet<>();
    }

    private void preprocess(AugmentedGraph g) {
        // order vertices by edge difference
        for (long v : g.getVertexIds()) {
            int edgeDiff = g.getEdgeDifference(v);
            toBeContracted.add(new PQElem(edgeDiff, v)); // O(log(n))
        }

        // contract vertices in order - update edge difference along the way
        int rank = 0;
        while (!toBeContracted.isEmpty()) {
            // contract and set rank
            long current = toBeContracted.poll().v; // O(log(n))
            g.contract(current);
            g.getAugVertex(current).setRank(rank);
            contracted.add(current);
            rank++; 

            // update edge difference of next vertex
            if (toBeContracted.isEmpty()) break;
            PQElem next = toBeContracted.peek(); // O(1)
            int edgeDiff = g.getEdgeDifference(next.v);
            if (next.key > edgeDiff) {
                next = toBeContracted.poll(); // O(log(n))
                toBeContracted.add(new PQElem(edgeDiff, next.v)); // O(log(n))
            }
        }
    }

    private void preprocessDebug(AugmentedGraph g) {
        // order vertices by edge difference
        System.out.println("=== ORDERING VERTICES BY EDGE DIFFERENCE ===");
        for (long v : g.getVertexIds()) {
            System.out.println(String.format("Processing vertex id: %d ...", v));
            int edgeDiff = g.getEdgeDifference(v);
            System.out.println(String.format("Computed edge difference: %d ...", edgeDiff));
            toBeContracted.add(new PQElem(edgeDiff, v)); // O(log(n))
            System.out.println(String.format("Vertex %d with edge difference %d added to the PQ!", v, edgeDiff));
        }

        // contract vertices in order - update edge difference along the way
        System.out.println("=== CONTRACTING VERTICES ===");
        int rank = 0;
        while (!toBeContracted.isEmpty()) {
            System.out.println();
            System.out.println(String.format("CURRENT RANK: %d", rank));
            // contract and set rank
            long current = toBeContracted.poll().v; // O(log(n))
            System.out.println(String.format("Contracting vertex %d ...", current));
            g.contract(current);
            System.out.println("Contracted!");
            g.getAugVertex(current).setRank(rank);
            System.out.println(String.format("Rank set to %d", rank));
            contracted.add(current);
            rank++;
            System.out.println(String.format("Number of vertices left in PQ: %d", toBeContracted.size()));

            // update edge difference of next vertex
            if (toBeContracted.isEmpty()) break;
            System.out.println();
            PQElem next = toBeContracted.peek(); // O(1)
            System.out.println(String.format("Checking new edge difference of vertex %d with edge diff %d ...", next.v, next.key));
            int edgeDiff = g.getEdgeDifference(next.v);
            System.out.println(String.format("New edge diff: %d ...", edgeDiff));
            if (next.key > edgeDiff) {
                next = toBeContracted.poll(); // O(log(n))
                System.out.println(String.format("Vertex %d removed from PQ", next.v));
                toBeContracted.add(new PQElem(edgeDiff, next.v)); // O(log(n))
                System.out.println(String.format("Vertex %d added from PQ with new edge diff!", next.v));
            }
        }
    }

    // not sure about this - seems to have something to do with reporting on the performance of a search??
    public Result<Integer> query(long s, long t) {
        
        // To be filled out

        return new Result<Integer>(0,0,0); 
    }

    // TODO Implement part 4.4 'storeGraph'
    // writes the augmented graph (original graph + all shortcuts added) that resulted from running the preprocessing phase.
    public void storeGraph() {
        // To be filled out
    }
}
