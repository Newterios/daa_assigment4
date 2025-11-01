package com.aitbek.graph.dagsp;

import com.aitbek.data.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathTest {

    @Test
    void testShortestPathSimpleDAG() {
        Graph graph = new Graph(6, true, "edge");
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 1);
        graph.addEdge(3, 5, 4);

        DAGShortestPath sp = new DAGShortestPath();
        DAGShortestPath.ShortestPathResult result = sp.findShortestPaths(graph, 0);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getDistanceTo(0));
        assertEquals(2, result.getDistanceTo(1));
        assertEquals(1, result.getDistanceTo(2));
        assertEquals(3, result.getDistanceTo(3)); // 0->2->3 (1+2=3)
        assertEquals(4, result.getDistanceTo(4)); // 0->2->3->4 (1+2+1=4)
        assertEquals(7, result.getDistanceTo(5)); // 0->2->3->5 (1+2+4=7)

        assertEquals(List.of(0, 2, 3), result.getPathTo(3));
        assertTrue(result.getMetrics().getCounter("relaxations") > 0);
    }

    @Test
    void testShortestPathWithUnreachableNodes() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(2, 3, 1);

        DAGShortestPath sp = new DAGShortestPath();
        DAGShortestPath.ShortestPathResult result = sp.findShortestPaths(graph, 0);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getDistanceTo(0));
        assertEquals(1, result.getDistanceTo(1));
        assertEquals(Integer.MAX_VALUE, result.getDistanceTo(2));
        assertEquals(Integer.MAX_VALUE, result.getDistanceTo(3));
    }

    @Test
    void testShortestPathWithCycle() {
        Graph graph = new Graph(3, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1); // цикл

        DAGShortestPath sp = new DAGShortestPath();
        DAGShortestPath.ShortestPathResult result = sp.findShortestPaths(graph, 0);

        assertTrue(result.hasCycle());
        assertEquals(1, result.getMetrics().getCounter("cycle_detected"));
    }

    @Test
    void testShortestPathSingleNode() {
        Graph graph = new Graph(1, true, "edge");

        DAGShortestPath sp = new DAGShortestPath();
        DAGShortestPath.ShortestPathResult result = sp.findShortestPaths(graph, 0);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getDistanceTo(0));
        assertEquals(List.of(0), result.getPathTo(0));
    }

    @Test
    void testShortestPathEmptyGraph() {
        Graph graph = new Graph(0, true, "edge");

        DAGShortestPath sp = new DAGShortestPath();
        DAGShortestPath.ShortestPathResult result = sp.findShortestPaths(graph, 0);

        assertFalse(result.hasCycle());
    }
}