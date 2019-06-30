package InterestGroupCassandra;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Map;

public class checker {
    public static void main(String[] args) {

        Map<Integer, Map<String, String>> mp = new HashMap<>();
        Map<Integer, Map<String, String>> mp1 = new HashMap<>();
        mp = mp1;
        mp1.put(1,new HashMap<>());
        Map<String, String>lol = mp1.get(1);
        lol.put("1","1");

        System.out.println("lol :::" + mp.get(1).get("1"));
    }
}
