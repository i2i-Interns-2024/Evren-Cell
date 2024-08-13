package org.sk.i2i.evren.TGF.util;

import org.i2i.hazelcast.utils.HazelcastSimulatorOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGenerator {

    private static final Random rand = new Random();
    private static List allMsisdn = new ArrayList<>(HazelcastSimulatorOperation.getAllMsisdn());

    public static int randomLocation() {
        return 1 + rand.nextInt(9);
    }
    public static int randomDataUsage() {
        return 1 + rand.nextInt(49);
    }
    public static int randomRatingGroup() {
        return 1 + rand.nextInt(5);
    }
    public static int randomDuration() {
        return 5 + rand.nextInt(115);
    }
    public static String randomMsisdn() {

        if(allMsisdn.isEmpty()) {
            System.out.println("hazelcast is empty...");
            return "5000000000";
        }

        int randIndex = rand.nextInt(allMsisdn.size());
        return allMsisdn.get(randIndex).toString();
    }
    public static void fetchMsisdn() {

        allMsisdn = new ArrayList<>(HazelcastSimulatorOperation.getAllMsisdn());

/*        System.out.println("fetchMsisdn: size " + allMsisdn.size());
        for (Object msisdn : allMsisdn) {
            System.out.println(msisdn);
        }*/
    }
}
