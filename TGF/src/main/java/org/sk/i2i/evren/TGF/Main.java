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
    private static final DeadLetterStats deadLetterStats = new DeadLetterStats();

    private static final ActorSystem actorSystem = ActorSystem.create("TGFSystem", ConfigFactory.load("application.conf"));
    private static final ActorRef actor = actorSystem.actorOf(Props.create(AkkaActor.class), "TGFActor");
    private static final ActorRef deadLetterListener = actorSystem.actorOf(Props.create(DeadLetterListener.class, deadLetterStats), "deadLetterListener");

    private static final TransactionGenerator voiceGenerator = new TransactionGenerator(TransactionGenerator.Types.VOICE, actor, 50000000L);
    private static final TransactionGenerator dataGenerator = new TransactionGenerator(TransactionGenerator.Types.DATA, actor, 5000000L);
    private static final TransactionGenerator smsGenerator = new TransactionGenerator(TransactionGenerator.Types.SMS, actor, 500000000L);

    private static Thread voiceThread;
    private static Thread dataThread;
    private static Thread smsThread;

    public static void main(String[] args) {

        //direct deadLetter to deadLetterListener actor
        actorSystem.eventStream().subscribe(deadLetterListener, DeadLetter.class);

        //count how many unrecognized commands was received,
        //stop traffic generation after 3 unrecognized commands.
        int stopCounter =  0;

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
                case "stop"          -> stopThreads();
                case "setDelay"      -> updateDelayAll();
                case "setDelayVoice" -> updateDelay(voiceGenerator);
                case "setDelayData"  -> updateDelay(dataGenerator);
                case "setDelaySms"   -> updateDelay(smsGenerator);
                case "printDelay"    -> printDelay();
                case "printStats"    -> printStats();
                case "resetStats"    -> resetStats();
                case "updateMsisdn"  -> RandomGenerator.fetchMsisdn();  //update the set of msisdn from Hazelcast
                case "terminate"     -> {

                    stopThreads();
                    sc.close();
                    Clock.delay(1_000_000); //delay for 1ms to make sure threads are stopped before terminating akka system
                    actorSystem.terminate();

                    break outer;
                }
                default -> {

                    stopCounter++;
                    if(stopCounter > 3)
                        stopThreads();
                    else
                        System.out.println("unrecognized command...");
                }
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

        if(voiceThread == null || dataThread == null || smsThread == null)
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
        deadLetterStats.reset();

    }

    private static void printDelay() {
        System.out.println(
                "Voice: " + voiceGenerator.getDelay() +
                "\nData:  " + dataGenerator.getDelay() +
                "\nSms:   " + smsGenerator.getDelay()
        );
    }

    private static void printStats() {
        voiceGenerator.printStats();
        System.out.println("dropped: " + deadLetterStats.getVoiceDeadLetters()  + " Transactions\n");

        dataGenerator.printStats();
        System.out.println("dropped: " + deadLetterStats.getDataDeadLetters()  + " Transactions\n");

        smsGenerator.printStats();
        System.out.println("dropped: " + deadLetterStats.getSmsDeadLetters()  + " Transactions\n");
    }

    private static void updateDelay(TransactionGenerator generator) {

        try {
            System.out.println("enter delay time:");
            generator.setDelay(sc.nextLong());

        } catch (InputMismatchException e) {
            System.out.println("unsupported input format...");
        }
    }
    private static void updateDelayAll() {
        try {
            System.out.println("enter delay time:");
            long newDelay = sc.nextLong();

            dataGenerator.setDelay(newDelay);
            smsGenerator.setDelay(newDelay);
            voiceGenerator.setDelay(newDelay);

        } catch (InputMismatchException e) {
            System.out.println("unsupported input format...");
        }
    }
}