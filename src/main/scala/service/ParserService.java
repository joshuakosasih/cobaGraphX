package service;

import bl.core.neo.graph.util.JSONKeyHelper;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.graphdb.idmanagement.IDManager;
import org.janusgraph.graphdb.relations.CacheEdge;
import org.json.simple.JSONObject;
import org.apache.spark.graphx.Edge;
import scala.Array;
import scala.Tuple2;
import scala.collection.Seq;

import java.util.*;

public class ParserService {

    public Object[] vertexes;
    public Object[] edges;

    public Tuple2 search(List responseList) throws Exception {

        JSONObject objResult = new JSONObject();
        Tuple2 result = new Tuple2(null, null);

        if (responseList.size() > 0) {
            Object resultObj = responseList.get(0);

            if (resultObj instanceof Vertex) {
                System.out.println("Instance of Vertex");
                Vertex v0 = (Vertex) responseList.get(0);
                String label = (String) v0.label();

                // proceed list of vertices
//                objResult = proceedVertexList(responseList);
            }
            else if (resultObj instanceof BulkSet) {
                System.out.println("Instance of Bulkset");
                BulkSet bulkSet = (BulkSet) resultObj;

                // proceed the bulkset
                result = proceedBulkSet(bulkSet);
            } else if (resultObj instanceof Long) {
                System.out.println("Instance of Long");
                // this condition is when user give query to count vertices or edges
                Map<String, Object> mapResult = new HashMap<>();
                mapResult.put(JSONKeyHelper.TOTAL_DATA, resultObj);
                objResult = new JSONObject(mapResult);
            }

            System.out.println("------------------------------");
            System.out.println( resultObj );
            System.out.println("--------------+++-------------");
            System.out.println( objResult );
            System.out.println("------------------------------");
        }

        return result;
    }

    /**
     * Proceed bulkset search result
     *
     * @param bulkSet
     * @return
     */
    private Tuple2 proceedBulkSet(BulkSet bulkSet) {
        // To maintain unique result of vertex list.
        // Before adding vertex to result,
        // check whether vertexGraphId has been added to vertexGraphIdResult or not.
        HashMap<String, Boolean> vertexGraphIdResult = new HashMap();

        Object[] resultArray = bulkSet.toArray();

        ArrayList arrayVertex = new ArrayList();
        ArrayList arrayEdge = new ArrayList();
        
        System.out.println("Result array");
        System.out.println(Arrays.toString(resultArray));
        System.out.println("------------");
        for (Object e : resultArray) {
            // get BulkSet elements
            CacheEdge edge = (CacheEdge) e;
            Vertex inVertex = edge.inVertex();
            Vertex outVertex = edge.outVertex();

            // proceed edge
            Edge edgeObj = edgeConvert(edge, inVertex, outVertex);
            arrayEdge.add(edgeObj);

            // proceed inVertex
            String inVertexGraphId = inVertex.id().toString();
            if(!vertexGraphIdResult.containsKey(inVertexGraphId)) {
                Tuple2 inVertexObj = vertexConvert(inVertex);
                arrayVertex.add(inVertexObj);
                vertexGraphIdResult.put(inVertexGraphId, true);
            }

            // proceed outVertex
            String outVertexGraphId = outVertex.id().toString();
            if(!vertexGraphIdResult.containsKey(outVertexGraphId)) {
                Tuple2 outVertexObj = vertexConvert(outVertex);
                arrayVertex.add(outVertexObj);
                vertexGraphIdResult.put(outVertexGraphId, true);
            }
        }

        Tuple2 output = new Tuple2(1, 2);

        edges = arrayEdge.toArray();
        vertexes =  arrayVertex.toArray();

        // put edge and vertex to final result
        return output;
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
//
//        for (Object element : results) {
//            Vertex v = (Vertex) element;
//
//            String vertexGraphId = v.id().toString();
//            if(!vertexGraphIdResult.containsKey(vertexGraphId)) {
//                JSONObject obj = proceedVertex(v);
//                arrayVertex.add(obj);
//                vertexGraphIdResult.put(vertexGraphId, true);
//            }
//        }
//
//        objResult.put(JSONKeyHelper.VERTEX_DATA, arrayVertex);
//
//        return objResult;
//    }

    public Edge edgeConvert(CacheEdge edge, Vertex inVertex, Vertex outVertex) {
        Edge e = new Edge((long)inVertex.id(), (long)outVertex.id(), edge.getType().name());
        return e;
    }

    public Tuple2 vertexConvert(Vertex v) {
        Iterator properties = v.properties(new String[0]);

        String local_id = "";

        while(properties.hasNext()) {
            VertexProperty<Object> property = (VertexProperty)properties.next();
            if (property.key().contains("_id")) {
                local_id = property.value().toString();
            }
//            else if (property.key().contains("")) {}
        }

        Tuple2 body = new Tuple2(local_id, v.label());

        Tuple2 vertex = new Tuple2((long)v.id(), body);

        return vertex;
    }
}
