package graphx

import java.util

import bl.core.neo.graph.db.CategoryDB
import bl.core.neo.graph.service.GraphService
import com.google.inject.Guice
import connector.{ConnectJava, DependencyInjection}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.janusgraph.core.JanusGraphFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.structure.T
import service.{ParserService, ParserServiceScala}
import scala.collection.JavaConverters._


object ComprehensiveExample {
  def createArray():Array[Any] = {
    val line = scala.io.StdIn.readInt()
    var arrOut = new Array[Any](line)
    var x = 0
    for (x <- 0 to line-1) {
      print("Enter text-"+x+": ")
      var inText = scala.io.StdIn.readLine()
      println(inText.split(" +").deep.mkString(", "))
      arrOut(x) = (inText.split(" +"), inText.split(" +").reverse)
    }
    println(arrOut.deep.mkString(", "))
    return arrOut
  }

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector(new DependencyInjection)
    val connectJava = injector.getInstance(classOf[ConnectJava])
//    connectJava.getCategories(0, 0)
//    createArray()

    val janus = JanusGraphFactory.build.
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

    val g = janus.traversal()

    println( g.V().has(T.label, "user").repeat(__.bothE().where(P.without("e")).store("e").otherV()).cap("e").toList() )

    val coba = g.V().has(T.label, "user").repeat(__.bothE().where(P.without("e")).store("e").otherV()).cap("e").toList()

    val parser = new ParserServiceScala

    val jsonCoba = parser.search(coba)

    val vertexes = parser.vertexes

    val edges = parser.edges

    val defaultVertex = ("0", "Missing")

    var userArr = Array(
      (3L, ("rxin", "student")),
      (7L, ("jgonzal", "postdoc")),
      (5L, ("franklin", "prof")),
      (2L, ("istoica", "prof"))
    )
//
//    println(userArr.deep)
//    println(vertexes)
//    val seqVertex =  vertexes.toSeq
//    println(seqVertex)


//    val relationshipArr = Array(
//      Edge(3L, 7L, "collab"),
//      Edge(5L, 3L, "advisor"),
//      Edge(2L, 5L, "colleague"),
//      Edge(5L, 7L, "pi")
//    )
//
//    println(relationshipArr.deep)
//    println(jsonCoba._1)

    println("Starting spark session")

    val spark = SparkSession
      .builder
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()

    val sc = spark.sparkContext

    println("Cleaning vertex array")
    val nullCount = vertexes.count(p => Option(p) == None)
    println("Null count: " + nullCount)
    val newvertexes = vertexes.dropRight(nullCount)

    println("Creating spark vertex")

    val users: RDD[(VertexId, Object)] = (sc.parallelize(newvertexes))
    println("Spark vertex created, # of element: " + users.count())
    println("Collection: " + users.collect())
    println("Deep: " + users.collect().deep)

    println("Creating another spark vertex")

    val userArrRdd: RDD[(VertexId, Object)] = (sc.parallelize(userArr))
    println("Spark vertex 2 created, # of element: " + userArrRdd.count())
    println("Collection 2: " + userArrRdd.collect())
    println("Deep 2: " + userArrRdd.collect().deep)

//    val tempVertexRdd = VertexRDD(users)
//    println("Spark vertexrdd created, # of element: " + tempVertexRdd.count())

    println("Creating spark edge")

    val relationships: RDD[Edge[String]] = sc.parallelize(edges)
    println("Spark edge created, # of element: ", relationships.count())

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
