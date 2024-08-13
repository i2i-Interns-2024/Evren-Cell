package com.i2i.evrencell.voltdb;

import org.apache.log4j.Logger;
import org.voltdb.VoltTable;
import org.voltdb.client.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

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
        return handleProcedureAsInt("GET_CUSTOMER_PACKAGE_DATA_BY_MSISDN",msisdn);
    }

    public int getPackageVoiceBalance(String msisdn){
        return handleProcedureAsInt("GET_CUSTOMER_PACKAGE_MINUTES_BY_MSISDN",msisdn);
    }

    public int getPackageSmsBalance(String msisdn){
        return handleProcedureAsInt("GET_CUSTOMER_PACKAGE_SMS_BY_MSISDN",msisdn);
    }

    public int getDataBalance(String msisdn) {
        return handleProcedureAsInt("GET_CUSTOMER_REMAINING_DATA_BY_MSISDN", msisdn);
    }

    public int getVoiceBalance(String msisdn) {
        return handleProcedureAsInt("GET_CUSTOMER_REMAINING_MINUTES_BY_MSISDN", msisdn);
    }

    public int getSmsBalance(String msisdn) {
        return handleProcedureAsInt("GET_CUSTOMER_REMAINING_SMS_BY_MSISDN", msisdn);
    }

    public int getMaxCustomerId() {
        return handleProcedureAsInt("GET_MAX_CUSTOMER_ID");
    }

    public int getMaxBalanceId() {
        return handleProcedureAsInt("GET_MAX_BALANCE_ID");
    }

    public int getPackageIdByName(String package_name) {
        return handleProcedureAsInt2("GET_PACKAGE_ID_BY_PACKAGE_NAME", package_name);
    }

    public int getCustomerIdByEmailAndTc(String email, String tc_no) {
        return handleProcedureGetId("GET_CUSTOMER_ID_BY_MAIL_AND_TCNO", email, tc_no);
    }


    public String getPackageName(String msisdn) {
        return handleProcedureAsString("GET_PACKAGE_NAME_BY_MSISDN", msisdn);
    }

    public String getCustomerPassword(String msisdn) {
        return handleProcedureAsString("GET_CUSTOMER_PASSWORD_BY_MSISDN", msisdn);
    }

    public int checkCustomerExists(String email, String tc_no) {
        return handleProcedureCheck("CHECK_CUSTOMER_EXISTS_BY_MAIL_AND_TCNO", email, tc_no);
    }



    public void insertCustomer(int cust_id, String name, String surname, String msisdn, String email, String password, Timestamp sdate, String TCNumber) {
        handleProcedureInsertCustomer("INSERT_NEW_CUSTOMER", cust_id, name, surname, msisdn, email, password, sdate, TCNumber);
    }

    public void insertBalance(int balance_id, int package_id, int cust_id, int bal_lvl_minutes, int bal_lvl_sms, int bal_lvl_data, Timestamp sdate, Timestamp edate) {
        handleProcedureInsertBalance("INSERT_BALANCE_TO_CUSTOMER", balance_id, cust_id, package_id, bal_lvl_minutes, bal_lvl_sms, bal_lvl_data, sdate, edate);
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

    public void updatePassword(String password, String email, String tc_no) {
        handleProcedureChangePassword("UPDATE_CUSTOMER_PASSWORD", password, email, tc_no);
    }






    public VoltPackage getPackageByMsisdn(String msisdn) {
        try {
            ClientResponse response = client.callProcedure("GET_PACKAGE_INFO_BY_MSISDN", msisdn);
            VoltTable responseTable = response.getResults()[0];
            if (responseTable.advanceRow()) {
                return new VoltPackage(
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
            logger.error("Error while calling procedure: GET_PACKAGE_INFO_BY_MSISDN", e);
            throw new RuntimeException("Error while calling procedure: GET_PACKAGE_INFO_BY_MSISDN", e);
        }
    }

    public Optional<VoltCustomer> getCustomerByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        ClientResponse response = client.callProcedure("GET_CUSTOMER_INFO_BY_MSISDN", msisdn);

        if (response.getStatus() == ClientResponse.SUCCESS) {
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                Integer customerId = (int) resultTable.getLong("CUST_ID");
                String name = resultTable.getString("NAME");
                String surname = resultTable.getString("SURNAME");
                String email = resultTable.getString("EMAIL");
                Timestamp sdate = (resultTable.getTimestampAsSqlTimestamp("SDATE"));
                String tcNo = resultTable.getString("TC_NO");

                VoltCustomer customer = VoltCustomer.builder()
                        .customerId(customerId)
                        .msisdn(msisdn)
                        .email(email)
                        .name(name)
                        .surname(surname)
                        .sDate(sdate)
                        .TCNumber(tcNo)
                        .build();

                client.close();
                return Optional.of(customer);
            }
        }
        client.close();
        throw new RuntimeException("Customer not found with this MSISDN: " + msisdn);
    }

    public VoltCustomerBalance getRemainingCustomerBalanceByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        ClientResponse response = client.callProcedure("GET_REMAINING_CUSTOMER_BALANCE_BY_MSISDN", msisdn);

        if (response.getStatus() == ClientResponse.SUCCESS) {
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                String msisdnResult = resultTable.getString("MSISDN");
                int balanceData = (int) resultTable.getLong("BAL_LVL_DATA");
                int balanceSms = (int) resultTable.getLong("BAL_LVL_SMS");
                int balanceMinutes = (int) resultTable.getLong("BAL_LVL_MINUTES");
                Timestamp sdate = resultTable.getTimestampAsSqlTimestamp("SDATE");
                Timestamp edate = resultTable.getTimestampAsSqlTimestamp("EDATE");

                VoltCustomerBalance balanceResponse = VoltCustomerBalance.builder()
                        .msisdn(msisdnResult)
                        .balanceData(balanceData)
                        .balanceMinutes(balanceMinutes)
                        .balanceSms(balanceSms)
                        .sdate(sdate)
                        .edate(edate)
                        .build();

                client.close();
                return balanceResponse;
            }
        }
        client.close();
        throw new RuntimeException("Customer balance not found for msisdn: " + msisdn);
    }

    public VoltPackageDetails getPackageInfoByPackageId(int packageId) throws IOException, ProcCallException, InterruptedException {
        ClientResponse response = client.callProcedure("GET_PACKAGE_INFO_BY_PACKAGE_ID", packageId);
        if (response.getStatus() == ClientResponse.SUCCESS) {
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                int period = (int) resultTable.getLong("PERIOD");
                int amountMinutes = (int) resultTable.getLong("AMOUNT_MINUTES");
                int amountSms = (int) resultTable.getLong("AMOUNT_SMS");
                int amountData = (int) resultTable.getLong("AMOUNT_DATA");
                return new VoltPackageDetails(period, amountMinutes, amountSms, amountData);
            }
        }
        client.close();
        throw new RuntimeException("Package not found with ID: " + packageId);
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

    private  int handleProcedureAsInt(String procedureName) {
        try {
            ClientResponse response = client.callProcedure(procedureName);
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

    private int handleProcedureAsInt(String procedureName, String msisdn) {
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

    private int handleProcedureAsInt2(String procedureName, String package_name) {
        try {
            ClientResponse response = client.callProcedure(procedureName, package_name);
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

    private String handleProcedureAsString(String procedureName, String msisdn) {
        try {
            ClientResponse response = client.callProcedure(procedureName, msisdn);
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                return resultTable.getString(0); // Cast long to int
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


    private void handleProcedureChangePassword(String procedureName, String password, String email, String tc_no) {
        try {
            ClientResponse response = client.callProcedure(procedureName, password, email, tc_no);
            if (response.getStatus() != ClientResponse.SUCCESS) {
                throw new RuntimeException("Procedure call failed: " + response.getStatusString());
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + procedureName, e);
            throw new RuntimeException("Error while calling procedure: " + procedureName, e);
        }
    }


    private void handleProcedureInsertCustomer(String procedureName, int cust_id, String name, String surname, String msisdn, String email, String password, Timestamp sdate, String TCNumber) {
        try {
            ClientResponse response = client.callProcedure(procedureName, cust_id, name, surname, msisdn, email, password, sdate, TCNumber);
            if (response.getStatus() != ClientResponse.SUCCESS) {
                throw new RuntimeException("Procedure call failed: " + response.getStatusString());
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + procedureName, e);
            throw new RuntimeException("Error while calling procedure: " + procedureName, e);
        }
    }


    private void handleProcedureInsertBalance(String procedureName, int balance_id, int cust_id, int package_id, int bal_lvl_minutes, int bal_lvl_sms, int bal_lvl_data, Timestamp sdate, Timestamp edate) {
        try {
            ClientResponse response = client.callProcedure(procedureName, balance_id, cust_id, package_id, bal_lvl_minutes, bal_lvl_sms, bal_lvl_data, sdate, edate);
            if (response.getStatus() != ClientResponse.SUCCESS) {
                throw new RuntimeException("Procedure call failed: " + response.getStatusString());
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + procedureName, e);
            throw new RuntimeException("Error while calling procedure: " + procedureName, e);
        }
    }

    private int handleProcedureGetId(String procedureName, String email, String tc_no) {
        try {
            ClientResponse response = client.callProcedure(procedureName, email, tc_no);
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

    private int handleProcedureCheck(String procedureName, String email, String tc_no) {
        try {
            ClientResponse response = client.callProcedure(procedureName, email, tc_no);
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