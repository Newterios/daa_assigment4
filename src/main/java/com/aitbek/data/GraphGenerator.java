package com.aitbek.data;

import com.aitbek.common.Constants;
import com.aitbek.common.Utils;

import java.util.*;

public class GraphGenerator {
    private final Random random;

    public GraphGenerator() {
        this.random = new Random();
    }

    public GraphGenerator(long seed) {
        this.random = new Random(seed);
    }

    public Graph generateGraph(int nodes, double density, boolean forceCycles, long seed) {
        this.random.setSeed(seed);
        Graph graph = new Graph(nodes, true, "edge");

        int maxEdges = nodes * (nodes - 1);
        int targetEdges = (int) (maxEdges * density);

        Set<String> existingEdges = new HashSet<>();

        addMinimumEdges(graph, existingEdges);

        while (graph.getEdges().size() < targetEdges) {
            int u = random.nextInt(nodes);
            int v = random.nextInt(nodes);

            if (u == v) continue;

            String edgeKey = u + "->" + v;
            if (!existingEdges.contains(edgeKey)) {
                int weight = Utils.getRandomInRange(random, Constants.MIN_WEIGHT, Constants.MAX_WEIGHT);
                graph.addEdge(u, v, weight);
                existingEdges.add(edgeKey);
            }
        }

        if (forceCycles && graph.getEdges().size() >= 2) {
            addCycles(graph, existingEdges);
        }

        return graph;
    }

    private void addMinimumEdges(Graph graph, Set<String> existingEdges) {
        int nodes = graph.getN();
        if (nodes <= 1) return;

        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < nodes; i++) {
            vertices.add(i);
        }
        Collections.shuffle(vertices, random);

        for (int i = 1; i < vertices.size(); i++) {
            int u = vertices.get(i-1);
            int v = vertices.get(i);
            int weight = Utils.getRandomInRange(random, Constants.MIN_WEIGHT, Constants.MAX_WEIGHT);

            graph.addEdge(u, v, weight);
            existingEdges.add(u + "->" + v);
        }
    }

    private void addCycles(Graph graph, Set<String> existingEdges) {
        int nodes = graph.getN();
        if (nodes < 3) return;

        int cyclesToAdd = Math.min(3, nodes / 2);

        for (int i = 0; i < cyclesToAdd; i++) {
            List<Integer> cycleVertices = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                int vertex = random.nextInt(nodes);
                while (cycleVertices.contains(vertex)) {
                    vertex = random.nextInt(nodes);
                }
                cycleVertices.add(vertex);
            }

            for (int j = 0; j < cycleVertices.size(); j++) {
                int u = cycleVertices.get(j);
                int v = cycleVertices.get((j + 1) % cycleVertices.size());

                String edgeKey = u + "->" + v;
                if (!existingEdges.contains(edgeKey)) {
                    int weight = Utils.getRandomInRange(random, Constants.MIN_WEIGHT, Constants.MAX_WEIGHT);
                    graph.addEdge(u, v, weight);
                    existingEdges.add(edgeKey);
                }
            }
        }
    }

    public void generateAllDatasets() {
        System.out.println("Generating 9 datasets...");

        generateDataset("small_1", Constants.SMALL_MIN_NODES, Constants.SMALL_MAX_NODES,
                Constants.SPARSE_DENSITY, true, 1001);
        generateDataset("small_2", Constants.SMALL_MIN_NODES, Constants.SMALL_MAX_NODES,
                Constants.MEDIUM_DENSITY, false, 1002);
        generateDataset("small_3", Constants.SMALL_MIN_NODES, Constants.SMALL_MAX_NODES,
                Constants.DENSE_DENSITY, true, 1003);

        generateDataset("medium_1", Constants.MEDIUM_MIN_NODES, Constants.MEDIUM_MAX_NODES,
                Constants.SPARSE_DENSITY, true, 2001);
        generateDataset("medium_2", Constants.MEDIUM_MIN_NODES, Constants.MEDIUM_MAX_NODES,
                Constants.MEDIUM_DENSITY, true, 2002);
        generateDataset("medium_3", Constants.MEDIUM_MIN_NODES, Constants.MEDIUM_MAX_NODES,
                Constants.DENSE_DENSITY, false, 2003);

        generateDataset("large_1", Constants.LARGE_MIN_NODES, Constants.LARGE_MAX_NODES,
                Constants.SPARSE_DENSITY, true, 3001);
        generateDataset("large_2", Constants.LARGE_MIN_NODES, Constants.LARGE_MAX_NODES,
                Constants.MEDIUM_DENSITY, true, 3002);
        generateDataset("large_3", Constants.LARGE_MIN_NODES, Constants.LARGE_MAX_NODES,
                Constants.DENSE_DENSITY, false, 3003);

        System.out.println("All datasets generated successfully!");
    }

    private void generateDataset(String name, int minNodes, int maxNodes,
                                 double density, boolean forceCycles, long seed) {
        try {
            int nodes = Utils.getRandomInRange(random, minNodes, maxNodes);
            Graph graph = generateGraph(nodes, density, forceCycles, seed);

            GraphWriter writer = new GraphWriter();
            String filePath = "data/graph_" + name + ".json";
            writer.writeToJson(graph, filePath);

            System.out.printf("Generated %s: %d nodes, %d edges, density=%.2f%n",
                    name, nodes, graph.getEdges().size(), density);
        } catch (Exception e) {
            System.err.println("Error generating dataset " + name + ": " + e.getMessage());
        }
    }
}