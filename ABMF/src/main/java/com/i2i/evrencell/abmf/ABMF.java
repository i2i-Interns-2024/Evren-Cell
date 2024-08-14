package com.i2i.evrencell.abmf;

import com.i2i.evrencell.kafka.Subscriber;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import com.i2i.evrencell.kafka.message.BalanceMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class ABMF {

    private static final Logger logger = LogManager.getLogger(ABMF.class);

    public static void main(String[] args) throws SQLException {

        Subscriber<BalanceMessage> subscriber = new Subscriber<BalanceMessage>();

        subscriber.createBalanceMessageConsumer();

        while (true) {
            ConsumerRecords<String, BalanceMessage> records = subscriber.poll();
            for (ConsumerRecord<String, BalanceMessage> record : records) {
                BalanceMessage message = record.value();
                OracleOperations.updateUserBalance(message);
                logBalanceMessage(message);
            }
        }

    }

    public static void logBalanceMessage(BalanceMessage message) {
        String logMessage = String.format("BalanceMessage [MSISDN: %s, Amount: %d, Type: %s]",
                message.getMsisdn(),
                message.getAmount(),
                message.getType());

        logger.info(logMessage);
    }
}
