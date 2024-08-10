package com.sk.i2i.evrencell.mq;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.util.Collections;
import java.util.Properties;

public class SubscriberUsageRecord {

    public static Consumer<String, UsageRecord> createConsumer() {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "159.89.229.126:9093");

        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroup");

        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "asd-kafka-1");

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());

        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.sk.i2i.evrencell.mq.model.UsageRecordDeserializer");

        Consumer<String, UsageRecord> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Collections.singletonList("UsageRecordTopic"));

        return consumer;
    }
}
