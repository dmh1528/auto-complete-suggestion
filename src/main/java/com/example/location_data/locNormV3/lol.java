package com.example.location_data.locNormV3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class lol {
    public static void main(String args[]) throws JSONException {
        List<JSONObject> list = new ArrayList<JSONObject>();
        String string = "[{\"countType\":\"fbReactionCount\",\"count\":0.0,\"lastUpdated\":1557403039591,\"score\":0.0},{\"countType\":\"fbCommentCount\",\"count\":0.0,\"lastUpdated\":1557403039591,\"score\":0.0},{\"countType\":\"fbShareCount\",\"count\":0.0,\"lastUpdated\":1557403039591,\"score\":0.0}]";
        try {
            int i;
            JSONArray array = new JSONArray(string);
            for (i = 0; i < array.length(); i++)
                list.add(array.getJSONObject(i));
        } catch (
                JSONException e) { ;
        }
        String res = "";
        for(int i = 0; i < list.size(); ++i) {
            JSONObject o1 = list.get(i);
            String x = o1.get("countType").toString();
            String y = o1.get("count").toString();

                res = res + x + "_" + y + ",";
        }
        if(!res.equals(""))
        {
            res = res.substring(0, res.length() - 1);
        }
        System.out.println(res);


    }
}

