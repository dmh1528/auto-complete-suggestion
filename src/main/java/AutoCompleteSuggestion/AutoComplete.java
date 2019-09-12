package AutoCompleteSuggestion;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

public class AutoComplete {

    private static Client esClient;

    public static void main(String args[]) {
        try {
            esClient = ElasticSearchConfig.getConnection();
            System.out.println("hello\n");
            String jsonObject = "{\"age\":10,\"dateOfBirth\":1471466076564,"
                    +"\"fullName\":\"John Doe\"}";
            IndexResponse response = esClient.prepareIndex("people", "Doe")
                    .setSource(jsonObject, XContentType.JSON).get();

            String id = response.getId();
            String index = response.getIndex();
            String type = response.getType();
            long version = response.getVersion();

            System.out.println(id + " " + index + " " + type + " " + version);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in connection to localhost");
        }

    }
}
