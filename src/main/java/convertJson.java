import com.google.gson.Gson;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class convertJson {

    public static void main(String args[]) throws JSONException, IOException, ParseException {
       /* String payload = "[[-0.027974722594700442,0.09485952134412715,-0.0212040918373294,-0.056184511536711314,0.005009630941153683,-0.07255176002780102,0.010090845135692986,0.006148825309992663,-0.0021463362011613974,0.054375725931050625,0.07980015637770399,-0.03414610594183276,0.10738171667688692,-0.14437466669589752,-0.03764165716842923,0.04698607951215896,-0.09692828542383244,-0.04505380022022956,-0.0665961034329849,0.08209122724018544,0.03967679832501402,0.1766929853559007,-0.007270235918429055,0.24927703540478896,-0.017195601736025364,-0.05811763875458205,0.04232957374347699,0.11211464529102008,0.054590041559383275,-0.020765102430004283,-0.04360944129442114,0.045081859998244156,0.12436725649423741,0.10812299794005642,0.037009971115397666,-0.07515699527374463,-0.0027880286016312753,0.027826432550184008,0.020456618524573502,-0.18869917864237884,0.06483722968937693,-0.038236804184032795,0.1313240914754393,0.039308106984430956,-0.021800531079552057,-0.1612717093275472,-0.03873517500271494,-0.05519700008948339,0.10253794443028892,-0.15831287957743004,-0.11968969650541046,0.01102261875000924,0.005929341166293394,-0.019196063973160475,0.05516628076515832,0.001256785915624613,0.032712835759700035,-0.023663618363116753,-0.08836689296259075,-0.06529455901517134,-0.06502394861364082,-0.1078144530215044,0.08119351459191143,-0.17110783797979282,0.07119757536630798,0.12104936921893714,0.06086590127222535,-0.15472547075014753,-0.023324773389893316,-0.08445403701773362,-0.07660662948788184,-0.0022593972303626125,0.01186462016083469,-0.22553773730474896,0.009864654633084035,0.008267338941203782,0.005899008649512599,-0.07254357488292075,-0.029998953353656228,0.003766589111085424,-0.1021041818136834,0.027555002382614262,-0.005856867356003514,-0.12772646999032455,-0.0651550486023575,0.09907418886205376,0.04173022151582486,-0.027554348447109685,0.02741387746874607,-0.005317130814449584,0.06693422250650949,0.0322243146490007,-0.1899489025825522,-0.021208182845330527,0.03094600840818471,-0.012094361939987636,0.012215527741015168,-0.11896587438154978,0.0423157972935887,0.14955667691338456,0.2476270653892782,0.03440873770443183,-0.07568488705647312,-0.08066607962537273,0.04806718195849174,0.008936403353190162,0.07881468495894012,0.09647927265579481,-0.0589163036404977,0.060038169109575136,-0.12390215503546037,-0.009592290495427609,0.0020893362570815026,-0.1290511493321613,0.06900656602266089,-0.019490466363096755,0.0686111759659986,-0.012201317159302661,0.040719202808090285,-0.0740919940336633,-0.01822555328461255,0.03505073653939204,0.08854394991155255,0.0026436543479472756,-0.11591633220009909,-0.10076450263065553,5.368672626391843E-4,0.03630042919078526,-0.02319593088666253,0.07830798193811112,-0.015009007239253675,0.004527847478144154,-0.06309211049351117,-0.04200029062145924,-0.006243298652696996,-0.08999477935709048,0.08306909548537936,0.1380393019810169,-0.07659788114495937,0.026949279759329023,-0.007170259661520534,-0.01906971675014452,0.031101160082182184,-0.0027574000147685637,-0.10598643733157993,0.02657365482507063,0.03078577856517322,0.07587999763156608,-0.19118387073401938,-0.09134262491173054]]";


        URL url = new URL("http://0.0.0.0:1123/getKNearestVectors?lang=hi");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(payload);
        out.flush();
        out.close();

        int res = connection.getResponseCode();

        System.out.println(res);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        System.out.println(response);
        JSONObject jsonObject = new JSONObject(response.toString());
        System.out.println(jsonObject);
        System.out.println(jsonObject.getString("ids"));

        Gson gson = new Gson();
        BuzzResponse buzzResponse = gson.fromJson(response.toString(), BuzzResponse.class);
        System.out.println( gson.toJson(buzzResponse.getDistance()) );

        in.close();
*/

        /*String _id = "id=" + "122007574" + "&lang=hi&date=" + "20190626";
        URL url = new URL("http://dh-ce-docvec-lsh-2.dailyhunt.in/doc2vec/getDocVec?" + _id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        //LOG.info("Status Code ::" + status);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject myResponse = new JSONObject(response.toString());
        String recievedDocVec = myResponse.getString("docvector");
        String replacedDocVec = recievedDocVec.replace('#',',');

        String payload = "[[" + replacedDocVec + "]]";
        System.out.println(payload);*/

        String str = "2019-08-12 01:33:43";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = df.parse(str);
        long epoch = date.getTime();
        System.out.println(epoch);
    }
}
