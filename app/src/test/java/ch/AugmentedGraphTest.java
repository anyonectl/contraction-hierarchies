package ch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import ch.Graph.Edge;

public class AugmentedGraphTest {

    AugmentedGraph simple;
    AugmentedGraph triangle;
    AugmentedGraph linear;
    AugmentedGraph star;

    @Before
    public void setup() {
        simple = new AugmentedGraph();
        simple.addVertex(1, new Graph.Vertex(0, 0));
        simple.addVertex(2, new Graph.Vertex(1, 0));
        simple.addVertex(3, new Graph.Vertex(0, 1));
        simple.addVertex(4, new Graph.Vertex(1, 1));
        simple.addUndirectedEdge(1, 2, 5);
        simple.addUndirectedEdge(1, 3, 3);
        simple.addUndirectedEdge(2, 4, 4);
        simple.addUndirectedEdge(3, 4, 6);

        triangle = new AugmentedGraph();
        triangle.addVertex(1, new Graph.Vertex(0, 0));
        triangle.addVertex(2, new Graph.Vertex(0, 1));
        triangle.addVertex(3, new Graph.Vertex(1, 1));
        triangle.addUndirectedEdge(1, 2, 5);
        triangle.addUndirectedEdge(1, 3, 8);
        triangle.addUndirectedEdge(2, 3, 3);

        linear = new AugmentedGraph();
        linear.addVertex(1, new Graph.Vertex(0, 0));
        linear.addVertex(2, new Graph.Vertex(1, 0));
        linear.addVertex(3, new Graph.Vertex(2, 0));
        linear.addUndirectedEdge(1, 2, 5);
        linear.addUndirectedEdge(2, 3, 3);

        star = new AugmentedGraph();
        star.addVertex(1, new Graph.Vertex(0, 0));
        star.addVertex(2, new Graph.Vertex(1, 0));
        star.addVertex(3, new Graph.Vertex(2, 0));
        star.addVertex(5, new Graph.Vertex(1, 1));
        star.addUndirectedEdge(1, 5, 2);
        star.addUndirectedEdge(2, 5, 3);
        star.addUndirectedEdge(3, 5, 4);
    }

    // CONTRACTION

    @Test
    public void contract_shortcutNeeded() {
        int initialEdges = triangle.m;

        // Contract vertex 2
        // neighbours: 1 and 3
        // Path 1→2→3 = 5 + 3 = 8
        // Direct path 1→3 = 8 (equal, so witness exists)
        // But if witness is equal, we still might add shortcut depending on implementation
        int shortcutsAdded = triangle.contract(2);
        
        // Since direct path 1→3 exists with weight 8, and path through 2 is also 8,
        // witness path should prevent shortcut (witness <= pathThroughV)
        assertEquals(0, shortcutsAdded);
        assertEquals(initialEdges, triangle.m);
        assertEquals(initialEdges + shortcutsAdded, triangle.m);
    }
    
    @Test
    public void contract_shortcutNeededNoWitness() {
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        
        // Linear graph: 1---5---2---3---3
        g.addUndirectedEdge(1, 2, 5);
        g.addUndirectedEdge(2, 3, 3);
        
        // Contract vertex 2
        // neighbours: 1 and 3
        // Path 1→2→3 = 8
        // No direct path 1→3, so shortcut needed
        int shortcutsAdded = g.contract(2);
        
        // Should add 2 shortcuts (1→3 and 3→1 for undirected)
        assertEquals(2, shortcutsAdded);
        
        // Verify shortcut was added
        boolean foundShortcut = false;
        for (Graph.Edge e : g.getNeighbours(1)) {
            AugmentedGraph.AugmentedEdge ae = (AugmentedGraph.AugmentedEdge) e;
            if (ae.to == 3 && ae.weight == 8 && ae.contracted == 2) {
                foundShortcut = true;
                break;
            }
        }
        assertTrue("Shortcut 1→3 should be added", foundShortcut);
    }
    
