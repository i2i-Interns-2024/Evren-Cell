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

        //counts unrecognized commands, stop threads after 3
        int stopCounter =  0;

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
                case "printDelay"    -> delayManager.printDelay();
                case "printStats"    -> statsManager.printStats();
                case "resetStats"    -> statsManager.resetStats();
                case "updateMsisdn"  -> RandomGenerator.fetchMsisdn();  //update the list of msisdn from Hazelcast
                case "testRandom"    -> printTransTest();               //print a random transaction to test values
                default -> {
                    stopCounter++;
                    if(stopCounter > 3)
                        threadManager.stopThreads();
                    else
                        System.out.println("unrecognized command...");
                }
            }
        }

        threadManager.stopThreads();
        sc.close();
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
