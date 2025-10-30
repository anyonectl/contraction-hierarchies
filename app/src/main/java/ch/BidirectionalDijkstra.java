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
        //Set<Long> leftVisited = new HashSet<>(), rightVisited = new HashSet<>();

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
            //Set<Long> visited = leftHasPriority ? leftVisited : rightVisited;

            PQElem elem = PQ.poll();
            long u = elem.v; // local u as in (u,v)
            int distU = elem.key; // distance from root

            //if (leftVisited.contains(u) && rightVisited.contains(u)) break;
            if (visited.contains(u)) continue; // if set to break the python run fail emensly.. idk why? 
            visited.add(u);

            if (g.getNeighbours(u) == null) continue;
            for (Graph.Edge e : g.getNeighbours(u)) {
                relaxed++;
                long v = e.to;
                int w = e.weight;
                if (!dists.containsKey(v) || distU + w < dists.get(v)) {
                    dists.put(v, distU + w);
                    PQ.add(new PQElem(distU + w, v));
                }
                
                // Update minimum distance between s and t (line 21)
                Integer leftDistHere = leftDists.get(u);
                Integer rightDistHere = rightDists.get(u);
                if (leftDistHere != null && rightDistHere != null)
                    d = Math.min(d, leftDistHere + rightDistHere);
            }
                
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
}