package org.sk.i2i.evren.TGF.trafficGenerators;

import akka.actor.ActorRef;
import org.sk.i2i.evren.TGF.Clock;
import org.sk.i2i.evren.TGF.DTO.DataTransaction;

public class DataTransGenerator implements Runnable{


    private final ActorRef actor;
    private long delay;
    private boolean generate;

    public DataTransGenerator(ActorRef actor, long delay, boolean generate) {
        this.actor = actor;
        this.delay = delay;
        this.generate = generate;
    }

    @Override
    public void run() {

        int counter = 0;

        long startTime = System.currentTimeMillis();

        while(counter < 100000 ) { //generate

            actor.tell(new DataTransaction("5461970089", 1, 50, 1), ActorRef.noSender());

            counter++;
            if(counter % 1000 == 0)
                System.out.println("counter:  " + counter);

            Clock.delay(delay);

        }

        long timeResult = ((System.currentTimeMillis() - startTime));
        System.out.println("sent counter " + counter + " transactions in: " + timeResult + "milliseconds");

    }

    public void stopGeneration() {
        this.generate = false;
        System.out.println("generation set to false");
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
