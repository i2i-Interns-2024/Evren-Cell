package com.ikbalci.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.logging.Logger;

public class KafkaOperator {

    private static final Logger logger = Logger.getLogger(KafkaOperator.class.getName());
    private KafkaProducer<String, String> producer;

    public KafkaOperator() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        logger.info("KafkaProducer initialized");
    }

    public void sendKafkaUsageMessage(String type, String msisdn, long userId, int usageAmount) {
        String topic = "usage_topic";
        String message = String.format("Type: %s, MSISDN: %s, UserID: %d, UsageAmount: %d", type, msisdn, userId, usageAmount);
        producer.send(new ProducerRecord<>(topic, msisdn, message));
        logger.info("Kafka message sent: " + message);
    }
}