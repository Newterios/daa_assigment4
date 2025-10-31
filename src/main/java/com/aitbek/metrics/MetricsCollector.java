package com.aitbek.metrics;

import com.aitbek.common.Constants;
import com.aitbek.data.Graph;
import com.aitbek.data.GraphReader;
import com.aitbek.graph.dagsp.DAGPathProcessor;
import com.aitbek.graph.scc.TarjanSCC;
import com.aitbek.graph.topo.TopologyProcessor;
import com.aitbek.graph.topo.TopologicalSortResult;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MetricsCollector {
    private final List<String[]> records;
    private int runId;

    public MetricsCollector() {
        this.records = new ArrayList<>();
        this.runId = 1;
    }

    public void collectMetricsForDataset(String datasetId, String filePath, long seed) {
        int nodeCount = 0;
        int edgeCount = 0;

        try {
            GraphReader reader = new GraphReader();
            Graph graph = reader.readFromJson(filePath);

            nodeCount = graph.getN();
            edgeCount = graph.getEdges().size();

            collectSCCMetrics(datasetId, graph, nodeCount, edgeCount, seed);

            collectTopoMetrics(datasetId, graph, nodeCount, edgeCount, seed);

            collectDAGPathMetrics(datasetId, graph, nodeCount, edgeCount, seed);

            runId++;

        } catch (Exception e) {
            System.err.println("Error processing dataset " + datasetId + ": " + e.getMessage());
            addErrorRecord(datasetId, nodeCount, edgeCount, e.getMessage());
        }
    }

    private void collectSCCMetrics(String datasetId, Graph graph, int n, int m, long seed) {
        TarjanSCC tarjan = new TarjanSCC();
        long startTime = System.nanoTime();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);
        long endTime = System.nanoTime();

        OperationMetrics metrics = result.getMetrics();
        boolean isDAG = result.getComponentCount() == n;
        long totalTime = endTime - startTime;

        addRecord(datasetId, n, m, isDAG, Constants.ALGO_SCC, "dfs_calls",
                metrics.getCounter("dfs_calls"), totalTime, runId, seed, "");
        addRecord(datasetId, n, m, isDAG, Constants.ALGO_SCC, "edges_processed",
                metrics.getCounter("edges_processed"), totalTime, runId, seed, "");
        addRecord(datasetId, n, m, isDAG, Constants.ALGO_SCC, "components_found",
                result.getComponentCount(), totalTime, runId, seed, "");
    }

    private void collectTopoMetrics(String datasetId, Graph graph, int n, int m, long seed) {
        TopologyProcessor processor = new TopologyProcessor();
        long startTime = System.nanoTime();
        TopologicalSortResult result = processor.processGraph(graph);
        long endTime = System.nanoTime();

        OperationMetrics metrics = result.getTopoResult().getMetrics();
        boolean hasCycle = result.hasCycle();
        long totalTime = endTime - startTime;

        addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_TOPO, "pushes",
                metrics.getCounter("pushes"), totalTime, runId, seed, "");
        addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_TOPO, "pops",
                metrics.getCounter("pops"), totalTime, runId, seed, "");
        addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_TOPO, "edges_processed",
                metrics.getCounter("edges_processed"), totalTime, runId, seed, "");
        addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_TOPO, "vertices_sorted",
                metrics.getCounter("vertices_sorted"), totalTime, runId, seed, "");
    }

    private void collectDAGPathMetrics(String datasetId, Graph graph, int n, int m, long seed) {
        try {
            DAGPathProcessor processor = new DAGPathProcessor();
            long startTime = System.nanoTime();
            DAGPathProcessor.CompletePathResult result = processor.processGraph(graph, 0);
            long endTime = System.nanoTime();

            OperationMetrics shortestMetrics = result.getShortestPathResult().getMetrics();
            boolean hasCycle = result.getShortestPathResult().hasCycle();
            long totalTime = endTime - startTime;

            String notes = result.getTopoResult().getComponentOrder().size() == 1 ?
                    "single_component_original_graph" : "multiple_components_condensation_graph";

            addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_DAG_SP, "relaxations",
                    shortestMetrics.getCounter("relaxations"), totalTime, runId, seed, notes);
            addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_DAG_SP, "updates",
                    shortestMetrics.getCounter("updates"), totalTime, runId, seed, notes);
            addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_DAG_SP, "path_comparisons",
                    shortestMetrics.getCounter("path_comparisons"), totalTime, runId, seed, notes);

            OperationMetrics longestMetrics = result.getLongestPathResult().getMetrics();

            addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_DAG_LP, "relaxations",
                    longestMetrics.getCounter("relaxations"), totalTime, runId, seed, notes);
            addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_DAG_LP, "updates",
                    longestMetrics.getCounter("updates"), totalTime, runId, seed, notes);
            addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_DAG_LP, "path_comparisons",
                    longestMetrics.getCounter("path_comparisons"), totalTime, runId, seed, notes);
            addRecord(datasetId, n, m, !hasCycle, Constants.ALGO_DAG_LP, "critical_path_length",
                    result.getLongestPathResult().getMaxDistance(), totalTime, runId, seed, notes);

        } catch (Exception e) {
            addRecord(datasetId, n, m, false, Constants.ALGO_DAG_SP, "error", -1, -1, runId, seed,
                    "Processing failed: " + e.getMessage());
        }
    }
    private void addRecord(String datasetId, int n, int m, boolean isDAG, String algo,
                           String metricName, int metricValue, long timeNs, int runId, long seed, String notes) {
        String[] record = {
                datasetId,
                String.valueOf(n),
                String.valueOf(m),
                String.valueOf(isDAG),
                algo,
                metricName,
                String.valueOf(metricValue),
                String.valueOf(timeNs),
                String.valueOf(runId),
                String.valueOf(seed),
                notes
        };
        records.add(record);
    }

    private void addErrorRecord(String datasetId, int n, int m, String error) {
        String[] record = {
                datasetId,
                String.valueOf(n),
                String.valueOf(m),
                "false",
                "ERROR",
                "processing_failed",
                "-1",
                "-1",
                String.valueOf(runId),
                "-1",
                error
        };
        records.add(record);
    }

    public void writeToCsv(String filePath) throws IOException {
        Files.createDirectories(Paths.get(filePath).getParent());

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(String.join(",", Constants.CSV_HEADERS));
            writer.write("\n");

            for (String[] record : records) {
                writer.write(String.join(",", record));
                writer.write("\n");
            }
        }
    }

    public void runBenchmark() {
        System.out.println("Running benchmark on all datasets...");

        records.clear();
        runId = 1;

        for (int i = 1; i <= 3; i++) {
            String size = "small";
            long baseSeed = 1000 + i;
            if (i == 2) {
                size = "medium";
                baseSeed = 2000 + i;
            } else if (i == 3) {
                size = "large";
                baseSeed = 3000 + i;
            }

            for (int j = 1; j <= 3; j++) {
                String datasetId = size + "_" + j;
                String filePath = "data/graph_" + datasetId + ".json";
                long seed = baseSeed + j;

                System.out.println("Processing: " + datasetId);
                collectMetricsForDataset(datasetId, filePath, seed);
            }
        }

        try {
            writeToCsv("results/csv/summary.csv");
            System.out.println("Benchmark completed! Results saved to results/csv/summary.csv");
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}