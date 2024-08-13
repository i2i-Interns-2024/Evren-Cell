package org.sk.i2i.evren.TGF.util;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.List;


public class HazelcastOperations {

    private final Config config = ConfigFactory.load("application.conf");
    private final ClientConfig clientConfig = new ClientConfig();

    public HazelcastOperations() {

        clientConfig.setClusterName(config.getString("hazelcast.cluster-name"));
        clientConfig.getNetworkConfig().addAddress(config.getString("hazelcast.network.address"));
        clientConfig.setProperty("hazelcast.logging.type", "slf4j");
    }

    public List<String> getAllMsisdn() {

        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        IMap<String, Integer> map = client.getMap(config.getString("hazelcast.map.name"));

        List<String> msisdnSet = new ArrayList<>(map.keySet());

//        for (String msisdn : msisdnSet) {
//            System.out.println(msisdn);
//        }

        client.shutdown();
        System.out.println("msisdn list was updated...");

        return msisdnSet;
    }


}
