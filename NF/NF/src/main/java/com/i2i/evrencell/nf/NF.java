package com.i2i.evrencell.nf;

import com.i2i.evrencell.kafka.Subscriber;
import com.i2i.evrencell.kafka.message.NotificationMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.mail.Session;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

public class NF {
    public static void main(String[] args) throws SQLException {
        new NF().runApplication();
    }

    public void runApplication() throws SQLException {
        Subscriber<NotificationMessage> subscriber = new Subscriber<>();
        subscriber.createNotificationMessageConsumer();

        NotificationOperations notificationOperations = new NotificationOperations();
        Session session = notificationOperations.createMailSession();

        OracleOperations oracleOperations = new OracleOperations();

        while (true) {
            ConsumerRecords<String, NotificationMessage> consumerRecords = subscriber.poll();
            for (ConsumerRecord<String, NotificationMessage> record : consumerRecords) {
                NotificationMessage notificationMessage = record.value()
                        ;
                oracleOperations.callInsertProcedure(notificationMessage);
                notificationOperations.sendMail(session, notificationMessage);
            }

        }
    }

}
