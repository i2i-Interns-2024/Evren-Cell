package com.i2i.evrencell.voltdb;

import org.apache.log4j.Logger;
import org.voltdb.VoltTable;
import org.voltdb.client.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;


public class VoltdbOperator2 {

    private static final Logger logger = Logger.getLogger(VoltdbOperator2.class);

    private final String ip;
    private final int port;
    private Client client;

    public VoltdbOperator2() {
        // "34.132.46.242" 32803 usa
        // "35.234.92.15"  32776 de
        this.ip = "35.234.92.15";
        this.port = 32776;
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
        clientConfig.setProcedureCallTimeout(0);
        Client client = ClientFactory.createClient(clientConfig);

        try {
            client.createConnection(ip + ":" + port);
        } catch (IOException e) {
            logger.error("Error while creating connection to VoltDB server", e);
            throw new IOException("Error while creating connection to VoltDB server", e);
        }

        return client;
    }

    public void getPackageDataBalance(String msisdn) {
        getBalance("GET_CUSTOMER_PACKAGE_DATA_BY_MSISDN", msisdn);
    }

    public void getPackageVoiceBalance(String msisdn) {
        getBalance("GET_CUSTOMER_PACKAGE_MINUTES_BY_MSISDN", msisdn);
    }

    public void getPackageSmsBalance(String msisdn) {
        getBalance("GET_CUSTOMER_PACKAGE_SMS_BY_MSISDN", msisdn);
    }

    public CompletableFuture<Integer> getDataBalance(String msisdn) {
        return getBalance("GET_CUSTOMER_REMAINING_DATA_BY_MSISDN", msisdn);
    }

    public void getVoiceBalance(String msisdn) {
        getBalance("GET_CUSTOMER_REMAINING_MINUTES_BY_MSISDN", msisdn);
    }

    public void getSmsBalance(String msisdn) {
        getBalance("GET_CUSTOMER_REMAINING_SMS_BY_MSISDN", msisdn);
    }


    public CompletableFuture<Integer> getBalance(String procName, String msisdn) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        VoltTable resultTable = response.getResults()[0];
                        if (resultTable.advanceRow()) {
                            int remainingMinutes = (int) resultTable.getLong(0);
                            future.complete(remainingMinutes);
                        } else {
                            future.completeExceptionally(new RuntimeException("Sonuçlar boş veya geçersiz."));
                        }
                    } else {
                        future.completeExceptionally(new RuntimeException("Çağrıda hata: " + response.getStatusString()));
                    }
                }
            }, procName, msisdn);
        } catch (IOException e) {
            future.completeExceptionally(new RuntimeException("Prosedür çağrısında hata: " + e.getMessage()));
        }
        return future;
    }

    // Asenkron veri güncelleme
    public CompletableFuture<Integer> updateDataBalance(String procName, int usage, String msisdn) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        future.complete(usage); // Başarılı güncelleme sonrası kullanılacak değeri döndür
                    } else {
                        future.completeExceptionally(new RuntimeException("Procedure call failed: " + response.getStatusString()));
                    }
                }
            }, procName, usage, msisdn);
        } catch (IOException e) {
            future.completeExceptionally(new RuntimeException("Error while calling procedure: " + e.getMessage()));
        }
        return future;
    }


    public void updateDataBalance(int dataUsage, String msisdn) {
        updateProcedureAsync("UPDATE_CUSTOMER_AMOUNT_DATA_BY_MSISDN", dataUsage, msisdn);
    }

/*
    public int updateDataBalance(int usage, String msisdn) {
        // Asenkron olarak güncellemeyi başlat
        CompletableFuture<Integer> future = updateBalance("UPDATE_CUSTOMER_AMOUNT_DATA_BY_MSISDN", usage, msisdn);

        try {
            // Asenkron işlemin tamamlanmasını bekle ve sonucu döndür
            return future.get(); // get() metodu, işlemin tamamlanmasını bekler ve sonucu döndürür
        } catch (InterruptedException | ExecutionException e) {
            // Hata durumunda uygun bir değer döndürün veya bir hata işleyici ekleyin
            System.err.println("Güncelleme işlemi sırasında hata: " + e.getMessage());
            throw new RuntimeException("Güncelleme işlemi sırasında hata", e);
        }
    }

    */

    private CompletableFuture<Integer> updateBalance(String procName, int usage, String msisdn) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        // Başarılı olursa yapılacak işlemler
                        future.complete(usage); // Veya burada response ile elde edilen sonuç döndürülebilir
                    } else {
                        // Hata durumunda yapılacak işlemler
                        future.completeExceptionally(new RuntimeException("Procedure call failed: " + response.getStatusString()));
                    }
                }
            }, procName, usage, msisdn);
        } catch (IOException e) {
            // Prosedür çağrısında hata durumunda yapılacak işlemler
            future.completeExceptionally(e);
        }
        return future;
    }


    public void updateVoiceBalance(int voiceUsage, String msisdn) {
        updateProcedureAsync("UPDATE_CUSTOMER_AMOUNT_MINUTES_BY_MSISDN", voiceUsage, msisdn);
    }

    public void updateSmsBalance(int smsUsage, String msisdn) {
        updateProcedureAsync("UPDATE_CUSTOMER_AMOUNT_SMS_BY_MSISDN", smsUsage, msisdn);
    }

