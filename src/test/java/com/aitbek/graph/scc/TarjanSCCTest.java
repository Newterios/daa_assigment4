package com.aitbek.graph.scc;

import com.aitbek.data.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TarjanSCCTest {

    @Test
    void testSimpleGraphWithTwoSCC() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(3, 4, 1);

        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);

        assertEquals(3, result.getComponentCount());
        assertTrue(result.getComponentSizes().contains(3));
        assertTrue(result.getComponentSizes().contains(1));
        assertTrue(result.getComponentSizes().contains(1));
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true, "edge");

        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);

        assertEquals(1, result.getComponentCount());
        assertEquals(1, result.getComponents().get(0).size());
        assertEquals(0, result.getComponents().get(0).get(0));
    }

    @Test
    void testEmptyGraph() {
        Graph graph = new Graph(0, true, "edge");

        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);

        assertEquals(0, result.getComponentCount());
    }

    @Test
    void testFullyConnectedGraph() {
        Graph graph = new Graph(4, true, "edge");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j) {
                    graph.addEdge(i, j, 1);
                }
            }
        }

        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);

        assertEquals(1, result.getComponentCount());
        assertEquals(4, result.getComponents().get(0).size());
    }

    @Test
    void testMultipleIndependentCycles() {
        Graph graph = new Graph(6, true, "edge");
        // First cycle: 0-1-2
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        // Second cycle: 3-4-5
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1);

        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);

        assertEquals(2, result.getComponentCount());
        assertEquals(3, result.getComponents().get(0).size());
        assertEquals(3, result.getComponents().get(1).size());
    }
}