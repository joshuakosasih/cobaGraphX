package graphx

import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class SparkGraphx {

  println("Starting spark session")
  val spark = SparkSession
    .builder
    .appName(s"${this.getClass.getSimpleName}")
    .getOrCreate()

  val sc = spark.sparkContext

  def createGraph(vertexes: Array[(VertexId, Object)],
                  edges: Array[Edge[String]],
                  defaultVertex: Object = null): Graph[Object, String] = {
    val users: RDD[(VertexId, Object)] = sc.parallelize(vertexes)
    println("Spark vertex created, # of element: " + users.count())

    val relationships: RDD[Edge[String]] = sc.parallelize(edges)
    println("Spark edge created, # of element: " + relationships.count())

    val graph = Graph(users, relationships, defaultVertex)

    return graph
  }
}
