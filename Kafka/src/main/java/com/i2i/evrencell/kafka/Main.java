package com.i2i.evrencell.kafka;

import com.i2i.evrencell.kafka.message.BalanceMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;

public class Main {
    public static void main(String[] args) {
        /*Publisher<BalanceMessage> publisher = new Publisher<>();
        publisher.createBalanceMessageProducer();
        BalanceMessage balanceMessage = new BalanceMessage("05477417766", 10, 10, 10);
        publisher.send(balanceMessage, "balanceTopic");*/

        Subscriber<BalanceMessage> subscriber = new Subscriber<>();
        subscriber.createBalanceMessageConsumer();
        while(true){
            ConsumerRecords<String, BalanceMessage> records = subscriber.poll();
            for(ConsumerRecord<String, BalanceMessage> record : records){
                System.out.println(record.value().toString());
            }
        }

    }
}