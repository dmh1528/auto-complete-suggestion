package Kafka;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
public class KafkaProducers {
    private final static String TOPIC = "post_change_log";
    private final static String BOOTSTRAP_SERVERS = "192.168.24.226:9092,192.168.25.8:9092";

    private static Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    static void runProducer(List<String> ids) throws Exception {
        final Producer<Long, String> producer = createProducer();
        try {
            int curr = 0;
            for (int index = 0; index < ids.size(); index++) {
                System.out.println("index ::" + index);
                String id = ids.get(index);
                String message = "{\"id\":\"" +id + "\",\"action\":\"UPDATE\"}";
                System.out.println("message ::" + message);
                final ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, message);
                RecordMetadata metadata = producer.send(record).get();

            }
        } finally {
            producer.flush();
            producer.close();
        }
    }

    public static List<String> PostRequest() throws IOException, JSONException {

        JSONObject payload = new JSONObject();
        String s = "9223372037";
        Long x = Long.parseLong(s);
        payload.put("limit", x);
        payload.put("where", "TRUE");

        JSONObject innerPayload = new JSONObject();
        innerPayload.put("field", "$post.common.dates.published");
        innerPayload.put("direction", "DESC");
        payload.put("order", innerPayload);

        JSONObject innerPayload1 = new JSONObject();
        payload.put("select", innerPayload1);

        URL url = new URL("http://192.168.10.233:6464/api/obelix/search/simple");


        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(payload.toString());
        out.flush();
        out.close();

        int res = connection.getResponseCode();

        System.out.println(res);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine = null;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        connection.disconnect();

        JSONArray jsonArray = new JSONArray(content.toString());

        List<String> list = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); ++i){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            list.add(id);
        }
        return list;

    }


    public static void main(String[] args) throws Exception {
        List<String> list = PostRequest();
        System.out.println("size ::" + list.size());
        runProducer(list);
    }
}
