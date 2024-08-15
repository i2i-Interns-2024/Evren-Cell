package com.i2i.evrencell.aom.repository;

import com.i2i.evrencell.aom.constant.OracleQueries;
import com.i2i.evrencell.aom.encryption.CustomerPasswordEncoder;
import com.i2i.evrencell.aom.exception.NotFoundException;
import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.aom.model.Customer;
import com.i2i.evrencell.aom.request.CreateBalanceRequest;
import com.i2i.evrencell.aom.request.RegisterCustomerRequest;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import oracle.jdbc.OracleTypes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
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

/**
 * This class is used to handle the database operations for the customers.
 */
@Repository
public class CustomerRepository {
    private static final Logger logger = LogManager.getLogger(CustomerRepository.class);
    private final OracleConnection oracleConnection;
    private final BalanceRepository balanceRepository;
    private final CustomerPasswordEncoder customerPasswordEncoder;
    private final VoltdbOperator voltdbOperator = new VoltdbOperator();
    
    public CustomerRepository(OracleConnection oracleConnection,
                              BalanceRepository balanceRepository,
                              CustomerPasswordEncoder customerPasswordEncoder) {
        this.oracleConnection = oracleConnection;
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
        logger.debug("Getting all customers from Oracle DB");
        List<Customer> customers = new ArrayList<>();
        logger.debug("Creating connection to Oracle DB");
        Connection connection = oracleConnection.getOracleConnection();
        logger.debug("Connection created to Oracle DB");
        logger.debug("Creating callable statement to get all customers");
        CallableStatement callableStatement = connection.prepareCall("{call get_all_customers(?)}");

        logger.debug("Registering out parameter for get_all_customers");
        callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
        logger.debug("Executing get_all_customers");
        callableStatement.execute();
        logger.debug("get_all_customers executed successfully");

        logger.debug("Getting result set from get_all_customers");
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
        logger.debug("Result set retrieved successfully from get_all_customers");

        logger.debug("Closing result set, callable statement and connection");
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

        logger.debug("Creating customer in Oracle DB");
        if (customerExists(registerCustomerRequest.msisdn(), registerCustomerRequest.email(), registerCustomerRequest.TCNumber())) {
            return new ResponseEntity<>("This customer already exists in Oracle DB.", HttpStatus.CONFLICT);
        }
        logger.debug("Connecting to Oracle DB");
        Connection connection = oracleConnection.getOracleConnection();
        logger.debug("Connected to Oracle DB");

        logger.debug("Retrieving package id from Oracle DB.");
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

        logger.debug("Retrieved package id: " + packageId);

        logger.debug("Encrypting password for customer.");
        String encodedPassword = customerPasswordEncoder.encrypt(registerCustomerRequest.password());
        logger.debug("Password encrypted successfully.");

        logger.debug("Creating customer in Oracle DB.");
        CallableStatement customerStmt = connection.prepareCall("{call INSERT_CUSTOMER(?, ?, ?, ?, ?, ?, ?)}");
        customerStmt.setString(1, registerCustomerRequest.name());
        customerStmt.setString(2, registerCustomerRequest.surname());
        customerStmt.setString(3, registerCustomerRequest.msisdn());
        customerStmt.setString(4, registerCustomerRequest.email());
        customerStmt.setString(5, encodedPassword);
        customerStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        customerStmt.setString(7, registerCustomerRequest.TCNumber());
        logger.debug("Executing INSERT_CUSTOMER");
        customerStmt.execute();
        logger.debug("INSERT_CUSTOMER executed successfully");
        customerStmt.close();
        logger.debug("Customer created successfully in Oracle DB.");

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(OracleQueries.SELECT_CUSTOMER_ID);
        int customerId = 0;
        if (rs.next()) {
            customerId = rs.getInt(1);
        }
        rs.close();
        stmt.close();


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
        logger.debug("Checking if customer exists in Oracle DB.");

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
        logger.debug("Getting encoded password of customer by MSISDN");
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
        logger.debug("Updating password in Oracle DB");
        logger.debug("Connecting to Oracle DB");
        Connection connection = oracleConnection.getOracleConnection();
        logger.debug("Connected to Oracle DB");
        logger.debug("Creating callable statement to update password in Oracle DB");
        CallableStatement updatePasswordInOracleStatement = connection.prepareCall("{call UPDATE_CUSTOMER_PASSWORD(?, ?, ?)}");
        updatePasswordInOracleStatement.setString(1, email);
        updatePasswordInOracleStatement.setString(2, tcNumber);
        updatePasswordInOracleStatement.setString(3, encryptedPassword);
        logger.debug("Executing UPDATE_CUSTOMER_PASSWORD");
        updatePasswordInOracleStatement.execute();
        logger.debug("UPDATE_CUSTOMER_PASSWORD executed successfully");
        logger.debug("Closing callable statement and connection");
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
        logger.debug("Inserting notification log in Oracle DB");
        Connection connection = oracleConnection.getOracleConnection();
        CallableStatement notificationLogCallabaleStatement = connection.prepareCall("{call INSERT_NOTIFICATION_LOG(?, ?, ?)}");
        notificationLogCallabaleStatement.setString(1, notificationType);
        notificationLogCallabaleStatement.setTimestamp(2, notificationTime);
        notificationLogCallabaleStatement.setInt(3, customerId);
        logger.debug("Executing INSERT_NOTIFICATION_LOG");
        notificationLogCallabaleStatement.execute();
        logger.debug("INSERT_NOTIFICATION_LOG executed successfully");
        logger.debug("Closing callable statement and connection");
        notificationLogCallabaleStatement.close();
        connection.close();
    }



    // ==VOLT OPERATIONS==
    /**
     * This method is used to create a customer in the VoltDB.
     * @param registerCustomerRequest
     * @return ResponseEntity
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public ResponseEntity<String> createUserInVoltdb(RegisterCustomerRequest registerCustomerRequest) throws IOException, ProcCallException, InterruptedException {

        logger.debug("Creating customer in VoltDB");
        int packageId = voltdbOperator.getPackageIdByName(registerCustomerRequest.packageName());
        int maxCustomerId = voltdbOperator.getMaxCustomerId();
        int customerId = maxCustomerId + 1;

        logger.debug("Inserting customer in VoltDB");
        voltdbOperator.insertCustomer(
                customerId,
                registerCustomerRequest.name(),
                registerCustomerRequest.surname(),
                registerCustomerRequest.msisdn(),
                registerCustomerRequest.email(),
                customerPasswordEncoder.encrypt(registerCustomerRequest.password()),
                new Timestamp(System.currentTimeMillis()),
                registerCustomerRequest.TCNumber()
        );
        logger.debug("Customer inserted successfully in VoltDB");
        CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
                .customerId(customerId)
                .packageId(packageId)
                .build();


        return balanceRepository.createVoltBalance(createBalanceRequest);
    }


    public void updatePasswordInVoltDB(String email, String tcNumber, String encryptedPassword) throws IOException, InterruptedException, ProcCallException {
        logger.debug("Updating password in VoltDB");
        voltdbOperator.updatePassword(email, tcNumber, encryptedPassword);
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
                                                                            ClassNotFoundException {
        logger.debug("Checking if customer exists in Oracle");
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
        logger.debug("Customer exists in Oracle: " + ifCustomerExistsInOracle);
        return ifCustomerExistsInOracle;

    }
}
