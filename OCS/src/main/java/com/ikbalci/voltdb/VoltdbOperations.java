package com.ikbalci.voltdb;

import org.voltdb.VoltTable;
import org.voltdb.client.*;

import java.io.IOException;
import java.math.BigDecimal;

public class VoltdbOperations {

    private String ip;
    private int port;
    private Client client;
    private ClientResponse response;

    public VoltdbOperations() {
        this.ip = "34.132.46.242";
        this.port = 32803;

        try {
            this.client = getClient();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create VoltDB client: " + e.getMessage(), e);
        }
    }

    private Client getClient() throws IOException, ProcCallException {
        ClientConfig clientConfig = new ClientConfig();
        client = ClientFactory.createClient(clientConfig);
        try {
            client.createConnection(ip + ":" + port);
        } catch (IOException e) {
            throw new IOException("Failed to connect to VoltDB server at " + ip + ":" + port, e);
        }
        return client;
    }

    public String getPackageName(String MSISDN) {
        try {
            response = client.callProcedure("GETPACKAGENAME", MSISDN);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                VoltTable resultTable = response.getResults()[0];
                while (resultTable.advanceRow()) {
                    return resultTable.getString(0);
                }
            } else {
                return "Error: " + response.getStatusString();
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
        return null;
    }

    public long getUserIdByMSISDN(String MSISDN) {
        try {
            response = client.callProcedure("GETUSERID", MSISDN);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                VoltTable resultTable = response.getResults()[0];
                while (resultTable.advanceRow()) {
                    return resultTable.getLong(0);
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public long getVoiceBalance(String MSISDN) {
        try {
            response = client.callProcedure("GETVOICEBALANCE", MSISDN);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                VoltTable resultTable = response.getResults()[0];
                while (resultTable.advanceRow()) {
                    return resultTable.getLong(0);
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public long getSmsBalance(String MSISDN) {
        try {
            response = client.callProcedure("GETSMSBALANCE", MSISDN);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                VoltTable resultTable = response.getResults()[0];
                while (resultTable.advanceRow()) {
                    return resultTable.getLong(0);
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public long getDataBalance(String MSISDN) {
        try {
            response = client.callProcedure("SHOW_AMOUNT_DATA", MSISDN);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                VoltTable resultTable = response.getResults()[0];
                while (resultTable.advanceRow()) {
                    return resultTable.getLong(0);
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public String updateVoiceBalance(String MSISDN, BigDecimal amount) {
        try {
            response = client.callProcedure("UPDATEVOICEBALANCE", MSISDN, amount);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                return "Success";
            } else {
                return "Error: " + response.getStatusString();
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    public String updateSmsBalance(String MSISDN, BigDecimal amount) {
        try {
            response = client.callProcedure("UPDATESMSBALANCE", MSISDN, amount);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                return "Success";
            } else {
                return "Error: " + response.getStatusString();
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    public String updateDataBalance(String MSISDN, BigDecimal amount) {
        try {
            response = client.callProcedure("UPDATEDATABALANCE", MSISDN, amount);
            if (response.getStatus() == ClientResponse.SUCCESS) {
                return "Success";
            } else {
                return "Error: " + response.getStatusString();
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        VoltdbOperations voltdbOperations = new VoltdbOperations();
        System.out.println(voltdbOperations.getDataBalance("5306667788"));
    }
}