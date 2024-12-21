package solvers;

import cse332.graph.GraphUtil;
import cse332.interfaces.BellmanFordSolver;
import main.Parser;
import paralleltasks.ArrayCopyTask;
import paralleltasks.RelaxOutTaskBad;

import java.util.List;
import java.util.Map;

public class OutParallelBad implements BellmanFordSolver {

    public List<Integer> solve(int[][] adjMatrix, int source) {
        List<Map<Integer, Integer>> g = Parser.parse(adjMatrix);

        int[] dist = new int[g.size()];
        int[] pred = new int[g.size()];

        for (int i = 0; i < g.size(); i++) {
            dist[i] = GraphUtil.INF;
            pred[i] = -1;
        }

        dist[source] = 0;

        for (int i = 0; i < g.size(); i++) {
            int[] dist_copy = ArrayCopyTask.copy(dist);
            RelaxOutTaskBad.parallel(g, dist, dist_copy, pred);
        }

        return GraphUtil.getCycle(pred);
    }

}
