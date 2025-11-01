package com.aitbek;

import com.aitbek.data.Graph;
import com.aitbek.data.GraphGenerator;
import com.aitbek.data.GraphReader;
import com.aitbek.graph.dagsp.DAGPathProcessor;
import com.aitbek.graph.scc.TarjanSCC;
import com.aitbek.graph.topo.TopologyProcessor;
import com.aitbek.graph.topo.TopologicalSortResult;
import com.aitbek.metrics.MetricsCollector;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GraphGenerator generator = new GraphGenerator();
    private static final MetricsCollector metricsCollector = new MetricsCollector();

    public static void main(String[] args) {
        if (args.length > 0) {
            processCommandLineArgs(args);
        } else {
            showInteractiveMenu();
        }
    }

    private static void showInteractiveMenu() {
        while (true) {
            System.out.println("\n=== Smart City Scheduling Analyzer ===");
            System.out.println("1. Generate datasets");
            System.out.println("2. Run SCC analysis");
            System.out.println("3. Run Topological Sort");
            System.out.println("4. Run DAG Shortest/Longest Path");
            System.out.println("5. Run Benchmark (all algorithms)");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    generateDatasets();
                    break;
                case "2":
                    runSCCAnalysis();
                    break;
                case "3":
                    runTopologicalSort();
                    break;
                case "4":
                    runDAGPaths();
                    break;
                case "5":
                    runBenchmark();
                    break;
                case "6":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void processCommandLineArgs(String[] args) {
        switch (args[0]) {
            case "generate":
                generator.generateAllDatasets();
                break;
            case "scc":
                if (args.length > 1) {
                    runSCCAnalysis(args[1]);
                } else {
                    System.err.println("Usage: scc <graph_file.json>");
                }
                break;
            case "topo":
                if (args.length > 1) {
                    runTopologicalSort(args[1]);
                } else {
                    System.err.println("Usage: topo <graph_file.json>");
                }
                break;
            case "dagsp":
                if (args.length > 1) {
                    runDAGPaths(args[1]);
                } else {
                    System.err.println("Usage: dagsp <graph_file.json>");
                }
                break;
            case "bench":
                metricsCollector.runBenchmark();
                break;
            default:
                System.err.println("Unknown command: " + args[0]);
                System.out.println("Available commands: generate, scc, topo, dagsp, bench");
        }
    }

    private static void generateDatasets() {
        System.out.println("Generating 9 datasets...");
        generator.generateAllDatasets();
    }

    private static void runSCCAnalysis() {
        System.out.print("Enter graph file path: ");
        String filePath = scanner.nextLine().trim();
        runSCCAnalysis(filePath);
    }

    private static void runSCCAnalysis(String filePath) {
        try {
            GraphReader reader = new GraphReader();
            Graph graph = reader.readFromJson(filePath);

            TarjanSCC tarjan = new TarjanSCC();
            var result = tarjan.findSCC(graph);

            System.out.println("\n=== SCC Analysis Results ===");
            System.out.println("Graph: " + graph);
            System.out.println("Components found: " + result.getComponentCount());
            System.out.println("Component sizes: " + result.getComponentSizes());

            for (int i = 0; i < result.getComponents().size(); i++) {
                System.out.println("Component " + i + ": " + result.getComponents().get(i));
            }

            System.out.println("Metrics: " + result.getMetrics());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void runTopologicalSort() {
        System.out.print("Enter graph file path: ");
        String filePath = scanner.nextLine().trim();
        runTopologicalSort(filePath);
    }

    private static void runTopologicalSort(String filePath) {
        try {
            GraphReader reader = new GraphReader();
            Graph graph = reader.readFromJson(filePath);

            TopologyProcessor processor = new TopologyProcessor();
            TopologicalSortResult result = processor.processGraph(graph);

            System.out.println("\n=== Topological Sort Results ===");
            System.out.println("Graph: " + graph);

            if (result.hasCycle()) {
                System.out.println("Warning: Graph contains cycles! Topological order may be incomplete.");
            }

            System.out.println("Component order: " + result.getComponentOrder());
            System.out.println("Task order: " + result.getTaskOrder());
            System.out.println("Metrics: " + result.getTopoResult().getMetrics());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void runDAGPaths() {
        System.out.print("Enter graph file path: ");
        String filePath = scanner.nextLine().trim();
        runDAGPaths(filePath);
    }

    private static void runDAGPaths(String filePath) {
        try {
            GraphReader reader = new GraphReader();
            Graph graph = reader.readFromJson(filePath);

            System.out.print("Enter source vertex (default 0): ");
            String sourceInput = scanner.nextLine().trim();
            int source = sourceInput.isEmpty() ? 0 : Integer.parseInt(sourceInput);

            DAGPathProcessor processor = new DAGPathProcessor();
            var result = processor.processGraph(graph, source);

            System.out.println("\n=== DAG Path Analysis Results ===");
            System.out.println("Graph: " + graph);
            System.out.println("Source vertex: " + source);

            var shortestResult = result.getShortestPathResult();
            if (shortestResult.hasCycle()) {
                System.out.println("Warning: Graph contains cycles! Shortest paths may be incorrect.");
            } else {
                System.out.println("\nShortest paths from vertex " + source + ":");
                for (int i = 0; i < graph.getN(); i++) {
                    int distance = shortestResult.getDistanceTo(i);
                    if (distance == Integer.MAX_VALUE) {
                        System.out.println("  to " + i + ": UNREACHABLE");
                    } else {
                        System.out.println("  to " + i + ": " + distance + " (path: " +
                                shortestResult.getPathTo(i) + ")");
                    }
                }
            }

            var longestResult = result.getLongestPathResult();
            if (longestResult.hasCycle()) {
                System.out.println("Warning: Graph contains cycles! Critical path may be incorrect.");
            } else {
                System.out.println("\nCritical Path (Longest):");
                System.out.println("  Length: " + longestResult.getMaxDistance());
                System.out.println("  Path: " + longestResult.getCriticalPath());
            }

            System.out.println("\nShortest Path Metrics: " + shortestResult.getMetrics());
            System.out.println("Longest Path Metrics: " + longestResult.getMetrics());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void runBenchmark() {
        System.out.println("Running benchmark on all 9 datasets...");
        metricsCollector.runBenchmark();
    }
}