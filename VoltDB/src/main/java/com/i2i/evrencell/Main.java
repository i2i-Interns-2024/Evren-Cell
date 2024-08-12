package com.i2i.evrencell;

import com.i2i.evrencell.voltdb.UserDetails;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientResponse;
import org.voltdb.VoltTable;

public class Main {
    public static void main(String[] args) {

        //mesaj
        double startTime = System.currentTimeMillis();

        VoltdbOperator voltdbOperator = new VoltdbOperator();


        System.out.println(voltdbOperator.getLastName("5537030253"));


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