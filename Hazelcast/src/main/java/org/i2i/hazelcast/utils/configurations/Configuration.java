package org.i2i.hazelcast.utils.configurations;

import com.hazelcast.client.config.ClientConfig;
import org.i2i.hazelcast.utils.constants.StringConstants;

public class Configuration {
    public static ClientConfig getConfig(){
        ClientConfig config = new ClientConfig();
        config.setProperty("hazelcast.logging.type", "slf4j");
        config.getNetworkConfig().addAddress(StringConstants.hazelcastUrl);
        return config;
    }
}
