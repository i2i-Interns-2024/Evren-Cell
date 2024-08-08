package org.sk.i2i.evren.TGF.trafficGenerators;

import akka.actor.ActorRef;
import org.sk.i2i.evren.TGF.Clock;
import org.sk.i2i.evren.TGF.DTO.DataTransaction;
import org.sk.i2i.evren.TGF.DTO.SmsTransaction;
import org.sk.i2i.evren.TGF.DTO.VoiceTransaction;

public class TransactionGenerator implements Runnable{


    public static enum Types { DATA, VOICE, SMS }
    private final Types type;
    private final ActorRef actor;
    private long delay;
    private boolean isGenerate = true;
    private long transCounter = 0;
    private final long maxTransactions; //setting it to -1 means no max number of transactions
    private long startTime = 0;

    /**
     * @param actor akka actor which will send transactions
     * @param delay delay between sending transactions in nanoseconds
     */
    public TransactionGenerator(Types type, ActorRef actor, long delay) {
        this.type = type;
        this.actor = actor;
        this.delay = delay;
        this.maxTransactions = -1;
    }

    /**
     * @param actor akka actor which will send transactions
     * @param delay delay between sending transactions in nanoseconds
     * @param maxTransactions max number of transactions to be sent, setting it to -1 will result in no limit
     */
    public TransactionGenerator(Types type, ActorRef actor, long delay, long maxTransactions) {
        this.type = type;
        this.actor = actor;
        this.delay = delay;
        this.maxTransactions = maxTransactions;
    }

    @Override
    public void run() {

        startTime = System.currentTimeMillis();
        isGenerate = true;

        while(isGenerate) {

            if(transCounter > maxTransactions && maxTransactions != -1) {
                System.out.println("max number of transactions reached:  " + maxTransactions);
                break;
            }

            sendTransaction();
            transCounter++;
            Clock.delay(delay);

        }

        System.out.println("traffic generation stopped...");
        printStats();
    }

    private void sendTransaction() {

        switch (type) {
            case DATA -> {
                actor.tell(new DataTransaction("5461970089", 1, 7, 1), ActorRef.noSender());
            }
            case VOICE -> {
                actor.tell(new VoiceTransaction("5461970089", "5461970089", 1, 1), ActorRef.noSender());
            }
            case SMS -> {
                actor.tell(new SmsTransaction("5461970089", "5461970089", 1), ActorRef.noSender());
            }
        }

    }

    public void stopGeneration() {
        this.isGenerate = false;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {

        System.out.println("delay updated from " + this.delay + " to " + delay);
        this.delay = delay;
    }

    public boolean isGenerating() {
        return isGenerate;
    }

    public void printStats() {

        long resultTime = ((System.currentTimeMillis() - startTime));

        System.out.println(
                "GENERATOR with type " + type +
                "\nhas sent " + transCounter + " Transactions" +
                "\nin " + resultTime + "ms | " + resultTime/1000 + "s | " +  resultTime/60000 + "min.\n"
        );

    }
    public void resetStats() {
        startTime = System.currentTimeMillis();
        transCounter = 0;
    }

}
