package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

    /**
     * Computes the shortest path distance between two vertices using Dijkstra's algorithm.
     * Returns -1 if there is no path.
     *
     * @param g     The graph to search.
     * @param from  The starting vertex ID.
     * @param to    The target vertex ID.
     * @return      A triple containing the duration (in ns), the number of relaxed edges, and the shortest path distance. The distance is -1 if no path exists.
     */
    public static Result<Integer> shortestPath(Graph g, long from, long to) {
        long start = System.nanoTime(); // to keep track of the duration (in ns) of running Dijkstra
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Set<Long> visited = new HashSet<>(); // set to keep track of visited vertices
        Map<Long, Integer> dists = new HashMap<>(); // map of shortest known distance to each vertex
        int relaxed = 0; // counter for number of relaxed edges

        pq.add(new PQElem(0, from)); // add the id of the source vertex (v = 'from') to the PQ with distance 0 (key = 0)
        dists.put(from, 0); // distance to source is 0
        
        while (!pq.isEmpty() && pq.peek().v != to) { // while the PQ still has element(s) and the next PQElem isn't the target vertex 'to'
            PQElem elem = pq.poll(); // get the next element of the MinPQ (the PQElem with the smallest key/distance to the source)
            long u = elem.v; // u is the id of the current vertex being processed
            if (visited.contains(u)) { // if we have already visited this vertex, skip it
                continue;
            }

            visited.add(u); // mark the current vertex as visited

            int dist = elem.key; // dist is the shortest known distance to the current vertex u
            for (Graph.Edge e : g.getNeighbours(u)) { // for each outgoing edge from the current vertex u:
                relaxed++; // increment the number of relaxed edges
                long v = e.to;  // v is the id of the destination vertex of the current edge
                int w = e.weight; // weight of the current edge
                if (!dists.containsKey(v) || dists.get(v) > dist + w) { // if we haven't recorded the distance to v before OR if the new distance to v is shorter than previously known distance:
                    pq.add(new PQElem(dist + w, v)); // add v to the PQ with the updated shorter distance
                    dists.put(v, dist + w); // add/update the shortest known distance map with the new shorter distance
                }
            }
        }
        long end = System.nanoTime();
        if (!dists.containsKey(to)) { // if there is no path from 'from' to 'to'
            return new Result<>(end - start, relaxed, -1);
        }
        return new Result<>(end - start, relaxed, dists.get(to)); // return the time, no. of relaxed edges, shortest path distance to target vertex
    }

    /**
     * Runs Dijkstra's algorithm from source to target, excluding a specific vertex.
     * If target is not reached within maxDistance, the search terminates early.
     *
     * @param g             The graph to search.
     * @param from          The starting vertex ID.
     * @param to            The target vertex ID.
     * @param exclude       The vertex ID to exclude from the search.
     * @param maxDistance   The maximum distance to search (to optimise witness path searches).
     * @return The shortest path distance from 'from' to 'to' in a graph excluding 'exclude', 
     *         or -1 if no path exists within maxDistance.
     */
    public static int shortestPathOnSubgraph(Graph g, long from, long to, long exclude, int maxDistance) {
        // special case if source or target is excluded
        if (from == exclude || to == exclude) return -1;

        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Map<Long, Integer> dists = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        
        pq.add(new PQElem(0, from));
        dists.put(from, 0);

        int best = Integer.MAX_VALUE; 
        
        while (!pq.isEmpty()) {
            PQElem elem = pq.poll();
            long u = elem.v;
            int dist = elem.key;
            if (u == to) return dist; // if we've reached the target, return the distance
            if (visited.contains(u)) continue; // skip if already visited
            visited.add(u);
            if (dist > maxDistance) continue; // skip if distance exceeds maximum
            if (!g.hasNeighbours(u)) continue; // skip if u has no neighbours
            
            // relax edges
            for (Graph.Edge e : g.getNeighbours(u)) {
                long v = e.to;
                if (v == exclude) continue; // skip excluded vertex
                int newDist = dist + e.weight;
                
                // only consider paths with dist smaller than maxDistance
                if (newDist <= maxDistance) {
                    if (!dists.containsKey(v) || dists.get(v) > newDist) {
                        dists.put(v, newDist);
                        pq.add(new PQElem(newDist, v));
                    }
                }
            }

            //Checks whether further searching can possibly improve the current best distance. If not, break.
            if (!pq.isEmpty()) {
                int min = pq.peek().key;
                if (min >= best)
                    break;
            }
        }
        return -1; // if no witness path is smaller than maxDistance
    }
}