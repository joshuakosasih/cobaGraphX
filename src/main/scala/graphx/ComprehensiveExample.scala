package graphx

import com.google.inject.Guice
import connector.{ConnectJava, ConnectJanus, DependencyInjection}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.structure.T
import org.janusgraph.core.JanusGraphFactory
import service.ParserServiceScala

object ComprehensiveExample {

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector(new DependencyInjection)
    val connectJava = injector.getInstance(classOf[ConnectJava])

    val connectJanus = new ConnectJanus
    val janus = connectJanus.inCassandra
    val g = janus.traversal()

    val coba = g.V().has(T.label, "user").repeat(__.bothE().where(P.without("e")).store("e").otherV()).cap("e").toList()

    val parser = new ParserServiceScala
    val jsonCoba = parser.search(coba)

    val vertexes = parser.vertexes
    val edges = parser.edges
    val defaultVertex = ("-1", "Missing")

    println("Starting spark session")
    val spark = SparkSession
      .builder
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()

    val sc = spark.sparkContext

    val nullCount = vertexes.count(p => Option(p) == None)
    println("Cleaning " + nullCount + " null values from vertex array")
    val newvertexes = vertexes.dropRight(nullCount)

    val users: RDD[(VertexId, Object)] = sc.parallelize(newvertexes)
    println("Spark vertex created, # of element: " + users.count())

    val relationships: RDD[Edge[String]] = sc.parallelize(edges)
    println("Spark edge created, # of element: " + relationships.count())

    println("Creating spark graph")

    val graph = Graph(users, relationships, defaultVertex)

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
