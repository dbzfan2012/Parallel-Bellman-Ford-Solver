package paralleltasks;

import cse332.graph.GraphUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class RelaxInTask extends RecursiveAction {

    public static final ForkJoinPool pool = new ForkJoinPool();
    public static final int CUTOFF = 1;

    private final int[] pred, dist, dist_copy;
    private final int lo, hi;
    private final List<Map<Integer, Integer>> adjList;

    public RelaxInTask(List<Map<Integer, Integer>> adjList, int[] dist, int[] dist_copy, int[] pred, int lo, int hi) {
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

            RelaxInTask left = new RelaxInTask(adjList, dist, dist_copy, pred, lo, mid);
            RelaxInTask right = new RelaxInTask(adjList, dist, dist_copy, pred, mid, hi);

            left.fork();
            right.compute();
            left.join();
        }
    }

    public static void sequential(int[] dist, int[] dist_copy, int[] pred, List<Map<Integer, Integer>> adjList, int lo, int hi) {
        for (int w = lo; w < hi; w++) {
            for (int v : adjList.get(w).keySet()) {
                int cost = adjList.get(w).get(v);
                if ((dist_copy[v] + cost) < dist[w] && dist_copy[v] != GraphUtil.INF) {
                    dist[w] = dist_copy[v] + cost;
                    pred[w] = v;
                }
            }
        }

    }

    public static void parallel(List<Map<Integer, Integer>> adjList, int[] dist, int[] dist_copy, int[] pred) {
        pool.invoke(new RelaxInTask(adjList, dist, dist_copy, pred, 0, adjList.size()));
    }

}
