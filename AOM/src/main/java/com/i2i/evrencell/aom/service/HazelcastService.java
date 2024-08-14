//package com.i2i.evrencell.aom.service;
//
//import com.hazelcast.client.HazelcastClient;
//import com.hazelcast.client.config.ClientConfig;
//import com.hazelcast.core.HazelcastInstance;
//import com.hazelcast.map.IMap;
//import com.i2i.evrencell.aom.configuration.HazelcastConfiguration;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import org.springframework.stereotype.Service;
//
//@Service
//public class HazelcastService {
//
//    private static HazelcastInstance hazelcast;
//
//    /**
//     * Initialize hazelcast client
//     */
//    @PostConstruct
//    public void init() {
//        ClientConfig config = HazelcastConfiguration.getConfig();
//        hazelcast = HazelcastClient.newHazelcastClient(config);
//    }
//
//    /**
//     * Destroy hazelcast client
//     */
//    @PreDestroy
//    public void destroy() {
//        if (hazelcast != null) {
//            hazelcast.shutdown();
//        }
//    }
//
//    /**
//     * Put key value pair to hazelcast map
//     *
//     * @param key   key
//     * @param value value
//     * @return result message
//     */
//    public String put(String key, String value) {
//        try {
//            IMap<Object, Object> map = hazelcast.getMap("evrencell");
//            map.put(key, value);
//            return "Put operation is successful";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Put operation is not successful";
//        }
//    }
//}