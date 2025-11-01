package com.aitbek.data;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class GraphGeneratorTest {

    @Test
    void testGraphGeneration() {
        GraphGenerator generator = new GraphGenerator(12345L);
        Graph graph = generator.generateGraph(10, 0.3, true, 12345L);

        assertNotNull(graph);
        assertEquals(10, graph.getN());
        assertTrue(graph.isDirected());
        assertEquals("edge", graph.getWeightModel());
        assertTrue(graph.getEdges().size() > 0);
    }

    @Test
    void testGraphFileCreation() {
        try {
            Graph graph = new Graph(5, true, "edge");
            graph.addEdge(0, 1, 1);
            graph.addEdge(1, 2, 2);

            GraphWriter writer = new GraphWriter();
            String testPath = "data/test_graph.json";
            writer.writeToJson(graph, testPath);

            File file = new File(testPath);
            assertTrue(file.exists());

            GraphReader reader = new GraphReader();
            Graph readGraph = reader.readFromJson(testPath);
            assertEquals(graph.getN(), readGraph.getN());

            file.delete();
        } catch (Exception e) {
            fail("Exception during graph file creation test: " + e.getMessage());
        }
    }
}