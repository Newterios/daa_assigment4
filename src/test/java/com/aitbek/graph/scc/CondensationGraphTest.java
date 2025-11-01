package com.aitbek.graph.scc;

import com.aitbek.data.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CondensationGraphTest {

    @Test
    void testCondensationGraph() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(3, 4, 1);

        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);

        CondensationGraph condensation = new CondensationGraph(graph, result.getComponents());

        assertEquals(3, condensation.getCondensationGraph().getN());
        assertEquals(2, condensation.getCondensationGraph().getEdges().size());
    }

    @Test
    void testCondensationSingleComponent() {
        Graph graph = new Graph(3, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult result = tarjan.findSCC(graph);

        CondensationGraph condensation = new CondensationGraph(graph, result.getComponents());

        assertEquals(1, condensation.getCondensationGraph().getN());
        assertEquals(0, condensation.getCondensationGraph().getEdges().size());
    }
}