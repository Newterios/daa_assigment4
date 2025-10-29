package com.aitbek.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GraphReader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public Graph readFromJson(String filePath) throws IOException {
        File file = new File(filePath);
        Map<String, Object> graphData = mapper.readValue(file, new TypeReference<Map<String, Object>>() {});

        validateGraphData(graphData);

        int n = (Integer) graphData.get("n");
        boolean directed = (Boolean) graphData.get("directed");
        String weightModel = (String) graphData.get("weight_model");

        Graph graph = new Graph(n, directed, weightModel);

        List<Map<String, Object>> edges = (List<Map<String, Object>>) graphData.get("edges");
        for (Map<String, Object> edge : edges) {
            int u = (Integer) edge.get("u");
            int v = (Integer) edge.get("v");
            int w = (Integer) edge.get("w");
            graph.addEdge(u, v, w);
        }

        return graph;
    }

    private void validateGraphData(Map<String, Object> graphData) {
        if (!graphData.containsKey("n")) {
            throw new IllegalArgumentException("Missing required field: n");
        }
        if (!graphData.containsKey("directed")) {
            throw new IllegalArgumentException("Missing required field: directed");
        }
        if (!graphData.containsKey("weight_model")) {
            throw new IllegalArgumentException("Missing required field: weight_model");
        }
        if (!graphData.containsKey("edges")) {
            throw new IllegalArgumentException("Missing required field: edges");
        }

        String weightModel = (String) graphData.get("weight_model");
        if (!"edge".equals(weightModel)) {
            throw new IllegalArgumentException("Only 'edge' weight model is supported");
        }
    }

    public static boolean isValidGraphFile(String filePath) {
        try {
            new GraphReader().readFromJson(filePath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}