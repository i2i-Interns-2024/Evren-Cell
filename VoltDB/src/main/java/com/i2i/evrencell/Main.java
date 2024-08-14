package com.i2i.evrencell;

import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.voltdb.client.ProcCallException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        double startTime = System.currentTimeMillis();
        VoltdbOperator voltdbOperator = new VoltdbOperator();

        for (int i = 0; i < 10; i++) {
            System.out.println(voltdbOperator.getSmsBalance("5551234567"));
            System.out.println(voltdbOperator.getVoiceBalance("5551234567"));
            System.out.println(voltdbOperator.getDataBalance("5551234567"));
            System.out.println(voltdbOperator.getCustomerIdByEmailAndTc("john.doe@example.com", "12345678901"));
            System.out.println(voltdbOperator.getLastName("5551234567"));
            System.out.println(voltdbOperator.getMaxBalanceId());
            System.out.println(voltdbOperator.getMaxCustomerId());
        }



        voltdbOperator.close();


        double endTime = System.currentTimeMillis();
        double elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime);
    }
}