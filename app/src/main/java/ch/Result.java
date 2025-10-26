package ch;

// represents a triple containing the duration (in ns), the number of relaxed edges, and the shortest path distance.
public class Result<T> {
    public long time; // duration (in ns) of computing shortest path distance between two vertices
    public T result; // the shortest path distance (-1 if no path exists)
    public int relaxed; // the number of relaxed edges after computing shortest path distance between two vertices

    public Result(long time, int relaxed, T result) {
        this.time = time;
        this.relaxed = relaxed;
        this.result = result;
    }
}