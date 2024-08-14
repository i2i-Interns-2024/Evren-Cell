package org.sk.i2i.evren.TGF;

import akka.actor.*;
import com.typesafe.config.ConfigFactory;
import org.sk.i2i.evren.TGF.actors.AkkaActor;
import org.sk.i2i.evren.TGF.actors.DeadLetterListener;
import org.sk.i2i.evren.TGF.command.CommandHandler;
import org.sk.i2i.evren.TGF.constants.TransType;
import org.sk.i2i.evren.TGF.management.DelayManager;
import org.sk.i2i.evren.TGF.management.StatsManager;
import org.sk.i2i.evren.TGF.management.ThreadsManager;
import org.sk.i2i.evren.TGF.trafficGenerators.TrafficGenerator;
import org.sk.i2i.evren.TGF.util.Clock;


public class Main {

    public static void main(String[] args) {

        //data managers
        StatsManager statsManager = new StatsManager();
        DelayManager delayManager = new DelayManager(500000L, 500000L, 500000L);

        //start akka system and actors
        ActorSystem actorSystem = ActorSystem.create("TGFSystem", ConfigFactory.load("application.conf"));
        ActorRef actor = actorSystem.actorOf(Props.create(AkkaActor.class), "TGFActor");
        ActorRef deadLetterListener = actorSystem.actorOf(Props.create(DeadLetterListener.class, statsManager), "deadLetterListener");
        //subscribe deadLetterListener actor to deadLetters
        actorSystem.eventStream().subscribe(deadLetterListener, DeadLetter.class);

        //runnable traffic generators
        TrafficGenerator voice = new TrafficGenerator(TransType.VOICE, actor, statsManager, delayManager);
        TrafficGenerator data = new TrafficGenerator(TransType.DATA, actor, statsManager, delayManager);
        TrafficGenerator sms = new TrafficGenerator(TransType.SMS, actor, statsManager, delayManager);

        //manages starting and stopping of threads
        ThreadsManager threadsManager = new ThreadsManager(voice, data, sms);

        //run the main loop
        CommandHandler commander = new CommandHandler(threadsManager, statsManager, delayManager);
        commander.startCommander();

        //exit the application
        Clock.delay(1_000_000);
        actorSystem.terminate();

    }
}