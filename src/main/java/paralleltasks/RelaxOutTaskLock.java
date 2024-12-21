package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.graph.GraphUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.ReentrantLock;

public class RelaxOutTaskLock extends RecursiveAction {

    public static final ForkJoinPool pool = new ForkJoinPool();
    public static final int CUTOFF = 1;
    private final int[] pred, dist, dist_copy;
    private final int lo, hi;
    private final List<Map<Integer, Integer>> adjList;
    private final ReentrantLock[] locks;

    public RelaxOutTaskLock(List<Map<Integer, Integer>> adjList, int[] dist, int[] dist_copy, int[] pred, int lo, int hi, ReentrantLock[] locks) {
        this.adjList = adjList;
        this.dist = dist;
        this.dist_copy = dist_copy;
        this.pred = pred;
        this.lo = lo;
        this.hi = hi;
        this.locks = locks;
    }

    protected void compute() {
        if (hi - lo <= CUTOFF) {
            sequential(dist, dist_copy, pred, adjList, lo, hi, locks);
        }
        else {
            int mid = lo + (hi - lo) / 2;

            RelaxOutTaskLock left = new RelaxOutTaskLock(adjList, dist, dist_copy, pred, lo, mid, locks);
            RelaxOutTaskLock right = new RelaxOutTaskLock(adjList, dist, dist_copy, pred, mid, hi, locks);

            left.fork();
            right.compute();
            left.join();
        }
    }

    public static void sequential(int[] dist, int[] dist_copy, int[] pred, List<Map<Integer, Integer>> adjList, int lo, int hi, ReentrantLock[] locks) {
        for (int v = lo; v < hi; v++) {
            for (int w : adjList.get(v).keySet()) {
                try {
                    locks[w].lock();
                    int cost = adjList.get(v).get(w);
                    if ((dist_copy[v] + cost) < dist[w] && dist_copy[v] != GraphUtil.INF) {
                        dist[w] = dist_copy[v] + cost;
                        pred[w] = v;
                    }
                }
                finally {
                    locks[w].unlock();
                }
            }
        }
    }

    public static void parallel(List<Map<Integer, Integer>> adjList, int[] dist, int[] dist_copy, int[] pred, ReentrantLock[] locks) {
        pool.invoke(new RelaxOutTaskLock(adjList, dist, dist_copy, pred, 0, adjList.size(), locks));
    }
}
