package ch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BidirectionalDijkstraTest {
    private Graph g;
    private Graph simple;
    private Graph triangle;
    
    @Before
    public void setUp() {
        g = new Graph();
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

        simple = new Graph();
        simple.addVertex(1, new Graph.Vertex(0, 0));
        simple.addVertex(2, new Graph.Vertex(1, 0));
        simple.addVertex(3, new Graph.Vertex(0, 1));
        simple.addVertex(4, new Graph.Vertex(1, 1));
        simple.addUndirectedEdge(1, 2, 5);
        simple.addUndirectedEdge(1, 3, 3);
        simple.addUndirectedEdge(2, 4, 4);
        simple.addUndirectedEdge(3, 4, 6);

        triangle = new Graph();
        triangle.addVertex(1, new Graph.Vertex(0, 0));
        triangle.addVertex(2, new Graph.Vertex(0, 1));
        triangle.addVertex(3, new Graph.Vertex(1, 1));
        triangle.addUndirectedEdge(1, 2, 5);
        triangle.addUndirectedEdge(1, 3, 8);
        triangle.addUndirectedEdge(2, 3, 3);
    }

    @Test
    public void testShortestPath_basic() {
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(g, 1, 4);
        assertEquals("Shortest path 1→2→3→4 should have total cost 7", (long) dist.result, 7);
    }

    @Test
    public void testShortestPath_directConnection() {
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(g, 1, 2);
        assertEquals("Direct edge 1→2 should cost 4", (long) dist.result, 4 );
    }

    @Test
    public void testShortestPath_noPathExists() {
        Graph disconnected = new Graph();
        disconnected.addVertex(1, new Graph.Vertex(0, 0));
        disconnected.addVertex(2, new Graph.Vertex(1, 1));
        disconnected.addVertex(3, new Graph.Vertex(2, 2));
        disconnected.addUndirectedEdge(1, 2, 4);
        // no edges between 1 and 3
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(disconnected, 1, 3);
        assertEquals("Unreachable vertex should return -1",-1, (long) dist.result);
    }
    
    @Test
    public void testShortestPath_sameNode() {
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(g, 1, 1);
        assertEquals("Distance from a node to itself should be 0", 0, (long) dist.result);
    }

    @Test
    public void testShortestPath_correctShortestPath() {
        Graph h = new Graph();
        h.addVertex(1, new Graph.Vertex(0, 0));
        h.addVertex(2, new Graph.Vertex(1, 1));
        h.addVertex(3, new Graph.Vertex(2, 2));
        h.addVertex(4, new Graph.Vertex(3, 3));
        h.addVertex(5, new Graph.Vertex(4, 4));
/* graph as in lecture: https://www.youtube.com/watch?v=CHvQ3q_gJ7E&t=1465s
         (3)- 2 -(3)- 3 -(3) 
          |               |
   source 1               4 target
           \             /
            (5)       (5)
              \__ 5 __/
*/
        h.addUndirectedEdge(1, 2, 3);
        h.addUndirectedEdge(2, 3, 3);
        h.addUndirectedEdge(3, 4, 3);
        h.addUndirectedEdge(4, 5, 5);
        h.addUndirectedEdge(5, 1, 5);
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(h, 1, 4);
        assertEquals("Distance from a node to itself should be 9", 9, (long) dist.result);
    }

    @Test
    public void testShortestPath_fromBlog() {
        Graph h = new Graph();
        h.addVertex(1, new Graph.Vertex(0, 0));
        h.addVertex(2, new Graph.Vertex(1, 1));
        h.addVertex(3, new Graph.Vertex(2, 2));
        h.addVertex(4, new Graph.Vertex(3, 3));
/* graph as in lecture: https://www.youtube.com/watch?v=CHvQ3q_gJ7E&t=1465s


                (12)-- 2 --(8)
                /             \
        source 1               3 target
                \             /
                (14)-- 4 --(5)
*/
        h.addUndirectedEdge(1, 2, 12);
        h.addUndirectedEdge(2, 3, 8);
        h.addUndirectedEdge(3, 4, 5);
        h.addUndirectedEdge(4, 1, 14);
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(h, 1, 3);
        assertEquals("Distance from a node to itself should be 19", 19, (long) dist.result);
    }

    
    @Test
    public void shortestPathOnSubgraph_directPath() {
        // Direct path from 1 to 2 has distance 5
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 2, -1, 100);
        assertEquals(5, distance);
    }
    
    @Test
    public void shortestPathOnSubgraph_excludeForceAlternate() {
        // Without exclusion: 1→2 = 5
        // Excluding vertex 2 forces: 1→3→4 (not possible directly to 2)
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 4, 2, 100);
        assertEquals(9, distance); // 3 + 6 = 9
    }
    
    @Test
    public void shortestPathOnSubgraph_excludeMiddleVertex() {
        // Direct path 1→2→3 = 5 + 3 = 8
        // Excluding 2 forces: 1→3 = 8
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(triangle, 1, 3, 2, 100);
        assertEquals(8, distance);
    }
    
    @Test
    public void shortestPathOnSubgraph_witnessPathShorter() {
        // Path 1→2→3 = 8, which is shorter than direct 1→3 = 8 (equal actually)
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(triangle, 1, 3, -1, 100);
        assertEquals(8, distance); // Both paths are equal
    }
    
    @Test
    public void shortestPathOnSubgraph_maxDistanceBoundRespected() {
        // Path 1→2 = 5, but maxDistance = 4 should stop search
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 2, -1, 4);
        assertEquals(-1, distance);
    }
    
    @Test
    public void shortestPathOnSubgraph_exactlyAtMaxDistance() {
        // Path 1→2 = 5, maxDistance = 5 should succeed
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 2, -1, 5);
        assertEquals(5, distance);
    }
    
    @Test
    public void shortestPathOnSubgraph_sourceEqualsTarget() {
        // Distance from vertex to itself is 0
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 1, -1, 100);
        assertEquals(0, distance);
    }

    @Test
    public void shortestPathOnSubgraph_noPath() {
        Graph disconnected = new Graph();
        disconnected.addVertex(1, new Graph.Vertex(0, 0));
        disconnected.addVertex(2, new Graph.Vertex(1, 0));
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(disconnected, 1, 2, -1, 100);
        assertEquals(-1, distance);
    }
    
    @Test
    public void shortestPathOnSubgraph_excludeOnlyPath() {
        Graph gr = new Graph();
        gr.addVertex(1, new Graph.Vertex(0, 0));
        gr.addVertex(2, new Graph.Vertex(1, 0));
        gr.addVertex(3, new Graph.Vertex(2, 0));
        gr.addUndirectedEdge(1, 2, 5);
        gr.addUndirectedEdge(2, 3, 5);
        // Only path 1→3 goes through 2
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(gr, 1, 3, 2, 100);
        assertEquals(-1, distance);
    }
    
    @Test
    public void shortestPathOnSubgraph_excludeSource() {
        // Excluding the source vertex should return -1
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 2, 1, 100);
        assertEquals(-1, distance);
    }
    
    @Test
    public void shortestPathOnSubgraph_maxDistanceTooSmall() {
        // Path exists but maxDistance too small
        int distance = BidirectionalDijkstra.shortestPathOnSubgraph(simple, 1, 4, -1, 5);
        assertEquals(-1, distance); // Actual distance is 9 (1→2→4)
    }
}
