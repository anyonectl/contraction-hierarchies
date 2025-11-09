package ch;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class BidirectionalDijkstraTest {

        Graph g;
        Graph square;
        Graph triangle;
        Graph linear;
        Graph disconnected;

    @Before
    public void setup() {
        g = readGraphAsInputStream(true, "/ch/g.graph");
        square = readGraphAsInputStream(true, "/ch/square.graph");
        triangle = readGraphAsInputStream(true, "/ch/triangle.graph");
        linear = readGraphAsInputStream(true, "/ch/linear.graph");
        disconnected = readGraphAsInputStream(true, "/ch/disconnected.graph");
    }

    // helper method for reading graphs 
    private Graph readGraphAsInputStream(boolean augmented, String filename) {
        InputStream in = getClass().getResourceAsStream(filename);
        assertNotNull("Resource not found!", in);
        Scanner sc = new Scanner(in);
        Graph g = augmented ? GraphReader.readAugmentedGraph(sc) : GraphReader.readGraph(sc);
        sc.close();
        return g;
    }

    //
    // Shortest Path
    //

    @Test
    public void shortestPath_basic() {
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(g, 1, 4);
        assertEquals("Shortest path 1→2→3→4 should have total cost 7", (long) dist.result, 7);
    }

    @Test
    public void shortestPath_directConnection() {
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(g, 1, 2);
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
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(disconnected, 1, 3);
        assertEquals("Unreachable vertex should return -1",-1, (long) dist.result);
    }
    
    @Test
    public void shortestPath_sameNode() {
        Result<Integer> dist = BidirectionalDijkstra.shortestPath(g, 1, 1);
        assertEquals("Distance from a node to itself should be 0", 0, (long) dist.result);
    }

    @Test
    public void shortestPath_correctShortestPath() {
        Graph h = new Graph();
        h.addVertex(1, new Graph.Vertex(0, 0));
        h.addVertex(2, new Graph.Vertex(1, 1));
        h.addVertex(3, new Graph.Vertex(2, 2));
        h.addVertex(4, new Graph.Vertex(3, 3));
        h.addVertex(5, new Graph.Vertex(4, 4));
/* 
graph as in lecture: https://www.youtube.com/watch?v=CHvQ3q_gJ7E&t=1465s

            (3)-- 2 --(3)-- 3 --(3) 
              \                 /
        source 1               4 target
                \             /
                 (5)-- 5 --(5)
we assess if the search ends when both queues first meet 1>5>4, or 
if the search continues and finds the correct shortest path 1>2>3>4.
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
    public void shortestPath_fromBlog() {
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


    // 
    // Bidirectional Search using ranks in augmented graph
    //


    // @Test
    // void bidirectionalSearch_ranksOnlyGoDown_returnsNotFound() {
    //     Graph g = new Graph();
    //     g.addVertex(1, new Graph.Vertex(0, 0, 1));
    //     g.addVertex(2, new Graph.Vertex(0, 0, 2));
    //     g.addVertex(3, new Graph.Vertex(0, 0, 3));
    //     g.addVertex(4, new Graph.Vertex(0, 0, 0));
    


    // }

}
