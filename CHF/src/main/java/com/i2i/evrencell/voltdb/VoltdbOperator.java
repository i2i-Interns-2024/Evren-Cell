package com.i2i.evrencell.voltdb;

import com.i2i.evrencell.packages.UserDetails;
import org.apache.log4j.Logger;
import org.voltdb.VoltTable;
import org.voltdb.client.*;

import java.io.IOException;

public class VoltdbOperator {

    private static final Logger logger = Logger.getLogger(VoltdbOperator.class);

    private final String ip;
    private final int port;
    private Client client;

    public VoltdbOperator() {
        this.ip = "34.132.46.242";
        this.port = 32803;
        initializeClient();
    }

    private void initializeClient() {
        try {
            this.client = getClient();
        } catch (IOException e) {
            logger.error("Error while creating VoltDB client", e);
            throw new RuntimeException("Error while creating VoltDB client", e);
        }
    }

    private Client getClient() throws IOException {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientFactory.createClient(clientConfig);

        try {
            client.createConnection(ip + ":" + port);
        } catch (IOException e) {
            logger.error("Error while creating connection to VoltDB server", e);
            throw new IOException("Error while creating connection to VoltDB server", e);
        }

        return client;
    }

    public int getDataBalance(String msisdn) {
        return handleProcedure("GET_CUSTOMER_AMOUNT_DATA_BY_MSISDN", msisdn);
    }

    public int getVoiceBalance(String msisdn) {
        return handleProcedure("GET_CUSTOMER_AMOUNT_VOICE_BY_MSISDN", msisdn);
    }

    public int getSmsBalance(String msisdn) {
        return handleProcedure("GET_CUSTOMER_AMOUNT_SMS_BY_MSISDN", msisdn);
    }

    public void updateDataBalance(String msisdn, int dataUsage) {
        handleProcedure("UPDATE_CUSTOMER_AMOUNT_DATA_BY_MSISDN", msisdn, dataUsage);
    }

    public void updateVoiceBalance(String msisdn, int voiceUsage) {
        handleProcedure("UPDATE_CUSTOMER_AMOUNT_VOICE_BY_MSISDN", msisdn, voiceUsage);
    }

    public void updateSmsBalance(String msisdn, int smsUsage) {
        handleProcedure("UPDATE_CUSTOMER_AMOUNT_SMS_BY_MSISDN", msisdn, smsUsage);
    }


    public UserDetails getUserDetails(String msisdn) {
        try {
            ClientResponse response = client.callProcedure("GET_CUSTOMER_INFO_PACKAGE_BY_MSISDN", msisdn);
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                return new UserDetails(
                        resultTable.getString("NAME"),
                        resultTable.getString("SURNAME"),
                        resultTable.getString("EMAIL"),
                        (int) resultTable.getLong("PACKAGE_ID")
                );
            } else {
                throw new RuntimeException("No data returned from procedure");
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: GET_CUSTOMER_INFO_PACKAGE_BY_MSISDN", e);
            throw new RuntimeException("Error while calling procedure: GET_CUSTOMER_INFO_PACKAGE_BY_MSISDN", e);
        }
    }

    private int handleProcedure(String procedureName, String msisdn) {
        try {
            ClientResponse response = client.callProcedure(procedureName, msisdn);
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                return (int) resultTable.getLong(0); // Cast long to int
            } else {
                throw new RuntimeException("No data returned from procedure");
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + procedureName, e);
            throw new RuntimeException("Error while calling procedure: " + procedureName, e);
        }
    }


    private void handleProcedure(String procedureName, String msisdn, int usage) {
        try {
            ClientResponse response = client.callProcedure(procedureName, msisdn, usage);
            if (response.getStatus() != ClientResponse.SUCCESS) {
                throw new RuntimeException("Procedure call failed: " + response.getStatusString());
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + procedureName, e);
            throw new RuntimeException("Error while calling procedure: " + procedureName, e);
        }
    }

    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (InterruptedException e) {
                logger.error("Error while closing VoltDB client", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        VoltdbOperator voltdbOperator = new VoltdbOperator();
        System.out.println(voltdbOperator.getDataBalance("5551234567"));
    }
}
