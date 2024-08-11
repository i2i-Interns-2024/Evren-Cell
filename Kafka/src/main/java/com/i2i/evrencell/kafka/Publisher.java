package com.i2i.evrencell.kafka;

import com.i2i.evrencell.kafka.message.BalanceMessage;
import com.i2i.evrencell.kafka.message.NotificationMessage;
import com.i2i.evrencell.kafka.message.UsageRecordMessage;
import com.i2i.evrencell.kafka.seralizer.BalanceMessageSerializer;
import com.i2i.evrencell.kafka.seralizer.GenericMessageSerializer;
import com.i2i.evrencell.kafka.seralizer.NotificationMessageSerializer;
import com.i2i.evrencell.kafka.seralizer.UsageRecordMessageSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Publisher {

    public <T> Producer<String, T> createProducer(Class<? extends GenericMessageSerializer<T>> serializerClass, String topic) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "159.89.229.126:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClass.getName());

        return new KafkaProducer<>(properties);
    }

    public Producer<String, BalanceMessage> createBalanceMessageProducer() {
        return createProducer(BalanceMessageSerializer.class, "balanceTopic");
    }

    public Producer<String, UsageRecordMessage> createUsageRecordMessageProducer() {
        return createProducer(UsageRecordMessageSerializer.class, "usageRecordTopic");
    }

    public Producer<String, NotificationMessage> createNotificationMessageProducer() {
        return createProducer(NotificationMessageSerializer.class, "notificationTopic");
    }
}
