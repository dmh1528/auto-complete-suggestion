package InterestGroupCassandra;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.gson.Gson;

public class InterestGroupTagger {

    private static final Logger LOG = LoggerFactory.getLogger(InterestGroupTagger.class);

    private String igPath="/home/himanshuk/Desktop/demo/src/main/resources/interest-group";

    private Gson gson = new Gson();

    private String igPath300="/home/himanshuk/Desktop/demo/src/main/resources/igs300";


    private Map<DocvecComputeVersion, Map<Language, Map<String, DocVector>>> igMap = new HashMap<>();
    private Map<DocvecComputeVersion, Map<Language, Map<String, Integer>>> igCountMap = new HashMap<>();

    private Map<DocvecComputeVersion, Map<Language, Map<String, String>>> igToIgUpdateMap = new HashMap<>();

    public void init(){

        File fileIgPath = new File(igPath);
        File fileIg300Path = new File(igPath300);

        Map<DocvecComputeVersion, Map<Language, Map<String, DocVector>>> tmpIgMap = new HashMap<>();
        Map<DocvecComputeVersion, Map<Language, Map<String, Integer>>> tmpIgCountMap = new HashMap<>();
        Map<DocvecComputeVersion, Map<Language, Map<String, String>>> tmpIgToIgUpdateMap = new HashMap<>();

        tmpIgMap.put(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150, new HashMap<>());
        tmpIgCountMap.put(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150, new HashMap<>());
        tmpIgToIgUpdateMap.put(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150, new HashMap<>());

        tmpIgMap.put(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300, new HashMap<>());
        tmpIgCountMap.put(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300, new HashMap<>());
        tmpIgToIgUpdateMap.put(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300, new HashMap<>());

        for(String igFiles:fileIgPath.list()){
            String lang = igFiles.split("\\.")[0];
            LOG.info(" lang init ::: " + lang);
            getIgMapConfig(Language.valueOf(lang), DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150, igPath, tmpIgMap, tmpIgCountMap, tmpIgToIgUpdateMap);
        }

        for(String ig300Files:fileIg300Path.list()){
            String lang = ig300Files.split("\\.")[0];
            getIgMapConfig(Language.valueOf(lang), DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300,igPath300, tmpIgMap, tmpIgCountMap, tmpIgToIgUpdateMap);
        }

        igMap=tmpIgMap;
        igCountMap=tmpIgCountMap;
        igToIgUpdateMap=tmpIgToIgUpdateMap;


    }

