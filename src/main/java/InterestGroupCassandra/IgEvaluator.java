package InterestGroupCassandra;

import com.google.gson.Gson;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class IgEvaluator {

    private static Gson gson = new Gson();

    private static Map<String, Map<String,Double>> ig150 = new HashMap<>();

    private static void init(){
        try (Scanner sc = new Scanner(new File("/home/himanshuk/Desktop/demo/src/main/resources/docvec150"))){
            while (sc.hasNext()){
                String line = sc.nextLine();
                if(!StringUtils.isEmpty(line)){
                    String records[] = line.split(" ");
                    String id = records[0];
                    String[] docvecStr = records[3].split("#");
                    List<Double> docvec = new ArrayList<>();
                    for(String doc:docvecStr) {
                        docvec.add(Double.parseDouble(doc));
                    }
                    InterestGroupTagger interestGroupTagger = new InterestGroupTagger();
                    interestGroupTagger.init();
                    interestGroupTagger.getInterestGroup(Language.TAMIL, docvec,DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){

        init();

    }



}