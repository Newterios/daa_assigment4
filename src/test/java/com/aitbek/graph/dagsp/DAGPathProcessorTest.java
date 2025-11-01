package com.aitbek.graph.dagsp;

import com.aitbek.data.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DAGPathProcessorTest {

    @Test
    void testCompleteProcessing() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 4);

        DAGPathProcessor processor = new DAGPathProcessor();
        DAGPathProcessor.CompletePathResult result = processor.processGraph(graph, 0);

        assertFalse(result.getShortestPathResult().hasCycle());
        assertFalse(result.getLongestPathResult().hasCycle());
        assertEquals(5, result.getTopoResult().getTaskOrder().size());
    }

    @Test
    void testCompleteProcessingWithCycle() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(1, 3, 1);

        DAGPathProcessor processor = new DAGPathProcessor();
        DAGPathProcessor.CompletePathResult result = processor.processGraph(graph, 0);

        assertFalse(result.getShortestPathResult().hasCycle());
        assertFalse(result.getLongestPathResult().hasCycle());
    }

    @Test
    void testCompleteProcessingSingleNode() {
        Graph graph = new Graph(1, true, "edge");

        DAGPathProcessor processor = new DAGPathProcessor();
        DAGPathProcessor.CompletePathResult result = processor.processGraph(graph, 0);

        assertFalse(result.getShortestPathResult().hasCycle());
        assertFalse(result.getLongestPathResult().hasCycle());
        assertEquals(0, result.getShortestPathResult().getDistanceTo(0));
        assertEquals(0, result.getLongestPathResult().getMaxDistance());
        assertEquals(1, result.getTopoResult().getTaskOrder().size());
    }
    @Test
    void testCompleteProcessingSingleComponent() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 1);

        DAGPathProcessor processor = new DAGPathProcessor();
        DAGPathProcessor.CompletePathResult result = processor.processGraph(graph, 0);

        assertFalse(result.getShortestPathResult().hasCycle());
        assertFalse(result.getLongestPathResult().hasCycle());
        assertTrue(result.getShortestPathResult().getMetrics().getCounter("relaxations") > 0);
        assertTrue(result.getLongestPathResult().getMetrics().getCounter("relaxations") > 0);
    }

    @Test
    void testCompleteProcessingMultipleComponents() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);  // цикл
        graph.addEdge(3, 4, 2);  // отдельная компонента

        DAGPathProcessor processor = new DAGPathProcessor();
        DAGPathProcessor.CompletePathResult result = processor.processGraph(graph, 0);

        assertFalse(result.getShortestPathResult().hasCycle());
        assertFalse(result.getLongestPathResult().hasCycle());
        assertEquals(3, result.getTopoResult().getComponentOrder().size());
    }
}