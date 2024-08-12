package com.i2i.evrencell.abmf;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.sql.SQLException;
import java.util.Properties;

public class ABMF {

    public static void main(String[] args) throws SQLException {
        String url = "";
        String userName = "";
        String password = "";

        OracleOperations oracleOperations = new OracleOperations(url, userName, password);

        Properties props = new Properties();
        props.put("bootstrap.servers", "34.154.217.137:9092");
        props.put("group.id", "AA111");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "com.i2i.evrencell.abmf.BalanceMessageDeserializer");

        KafkaOperations kafkaOperations = new KafkaOperations(props, "BalanceTopic");

        while (true) {
            ConsumerRecords<String, BalanceMessage> records = kafkaOperations.pollMessage();
            for (ConsumerRecord<String, BalanceMessage> record : records) {
                BalanceMessage message = record.value();
                oracleOperations.updateUserBalance(message);
            }
        }

    }
}
