package solvers;

import cse332.graph.GraphUtil;
import cse332.interfaces.BellmanFordSolver;
import main.Parser;

import java.util.List;
import java.util.Map;

public class OutSequential implements BellmanFordSolver {

    public List<Integer> solve(int[][] adjMatrix, int source) {
        List<Map<Integer, Integer>> g = Parser.parse(adjMatrix);
        int[] dist = new int[g.size()];
        int[] dist_copy = new int[g.size()];
        int[] pred = new int[g.size()];

        for (int i = 0; i < g.size(); i++) {
            dist[i] = GraphUtil.INF;
            pred[i] = -1;
        }

        dist[source] = 0;

        for (int i = 0; i < g.size(); i++) {
            for (int v = 0; v < g.size(); v++) {
                dist_copy[v] = dist[v];
            }
            for (int v = 0; v < g.size(); v++) { // parallelize
                for (int w : g.get(v).keySet()) { //stays sequential
                    int cost = g.get(v).get(w);
                    if ((dist_copy[v] + cost) < dist[w] && dist_copy[v] != GraphUtil.INF) {
                        dist[w] = dist_copy[v] + cost;
                        pred[w] = v;
                    }
                }
            }
        }

        return GraphUtil.getCycle(pred);
    }

}
