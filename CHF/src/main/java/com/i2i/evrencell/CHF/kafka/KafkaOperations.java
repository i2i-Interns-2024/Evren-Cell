package com.i2i.evrencell.CHF.kafka;

import com.i2i.evrencell.kafka.KafkaTopicConstants;
import com.i2i.evrencell.kafka.Publisher;
import com.i2i.evrencell.kafka.message.BalanceMessage;
import com.i2i.evrencell.kafka.message.BalanceType;
import com.i2i.evrencell.kafka.message.NotificationMessage;
import com.i2i.evrencell.kafka.message.UsageRecordMessage;

import java.sql.Timestamp;

public class KafkaOperations {

    private static final Publisher<BalanceMessage> balanceMessagePublisher;
    private static final Publisher<UsageRecordMessage> usageRecordMessagePublisher;
    private static final Publisher<NotificationMessage> notificationMessagePublisher;

    static {
        balanceMessagePublisher = new Publisher<>();
        balanceMessagePublisher.createBalanceMessageProducer();

        usageRecordMessagePublisher = new Publisher<>();
        usageRecordMessagePublisher.createUsageRecordMessageProducer();

        notificationMessagePublisher = new Publisher<>();
        notificationMessagePublisher.createNotificationMessageProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            balanceMessagePublisher.close();
            usageRecordMessagePublisher.close();
            notificationMessagePublisher.close();
        }));
    }

    public static void sendUsageRecordMessage(BalanceType type, String callerMsisdn, String calleeMsisdn, Integer usageDuration, Timestamp usageDate) {
        usageRecordMessagePublisher.send(new UsageRecordMessage(callerMsisdn, calleeMsisdn, type, usageDuration, usageDate), KafkaTopicConstants.USAGE_RECORD_TOPIC);
    }

    public static void sendUpdatedBalanceMessage(BalanceType type, String msisdn, int updatedBalance) {
        balanceMessagePublisher.send(new BalanceMessage(msisdn, type, updatedBalance), KafkaTopicConstants.BALANCE_TOPIC);
    }

    public static void sendNotificationMessage(String name, String lastname, String msisdn, String email, BalanceType type, Integer amount, String threshold, Timestamp notificationTime) {
        notificationMessagePublisher.send(new NotificationMessage(name, lastname, msisdn, email, type, amount, threshold, notificationTime), KafkaTopicConstants.NOTIFICATION_TOPIC);
    }
}
