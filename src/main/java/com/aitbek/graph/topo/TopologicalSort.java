package com.aitbek.graph.topo;

import com.aitbek.data.Graph;

public interface TopologicalSort {
    TopoResult sort(Graph graph);

    class TopoResult {
        private final java.util.List<Integer> order;
        private final com.aitbek.metrics.OperationMetrics metrics;
        private final boolean hasCycle;

        public TopoResult(java.util.List<Integer> order, com.aitbek.metrics.OperationMetrics metrics, boolean hasCycle) {
            this.order = order;
            this.metrics = metrics;
            this.hasCycle = hasCycle;
        }

        public java.util.List<Integer> getOrder() { return order; }
        public com.aitbek.metrics.OperationMetrics getMetrics() { return metrics; }
        public boolean hasCycle() { return hasCycle; }

        @Override
        public String toString() {
            return String.format("TopoResult{order=%s, hasCycle=%s}", order, hasCycle);
        }
    }
}