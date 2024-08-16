package org.sk.i2i.evren.TGF.util;

public class Clock {

    public static final long secondInNano = 1000000000L;

    /**
     * @param delay time to delay in ns
     */
    public static void delay(long delay) {

        long nanoStart = System.nanoTime();

        while (System.nanoTime() - nanoStart < delay) {
            // Busy-wait loop
        }

    }
}
