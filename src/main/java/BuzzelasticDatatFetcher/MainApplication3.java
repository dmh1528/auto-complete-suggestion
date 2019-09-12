package BuzzelasticDatatFetcher;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import com.google.gson.JsonObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainApplication3 {
    private static ESConfig esConfig = new ESConfig();

    private static Client esClient;

    private static  Map<String, Map<String, String> > keywordMap = new HashMap<>();

    private static Map<String, String> mp = new HashMap<>();

    public static void read() throws IOException {
        File file = new File("/home/himanshuk/Desktop/demo/src/main/resources/himanshu/genre_subgenre_tags - Sheet1.tsv");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        int flag = 0;
        st = br.readLine();
        while((st = br.readLine()) != null)
        {
            String splitString[] = st.split("\\t");
            for(int i = 5; i < splitString.length; ++i)
            {
                if(!keywordMap.containsKey(splitString[i]))
                {
                    Map<String, String> tmp = new HashMap<>();
                    tmp.put(splitString[4], splitString[3]);
                    keywordMap.put(splitString[i], tmp);
                }
                else
                {
                    Map<String, String>tmp = keywordMap.get(splitString[i]);
                    tmp.put(splitString[4], splitString[3]);
                    keywordMap.put(splitString[i], tmp);
                }
            }
            if(splitString.length > 4)
            mp.put(splitString[4], splitString[3]);
        }
    }


    public static void main(String args[]) {
        try {
            read();

            esClient = ESConfig.getConnection();
            String startDate = "2019-08-12";
            String endDate = "2019-08-12";
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

    private static void getData(String dateToFetch) throws ParseException {

        String str = dateToFetch + " 00:00:00.000";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = df.parse(str);
        long epoch = date.getTime();

        String str1 = dateToFetch + " 23:59:59.999";
        date = df.parse(str1);
        long epoch1 = date.getTime();

        Integer total_count = 0;
        Integer s2tCount = 0;

        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        builder.must(QueryBuilders.termQuery("language", "hindi"));

        builder.must(QueryBuilders.rangeQuery("createdDate").from(epoch).to(epoch1)
                .includeLower(true).includeUpper(true));

        System.out.println(epoch + " " + epoch1);

        SearchResponse scrollResp = esClient.prepareSearch("content-enrichment-buzz").setTypes("buzz").setScroll(new TimeValue(60000)).setQuery(builder).setSize(200).setFetchSource(new String[]{"id", "keywords", "dhTags", "interestGroup"}, null).execute().actionGet();
        try{
            int i=0;
            while (true) {
                long time1 = System.currentTimeMillis();
                SearchHit[] hits = scrollResp.getHits().getHits();
                if (hits != null && hits.length > 0) {
                    for (SearchHit hit : hits) {
                        StringBuilder stringBuilder = new StringBuilder();
                        Object idObj = hit.getSource().get("id");
                        Object keywordsObj = hit.getSource().get("keywords");
                        Object dhTagsObj = hit.getSource().get("dhTags");
                        Object interestGroupObj = hit.getSource().get("interestGroup");


                        if (idObj != null) {
                            String id = idObj.toString();
                            String keywords = keywordsObj != null ? keywordsObj.toString() : null;
                            System.out.println(id + "\t" + keywords );
                            Gson converter = new Gson();
                            Type type = new TypeToken<List<String>>(){}.getType();
                            List<String> list = converter.fromJson(keywords, type);
                            List<String> tagID = new ArrayList<>();
                            List<String>tags = new ArrayList<>();
                            if(list != null) {
                                for (int itr = 0; itr < list.size(); ++itr) {
                                    if (keywordMap.containsKey(list.get(itr))) {
                                        Map<String, String> res = keywordMap.get(list.get(itr));
                                        for (Map.Entry<String, String> it : res.entrySet()) {
                                            if(!it.getKey().isEmpty()){
                                            tagID.add(it.getKey());
                                            tags.add(it.getValue());}
                                        }
                                    }
                                }
                            }
                            stringBuilder.append(id).append("\t");
                            stringBuilder.append(tagID.toString()).append("\t");
                            stringBuilder.append(tags.toString()).append("\t");
                            String genre = null;
                            String genreId = null;
                            String subGenre = null;
                            String subGenreId = null;
                            if(dhTagsObj != null) {
                                System.out.println(dhTagsObj.toString());
                                JSONArray jsonArray = new JSONArray(dhTagsObj.toString());

                                for (int lol = 0; lol < jsonArray.length(); ++lol) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(lol);
                                    String genreType = jsonObject.getString("type");
                                    if (genreType.equals("GENRE")) {
                                        genre = jsonObject.getString("name");
                                        genreId = jsonObject.getString("dhTagId");
                                        break;
                                    }
                                }

                                for (int lol = 0; lol < jsonArray.length(); ++lol) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(lol);
                                    String genreType = jsonObject.getString("type");
                                    if (genreType.equals("SUBGENRE")) {
                                        subGenre = jsonObject.getString("name");
                                        subGenreId = jsonObject.getString("dhTagId");
                                        break;
                                    }
                                }
                            }

                            stringBuilder.append(genreId).append("\t");
                            stringBuilder.append(subGenreId).append("\t");
                            String interestGroup = null;
                            if(interestGroupObj != null)
                                interestGroup = interestGroupObj.toString();
                            stringBuilder.append(interestGroup).append("\t");

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
            FileWriter fileWriter = createFileToWrite("result");
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
