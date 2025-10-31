package com.aitbek.graph.dagsp;

import com.aitbek.data.Graph;
import com.aitbek.graph.scc.CondensationGraph;
import com.aitbek.graph.scc.TarjanSCC;
import com.aitbek.graph.topo.TopologyProcessor;
import com.aitbek.graph.topo.TopologicalSortResult;

public class DAGPathProcessor {

    public CompletePathResult processGraph(Graph graph, int source) {
        CompletePathResult result = new CompletePathResult();

        TarjanSCC tarjan = new TarjanSCC();
        var sccResult = tarjan.findSCC(graph);
        CondensationGraph condensation = new CondensationGraph(graph, sccResult.getComponents());
        Graph condensedGraph = condensation.getCondensationGraph();

        TopologyProcessor topologyProcessor = new TopologyProcessor();
        TopologicalSortResult topoResult = topologyProcessor.processGraph(graph);
        result.setTopoResult(topoResult);


        if (sccResult.getComponentCount() == 1) {
            DAGShortestPath shortestPath = new DAGShortestPath();
            DAGShortestPath.ShortestPathResult shortestResult = shortestPath.findShortestPaths(graph, source);
            result.setShortestPathResult(shortestResult);

            DAGLongestPath longestPath = new DAGLongestPath();
            DAGLongestPath.LongestPathResult longestResult = longestPath.findLongestPaths(graph);
            result.setLongestPathResult(longestResult);
        } else {
            int condensedSource = condensation.getVertexToComponent().getOrDefault(source, 0);

            DAGShortestPath shortestPath = new DAGShortestPath();
            DAGShortestPath.ShortestPathResult shortestResult = shortestPath.findShortestPaths(condensedGraph, condensedSource);
            result.setShortestPathResult(shortestResult);

            DAGLongestPath longestPath = new DAGLongestPath();
            DAGLongestPath.LongestPathResult longestResult = longestPath.findLongestPaths(condensedGraph);
            result.setLongestPathResult(longestResult);
        }

        return result;
    }

    public static class CompletePathResult {
        private TopologicalSortResult topoResult;
        private DAGShortestPath.ShortestPathResult shortestPathResult;
        private DAGLongestPath.LongestPathResult longestPathResult;

        public TopologicalSortResult getTopoResult() { return topoResult; }
        public void setTopoResult(TopologicalSortResult topoResult) { this.topoResult = topoResult; }

        public DAGShortestPath.ShortestPathResult getShortestPathResult() { return shortestPathResult; }
        public void setShortestPathResult(DAGShortestPath.ShortestPathResult shortestPathResult) {
            this.shortestPathResult = shortestPathResult;
        }

        public DAGLongestPath.LongestPathResult getLongestPathResult() { return longestPathResult; }
        public void setLongestPathResult(DAGLongestPath.LongestPathResult longestPathResult) {
            this.longestPathResult = longestPathResult;
        }

        @Override
        public String toString() {
            return String.format(
                    "CompletePathResult{SCC=%d, Topo=%d, ShortestPaths=%s, CriticalPath=%d}",
                    topoResult.getComponentOrder().size(),
                    topoResult.getTaskOrder().size(),
                    !shortestPathResult.hasCycle(),
                    longestPathResult.getMaxDistance()
            );
        }
    }
}