    private void getIgMapConfig(Language language, DocvecComputeVersion docvecComputeVersion, String path, Map<DocvecComputeVersion, Map<Language, Map<String, DocVector>>> tmpIgMap, Map<DocvecComputeVersion, Map<Language, Map<String, Integer>>> tmpIgCountMap, Map<DocvecComputeVersion, Map<Language, Map<String, String>>> tmpIgToIgUpdateMap){
        Map<Language, Map<String, DocVector>> igMapTmp = tmpIgMap.get(docvecComputeVersion);
        Map<Language, Map<String, Integer>> igCountMapTmp = tmpIgCountMap.get(docvecComputeVersion);
        Map<Language, Map<String, String>> igToIgUpdateMapTmp = tmpIgToIgUpdateMap.get(docvecComputeVersion);
        if(!igCountMapTmp.containsKey(language)){
            igCountMapTmp.put(language, new HashMap<>());
            igCountMapTmp.put(language, new HashMap<>());
            igToIgUpdateMapTmp.put(language, new HashMap<>());
        }

        if (!Language.ALL.equals(language)) {
            igMapTmp.put(language, new HashMap<String, DocVector>());
            Map<String, Double> thresholdMap = new HashMap<>();
            Map<String, Integer> countMap = new HashMap<>();
            igCountMapTmp.put(language, countMap);
            Map<String, String> igMappingMap = new HashMap<>();
            igToIgUpdateMapTmp.put(language, igMappingMap);
            if (StringUtils.isNotBlank(path)) {
                File file = new File(path + "/" + language.name() + ".tsv");
                if (file.exists()) {
                    LOG.info("Start :: reading interest group file for the language = " + language.name()
                            + " at location = " + path);
                    try {
                        Map<String, DocVector> docVectorMap = igMapTmp.get(language);
                        Scanner scan = new Scanner(file);
                        try {
                            scan.nextLine(); // to remove header of the file
                            while (scan.hasNext()) {
                                String line = scan.nextLine();
                                String[] values = line.split("\t");
                                List<Double> vectors = new ArrayList<>();
                                for (int i = 3; i < values.length; ++i) {
                                    vectors.add(Double.valueOf(values[i]));
                                }
                                String docId = values[0];
                                String groupId = docId.split("_")[2];
                                thresholdMap.put(groupId, Double.valueOf(values[1]));
                                countMap.put(groupId, Integer.valueOf(values[2]));
                                DocVector docVec = new DocVector(docId, vectors);
                                docVectorMap.put(docId, docVec);
                                System.out.print(" igMap ::: " + gson.toJson(igMap));
                            }
                        } catch (Exception e) {
                            LOG.error("Exception in caching interest group doc vectors", e);
                        }
                        scan.close();
                        LOG.info("SUCCESS :: reading interest group file for language = " + language.name()
                                + " at location = " + path);
                    } catch (FileNotFoundException e) {
                        LOG.error("Exception while reading interest group file for language = " + language.name()
                                + " at location = " + path, e);
                    }
                }

                File mappingFile = new File(path + "/" + language.name() + "_mapping" + ".tsv");
                if (mappingFile.exists()) {
                    try {
                        Scanner scan = new Scanner(mappingFile);
                        try {
                            scan.nextLine(); // to remove header of the file
                            while (scan.hasNext()) {
                                String line = scan.nextLine();
                                String[] values = line.split("\t");
                                if (values.length > 1) {
                                    igMappingMap.put(values[0], values[1]);
                                }
                            }
                        } catch (Exception e) {
                            LOG.error("Exception in caching interest group doc vectors", e);
                        }
                        scan.close();
                    } catch (FileNotFoundException e) {
                        LOG.error("Exception while reading interest group file for language = " + language.name()
                                + " at location = " + path, e);
                    }
                }
            } else {
                LOG.error("interest group file not found int the path specified = " + path + " for language = "
                        + language);
            }

        }

        igCountMap.put(docvecComputeVersion,igCountMapTmp);
        igMap.put(docvecComputeVersion,igMapTmp);
        igToIgUpdateMap.put(docvecComputeVersion,igToIgUpdateMapTmp);

        LOG.info(" igCountMap ::: " + igCountMap.size());
        LOG.info(" igMap ::: " + igMap.size());
        LOG.info(" igToIgUpdateMap ::: " + igToIgUpdateMap.size());
    }

    public boolean languageSupportedBy300(String id, Language language){
        LOG.info(" NewsId ::: " + id + " Eligible Languages for 300 Dim docvecs ::: "+ igMap.get(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300).keySet() + "  language requestedFor ::: " + language  +" ::: " + igMap.get(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300).containsKey(language) );
        return igMap.get(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300).containsKey(language);
    }

    public Map<String, Double> tagInterestGroup(NewsItem newsItem, DocvecComputeVersion docvecComputeVersion) {
        if (docvecComputeVersion.getDocvecComputeVersion().equalsIgnoreCase(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150.getDocvecComputeVersion()) && newsItem.getDocVector() == null) {
            return new HashMap<>();
        }

        if (docvecComputeVersion.getDocvecComputeVersion().equalsIgnoreCase(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300.getDocvecComputeVersion()) && newsItem.getDocVector300Dim() == null) {
            return new HashMap<>();
        }

        LOG.info("geting interest group for " + newsItem.getId());
        LOG.info("docvecComputeVersion. getDocvecComputeVersion()" +  docvecComputeVersion.getDocvecComputeVersion() +"id::  " + newsItem.getId() + "   docvecComputeVersion. getDocvecComputeVersion()" +  docvecComputeVersion.getDocvecComputeVersion());
        return (docvecComputeVersion.getDocvecComputeVersion().equals(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150.getDocvecComputeVersion()))?getInterestGroup(newsItem.getLanguage(), newsItem.getDocVector(), docvecComputeVersion):getInterestGroup(newsItem.getLanguage(), newsItem.getDocVector300Dim(), docvecComputeVersion);
    }


