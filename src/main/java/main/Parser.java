package main;

import cse332.graph.GraphUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    /**
     * Parse an adjacency matrix into an adjacency list.
     * @param adjMatrix Adjacency matrix
     * @return Adjacency list of maps from node to weight
     */
    public static List<Map<Integer, Integer>> parse(int[][] adjMatrix) {
        List<Map<Integer, Integer>> adjList = new ArrayList<>();
        for (int v = 0; v < adjMatrix.length; v++) {
            Map<Integer, Integer> map = new HashMap<>();
            for (int w = 0; w < adjMatrix[0].length; w++) {
                if (adjMatrix[v][w] != GraphUtil.INF) {
                    map.put(w, adjMatrix[v][w]);
                }
            }
            adjList.add(v, map);
        }
        return adjList;
    }

    /**
     * Parse an adjacency matrix into an adjacency list with incoming edges instead of outgoing edges.
     * @param adjMatrix Adjacency matrix
     * @return Adjacency list of maps from node to weight with incoming edges
     */
    public static List<Map<Integer, Integer>> parseInverse(int[][] adjMatrix) {
        List<Map<Integer, Integer>> adjList = new ArrayList<>();
        for (int v = 0; v < adjMatrix.length; v++) {
            Map<Integer, Integer> map = new HashMap<>();
            for (int w = 0; w < adjMatrix[0].length; w++) {
                if (adjMatrix[w][v] != GraphUtil.INF) {
                    map.put(w, adjMatrix[w][v]);
                }
            }
            adjList.add(v, map);
        }
        return adjList;
    }

}
