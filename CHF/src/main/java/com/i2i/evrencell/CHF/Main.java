package com.i2i.evrencell.CHF;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.i2i.evrencell.CHF.akka.ChargingActor;
import com.i2i.evrencell.CHF.calculator.BalanceCalculator;
import com.i2i.evrencell.voltdb.VoltdbOperator;

public class Main {

    public static void main(String[] args) {
        BalanceCalculator balanceCalculator = new BalanceCalculator(new VoltdbOperator());
        ActorSystem system = ActorSystem.create("ChargingSystem");
        ActorRef actor = system.actorOf(Props.create(ChargingActor.class), "ChargingActor");
    }
}