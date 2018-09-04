package connector

import java.util

import bl.core.neo.graph.service.GraphService
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversalSource, __}
import org.apache.tinkerpop.gremlin.structure.T
import org.janusgraph.core.{JanusGraph, JanusGraphFactory}

class ConnectJanus() extends GraphService {

  var mode: String = ""

  var janus: JanusGraph = null

  var g: GraphTraversalSource = null

  if (mode == "inmemory") {
    janus = JanusGraphFactory.open("inmemory")
  }
  else {
    janus = JanusGraphFactory.build.
      set("gremlin.graph", "org.janusgraph.core.JanusGraphFactory").
      set("cache.db-cache", "true").
      set("cache.db-cache-clean-wait", "20").
      set("cache.db-cache-time", "180000").
      set("cache.db-cache-size", "0.25").
      set("index.search.backend", "elasticsearch").
      set("index.search.hostname", "127.0.0.1").
      set("standardElementConstraints", "false").
      set("storage.backend", "cassandra").
      set("storage.hostname", "127.0.0.1").
      set("storage.username", null).
      set("storage.password", null).
      set("storage.cassandra.keyspace", null).
      set("storage.port", null).open()
  }

  g = janus.traversal()

  def traverseQuery(query: String): util.List[_] = {
    val bindings = new util.HashMap[String, Object]
    bindings.put("g", g)

    val gremlinExecutor = GremlinExecutor.build.scriptEvaluationTimeout(60000).create
    val response = gremlinExecutor.eval(query, bindings)

    return response.get().asInstanceOf[util.List[_]]
  }

  def demoTraverse(): util.List[_] = {
    val res = g.V().has(T.label, "user").repeat(__.bothE().where(P.without("e")).store("e").otherV()).cap("e").toList()
    return res
  }

  override def getGraph: JanusGraph = {
    return janus
  }

  override def getGraphTraversal: GraphTraversalSource = {
    return g
  }
}