    @Test
    public void contract_multipleShortcuts() {
        AugmentedGraph g = new AugmentedGraph();
        // Star graph with center vertex 5
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(5, new Graph.Vertex(1, 1));
        
        g.addUndirectedEdge(1, 5, 2);
        g.addUndirectedEdge(2, 5, 3);
        g.addUndirectedEdge(3, 5, 4);
        
        // Contract center vertex 5
        // Should add shortcuts: 1-2, 1-3, 2-3 (all pairs)
        int shortcutsAdded = g.contract(5);
        
        // 3 neighbours, so 3*2 = 6 directed edges (or 3 pairs * 2 directions each)
        assertEquals(6, shortcutsAdded);
    }
    
    @Test
    public void contract_verifyShortcutContractedField() {
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        
        g.addUndirectedEdge(1, 2, 5);
        g.addUndirectedEdge(2, 3, 3);
        
        g.contract(2);
        
        // Check that shortcut has contracted field set to 2
        for (Edge e : g.getNeighbours(1)) {
            if (e.to == 3) {
                AugmentedGraph.AugmentedEdge ae = (AugmentedGraph.AugmentedEdge) e;
                assertEquals("Shortcut should have contracted=2", 2, ae.contracted);
            }
        }
    }
    
    @Test
    public void contract_isolatedVertex() {
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        
        // Vertex 1 has no edges
        int shortcutsAdded = g.contract(1);
        assertEquals(0, shortcutsAdded);
    }
    
    @Test
    public void contract_singleNeighbour() {
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        
        g.addUndirectedEdge(1, 2, 5);
        
        // Vertex 2 has only one neighbour (1)
        // No pairs to check
        int shortcutsAdded = g.contract(2);
        assertEquals(0, shortcutsAdded);
    }
        
    @Test
    public void contract_witnessPathPreventsShortcut() {
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(2, 0));
        g.addVertex(4, new Graph.Vertex(1, 1));
        
        // Create graph where 1-2-3 but also 1-4-3 with shorter path
        g.addUndirectedEdge(1, 2, 5);
        g.addUndirectedEdge(2, 3, 5);
        g.addUndirectedEdge(1, 4, 2);
        g.addUndirectedEdge(4, 3, 2);
        
