package com.example.location_data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BitWiseIndexMap {

    private Map<Long, Integer> indexMap = new HashMap<>();

    @JsonIgnore
    private transient int max = Integer.bitCount(Integer.MAX_VALUE) + 1;

    public BitWiseIndexMap getIntersection(BitWiseIndexMap map) {
        BitWiseIndexMap iMap = new BitWiseIndexMap();
        for(Entry<Long, Integer> entry: map.getIndexMap().entrySet()) {
            if(indexMap.get(entry.getKey()) != null) {
                iMap.getIndexMap().put(entry.getKey(), indexMap.get(entry.getKey()) & entry.getValue());
            }
        }
        return iMap;
    }

    public int getWordCount() {
        int count = 0;
        for(Entry<Long, Integer> entry: indexMap.entrySet()) {
            count += Integer.bitCount(entry.getValue());
        }
        return count;
    }

    public int getWordIntersection(BitWiseIndexMap map) {
        int count = 0;
        for(Entry<Long, Integer> entry: indexMap.entrySet()) {
            count += Integer.bitCount(entry.getValue() & map.getIndexRep(entry.getKey()));
        }
        return count;
    }

    private int getIndexRep(Long index) {
        if(indexMap.containsKey(index)) {
            return indexMap.get(index);
        }
        return 0;
    }

    public int getWordUnion(BitWiseIndexMap map) {
        int count = 0;
        count = map.getWordCount() + getWordCount() - getWordIntersection(map);
        return count;
    }

    public void addToken(long index) {
        long pos = (long)index/max;
        Integer val = indexMap.get(pos);
        if(val == null) {
            val = Integer.valueOf(0);
        }
        indexMap.put(pos, (1 << (index % max)) | val);
    }

    public void addTokenFromMap(BitWiseIndexMap map) {
        for(Entry<Long, Integer> entry: map.indexMap.entrySet()) {
            Integer val = indexMap.get(entry.getKey());
            if(val == null) {
                val = Integer.valueOf(0);
            }
            val = val | entry.getValue();
            indexMap.put(entry.getKey(), val);
        }
    }

    public void removeCommonTokenFromMap(BitWiseIndexMap map) {
        for(Entry<Long, Integer> entry: map.indexMap.entrySet()) {
            Integer val = indexMap.get(entry.getKey());
            if(val != null) {
                int a = entry.getValue();
                val = ((a ^ val)&a)^(a^val);

                indexMap.put(entry.getKey(), val);
            }
        }
    }

    public Double getCosineSimilarity(BitWiseIndexMap map) {
        double divisor = Math.pow(getWordCount() * map.getWordCount(), 0.5);
        if(divisor == 0d){
            return 0d;
        }else{
            return (double)getWordIntersection(map)/divisor;
        }
    }

    public Double getJaccardCoefficient(BitWiseIndexMap map) {
        return (double)getWordIntersection(map)/getWordUnion(map);
    }

    public String convertToString() {
        StringBuilder str = new StringBuilder();
        for(Entry<Long, Integer> entry: indexMap.entrySet()) {
            if(str.length() > 0) {
                str.append(",");
            }
            str.append(entry.getKey()).append(":").append(entry.getValue());

        }
        return str.toString();
    }

    public void setStringAsMap(String mapString) {
        String[] indexArray = mapString.split(",");
        Map<Long, Integer> indexMapToSet = new HashMap<>();
        for(String iArr: indexArray) {
            if(StringUtils.isNotBlank(iArr) && iArr.contains(":")) {
                String[] index = iArr.split(":");
                Integer indexVal = Integer.valueOf(index[1]);
                indexMapToSet.put(Long.valueOf(index[0]), indexVal);
            }
        }
        indexMap = indexMapToSet;
    }

    public Map<Long, Integer> getIndexMap() {

        return indexMap;
    }
}

