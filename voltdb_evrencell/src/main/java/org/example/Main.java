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
            client.createConnection("34.132.46.242:32794");

            ClientResponse response0 = client.callProcedure("PROC1");

            if (response0.getStatus() == ClientResponse.SUCCESS) {
                VoltTable results = response0.getResults()[0];
                while (results.advanceRow()) {
                    System.out.println(
                            "PACKAGE_ID: " + results.getLong("PACKAGE_ID") +
                                    ", PACKAGE_NAME: " + results.getString("PACKAGE_NAME") +
                                    ", PRICE: " + results.getLong("PRICE") +
                                    ", AMOUNT_MINUTES: " + results.getLong("AMOUNT_MINUTES") +
                                    ", AMOUNT_DATA: " + results.getLong("AMOUNT_DATA") +
                                    ", AMOUNT_SMS: " + results.getLong("AMOUNT_SMS") +
                                    ", PERIOD: " + results.getLong("PERIOD")


                    );
                }
            } else {
                System.out.println("Procedure call failed: " + response0.getStatusString());
            }

           /* System.out.println("--------------------------------------------------------------------------------------------------");
            // Prosedürü çağırıyoruz (accountId parametresi olarak 4 gönderiyoruz)
            ClientResponse response = client.callProcedure("PROC6", 6,5);
            ClientResponse response2 = client.callProcedure("PROC1");


            // Yanıtı kontrol ediyoruz ve sonuçları ekrana yazdırıyoruz
            if (response2.getStatus() == ClientResponse.SUCCESS) {
                VoltTable results = response2.getResults()[0];
                while (results.advanceRow()) {
                    System.out.println(
                            "AMOUNT_DATA: " + results.getLong("PACKAGE_ID") +
                                    ", PACKAGE_NAME: " + results.getString("PACKAGE_NAME") +
                                    ", PRICE: " + results.getLong("PRICE") +
                                    ", AMOUNT_MINUTES: " + results.getLong("AMOUNT_MINUTES") +
                                    ", AMOUNT_DATA: " + results.getLong("AMOUNT_DATA") +
                                    ", AMOUNT_SMS: " + results.getLong("AMOUNT_SMS") +
                                    ", PERIOD: " + results.getLong("PERIOD")


                    );

                }
            } else {
                System.out.println("Procedure call failed: " + response2.getStatusString());
            }


*/
            // Bağlantıyı kapatıyoruz
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("intellij commit deneme2");
    }
}
