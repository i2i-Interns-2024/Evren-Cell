package org.i2iSystems;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file", ex);
        }
    }

    public static String getProperty(String key) {
        String envValue = System.getenv(key.toUpperCase().replace(".", "_"));
        if (envValue != null) {
            return envValue;
        }
        return properties.getProperty(key);
    }
}