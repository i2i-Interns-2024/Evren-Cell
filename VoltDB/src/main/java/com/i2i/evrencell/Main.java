package com.i2i.evrencell;

import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.voltdb.client.ProcCallException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        double startTime = System.currentTimeMillis();
        VoltdbOperator voltdbOperator = new VoltdbOperator();

        System.out.println(voltdbOperator.getSmsBalance("5551234567"));
        System.out.println(voltdbOperator.getVoiceBalance("5551234567"));
        System.out.println(voltdbOperator.getDataBalance("5551234567"));

        voltdbOperator.close();


        double endTime = System.currentTimeMillis();
        double elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime);
    }
}