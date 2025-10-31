package com.aitbek.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphWriter {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void writeToJson(Graph graph, String filePath) throws IOException {
        Map<String, Object> graphData = new HashMap<>();

        graphData.put("n", graph.getN());
        graphData.put("directed", graph.isDirected());
        graphData.put("weight_model", graph.getWeightModel());
        graphData.put("source", 0);

        List<Map<String, Object>> edges = new ArrayList<>();
        for (Graph.Edge edge : graph.getEdges()) {
            Map<String, Object> edgeData = new HashMap<>();
            edgeData.put("u", edge.getU());
            edgeData.put("v", edge.getV());
            edgeData.put("w", edge.getWeight());
            edges.add(edgeData);
        }
        graphData.put("edges", edges);

        mapper.writeValue(new File(filePath), graphData);
    }
}