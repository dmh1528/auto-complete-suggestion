package EStoCassandra;

import com.example.location_data.LocationType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class MainApplicationES {



    public static CreatorUniqueSequenceGenerator creatorUniqueSequenceGenerator = new CreatorUniqueSequenceGenerator();

    private static ESConfigstage esConfig = new ESConfigstage();

    private static Client esClient;

    private static final long TTL = 90L * 24L * 3600L * 1000L;

    public static List<String> NFSpathForDate = new ArrayList<>();

    public static void solve() {
        initNfsPathForDateRanges();

    }

    public static void initNfsPathForDateRanges() {
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader("/home/himanshuk/Desktop/demo/src/main/resources/himanshu/NFS_FILE_PATH_FOR_DATE"));
            String line = null;
            TreeMap<Integer, String> sortedMapNFSpathForDate = new TreeMap<>();
            while ((line = bf.readLine()) != null) {
                String[] pathArr = line.split("=");
                if (pathArr.length == 2) {
                    sortedMapNFSpathForDate.put(Integer.parseInt(pathArr[0]), pathArr[1]);
                }
            }

            for (Integer key : sortedMapNFSpathForDate.keySet()) {
                String dateAndPath = (String) sortedMapNFSpathForDate.get(key);
                NFSpathForDate.add(dateAndPath);
            }

        } catch (Exception e) {
            System.out.println("ERROR while initNfsPathForDateRanges");
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    System.out.println("Buffer close error");
                }
            }
        }

    }




    public static void main(String args[]) {
        try {
            solve();
            esClient = ESConfigstage.getConnection();
            String startDate = "2019-07-20";
            String endDate = "2019-07-26";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            while (start.before(end) || start.equals(end)) {
                long curr_time = System.currentTimeMillis();
                System.out.println(" File write Started for date ::: " + getDateString(start) + " at :: " + curr_time );
                getData(getDateString(start));
                long time_taken = System.currentTimeMillis() - curr_time;
                System.out.println(" File write ended for date ::: " + getDateString(start) + " time taken by it is :: " + time_taken);

                cal.add(Calendar.DATE, 1);
                start = cal.getTime();
            }
            esClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }



    public static Long PostRequest(String imagePath) throws IOException, JSONException {

        JSONObject payload = new JSONObject();
        payload.put("tableName", "OGC_NEWS_IMAGE");
        URL url = new URL("http://192.168.1.46:7766/doc2vec/genImageDocVecItem");

        JSONObject temp = new JSONObject();
        temp.put("imagePath", imagePath);

        payload.put("docvecStore", temp);

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

        return Long.parseLong(content.toString());
    }

    public static File getImageFile(String url, String createdDate, String Newspaper, String Category) {

        String createDate = createdDate;
        if (createDate.contains(" ")) {
            String datearr[] = (createDate.split(" ")[0]).split("-");
            createDate = datearr[0] + datearr[1] + datearr[2];
        }

        String finalPath = "";
        if (url.startsWith("images/")) {
            finalPath = getNcpImagePath(createDate, url);
        } else {
            // OCP
            if (url.startsWith("##OCP##")) {
                url = url.substring(7);
            }
            finalPath = getOcpImagePath(createDate, Newspaper, Category, url);
        }
        File file = new File(finalPath);
        System.out.println(file.toString());
        /*if (!file.exists()) {
            System.out.println("For news id: File " + finalPath + " does not exist");
            return null;
        }*/

        return file;
    }

    public static String getNcpImagePath(String createdDate, String relativePath) {
        return getBasePath(createdDate) + "shared" + "/" + "fetchdata" + "/" + relativePath;
    }

    public static String getOcpImagePath(String createdDate, String newsPaper, String catagory, String relativePath) {
        return getBasePath(createdDate) + "shared" + "/" + "fetchdata" + "/" + createdDate + "/" + newsPaper + "/" + catagory + "/images/" + relativePath;
    }

    private static String getBasePath(String createdDate) {
        String imagePath = null;
        if(imagePath==null) {
            if (NFSpathForDate.isEmpty()) {
                return null;
            }
            try {
                long newsDate = Long.parseLong(createdDate);
                for (int i = NFSpathForDate.size() - 1; i >= 0; i--) {
                    String dateAndPath = (String) NFSpathForDate.get(i);
                    if (dateAndPath.indexOf("~") != -1) {
                        String[] datePathArr = dateAndPath.split("~");
                        long dateToCompare = Long.parseLong(datePathArr[0]);
                        if (newsDate >= dateToCompare) {
                            imagePath = datePathArr[1];
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while getNfsPathforCreatedDate :" + createdDate);
            }
            if(imagePath==null) {
                imagePath = NFSpathForDate.get(0);
            }
        }
        return imagePath;
    }

    private static void getData(String dateToFetch) throws ParseException {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        String dateString = dateToFetch;
        builder.must(QueryBuilders.rangeQuery("date").from(dateToFetch + "T00:00:00.000Z").to(dateToFetch + "T23:59:59.999Z")
                .includeLower(true).includeUpper(true));
        SearchResponse scrollResp = esClient.prepareSearch("content-enrichment-ops-test").setTypes("news").setScroll(new TimeValue(60000)).setQuery(builder).setSize(200).setFetchSource(new String[]{"id", "imageUrl", "contentCreatedDate", "newspaper", "category"}, null).execute().actionGet();
        try{
            int i=0;
            while (true) {
                long time1 = System.currentTimeMillis();
                SearchHit[] hits = scrollResp.getHits().getHits();
                if (hits != null && hits.length > 0) {
                    for (SearchHit hit : hits) {
                        StringBuilder stringBuilder = new StringBuilder();
                        Object idObj = hit.getSource().get("id");
                        Object imageUrlObj = hit.getSource().get("imageUrl");
                        Object createdDateObj = hit.getSource().get("contentCreatedDate");
                        Object NewspaperObj = hit.getSource().get("newspaper");
                        Object CategoryObj = hit.getSource().get("category");
                        if (idObj != null) {
                            String id = idObj.toString();
                            if(imageUrlObj != null && createdDateObj != null) {
                                System.out.println("execution started for id :: " + id);
                                String imageUrl = imageUrlObj.toString();
                                File f1 = getImageFile(imageUrl, createdDateObj.toString(), NewspaperObj.toString(), CategoryObj.toString());
                                id = "44129856";
                                Long unique_id = Long.valueOf(1);
                                UpdateRequest updateRequest = new UpdateRequest();
                                updateRequest.index("content-enrichment-ops-test");
                                updateRequest.type("news");
                                updateRequest.id(id);
                                updateRequest.doc(jsonBuilder()
                                        .startObject()
                                        .field("unique_id", unique_id)
                                        .endObject());
                                esClient.update(updateRequest).get();
                            }
                        }
                    }
                }
                try {
                    scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).
                            setScroll(new TimeValue(60000)).execute().actionGet();
                }catch (Exception e){
                    e.printStackTrace();
                }

                System.out.println("A Batch of "+ hits.length+" fetched from ES");
                System.out.println("Completed Batch-"+ (++i) + " Time taken to complete "+  (System.currentTimeMillis() - time1 ));
                if (scrollResp.getHits().getHits().length == 0) {
                    System.out.println(" Completed fetching from ES : ");
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
