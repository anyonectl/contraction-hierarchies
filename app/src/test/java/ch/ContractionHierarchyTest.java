package ch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class ContractionHierarchyTest {

    private Graph g;
    private Graph square;
    private Graph triangle;
    private Graph linear;
    private Graph disconnected;
    private Graph test;

    @Before
    public void setup() {
        g = readGraphAsInputStream(true, "/ch/g.graph");
        square = readGraphAsInputStream(true, "/ch/square.graph");
        triangle = readGraphAsInputStream(true, "/ch/triangle.graph");
        linear = readGraphAsInputStream(true, "/ch/linear.graph");
        disconnected = readGraphAsInputStream(true, "/ch/disconnected.graph");
        test = readGraphAsInputStream(false, "/ch/test.graph");
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

    @Test
    public void preprocess_thoughConstructor_resultAugmentedGrapEffected_outSideReadGraphAsInputStream() {
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
    public void preprocess_thoughConstructor_resultAugmentedGrapEffected() {
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
    public void preprocess_thoughConstructor_resultOriginalGraphNotEffected() {
        ContractionHierarchy ch = new ContractionHierarchy(g);

        Graph original = ch.getOriginal();
        // Verify all vertices have been ranked (not -1)
        for (long v : original.getVertexIds()) {
            long rank = original.getVertex(v).rank;
            assertEquals("Vertex " + v + " should have a rank assigned", 
                            -1, rank);
        }
    }

    @Test
    public void preprocess_thoughConstructor_resultPassedGraphNotEffected() {
        ContractionHierarchy ch = new ContractionHierarchy(g);

        // Assess if passed original graph has been modified
        for (long v : g.getVertexIds()) {
            long rank = g.getVertex(v).rank;
            assertEquals("Vertex " + v + " should not have a rank assigned", 
                            -1, rank);
        }
    }

    @Test
    public void storeGraph_createsFile_checkFileExists() throws IOException {
        ContractionHierarchy ch = new ContractionHierarchy(test);

        String fileName = "testAugmented.graph";
        ch.storeGraph(test, fileName);

        File file = new File(fileName);
        assertEquals("Output file should be created", file.exists(), true);
        file.delete(); // cleanup

    }

    @Test
    public void storeGraph_createsFile_checkIfContentCorrect() throws IOException {
        ContractionHierarchy ch = new ContractionHierarchy(test);

        String fileName = "testAugmented.graph";
        ch.storeGraph(test, fileName);

        File file = new File(fileName);

        // Verify file content has expected header line
        List<String> lines = Files.readAllLines(file.toPath());
        String[] header = lines.get(0).trim().split("\\s+");
        String[] firstLineOfVertices = lines.get(1).trim().split("\\s+");
        String[] firstLineOfEdges = lines.get(7).trim().split("\\s+");

        String[] headerExpected = {"6", "10"};
        String[] firstLineOfVerteicesExpected = {"1", "0.000000", "0.000000", "-1"}; // line no. 1
        String[] firstLineOfEdgesExpected = {"1", "6", "3", "-1"}; // line no. 7

        assertArrayEquals(headerExpected, header);
        assertArrayEquals(firstLineOfVerteicesExpected, firstLineOfVertices);
        assertArrayEquals(firstLineOfEdgesExpected, firstLineOfEdges);

        file.delete(); // cleanup
    }
}
