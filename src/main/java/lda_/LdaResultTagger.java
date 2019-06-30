package lda_;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.example.location_data.Language;
import com.example.location_data.NewsItem;
import lda_.LdaResult;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Component
public class LdaResultTagger {

    private String ldaConfigString="http://192.168.3.214:9880/ldalanguagetopics#hi,en,te,ta";

    private String url;

    private static List<String> languages = new ArrayList<>();

    private static Map<String, String> urlMap = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(LdaResultTagger.class);

    private RestTemplate restTemplate = new RestTemplate();

    private static int urlFailedCount = 3;

    private static class MyResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            LOG.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
        }

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            HttpStatus.Series series = response.getStatusCode().series();
            return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
        }
    }

    public void init() {
        LOG.info( " lda Configs " + ldaConfigString);
        restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setErrorHandler(new LdaResultTagger.MyResponseErrorHandler());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        try {
            url = ldaConfigString.split("#")[0];
            String[] lang = ldaConfigString.split("#")[1].split(",");
            for(String language : lang){
                languages.add(Language.getLanguage(language).toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(15000);
        factory.setReadTimeout(15000);
        factory.setConnectTimeout(15000);
        return factory;
    }

    public boolean isElegibleForTagging(String lang){
        return languages.contains(lang);
    }

    public  List<String> getLanguages(){
        return languages;
    }

    public List<LdaResult> tagLdaResult(NewsItem newsItem) throws JSONException {
        String result = null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", newsItem.getText());
        jsonObject.put("language", Language.valueOf(newsItem.getLang()).getLanguage());
        LOG.info(" request body for getting ldaResult ::: " + jsonObject.toString());
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < urlFailedCount; i++) {
            try {
                result = getResponseFromClient(newsItem.getId(), url, jsonObject.toString());
                LOG.info(" ldaResult result from client ::: " + result + " for newsItem ::: ");
                long time2 = System.currentTimeMillis();
                if (StringUtils.isNotEmpty(result)) {
                    List<LdaResult> ldaResults = refactorResponseString(result, newsItem);
                    LOG.info(" result from api of ldaResult ::: " + result + " time taken ::: " + (time2 - time1) + " milis for news item " + newsItem.getId() + " lad17Status ::: " + newsItem.getLda17());
                    return ldaResults;
                }
                LOG.error(" Failed Retrying " + i + " for getting ldaResult result " + " time taken ::: " + (time2 - time1) + " milis for news item " + newsItem.getId());
            } catch (Exception e) {
                LOG.error("Error while get LDAResult:",e);
            }
        }
        return null;
    }

    private List<LdaResult> refactorResponseString(String response, NewsItem newsItem){
        response = response.trim();
        Gson gson = new Gson();
        System.out.println( "  our response :::: ");
        List<LdaResult> ldaResults = gson.fromJson(response, (new TypeToken<ArrayList<LdaResult>>(){}.getType()));
        if(!CollectionUtils.isEmpty(ldaResults)){
            for(LdaResult ldaResult : ldaResults){
                /*if( ldaResult.getTopic() == 17){
                    newsItem.setLda17("POS");
                }*/
                ;
            }
        }
        return ldaResults;
    }

    private String getResponseFromClient(String id, String url, String jsonString ) {
        LOG.info(" Inside Response from Client for ldaResult ::: url " + url + " for newsItem ::: " + id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String res = response.getBody();
        LOG.info( " Response of ldaResult ::: " + res + " for newsItem ::: " + id);
        if(StringUtils.isEmpty(res)) {
            LOG.info(" Response body is null for ldaResult for newsItem ::: " + id);
            if(response!=null) {
                LOG.info("ldaResult Search response for news id for newsItem ::: " + id);
                if(response.getStatusCode()==HttpStatus.TOO_MANY_REQUESTS) {
                    LOG.info("Too many requests. Retry count: for newsItem ::: " + id);
                }
            }
            return null;
        }
        LOG.info(" Response result after hitting api ::: "+ res + " for newsItem ::: " + id);
        return res;
    }
}
