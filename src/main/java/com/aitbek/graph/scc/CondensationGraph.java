package com.aitbek.graph.scc;

import com.aitbek.data.Graph;

import java.util.*;

public class CondensationGraph {
    private final Graph originalGraph;
    private final List<List<Integer>> components;
    private final Map<Integer, Integer> vertexToComponent;
    private final Graph condensationGraph;

    public CondensationGraph(Graph originalGraph, List<List<Integer>> components) {
        this.originalGraph = originalGraph;
        this.components = components;
        this.vertexToComponent = new HashMap<>();
        this.condensationGraph = buildCondensationGraph();
    }

    private Graph buildCondensationGraph() {
        for (int i = 0; i < components.size(); i++) {
            for (int vertex : components.get(i)) {
                vertexToComponent.put(vertex, i);
            }
        }

        Graph condensed = new Graph(components.size(), true, originalGraph.getWeightModel());

        Set<String> edgesAdded = new HashSet<>();
        for (List<Integer> component : components) {
            for (int vertex : component) {
                for (Graph.Edge edge : originalGraph.getAdjacentEdges(vertex)) {
                    int v = edge.getV();
                    int compU = vertexToComponent.get(vertex);
                    int compV = vertexToComponent.get(v);

                    if (compU != compV) {
                        String edgeKey = compU + "->" + compV;
                        if (!edgesAdded.contains(edgeKey)) {
                            edgesAdded.add(edgeKey);
                            condensed.addEdge(compU, compV, edge.getWeight());
                        }
                    }
                }
            }
        }

        return condensed;
    }

    public Graph getCondensationGraph() {
        return condensationGraph;
    }

    public Map<Integer, Integer> getVertexToComponent() {
        return Collections.unmodifiableMap(vertexToComponent);
    }

    public List<List<Integer>> getComponents() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public String toString() {
        return String.format("CondensationGraph{originalVertices=%d, components=%d, condensedVertices=%d}",
                originalGraph.getN(), components.size(), condensationGraph.getN());
    }
}