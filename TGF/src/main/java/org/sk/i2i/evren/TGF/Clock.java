package org.sk.i2i.evren.TGF;

public class Clock {

    /**
     * @param delay time to delay in nanoseconds
     */
    public static void delay(long delay) {

        long nanostart = System.nanoTime();

        while (System.nanoTime() - nanostart < delay) {
            // Busy-wait loop
        }

    }
}
