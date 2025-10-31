package com.aitbek.graph.dagsp;

import com.aitbek.data.Graph;
import com.aitbek.graph.topo.KahnTopologicalSort;
import com.aitbek.metrics.OperationMetrics;

import java.util.*;

public class DAGLongestPath {

    public LongestPathResult findLongestPaths(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("DAG longest path requires a directed graph");
        }

        OperationMetrics metrics = new OperationMetrics();
        metrics.startTimer();

        int n = graph.getN();

        if (n == 0) {
            metrics.stopTimer();
            return new LongestPathResult(new int[0], new int[0], -1, -1, 0, metrics, false);
        }

        int[] dist = new int[n];
        int[] prev = new int[n];
        int startNode = -1, endNode = -1;

        KahnTopologicalSort topo = new KahnTopologicalSort();
        KahnTopologicalSort.TopoResult topoResult = topo.sort(graph);

        if (topoResult.hasCycle()) {
            metrics.stopTimer();
            metrics.incrementCounter("cycle_detected");
            return new LongestPathResult(dist, prev, -1, -1, 0, metrics, true);
        }

        List<Integer> order = topoResult.getOrder();
        metrics.setCounter("topo_vertices", order.size());

        Arrays.fill(dist, 0);
        Arrays.fill(prev, -1);


        int maxDistance = 0;
        endNode = order.isEmpty() ? -1 : order.get(0);

        for (int u : order) {
            for (Graph.Edge edge : graph.getAdjacentEdges(u)) {
                int v = edge.getV();
                int weight = edge.getWeight();
                metrics.incrementCounter("relaxations");

                if (dist[u] + weight > dist[v]) {
                    dist[v] = dist[u] + weight;
                    prev[v] = u;
                    metrics.incrementCounter("updates");

                    if (dist[v] > maxDistance) {
                        maxDistance = dist[v];
                        endNode = v;
                    }
                }
                metrics.incrementCounter("path_comparisons");
            }
        }

        if (endNode != -1) {
            startNode = endNode;
            while (prev[startNode] != -1) {
                startNode = prev[startNode];
            }
        } else if (!order.isEmpty()) {
            startNode = order.get(0);
            endNode = startNode;
        }

        metrics.stopTimer();
        return new LongestPathResult(dist, prev, startNode, endNode, maxDistance, metrics, false);
    }

    private int[] calculateInDegree(Graph graph) {
        int n = graph.getN();
        int[] inDegree = new int[n];
        for (int i = 0; i < n; i++) {
            for (Graph.Edge edge : graph.getAdjacentEdges(i)) {
                inDegree[edge.getV()]++;
            }
        }
        return inDegree;
    }

    public static class LongestPathResult {
        private final int[] distances;
        private final int[] predecessors;
        private final int startNode;
        private final int endNode;
        private final int maxDistance;
        private final OperationMetrics metrics;
        private final boolean hasCycle;

        public LongestPathResult(int[] distances, int[] predecessors, int startNode,
                                 int endNode, int maxDistance, OperationMetrics metrics,
                                 boolean hasCycle) {
            this.distances = distances.clone();
            this.predecessors = predecessors.clone();
            this.startNode = startNode;
            this.endNode = endNode;
            this.maxDistance = maxDistance;
            this.metrics = metrics;
            this.hasCycle = hasCycle;
        }

        public int[] getDistances() { return distances.clone(); }
        public int[] getPredecessors() { return predecessors.clone(); }
        public int getStartNode() { return startNode; }
        public int getEndNode() { return endNode; }
        public int getMaxDistance() { return maxDistance; }
        public OperationMetrics getMetrics() { return metrics; }
        public boolean hasCycle() { return hasCycle; }

        public List<Integer> getCriticalPath() {
            if (hasCycle) {
                throw new IllegalStateException("Cannot get critical path in cyclic graph");
            }
            if (startNode == -1 || endNode == -1) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<>();
            for (int v = endNode; v != -1; v = predecessors[v]) {
                path.add(v);
            }
            Collections.reverse(path);
            return path;
        }
        // Nugmanov Aitbek's
        @Override
        public String toString() {
            return String.format(
                    "LongestPathResult{criticalPathLength=%d, hasCycle=%s}",
                    maxDistance, hasCycle
            );
        }
    }
}