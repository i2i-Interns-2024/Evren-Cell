package org.sk.i2i.evren.TGF.trafficGenerators;

import akka.actor.ActorRef;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.TGF.Clock;
import org.sk.i2i.evren.TGF.RandomGenerator;
import org.sk.i2i.evren.VoiceTransaction;

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
     * @param type type of transaction to be generated
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

        System.out.println(type + " traffic generation stopped...");
    }

    private void sendTransaction() {

        String msisdn = RandomGenerator.randomMsisdn();
        int location = RandomGenerator.randomLocation();

        switch (type) {
            case DATA -> {
                DataTransaction trans = new DataTransaction(
                        msisdn,
                        location,
                        RandomGenerator.randomDataUsage(),
                        RandomGenerator.randomRatingGroup());
                actor.tell(trans, ActorRef.noSender());
            }
            case VOICE -> {
                VoiceTransaction trans =  new VoiceTransaction(
                        msisdn,
                        RandomGenerator.randomMsisdn(),
                        location,
                        RandomGenerator.randomDuration()
                );
                actor.tell(trans, ActorRef.noSender());
            }
            case SMS -> {
                SmsTransaction trans = new SmsTransaction(
                        msisdn,
                        RandomGenerator.randomMsisdn(),
                        location
                );
                actor.tell(trans, ActorRef.noSender());
            }
        }//end switch
    }

    public void stopGeneration() {
        this.isGenerate = false;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {

        System.out.println("delay updated:  " + this.delay + " --> " + delay + " nanoseconds");
        this.delay = delay;
    }

    public void printStats() {

        long resultTime = ((System.currentTimeMillis() - startTime));

        System.out.println(
                type + " GENERATOR:" +
                "\nSent:    " + transCounter + " Transactions" +
                "\nThrough: " + resultTime + "ms | " +  resultTime/60000 + "min."
        );

    }
    public void resetStats() {
        startTime = System.currentTimeMillis();
        transCounter = 0;
    }

}
