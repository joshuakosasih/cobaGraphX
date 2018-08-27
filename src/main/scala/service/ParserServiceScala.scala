package service

import java.util

import scala.collection.JavaConversions._

import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet
import org.apache.tinkerpop.gremlin.structure.{Vertex, VertexProperty}
import org.janusgraph.graphdb.relations.CacheEdge

class ParserServiceScala {
  var vertexes: Array[(VertexId, Object)] = null
  var edges: Array[Edge[String]] = null

  @throws[Exception]
  def search(responseList: util.List[_]) {

    if (responseList.size > 0) {
      if (responseList.get(0).isInstanceOf[Vertex]) {
        System.out.println("Parsing instance of Vertex")
        proceedVertexList(responseList)
      }
      else if (responseList.get(0).isInstanceOf[BulkSet[_]]) {
        System.out.println("Parsing instance of Bulkset")
        val bulkSet = responseList.get(0).asInstanceOf[BulkSet[_]]
        proceedBulkSet(bulkSet)
      }
    }
  }

  /**
    * Proceed bulkset search result
    *
    * @param bulkSet
    * @return
    */
  private def proceedBulkSet(bulkSet: BulkSet[_]){
    // vertexGraphIdResult keep the vertexes unique
    val vertexGraphIdResult = new util.HashMap[String, Boolean]

    val resultArray = bulkSet.toArray

    val arrayVertex = new Array[(VertexId, Object)](resultArray.length*2)
    val arrayEdge = new Array[Edge[String]](resultArray.length)

    var ie = 0
    var iv = 0

    for (e <- resultArray) {
      val edge = e.asInstanceOf[CacheEdge]
      val inVertex = edge.inVertex
      val outVertex = edge.outVertex

      // process edge
      val edgeObj = edgeConvert(edge, inVertex, outVertex)
      arrayEdge(ie) = edgeObj
      ie += 1

      // process inVertex
      val inVertexGraphId = inVertex.id.toString
      if (!vertexGraphIdResult.containsKey(inVertexGraphId)) {
        val inVertexObj = vertexConvert(inVertex)
        arrayVertex(iv) = inVertexObj
        iv += 1
        vertexGraphIdResult.put(inVertexGraphId, true)
      }

      // process outVertex
      val outVertexGraphId = outVertex.id.toString
      if (!vertexGraphIdResult.containsKey(outVertexGraphId)) {
        val outVertexObj = vertexConvert(outVertex)
        arrayVertex(iv) = outVertexObj
        iv += 1
        vertexGraphIdResult.put(outVertexGraphId, true)
      }
    }
    edges = arrayEdge
    vertexes = arrayVertex
  }

  private def proceedVertexList(resultArray: util.List[_]) {
    // vertexGraphIdResult keep the vertexes unique
    val vertexGraphIdResult = new util.HashMap[String, Boolean]

    val arrayVertex = new Array[(VertexId, Object)](resultArray.size())

    var i = 0

    for (element <- resultArray) {
      val v = element.asInstanceOf[Vertex]
      val vertexGraphId = v.id.toString
      if (!vertexGraphIdResult.containsKey(vertexGraphId)) {
        val obj = vertexConvert(v)
        arrayVertex(i) = obj
        i += 1
        vertexGraphIdResult.put(vertexGraphId, true)
      }
    }
    vertexes = arrayVertex
  }

  def edgeConvert(edge: CacheEdge, inVertex: Vertex, outVertex: Vertex): Edge[String] = {
    val e = new Edge[String](inVertex.id.asInstanceOf[Long], outVertex.id.asInstanceOf[Long], edge.getType.name)
    return e
  }

  def vertexConvert(v: Vertex): (VertexId, Object) = {
    val properties = v.properties()
    var local_id = ""
    while ( {properties.hasNext} ) {
      val property = properties.next.asInstanceOf[VertexProperty[_]]
      if (property.key.contains("_id"))
        local_id = property.value.toString
    }
    return (v.id.asInstanceOf[Long], (local_id, v.label))
  }
}
