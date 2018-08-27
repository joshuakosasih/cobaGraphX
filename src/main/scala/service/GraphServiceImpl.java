package service;

import bl.core.neo.graph.service.GraphService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;


public class GraphServiceImpl implements GraphService {
    private JanusGraph graph;

    public GraphServiceImpl() {
        graph = JanusGraphFactory.open("inmemory");
    }

    @Override
    public GraphTraversalSource getGraphTraversal() {
        if (graph.isClosed()) {
            graph = JanusGraphFactory.open("inmemory");
        }

        return graph.traversal();
    }

    @Override
    public JanusGraph getGraph() {
        if (graph.isClosed()) {
            graph = JanusGraphFactory.open("inmemory");
        }

        return graph;
    }
}
