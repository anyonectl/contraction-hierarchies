package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.List;

public class BidirectionalDijkstra extends Dijkstra{

    /**
     * Searches for the shortest path in an augmented graph (where all vertexes are of type augmented (have ranks)).
     * Utilizes bidirectional dijkstra. Only goes up in rank from both ends. 
     * @param ag    The graph we search
     * @param from  From vertex id
     * @param to    To vertex id
     * @return      A Result: The shortest path distance, the number of relaxed edges, and runtime in ns
     */
    public static Result<Integer> bidirectionalSearch(Graph g, long from, long to) {
        long begin = System.nanoTime();

        if (from == to) return new Result<>(0, 0, 0); // early catch

        PriorityQueue<PQElem> leftPQ = new PriorityQueue<>(), rightPQ = new PriorityQueue<>();
        Map<Long, Integer> leftDist = new HashMap<>(), rightDist = new HashMap<>();
        Set<Long> leftVisited = new HashSet<>(), rightVisited = new HashSet<>();

        leftDist.put(from, 0); rightDist.put(to, 0);
        leftPQ.add(new PQElem(0, from)); rightPQ.add(new PQElem(0, to));

        int relaxed = 0, best = Integer.MAX_VALUE;

        while(!leftPQ.isEmpty() && !rightPQ.isEmpty()) {
            PQElem leftElem = leftPQ.peek(), rightElem = rightPQ.peek();
            boolean leftHasPriority = leftElem.compareTo(rightElem) <= 0;

            Map<Long, Integer> dists = leftHasPriority ? leftDist : rightDist, otherDists = leftHasPriority ? rightDist : leftDist;
            PriorityQueue<PQElem> pq = leftHasPriority ? leftPQ : rightPQ;
            Set<Long> visited = leftHasPriority ? leftVisited : rightVisited;

            PQElem elem = pq.poll();
            long u = elem.v; // id
            int dist = elem.key;
            long uRank = g.getVertex(u).rank;

            if (visited.contains(u)) continue;
            visited.add(u);

            if (otherDists.containsKey(u)) {
                int candidate = dist + otherDists.get(u);
                if (candidate < best) best = candidate;
            }

            if (g.getNeighbours(u) == null) continue;

            List<Graph.Edge> edges = g.getNeighbours(u); // for debugging)
            for (Graph.Edge e : edges) {
                relaxed++;
                long v = e.to;
                long vRank = g.getVertex(v).rank;
                if (uRank > vRank)  continue; // we only go up in rank
                int newDist = dist + e.weight;
                if (!dists.containsKey(v) || dists.get(v) > newDist){
                    dists.put(v, newDist);
                    pq.add(new PQElem(newDist, v));
                }

                if (otherDists.containsKey(v)) {
                    int candidate = newDist + otherDists.get(v);
                    if (candidate < best) best = candidate;
                }
            }
            
            // peek to check if next seach might be better path
            if(!leftPQ.isEmpty() && !rightPQ.isEmpty()) {
                int leftMin = leftPQ.peek().key;
                int rightMin = rightPQ.peek().key;
                if (leftMin + rightMin >= best) break;
            }
        }
        long end = System.nanoTime();

        if (best == Integer.MAX_VALUE) return new Result<Integer>(end- begin, relaxed, -1);

        return new Result<>(end - begin, relaxed, best);
    }

    public static Result<Integer> shortestPath(Graph g, long start, long target) {
        long begin = System.nanoTime();
        
        if (start == target) return new Result<>(0, 0, 0); // early catch

        PriorityQueue<PQElem> leftPQ = new PriorityQueue<>(), rightPQ = new PriorityQueue<>();
        Map<Long, Integer> leftDists = new HashMap<>(), rightDists = new HashMap<>();
        Set<Long> leftVisited = new HashSet<>(), rightVisited = new HashSet<>();

        leftDists.put(start, 0);
        rightDists.put(target, 0);

        leftPQ.add(new PQElem(0, start));
        rightPQ.add(new PQElem(0, target));

        int relaxed = 0, best = Integer.MAX_VALUE; // our result values

        while (!leftPQ.isEmpty() && !rightPQ.isEmpty()) {
            PQElem leftElem = leftPQ.peek(), rightElem = rightPQ.peek();
            boolean leftHasPriority = leftElem.compareTo(rightElem) <= 0;

            Map<Long, Integer> dists = leftHasPriority ? leftDists : rightDists, otherDists = leftHasPriority ? rightDists : leftDists;
            PriorityQueue<PQElem> PQ = leftHasPriority ? leftPQ : rightPQ;
            Set<Long> visited = leftHasPriority ? leftVisited : rightVisited;

            PQElem elem = PQ.poll();
            long u = elem.v; // local u as in (u,v)
            int dist = elem.key; // distance from root

            if (visited.contains(u)) continue; // current side has explored this vertex
            visited.add(u);

            if (otherDists.containsKey(u)) { // if we have recorded a distance to u from the other search, then we update best if total distance is better.
                int totalDist = dist + otherDists.get(u);
                if (totalDist < best) best = totalDist;
            }

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
                
            //Checks whether further searching can possibly improve the current best distance. If not, break.
            if (!leftPQ.isEmpty() && !rightPQ.isEmpty()) {
                int leftMin = leftPQ.peek().key;
                int rightMin = rightPQ.peek().key;
                if (leftMin + rightMin >= best)
                    break;
            }
        }

        long end = System.nanoTime();

        if (best == Integer.MAX_VALUE) {
            return new Result<>(end - begin, relaxed, -1); // -1 for no path
        }

        return new Result<>(end - begin, relaxed, best);
    }
}