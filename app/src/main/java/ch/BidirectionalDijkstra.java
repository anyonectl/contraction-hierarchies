package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class BidirectionalDijkstra {

    public static Result<Integer> shortestPath(Graph g, long start, long target) {
        long begin = System.nanoTime(); // to keep track of the duration (in ns) of running Dijkstra
        
        // start
        PriorityQueue<PQElem> pqL = new PriorityQueue<>(); 
        Map<Long, Integer> distsL = new HashMap<>();
        pqL.add(new PQElem(0, start));
        distsL.put(start, 0);

        // target 
        PriorityQueue<PQElem> pqR = new PriorityQueue<>();
        Map<Long, Integer> distsR = new HashMap<>();
        pqR.add(new PQElem(0, target));
        distsR.put(target, 0);
        
        // other initializations
        Set<Long> visited = new HashSet<>(); // settled
        int relaxed = 0; // counter for number of relaxed edges
        int d = Integer.MAX_VALUE;// dist is the shortest known distance to the current vertex u

        while (!pqL.isEmpty() || !pqR.isEmpty()) {
            PQElem elemL = pqL.peek(); // retrieve, but not remove
            PQElem elemR = pqR.peek();
            PQElem i = null; // yet to be determined: get the next element of the MinPQ (the PQElem with the smallest key/distance to the source)
            boolean leftPriority = elemL.compareTo(elemR) > 0; // l is higher rank than r

            if (!pqL.isEmpty() && leftPriority) // L is not empty and left has priority
                i = pqL.poll(); // TODO maybe we only need u not also i. We do need to remove deleteMin either way
            else
                i = pqR.poll();

            long u = i.v; // yet to be determined: u is the id of the current vertex being processed
            if (visited.contains(u)) 
                // continue; // This vertex was settled by the other instance
                break; // is this the only place we stop? 
            visited.add(u);

            int dist = i.key;
            for (Graph.Edge e : g.getNeighbours(u)) { // for each outgoing edge from the current vertex u:
                relaxed++; // increment the number of relaxed edges

                long v = e.to; // v is the id of the destination vertex of the current edge
                int w = e.weight; // c(u,v) or weight of the current edge

                if (leftPriority) {
                    if (!distsL.containsKey(v) || dist + w < distsL.get(v)) {
                        distsL.put(v, dist + w); // add/update the shortest known distance map with the new shorter distance
                        pqL.add(new PQElem(dist + w, v)); // add v to the PQ with the updated shorter distance
                    }
                } 
                else {
                    if (!distsR.containsKey(v) || dist + w < distsR.get(v)) {
                        distsR.put(v, dist + w);
                        pqR.add(new PQElem(dist + w, v));
                    }
                }
                d = Integer.min(dist, distsL.get(v) + distsR.get(v)); // line 21
            }
        }

        long end = System.nanoTime();
        // Doesn't matter which one we return as they should both include eachother and the same dist
        if (!distsL.containsKey(target)) { // if there is no path from 'from' to 'to'
            return new Result<>(end - begin, relaxed, -1); // -1 for no path
        }

        return new Result<>(end - begin, relaxed, d);
    }    
}