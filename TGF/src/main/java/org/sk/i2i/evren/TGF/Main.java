package org.sk.i2i.evren.TGF;

import akka.actor.*;
import com.typesafe.config.ConfigFactory;
import org.sk.i2i.evren.TGF.actors.AkkaActor;
import org.sk.i2i.evren.TGF.actors.DeadLetterListener;
import org.sk.i2i.evren.TGF.trafficGenerators.TransactionGenerator;

import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static ActorSystem system = ActorSystem.create("TGFSystem", ConfigFactory.load("application.conf"));

    private static ActorSelection remoteActor = system.actorSelection("akka://TGFSystemCopy@127.0.0.1:25521/user/TGFActorCopy");
    private static ActorRef akkaActor = system.actorOf(Props.create(AkkaActor.class, remoteActor), "TGFActor");
    private static ActorRef deadLetterListener = system.actorOf(DeadLetterListener.props(), "deadLetterListener");


    private static TransactionGenerator voiceGenerator = new TransactionGenerator(TransactionGenerator.Types.VOICE, akkaActor, 1_000_000, -1);
    private static TransactionGenerator dataGenerator = new TransactionGenerator(TransactionGenerator.Types.DATA, akkaActor, 500_000, -1);
    private static TransactionGenerator smsGenerator = new TransactionGenerator(TransactionGenerator.Types.SMS, akkaActor, 2_000_000, -1);

    private static Thread voiceThread;
    private static Thread dataThread;
    private static Thread smsThread;

    public static void main(String[] args) {

        system.eventStream().subscribe(deadLetterListener, DeadLetter.class);

        outer:
        while(true) {

            System.out.println("enter command: ");
            String input = sc.nextLine();

            switch (input) {
                case "start" -> {

                    if(voiceThread == null || dataThread == null || smsThread == null)
                        startThreads();
                    else if(voiceThread.isAlive() || dataThread.isAlive() || smsThread.isAlive())
                        System.out.println("Generator already running...");
                    else
                        startThreads();

                }
                case "stop" -> {
                    stopThreads();
                }
                case "terminate" -> {

                    stopThreads();
                    sc.close();
                    Clock.delay(1_000_000);
                    system.terminate();

                    break outer;

                }
                case "setDelayVoice" -> {
                    updateDelay(voiceGenerator);
                }
                case "setDelayData" -> {
                    updateDelay(dataGenerator);
                }
                case "setDelaySms" -> {
                    updateDelay(smsGenerator);
                }
                case "printDelay" -> {
                    printDelay();
                }
                case "printStats" -> {
                    printStats();
                }
                case "resetStats" -> {
                    resetStats();
                }
                default -> System.out.println("unrecognized command...");
            }
        }
    }

    private static void startThreads() {

        resetStats();

        voiceThread = new Thread(voiceGenerator);
        dataThread = new Thread(dataGenerator);
        smsThread = new Thread(smsGenerator);

        voiceThread.start();
        dataThread.start();
        smsThread.start();

        System.out.println("Transaction generator started...");

    }

    private static void stopThreads() {

        if(voiceThread == null || dataThread == null || smsThread == null) //TODO check if alive
            System.out.println("No generators are running...");
        else {
            voiceGenerator.stopGeneration();
            dataGenerator.stopGeneration();
            smsGenerator.stopGeneration();
        }
    }

    private static void resetStats() {
        voiceGenerator.resetStats();
        dataGenerator.resetStats();
        smsGenerator.resetStats();
    }

    private static void printDelay() {
        System.out.println(
                "Voice Delay: " + voiceGenerator.getDelay() +
                "\nData Delay:  " + dataGenerator.getDelay() +
                "\nSms Delay:   " + smsGenerator.getDelay()
        );
    }

    private static void printStats() {
        voiceGenerator.printStats();
        dataGenerator.printStats();
        smsGenerator.printStats();
    }

    private static void updateDelay(TransactionGenerator generator) {

        try {
            System.out.println("enter delay time:");
            generator.setDelay(sc.nextLong());

        } catch (InputMismatchException e) {
            System.out.println("unsupported input format...");
        }
    }

}