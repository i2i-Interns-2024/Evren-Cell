package com.sk.i2i.evrencell.mq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public interface KafkaConstants {
    Logger logger = LogManager.getLogger(KafkaConstants.class);
    Properties prop = new Properties();
    String fileName = "kafka.config";


    public static String getTopicName() {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            logger.error("Config file not found!");
        } catch (IOException ex) {
            logger.error("Config file couldn't open!");
        }
        return prop.getProperty("kafka.topic");
    }

    public static String getServers() {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            logger.error("Config file not found!");
        } catch (IOException ex) {
            logger.error("Config file couldn't open!");
        }
        return prop.getProperty("kafka.servers");
    }

    public static String getClientID() {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            logger.error("Config file not found!");
        } catch (IOException ex) {
            logger.error("Config file couldn't open!");
        }
        return prop.getProperty("kafka.clientID");
    }

    String topicName = getTopicName();
    String bootstrapServers = getServers();
    String clientID = getClientID();
    String autoOffsetReset = "earliest";
}