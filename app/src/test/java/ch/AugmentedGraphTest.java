package ch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import ch.Graph.Edge;

public class AugmentedGraphTest {

    AugmentedGraph simple;
    AugmentedGraph triangle;

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
    }

    @Test
    public void contract_shortcutNeeded() {
        int initialEdges = triangle.m;

        // Contract vertex 2
        // Neighbors: 1 and 3
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
        // Neighbors: 1 and 3
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
        
        // 3 neighbors, so 3*2 = 6 directed edges (or 3 pairs * 2 directions each)
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
    public void contract_singleNeighbor() {
        AugmentedGraph g = new AugmentedGraph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        
        g.addUndirectedEdge(1, 2, 5);
        
        // Vertex 2 has only one neighbor (1)
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
        // Contract vertex 2: neighbors are 1 and 3
        // 1 and 3 are already directly connected with weight 8
        // Path through 2: 5 + 3 = 8
        // Witness exists with same distance, so no shortcut
        int shortcutsAdded = triangle.contract(2);
        assertEquals(0, shortcutsAdded);
    }
}