package com.i2i.aom.configuration;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;

public class HazelcastConfiguration {


    public static ClientConfig getConfig(){
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().addAddress("51.120.121.91:5701");
        config.getConnectionStrategyConfig()
                .setReconnectMode(ClientConnectionStrategyConfig.ReconnectMode.ON);
        return config;
    }
}
