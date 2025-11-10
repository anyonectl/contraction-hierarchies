package ch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ContractionHierarchyTest {

    private Graph g;
    private Graph square;
    private Graph triangle;

    @Before
    public void setUp() {
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

/*  square:
        1 --(5)-- 2
        |         |
        (3)      (4)
        |         |
        3 --(6)-- 4 
*/

/* triangle:
        1 --(5)-- 2
         \       /
          (8)  (3) 
            \ /
             3
 */
    }
    
    @Test
    public void preprocess_checkAugmentedIsAffected() {
        ContractionHierarchy ch = new ContractionHierarchy(g);

        Graph augmented = ch.getAugmented();
        // Verify all vertices have been ranked (not -1)
        for (long v : augmented.getVertexIds()) {
            long rank = augmented.getVertex(v).rank;
            assertNotEquals("Vertex " + v + " should have a rank assigned", 
                            -1, rank);
        }
    }

    @Test
    public void preprocess_checkOriginalIsNotAffected() {
        ContractionHierarchy ch = new ContractionHierarchy(g);

        Graph original = ch.getOriginal();
        // Verify all vertices have been ranked (not -1)
        for (long v : original.getVertexIds()) {
            long rank = original.getVertex(v).rank;
            assertEquals("Vertex " + v + " should have a rank assigned", 
                            -1, rank);
        }
    }

    // @Test
    // public void preprocess_checkSourceIsNotAffected() {
    //     // constructor also also preprocesses the graph, but should not effect the original graph
    //     ContractionHierarchy ch = new ContractionHierarchy(g);

    //     // Verify all vertices have been ranked (not -1)
    //     for (long v : g.getVertexIds()) {
    //         long rank = g.getVertex(v).rank;
    //         assertEquals("Vertex " + v + " should have a rank assigned", 
    //                         -1, rank);
    //     }
    // }

    // @Test
    // void storeGraph_createsFile_checkFileHeader() throws IOException {
    //     Graph graph = new Graph();
    //     ContractionHierarchy ch = new ContractionHierarchy(graph);

    //     String fileName = "test_graph.txt";
    //     ch.storeGraph(graph, fileName);

    //     File file = new File(fileName);
    //     assertTrue(file.exists(), "Output file should be created");

    //     // Verify file content has expected header line
    //     String firstLine = Files.readAllLines(file.toPath()).get(0);
    //     assertTrue(firstLine.contains("3"), "First line should contain vertex count");

    //     file.delete(); // cleanup
    // }

    // @Test
    // void preprocess_emptyGraph_doesNotCrash() {
    //     Graph emptyGraph = new Graph();
    //     assertDoesNotThrow(() -> new ContractionHierarchy(emptyGraph),
    //             "Should handle empty graph gracefully");
    // }

    // @Test
    // void query_triangleGraph_returnsDefaultResult() {
    //     Graph emptyGraph = new Graph();
    //     ContractionHierarchy ch = new ContractionHierarchy(graph);

    //     Result<Integer> result = ch.query(0, 1);
    //     assertNotNull(result, "Query should return a non-null result");
    //     assertEquals(0, result.value(), "Placeholder result value should be 0");
    // }

}
