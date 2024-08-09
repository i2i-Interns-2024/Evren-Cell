package org.example;

import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientResponse;
import org.voltdb.VoltTable;

public class Main {
    public static void main(String[] args) {
        // Zaman ölçümü için başlangıç zamanı alınıyor
        long startTime = System.currentTimeMillis();

        // VoltDB'ye bağlanmak için ClientConfig oluşturuyoruz
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientFactory.createClient(clientConfig);

        try {
            // VoltDB sunucusuna bağlanıyoruz
            client.createConnection("34.132.46.242:32803"); //usa server

            ClientResponse response0 = client.callProcedure("GET_CUSTOMER_AMOUNT_DATA_BY_MSISDN", "5551234567");

            if (response0.getStatus() == ClientResponse.SUCCESS) {
                VoltTable results = response0.getResults()[0];
                while (results.advanceRow()) {
                    System.out.println(
                            "AMOUNT_DATA: " + results.getLong("BAL_LVL_DATA") /*+
                                    ", MSISDN: " + results.getString("MSISDN") +
                                    ", NAME: " + results.getString("NAME") +
                                    ", SURNAME: " + results.getString("SURNAME") +
                                    ", EMAIL: " + results.getString("EMAIL") +
                                    ", SDATE: " + results.getTimestampAsSqlTimestamp("SDATE") +
                                    ", TC_NO: " + results.getString("TC_NO")*/
                    );
                }
            } else {
                System.out.println("Procedure call failed: " + response0.getStatusString());
            }

            // Bağlantıyı kapatıyoruz
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Zaman ölçümü için bitiş zamanı alınıyor
        long endTime = System.currentTimeMillis();

        // Toplam çalışma süresi hesaplanıyor
        long duration = endTime - startTime;

        // Sonuç yazdırılıyor
        System.out.println("Code execution time: " + duration + " milliseconds");

        System.out.println("intellij commit deneme2");
    }
}
