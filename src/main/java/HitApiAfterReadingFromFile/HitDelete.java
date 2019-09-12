package HitApiAfterReadingFromFile;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HitDelete {
/*
    private static void sendGet(String id) throws Exception {

        try {
            String _id = id + "/delete";
            URL url = new URL("http://192.168.25.56:7070/node/" + _id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            System.out.println(status);
            if (status != 200) {
                System.out.println("Status code not 200 for id ::" + id);
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            System.out.println(content);
            in.close();
        } catch (Exception e) {
            System.out.println("Error with id ::" + id);
        }

    }

    public static String s;

    public static String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }*/

    public static void main(String[] args) throws Exception {

        /*File file = new File("/home/himanshuk/Downloads/incative-topic-list.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        int flag = 0;

        while((st = br.readLine()) != null)
        {
            if(flag == 1)
            {
                String[] split = st.split("\\s+");

                if(split[0].compareTo("11536") == 0) {
                    ;
                }
                else {
                  sendGet(split[0]);
                }
            }

            flag = 1;
        }
*/
/*
        String x = "43200";
        String s = String.valueOf(TimeUnit.SECONDS.toMinutes(Long.parseLong(x)));
        *//*Map<String, Map<String, String> > mp = new HashMap<>();
        mp.put("himanshu", new HashMap<>());
        Map<String, String> mp1 = mp.get("himanshu");
        mp1.put("lol", "lol");

        System.out.println(mp.get("himanshu"));
        Map<String, String>mp2 = new HashMap<>();
        mp2.put("aa", "aa");
        mp.put("himanshu", mp2);

        System.out.println(mp.get("himanshu"));
        *//*System.out.println(s);
        String sx = "*/

       /* DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        System.out.println(LocalDate.now());*/
       System.out.println(checker.ENGLISH);




    }
}