        // Contract vertex 2
        // Path 1→2→3 = 10
        // Witness path 1→4→3 = 4 (shorter!)
        // No shortcut should be added
        int shortcutsAdded = g.contract(2);
        assertEquals(0, shortcutsAdded);
    }
    
    @Test
    public void contract_noNeighbours() {
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        
        // Vertex with no edges
        int shortcutsAdded = g.contract(1);
        assertEquals(0, shortcutsAdded);
    }
    
    @Test
    public void contract_alreadyDirectlyConnected() {
        // All vertices are directly connected
        // Contract vertex 2: neighbours are 1 and 3
        // 1 and 3 are already directly connected with weight 8
        // Path through 2: 5 + 3 = 8
        // Witness exists with same distance, so no shortcut
        int shortcutsAdded = triangle.contract(2);
        assertEquals(0, shortcutsAdded);
    }
        
    @Test
    public void getEdgeDifference_linearGraphMiddleVertex() {
        // Linear graph: 1---2---3
        // Contracting 2: removes 2 edges (to 1 and 3), adds 2 shortcuts (1↔3)
        // Edge difference: 2 - 2 = 0
        int edgeDiff = linear.getEdgeDifference(2);
        assertEquals(0, edgeDiff);
    }


    // EDGE DIFFERENCE
    
    @Test
    public void getEdgeDifference_linearGraphEndVertex() {
        // Linear graph: contracting end vertex (vertex 1)
        // Removes one edge (to vertex 2), adds 0 shortcuts (only one neighbour)
        // Edge difference: 0 - 1 = -1
        int edgeDiff = linear.getEdgeDifference(1);
        assertEquals(-1, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_triangleVertexWithWitness() {
        // Triangle: all vertices connected
        // Contracting vertex 2, neighbours are 1 and 3
        // Path 1→2→3 = 5 + 3 = 8
        // Direct path 1→3 = 8 (witness exists with equal distance)
        // No shortcuts needed, removes 2 edges
        // Edge difference: 0 - 2 = -2
        int edgeDiff = triangle.getEdgeDifference(2);
        assertEquals(-2, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_starCenterVertex() {
        // Star graph: center vertex 5 with 3 neighbours
        // Removes 3 edges
        // Adds 6 shortcuts (3 pairs × 2 directions: 1↔2, 1↔3, 2↔3)
        // Edge difference: 6 - 3 = 3
        int edgeDiff = star.getEdgeDifference(5);
        assertEquals(3, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_starLeafVertex() {
        // Star graph: leaf vertex has only 1 neighbour (center)
        // Removes 1 edge, adds 0 shortcuts
        // Edge difference: 0 - 1 = -1
        int edgeDiff = star.getEdgeDifference(1);
        assertEquals(-1, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_twoNeighboursNoDirectPath() {
        // Simple graph: contract vertex 1
        // neighbours: 2 and 3
        // No direct path 2↔3, so needs shortcut
        // Removes 2 edges, adds 2 shortcuts
        // Edge difference: 2 - 2 = 0
        int edgeDiff = simple.getEdgeDifference(1);
        assertEquals(0, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_multipleCallsSameResult() {
        // Calling getEdgeDifference multiple times should give same result
        // (it shouldn't't modify the graph)
        int first = linear.getEdgeDifference(2);
        int second = linear.getEdgeDifference(2);
        int third = linear.getEdgeDifference(2);
        
        assertEquals(first, second);
        assertEquals(second, third);
    }
    
    @Test
    public void getEdgeDifference_doesNotModifyGraph() {
        // getEdgeDifference should not modify the graph
        int initialEdges = linear.m;
        linear.getEdgeDifference(2);
        int finalEdges = linear.m;
        
        assertEquals(initialEdges, finalEdges);
    }
    
    @Test
    public void getEdgeDifference_vertexWithShorterWitnessPath() {
        // Create graph where witness path is shorter than path through v
        AugmentedGraph g = new AugmentedGraph();
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
        // Edge difference: 0 - 2 = -2
        int edgeDiff = g.getEdgeDifference(2);
        assertEquals(-2, edgeDiff);
    }
        
    @Test
    public void getEdgeDifference_isolatedVertex() {
        // Isolated vertex with no edges
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        
        // Vertex 1 has no neighbours
        int edgeDiff = g.getEdgeDifference(1);
        assertEquals(0, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_singleNeighbour() {
        // Vertex with only one neighbour
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addUndirectedEdge(1, 2, 5);
        
        // Vertex 1 has only one neighbour
        // No pairs to check, so 0 shortcuts
        // Removes 1 edge
        // Edge difference: 0 - 1 = -1
        int edgeDiff = g.getEdgeDifference(1);
        assertEquals(-1, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_fullyConnected() {
        // All vertices are directly connected so no shortcuts are needed
        AugmentedGraph g = new AugmentedGraph();
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
        // Edge difference: 0 - 3 = -3
        int edgeDiff = g.getEdgeDifference(1);
        assertEquals(-3, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_longChainMaximumShortcuts() {
        // Create a long chain where contracting middle adds many shortcuts
        AugmentedGraph g = new AugmentedGraph();
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
        // Edge difference: 2 - 2 = 0
        int edgeDiff = g.getEdgeDifference(3);
        assertEquals(0, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_nonExistentVertexReturnsZero() {
        // Vertex that doesn't exist or has no neighbours
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        
        // Vertex 999 doesn't exist (hasNeighbours will return false/null)
        // Should handle gracefully and return 0
        int edgeDiff = g.getEdgeDifference(999);
        assertEquals(0, edgeDiff);
    }
    
    @Test
    public void getEdgeDifference_highDegreeVertex() {
        // High degree vertex should have large positive edge difference
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(0, new Graph.Vertex(0, 0));
        
        // Create star with 5 leaf nodes
        for (int i = 1; i <= 5; i++) {
            g.addVertex(i, new Graph.Vertex(i, 0));
            g.addUndirectedEdge(0, i, 1);
        }
        
        // Contract center: 5 neighbours
        // Removes 5 edges
        // Needs shortcuts for all pairs: C(5,2) * 2 = 10 * 2 = 20 directed edges
        // Edge difference: 20 - 5 = 15
        int edgeDiff = g.getEdgeDifference(0);
        assertEquals(15, edgeDiff);
    }
}