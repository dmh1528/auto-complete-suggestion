package AutoCompleteSuggestion;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearchConfig {
    public static Client getConnection() throws NumberFormatException, UnknownHostException {

        Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").build();

        String[] host = { "127.0.0.1"};
        String[] port = { "9300"};

        TransportClient transportClient = TransportClient.builder().settings(settings).build();
        for (int i = 0; i < host.length; ++i) {
            transportClient = transportClient.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(host[i]), Integer.valueOf(port[i])));
        }
        System.out.println("lol");
        return transportClient;
    }
}
