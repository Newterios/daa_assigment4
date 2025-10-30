package com.aitbek.graph.scc;

import com.aitbek.data.Graph;
import com.aitbek.metrics.OperationMetrics;

import java.util.*;

public class TarjanSCC {
    private int index;
    private int[] disc;
    private int[] low;
    private boolean[] onStack;
    private Deque<Integer> stack;
    private List<List<Integer>> sccList;
    private Graph graph;
    private OperationMetrics metrics;

    public TarjanSCC() {
        this.metrics = new OperationMetrics();
    }

    public SCCResult findSCC(Graph graph) {
        this.graph = graph;
        int n = graph.getN();
        initialize(n);

        metrics.startTimer();

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTimer();

        return new SCCResult(sccList, metrics);
    }

    private void initialize(int n) {
        index = 0;
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new ArrayDeque<>();
        sccList = new ArrayList<>();
        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);
        metrics.reset();
    }

    private void dfs(int u) {
        disc[u] = low[u] = index++;
        stack.push(u);
        onStack[u] = true;
        metrics.incrementCounter("dfs_calls");

        for (Graph.Edge edge : graph.getAdjacentEdges(u)) {
            int v = edge.getV();
            metrics.incrementCounter("edges_processed");

            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != u);
            sccList.add(component);
            metrics.incrementCounter("components_found");
        }
    }

    public static class SCCResult {
        private final List<List<Integer>> components;
        private final OperationMetrics metrics;

        public SCCResult(List<List<Integer>> components, OperationMetrics metrics) {
            this.components = components;
            this.metrics = metrics;
        }

        public List<List<Integer>> getComponents() {
            return Collections.unmodifiableList(components);
        }

        public OperationMetrics getMetrics() {
            return metrics;
        }

        public int getComponentCount() {
            return components.size();
        }

        public List<Integer> getComponentSizes() {
            List<Integer> sizes = new ArrayList<>();
            for (List<Integer> comp : components) {
                sizes.add(comp.size());
            }
            return sizes;
        }

        @Override
        public String toString() {
            return String.format("SCCResult{components=%d, sizes=%s}",
                    getComponentCount(), getComponentSizes());
        }
    }
}