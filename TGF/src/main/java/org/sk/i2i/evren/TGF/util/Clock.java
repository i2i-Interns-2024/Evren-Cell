package org.sk.i2i.evren.TGF.util;

public class Clock {

    /**
     * @param delay time to delay in ms
     */
//    public static void delay(long delay) {
//
//        try {
//            Thread.sleep(delay);
//        } catch (InterruptedException e) {
//            System.out.println("exception: couldn't delay!");
//        }
//
//    }

    public static void delay(long delay) {

        long nanostart = System.nanoTime();

        while (System.nanoTime() - nanostart < delay) {
            // Busy-wait loop
        }

    }
}
