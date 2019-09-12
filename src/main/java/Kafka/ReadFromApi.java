package Kafka;

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

public class ReadFromApi {

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

        URL url = new URL("http://192.168.10.231:6464/api/obelix/search/simple");


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

    public static void main(String[] args) throws IOException, JSONException {
        ;
    }
}