/*
    public int updateDataBalance(int usage, String msisdn) {
        return updateBalance("UPDATE_CUSTOMER_AMOUNT_DATA_BY_MSISDN", usage, msisdn);
    }

*/

 /*   public void getBalance(String procName, String msisdn, Consumer<Integer> onSuccess) {
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        VoltTable resultTable = response.getResults()[0];
                        if (resultTable.advanceRow()) {
                            int remainingMinutes = (int) resultTable.getLong(0);
                            // Callback fonksiyonu kullanarak sonucu işleyin
                            onSuccess.accept(remainingMinutes);
                        } else {
                            System.err.println("Sonuçlar boş veya geçersiz.");
                        }
                    } else {
                        System.err.println("Çağrıda hata: " + response.getStatusString());
                    }
                }
            }, procName, msisdn);
        } catch (IOException e) {
            System.err.println("Prosedür çağrısında hata: " + e.getMessage());
        }
    }
*/

    public void getString(String procName, String msisdn, Consumer<String> onSuccess) {
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        VoltTable resultTable = response.getResults()[0];
                        if (resultTable.advanceRow()) {
                            String String = (String) resultTable.getString(0);
                            // Callback fonksiyonu kullanarak sonucu işleyin
                            onSuccess.accept(String);
                        } else {
                            System.err.println("Sonuçlar boş veya geçersiz.");
                        }
                    } else {
                        System.err.println("Çağrıda hata: " + response.getStatusString());
                    }
                }
            }, procName, msisdn);
        } catch (IOException e) {
            System.err.println("Prosedür çağrısında hata: " + e.getMessage());
        }
    }

