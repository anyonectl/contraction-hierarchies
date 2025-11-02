package ch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class DijkstraTest {
    private Graph g;
    @Before
    public void setUp() {
        g = new Graph();
        g.addVertex(1, new   Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 1));
        g.addVertex(3, new Graph.Vertex(2, 2));
        g.addVertex(4, new Graph.Vertex(3, 3));

        // Graph structure:
        // 1 --(4)-- 2 --(1)-- 3
        //  \                /
        //   (8)          (2)
        //     \          /
        //          4
        g.addUndirectedEdge(1, 2, 4);
        g.addUndirectedEdge(2, 3, 1);
        g.addUndirectedEdge(1, 4, 8);
        g.addUndirectedEdge(3, 4, 2);
    }

    @Test
    public void testShortestPath_basic() {
        Result<Integer> dist = Dijkstra.shortestPath(g, 1, 4);
        assertEquals("Shortest path 1→2→3→4 should have total cost 7", (long) dist.result, 7);
    }

    @Test
    public void testShortestPath_directConnection() {
        Result<Integer> dist = Dijkstra.shortestPath(g, 1, 2);
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
        Result<Integer> dist = Dijkstra.shortestPath(disconnected, 1, 3);
        assertEquals("Unreachable vertex should return -1",-1, (long) dist.result );
    }
    @Test
    public void testShortestPath_sameNode() {
        Result<Integer> dist = Dijkstra.shortestPath(g, 1, 1);
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
          1               4
           \             /
            (5)       (5)
              \__ 5 __/
*/
        h.addUndirectedEdge(1, 2, 3);
        h.addUndirectedEdge(2, 3, 3);
        h.addUndirectedEdge(3, 4, 3);
        h.addUndirectedEdge(4, 5, 5);
        h.addUndirectedEdge(5, 1, 5);
        Result<Integer> dist = Dijkstra.shortestPath(h, 1, 4);
        assertEquals("Distance from a node to itself should be 9", 9, (long) dist.result);
    }
}
