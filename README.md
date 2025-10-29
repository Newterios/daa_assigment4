# Smart City / Smart Campus Scheduling — Assignment 4

## Project overview
This repository implements graph algorithms and tooling required for Assignment 4:
- Tarjan’s algorithm for Strongly Connected Components (SCC).
- Condensation graph construction (SCC compression).
- Topological ordering using Kahn’s algorithm.
- Shortest and longest path algorithms on DAGs (topological relaxation / DP).
- Dataset generator and JSON input/output utilities.
- Benchmark instrumentation (operation counters and timing) and CSV export.
- Unit tests using JUnit.

## Repository layout
```
.
├── data/                              # Input datasets (graph_small_*, graph_medium_*, graph_large_*)
│   ├── graph_small_1.json
│   ├── graph_medium_2.json
│   └── graph_large_3.json
│
├── results/                           # Benchmark outputs and experiment results
│   └── csv/
│       └── summary.csv
│
├── src/
│   ├── main/java/com/aitbek/
│   │   ├── common/                    # Shared utilities and constants
│   │   │   ├── Constants.java
│   │   │   └── Utils.java
│   │   │
│   │   ├── data/                      # Graph data structures and I/O
│   │   │   ├── Graph.java
│   │   │   ├── GraphGenerator.java
│   │   │   ├── GraphReader.java
│   │   │   └── GraphWriter.java
│   │   │
│   │   ├── graph/                     # Algorithmic modules
│   │   │   ├── scc/                   # Strongly Connected Components
│   │   │   │   ├── TarjanSCC.java
│   │   │   │   └── CondensationGraph.java
│   │   │   │
│   │   │   ├── topo/                  # Topological sorting algorithms
│   │   │   │   ├── KahnTopologicalSort.java
│   │   │   │   ├── TopologicalSort.java
│   │   │   │   └── TopologyProcessor.java
│   │   │   │
│   │   │   └── dagsp/                 # Shortest/Longest path in DAGs
│   │   │       ├── DAGShortestPath.java
│   │   │       ├── DAGLongestPath.java
│   │   │       └── DAGPathProcessor.java
│   │   │
│   │   └── metrics/                   #  Performance measurement utilities
│   │       ├── MetricsCollector.java
│   │       └── OperationMetrics.java
│   │
│   └── test/java/com/aitbek/          # JUnit test suites
│       ├── data/                      # Graph data tests
│       ├── graph/                     # Algorithm validation
│       └── metrics/                   # Metrics correctness tests
│
├── pom.xml                            #  Maven build configuration
├── README.md                          #  Project overview & documentation
└── report.pdf                         #  Final analysis report (Assignment 4)

```

## Prerequisites
- Java 17+ (JDK 25 recommended)
- Maven 3.9.11+
- git (optional, for version control)

## Build
```bash
# from repository root
mvn clean package -DskipTests
```

## Run tests
```bash
mvn test
```

## Running the main pipeline
A `Main` class is provided to run the processing pipeline on a JSON graph file:
```bash
# run with java (adjust classpath if needed)
java -cp target/classes:target/dependency/* com.aitbek.Main data/graph_small_1.json

# or via Maven exec plugin
mvn exec:java -Dexec.mainClass="com.aitbek.Main" -Dexec.args="data/graph_small_1.json"
```
The pipeline executes: load graph → compute SCCs → build condensation graph → topological sort → compute DAG shortest & longest paths. Results and metrics are exported to `results/csv/summary.csv`.

## Input format
Input graph JSON example:
```json
{
  "directed": true,
  "n": 8,
  "edges": [
    {"u": 0, "v": 1, "w": 3},
    {"u": 1, "v": 2, "w": 2}
  ],
  "source": 4,
  "weight_model": "edge"
}
```
- `weight_model` — currently `"edge"` is supported.
- Vertex indices are 0-based.

## Metrics & CSV
The benchmark CSV (`results/csv/summary.csv`) contains per-run metrics:
```
dataset_id,n,m,is_dag,algo,metric_name,metric_value,time_ns,run_id,seed,notes
```
Include CSV data and plots in the final report to compare algorithmic behavior across datasets.

## Tests
Unit tests cover:
- Graph generator and I/O.
- SCC correctness (including corner cases).
- Topological ordering correctness.
- DAG shortest and longest path results.

Run tests with `mvn test`.

## Notes & limitations
- Only the edge-weight model is implemented. Extending to node-duration model requires input conversion and small changes to path algorithms.
- Graphs assume 0-based indexing.
- The dataset generator produces multiple sizes and densities; inspect `data/` for generated files.

## For using functionaloty of program:
- You need to run Main and use cli

## Author
Nugmanov Aitbek