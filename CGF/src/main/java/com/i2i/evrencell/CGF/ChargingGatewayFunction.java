package com.i2i.evrencell.CGF;


import com.i2i.evrencell.kafka.Subscriber;
import com.i2i.evrencell.kafka.message.UsageRecordMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class ChargingGatewayFunction {
    public static void main( String[] args ) {
        Subscriber<UsageRecordMessage> subscriber = new Subscriber<>();
        subscriber.createUsageRecordMessageConsumer();

        while (true){
            ConsumerRecords<String, UsageRecordMessage> consumerRecords = subscriber.poll();
            for(ConsumerRecord<String, UsageRecordMessage> record: consumerRecords){
                UsageRecordMessage usageRecord = record.value();
                OracleCRUD.callInsertProcedure(usageRecord);
            }
        }
    }
}
