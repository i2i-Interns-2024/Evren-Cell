package com.i2i.evrencell;

import com.i2i.evrencell.voltdb.VoltCustomer;
import com.i2i.evrencell.voltdb.VoltPackage;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ProcCallException {

        double startTime = System.currentTimeMillis();
        VoltdbOperator voltdbOperator = new VoltdbOperator();

        //voltdbOperator.updatePassword("buBenimYepyeniParolam", "john.doe@example.com","12345678901");
        //System.out.println(voltdbOperator.getCustomerIdByEmailAndTc("john.doe@example.com","12345678901"));

        System.out.println(voltdbOperator.checkCustomerExists("john.doe@example.com","12345678901"));


        double endTime = System.currentTimeMillis();
        double elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime);
    }
}