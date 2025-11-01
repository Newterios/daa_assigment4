package com.aitbek.graph.topo;

import com.aitbek.data.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KahnTopologicalSortTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        KahnTopologicalSort topo = new KahnTopologicalSort();
        KahnTopologicalSort.TopoResult result = topo.sort(graph);

        assertFalse(result.hasCycle());
        assertEquals(5, result.getOrder().size());
    }

    @Test
    void testGraphWithCycle() {
        Graph graph = new Graph(3, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        KahnTopologicalSort topo = new KahnTopologicalSort();
        KahnTopologicalSort.TopoResult result = topo.sort(graph);

        assertTrue(result.hasCycle());
        assertTrue(result.getOrder().size() < 3);
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true, "edge");

        KahnTopologicalSort topo = new KahnTopologicalSort();
        KahnTopologicalSort.TopoResult result = topo.sort(graph);

        assertFalse(result.hasCycle());
        assertEquals(1, result.getOrder().size());
        assertEquals(0, result.getOrder().get(0));
    }

    @Test
    void testEmptyGraph() {
        Graph graph = new Graph(0, true, "edge");

        KahnTopologicalSort topo = new KahnTopologicalSort();
        KahnTopologicalSort.TopoResult result = topo.sort(graph);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getOrder().size());
    }

    @Test
    void testMultipleSources() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 3, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        KahnTopologicalSort topo = new KahnTopologicalSort();
        KahnTopologicalSort.TopoResult result = topo.sort(graph);

        assertFalse(result.hasCycle());
        assertEquals(4, result.getOrder().size());
        assertEquals(3, result.getOrder().get(3));
    }
}