package ch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Test;

public class GraphReaderTest {

    // helper method for reading graphs
    private Graph readGraphAsInputStream(boolean augmented, String filename) {
        // debugging where the resource is located:
        // System.out.println("Classpath root: " + getClass().getResource("/"));
        // System.out.println("Trying resource: " + getClass().getResource(filename));
        // System.out.println("Trying resource with leading slash: " + getClass().getResource("/ch/" + filename));
        InputStream in = getClass().getResourceAsStream(filename);
        assertNotNull("Resource not found!", in);
        Scanner sc = new Scanner(in);
        Graph g = augmented ? GraphReader.readAugmentedGraph(sc) : GraphReader.readGraph(sc);
        sc.close();
        return g;
    }

    @Test
    public void readGraph_simpleGraph() {
        Graph simple = new Graph();
        simple.addVertex(1, new Graph.Vertex(0, 0));
        simple.addVertex(2, new Graph.Vertex(1, 0));
        simple.addVertex(3, new Graph.Vertex(0, 1));
        simple.addVertex(4, new Graph.Vertex(1, 1));
        /*  simple:
                1 --(5)-- 2
                |         |
                (3)      (4)
                |         |
                3 --(6)-- 4 
        */
        simple.addUndirectedEdge(1, 2, 5);
        simple.addUndirectedEdge(1, 3, 3);
        simple.addUndirectedEdge(2, 4, 4);
        simple.addUndirectedEdge(3, 4, 6);

        Graph fromFile = readGraphAsInputStream(false,"/ch/simple.graph");
        assertEquals(simple.getN(), fromFile.getN());
        assertEquals(simple.getM(), fromFile.getM());
    }

    @Test
    public void readAugmentedGraph_simpleAugmentedGraph() {
        Graph simple = new Graph();
        simple.addVertex(1, new Graph.Vertex(0, 0, 3));
        simple.addVertex(2, new Graph.Vertex(1, 0, 0));
        simple.addVertex(3, new Graph.Vertex(0, 1, 2));
        simple.addVertex(4, new Graph.Vertex(1, 1, 1));
        /*  simple:
                1,3 --(5)-- 2,0
                |            |
                (3)         (4)
                |            |
                3,2 --(6)-- 4,1 
        */
        simple.addUndirectedEdge(1, 2, 5); // there are no contractions in simple
        simple.addUndirectedEdge(1, 3, 3);
        simple.addUndirectedEdge(2, 4, 4);
        simple.addUndirectedEdge(3, 4, 6);

        Graph fromFile = readGraphAsInputStream(true,"/ch/simpleaugmented.graph");
        for (Long id : simple.getVertices().keySet()) {
            long simpleRank = simple.getVertex(id).rank;
            long fromFileRank = fromFile.getVertex(id).rank;
            assertEquals(fromFileRank, simpleRank);
        }
    }
    
}
