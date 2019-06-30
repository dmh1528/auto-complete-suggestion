package HitApiAfterReadingFromFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HitDelete {

    private static void sendGet(String id) throws Exception {

        try {
            String _id = id + "/delete";
            URL url = new URL("http://192.168.25.56:7070/node/" + _id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            System.out.println(status);
            if(status != 200)
            {
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
        }
        catch (Exception e)
        {
            System.out.println("Error with id ::" + id);
        }

    }
    public static void main(String[] args) throws Exception {

        File file = new File("/home/himanshuk/Downloads/incative-topic-list.txt");

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


    }

}
