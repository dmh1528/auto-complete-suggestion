package EStoCassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

public class CreatorUniqueSequenceGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(CreatorUniqueSequenceGenerator.class);


    private int TOTAL_BITS = 64;
    private int EPOCH_BITS = 42;
    private static int NODE_ID_BITS = 10;
    private int SEQUENCE_BITS = 12;

    private static int maxNodeId = (int)(Math.pow(2, NODE_ID_BITS) - 1);
    private int maxSequence = (int)(Math.pow(2, SEQUENCE_BITS) - 1);

    private long CUSTOM_EPOCH = 1420070400000L;

    private static StringBuilder sb;

    private static int nodeId;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    public static void main(String[] args) {
        try {
            LOG.info(" init pg  CreatorUniqueSequenceGenerator ::: ");
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            sb = new StringBuilder();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for(int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & maxNodeId;
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if(currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if(sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        long id = currentTimestamp << (TOTAL_BITS - EPOCH_BITS);
        id |= (nodeId << (TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS));
        id |= sequence;
        return id;
    }


    private long timestamp() {
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

}
