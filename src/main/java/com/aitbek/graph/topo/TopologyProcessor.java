package com.aitbek.graph.topo;

import com.aitbek.graph.scc.CondensationGraph;
import com.aitbek.graph.scc.TarjanSCC;
import com.aitbek.data.Graph;

import java.util.List;

public class TopologyProcessor {
    
    public TopologicalSortResult processGraph(Graph graph) {
        TarjanSCC tarjan = new TarjanSCC();
        TarjanSCC.SCCResult sccResult = tarjan.findSCC(graph);
        
        CondensationGraph condensation = new CondensationGraph(graph, sccResult.getComponents());
        
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        Graph condensedGraph = condensation.getCondensationGraph();
        KahnTopologicalSort.TopoResult componentTopo = kahn.sort(condensedGraph);
        
        List<Integer> taskOrder = kahn.getTaskOrderFromComponents(
            sccResult.getComponents(), 
            componentTopo.getOrder()
        );
        
        return new TopologicalSortResult(
            componentTopo.getOrder(),
            taskOrder,
            componentTopo
        );
    }
}
