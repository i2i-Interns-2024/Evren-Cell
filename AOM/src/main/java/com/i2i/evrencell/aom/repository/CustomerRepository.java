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
import org.slf4j.LoggerFactory;
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
import java.util.logging.Logger;

/**
 * This class is used to handle the database operations for the customers.
 */
@Repository
public class CustomerRepository {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CustomerRepository.class);
    private final OracleConnection oracleConnection;
    private final BalanceRepository balanceRepository;
    private final CustomerPasswordEncoder customerPasswordEncoder;
    private final VoltdbOperator voltdbOperator = new VoltdbOperator();
    
    private static final Logger logger = Logger.getLogger(CustomerRepository.class.getName());

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
     * This method is used to create a customer in the VoltDB.
     * @param registerCustomerRequest
     * @return ResponseEntity
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public ResponseEntity<String> createUserInVoltdb(RegisterCustomerRequest registerCustomerRequest) throws IOException, ProcCallException, InterruptedException {

        logger.info("Creating customer in VoltDB");
        int packageId = voltdbOperator.getPackageIdByName(registerCustomerRequest.packageName());
        logger.info("Package ID: " + packageId);
        int maxCustomerId = voltdbOperator.getMaxCustomerId();
        logger.info("Max customer ID: " + maxCustomerId);
        int customerId = maxCustomerId + 1;

        logger.info("Inserting customer in VoltDB");
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
        logger.info("Customer inserted in VoltDB");

        logger.info("Creating balance in VoltDB");
        CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
                .customerId(customerId)
                .packageId(packageId)
                .build();

        //        client.close();

        return balanceRepository.createVoltBalance(createBalanceRequest);
    }


    public void updatePasswordInVoltDB(String email, String tcNumber, String encryptedPassword) throws IOException, InterruptedException, ProcCallException {
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

    }
}
