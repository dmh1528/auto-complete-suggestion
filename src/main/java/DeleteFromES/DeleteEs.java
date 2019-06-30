package DeleteFromES;

import location_data.ESConfig;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeleteEs {

    private static ESConfig esConfig = new ESConfig();

    private static Client esClient;

    public static void main(String args[]) {
        try {
            esClient = ESConfig.getConnection();

            /*String endDate = "2018-01-01";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date end = format.parse(endDate);
            Calendar cal = Calendar.getInstance();
            System.out.println(" File write Started for date ::: " + getDateString(end) );
            List<String> res = new ArrayList<String>();
            *///res = getAllEntitiesBeforedDate(getDateString(end));
            File file = new File("/home/himanshuk/Desktop/himanshu1.log");

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            int flag = 0;
            int cnt = 0;
            while((st = br.readLine()) != null)
            {
                System.out.println(st);
                String[] split = st.split("\\s+");
                String it = split[2];
                System.out.println(it);
                DeleteResponse response = esClient.prepareDelete("content-enrichment", "news", it).get();
                cnt++;
                System.out.println("cnt ::" + cnt);
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
    public static List<String> getAllEntitiesBeforedDate(String endDateString) {
        List<String> result = new ArrayList<String>();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        builder.must(QueryBuilders.rangeQuery("date").to(endDateString + "T23:59:59.999Z")
                .includeLower(true).includeUpper(true));

        SearchResponse scrollResp = esClient.prepareSearch("content-enrichment").setTypes("news")
                .setScroll(new TimeValue(60000)).setQuery(builder).setSize(200).setFetchSource(new String[]{"id", "contentModifiedDate"}, null).execute().actionGet();

        System.out.println("ELASTIC_SEARCH_QUERY:: Till " + endDateString
                + " 23:59:59 query = " );
        while (true) {
            SearchHit[] hits = scrollResp.getHits().getHits();
            if (hits != null && hits.length > 0) {
                for (SearchHit hit : hits) {
                    Object idObj = hit.getSource().get("id");
                    Object contentModifiedDateObj = hit.getSource().get("contentModifiedDate");
                    if (idObj != null) {

                        String id = idObj.toString();
                        if (contentModifiedDateObj != null) {
                            String contentModifiedDate = contentModifiedDateObj.toString();
                            if(checkValid(contentModifiedDate, endDateString))
                            {
                                System.out.println("id :: " + idObj.toString());
                                result.add(id);
                            }
                        }
                    }
                }
            }
            scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).
                    setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                System.out.println(" Completed fetching from ES : ");
                break;
            }
        }

        return result;
    }

    static boolean checkValid(String contentModifedDate, String limit)
    {
        String s1[] = contentModifedDate.split("\\s+");
        String z[] = s1[0].split("-");
        int x = Integer.parseInt(z[0]);
        if(x < 2018)
            return true;
        return false;
    }
}