/*
    public CompletableFuture<Integer> updateBalance(String procName, int usage, String msisdn) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        // Başarılı olursa yapılacak işlemler
                        future.complete(usage); // veya response ile elde edilen sonuç
                    } else {
                        // Hata durumunda yapılacak işlemler
                        future.completeExceptionally(new RuntimeException("Procedure call failed: " + response.getStatusString()));
                    }
                }
            }, procName, usage, msisdn);
        } catch (IOException e) {
            // Prosedür çağrısında hata durumunda yapılacak işlemler
            future.completeExceptionally(e);
        }
        return future;
    }
*/


    /*public void updateBalance(String procName, int usage, String msisdn, Runnable onSuccess, Consumer<Exception> onError) {
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        // Başarılı olursa yapılacak işlemler
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    } else {
                        // Hata durumunda yapılacak işlemler
                        if (onError != null) {
                            onError.accept(new RuntimeException("Procedure call failed: " + response.getStatusString()));
                        }
                    }
                }
            }, procName, usage, msisdn);
        } catch (IOException e) {
            // Prosedür çağrısında hata durumunda yapılacak işlemler
            if (onError != null) {
                onError.accept(e);
            }
        }
    }*/





    public void getNameByMsisdn(String msisdn) {
        getString("GET_CUSTOMER_NAME_BY_MSISDN", msisdn, string -> {
            System.out.println("Bilgi: " + string);
        });
    }

    public void getLastNameByMsisdn(String msisdn) {
        executeStringAsyncProcedure("GET_CUSTOMER_SURNAME_BY_MSISDN", msisdn);
    }

    public void getEmailByMsisdn(String msisdn) {
        executeStringAsyncProcedure("GET_CUSTOMER_EMAIL_BY_MSISDN", msisdn);
    }

    public void getPackageIdByMsisdn(String msisdn) {
        executeIntegerAsyncProcedure("GET_PACKAGE_ID_BY_MSISDN", msisdn);
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




    public void getPackageName(String msisdn) {
        getString("GET_PACKAGE_NAME_BY_MSISDN", msisdn, string -> {
            System.out.println("Bilgi: " + string);
        });
    }

    public void getCustomerPassword(String msisdn) {
        getString("GET_CUSTOMER_PASSWORD_BY_MSISDN", msisdn, string -> {
            System.out.println("Bilgi: " + string);
        });
    }

    public int checkCustomerExists(String email, String tc_no) {
        return handleProcedureCheck("CHECK_CUSTOMER_EXISTS_BY_MAIL_AND_TCNO", email, tc_no);
    }


    public void insertCustomer(int cust_id, String name, String surname, String msisdn, String email, String password, Timestamp sdate, String TCNumber) {
        handleProcedureInsertCustomer(cust_id, name, surname, msisdn, email, password, sdate, TCNumber);
    }

    public void insertBalance(int balance_id, int cust_id, int package_id, int bal_lvl_minutes, int bal_lvl_sms, int bal_lvl_data, Timestamp sdate, Timestamp edate) {
        handleProcedureInsertBalance("INSERT_BALANCE_TO_CUSTOMER", balance_id, cust_id, package_id, bal_lvl_minutes, bal_lvl_sms, bal_lvl_data, sdate, edate);
    }



    public void updatePassword(String email, String tcNumber, String encryptedPassword) throws
            IOException,
            ProcCallException,
            InterruptedException {
        Client client1 = getClient();
        client1.callProcedure("UPDATE_CUSTOMER_PASSWORD", encryptedPassword, email, tcNumber);
        client1.close();

    }


    public VoltPackage getPackageByMsisdn(String msisdn) {
        try {
            Client client1 = getClient();
            ClientResponse response = client1.callProcedure("GET_PACKAGE_INFO_BY_MSISDN", msisdn);
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
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: GET_PACKAGE_INFO_BY_MSISDN", e);
            throw new RuntimeException("Error while calling procedure: GET_PACKAGE_INFO_BY_MSISDN", e);
        }
    }

    public Optional<VoltCustomer> getCustomerByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        Client client1 = getClient();
        ClientResponse response = client1.callProcedure("GET_CUSTOMER_INFO_BY_MSISDN", msisdn);

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

                client1.close();
                return Optional.of(customer);
            }
        }
        client1.close();
        throw new RuntimeException("Customer not found with this MSISDN: " + msisdn);
    }

    public VoltCustomerBalance getRemainingCustomerBalanceByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        Client client1 = getClient();
        ClientResponse response = client1.callProcedure("GET_REMAINING_CUSTOMER_BALANCE_BY_MSISDN", msisdn);

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

                client1.close();
                return balanceResponse;
            }
        }
        client1.close();
        throw new RuntimeException("Customer balance not found for msisdn: " + msisdn);
    }

    public VoltPackageDetails getPackageInfoByPackageId(int packageId) throws IOException, ProcCallException, InterruptedException {
        Client client1 = getClient();
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
        client1.close();
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

    public void getUserDetailsAsync(String msisdn, Consumer<UserDetails> onSuccess) {
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        VoltTable resultTable = response.getResults()[0];
                        if (resultTable.advanceRow()) {
                            UserDetails userDetails = new UserDetails(
                                    resultTable.getString("NAME"),
                                    resultTable.getString("SURNAME"),
                                    resultTable.getString("EMAIL"),
                                    (int) resultTable.getLong("PACKAGE_ID")
                            );
                            // onSuccess kullanarak sonucu işleyin
                            onSuccess.accept(userDetails);
                        } else {
                            System.err.println("No data returned from procedure");
                        }
                    } else {
                        System.err.println("Procedure call failed: " + response.getStatusString());
                    }
                }
            }, "GET_CUSTOMER_INFO_PACKAGE_BY_MSISDN", msisdn);
        } catch (IOException e) {
            System.err.println("Error while calling procedure: GET_CUSTOMER_INFO_PACKAGE_BY_MSISDN: " + e.getMessage());
        }
    }


    private int handleProcedureAsInt(String procedureName) {
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
                return (int) resultTable.fetchRow(0).get(0); // Cast long to int
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


    public void executeStringAsyncProcedure(String procName, String msisdn) {
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        VoltTable resultTable = response.getResults()[0];
                        if (resultTable.advanceRow()) {  // İlk satıra geçiş yapılıyor
                            String result = resultTable.getString(0);
                            System.out.println("Sonuç: " + result);
                        } else {
                            System.err.println("Sonuçlar boş veya geçersiz.");
                        }
                    } else {
                        System.err.println("Çağrıda hata: " + response.getStatusString());
                    }
                }
            }, procName, msisdn);
        } catch (IOException e) {
            System.err.println("Prosedür çağrısında hata: " + e.getMessage());
        }
    }


    public void executeIntegerAsyncProcedure(String procName, String msisdn) {
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() == ClientResponse.SUCCESS) {
                        VoltTable resultTable = response.getResults()[0];
                        if (resultTable.advanceRow()) {  // İlk satıra geçiş yapılıyor
                            long result = resultTable.getLong(0);
                            System.out.println("Sonuç: " + result);
                        } else {
                            System.err.println("Sonuçlar boş veya geçersiz.");
                        }
                    } else {
                        System.err.println("Çağrıda hata: " + response.getStatusString());
                    }
                }
            }, procName, msisdn);
        } catch (IOException e) {
            System.err.println("Prosedür çağrısında hata: " + e.getMessage());
        }
    }




    private void updateProcedureAsync(String procedureName, int usage, String msisdn) {
        try {
            client.callProcedure(new ProcedureCallback() {
                @Override
                public void clientCallback(ClientResponse response) {
                    if (response.getStatus() != ClientResponse.SUCCESS) {
                        // Eğer prosedür çağrısı başarısız olursa hata fırlatıyoruz
                        System.err.println("Procedure call failed: " + response.getStatusString());
                    } else {}
                }
            }, procedureName, usage, msisdn);
        } catch (IOException e) {
            logger.error("Error while calling procedure: " + procedureName, e);
            throw new RuntimeException("Error while calling procedure: " + procedureName, e);
        }
    }



    private void handleProcedure(String procedureName, int usage, String msisdn) {
        try {
            ClientResponse response = client.callProcedure(procedureName, usage, msisdn);
            if (response.getStatus() != ClientResponse.SUCCESS) {
                throw new RuntimeException("Procedure call failed: " + response.getStatusString());
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + procedureName, e);
            throw new RuntimeException("Error while calling procedure: " + procedureName, e);
        }
    }


    /*private void handleProcedureChangePassword(String password, String email, String tc_no) {
        try {
            Client client1 = getClient();
            ClientResponse response = client1.callProcedure("UPDATE_CUSTOMER_PASSWORD", password, email, tc_no);
            if (response.getStatus() != ClientResponse.SUCCESS) {
                throw new RuntimeException("Procedure call failed: " + response.getStatusString());
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + "UPDATE_CUSTOMER_PASSWORD", e);
            throw new RuntimeException("Error while calling procedure: " + "UPDATE_CUSTOMER_PASSWORD", e);
        }
    }*/


    private void handleProcedureInsertCustomer(int cust_id, String name, String surname, String msisdn, String email, String password, Timestamp sdate, String TCNumber) {
        try {
            ClientResponse response = client.callProcedure("INSERT_NEW_CUSTOMER", cust_id, name, surname, msisdn, email, password, sdate, TCNumber);
            if (response.getStatus() != ClientResponse.SUCCESS) {
                throw new RuntimeException("Procedure call failed: " + response.getStatusString());
            }
        } catch (IOException | ProcCallException e) {
            logger.error("Error while calling procedure: " + "INSERT_NEW_CUSTOMER", e);
            throw new RuntimeException("Error while calling procedure: " + "INSERT_NEW_CUSTOMER", e);
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

    public void getName(String msisdn, Consumer<String> onSuccess) {
        getUserDetailsAsync(msisdn, userDetails -> {
            onSuccess.accept(userDetails.getName());
        });
    }


    public void getLastName(String msisdn, Consumer<String> onSuccess) {
        getUserDetailsAsync(msisdn, userDetails -> {
            onSuccess.accept(userDetails.getLastName());
        });
    }

    public void getUserEmail(String msisdn, Consumer<String> onSuccess) {
        getUserDetailsAsync(msisdn, userDetails -> {
            onSuccess.accept(userDetails.getEmail());
        });
    }

    public void getPackageId(String msisdn, Consumer<Integer> onSuccess) {
        getUserDetailsAsync(msisdn, userDetails -> {
            onSuccess.accept(userDetails.getPackageId());
        });
    }


}