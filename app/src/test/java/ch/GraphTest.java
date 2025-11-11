package ch;

import static org.junit.Assert.*;

import java.util.Scanner;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import ch.Graph.Edge;
import ch.Graph.Vertex;

public class GraphTest {
    Graph h;
    Graph square;
    Graph triangle;
    Graph linear;
    Graph star;

    @Before // each
    public void setUp() {
        h = new Graph();
        h.addVertex(1, new Graph.Vertex(0, 0));
        h.addVertex(2, new Graph.Vertex(1, 1));
        h.addVertex(3, new Graph.Vertex(2, 2));
        h.addVertex(4, new Graph.Vertex(3, 3));
/* g:
        1 -(4)- 2 -(1)- 3
         \             /
          (8)-- 4 --(2)
*/
        h.addUndirectedEdge(1, 2, 4);
        h.addUndirectedEdge(2, 3, 1);
        h.addUndirectedEdge(1, 4, 8);
        h.addUndirectedEdge(3, 4, 2);

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
        linear.addVertex(3, new Graph.Vertex(1, 0));
        linear.addUndirectedEdge(1, 2, 5);
        linear.addUndirectedEdge(2, 3, 5);
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
    public void checkFields_emptyGraph_returnsTrue() {
        Graph g = new Graph();
        assertEquals(0, g.getM());
        assertEquals(0, g.getN());
        assertEquals(0, g.getVertices().size());
        assertEquals(0, g.getOutEdges().size());
        assertEquals(0, g.getInEdges().size());
    }

    @Test 
    public void checkFields_directedEdgeSingleVertex_returnsTrue() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        g.addEdge(0, 0, 0);
        assertEquals(1, g.getM());
        assertEquals(1, g.getN());
        assertEquals(1, g.getVertices().size());
        assertEquals(1, g.getOutEdges().size());
        assertEquals(1, g.getInEdges().size());
    }

