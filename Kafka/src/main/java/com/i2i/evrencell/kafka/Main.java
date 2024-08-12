package com.i2i.evrencell.kafka;

import com.i2i.evrencell.kafka.message.BalanceMessage;
import com.i2i.evrencell.kafka.message.BalanceType;
import com.i2i.evrencell.kafka.message.NotificationMessage;
import com.i2i.evrencell.kafka.message.UsageRecordMessage;

public class Main {
    public static void main(String[] args) {
        Publisher<NotificationMessage> publisher = new Publisher<>();
        publisher.createNotificationMessageProducer();
        NotificationMessage notificationMessage = new NotificationMessage("ali", "veli", "1234567890", "gmail.com",
                BalanceType.SMS, 10, "20", "");
        publisher.send(notificationMessage, KafkaTopicConstants.NOTIFICATION_TOPIC);
        publisher.close();

        /*Subscriber<BalanceMessage> subscriber = new Subscriber<>();
        subscriber.createBalanceMessageConsumer();
        while(true){
            ConsumerRecords<String, BalanceMessage> records = subscriber.poll();
            for(ConsumerRecord<String, BalanceMessage> record : records){
                System.out.println(record.value().toString());
            }
        }*/

    }
}