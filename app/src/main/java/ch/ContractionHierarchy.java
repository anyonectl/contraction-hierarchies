package ch;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class ContractionHierarchy {

    AugmentedGraph original; // so we can compare before/after
    AugmentedGraph g;
    PriorityQueue<PQElem> toBeContracted;
    Set<Long> contracted;


    public ContractionHierarchy(AugmentedGraph original) {
        this.original = original;
        this.g = original;
        toBeContracted = new PriorityQueue<>();
        contracted = new HashSet<>();
        preprocess();
    }

    private void preprocess() {
        // order vertices by edge difference
        for (long v : g.getVertexIds()) {
            int edgeDiff = g.getEdgeDifference(v);
            toBeContracted.add(new PQElem(edgeDiff, v)); // O(log(n))
        }

        // contract vertices in order - update edge difference along the way
        int rank = 0;
        while (!toBeContracted.isEmpty()) {
            // get vertex to be contracted
            PQElem current = toBeContracted.poll(); // O(log(n))
            if (contracted.contains(current.v)) continue;

            // check and update edge difference (lazy updates)
            int edgeDiff = g.getEdgeDifference(current.v);
            if (current.key < edgeDiff) {
                toBeContracted.add(new PQElem(edgeDiff, current.v)); // O(log(n))
                continue; // edge diff was updated, so skip contraction
            }

            // contract and set rank
            g.contract(current.v);
            g.getAugVertex(current.v).setRank(rank);
            contracted.add(current.v);
            rank++;
        }
    }

    private void preprocessDebug(AugmentedGraph g) {
        // for debugging/reporting on progress
        long startTime = System.currentTimeMillis();
        int V = g.n;
        int skippedDuplicates = 0;
        int lazyUpdates = 0;

        System.err.println("=== ORDERING VERTICES BY EDGE DIFFERENCE ===");
        System.out.println();
        System.out.println("Processing...");
        // order vertices by edge difference
        for (long v : g.getVertexIds()) {
            int edgeDiff = g.getEdgeDifference(v);
            toBeContracted.add(new PQElem(edgeDiff, v)); // O(log(n))
        }
        long orderingElapsed = System.currentTimeMillis() - startTime;
        System.out.println();
        System.out.println("Initial ordering complete!");
        System.out.println("Time spent so far: " + orderingElapsed / 1000.0);
        System.out.println();

        System.out.println("=== CONTRACTING VERTICES ===");
        System.out.println("Total vertices: " + V);
        System.out.println();
        System.out.println("Processing...");
        // contract vertices in order - update edge difference along the way
        int rank = 0;
        while (!toBeContracted.isEmpty()) {
            // get vertex to be contracted
            PQElem current = toBeContracted.poll(); // O(log(n))
            if (contracted.contains(current.v)) {
                skippedDuplicates++;
                continue;
            }

            // check and update edge difference (lazy updates)
            int edgeDiff = g.getEdgeDifference(current.v);
            if (current.key < edgeDiff) {
                toBeContracted.add(new PQElem(edgeDiff, current.v)); // O(log(n))
                lazyUpdates++;
                continue; // edge diff was updated, so skip contraction
            }

            // contract and set rank
            g.contract(current.v);
            g.getAugVertex(current.v).setRank(rank);
            contracted.add(current.v);
            rank++;

            // only for reporting on progress / debugging
            if (rank % 1000 == 0 || rank == V - 1) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = (rank * 100.0) / V;
                int queueSize = toBeContracted.size();

                System.out.printf("Progress: %d/%d (%.1f%%) | Time: %.2fs | Queue size: %d\n",
                    rank, V, progress, elapsed / 1000.0, queueSize);
            }
        }
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        System.out.println("\n=== PREPROCESSING COMPLETE ===");
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

    // TODO Implement part 4.4 'storeGraph'
    // writes the augmented graph (original graph + all shortcuts added) that resulted from running the preprocessing phase.
    public void storeGraph() {
        // To be filled out
    }
}
