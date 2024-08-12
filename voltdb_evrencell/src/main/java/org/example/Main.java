package org.example;

import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientResponse;
import org.voltdb.VoltTable;

public class Main {
    public static void main(String[] args) {
        // VoltDB'ye bağlanmak için ClientConfig oluşturuyoruz
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientFactory.createClient(clientConfig);

        try {
            // VoltDB sunucusuna bağlanıyoruz
            client.createConnection("34.132.46.242:32803");
            ClientResponse response0 = client.callProcedure("GET_CUSTOMER_INFO_BY_MSISDN","5306667788");

            if (response0.getStatus() == ClientResponse.SUCCESS) {
                VoltTable results = response0.getResults()[0];
                while (results.advanceRow()) {
                    System.out.println(
                            "CUST_ID: " + results.getLong("CUST_ID") +
                                    ", MSISDN: " + results.getString("MSISDN") +
                                    ", NAME: " + results.getString("NAME") +
                                    ", SURNAME: " + results.getString("SURNAME") +
                                    ", EMAIL: " + results.getString("EMAIL") +
                                    ", SDATE: " + results.getTimestampAsSqlTimestamp("SDATE") +
                                    ", TC_NO: " + results.getString("TC_NO")
                    );
                }
            }
            else {
                System.out.println("Procedure call failed: " + response0.getStatusString());
            }

            // Bağlantıyı kapatıyoruz
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("intellij commit deneme2");
    }
}
