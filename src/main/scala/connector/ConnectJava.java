package connector;

import bl.core.neo.graph.db.CategoryDB;
import com.google.inject.Inject;
import org.json.simple.JSONArray;

public class ConnectJava {

    @Inject
    CategoryDB categoryDB;

    public JSONArray getCategories(int offset, int limit) {
        return categoryDB.getCategories(offset, limit);
    }
}
