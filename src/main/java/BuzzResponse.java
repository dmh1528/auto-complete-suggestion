import com.google.gson.JsonArray;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;

public class BuzzResponse {

    List<List<String >> distance;
    List<List<Integer>> ids;

    public List<List<String>> getDistance() {
        return distance;
    }

    public void setDistance(List<List<String>> distance) {
        this.distance = distance;
    }

    public List<List<Integer>> getIds() {
        return ids;
    }

    public void setIds(List<List<Integer>> ids) {
        this.ids = ids;
    }
}
