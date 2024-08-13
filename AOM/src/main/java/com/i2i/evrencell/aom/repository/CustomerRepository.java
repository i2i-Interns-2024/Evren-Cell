package com.i2i.evrencell.aom.repository;

import com.i2i.evrencell.aom.constant.OracleQueries;
import com.i2i.evrencell.aom.encryption.CustomerPasswordEncoder;
import com.i2i.evrencell.aom.exception.NotFoundException;
import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.aom.helper.VoltDBConnection;
import com.i2i.evrencell.aom.model.Customer;
import com.i2i.evrencell.aom.request.CreateBalanceRequest;
import com.i2i.evrencell.aom.request.RegisterCustomerRequest;
import oracle.jdbc.OracleTypes;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * This class is used to handle the database operations for the customers.
 */
@Repository
public class CustomerRepository {
    private final OracleConnection oracleConnection;
    private final VoltDBConnection voltDBConnection;
    private final BalanceRepository balanceRepository;
    private final CustomerPasswordEncoder customerPasswordEncoder;
    
    private static final Logger logger = Logger.getLogger(CustomerRepository.class.getName());

    public CustomerRepository(OracleConnection oracleConnection,
                              VoltDBConnection voltDBConnection,
                              BalanceRepository balanceRepository,
                              CustomerPasswordEncoder customerPasswordEncoder) {
        this.oracleConnection = oracleConnection;
        this.voltDBConnection = voltDBConnection;
        this.balanceRepository = balanceRepository;
        this.customerPasswordEncoder = customerPasswordEncoder;
    }

    // ==ORACLE OPERATIONS==

    /**
     * This method is used to get all customers from the Oracle DB.
     * @return List<Customer>
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        List<Customer> customers = new ArrayList<>();
        Connection connection = oracleConnection.getOracleConnection();
        CallableStatement callableStatement = connection.prepareCall("{call get_all_customers(?)}");

        callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
        callableStatement.execute();

        ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

        while (resultSet.next()) {
            Integer customerId = resultSet.getInt("CUST_ID");
            String msisdn = resultSet.getString("MSISDN");
            String name = resultSet.getString("NAME");
            String surname = resultSet.getString("SURNAME");
            String email = resultSet.getString("EMAIL");
            Timestamp sdate = resultSet.getTimestamp("SDATE");
            String tcNo = resultSet.getString("TC_NO");

            Customer customer = Customer.builder()
                    .customerId(customerId)
                    .msisdn(msisdn)
                    .email(email)
                    .name(name)
                    .surname(surname)
                    .sDate(sdate)
                    .TCNumber(tcNo)
                    .build();
            customers.add(customer);
        }

        resultSet.close();
        callableStatement.close();
        connection.close();

        return customers;
    }


    /**
     * This method is used create a customer in the Oracle DB.
     * It first checks if the customer already exists in the Oracle DB.
     * If the customer does not exist, it creates the customer in the Oracle DB with procedure calls.
     * @param registerCustomerRequest
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ResponseEntity<String> createUserInOracle(RegisterCustomerRequest registerCustomerRequest) throws SQLException, ClassNotFoundException {

        if (customerExists(registerCustomerRequest.msisdn(), registerCustomerRequest.email(), registerCustomerRequest.TCNumber())) {
            return new ResponseEntity<>("This customer already exists in Oracle DB.", HttpStatus.CONFLICT);
        }
        System.out.println("Customer created successfully in Oracle DB1.");

        Connection connection = oracleConnection.getOracleConnection();

        CallableStatement packageStmt = connection.prepareCall("{call SELECT_PACKAGE_ID(?, ?)}");
        packageStmt.setString(1, registerCustomerRequest.packageName());
        packageStmt.registerOutParameter(2, Types.INTEGER);
        packageStmt.execute();

        int packageId = packageStmt.getInt(2);
        packageStmt.close();

        if (packageId == 0) {
            connection.close();
            throw new NotFoundException("Package Not Found IN ORACLE.");
        }

        logger.info("Retrieved package id: " + packageId);

        String encodedPassword = customerPasswordEncoder.encrypt(registerCustomerRequest.password());
        logger.info("Customer created successfully in Oracle DB2.");

        CallableStatement customerStmt = connection.prepareCall("{call INSERT_CUSTOMER(?, ?, ?, ?, ?, ?, ?)}");
        customerStmt.setString(1, registerCustomerRequest.name());
        customerStmt.setString(2, registerCustomerRequest.surname());
        customerStmt.setString(3, registerCustomerRequest.msisdn());
        customerStmt.setString(4, registerCustomerRequest.email());
        customerStmt.setString(5, encodedPassword);
        customerStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        customerStmt.setString(7, registerCustomerRequest.TCNumber());
        customerStmt.execute();
        customerStmt.close();

        logger.info("Customer created successfully in Oracle DB.");
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(OracleQueries.SELECT_CUSTOMER_ID);
        int customerId = 0;
        if (rs.next()) {
            customerId = rs.getInt(1);
        }
        rs.close();
        stmt.close();

        logger.info("Generated customer ID: " + customerId);

        CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
                .customerId(customerId)
                .packageId(packageId)
                .build();
        balanceRepository.createOracleBalance(createBalanceRequest);

        connection.close();

        return new ResponseEntity<>("Customer created successfully", HttpStatus.CREATED);
    }


    /**
    * This method checks if the customer already exists in the Oracle DB.
    * If the customer exists, it returns true, otherwise it returns false.
    * @param msisdn
    * @param email
    * @param tcNo
    * @return boolean
    * @throws SQLException
    * @throws ClassNotFoundException
     */
    private boolean customerExists(String msisdn, String email, String tcNo) throws SQLException, ClassNotFoundException {
        System.out.println("customer exists.");

        Connection connection = oracleConnection.getOracleConnection();
        PreparedStatement stmt = connection.prepareStatement(OracleQueries.IS_CUSTOMER_ALREADY_EXISTS);
        stmt.setString(1, msisdn);
        stmt.setString(2, email);
        stmt.setString(3, tcNo);
        ResultSet rs = stmt.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt(1) > 0;
        }
        rs.close();
        stmt.close();
        connection.close();
        System.out.println("customer exists.2");

