package Kafka;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

public class CassandraConnector2
{
    private static Cluster cluster;

    private static Session session;


    public static void connect(String node1, String node2, String node3, String node4, String node5, int port, String keySpace)
    {
        cluster = Cluster.builder().addContactPoints(node1, node2, node3, node4, node5).withPort(port).withCredentials("cassandra", "cassandra").withoutJMXReporting().build();
        session = cluster.connect(keySpace);
    }

    public static void connect(String node1, String node2, String node3, int port, String keySpace)
    {
        cluster = Cluster.builder().addContactPoints(node1, node2, node3).withPort(port).withoutJMXReporting().build();
        session = cluster.connect(keySpace);
    }

    public void connectKeyspace(String node, int port, String keyspace) {
        this.cluster = Cluster.builder().addContactPoints(node).withPort(port).withCredentials("cassandra","cassandra").build();
        final Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (final Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect(keyspace);
    }

    public static Session getSession()
    {
        return session;
    }

    public Cluster getCluster()
    {
        return this.cluster;
    }

    public void close()
    {
        cluster.close();
    }
}
