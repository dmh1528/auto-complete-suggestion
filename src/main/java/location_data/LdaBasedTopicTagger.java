package location_data;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LdaBasedTopicTagger {

    private static Map<String, String> ldaTopicCatMap = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(LdaBasedTopicTagger.class);

    private Gson gson = new Gson();

    private static String ldaNpcatConfigFile = "/home/himanshuk/Downloads/HI_npCategory_LDA_mapping - Sheet1.tsv";

    public static void main(String[] args) {


        if(StringUtils.isNotEmpty(ldaNpcatConfigFile)) {
            try (Scanner sc = new Scanner(new File(ldaNpcatConfigFile))) {
                sc.nextLine();
                while (sc.hasNext()) {

                    String line = sc.nextLine();

                    if (StringUtils.isNotEmpty(line)) {

                        String[] records = line.split("\t");

                        if (records.length > 0) {
                            ldaTopicCatMap.put(records[1], records[3]);

                        }

                    }

                }

            } catch (Exception e) {

                LOG.error(" Exception while initializing LdaNerTagger configs ::: ", e);

            }
        }
        tagLdaBasedTopics();


    }

    public static void tagLdaBasedTopics(){
        List<LdaResult> lda = new ArrayList<>();
        LdaResult o1 = new LdaResult();
        o1.setTopic("HI_L_128_74");
        o1.setScore(0.0);
        lda.add(o1);
        if(!CollectionUtils.isEmpty(lda)) {
            Set<String> ldaTopicsSet = lda.stream().map(data -> data.getTopic()).collect(Collectors.toSet());
            //LOG.info(" ldaTopicsSet " + gson.toJson(ldaTopicsSet) + " for NewsItem ::: " + newsItem.getId() + " language ::: " + newsItem.getLanguage());
            Set<String> lda1 =  ldaTopicsSet.stream().filter(data-> ldaTopicCatMap.containsKey(data)).map(data->ldaTopicCatMap.get(data)).collect(Collectors.toSet());
            if(lda1.size() == 1)
            {
                System.out.println(Integer.parseInt(lda1.iterator().next()));
            }
            //LOG.info("tagged NpCat of item ::: " + newsItem.getId() + " language ::: " + newsItem.getLanguage() + " is :::  " + gson.toJson(lda));
            //return lda;
        }
        //return null;
    }

}
