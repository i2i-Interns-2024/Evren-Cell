package com.i2i.evrencell.kafka;

import com.i2i.evrencell.kafka.deserializer.BalanceMessageDeserializer;
import com.i2i.evrencell.kafka.deserializer.GenericMessageDeserializer;
import com.i2i.evrencell.kafka.deserializer.NotificationMessageDeserializer;
import com.i2i.evrencell.kafka.deserializer.UsageRecordMessageDeserializer;
import com.i2i.evrencell.kafka.message.BalanceMessage;
import com.i2i.evrencell.kafka.message.Message;
import com.i2i.evrencell.kafka.message.NotificationMessage;
import com.i2i.evrencell.kafka.message.UsageRecordMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Subscriber <T extends Message>{
    Consumer<String, T> consumer;

    public Consumer<String, T> createConsumer(String className, String topicName, String consumerGroup) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigLoader.getProperty("kafka.url"));
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, className);

        Consumer<String, T> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topicName));

        return consumer;
    }

    public void createBalanceMessageConsumer() {
        consumer = createConsumer(BalanceMessageDeserializer.class.getName(), KafkaTopicConstants.BALANCE_TOPIC, "balanceConsumerGroup");
    }

    public void  createUsageRecordMessageConsumer() {
        consumer = createConsumer(UsageRecordMessageDeserializer.class.getName(), KafkaTopicConstants.USAGE_RECORD_TOPIC, "usageRecordConsumerGroup");
    }

    public void createNotificationMessageConsumer() {
        consumer = createConsumer(NotificationMessageDeserializer.class.getName(), KafkaTopicConstants.NOTIFICATION_TOPIC, "notificationConsumerGroup");
    }

    public ConsumerRecords<String, T> poll(){
        if(consumer != null) return consumer.poll(Duration.ofMillis(1000));
        else return null;
    }

}
