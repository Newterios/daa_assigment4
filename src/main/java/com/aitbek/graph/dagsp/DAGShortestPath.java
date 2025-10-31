package com.aitbek.graph.dagsp;

import com.aitbek.data.Graph;
import com.aitbek.graph.topo.KahnTopologicalSort;
import com.aitbek.metrics.OperationMetrics;

import java.util.*;

public class DAGShortestPath {

    public ShortestPathResult findShortestPaths(Graph graph, int source) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("DAG shortest path requires a directed graph");
        }

        OperationMetrics metrics = new OperationMetrics();
        metrics.startTimer();

        int n = graph.getN();

        if (n == 0) {
            metrics.stopTimer();
            return new ShortestPathResult(new int[0], new int[0], source, metrics, false);
        }

        int[] dist = new int[n];
        int[] prev = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        if (source < 0 || source >= n) {
            metrics.stopTimer();
            return new ShortestPathResult(dist, prev, source, metrics, false);
        }

        dist[source] = 0;

        KahnTopologicalSort topo = new KahnTopologicalSort();
        KahnTopologicalSort.TopoResult topoResult = topo.sort(graph);

        if (topoResult.hasCycle()) {
            metrics.stopTimer();
            metrics.incrementCounter("cycle_detected");
            return new ShortestPathResult(dist, prev, source, metrics, true);
        }

        List<Integer> order = topoResult.getOrder();
        metrics.setCounter("topo_vertices", order.size());

        for (int u : order) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getAdjacentEdges(u)) {
                    int v = edge.getV();
                    int weight = edge.getWeight();
                    metrics.incrementCounter("relaxations");

                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        prev[v] = u;
                        metrics.incrementCounter("updates");
                    }
                    metrics.incrementCounter("path_comparisons");
                }
            }
        }

        metrics.stopTimer();
        return new ShortestPathResult(dist, prev, source, metrics, false);
    }

    public static class ShortestPathResult {
        private final int[] distances;
        private final int[] predecessors;
        private final int source;
        private final OperationMetrics metrics;
        private final boolean hasCycle;

        public ShortestPathResult(int[] distances, int[] predecessors, int source,
                                  OperationMetrics metrics, boolean hasCycle) {
            this.distances = distances.clone();
            this.predecessors = predecessors.clone();
            this.source = source;
            this.metrics = metrics;
            this.hasCycle = hasCycle;
        }

        public int[] getDistances() { return distances.clone(); }
        public int[] getPredecessors() { return predecessors.clone(); }
        public int getSource() { return source; }
        public OperationMetrics getMetrics() { return metrics; }
        public boolean hasCycle() { return hasCycle; }

        public List<Integer> getPathTo(int target) {
            if (hasCycle) {
                throw new IllegalStateException("Cannot get path in cyclic graph");
            }
            if (target < 0 || target >= distances.length || distances[target] == Integer.MAX_VALUE) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<>();
            for (int v = target; v != -1; v = predecessors[v]) {
                path.add(v);
            }
            Collections.reverse(path);
            return path;
        }

        public int getDistanceTo(int target) {
            if (target < 0 || target >= distances.length) {
                return Integer.MAX_VALUE;
            }
            return distances[target];
        }

        @Override
        public String toString() {
            return String.format("ShortestPathResult{source=%d, hasCycle=%s}", source, hasCycle);
        }
    }
}