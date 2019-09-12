package Kafka;

import com.datastax.driver.core.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
public class KafkaReadData {

    private static String default_host = "dh4-a2-kafka-n1-internal.dailyhunt.in:9092,dh4-a2-kafka-n2-internal.dailyhunt.in:9092,dh4-a2-kafka-n3-internal.dailyhunt.in:9092";
    public static Session session;
    public static Session session1;

    private static  KafkaConsumer<Long, String> createNewConsumer(String groupId, String host, String Topic) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final org.apache.kafka.clients.consumer.Consumer<Long, String> consumer;
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(Topic));
        return new KafkaConsumer<>(properties);
    }

    public static void main(String[] args) {
        final Consumer<Long, String> consumer = createNewConsumer("cis_creator_item_events_local_", default_host, "content_enrichment_docvec_events");
        consumer.subscribe(Collections.singletonList("content_enrichment_docvec_events"));

        String old_node1 = "192.168.24.104";
        String old_node2 = "192.168.2.32";
        String old_node3 = "192.168.2.34";
        String old_node4 = "192.168.2.33";
        String old_node5 = "192.168.24.105";
        String old_keySpace = "content";

        int port = 9042;
        CassandraConnector2.connect(old_node1, old_node2, old_node3, old_node4, old_node5, port, old_keySpace);
        session = CassandraConnector2.getSession();

        String new_node1 = "172.30.9.210";
        String new_node2 = "172.30.9.220";
        String new_node3 = "172.30.9.221";
        String new_keySpace = "enrichment_vectors";

        int port1 = 9042;

        CassandraConnector2.connect(new_node1,new_node2, new_node3,port1, new_keySpace);
        session1 = CassandraConnector2.getSession();

        final int giveUp = 100;
        int noRecordsCount = 0;
        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);
            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }
            consumerRecords.forEach(record -> {
                JSONObject jsonObject = new JSONObject();
                JSONObject vector = new JSONObject();
                int id = 0;
                try {
                    jsonObject = new JSONObject(record.value());
                    vector = new JSONObject(jsonObject.get("docVector").toString());
                    id = vector.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PreparedStatement prepared = session.prepare("SELECT * FROM content_docvectorstore WHERE mysql_id =" +  "?");
                BoundStatement bound = prepared.bind(id);

                ResultSet result = session.execute(bound);
                Row x = result.one();
                String language = x.getString(0);
                String item_date = x.getString(1);
                String docvec_version = x.getString(2);
                long mysql_id = x.getInt(3);
                String docvector = x.getString(4);
                String initial_docvector = x.getString(5);
                long provider_id = x.getInt(6);

                String new_table = "ogc_news_text_docvecstore";
                PreparedStatement prepared1 = session1.prepare("INSERT INTO " + new_table + " (language, item_date, docvec_version, id, docvector, initial_docvector, source_id)" + " values (?, ?, ?, ?, ?, ?, ?)");
                BoundStatement bound1 = prepared1.bind(language, item_date, docvec_version, mysql_id, docvector, initial_docvector, provider_id);
                session1.execute(bound1);

            });
            consumer.commitAsync();
        }
        consumer.close();
    }

}