    public Map<String, Double> getInterestGroup(Language language, List<Double> docVector, DocvecComputeVersion docvecComputeVersion) {
        LOG.info("get interest group for language :::" + language.getLanguage() + "DocvecComputeversion :::" + docvecComputeVersion.getDocvecComputeVersion() + " vectors size ::: " + docVector.size());
        Map<String, Double> mappedIG = new HashMap<>();

        Map<Language, Map<String, DocVector>> igMapTmp = igMap.get(docvecComputeVersion);
        Map<Language, Map<String, Integer>> igCountMapTmp = igCountMap.get(docvecComputeVersion);
        Map<Language, Map<String, String>> igToIgUpdateMapTmp = igToIgUpdateMap.get(docvecComputeVersion);


        LOG.info(" igCountMaptmp ::: " + igCountMapTmp.size());
        LOG.info(" igMaptmp ::: " + igMapTmp.size());
        LOG.info(" igToIgUpdateMaptmp ::: " + igToIgUpdateMapTmp.size());

        LOG.info( " igMapTmp.keySet() ::: " + igMapTmp.keySet() + "language :::" + language.getLanguage());

        Map<String, DocVector> igList = igMapTmp.get(language);

        LOG.info(" igList ::: " + igList.size());

        Map<String, MinMaxPriorityQueue<InterestGroupItem>> priorityQueues = new HashMap<>();
        if (igList != null && igList.size() > 0) {
            LOG.info("igList not empty");
            for (Entry<String, Integer> entry : igCountMapTmp.get(language).entrySet()) {
                MinMaxPriorityQueue<InterestGroupItem> igPrirityQueue = MinMaxPriorityQueue
                        .maximumSize(entry.getValue()).create();
                priorityQueues.put(entry.getKey(), igPrirityQueue);
            }
            Map<String, String> igUpdateMap = igToIgUpdateMapTmp.get(language);
            Set<String> alreadyProcessedIG = new HashSet<>();

            /*
             * creating new hash map to avoid ConcurrentModificationException while updating the list.
             *
             */

            igList = new HashMap<>(igList);
            for (Entry<String, DocVector> entry : igList.entrySet()) {
                try {

                    DocVector docVec = entry.getValue();
                    String updatedIg = igUpdateMap.get(entry.getKey());
                    if (updatedIg != null) {
                        DocVector updatedDocVec = igList.get(updatedIg);
                        if (updatedDocVec != null) {
                            docVec = updatedDocVec;
                        }
                    }
                    if (!alreadyProcessedIG.contains(docVec.getId())) {
                        alreadyProcessedIG.add(docVec.getId());
                        String groupId = docVec.getId().split("_")[2];
                        Double distance = RelatedNewsUtils.calculateEuclideanDistanceBetweenDocVectors(docVector,
                                docVec.getVector());

                        MinMaxPriorityQueue<InterestGroupItem> igPriorityQueue = priorityQueues.get(groupId);
                        if (igPriorityQueue != null) {
                            Double score = 1.0 - distance;
                            igPriorityQueue.add(new InterestGroupItem(docVec.getId(), score));
                        }
                    }
                } catch (Exception e) {
                    LOG.error("exception in calculating distance from centroid for ig = " + entry.getKey(), e);
                }
            }

            for (Entry<String, MinMaxPriorityQueue<InterestGroupItem>> entry : priorityQueues.entrySet()) {
                if (entry.getValue().size() > 0) {
                    for (InterestGroupItem item : entry.getValue()) {
                        mappedIG.put(item.getId(), item.getScore());
                    }
                }
            }
        }
        LOG.info("intrest group size " + mappedIG.size());
        return mappedIG;
    }

}