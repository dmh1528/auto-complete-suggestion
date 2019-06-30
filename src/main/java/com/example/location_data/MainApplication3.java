package com.example.location_data;

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
import java.text.SimpleDateFormat;
import java.util.*;

public class MainApplication3 {
    private static ESConfig esConfig = new ESConfig();

    private static Client esClient;

    private static String url = "http://dh-ldaservice.dailyhunt.in/ldatopics";

    public static void main(String args[]) {
        try {
            esClient = ESConfig.getConnection();
            String startDate = "2019-03-31";
            String endDate = "2019-03-31";
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
            esClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private static void getData(String dateToFetch){
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        //builder.must(QueryBuilders.termQuery("language", "TAMIL"));
        //builder.should(QueryBuilders.termQuery("language", "HINDI")).should(QueryBuilders.termQuery("language", "ENGLISH")).minimumNumberShouldMatch(1);
        String dateString = dateToFetch;
        builder.must(QueryBuilders.rangeQuery("date").from(dateString + "T00:00:00.000Z").to(dateString + "T23:59:59.999Z")
                .includeLower(true).includeUpper(true));
        SearchResponse scrollResp = esClient.prepareSearch("content-enrichment").setTypes("news")
                .setScroll(new TimeValue(60000)).setQuery(builder).setSize(200).setFetchSource(new String[]{"id", "text", "locNormV3"}, null).execute().actionGet();
        try{
            int i=0;
            Map<String, List<String> > mp = new HashMap< String, List<String>>();
            while (true) {
                long time1 = System.currentTimeMillis();
                SearchHit[] hits = scrollResp.getHits().getHits();
                if (hits != null && hits.length > 0) {
                    for (SearchHit hit : hits) {
                        StringBuilder stringBuilder = new StringBuilder();
                        Object idObj = hit.getSource().get("id");
                        Object textObj = hit.getSource().get("text");
                        Object locNormV3Obj = hit.getSource().get("locNormV3");
                        BitWiseIndexMap o1 = new BitWiseIndexMap();
                        if(idObj != null) {
                            String id = idObj.toString();
                            stringBuilder.append(id).append("\t");


                                if (textObj != null) {
                                    String category1 = textObj.toString();
                                    if (StringUtils.isNotEmpty(category1)) {
                                        stringBuilder.append(category1).append("\t");
                                    }
                                    TextUtil O1 = new TextUtil();
                                    String getFirstParagraph = O1.getFirstParagraph(textObj.toString());
                                    String getFirstWord = O1.getFirstWord(textObj.toString());
                                    String firstSentence = O1.firstSentence(textObj.toString());

                                    if (StringUtils.isNotEmpty(getFirstParagraph)) {
                                        stringBuilder.append(getFirstParagraph).append("\t");
                                    }

                                    if (StringUtils.isNotEmpty(getFirstWord)) {
                                        stringBuilder.append(getFirstWord).append("\t");
                                    }

                                    if (StringUtils.isNotEmpty(firstSentence)) {
                                        stringBuilder.append(firstSentence).append("\t");
                                    }



                                }

                                if(locNormV3Obj != null){
                                    String category1 = locNormV3Obj.toString();
                                    if (StringUtils.isNotEmpty(category1)) {
                                        stringBuilder.append(category1).append("\t");
                                    }

                                }

                            writeDataInFile(stringBuilder.toString());

                        }
                    }
                }

                //System.out.println("#################", + mp.size());

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
            FileWriter fileWriter = createFileToWrite("sample");
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
            file = new File("/home/himanshuk/Desktop/demo/src/main/resources/temp/" + fileName+".tsv");
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