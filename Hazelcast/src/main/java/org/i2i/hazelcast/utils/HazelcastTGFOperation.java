package org.i2i.hazelcast.utils;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.i2i.hazelcast.utils.configurations.Configuration;
import org.i2i.hazelcast.utils.constants.StringConstants;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HazelcastTGFOperation {
    private static final ClientConfig config = Configuration.getConfig();
    private static final HazelcastInstance hazelcast = HazelcastClient.newHazelcastClient(config);

    public static String getAllMsisdn(String key) {
        try {
            IMap<Object, Object> map = hazelcast.getMap(StringConstants.mapName);

            // If a key is given, it returns the value of that key
            if (key != null && !key.isEmpty()) {
                if (map.containsKey(key)) {
                    String value = map.get(key).toString();
                    return value;
                } else {
                    return "Not found key";
                }
            } else {
                // If the key is not given, it returns all key-value pairs
                Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
                String allEntries = entrySet.stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining(", "));

                return allEntries;
            }
        } catch (Exception e) {
            return e.toString();
        }/*finally {
            hazelcast.shutdown();
        }*/
    }
}







//package org.i2i.hazelcast.utils;
//
//import com.hazelcast.client.HazelcastClient;
//import com.hazelcast.client.config.ClientConfig;
//import com.hazelcast.core.HazelcastInstance;
//
//import com.hazelcast.map.IMap;
//import org.i2i.hazelcast.utils.configurations.Configuration;
//import org.i2i.hazelcast.utils.constants.StringConstants;
//
//
//public class HazelcastTGFOperation {
//    private static final ClientConfig config = Configuration.getConfig();
//    private static final HazelcastInstance hazelcast = HazelcastClient.newHazelcastClient(config);
//
//   public static String getAllMsisdn(String key) {
//       try {
//           IMap<Object, Object> map = hazelcast.getMap(StringConstants.mapName);
//
//           if (map.containsKey(key)) {
//               String value = map.get(key).toString();
//               return value;
//           } else {
//               return "Not found key";
//           }
//       } catch (Exception e) {
//           return e.toString();
//       }/*finally {
//            hazelcast.shutdown();
//        }*/
//
//   }
//}


