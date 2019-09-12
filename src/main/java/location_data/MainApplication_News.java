package location_data;

import BuzzelasticDatatFetcher.ESConfig;
import com.google.gson.Gson;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainApplication_News {
    private static ESConfig esConfig = new ESConfig();

    private static Client esClient;
    private static int API_HIT = 0;
    private static int success_ig_generated = 0;
    private static int total_buzz_items = 0;

    public static void main(String args[]) {
        try {
            esClient = ESConfig.getConnection();
            String startDate = "2019-06-01";
            String endDate = "2019-06-01";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);

            File file = new File("/home/himanshuk/Desktop/demo/src/main/resources/lang/Item_id_urdu_top100.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            int flag = 0;
            while((st = br.readLine()) != null)
            {
                getData(st);
            }

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
        builder.must(QueryBuilders.termQuery("id", dateToFetch));
        String string_date = "28-April-2019";
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        String dateString = dateToFetch;
        Date d = f.parse(string_date);
        long milliseconds = d.getTime();
        /*builder.must(QueryBuilders.rangeQuery("createdDate").from("1563494400000").to("1563577200000")
                .includeLower(true).includeUpper(true));
*/
        SearchResponse scrollResp = esClient.prepareSearch("content-enrichment").setTypes("news").setScroll(new TimeValue(60000)).setQuery(builder).setSize(200).setFetchSource(new String[]{"id", "keywords"}, null).execute().actionGet();
        try{
            int i=0;
            while (true) {
                long time1 = System.currentTimeMillis();
                SearchHit[] hits = scrollResp.getHits().getHits();
                if (hits != null && hits.length > 0) {
                    for (SearchHit hit : hits) {
                        StringBuilder stringBuilder = new StringBuilder();
                        Object idObj = hit.getSource().get("id");
                        Object s2textObj = hit.getSource().get("keywords");
                        if (idObj != null) {
                            String id = idObj.toString();
                            stringBuilder.append(id).append("\t");
                            stringBuilder.append(s2textObj.toString()).append("\t");
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
            FileWriter fileWriter = createFileToWrite("urdu");
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
            file = new File("/home/himanshuk/Desktop/demo/src/main/resources/result/" + fileName+".tsv");
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
