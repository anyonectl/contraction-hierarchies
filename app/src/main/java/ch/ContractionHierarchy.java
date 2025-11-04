package ch;

// TODO Part 4.3: Implement the preprocessing phase of Contraction Hierarchies
public class ContractionHierarchy {

    public ContractionHierarchy() {
      preprocess();
    }

    private void preprocess() {
        
    }

    /**
     * Run a bidirectional Dijkstra search, but only consider edges leading to nodes with a higher importance.
     */
    public Result<Integer> query(long s, long t) {
        
        // To be filled out

        return new Result<Integer>(0,0,0); 
    }

    // TODO Implement part 4.4 'storeGraph'
    // writes the augmented graph (original graph + all shortcuts added) that resulted from running the preprocessing phase.
    public void storeGraph() {
        // To be filled out
    }
}
