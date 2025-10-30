package ch;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.PriorityQueue;

public class BidirectionalDijkstra {
        
    /**
     * Computes the shortest path distance between two vertices using bidirectional Dijkstra
     * (running two Dijkstras: one from the source vertex (forwards), one from the target vertex (backwards)).
     * Returns (_, _, -1) if there is no path.
     *
     * @param g         The graph to search.
     * @param source    The source vertex ID.
     * @param target    The target vertex ID.
     * @return          A triple containing the duration (in ns), the number of relaxed edges, and the shortest path distance. The distance is -1 if no path exists.
     */
    public static Result<Integer> shortestPath(Graph g, long source, long target) {
        // for comparisons
        long start = System.nanoTime();
        int relaxed = 0;

        // the shortest path distance to be updated
        int distance = Integer.MAX_VALUE;

        // flag to check if there is a path from source to target
        boolean hasPath = false;

        // initialise set to keep track of visited/settled vertices
        Set<Long> visited = new HashSet<>();

        // initialise distance maps
        Map<Long, Integer> distsSource = new HashMap<>(); 
        Map<Long, Integer> distsTarget = new HashMap<>();
        distsSource.put(source, 0);
        distsTarget.put(target, 0);

        // initialise priority queues
        PriorityQueue<PQElem> pqSource = new PriorityQueue<>();
        PriorityQueue<PQElem> pqTarget = new PriorityQueue<>();
        pqSource.add(new PQElem(0, source));
        pqTarget.add(new PQElem(0, target));

        // start while loop
        while (!pqSource.isEmpty() || !pqTarget.isEmpty()) {
            // choose forwards or backwards Dijkstra
            PriorityQueue<PQElem> pq;
            Map<Long, Integer> dists;
            if (!pqSource.isEmpty() && (pqSource.peek().compareTo(pqTarget.peek()) <= 0)) {
                pq = pqSource;
                dists = distsSource;
            }
            else {
                pq = pqTarget;
                dists = distsTarget;
            }
    
            // settle next vertex
            long current = pq.poll().v;
            if (visited.contains(current)) { // termination condition!
                hasPath = true;
                break;
            }
            visited.add(current);

            // relax all edges incident on current vertex
            for (Graph.Edge e : g.getNeighbours(current)) {
                long to = e.to;
                relaxed++;
                if ((dists.get(current) + e.weight) < dists.get(to)) {
                    // if shorter path to e.to is found,
                    // update dist to e.to and add it to the PQ
                    dists.put(to, dists.get(current) + e.weight); 
                    PQElem elem = new PQElem(dists.get(to), to);
                    pq.add(elem);
                }
                // update distance if new, lower distance between source and target is found
                distance = 
                    (distance > distsSource.get(to) + distsTarget.get(to)) 
                    ? distsSource.get(to) + distsTarget.get(to) : distance;
            }
        }
        long end = System.nanoTime();
        if (!hasPath) return new Result<>(end - start, relaxed, -1);
        return new Result<>(end - start, relaxed, distance);
    }
}