    @Test 
    public void checkFields_undirectedEdgeSingleVertex_returnsTrue() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        g.addVertex(1, new Vertex(0, 0));
        g.addUndirectedEdge(1, 0, 0);
        assertEquals(2, g.getM());
        assertEquals(2, g.getN());
        assertEquals(2, g.getVertices().size());
        assertEquals(2, g.getOutEdges().size());
        assertEquals(2, g.getInEdges().size());
    }

    @Test
    public void addVertex_notRanked_addsSingleVertex() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        int numberOfVertices = g.getVertices().size();
        int n = g.getN();
        assertEquals(1, numberOfVertices);
        assertEquals(1, n);
    }

    @Test
    public void addVertex_ranked_addsSingleVertex() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0, 1));
        int numberOfVertices = g.getVertices().size();
        int n = g.getN();
        assertEquals(1, numberOfVertices);
        assertEquals(1, n);
    }

    @Test
    public void addEdge_notContracted_addsSingleDirectedEdge() {
        Graph g = new Graph();
        g.addEdge(0, 0, 0);
        int m = g.getM();
        int sizeOfInEdges = g.getInNeighbours(0).size();
        int sizeOfOutEdges = g.getNeighbours(0).size();
        assertEquals(1, m);
        assertEquals(1, sizeOfOutEdges);
        assertEquals(1, sizeOfInEdges);
    }

    @Test
    public void addEdge_contracted_addsSingleDirectedEdge() {
        Graph g = new Graph();
        g.addEdge(0, 0, 1,0);
        int m = g.getM();
        int sizeOfInEdges = g.getInNeighbours(0).size();
        int sizeOfOutEdges = g.getNeighbours(0).size();
        assertEquals(1, m);
        assertEquals(1, sizeOfOutEdges);
        assertEquals(1, sizeOfInEdges);
    }

    @Test
    public void addUndirectedEdge_notContracted_addsTwoDirectedEdge() {
        Graph g = new Graph();
        g.addUndirectedEdge(0, 0, 0);
        int m = g.getM();
        int sizeOfInEdges = g.getInNeighbours(0).size();
        int sizeOfOutEdges = g.getNeighbours(0).size();
        assertEquals(2, m);
        assertEquals(2, sizeOfOutEdges);
        assertEquals(2, sizeOfInEdges);
    }

    @Test
    public void addUndirectedEdge_contracted_addsTwoDirectedEdge() {
        Graph g = new Graph();
        g.addUndirectedEdge(0, 0, 1, 0);
        int m = g.getM();
        int sizeOfInEdges = g.getInNeighbours(0).size();
        int sizeOfOutEdges = g.getNeighbours(0).size();
        assertEquals(2, m);
        assertEquals(2, sizeOfOutEdges);
        assertEquals(2, sizeOfInEdges);
    }

    @Test
    public void outdegree_addTwoUndirectedEdges_returnsAllIncidents() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        g.addVertex(1, new Vertex(0, 0));
        g.addVertex(2, new Vertex(0, 0));
        g.addUndirectedEdge(1, 0, 0);
        g.addUndirectedEdge(2, 0, 0);
        int[] outdegrees = { g.outdegree(0), g.outdegree(1), g.outdegree(2) };
        int[] expected = { 2, 1, 1 };
        assertArrayEquals(expected, outdegrees);
    }

    @Test
    public void outdegree_addDirectedEdge_returnsAllIncidents() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        g.addVertex(1, new Vertex(0, 0));
        g.addEdge(0, 1, 0);
        int[] outdegrees = {g.outdegree(0), g.outdegree(1)};
        int[] expected = {1,0};
        assertArrayEquals(expected, outdegrees);
    }

    @Test
    public void outdegree_noEdges_returnsZero() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        assertEquals(0, g.outdegree(0));
    }

    @Test
    public void indegree_addTwoUndirectedEdges_returnsAllIncidents() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        g.addVertex(1, new Vertex(0, 0));
        g.addVertex(2, new Vertex(0, 0));
        g.addUndirectedEdge(1, 0, 0);
        g.addUndirectedEdge(2, 0, 0);
        int[] indegrees = { g.indegree(0), g.indegree(1), g.indegree(2) };
        int[] expected = { 2, 1, 1 };
        assertArrayEquals(expected, indegrees);
    }

    @Test
    public void indegree_addDirectedEdge_returnsAllIncidents() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        g.addVertex(1, new Vertex(0, 0));
        g.addEdge(0, 1, 0);
        int[] indegrees = {g.indegree(0), g.indegree(1)};
        int[] expected = {0,1};
        assertArrayEquals(expected, indegrees);
    }

    @Test
    public void indegree_noEdges_returnsZero() {
        Graph g = new Graph();
        g.addVertex(0, new Vertex(0, 0));
        assertEquals(0, g.indegree(0));
    }

    @Test
    public void copy_unrankedGraph_modifyRank() { 
        Graph simple = readGraphAsInputStream(false, "/ch/simple.graph");
        Graph simpleCopy = simple.copy();
        
        // modify
        simpleCopy.getVertex(1).rank = Integer.MAX_VALUE;
        
        Vertex original = simple.getVertex(1);
        Vertex copy = simpleCopy.getVertex(1);

        assertEquals(-1, original.rank);
        assertEquals(Integer.MAX_VALUE, copy.rank);
    }

    @Test
    public void copy_rankedGraph_modifyRank_inputStream() throws Exception {
        Graph augmented = readGraphAsInputStream(true,"/ch/simpleaugmented.graph");
        Graph augmentedCopy = augmented.copy();

        // modify
        augmentedCopy.getVertex(1).rank = Integer.MAX_VALUE;

        Vertex original = augmented.getVertex(1);
        Vertex copy = augmentedCopy.getVertex(1);

        assertEquals(3, original.rank);
        assertEquals(Integer.MAX_VALUE, copy.rank);
    }
 

    //
    // CONTRACTION
    //


    @Test
    public void contract_triangle_noShortcutAdded() {
        int initialEdges = triangle.getM();
/* triangle:
        1 --(5)-- 2
         \       /
          (8)  (3) 
            \ /
             3

        Contract vertex 2
        Neighbours: 1 and 3
        Path 1→2→3 = 5 + 3 = 8
        Direct path 1→3 = 8 (equal, so witness exists)
 */
        triangle.contract(2, 0);
        int edgesAfterContraction = triangle.getM();
        assertEquals(0, initialEdges - edgesAfterContraction);
    }
    
    @Test
    public void contract_linearGraph_returnsNumberOfShortCuts() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        
        g.addUndirectedEdge(1, 2, 5);
        g.addUndirectedEdge(2, 3, 3);
        // Linear graph: 1 --(5)-- 2 --(3)-- 3
        // Contract: 2
        // Neighbours: 1 and 3
        // Path: 1→2→3 = 8
        // No direct path 1→3. Shortcut needed.
        g.contract(2, 1);
        int shortcuts = g.getShortcuts();
        int edgesAfterContraction = g.getM();
        // Should add 2 shortcuts (1→3 and 3→1 for undirected)
        assertEquals(6, edgesAfterContraction);
        assertEquals(2, shortcuts);
        
        // Verify shortcut was added
        boolean foundShortcut = false;
        for (Graph.Edge e : g.getNeighbours(1)) {
            Graph.Edge ae = (Graph.Edge) e;
            if (ae.to == 3 && ae.weight == 8 && ae.contracted == 2) {
                foundShortcut = true;
                break;
            }
        }
        assertTrue("Shortcut 1→3 should be added", foundShortcut);
    }
    
    @Test
    public void contract_starGraph_returnsNumberOfShortCuts() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(5, new Graph.Vertex(1, 1));
        g.addUndirectedEdge(1, 5, 2);
        g.addUndirectedEdge(2, 5, 3);
        g.addUndirectedEdge(3, 5, 4);
