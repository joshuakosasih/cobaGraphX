package graphx

import java.util

import connector.ConnectJanus
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor
import service.ParserServiceScala

object ComprehensiveExample {

  def main(args: Array[String]): Unit = {

    val connectJanus = new ConnectJanus("incassandra")

    var query = "g.V().has(T.label, \"user\").repeat(__.bothE().where(P.without(\"e\")).store(\"e\").otherV()).cap(\"e\").toList()"

    val gremlinExecutor = GremlinExecutor.build.scriptEvaluationTimeout(60000).create

    val coba: util.List[_] = connectJanus.traverseQuery(query)

    val parser = new ParserServiceScala
    val jsonCoba = parser.search(coba)

    val vertexes = parser.vertexes
    val edges = parser.edges
    val defaultVertex = ("-1", "Missing")

    val nullCount = vertexes.count(p => Option(p) == None)
    println("Cleaning " + nullCount + " null values from vertex array")
    val newvertexes = vertexes.dropRight(nullCount)

    val graphx = new SparkGraphx

    val graph = graphx.createGraph(newvertexes, edges, defaultVertex)

    println("---------------graph")
    println("plain graph:" + graph)
    println("edges:" + graph.numEdges)
    println("plain in-degree:" + graph.inDegrees)
    println(graph.inDegrees.foreach(println))
    println("vertices:" + graph.numVertices)
    println("genap:" + graph.vertices.filter { case (id, (name, pos)) => name == "1" }.count)
    println("---------------")
  }
}
