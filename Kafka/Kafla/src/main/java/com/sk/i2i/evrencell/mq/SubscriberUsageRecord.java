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

        // Kafka sunucuları
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "159.89.229.126:9093");

        // Tüketici grubu
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroup");

        // İstemci kimliği
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "asd-kafka-1");

        // Anahtar deserializer
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());

        // Değer deserializer (UsageRecord için)
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.sk.i2i.evrencell.mq.model.UsageRecordDeserializer");

        // KafkaConsumer nesnesi oluşturuluyor
        Consumer<String, UsageRecord> consumer = new KafkaConsumer<>(properties);

        // Kafka konusuna abone olunuyor
        consumer.subscribe(Collections.singletonList("UsageRecordTopic"));

        return consumer;
    }
}
