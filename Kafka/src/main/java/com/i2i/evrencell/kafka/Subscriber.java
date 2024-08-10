package com.i2i.evrencell.kafka;

import com.i2i.evrencell.kafka.deserializer.BalanceMessageDeserializer;
import com.i2i.evrencell.kafka.deserializer.GenericMessageDeserializer;
import com.i2i.evrencell.kafka.deserializer.NotificationMessageDeserializer;
import com.i2i.evrencell.kafka.deserializer.UsageRecordMessageDeserializer;
import com.i2i.evrencell.kafka.message.BalanceMessage;
import com.i2i.evrencell.kafka.message.NotificationMessage;
import com.i2i.evrencell.kafka.message.UsageRecordMessage;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class Subscriber {

    public <T> Consumer<String, T> createConsumer(Class<? extends GenericMessageDeserializer<T>> deserializerClass, String topic, String consumerGroup) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "159.89.229.126:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializerClass.getName());

        Consumer<String, T> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));

        return consumer;
    }

    public Consumer<String, BalanceMessage> createBalanceMessageConsumer() {
        return createConsumer(BalanceMessageDeserializer.class, "balanceTopic", "balanceConsumerGroup");
    }

    public Consumer<String, UsageRecordMessage> createUsageRecordMessageConsumer() {
        return createConsumer(UsageRecordMessageDeserializer.class, "usageRecordTopic", "usageRecordConsumerGroup");
    }

    public Consumer<String, NotificationMessage> createNotificationMessageConsumer() {
        return createConsumer(NotificationMessageDeserializer.class, "notificationTopic", "notificationConsumerGroup");
    }

}
