package com.sk.i2i.evrencell.mq;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class SubscriberBalance {

    public static Consumer<String, Balance> createConsumer() {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "159.89.229.126:9093");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroup");
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "asd-kafka-1");

        // Anahtar (Key) deserializer'ını düzeltin:
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Değer (Value) deserializer'ı zaten doğru:
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.sk.i2i.evrencell.mq.BalanceMessageDeserializer");

        // Doğru Consumer türünü oluşturun:
        Consumer<String, Balance> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Collections.singletonList("BalanceTopic"));

        return consumer;
    }
}
