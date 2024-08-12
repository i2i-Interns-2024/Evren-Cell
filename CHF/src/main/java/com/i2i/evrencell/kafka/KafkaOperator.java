package com.i2i.evrencell.kafka;

import com.i2i.evrencell.packages.PackageDetails;
import com.i2i.evrencell.packages.PackageDetailsReader;
import com.i2i.evrencell.packages.UserDetails;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class KafkaOperator {

    private final PackageDetailsReader packageDetailsReader;
    private final VoltdbOperator voltOperation;
    private final KafkaProducer<String, String> producer;
    private final String usageTopic;
    private final String usageThresholdTopic;

    public KafkaOperator(PackageDetailsReader packageDetailsReader, VoltdbOperator voltOperation) {
        this.packageDetailsReader = packageDetailsReader;
        this.voltOperation = voltOperation;
        Properties kafkaProperties = loadKafkaProperties();

        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", kafkaProperties.getProperty("kafka.servers"));
        producerProps.put("client.id", kafkaProperties.getProperty("kafka.clientID"));
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(producerProps);
        this.usageTopic = kafkaProperties.getProperty("kafka.topic.balance_update");
        this.usageThresholdTopic = kafkaProperties.getProperty("kafka.topic.usage_threshold");
    }

    private Properties loadKafkaProperties() {
        Properties properties = new Properties();
        String configFilePath = "CHF/src/main/resources/kafka.conf";
        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading Kafka properties from " + configFilePath, e);
        }
        return properties;
    }

    public void sendKafkaUpdatedBalance(String type, String msisdn, int updatedBalance) {
        String message = String.format("Type: %s, MSISDN: %s, Updated Balance: %d", type, msisdn, updatedBalance);
        sendKafkaMessage(usageTopic, message);
    }

    public void sendKafkaUsageThresholdMessage(String type, String msisdn, String threshold) {
        UserDetails userDetails = voltOperation.getUserDetails(msisdn);
        int packageId = userDetails.getPackageId();
        PackageDetails packageDetails = packageDetailsReader.getPackageDetailsById(packageId);

        String message
                = "Type: " + type +
                ", MSISDN: " + msisdn +
                ", Threshold: " + threshold +
                ", Name: " + userDetails.getName() +
                ", Lastname: " + userDetails.getLastName() +
                ", Email: " + userDetails.getEmail() +
                ", Package: " + packageDetails.getName() +
                ", Price: " + packageDetails.getPrice() +
                ", Minutes: " + packageDetails.getVoiceAmount() +
                ", Data: " + packageDetails.getDataAmount() +
                ", SMS: " + packageDetails.getSmsAmount();
        sendKafkaMessage(usageThresholdTopic, message);
        System.out.println(message);
    }

    private void sendKafkaMessage(String topic, String message) {
        producer.send(new ProducerRecord<>(topic, message));
        producer.flush(); // Optional, to ensure the message is sent immediately
    }

    public void close() {
        producer.close();
    }

    public static void main(String[] args) {
        PackageDetailsReader packageDetailsReader = new PackageDetailsReader();
        VoltdbOperator voltdbOperator = new VoltdbOperator();
        KafkaOperator kafkaOperator = new KafkaOperator(packageDetailsReader, voltdbOperator);

        kafkaOperator.sendKafkaUsageThresholdMessage("data", "5551234567", "80%");
        System.out.println("Sent usage threshold message");


        kafkaOperator.close();
        voltdbOperator.close();
    }
}
