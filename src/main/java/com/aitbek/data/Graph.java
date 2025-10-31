package com.aitbek.data;

import java.util.*;

public class Graph {
    private final int n;
    private final boolean directed;
    private final List<Edge> edges;
    private final List<List<Edge>> adjacencyList;
    private final String weightModel;

    public Graph(int n, boolean directed, String weightModel) {
        this.n = n;
        this.directed = directed;
        this.weightModel = weightModel;
        this.edges = new ArrayList<>();
        this.adjacencyList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        if (u < 0 || u >= n || v < 0 || v >= n) {
            throw new IllegalArgumentException("Invalid vertex index: " + u + " or " + v);
        }

        Edge edge = new Edge(u, v, weight);
        edges.add(edge);
        adjacencyList.get(u).add(edge);

        if (!directed) {
            Edge reverseEdge = new Edge(v, u, weight);
            adjacencyList.get(v).add(reverseEdge);
        }
    }

    public int getN() { return n; }
    public boolean isDirected() { return directed; }
    public String getWeightModel() { return weightModel; }
    public List<Edge> getEdges() { return Collections.unmodifiableList(edges); }
    public List<Edge> getAdjacentEdges(int vertex) {
        if (vertex < 0 || vertex >= n) {
            throw new IllegalArgumentException("Invalid vertex index: " + vertex);
        }
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public static class Edge {
        private final int u;
        private final int v;
        private final int weight;

        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        public int getU() { return u; }
        public int getV() { return v; }
        public int getWeight() { return weight; }

        @Override
        public String toString() {
            return String.format("(%d -> %d, w=%d)", u, v, weight);
        }
    }

    @Override
    public String toString() {
        return String.format("Graph{n=%d, directed=%s, edges=%d}", n, directed, edges.size());
    }
}