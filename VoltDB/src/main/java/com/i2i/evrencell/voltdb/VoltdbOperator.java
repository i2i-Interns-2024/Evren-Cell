package com.i2i.evrencell.voltdb;

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

    public int getPackageDataBalance(String msisdn){
        return handleProcedure("GET_CUSTOMER_PACKAGE_DATA_BY_MSISDN",msisdn);
    }

    public int getPackageVoiceBalance(String msisdn){
        return handleProcedure("GET_CUSTOMER_PACKAGE_MINUTES_BY_MSISDN",msisdn);
    }

    public int getPackageSmsBalance(String msisdn){
        return handleProcedure("GET_CUSTOMER_PACKAGE_SMS_BY_MSISDN",msisdn);
    }

    public int getDataBalance(String msisdn) {
        return handleProcedure("GET_CUSTOMER_REMAINING_DATA_BY_MSISDN", msisdn);
    }

    public int getVoiceBalance(String msisdn) {
        return handleProcedure("GET_CUSTOMER_REMAINING_MINUTES_BY_MSISDN", msisdn);
    }

    public int getSmsBalance(String msisdn) {
        return handleProcedure("GET_CUSTOMER_REMAINING_SMS_BY_MSISDN", msisdn);
    }

    public void updateDataBalance(int dataUsage, String msisdn) {
        handleProcedure("UPDATE_CUSTOMER_AMOUNT_DATA_BY_MSISDN", dataUsage, msisdn);
    }

    public void updateVoiceBalance(int voiceUsage, String msisdn) {
        handleProcedure("UPDATE_CUSTOMER_AMOUNT_MINUTES_BY_MSISDN", voiceUsage, msisdn);
    }

    public void updateSmsBalance(int smsUsage, String msisdn) {
        handleProcedure("UPDATE_CUSTOMER_AMOUNT_SMS_BY_MSISDN", smsUsage, msisdn);
    }

    public Package getPackageByMsisdn(String msisdn) {
        try {
            ClientResponse response = client.callProcedure("GET_PACKAGE_INFO_BY_MSISDN", msisdn);
            VoltTable responseTable = response.getResults()[0];
            if (responseTable.advanceRow()) {
                return new Package(
                        (int) responseTable.getLong("PACKAGE_ID"),
                        responseTable.getString("PACKAGE_NAME"),
                        responseTable.getDouble("PRICE"),
                        (int) responseTable.getLong("AMOUNT_MINUTES"),
                        (int) responseTable.getLong("AMOUNT_DATA"),
                        (int) responseTable.getLong("AMOUNT_SMS"),
                        (int) responseTable.getLong("PERIOD")
                );
            } else {
                throw new RuntimeException("Error while getting package by Msisdn");
            }
        }catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: GET_CUSTOMER_INFO_PACKAGE_BY_MSISDN", e);
            throw new RuntimeException("Error while calling procedure: GET_CUSTOMER_INFO_PACKAGE_BY_MSISDN", e);
        }
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


    private void handleProcedure(String procedureName, int usage, String msisdn) {
        try {
            ClientResponse response = client.callProcedure(procedureName, usage, msisdn);
            //ClientResponse response0 = client.callProcedure("GET_CUSTOMER_AMOUNT_DATA_BY_MSISDN", "5551234567");
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

    public String getName(String msisdn) {
        return getUserDetails(msisdn).getName();
    }

    public String getLastName(String msisdn) {
        return getUserDetails(msisdn).getLastName();
    }

    public String getUserEmail(String msisdn) {
        return getUserDetails(msisdn).getEmail();
    }

    public int getPackageId(String msisdn) {
        return getUserDetails(msisdn).getPackageId();
    }

}