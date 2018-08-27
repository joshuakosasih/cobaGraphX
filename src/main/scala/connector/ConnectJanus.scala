package connector

import org.janusgraph.core.{JanusGraph, JanusGraphFactory}

class ConnectJanus {
  def inMemory: JanusGraph = {
    return JanusGraphFactory.open("inmemory")
  }

  def inCassandra: JanusGraph = {
    return JanusGraphFactory.build.
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
}
