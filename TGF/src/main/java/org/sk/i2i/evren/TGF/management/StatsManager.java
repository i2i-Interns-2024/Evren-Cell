package org.sk.i2i.evren.TGF.management;

import org.sk.i2i.evren.TGF.trafficGenerators.TrafficGenerator;

import java.util.HashMap;

public class StatsManager {

    private final HashMap<TrafficGenerator.TransTypes, Counter> map = new HashMap<>();

    private class Counter {
        private long counter = 0;
        private long deadCounter = 0;
        private long startTime = 0;
    }

    public StatsManager() {
        for (TrafficGenerator.TransTypes type : TrafficGenerator.TransTypes.values()) {
            map.put(type, new Counter());
        }
    }

    public void incrementCounter(TrafficGenerator.TransTypes type) {

        map.get(type).counter++;
    }

    public void incrementDeadCounter(TrafficGenerator.TransTypes type) {

        map.get(type).deadCounter++;
    }

    public void resetTimer(TrafficGenerator.TransTypes type) {
        map.get(type).startTime = System.currentTimeMillis();
    }

    public void printStats() {
        for (TrafficGenerator.TransTypes type : TrafficGenerator.TransTypes.values()) {

            long resultTime = ((System.currentTimeMillis() - map.get(type).startTime));

            System.out.println(
                    type + " GENERATOR:" +
                    "\nSent:    " + map.get(type).counter + " Transactions" +
                    "\nDropped: " + map.get(type).deadCounter + " Transactions" +
                    "\nThrough: " + resultTime + "ms | " +  resultTime/60000 + "min."
            );

        }
    }

    public void resetStats() {

        for (TrafficGenerator.TransTypes type : TrafficGenerator.TransTypes.values()) {
            map.get(type).counter = 0;
            map.get(type).deadCounter = 0;
            map.get(type).startTime = System.currentTimeMillis();
        }
    }

}
