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
    
    public static void main(String[] args) {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 1));
        g.addVertex(3, new Graph.Vertex(2, 2));
        g.addVertex(4, new Graph.Vertex(3, 3));

        // Graph structure:
        // 1 -(4)- 2 -(1)- 3
        //  \             /
        //   \           /
        //   (8)      (2)
        //      \     /
        //       \   /
        //         4
        g.addUndirectedEdge(1, 2, 4);
        g.addUndirectedEdge(2, 3, 1);
        g.addUndirectedEdge(1, 4, 8);
        g.addUndirectedEdge(3, 4, 2);

        Result<Integer> test = shortestPath(g, 1, 4);
        System.out.println(String.format("Shortest path 1→2→3→4 should have total cost 7: %d", test.result));
        System.out.println(String.format("Shortest path 1→2→3→4 should relax 6. Returned: %d", test.relaxed));

        Result<Integer> gResult2 = shortestPath(g, 1, 2);
        System.out.println(String.format("Direct edge 1→2 should cost 4. Returned: %d", gResult2.result));
        System.out.println(String.format("Direct edge 1→2 should relax 2. Returned: %d", gResult2.relaxed));

        Graph disconnected = new Graph();
        disconnected.addVertex(1, new Graph.Vertex(0, 0));
        disconnected.addVertex(2, new Graph.Vertex(1, 1));
        disconnected.addVertex(3, new Graph.Vertex(2, 2));
        disconnected.addUndirectedEdge(1, 2, 4);
        // no edges between 1 and 3
        Result<Integer> disconnectedRes = shortestPath(disconnected, 1, 3);
        System.out.println(String.format("Unreachable vertex should return -1. Returned: %d", disconnectedRes.result));
        System.out.println(String.format("Unreachable vertex should relax 2. Returned: %d", disconnectedRes.relaxed));

        Result<Integer> startTargetSame = shortestPath(g, 1, 1);
        System.out.println(String.format("Distance from a node to itself should be 0. Returned: %d", startTargetSame.result));
        System.out.println(String.format("Distance from a node to itself should relax 0. Returned: %d", startTargetSame.result));
    }
}