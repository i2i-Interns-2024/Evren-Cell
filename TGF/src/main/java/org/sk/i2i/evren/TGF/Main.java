package org.sk.i2i.evren.TGF;

import akka.actor.*;
import com.typesafe.config.ConfigFactory;
import org.sk.i2i.evren.TGF.actors.AkkaActor;
import org.sk.i2i.evren.TGF.actors.DeadLetterListener;
import org.sk.i2i.evren.TGF.trafficGenerators.DataTransGenerator;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("TGFSystem", ConfigFactory.load("application.conf"));

        ActorSelection remoteActor = system.actorSelection("akka://TGFSystemCopy@127.0.0.1:25521/user/TGFActorCopy");
        ActorRef akkaActor = system.actorOf(Props.create(AkkaActor.class, remoteActor), "TGFActor");

        ActorRef deadLetterListener = system.actorOf(DeadLetterListener.props(), "deadLetterListener");
        system.eventStream().subscribe(deadLetterListener, DeadLetter.class);

        DataTransGenerator trafficGen = new DataTransGenerator(akkaActor, 500_000, true);
        Thread thread = new Thread(trafficGen);

        thread.start();

        Scanner sc = new Scanner(System.in);
        System.out.println("press enter to stop generator... ");
        sc.nextLine();
        trafficGen.stopGeneration();



    }
}