/* Star graph with center vertex 5 
        1     2
        |    /
       (2) (3)
        | /
        5 --(4)-- 3

        Contract: 5
        Should add shortcuts: 1-2, 1-3, 2-3 (all pairs)
        3 neighbours, so 3*2 = 6 directed edges (or 3 pairs * 2 directions each)
*/
        g.contract(5, 1);
        int shortcuts = g.getShortcuts();
        assertEquals(6, shortcuts);
    }
    
    @Test
    public void contract_linearGraph_returnConfirmContractionOnEdgeField() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addUndirectedEdge(1, 2, 5);
        g.addUndirectedEdge(2, 3, 3);
/* graph:
        1 --(5)-- 2 --(3)-- 3

        When we contract 2 the edge added (which is a shortcut) should point to 2.
 */
        g.contract(2, 1);
        for (Edge e : g.getNeighbours(1)) {
            if (e.to == 3) {
                assertEquals("Shortcut should have contracted=2", 2, e.contracted);
            }
        }
    }
    
    @Test
    public void contract_disconnectedGraph_returnsNoShortcuts() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        
        g.contract(1, 1);
        int shortcuts = g.getShortcuts();
        assertEquals(0, shortcuts);
    }
    
    @Test
    public void contract_singleNeighbourGraph_returnsNoShortcuts() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addUndirectedEdge(1, 2, 5);
/* graph:
        1 --(5) -- 2
        Vertex 2 has only one neighbour (1)
        No pairs to check. No shortcuts added.
 */
        g.contract(2, 1);
        int shortcuts = g.getShortcuts();
        assertEquals(0, shortcuts);
    }
        
    @Test
    public void contract_vertexWithNoWitnessPath_createsNoShortcuts() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(4, new Graph.Vertex(1, 1));
        g.addUndirectedEdge(1, 2, 5);
        g.addUndirectedEdge(2, 3, 5);
        g.addUndirectedEdge(1, 4, 2);
        g.addUndirectedEdge(4, 3, 2);
