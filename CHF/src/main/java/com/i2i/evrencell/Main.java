package com.i2i.evrencell;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.i2i.evrencell.akka.ChargingActor;
import com.i2i.evrencell.calculator.BalanceCalculator;
import com.i2i.evrencell.kafka.KafkaOperator;
import com.i2i.evrencell.packages.PackageDetailsReader;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import com.typesafe.config.ConfigFactory;

public class Main {
    public static void main(String[] args) {

        VoltdbOperator voltdbOperator = new VoltdbOperator();
        PackageDetailsReader packageDetailsReader = new PackageDetailsReader();
        KafkaOperator kafkaOperator = new KafkaOperator(packageDetailsReader,voltdbOperator);
        BalanceCalculator balanceCalculator = new BalanceCalculator(voltdbOperator,packageDetailsReader,kafkaOperator);
        // create akka system
        ActorSystem system = ActorSystem.create("ChargingSystem", ConfigFactory.load("application.conf"));

        // receive data transaction objects from chf akka actor
        // and calculate balance
        ActorRef actor = system.actorOf(Props.create(ChargingActor.class, balanceCalculator), "ChargingActor");

    }
}