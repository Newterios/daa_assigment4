package com.aitbek.graph.topo;

import java.util.List;

public class TopologicalSortResult {
    private final List<Integer> componentOrder;
    private final List<Integer> taskOrder;
    private final KahnTopologicalSort.TopoResult topoResult;

    public TopologicalSortResult(List<Integer> componentOrder,
                                 List<Integer> taskOrder,
                                 KahnTopologicalSort.TopoResult topoResult) {
        this.componentOrder = componentOrder;
        this.taskOrder = taskOrder;
        this.topoResult = topoResult;
    }

    public List<Integer> getComponentOrder() { return componentOrder; }
    public List<Integer> getTaskOrder() { return taskOrder; }
    public KahnTopologicalSort.TopoResult getTopoResult() { return topoResult; }

    public boolean hasCycle() {
        return topoResult != null && topoResult.hasCycle();
    }

    @Override
    public String toString() {
        return String.format(
                "TopologicalSortResult{componentOrder=%s, taskOrder=%s, hasCycle=%s}",
                componentOrder, taskOrder, hasCycle()
        );
    }
}