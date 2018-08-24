package service

import java.util
import java.util.{Arrays, HashMap, Map}

import bl.core.neo.graph.util.JSONKeyHelper
import org.apache.tinkerpop.gremlin.structure.{Vertex, VertexProperty}
import org.janusgraph.graphdb.relations.CacheEdge
import org.json.simple.JSONObject
import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet

class ParserServiceScala {
  var vertexes: Array[(VertexId, Object)] = null
  var edges: Array[Edge[String]] = null

  @throws[Exception]
  def search(responseList: util.List[Nothing]) {
    var objResult = new JSONObject

    if (responseList.size > 0) {
//      val resultObj = responseList.get(0)
      if (responseList.get(0).isInstanceOf[Vertex]) {
        System.out.println("Instance of Vertex")
        val v0 = responseList.get(0).asInstanceOf[Vertex]
        val label = v0.label.asInstanceOf[String]
        // proceed list of vertices
        //                objResult = proceedVertexList(responseList);
      }
      else if (responseList.get(0).isInstanceOf[BulkSet[_]]) {
        System.out.println("Instance of Bulkset")
        val bulkSet = responseList.get(0).asInstanceOf[BulkSet[_]]
        // proceed the bulkset
        val result = proceedBulkSet(bulkSet)
      }
//      else if (resultObj.isInstanceOf[Long]) {
//          System.out.println("Instance of Long")
//          // this condition is when user give query to count vertices or edges
//          val mapResult = new util.HashMap[String, AnyRef]
//          mapResult.put(JSONKeyHelper.TOTAL_DATA, resultObj)
//          objResult = new JSONObject(mapResult)
//      }
    }
  }

  /**
    * Proceed bulkset search result
    *
    * @param bulkSet
    * @return
    */
  private def proceedBulkSet(bulkSet: BulkSet[_]){ // To maintain unique result of vertex list.
    // Before adding vertex to result,
    // check whether vertexGraphId has been added to vertexGraphIdResult or not.
    val vertexGraphIdResult = new util.HashMap[String, Boolean]

    val resultArray = bulkSet.toArray

    val arrayVertex = new Array[(VertexId, Object)](resultArray.length*2)
    val arrayEdge = new Array[Edge[String]](resultArray.length)

    var ie = 0
    var iv = 0

    for (e <- resultArray) { // get BulkSet elements
      val edge = e.asInstanceOf[CacheEdge]
      val inVertex = edge.inVertex
      val outVertex = edge.outVertex

      // proceed edge
      val edgeObj = edgeConvert(edge, inVertex, outVertex)
      arrayEdge(ie) = edgeObj
      ie += 1

      // proceed inVertex
      val inVertexGraphId = inVertex.id.toString
      if (!vertexGraphIdResult.containsKey(inVertexGraphId)) {
        val inVertexObj = vertexConvert(inVertex)
        arrayVertex(iv) = inVertexObj
        iv += 1
        vertexGraphIdResult.put(inVertexGraphId, true)
      }

      // proceed outVertex
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
    // put edge and vertex to final result
    return iv
  }

  //    /**
  //     * Proceed list of vertices search result
  //     */
  //    private JSONObject proceedVertexList(List results) {
  //        // To maintain unique result of vertex list.
  //        // Before adding vertex to result,
  //        // check whether vertexGraphId has been added to vertexGraphIdResult or not.
  //        HashMap<String, Boolean> vertexGraphIdResult = new HashMap();
  //
  //        JSONObject objResult = new JSONObject();
  //        JSONArray arrayVertex = new JSONArray();

  //        for (Object element : results) {
  //            Vertex v = (Vertex) element;

  //            String vertexGraphId = v.id().toString();
  //            if(!vertexGraphIdResult.containsKey(vertexGraphId)) {
  //                JSONObject obj = proceedVertex(v);
  //                arrayVertex.add(obj);
  //                vertexGraphIdResult.put(vertexGraphId, true);
  //            }
  //        }

  //        objResult.put(JSONKeyHelper.VERTEX_DATA, arrayVertex);

  //        return objResult;
  //    }

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
      //            else if (property.key().contains("")) {}
    }
    return (v.id.asInstanceOf[Long], (local_id, v.label))
  }
}
