package org.sk.i2i.evren.TGF;

import akka.actor.*;
import com.typesafe.config.ConfigFactory;
import org.sk.i2i.evren.TGF.DTO.DataTransaction;
import org.sk.i2i.evren.TGF.DTO.SmsTransaction;
import org.sk.i2i.evren.TGF.DTO.VoiceTransaction;
import org.sk.i2i.evren.TGF.actors.AkkaActor;


public class Main {
    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("TGFSystem", ConfigFactory.load("application.conf"));

        ActorSelection remoteActor = system.actorSelection("akka://TGFSystemCopy@127.0.0.1:25521/user/TGFActorCopy");
        ActorRef akkaActor = system.actorOf(Props.create(AkkaActor.class, remoteActor), "TGFActor");

        akkaActor.tell(new DataTransaction("5461970089", 1, 50, 1), ActorRef.noSender());
        akkaActor.tell(new SmsTransaction("5461958439", "5636039954", 1), ActorRef.noSender());
        akkaActor.tell(new VoiceTransaction("5123456789", "5396302734", 1, 1), ActorRef.noSender());

    }
}