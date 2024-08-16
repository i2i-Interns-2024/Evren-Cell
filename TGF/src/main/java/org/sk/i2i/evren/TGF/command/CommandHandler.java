package org.sk.i2i.evren.TGF.command;

import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.TGF.constants.TransType;
import org.sk.i2i.evren.TGF.management.DelayManager;
import org.sk.i2i.evren.TGF.management.StatsManager;
import org.sk.i2i.evren.TGF.management.ThreadsManager;
import org.sk.i2i.evren.TGF.util.Clock;
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
                case "setTpsVoice"   -> setDelayByTps(TransType.VOICE);
                case "setTpsData"    -> setDelayByTps(TransType.DATA);
                case "setTpsSms"     -> setDelayByTps(TransType.SMS);

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

    private void setDelayByTpsAll() {

        try {
            System.out.println("Enter TPS value:");



            double tps = sc.nextDouble() ;

            if(tps <= 0)
                threadManager.stopThreads();

            double delay = Clock.secondInNano / (tps/ 3.0);

            // Ensure the delay is at least 100 ns
            long finalDelay = (long) Math.max(Math.ceil(delay), 100);

            delayManager.setDelayAll(finalDelay);

        } catch (InputMismatchException e) {
            System.out.println("Unsupported input format...");
        }
    }

    private void setDelayByTps(TransType type) {

        try {
            System.out.println("Enter TPS value:");

            double tps = sc.nextDouble();

            double delay = Clock.secondInNano / tps;

            // Ensure the delay is at least 100 ns
            long finalDelay = (long) Math.max(Math.ceil(delay), 100);

            switch (type) {
                case DATA -> delayManager.setDataDelay(finalDelay);
                case VOICE -> delayManager.setVoiceDelay(finalDelay);
                case SMS -> delayManager.setSmsDelay(finalDelay);
            }

        } catch (InputMismatchException e) {
            System.out.println("unsupported input format...");
        }

    }

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
