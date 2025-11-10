package ch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class DijkstraTest {

        Graph g;
        Graph square;
        Graph triangle;
        Graph linear;
        Graph disconnected;
    
    @Before
    public void setup() {
        g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 1));
        g.addVertex(3, new Graph.Vertex(2, 2));
        g.addVertex(4, new Graph.Vertex(3, 3));
/* g:
        1 -(4)- 2 -(1)- 3
         \             /
          (8)-- 4 --(2)
*/
        g.addUndirectedEdge(1, 2, 4);
        g.addUndirectedEdge(2, 3, 1);
        g.addUndirectedEdge(1, 4, 8);
        g.addUndirectedEdge(3, 4, 2);

        square = new Graph();
        square.addVertex(1, new Graph.Vertex(0, 0));
        square.addVertex(2, new Graph.Vertex(1, 0));
        square.addVertex(3, new Graph.Vertex(0, 1));
        square.addVertex(4, new Graph.Vertex(1, 1));
/*  square:
        1 --(5)-- 2
        |         |
        (3)      (4)
        |         |
        3 --(6)-- 4 
*/
        square.addUndirectedEdge(1, 2, 5);
        square.addUndirectedEdge(1, 3, 3);
        square.addUndirectedEdge(2, 4, 4);
        square.addUndirectedEdge(3, 4, 6);

        triangle = new Graph();
        triangle.addVertex(1, new Graph.Vertex(0, 0));
        triangle.addVertex(2, new Graph.Vertex(0, 1));
        triangle.addVertex(3, new Graph.Vertex(1, 1));
/* triangle:
        1 --(5)-- 2
         \       /
          (8)  (3) 
            \ /
             3
 */
        triangle.addUndirectedEdge(1, 2, 5);
        triangle.addUndirectedEdge(1, 3, 8);
        triangle.addUndirectedEdge(2, 3, 3); 

        linear = new Graph();
        linear.addVertex(1, new Graph.Vertex(0, 0));
        linear.addVertex(2, new Graph.Vertex(1, 0));
        linear.addVertex(3, new Graph.Vertex(2, 0));
        linear.addVertex(4, new Graph.Vertex(3, 0));
/* linear: 
        1 --(5)-- 2 --(3)-- 3 --(1)-- 4
*/
        linear.addUndirectedEdge(1, 2, 5);
        linear.addUndirectedEdge(2, 3, 3);
        linear.addUndirectedEdge(3, 4, 1);

        disconnected = new Graph();
        disconnected.addVertex(1, new Graph.Vertex(0, 0));
        disconnected.addVertex(2, new Graph.Vertex(1, 0));
/* disconnected:
        1       2
 */
    }

    @Test
    public void shortestPath_basic() {
        Result<Integer> dist = Dijkstra.shortestPath(g, 1, 4);
        assertEquals("Shortest path 1→2→3→4 should have total cost 7", (long) dist.result, 7);
    }

    @Test
    public void shortestPath_directConnection() {
        Result<Integer> dist = Dijkstra.shortestPath(g, 1, 2);
        assertEquals("Direct edge 1→2 should cost 4", (long) dist.result, 4 );
    }

    @Test
    public void shortestPath_noPathExists() {
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
    public void shortestPath_sameNode() {
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


    //
    // Local Search
    //


    @Test
    public void localSearch_directPath() {
        // Direct edge exists: 1→2
        // Since there is a direct path, no shortcut needed (witness path exists)
        boolean result = Dijkstra.localSearch(square, 1, 2, -1, 100);
        assertFalse("Direct path exists, shortcut not needed", result);
    }

    @Test
    public void localSearch_excludeMiddleVertex() {
        // Square graph: exclude vertex 2
        // Original shortest path 1->2->4 = 5 + 4 = 9
        // Excluding 2: path 1->3->4 = 3 + 6 = 9 (equal)
        boolean result = Dijkstra.localSearch(square, 1, 4, 2, 100);
        assertFalse("Alternate path exists with equal length, shortcut not needed", result);
    }

    @Test
    public void localSearch_witnessPathShorter() {
        // Triangle graph: 1->3 direct = 8, 1->2->3 = 5+3=8
        // Alternate path exists with equal length
        boolean result = Dijkstra.localSearch(triangle, 1, 3, -1, 100);
        assertFalse("Alternate path exists with equal length, shortcut not needed", result);
    }

    @Test
    public void localSearch_excludeOnlyPath() {
        // Linear graph: only path from 1->3 through 2
        boolean result = Dijkstra.localSearch(linear, 1, 3, 2, 100);
        assertTrue("Excluding the only path vertex, shortcut needed", result);
    }

    @Test
    public void localSearch_maxDistanceBoundRespected() {
        // Square graph: path 1->2 = 5, maxDist = 4 → stop search
        boolean result = Dijkstra.localSearch(square, 1, 2, -1, 4);
        assertTrue("Max distance too small, shortcut needed", result);
    }


    @Test
    public void localSearch_exactlyAtMaxDistance() {
        // Square graph: path 1->2 = 5, maxDist = 5 → reachable
        boolean result = Dijkstra.localSearch(square, 1, 2, -1, 5);
        assertFalse("Path exactly at maxDist, no shortcut needed", result);
    }
    
    @Test
    public void localSearch_sourceEqualsTarget() {
        // Distance from a node to itself
        boolean result = Dijkstra.localSearch(square, 1, 1, -1, 100);
        assertFalse("Source equals target, shortcut not needed", result);
    }

    @Test
    public void localSearch_noPath() {
        boolean result = Dijkstra.localSearch(disconnected, 1, 2, -1, 100);
        assertTrue("No path exists, shortcut needed", result);
    }
    
    @Test
    public void localSearch_excludeSource() {
        // Excluding source vertex.. TODO does this require shortcut?
        boolean result = Dijkstra.localSearch(square, 1, 2, 1, 100);
        assertFalse("Excluding source vertex, shortcut needed", result);
    }

    @Test
    public void localSearch_maxDistanceTooSmall() {
        // Square graph: path 1->4 = 1->2->4 = 5+4=9, maxDist = 5 → stop search
        boolean result = Dijkstra.localSearch(square, 1, 4, -1, 5);
        assertTrue("Max distance too small, shortcut needed", result);
    }
}
