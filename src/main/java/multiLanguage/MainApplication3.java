package multiLanguage;

import com.google.gson.Gson;
import location_data.ESConfig;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainApplication3 {

    private static ESConfig esConfig = new ESConfig();

    private static Client esClient;

    public static void main(String args[]) {
        try {
            esClient = ESConfig.getConnection();

            File file = new File("/home/himanshuk/Downloads/itemid_list.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));

            String str;

            while((str = br.readLine()) != null) {
                getAllEntitiesBeforedDate(str);
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
    public static void getAllEntitiesBeforedDate(String str) {
        List<String> result = new ArrayList<String>();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery("id", str));

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        SearchResponse scrollResp = esClient.prepareSearch("content-enrichment").setTypes("news")
                .setScroll(new TimeValue(60000)).setQuery(builder).setSize(200).setFetchSource(new String[]{"id", "date", "cluster", "keywords", "topicTitles", "language",
                        "interestGroup", "rawTopics", "searchableItem", "text", "tagsIndexMap", "contentType",
                        "contentQualityScore", "ucType", "dhDate", "version", "ingestionDate", "keywordList",  "isDocVectorPresent",
                        "newspaper", "textIndexMap", "contentModifiedDate", "titleIndexMap", "title",
                        "genre", "newspaperId", "contentCreatedDate", "isLangQualityOk", "timestamp",
                        "publisherModifiedDate", "topics", "langQualityScore",
                        "storyKeyword", "topicClassification", "titleUni", "editions", "locations",
                        "category", "topicScoreResults", "categoryId"}, null).execute().actionGet();


        String tagsToHit[] = {"id", "date", "cluster", "keywords", "topicTitles", "language",
                "interestGroup", "rawTopics", "searchableItem", "text", "tagsIndexMap", "contentType",
                "contentQualityScore", "ucType", "dhDate", "version", "ingestionDate", "keywordList",  "isDocVectorPresent",
                "newspaper", "textIndexMap", "contentModifiedDate", "titleIndexMap", "title",
                "genre", "newspaperId", "contentCreatedDate", "isLangQualityOk", "timestamp"
                , "publisherModifiedDate", "topics", "langQualityScore",
                "storyKeyword", "topicClassification", "titleUni", "editions", "locations",
                "category", "topicScoreResults", "categoryId"};


        while (true) {
            SearchHit[] hits = scrollResp.getHits().getHits();
            if (hits != null && hits.length > 0) {
                for (SearchHit hit : hits) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Object idObj = hit.getSource().get("id");
                    Object isDocVectorPresentObj = hit.getSource().get("isDocVectorPresent");
                    if (idObj != null) {
                        String isDocVectorPresent = isDocVectorPresentObj.toString();
                        if(isDocVectorPresent.equalsIgnoreCase("true")) {

                            for (int itr = 0; itr < tagsToHit.length; ++itr) {
                                Object res = hit.getSource().get(tagsToHit[itr]);
                                if(res != null){
                               // System.out.println(res.toString() + " " + tagsToHit[itr]);
                                stringBuilder.append(res.toString()).append("\t");}
                                else
                                {
                                    stringBuilder.append(" ").append("\t");
                                }
                            }
                        }
                    }
                    writeDataInFile(stringBuilder.toString());
                }
            }
            scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).
                    setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                System.out.println(" Completed fetching from ES : ");
                break;
            }
        }

    }
    private static void writeDataInFile(String newsItem) {
        try{
            FileWriter fileWriter = createFileToWrite("DataDoc");
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
            file = new File("/home/himanshuk/Desktop/" + fileName+".tsv");
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
