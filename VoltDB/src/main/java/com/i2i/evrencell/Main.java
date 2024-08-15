package com.i2i.evrencell;

import com.i2i.evrencell.voltdb.UserDetails;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import com.i2i.evrencell.voltdb.VoltdbOperator2;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        VoltdbOperator op = new VoltdbOperator();

        double startTime = System.currentTimeMillis();
        System.out.println(1);


        for (int i = 0; i < 5; ++i) {
            op.updateDataBalance(25000 - i, "905372117887");
            System.out.println(op.getDataBalance("905372117887"));
        }
        op.close();


        double endTime = System.currentTimeMillis();
        double elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime);
        System.out.println("Time taken: " + (endTime - startTime));
    }


}

