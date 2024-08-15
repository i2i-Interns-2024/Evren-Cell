package org.sk.i2i.evren.TGF.command;

import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.TGF.constants.TransType;
import org.sk.i2i.evren.TGF.management.DelayManager;
import org.sk.i2i.evren.TGF.management.StatsManager;
import org.sk.i2i.evren.TGF.management.ThreadsManager;
import org.sk.i2i.evren.TGF.util.RandomGenerator;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CommandHandler {

    private static final Scanner sc = new Scanner(System.in);

    private final ThreadsManager threadManager;
    private final StatsManager statsManager;
    private final DelayManager delayManager;

    public CommandHandler(ThreadsManager threadManager, StatsManager statsManager, DelayManager delayManager) {
        this.threadManager = threadManager;
        this.statsManager = statsManager;
        this.delayManager = delayManager;
    }

    public void startCommander() {

        outer:
        while(true) {

            System.out.println("enter command: ");
            String input = sc.nextLine();

            switch (input) {
                case "start"         -> threadManager.startThreads();
                case "stop"          -> threadManager.stopThreads();
                case "terminate"     -> { break outer; }

                case "setDelay"      -> updateDelayAll();
                case "setDelayVoice" -> updateDelay(TransType.VOICE);
                case "setDelayData"  -> updateDelay(TransType.DATA);
                case "setDelaySms"   -> updateDelay(TransType.SMS);

                case "setTps"        -> setDelayByTpsAll();
//                case "setTpsVoice"   -> setDelayByTps(TransType.VOICE);
//                case "setTpsData"    -> setDelayByTps(TransType.DATA);
//                case "setTpsSms"     -> setDelayByTps(TransType.SMS);

                case "printDelay"    -> delayManager.printDelay();
                case "printTps"      -> delayManager.printTps();
                case "printStats"    -> statsManager.printStats();
                case "resetStats"    -> statsManager.resetStats();

                case "updateMsisdn"  -> RandomGenerator.fetchMsisdn();  //update the list of msisdn from Hazelcast
                case "testRandom"    -> printTransTest();               //print a random transaction to test values
                default -> System.out.println("unrecognized command...");
            }
        }

        threadManager.stopThreads();
        sc.close();
    }

//    private void setDelayByTpsAll() {
//
//        try {
//            System.out.println("enter tps value:");
//
//            long tpsPerGenerator = Math.round((sc.nextDouble() / 3) * 100000) / 100000;
//
//            long delay = Math.round( 1000.0 / tpsPerGenerator);
//
//            delayManager.setDelayAll(delay);
//
//        } catch (InputMismatchException e) {
//            System.out.println("unsupported input format...");
//        }
//
//    }

//    private void setDelayByTpsAll() {
//
//        try {
//            System.out.println("enter tps value:");
//
//            double tpsPerGenerator = sc.nextDouble() / 3;
//
//            double delay = 1000.0 / tpsPerGenerator ;
//
//            delayManager.setDelayAll((long) Math.ceil(delay));
//
//        } catch (InputMismatchException e) {
//            System.out.println("unsupported input format...");
//        }
//
//    }

    private void setDelayByTpsAll() {

        try {
            System.out.println("Enter TPS value:");

            double tpsPerGenerator = sc.nextDouble() / 3.0;

            double delay = 1000000000L / tpsPerGenerator;

            // Ensure the delay is at least 1 ms to avoid a delay of 0
            long finalDelay = (long) Math.max(Math.ceil(delay), 1);

            delayManager.setDelayAll(finalDelay);

        } catch (InputMismatchException e) {
            System.out.println("Unsupported input format...");
        }
    }

//    private void setDelayByTps(TransType type) {
//
//        try {
//            System.out.println("enter tps value:");
//
//            float tps = sc.nextFloat();
//
//            long delay = Math.round(1000 / tps);
//
//            switch (type) {
//                case DATA -> delayManager.setDataDelay(delay);
//                case VOICE -> delayManager.setVoiceDelay(delay);
//                case SMS -> delayManager.setSmsDelay(delay);
//            }
//
//        } catch (InputMismatchException e) {
//            System.out.println("unsupported input format...");
//        }
//
//    }

    private void updateDelay(TransType type) {

        try {
            System.out.println("enter delay value:");
            long delay = sc.nextLong();

            switch (type) {
                case DATA -> delayManager.setDataDelay(delay);
                case VOICE -> delayManager.setVoiceDelay(delay);
                case SMS -> delayManager.setSmsDelay(delay);
            }

        } catch (InputMismatchException e) {
            System.out.println("unsupported input format...");
        }
    }

    private void updateDelayAll() {

        try {
            System.out.println("enter delay value:");
            long delay = sc.nextLong();
            delayManager.setDelayAll(delay);

        } catch (InputMismatchException e) {
            System.out.println("unsupported input format...");
        }
    }

    private void printTransTest() {
        System.out.println(new DataTransaction(
                RandomGenerator.randomMsisdn(),
                RandomGenerator.randomLocation(),
                RandomGenerator.randomDataUsage(),
                RandomGenerator.randomRatingGroup()));
    }

}
