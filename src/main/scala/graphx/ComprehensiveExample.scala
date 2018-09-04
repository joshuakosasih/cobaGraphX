package graphx

import java.util

import bl.core.neo.graph.db.UserDB
import com.google.inject.Guice
import connector.{ConnectJanus, DependencyInjection}
import org.apache.tinkerpop.gremlin.structure.Vertex
import service.ParserServiceScala

object ComprehensiveExample {

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector(new DependencyInjection)
    val userDB = injector.getInstance(classOf[UserDB])

    val connectJanus = new ConnectJanus

//    var query = "g.V().has(T.label, \"user\").repeat(__.bothE().where(P.without(\"e\")).store(\"e\").otherV()).cap(\"e\").toList()"
//    val gremlinExecutor = GremlinExecutor.build.scriptEvaluationTimeout(60000).create
//    val coba: util.List[_] = connectJanus.traverseQuery(query)

    val corecobavertex: Vertex = userDB.getUserVertex(1)

    val coba: util.List[_] = connectJanus.demoTraverse()

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
