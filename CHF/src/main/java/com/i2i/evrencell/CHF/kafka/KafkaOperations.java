package com.i2i.evrencell.CHF.kafka;

import com.i2i.evrencell.kafka.KafkaTopicConstants;
import com.i2i.evrencell.kafka.Publisher;
import com.i2i.evrencell.kafka.message.BalanceMessage;
import com.i2i.evrencell.kafka.message.BalanceType;
import com.i2i.evrencell.kafka.message.NotificationMessage;
import com.i2i.evrencell.kafka.message.UsageRecordMessage;

import java.sql.Timestamp;

public class KafkaOperations {
    public static void sendUsageRecordMessage(BalanceType type, String callerMsisdn, String calleeMsisdn, Integer usageDuration, Timestamp usageDate) {
        Publisher<UsageRecordMessage> publisher = new Publisher<>();
        publisher.createUsageRecordMessageProducer();
        publisher.send(new UsageRecordMessage(callerMsisdn, calleeMsisdn, type, usageDuration, usageDate), KafkaTopicConstants.USAGE_RECORD_TOPIC);
        publisher.close();
    }

    public static void sendUpdatedBalanceMessage(BalanceType type, String msisdn, int updatedBalance) {
        Publisher<BalanceMessage> publisher = new Publisher<>();
        publisher.createBalanceMessageProducer();
        publisher.send(new BalanceMessage(msisdn, type, updatedBalance), KafkaTopicConstants.BALANCE_TOPIC);
        publisher.close();
    }

    public static void sendNotificationMessage(String name, String lastname, String msisdn, String email, BalanceType type, Integer amount, String threshold, Timestamp notificationTime) {
        Publisher<NotificationMessage> publisher = new Publisher<>();
        publisher.createNotificationMessageProducer();
        publisher.send(new NotificationMessage(name, lastname, msisdn, email, type, amount, threshold, notificationTime), KafkaTopicConstants.NOTIFICATION_TOPIC);
        publisher.close();
    }
}