        return exists;
    }

    /**
     * This method is used to get the encoded password of a customer by their MSISDN.
     * @param msisdn
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String getEncodedCustomerPasswordByMsisdn(String msisdn) throws SQLException, ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();
        PreparedStatement stmt = connection.prepareStatement(OracleQueries.SELECT_PASSWORD);
        stmt.setString(1, msisdn);
        ResultSet resultSet= stmt.executeQuery();
        String encodedPassword = null;
        if (resultSet.next()) {
            encodedPassword = resultSet.getString("PASSWORD");
        }
        resultSet.close();
        stmt.close();
        connection.close();
        return encodedPassword;

    }

    /**
     * This method is used to fetch the customer password by their MSISDN from the VoltDB.
     * It calls a procedure in the VoltDB to get the password.
     * If the customer exists in the VoltDB, it returns the encoded password.
     * @param msisdn
     * @return String
     */
    public String getEncodedCustomerPasswordByMsisdnFromVolt(String msisdn) throws IOException, ProcCallException, InterruptedException {
        Client client = voltDBConnection.getClient();
        ClientResponse response = client.callProcedure("GET_CUSTOMER_PASSWORD_BY_MSISDN", msisdn);
        String encodedPassword = null;
        if (response.getStatus() == ClientResponse.SUCCESS) {
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                encodedPassword = resultTable.getString("PASSWORD");
            }
        }
        client.close();
        return encodedPassword;
    }

    /**
     * This method is used to get a customer by their MSISDN.
     * @param email
     * @param tcNumber
     * @param encryptedPassword
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void updatePasswordInOracle(String email, String tcNumber, String encryptedPassword) throws
            SQLException,
            ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();
        CallableStatement updatePasswordInOracleStatement = connection.prepareCall("{call UPDATE_CUSTOMER_PASSWORD(?, ?, ?)}");
        updatePasswordInOracleStatement.setString(1, email);
        updatePasswordInOracleStatement.setString(2, tcNumber);
        updatePasswordInOracleStatement.setString(3, encryptedPassword);
        updatePasswordInOracleStatement.execute();
        updatePasswordInOracleStatement.close();
        connection.close();
    }

    /**
     * This method is used to insert a notification log in the Oracle DB.
     * It inserts the notification type, notification time, and customer ID into the notification log table via a procedure call.
     * @param notificationType
     * @param notificationTime
     * @param customerId
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void insertNotificationLogInOracle(String notificationType, Timestamp notificationTime, int customerId) throws SQLException, ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();
        CallableStatement notificationLogCallabaleStatement = connection.prepareCall("{call INSERT_NOTIFICATION_LOG(?, ?, ?)}");
        notificationLogCallabaleStatement.setString(1, notificationType);
        notificationLogCallabaleStatement.setTimestamp(2, notificationTime);
        notificationLogCallabaleStatement.setInt(3, customerId);
        notificationLogCallabaleStatement.execute();
        notificationLogCallabaleStatement.close();
        connection.close();
    }



    // ==VOLT OPERATIONS==

    /**
     * This method is used to get a customer by their MSISDN.
     * @param msisdn
     * @return
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public Optional<Customer> getCustomerByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        Client client = voltDBConnection.getClient();
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

                Customer customer = Customer.builder()
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
        throw new NotFoundException("Customer not found with this MSISDN: " + msisdn);
    }

    /**
     * This method is used to create a customer in the VoltDB.
     * @param registerCustomerRequest
     * @return ResponseEntity
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public ResponseEntity<String> createUserInVoltdb(RegisterCustomerRequest registerCustomerRequest) throws IOException, ProcCallException, InterruptedException {
        Client client = voltDBConnection.getClient();

        ClientResponse packageResponse = client.callProcedure("GET_PACKAGE_INFO_BY_PACKAGE_NAME", registerCustomerRequest.packageName());
        VoltTable packageTable = packageResponse.getResults()[0];

        if (!packageTable.advanceRow()) {
            client.close();
            return new ResponseEntity<>("Package not found IN VOLT", HttpStatus.NOT_FOUND);
        }

        int packageId = (int) packageTable.getLong("PACKAGE_ID");

        ClientResponse maxIdResponse = client.callProcedure("GET_MAX_CUSTOMER_ID");
        VoltTable maxIdTable = maxIdResponse.getResults()[0];
        int maxCustomerId = 0;
        if (maxIdTable.advanceRow()) {
            maxCustomerId = (int) maxIdTable.getLong("MAX_CUST_ID");
        }
        int customerId = maxCustomerId + 1;

        ClientResponse customerResponse = client.callProcedure("INSERT_NEW_CUSTOMER",
                customerId,
                registerCustomerRequest.name(),
                registerCustomerRequest.surname(),
                registerCustomerRequest.msisdn(),
                registerCustomerRequest.email(),
                customerPasswordEncoder.encrypt(registerCustomerRequest.password()),
                new Timestamp(System.currentTimeMillis()),
                registerCustomerRequest.TCNumber()
        );

        VoltTable customerTable = customerResponse.getResults()[0];
        if (!customerTable.advanceRow()) {
            client.close();
            return new ResponseEntity<>("Failed to create customer", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
                .customerId(customerId)
                .packageId(packageId)
                .build();

        ResponseEntity<String> balanceResponse = balanceRepository.createVoltBalance(createBalanceRequest);

        client.close();
        return balanceResponse;
    }


    /**
     * This method is used to check if a customer exists in the VoltDB.
     * @param email
     * @param tcNumber
     * @param encryptedPassword
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public void updatePasswordInVoltDB(String email, String tcNumber, String encryptedPassword) throws
            IOException,
            ProcCallException,
            InterruptedException {
        Client client = voltDBConnection.getClient();
        client.callProcedure("UPDATE_CUSTOMER_PASSWORD", encryptedPassword, email, tcNumber);
        client.close();

    }


    /**
     * This method is used to insert a notification log in the VoltDB after a password reset.
     * @param notificationType
     * @param notificationTime
     * @param customerId
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public void insertNotificationLogInVoltDB(String notificationType, Timestamp notificationTime, int customerId) throws
            IOException,
            ProcCallException,
            InterruptedException {

        Client client = voltDBConnection.getClient();
        ClientResponse maxIdResponse = client.callProcedure("GET_MAX_NOTIFICATION_ID");
        VoltTable maxIdTable = maxIdResponse.getResults()[0];
        int maxNotificationId = 0;
        if (maxIdTable.advanceRow()){
            maxNotificationId = (int) maxIdTable.getLong("MAX_NOTIFICATION_ID");
        }

        int newNotificationId = maxNotificationId + 1;
        client.callProcedure("INSERT_NOTIFICATION_LOG", newNotificationId, notificationType, notificationTime, customerId);
//        client.callProcedure("InsertNotificationLog", newNotificationId, notificationType, notificationTime, customerId);
        client.close();

    }


    /**
     * This method is used to check if a customer exists in the Oracle and VoltDB.
     * If the customer exists in both databases, it returns true, otherwise it returns false.
     * @param email
     * @param tcNumber
     * @return boolean
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public boolean checkCustomerExists(String email, String tcNumber) throws
                                                                            SQLException,
                                                                            ClassNotFoundException
                                                                             {
        // Check if the customer exists in Oracle
        Connection connection = oracleConnection.getOracleConnection();
        CallableStatement oracleCallableStatement = connection.prepareCall("{call CHECK_CUSTOMER_EXISTS_BY_MAIL_AND_TCNO(?, ?, ?)}");
        oracleCallableStatement.setString(1, email);
        oracleCallableStatement.setString(2, tcNumber);
        oracleCallableStatement.registerOutParameter(3, Types.INTEGER);
        oracleCallableStatement.execute();
        int oracleStatementReturnCount = oracleCallableStatement.getInt(3);
        oracleCallableStatement.close();
        connection.close();
        boolean ifCustomerExistsInOracle = oracleStatementReturnCount > 0;
        return ifCustomerExistsInOracle;

        // Check if the customer exists in VoltDB
//        Client voltClient = voltDBConnection.getClient();
//        ClientResponse voltResponse = voltClient.callProcedure("CHECK_CUSTOMER_EXISTS_BY_MAIL_AND_TCNO", email, tcNumber);
//        VoltTable voltTable = voltResponse.getResults()[0];
//        boolean ifCustomerExistsInVolt = false;
//        if(voltTable.advanceRow()){
//            ifCustomerExistsInVolt = voltTable.getLong(0) > 0;
//        }
//        voltClient.close();
//        return ifCustomerExistsInOracle && ifCustomerExistsInVolt;
    }
}
