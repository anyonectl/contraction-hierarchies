package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class BidirectionalDijkstra extends Dijkstra{

    public static Result<Integer> shortestPath(Graph g, long start, long target) {
        long begin = System.nanoTime();
        
        if (start == target) return new Result<>(0, 0, 0); // early catch

        PriorityQueue<PQElem> leftPQ = new PriorityQueue<>(), rightPQ = new PriorityQueue<>();
        Map<Long, Integer> leftDists = new HashMap<>(), rightDists = new HashMap<>();

        leftDists.put(start, 0);
        rightDists.put(target, 0);

        leftPQ.add(new PQElem(0, start));
        rightPQ.add(new PQElem(0, target));

        Set<Long> visited = new HashSet<>();
        int relaxed = 0, d = Integer.MAX_VALUE; // our result values

        while (!leftPQ.isEmpty() || !rightPQ.isEmpty()) {
            PQElem leftElem = leftPQ.peek(), rightElem = rightPQ.peek();
            boolean leftHasPriority = (rightElem == null) || (leftElem != null && leftElem.compareTo(rightElem) <= 0);

            Map<Long, Integer> dists = leftHasPriority ? leftDists : rightDists;
            PriorityQueue<PQElem> PQ = leftHasPriority ? leftPQ : rightPQ;

            PQElem elem = PQ.poll();
            long u = elem.v; // local u as in (u,v)
            int dist = elem.key; // distance from root

            if (visited.contains(u)) continue; // TODO Ask TA why this doesn't work if we use break instead of continue?
            visited.add(u);
            if (g.getNeighbours(u) == null) continue; 

            for (Graph.Edge e : g.getNeighbours(u)) {
                relaxed++;
                long v = e.to;
                int newDist = dist + e.weight;

                if (!dists.containsKey(v) || newDist < dists.get(v)) {
                    dists.put(v, newDist);
                    PQ.add(new PQElem(newDist, v));
                }
            }

            // Update minimum distance between s and t (line 21)
            Integer leftDistHere = leftDists.get(u);
            Integer rightDistHere = rightDists.get(u);
            if (leftDistHere != null && rightDistHere != null)
                d = Math.min(d, leftDistHere + rightDistHere);
                
            //Checks whether further searching can possibly improve the current best distance d.
            if (!leftPQ.isEmpty() && !rightPQ.isEmpty()) {
                int leftMin = leftPQ.peek().key;
                int rightMin = rightPQ.peek().key;
                if (leftMin + rightMin >= d)
                    break;
            }
        }

        long end = System.nanoTime();

        if (d == Integer.MAX_VALUE) {
            return new Result<>(end - begin, relaxed, -1); // -1 for no path
        }

        return new Result<>(end - begin, relaxed, d);
    }

    public static int shortestPathOnSubgraph(Graph g, long from, long to, long exclude, int maxDistance) {
        // special case if source or target is excluded
        if (from == exclude || to == exclude) return -1;
        
        PriorityQueue<PQElem> leftPQ = new PriorityQueue<>(), rightPQ = new PriorityQueue<>();
        Map<Long, Integer> leftDists = new HashMap<>(), rightDists = new HashMap<>();
        Set<Long> leftVisited = new HashSet<>(), rightVisited = new HashSet<>();

        leftDists.put(from, 0);
        rightDists.put(to, 0);

        leftPQ.add(new PQElem(0, from));
        rightPQ.add(new PQElem(0, to));

        int best = Integer.MAX_VALUE;

        while (!leftPQ.isEmpty() || !rightPQ.isEmpty()) {
            PQElem leftElem = leftPQ.peek(), rightElem = rightPQ.peek();
            boolean leftHasPriority = (rightElem == null) || (leftElem != null && leftElem.compareTo(rightElem) <= 0);

            Map<Long, Integer> thisSide = leftHasPriority ? leftDists : rightDists;
            Map<Long, Integer> otherSide = leftHasPriority ? rightDists : leftDists;
            PriorityQueue<PQElem> PQ = leftHasPriority ? leftPQ : rightPQ;
            Set<Long> visited = leftHasPriority ? leftVisited : rightVisited;

            PQElem elem = PQ.poll();
            long u = elem.v;
            int dist = elem.key;

            if (visited.contains(u)) continue; // skip if already visited
            visited.add(u);
            if (otherSide.containsKey(u)) { // check if node is seen from both directions
                int totalDist = dist + otherSide.get(u);
                if (totalDist < best) best = totalDist;
            }
            if (dist > best || dist > maxDistance) continue; // skip if we already have better distance or distance exceeds maximum
            if (!g.hasNeighbours(u)) continue; // skip if u has no neighbours
            
            // relax edges
            for (Graph.Edge e : g.getNeighbours(u)) {
                long v = e.to;
                if (v == exclude) continue; // skip excluded vertex
                int newDist = dist + e.weight;
                
                // only consider paths with dist smaller than maxDistance
                if (newDist <= maxDistance) {
                    if (!thisSide.containsKey(v) || thisSide.get(v) > newDist) {
                        thisSide.put(v, newDist);
                        PQ.add(new PQElem(newDist, v));
                    }
                }
            }
        }
        return (best == Integer.MAX_VALUE || best > maxDistance) ? -1 : best; // if no witness path is smaller than maxDistance
    }

    public static void main(String[] args) {
        Graph simple = new Graph();
        simple.addVertex(1, new Graph.Vertex(0, 0));
        simple.addVertex(2, new Graph.Vertex(1, 0));
        simple.addVertex(3, new Graph.Vertex(0, 1));
        simple.addVertex(4, new Graph.Vertex(1, 1));
        simple.addUndirectedEdge(1, 2, 5);
        simple.addUndirectedEdge(1, 3, 3);
        simple.addUndirectedEdge(2, 4, 4);
        simple.addUndirectedEdge(3, 4, 6);
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 2, -1, 100);
        System.out.println(distance == 5);
    }
}