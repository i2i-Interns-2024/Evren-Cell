package org.i2i.hazelcast.utils;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.i2i.hazelcast.utils.configurations.Configuration;



public class HazelcastManager {
    private static final ClientConfig config = Configuration.getConfig();
    private static final HazelcastInstance hazelcast = HazelcastClient.newHazelcastClient(config);

    public static HazelcastInstance getInstance() {
        return hazelcast;
    }

    public static void shutdown() {
        hazelcast.shutdown();
    }
}


