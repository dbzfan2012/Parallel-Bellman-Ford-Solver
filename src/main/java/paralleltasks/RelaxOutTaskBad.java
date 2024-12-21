package paralleltasks;

import cse332.graph.GraphUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class RelaxOutTaskBad extends RecursiveAction {

    public static final ForkJoinPool pool = new ForkJoinPool();
    public static final int CUTOFF = 1;

    private final int[] pred, dist, dist_copy;
    private final int lo, hi;
    private final List<Map<Integer, Integer>> adjList;

    public RelaxOutTaskBad(List<Map<Integer, Integer>> adjList, int[] dist, int[] dist_copy, int[] pred, int lo, int hi) {
        this.adjList = adjList;
        this.dist = dist;
        this.dist_copy = dist_copy;
        this.pred = pred;
        this.lo = lo;
        this.hi = hi;
    }

    protected void compute() {
        if (hi - lo <= CUTOFF) {
            sequential(dist, dist_copy, pred, adjList, lo, hi);
        }
        else {
            int mid = lo + (hi - lo) / 2;

            RelaxOutTaskBad left = new RelaxOutTaskBad(adjList, dist, dist_copy, pred, lo, mid);
            RelaxOutTaskBad right = new RelaxOutTaskBad(adjList, dist, dist_copy, pred, mid, hi);

            left.fork();
            right.compute();
            left.join();
        }
    }

    public static void sequential(int[] dist, int[] dist_copy, int[] pred, List<Map<Integer, Integer>> adjList, int lo, int hi) {
        for (int v = lo; v < hi; v++) {
            for (int w : adjList.get(v).keySet()) {
                int cost = adjList.get(v).get(w);
                if ((dist_copy[v] + cost) < dist[w] && dist_copy[v] != GraphUtil.INF) {
                    dist[w] = dist_copy[v] + cost;
                    pred[w] = v;
                }
            }
        }
    }

    public static void parallel(List<Map<Integer, Integer>> adjList, int[] dist, int[] dist_copy, int[] pred) {
        pool.invoke(new RelaxOutTaskBad(adjList, dist, dist_copy, pred, 0, adjList.size()));
    }

}
