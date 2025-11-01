package com.aitbek.graph.dagsp;

import com.aitbek.data.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGLongestPathTest {

    @Test
    void testLongestPathSimpleDAG() {
        Graph graph = new Graph(6, true, "edge");
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 4, 1);
        graph.addEdge(3, 5, 2);

        DAGLongestPath lp = new DAGLongestPath();
        DAGLongestPath.LongestPathResult result = lp.findLongestPaths(graph);

        assertFalse(result.hasCycle());
        assertEquals(7, result.getMaxDistance()); // 0->2->3->5 (1+4+2=7)

        List<Integer> criticalPath = result.getCriticalPath();
        assertFalse(criticalPath.isEmpty());

        assertTrue(result.getMetrics().getCounter("relaxations") > 0);
    }

    @Test
    void testLongestPathMultipleSources() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 4, 1);

        DAGLongestPath lp = new DAGLongestPath();
        DAGLongestPath.LongestPathResult result = lp.findLongestPaths(graph);

        assertFalse(result.hasCycle());
        assertEquals(8, result.getMaxDistance()); // 0->2->3->4 (3+4+1=8)
    }

    @Test
    void testLongestPathSingleNode() {
        Graph graph = new Graph(1, true, "edge");

        DAGLongestPath lp = new DAGLongestPath();
        DAGLongestPath.LongestPathResult result = lp.findLongestPaths(graph);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getMaxDistance());
        assertEquals(List.of(0), result.getCriticalPath());
    }

    @Test
    void testLongestPathWithUnreachableNodes() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 3);

        DAGLongestPath lp = new DAGLongestPath();
        DAGLongestPath.LongestPathResult result = lp.findLongestPaths(graph);

        assertFalse(result.hasCycle());
        assertEquals(3, result.getMaxDistance());
    }

    @Test
    void testLongestPathWithCycle() {
        Graph graph = new Graph(3, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        DAGLongestPath lp = new DAGLongestPath();
        DAGLongestPath.LongestPathResult result = lp.findLongestPaths(graph);

        assertTrue(result.hasCycle());
    }

    @Test
    void testLongestPathEmptyGraph() {
        Graph graph = new Graph(0, true, "edge");

        DAGLongestPath lp = new DAGLongestPath();
        DAGLongestPath.LongestPathResult result = lp.findLongestPaths(graph);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getMaxDistance());
        assertTrue(result.getCriticalPath().isEmpty());
    }

    @Test
    void testLongestPathWithPositiveWeights() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 4);

        DAGLongestPath lp = new DAGLongestPath();
        DAGLongestPath.LongestPathResult result = lp.findLongestPaths(graph);

        assertFalse(result.hasCycle());
        assertTrue(result.getMaxDistance() > 0);
        assertTrue(result.getMetrics().getCounter("relaxations") > 0);
    }
}