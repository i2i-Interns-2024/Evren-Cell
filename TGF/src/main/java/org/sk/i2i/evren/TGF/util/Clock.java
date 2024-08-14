package org.sk.i2i.evren.TGF.util;

public class Clock {

    /**
     * @param delay time to delay in ms
     */
    public static void delay(int delay) {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            System.out.println("exception: couldn't delay!");
        }

    }
}
