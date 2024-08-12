package com.i2i.evrencell.abmf;

import com.i2i.evrencell.kafka.Subscriber;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import com.i2i.evrencell.kafka.message.BalanceMessage;

import java.sql.SQLException;

public class ABMF {

    public static void main(String[] args) throws SQLException {
        String oracleUrl = ConfigLoader.getProperty("oracle.url");
        String oracleUserName = ConfigLoader.getProperty("oracle.username");
        String oraclePassword = ConfigLoader.getProperty("oracle.password");

        OracleOperations oracleOperations = new OracleOperations(oracleUrl, oracleUserName, oraclePassword);

        Subscriber<BalanceMessage> subscriber = new Subscriber<BalanceMessage>();

        subscriber.createBalanceMessageConsumer();

        while (true) {
            ConsumerRecords<String, BalanceMessage> records = subscriber.poll();
            for (ConsumerRecord<String, BalanceMessage> record : records) {
                BalanceMessage message = record.value();
                System.out.println(message);
                oracleOperations.updateUserBalance(message);
            }
        }

    }
}
