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

        //mesaj
        double startTime = System.currentTimeMillis();

        VoltdbOperator voltdbOperator = new VoltdbOperator();

        voltdbOperator.updateDataBalance(5679, "5537530253");

        //System.out.println(voltdbOperator.getMaxCustomerId());

        //System.out.println(voltdbOperator.getPackageIdByName("EVRENCELL MARS"));

        //voltdbOperator.insertCustomer(68,"eno","meno","ceno","seno","zeno", new Timestamp(System.currentTimeMillis()),"neno");

       /* System.out.println(voltdbOperator.getCustomerPassword("5537030253"));
        System.out.println(voltdbOperator.getMaxBalanceId());

        VoltPackage package1 = new VoltPackage();

        package1 = (VoltPackage) voltdbOperator.getPackageByMsisdn("5551234567");
        System.out.println(package1);

        VoltCustomer customer1 = new VoltCustomer();
        System.out.println(voltdbOperator.getCustomerByMsisdn("5551234567"));
*/


        double endTime = System.currentTimeMillis();
        double elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime);


        /*ClientConfig clientConfig = new ClientConfig();
        Client client = ClientFactory.createClient(clientConfig);

        try {
            client.createConnection("34.132.46.242:32803"); //usa server

            ClientResponse response0 = client.callProcedure("GET_CUSTOMER_AMOUNT_DATA_BY_MSISDN", "5551234567");

            if (response0.getStatus() == ClientResponse.SUCCESS) {
                VoltTable results = response0.getResults()[0];
                while (results.advanceRow()) {
                    System.out.println(
                            "AMOUNT_DATA: " + results.getLong("BAL_LVL_DATA"));
                }
            } else {
                System.out.println("Procedure call failed: " + response0.getStatusString());
            }

            // Bağlantıyı kapatıyoruz
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}