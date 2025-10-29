package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class BidirectionalDijkstra {

    public static Result<Integer> shortestPath(Graph g, long start, long target) {
        long begin = System.nanoTime(); // to keep track of the duration (in ns) of running Dijkstra
        
        // early catch of edge case
        if (start == target)
            return new Result<>(0, 0, 0);

        // start
        PriorityQueue<PQElem> leftPQ = new PriorityQueue<>(); 
        Map<Long, Integer> leftDists = new HashMap<>();
        leftPQ.add(new PQElem(0, start));
        leftDists.put(start, 0);
        Set<Long> leftVisited = new HashSet<>();

        // target 
        PriorityQueue<PQElem> rightPQ = new PriorityQueue<>();
        Map<Long, Integer> rightDists = new HashMap<>();
        rightPQ.add(new PQElem(0, target));
        rightDists.put(target, 0);
        Set<Long> rightVisited = new HashSet<>(); // settled

        // other initializations
        int relaxed = 0; // counter for number of relaxed edges
        int d = Integer.MAX_VALUE;// dist is the shortest known distance to the current vertex u

        
        while (!leftPQ.isEmpty() && !rightPQ.isEmpty()) { // should we continue while one is not empty?
            PQElem leftElem = leftPQ.peek(); // retrieve, but not remove
            PQElem rightElem = rightPQ.peek();
            PQElem elem = null; // yet to be determined: get the next element of the MinPQ (the PQElem with the smallest key/distance to the source)
            
            boolean leftHasPriority = leftElem != null ? true : false; // If left is null then right has priority (because of check in while loop)
            if (leftElem != null && rightElem != null) 
                leftHasPriority = leftElem.compareTo(rightElem) <= 0; // l is higher rank than r

            if (leftHasPriority) elem = leftPQ.poll();
            else elem = rightPQ.poll();

            long elemId = elem.v; // prev. u

            // Check if both searches has visited the same element
            if (leftVisited.contains(elemId) && rightVisited.contains(elemId)) 
                // continue; // This vertex was settled by the other instance
                 break; // This changes to break because in theory the moment the two searches meet we have found the shortest path?? 
            
            // Add element ID to visited
            if (leftHasPriority) leftVisited.add(elemId);
            else rightVisited.add(elemId);

            int dist = elem.key;

            for (Graph.Edge e : g.getNeighbours(elemId)) { // for each outgoing edge from the current vertex u:
                relaxed++; // increment the number of relaxed edges

                long v = e.to; // v is the id of the destination vertex of the current edge
                int w = e.weight; // c(u,v) or weight of the current edge

                if (leftHasPriority) {
                    if (!leftDists.containsKey(v) || dist + w < leftDists.get(v)) {
                        leftDists.put(v, dist + w); // add/update the shortest known distance map with the new shorter distance
                        leftPQ.add(new PQElem(dist + w, v)); // add v to the PQ with the updated shorter distance
                    }
                } 
                else {
                    if (!rightDists.containsKey(v) || dist + w < rightDists.get(v)) {
                        rightDists.put(v, dist + w);
                        rightPQ.add(new PQElem(dist + w, v));
                    }
                }

                Integer leftDist = leftDists.get(v);
                Integer rightDist = rightDists.get(v);

                if (leftDist != null && rightDist != null) {
                    d = Math.min(d, leftDist + rightDist);
}            }
        }

        long end = System.nanoTime();
        // Doesn't matter which one we return as they should both include eachother and the same dist
        if (d == Integer.MAX_VALUE) { // if there is no path from 'from' to 'to' we haven't touched d
            return new Result<>(end - begin, relaxed, -1); // -1 for no path
        }
        

        return new Result<>(end - begin, relaxed, d);
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

        Result<Integer> gResult1 = shortestPath(g, 1, 4);
        System.out.println(String.format("Shortest path 1→2→3→4 should have total cost 7. Returned: %d", gResult1.result));
        System.out.println(String.format("Shortest path 1→2→3→4 should relax 8. Returned: %d", gResult1.relaxed));

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