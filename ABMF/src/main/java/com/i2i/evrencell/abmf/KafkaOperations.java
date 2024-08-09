package com.i2i.evrencell.abmf;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaOperations {

    private KafkaConsumer<String, BalanceMessage> consumer;

    public KafkaOperations(Properties props, String topic) {
        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public ConsumerRecords<String, BalanceMessage> pollMessage() {
        return this.consumer.poll(Duration.ofMillis(100));
    }

    public KafkaConsumer<String, BalanceMessage> getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumer<String, BalanceMessage> consumer) {
        this.consumer = consumer;
    }
}
