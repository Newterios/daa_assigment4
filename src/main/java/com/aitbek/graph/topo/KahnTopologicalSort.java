package com.aitbek.graph.topo;

import com.aitbek.data.Graph;
import com.aitbek.metrics.OperationMetrics;

import java.util.*;

public class KahnTopologicalSort implements TopologicalSort {

    @Override
    public TopoResult sort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("Topological sort requires a directed graph");
        }

        OperationMetrics metrics = new OperationMetrics();
        metrics.startTimer();

        int n = graph.getN();
        int[] inDegree = new int[n];
        List<Integer> order = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (Graph.Edge edge : graph.getAdjacentEdges(i)) {
                inDegree[edge.getV()]++;
                metrics.incrementCounter("edges_processed");
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementCounter("pushes");
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementCounter("pops");
            order.add(u);

            for (Graph.Edge edge : graph.getAdjacentEdges(u)) {
                int v = edge.getV();
                inDegree[v]--;
                metrics.incrementCounter("edges_processed");

                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementCounter("pushes");
                }
            }
        }

        metrics.stopTimer();

        boolean hasCycle = order.size() != n;
        if (hasCycle) {
            metrics.incrementCounter("cycle_detected");
        }

        metrics.setCounter("vertices_sorted", order.size());

        return new TopoResult(order, metrics, hasCycle);
    }

    public List<Integer> getTaskOrderFromComponents(List<List<Integer>> components, List<Integer> componentOrder) {
        List<Integer> taskOrder = new ArrayList<>();

        for (int compIndex : componentOrder) {
            List<Integer> component = components.get(compIndex);
            Collections.sort(component);
            taskOrder.addAll(component);
        }

        return taskOrder;
    }
}