package com.i2i.aom.repository;

import com.i2i.aom.constant.OracleQueries;
import com.i2i.aom.helper.OracleConnection;
import com.i2i.aom.helper.VoltDBConnection;
import com.i2i.aom.model.Customer;
import com.i2i.aom.request.CreateBalanceRequest;
import com.i2i.aom.request.RegisterCustomerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class CustomerRepository {
    private final OracleConnection oracleConnection;
    private final VoltDBConnection voltDBConnection;
    private final BalanceRepository balanceRepository;
    
    private static final Logger logger = Logger.getLogger(CustomerRepository.class.getName());

    public CustomerRepository(OracleConnection oracleConnection,
                              VoltDBConnection voltDBConnection,
                              BalanceRepository balanceRepository) {
        this.oracleConnection = oracleConnection;
        this.voltDBConnection = voltDBConnection;
        this.balanceRepository = balanceRepository;
    }

    //oracle
    public List<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        List<Customer> customers = new ArrayList<>();
        Connection connection = oracleConnection.getOracleConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(OracleQueries.GET_ALL_CUSTOMERS);

        while (resultSet.next()){
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
        connection.close();
        return customers;
    }

    public ResponseEntity createUserInOracle(RegisterCustomerRequest registerCustomerRequest) throws SQLException, ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();

        PreparedStatement packageStmt = connection.prepareStatement(OracleQueries.SELECT_PACKAGE_ID);
        packageStmt.setString(1, registerCustomerRequest.packageName());
        ResultSet packageResultSet = packageStmt.executeQuery();

        if (!packageResultSet.next()) {
            packageStmt.close();
            connection.close();
            return new ResponseEntity<>("Package not found", HttpStatus.NOT_FOUND);
        }

        int packageId = packageResultSet.getInt("PACKAGE_ID");
        packageStmt.close();

        logger.info("Retrieved package id: " + packageId);

        String encodedPassword = encodePassword(registerCustomerRequest.password());

        String insertCustomerSQL = "INSERT INTO CUSTOMER (CUST_ID, NAME, SURNAME, MSISDN, EMAIL, PASSWORD, SDATE, TC_NO) " +
                "VALUES (cust_id_sequence.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement customerStmt = connection.prepareStatement(insertCustomerSQL, new String[]{"CUST_ID"});
        customerStmt.setString(1, registerCustomerRequest.name());
        customerStmt.setString(2, registerCustomerRequest.surname());
        customerStmt.setString(3, registerCustomerRequest.msisdn());
        customerStmt.setString(4, registerCustomerRequest.email());
        customerStmt.setString(5, encodedPassword);
        customerStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        customerStmt.setString(7, registerCustomerRequest.TCNumber());
        customerStmt.executeUpdate();

        ResultSet generatedKeys = customerStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            int customerId = generatedKeys.getInt(1);

            logger.info("Generated customer ID: " + customerId);

            CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
                    .customerId(customerId)
                    .packageId(packageId)
                    .build();
            balanceRepository.createOracleBalance(createBalanceRequest);
        }

        customerStmt.close();
        connection.close();

        return new ResponseEntity<>("Customer created successfully", HttpStatus.CREATED);
    }



    //volt
    public Optional<Customer> getCustomerByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        Client client = voltDBConnection.getClient();
        ClientResponse response = client.callProcedure("GetCustomerByMsisdn", msisdn);

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
        return Optional.empty();
    }

    public ResponseEntity createUserInVoltdb(RegisterCustomerRequest registerCustomerRequest) throws IOException, ProcCallException, InterruptedException {
        Client client = voltDBConnection.getClient();

        // Retrieve package ID
        ClientResponse packageResponse = client.callProcedure("GetPackageByName", registerCustomerRequest.packageName());
        VoltTable packageTable = packageResponse.getResults()[0];

        if (!packageTable.advanceRow()) {
            client.close();
            return new ResponseEntity<>("Package not found", HttpStatus.NOT_FOUND);
        }

        int packageId = (int) packageTable.getLong("PACKAGE_ID");

        ClientResponse maxIdResponse = client.callProcedure("GetMaxCustomerId");
        VoltTable maxIdTable = maxIdResponse.getResults()[0];
        int maxCustomerId = 0;
        if (maxIdTable.advanceRow()) {
            maxCustomerId = (int) maxIdTable.getLong("MAX_CUST_ID");
        }
        int customerId = maxCustomerId + 1;

        // Insert customer details
        ClientResponse customerResponse = client.callProcedure("InsertCustomer",
                customerId,
                registerCustomerRequest.name(),
                registerCustomerRequest.surname(),
                registerCustomerRequest.msisdn(),
                registerCustomerRequest.email(),
                encodePassword(registerCustomerRequest.password()),
                new Timestamp(System.currentTimeMillis()),
                registerCustomerRequest.TCNumber()
        );

        VoltTable customerTable = customerResponse.getResults()[0];
        if (!customerTable.advanceRow()) {
            client.close();
            return new ResponseEntity<>("Failed to create customer", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Create balance request
        CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
                .customerId(customerId)
                .packageId(packageId)
                .build();

        // Insert balance
        ResponseEntity balanceResponse = balanceRepository.createVoltBalance(createBalanceRequest);

        client.close();
        return balanceResponse;
    }

    private static String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }


}
