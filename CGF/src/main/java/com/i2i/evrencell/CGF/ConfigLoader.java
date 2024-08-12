package com.i2i.evrencell.CGF;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    public ConfigLoader() {
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    static {
        try {
            InputStream input = com.i2i.evrencell.kafka.ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties");

            try {
                if (input == null) {
                    throw new RuntimeException("Sorry, unable to find application.properties");
                }

                properties.load(input);
            } catch (Throwable var4) {
                if (input != null) {
                    try {
                        input.close();
                    } catch (Throwable var3) {
                        var4.addSuppressed(var3);
                    }
                }

                throw var4;
            }

            if (input != null) {
                input.close();
            }

        } catch (IOException var5) {
            IOException ex = var5;
            throw new RuntimeException("Error loading properties file", ex);
        }
    }
}
