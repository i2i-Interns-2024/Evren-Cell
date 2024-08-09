package org.example;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Properties;

public class ChargingGatewayFunction {
    public static void main( String[] args ) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "34.154.217.137:9092");
        props.put("group.id", "m-id");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.example.UsageRecordDeserializer");

        KafkaConsumer<String, UsageRecord> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("UsageRecord"));

        while (true){
            ConsumerRecords<String, UsageRecord> consumerRecords = consumer.poll(1000);
            for(ConsumerRecord<String, UsageRecord> record: consumerRecords){
                UsageRecord usageRecord = record.value();
                OracleCRUD.callInsertProcedure(usageRecord);
            }
        }
    }
}
