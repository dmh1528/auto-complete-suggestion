package location_data;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class BuzzESConfig {
    public static Client getConnection() throws NumberFormatException, UnknownHostException {

        Settings settings = Settings.settingsBuilder().put("cluster.name", "content-enrichment-cluster").build();
        String[] host = { "192.168.2.172", "192.168.2.171", "192.168.2.173" };
        String[] port = { "9300", "9300", "9300" };

        TransportClient transportClient = TransportClient.builder().settings(settings).build();
        for (int i = 0; i < host.length; ++i) {
            transportClient = transportClient.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(host[i]), Integer.valueOf(port[i])));
        }
        String indexName = "content-enrichment-buzz";

        boolean exists = transportClient.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
        if (!exists) {
            Settings indexSettings = Settings.settingsBuilder().put("number_of_shards", 10).put("number_of_replicas", 1)
                    .put("refresh_interval", "1s").build();
            CreateIndexRequest indexRequest = new CreateIndexRequest(indexName, indexSettings);
            transportClient.admin().indices().create(indexRequest).actionGet();

            try {
                XContentBuilder xbMapping = jsonBuilder().startObject().startObject("buzz")
                        .startObject("properties").startObject("id").field("type", "string")
                        .field("type", "string").field("index", "not_analyzed").endObject().startObject("text")
                        .field("type", "string").field("index", "analyzed").field("analyzer", "whitespace")
                        .field("index", "analyzed").field("analyzer", "whitespace")
                        .field("search_analyzer", "whitespace").endObject().startObject("language")
                        .field("type", "date","format","epoch_millis").field("index", "analyzed").endObject().startObject("createdDate");
                transportClient.admin().indices().preparePutMapping(indexName).setType("buzz")
                        .setSource(xbMapping).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transportClient;
    }
}
