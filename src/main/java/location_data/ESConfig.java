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

public class ESConfig {
    public static Client getConnection() throws NumberFormatException, UnknownHostException {

        Settings settings = Settings.settingsBuilder().put("cluster.name", "content-enrichment-cluster").build();
        String[] host = { "172.30.9.172", "172.30.9.173", "172.30.9.174", "172.30.9.175", "172.30.9.176"};
        String[] port = { "9300", "9300", "9300", "9300", "9300", "9300" };

        TransportClient transportClient = TransportClient.builder().settings(settings).build();
        for (int i = 0; i < host.length; ++i) {
            transportClient = transportClient.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(host[i]), Integer.valueOf(port[i])));
        }
        String indexName = "content-enrichment";

        boolean exists = transportClient.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
        if (!exists) {
            Settings indexSettings = Settings.settingsBuilder().put("number_of_shards", 10).put("number_of_replicas", 1)
                    .put("refresh_interval", "1s").build();
            CreateIndexRequest indexRequest = new CreateIndexRequest(indexName, indexSettings);
            transportClient.admin().indices().create(indexRequest).actionGet();

            try {
                XContentBuilder xbMapping = jsonBuilder().startObject().startObject("news")
                        .startObject("properties").startObject("id").field("type", "string")
                        .field("type", "string").field("index", "not_analyzed").endObject().startObject("text")
                        .field("type", "string").field("index", "analyzed").field("analyzer", "whitespace")
                        .field("index", "analyzed").field("analyzer", "whitespace")
                        .field("search_analyzer", "whitespace").endObject().startObject("language")
                        .field("type", "date").field("index", "analyzed").endObject().startObject("dhDate");
                transportClient.admin().indices().preparePutMapping(indexName).setType("news")
                        .setSource(xbMapping).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transportClient;
    }
}