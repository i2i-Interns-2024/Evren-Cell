package org.sk.i2i.evren.TGF;

import java.util.List;
import java.util.Random;

public class RandomGenerator {

    private static final HazelcastOperations hazelcastOperations = new HazelcastOperations();
    private static final Random rand = new Random();
    private static List<String> allMsisdn = hazelcastOperations.getAllMsisdn();


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

        int randIndex = rand.nextInt(allMsisdn.size());
        return allMsisdn.get(randIndex);
    }
    public static void fetchMsisdn() {
        allMsisdn = hazelcastOperations.getAllMsisdn();
    }
}
