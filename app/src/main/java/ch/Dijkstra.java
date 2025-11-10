package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import ch.Graph.Edge;

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
     * Performs a search for witness paths using limited Dijkstra from u to w avoiding v
     * and determines if a shortcut is needed from u to w when contracting v.
     * A shortcut is needed if there's no path from u to w (avoiding v) that is
     * shorter than or equal to maxDist (witness path).
     * 
     * @param g         The graph to search.
     * @param u         The starting vertex ID.
     * @param w        The target vertex ID.
     * @param v         The vertex ID to exclude from the search.
     * @param maxDist   The maximum distance to search (to optimise witness path searches).
     * @return True if the path from u to w through v (u->v->w) is a unique shortest, or False if a witness path was found.
     */
    public static boolean localSearch(Graph g, long u, long w, long v, int maxDist) {
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Map<Long, Integer> dists = new HashMap<>();

        dists.put(u, 0);
        pq.offer(new PQElem(0, u));
        
        while (!pq.isEmpty()) {
            PQElem current = pq.poll();
            long node = current.v;
            int dist = current.key;
            if (dist > maxDist) break; // maxDist reached, next dists will be larger, so no need to search further
            if (node == w) return false; // we found a witness path, stop search
            if (dists.get(node) < dist) continue; // skip if we already found a shorter path to the current node
            List<Edge> neighbours = g.getNeighbours(node);
            if (neighbours == null) continue; // skip if current node has no neighbours
            
            // relax edges
            for (Edge e : neighbours) {
                if (e.to == v) continue; // exclude the vertex being contracted
                if (g.getVertex(e.to).getRank() != -1) continue; // exclude vertices that have already been contracted
                
                int newDist = dist + e.weight;
                // only consider paths with dist smaller than maxDistance
                if (newDist <= maxDist && (!dists.containsKey(e.to) || newDist < dists.get(e.to))) {
                    dists.put(e.to, newDist);
                    pq.offer(new PQElem(newDist, e.to));
                }
            }
        }
        return true; // no witness path found, shortcut needed
    }
}