/* 
        1 --(5)-- 2
        |         |
       (2)       (5)   
        |         |
        4 --(2)-- 3

        Create graph where 1-2-3 but also 1-4-3 with shorter path
        Contract vertex 2
        Path 1→2→3 = 10
        Witness path 1→4→3 = 4 (shorter!)
        No shortcut should be added
*/
        g.contract(2, 1);
        int shortcuts = g.getShortcuts();
        assertEquals(0, shortcuts);
    }
    
    @Test
    public void contract_singleVertexNoNeighbors_returnsNoShortcuts() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        
        // Vertex with no edges
        g.contract(1, 1);
        int shortcuts = g.getShortcuts(); 
        assertEquals(0, shortcuts);
    }

    //
    // EDGE DIFFERENCE
    //

    @Test
    public void getEdgeDifference_linearGraphMiddleVertex() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(1, 0));
        g.addUndirectedEdge(1, 2, 5);
        g.addUndirectedEdge(2, 3, 5);
/* graph:
        1 --(5)-- 2 --(5)-- 3
        Contracting 2: removes 4 edges (2 <-> 1 and 2 <-> 3)), adds 2 shortcuts (1 <-> 3)
        Edge difference: 2 - 4 = -2
*/
        int edgeDiff = g.getEdgeDifference(2);
        assertEquals(-2, edgeDiff);
    }

    @Test
    public void getEdgeDifference_linearGraphEndVertex() {

/* graph:
        1 --(5)-- 2 --(5)-- 3
        Linear graph: contracting end vertex (vertex 1)
        Removes one edge (to vertex 2), adds 0 shortcuts (only one neighbour)
        Edge difference: 0 - 2 = -2
*/
        int edgeDiff = linear.getEdgeDifference(1);
        assertEquals(-2, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_triangleVertexWithWitness() {
/* triangle:
        1 --(5)-- 2
         \       /
          (8)  (3) 
            \ /
             3

    Contracting 2, neighbours are 1 and 3
    Direct path 1→3 = 8 (witness exists with equal distance)
    No shortcuts needed, removes 2 undirected edges (== 4 edges)
    Edge difference: 0 - 4 = -4
*/  
        int edgeDiff = triangle.getEdgeDifference(2);
        assertEquals(-4, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_starCenterVertex() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(5, new Graph.Vertex(1, 1));
        g.addUndirectedEdge(1, 5, 2);
        g.addUndirectedEdge(2, 5, 3);
        g.addUndirectedEdge(3, 5, 4);
/* Star graph:
        1     2
        |    /
       (2) (3)
        | /
        5 --(4)-- 3

        Contract: 5
        Should add shortcuts: 1-2, 1-3, 2-3 (all pairs)
        Removes 3 undirected edges (== 6 edges) 
        Adds 6 shortcuts (3 pairs × 2 directions: 1↔2, 1↔3, 2↔3)
        Edge difference: 6 - 6 = 0
*/
        int edgeDiff = g.getEdgeDifference(5);
        assertEquals(0, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_starLeafVertex() {
                        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(5, new Graph.Vertex(1, 1));
        g.addUndirectedEdge(1, 5, 2);
        g.addUndirectedEdge(2, 5, 3);
        g.addUndirectedEdge(3, 5, 4);
/* Star graph:
        1     2
        |    /
       (2) (3)
        | /
        5 --(4)-- 3

        Contract: 1 (leaf)
        Removes 1 undirected edge (== 2 edges), adds 0 shortcuts        
*/

        int edgeDiff = g.getEdgeDifference(1);
        assertEquals(-2, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_twoNeighboursNoDirectPath() {
/*  square:
        1 --(5)-- 2
        |         |
        (3)      (4)
        |         |
        3 --(6)-- 4 
*/
        int edgeDiff = square.getEdgeDifference(1);
        assertEquals(-2, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_multipleCallsSameResult() {
        int first = linear.getEdgeDifference(2);
        int second = linear.getEdgeDifference(2);
        int third = linear.getEdgeDifference(2);
        
        assertEquals(first, second);
        assertEquals(second, third);
    }
    
    @Test
    public void getEdgeDifference_doesNotModifyGraph_returnsSame() {
        int initialEdges = linear.getM();
        linear.getEdgeDifference(2);
        int finalEdges = linear.getM();
        assertEquals(initialEdges, finalEdges);
    }
    
    @Test
    public void getEdgeDifference_vertexWithShorterWitnessPath() {
        // Create graph where witness path is shorter than path through v
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(4, new Graph.Vertex(1, 1));
        g.addUndirectedEdge(1, 2, 10);
        g.addUndirectedEdge(2, 3, 10);
        g.addUndirectedEdge(1, 4, 2);
        g.addUndirectedEdge(4, 3, 2);
        
        // Contract vertex 2
        // Path 1→2→3 = 20
        // Witness 1→4→3 = 4 (much shorter!)
        // No shortcut needed
        // Removes 2 edges, adds 0 shortcuts
        int edgeDiff = g.getEdgeDifference(2);
        assertEquals(-4, edgeDiff);
    }
        
    @Test
    public void getEdgeDifference_isolatedVertex() {
        // Isolated vertex with no edges
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        
        // Vertex 1 has no neighbours
        int edgeDiff = g.getEdgeDifference(1);
        assertEquals(0, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_singleNeighbour() {
        // Vertex with only one neighbour
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addUndirectedEdge(1, 2, 5);
        
        // Vertex 1 has only one neighbour
        // No pairs to check, so 0 shortcuts
        // Removes 1 edge
        int edgeDiff = g.getEdgeDifference(1);
        assertEquals(-2, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_fullyConnected() {
        // All vertices are directly connected so no shortcuts are needed
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(4, new Graph.Vertex(3, 0));
        
        // Fully connected: every vertex connected to every other
        g.addUndirectedEdge(1, 2, 1);
        g.addUndirectedEdge(1, 3, 1);
        g.addUndirectedEdge(1, 4, 1);
        g.addUndirectedEdge(2, 3, 1);
        g.addUndirectedEdge(2, 4, 1);
        g.addUndirectedEdge(3, 4, 1);
        
        // Contract vertex 1: has 3 neighbours (2, 3, 4)
        // All pairs already directly connected with weight 1
        // Path through 1 is always 2, direct path is 1
        // Witnesses exist for all pairs, no shortcuts needed
        // Removes 3 edges, adds 0 shortcuts
        int edgeDiff = g.getEdgeDifference(1);
        assertEquals(-6, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_longChainMaximumShortcuts() {
        // Create a long chain where contracting middle adds many shortcuts
        Graph g = new Graph();
        for (int i = 1; i <= 5; i++) {
            g.addVertex(i, new Graph.Vertex(i, 0));
        }
        
        // Chain: 1-2-3-4-5
        g.addUndirectedEdge(1, 2, 1);
        g.addUndirectedEdge(2, 3, 1);
        g.addUndirectedEdge(3, 4, 1);
        g.addUndirectedEdge(4, 5, 1);
        
        // Contract vertex 3: neighbours are 2 and 4
        // No direct path 2↔4
        // Adds 2 shortcuts, removes 2 edges
        int edgeDiff = g.getEdgeDifference(3);
        assertEquals(-2, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_nonExistentVertexReturnsZero() {
        Graph g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        
        // Vertex 999 doesn't exist (hasNeighbours will return false/null)
        // Should handle gracefully and return 0
        int edgeDiff = g.getEdgeDifference(999);
        assertEquals(0, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_highDegreeVertex() {
        Graph g = new Graph();
        g.addVertex(0, new Graph.Vertex(0, 0));
        
        // Create star with 5 leaf nodes
        for (int i = 1; i <= 5; i++) {
            g.addVertex(i, new Graph.Vertex(i, 0));
            g.addUndirectedEdge(0, i, 1);
        }
        
        // Contract center: 5 neighbours
        // Removes 5 edges
        // Needs shortcuts for all pairs: C(5,2) * 2 = 10 * 2 = 20 directed edges
        int edgeDiff = g.getEdgeDifference(0);
        assertEquals(10, edgeDiff);
    }
}