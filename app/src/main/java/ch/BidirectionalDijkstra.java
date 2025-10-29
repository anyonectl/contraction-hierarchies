package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class BidirectionalDijkstra {

    public static Result<Integer> shortestPath(Graph g, long start, long target) {
        long begin = System.nanoTime();
        
        // Early catch of edge case
        if (start == target)
            return new Result<>(0, 0, 0);

        // Left side: start
        PriorityQueue<PQElem> leftPQ = new PriorityQueue<>(); 
        Map<Long, Integer> leftDists = new HashMap<>();
        Set<Long> leftVisited = new HashSet<>();
        leftPQ.add(new PQElem(0, start));
        leftDists.put(start, 0);

        // Right side: target 
        PriorityQueue<PQElem> rightPQ = new PriorityQueue<>();
        Map<Long, Integer> rightDists = new HashMap<>();
        Set<Long> rightVisited = new HashSet<>(); // settled
        rightPQ.add(new PQElem(0, target));
        rightDists.put(target, 0);

        int relaxed = 0; 
        int d = Integer.MAX_VALUE; // our result value

        while (!leftPQ.isEmpty() || !rightPQ.isEmpty()) {
            // Peek and compare: which PQ should we poll from? 
            PQElem leftElem = leftPQ.peek(), rightElem = rightPQ.peek();
            boolean leftHasPriority = (rightElem == null) || (leftElem != null && leftElem.compareTo(rightElem) <= 0);

            // Specify variables based on priority
            Map<Long, Integer> dists = leftHasPriority ? leftDists : rightDists;
            PriorityQueue<PQElem> PQ = leftHasPriority ? leftPQ : rightPQ;
            Set<Long> visited = leftHasPriority ? leftVisited : rightVisited;
            PQElem elem = leftHasPriority ? leftPQ.poll() : rightPQ.poll();
            long elemId = elem.v;
            int elementDist = elem.key;

            visited.add(elemId);

            if (g.getNeighbours(elemId) == null) continue;
            for (Graph.Edge e : g.getNeighbours(elemId)) {
                relaxed++;
                long v = e.to;
                int w = e.weight;
                if (!dists.containsKey(v) || elementDist + w < dists.get(v)) {
                    dists.put(v, elementDist + w);
                    PQ.add(new PQElem(elementDist + w, v));
                }

                // Update min dist between s and t inside loop??
                // Integer leftDist = leftDists.get(v);
                // Integer rightDist = rightDists.get(v);
                // if (leftDist != null && rightDist != null)
                //     d = Math.min(d, leftDist + rightDist);
            }

            // Update min dist between s and t outside loop
            Integer leftDistHere = leftDists.get(elemId);
            Integer rightDistHere = rightDists.get(elemId);
            if (leftDistHere != null && rightDistHere != null)
                d = Math.min(d, leftDistHere + rightDistHere);

            if (!leftPQ.isEmpty() && !rightPQ.isEmpty()) {
                int leftMin = leftPQ.peek().key;
                int rightMin = rightPQ.peek().key;
                if (leftMin + rightMin >= d)
                    break;
            }
        }

        // stop measuring time
        long end = System.nanoTime();

        // if we haven't found a path, the d should not have been updated
        if (d == Integer.MAX_VALUE) {
            return new Result<>(end - begin, relaxed, -1); // -1 for no path
        }

        return new Result<>(end - begin, relaxed, d);
    }    

    // debugging
    // public static void main(String[] args) {
    //     Graph g = new Graph();
    //     g.addVertex(1, new Graph.Vertex(0, 0));
    //     g.addVertex(2, new Graph.Vertex(1, 1));
    //     g.addVertex(3, new Graph.Vertex(2, 2));
    //     g.addVertex(4, new Graph.Vertex(3, 3));

    //     // Graph structure:
    //     // 1 -(4)- 2 -(1)- 3
    //     //  \             /
    //     //   \           /
    //     //   (8)      (2)
    //     //      \     /
    //     //       \   /
    //     //         4
    //     g.addUndirectedEdge(1, 2, 4);
    //     g.addUndirectedEdge(2, 3, 1);
    //     g.addUndirectedEdge(1, 4, 8);
    //     g.addUndirectedEdge(3, 4, 2);

    //     Result<Integer> gResult1 = shortestPath(g, 1, 4);
    //     System.out.println(String.format("Shortest path 1→2→3→4 should have total cost 7. Returned: %d", gResult1.result));
    //     System.out.println(String.format("Shortest path 1→2→3→4 should relax 6. Returned: %d", gResult1.relaxed));

    //     Result<Integer> gResult2 = shortestPath(g, 1, 2);
    //     System.out.println(String.format("Direct edge 1→2 should cost 4. Returned: %d", gResult2.result));
    //     System.out.println(String.format("Direct edge 1→2 should relax 2. Returned: %d", gResult2.relaxed));


    //     Result<Integer> startTargetSame = shortestPath(g, 1, 1);
    //     System.out.println(String.format("Distance from a node to itself should be 0. Returned: %d", startTargetSame.result));
    //     System.out.println(String.format("Distance from a node to itself should relax 0. Returned: %d", startTargetSame.result));


    //     Graph disconnected = new Graph();
    //     disconnected.addVertex(1, new Graph.Vertex(0, 0));
    //     disconnected.addVertex(2, new Graph.Vertex(1, 1));
    //     disconnected.addVertex(3, new Graph.Vertex(2, 2));
    //     disconnected.addUndirectedEdge(1, 2, 4);
    //     // no edges between 1 and 3
    //     Result<Integer> disconnectedRes = shortestPath(disconnected, 1, 3);
    //     System.out.println(String.format("Unreachable vertex should return -1. Returned: %d", disconnectedRes.result));
    //     System.out.println(String.format("Unreachable vertex should relax 2. Returned: %d", disconnectedRes.relaxed));
    // }
}