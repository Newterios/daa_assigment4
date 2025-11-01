package com.aitbek.graph.topo;

import com.aitbek.data.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TopologyProcessorTest {

    @Test
    void testTopologyProcessorWithCyclicGraph() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(3, 4, 1);

        TopologyProcessor processor = new TopologyProcessor();
        TopologicalSortResult result = processor.processGraph(graph);

        assertFalse(result.hasCycle());
        assertEquals(5, result.getTaskOrder().size());
        assertEquals(3, result.getComponentOrder().size());
    }

    @Test
    void testTopologyProcessorWithPureDAG() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        TopologyProcessor processor = new TopologyProcessor();
        TopologicalSortResult result = processor.processGraph(graph);

        assertFalse(result.hasCycle());
        assertEquals(4, result.getTaskOrder().size());
        assertEquals(4, result.getComponentOrder().size());
    }

    @Test
    void testTopologyProcessorSingleNode() {
        Graph graph = new Graph(1, true, "edge");

        TopologyProcessor processor = new TopologyProcessor();
        TopologicalSortResult result = processor.processGraph(graph);

        assertFalse(result.hasCycle());
        assertEquals(1, result.getTaskOrder().size());
        assertEquals(1, result.getComponentOrder().size());
    }
}