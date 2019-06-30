package BuzzelasticDatatFetcher;

import com.example.location_data.LocationType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import location_data.ESConfig;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainApplication3 {
    private static ESConfig esConfig = new ESConfig();

    private static Client esClient;
    private static int API_HIT = 0;
    private static int success_ig_generated = 0;
    private static int total_buzz_items = 0;

    public static void main(String args[]) {
        try {
            esClient = ESConfig.getConnection();
            String startDate = "2019-04-30";
            String endDate = "2019-04-30";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            while (start.before(end) || start.equals(end)) {
                System.out.println(" File write Started for date ::: " + getDateString(start) );
                getData(getDateString(start));
                System.out.println(" File write ended for date ::: " + getDateString(start) );
                cal.add(Calendar.DATE, 1);
                start = cal.getTime();
            }
            System.out.println("API_HIT" + API_HIT);
            System.out.println("success_ig_generated" + success_ig_generated);
            System.out.println("total_buzz_items" + total_buzz_items);
            esClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private static void getData(String dateToFetch) throws ParseException {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery("language", "hindi"));
        String string_date = "28-April-2019";
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        String dateString = dateToFetch;
        Date d = f.parse(string_date);
        long milliseconds = d.getTime();
        builder.must(QueryBuilders.rangeQuery("createdDate").from("1559520000000").to("1559606399000")
                .includeLower(true).includeUpper(true));

        SearchResponse scrollResp = esClient.prepareSearch("content-enrichment-buzz").setTypes("buzz").setScroll(new TimeValue(60000)).setQuery(builder).setSize(200).setFetchSource(new String[]{"id", "s2tText", "s2tIG", "interestGroup"}, null).execute().actionGet();
        try{
            int i=0;
            while (true) {
                long time1 = System.currentTimeMillis();
                SearchHit[] hits = scrollResp.getHits().getHits();
                if (hits != null && hits.length > 0) {
                    for (SearchHit hit : hits) {
                        StringBuilder stringBuilder = new StringBuilder();
                        Object idObj = hit.getSource().get("id");
                        Object s2textObj = hit.getSource().get("s2tText");
                        Object s2tIGObj = hit.getSource().get("s2tIG");
                        Object interestGroupObj = hit.getSource().get("interestGroup");
                        if(idObj != null) {
                            String id = idObj.toString();
                            stringBuilder.append(id).append("\t");
                            total_buzz_items++;
                            if(s2textObj != null) {
                                API_HIT++;
                                String txt = s2textObj.toString();
                                stringBuilder.append(txt).append('\t');
                                if (s2tIGObj != null) {
                                    success_ig_generated++;
                                    String s2tIG = s2tIGObj.toString();
                                    ObjectMapper mapper = new ObjectMapper();
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map = mapper.readValue(s2tIG, new TypeReference<Map<String, String>>() {
                                    });
                                    String res = "";
                                    for (String x : map.keySet()) {
                                        if (x.contains("HI_X_256")) {
                                            res = x;
                                        }
                                    }
                                    if (StringUtils.isNotEmpty(res)) {
                                        stringBuilder.append(res).append("\t");
                                    }
                                }
                                else
                                {
                                    System.out.println("text :::" + s2textObj.toString());
                                }
                            }

                            if(interestGroupObj != null) {
                                String text = interestGroupObj.toString();
                                ObjectMapper mapper = new ObjectMapper();
                                Map<String, Object> map = new HashMap<String, Object>();
                                map = mapper.readValue(text, new TypeReference<Map<String, String>>(){});
                                String ans = "";
                                for(String y: map.keySet())
                                {
                                    if(y.contains("HI_X_256"))
                                    {
                                        ans = y;
                                    }
                                }
                                if(StringUtils.isNotEmpty(ans)){
                                    stringBuilder.append(ans).append("\t");
                                }
                            }
                            if(s2tIGObj != null)
                                writeDataInFile(stringBuilder.toString());
                        }
                    }
                }
                scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).
                        setScroll(new TimeValue(60000)).execute().actionGet();
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


    private static void writeDataInFile(String newsItem) {
        try{
            FileWriter fileWriter = createFileToWrite("BUZZ_HINDI_DATA");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            Gson gson = new Gson();
            if (newsItem != null) {
                writeLine(bufferedWriter, newsItem);
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static FileWriter createFileToWrite(String fileName) {
        FileWriter fw = null;
        File file;
        try {
            file = new File("/home/himanshuk/Desktop/demo/src/main/resources/himanshu/" + fileName+".tsv");
            //      System.out.println(" File to be written " + file.getAbsolutePath());
            fw = new FileWriter(file.getAbsoluteFile(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fw;
    }

    private static void writeLine(BufferedWriter bw, String jsonData) {
        try {
            bw.write(jsonData);
            bw.write('\n');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
