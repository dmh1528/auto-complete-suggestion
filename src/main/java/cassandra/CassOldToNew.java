package cassandra;

import com.datastax.driver.core.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CassOldToNew{

    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = sdfo.parse("2019-08-01");
        Date d2 = sdfo.parse("2019-08-20");

        new MultiThread(sdfo.parse("2019-08-01"), sdfo.parse("2019-08-05"));
        new MultiThread(sdfo.parse("2019-08-06"), sdfo.parse("2019-08-10"));
        new MultiThread(sdfo.parse("2019-08-11"), sdfo.parse("2019-08-15"));
        new MultiThread(sdfo.parse("2019-08-16"), sdfo.parse("2019-08-20"));
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }*/
        System.out.println("Main thread exiting.");
    }

}


 class MultiThread implements Runnable {

    Date d1;
    Date d2;
    Thread T;

    MultiThread(Date start, Date end)
    {
        d1 = start;
        d2 = end;
        T = new Thread(this);
        T.start();
    }

     @Override
     public void run() {

        try {
            Session session;
            Session session1;

            String old_node1 = "192.168.24.104";
            String old_node2 = "192.168.2.32";
            String old_node3 = "192.168.2.34";
            String old_node4 = "192.168.2.33";
            String old_node5 = "192.168.24.105";
            String old_keySpace = "content";

            int port = 9042;

            CassandraConnector3 cassandraConnector3 = new CassandraConnector3();
            cassandraConnector3.connect(old_node1, old_node2, old_node3, old_node4, old_node5, port, old_keySpace);
            session = cassandraConnector3.getSession();

            String new_node1 = "172.30.9.210";
            String new_node2 = "172.30.9.220";
            String new_node3 = "172.30.9.221";
            String new_keySpace = "enrichment_vectors";

            int port1 = 9042;

            CassandraConnector3 cassandraConnector31 = new CassandraConnector3();
            cassandraConnector31.connect(new_node1, new_node2, new_node3, new_node3, port1, new_keySpace);
            session1 = cassandraConnector31.getSession();

            String new_table = "ogc_news_text_docvecstore";

            SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");

            while (d1.compareTo(d2) < 0) {

                String xx = sdfo.format(d1);

                Calendar cal = Calendar.getInstance();
                cal.setTime(d1);

                String curr_date[] = xx.split("-");
                xx = String.join("", curr_date);

                System.out.println(d1);

                String version = "1_x_150";
                String lang[] = {"hi", "en", "te", "ta", "mr", "ml", "bn", "kn", "gu"};


                for (int i = 0; i < lang.length; ++i) {
                    String curr_lang = lang[i];
                    System.out.println(curr_lang + " " + xx);
                    Statement statement = new SimpleStatement("SELECT * FROM content_docvectorstore WHERE language='" + curr_lang + "' and item_date ='" + xx + "' and docvec_version = '" + version + "'").setFetchSize(1000);
                    ResultSet result = session.execute(statement);

                    Row x = result.one();
                    while (x != null) {
                        String language = x.getString(0);
                        String item_date = x.getString(1);
                        String docvec_version = x.getString(2);
                        long mysql_id = x.getInt(3);
                        String docvector = x.getString(4);
                        String initial_docvector = x.getString(5);
                        long provider_id = x.getInt(6);
                        System.out.println(mysql_id + " " + item_date);
                        /*PreparedStatement prepared1 = session1.prepare("SELECT * FROM ogc_news_text_docvecstore WHERE id =" + "?" + "ALLOW FILTERING");
                        BoundStatement bound1 = prepared1.bind(mysql_id);
                        ResultSet resultSet1 = session1.execute(bound1);

                        Row y = resultSet1.one();
*/
                        /*if (y == null) {
                            PreparedStatement prepared = session1.prepare("INSERT INTO " + new_table + " (language, item_date, docvec_version, id, docvector, initial_docvector, source_id)" + " values (?, ?, ?, ?, ?, ?, ?)");
                            BoundStatement bound = prepared.bind(language, item_date, docvec_version, mysql_id, docvector, initial_docvector, provider_id);
                            session1.execute(bound);
                        }*/
                        x = result.one();
                    }
                }


                cal.add(Calendar.DATE, 1);
                d1 = cal.getTime();
                Thread.sleep(1000);
            }

            session1.close();
            cassandraConnector31.close();

            session.close();
            cassandraConnector3.close();
        }catch (Exception e)
        {
            System.out.println("Error");
        }
    }

}

