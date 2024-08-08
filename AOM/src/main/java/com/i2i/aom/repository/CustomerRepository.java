package com.i2i.aom.repository;

import com.i2i.aom.constant.OracleQueries;
import com.i2i.aom.helper.OracleConnection;
import com.i2i.aom.helper.VoltDBConnection;
import com.i2i.aom.model.Customer;
import com.i2i.aom.request.CreateBalanceRequest;
import com.i2i.aom.request.RegisterCustomerRequest;
import oracle.jdbc.OracleTypes;
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

    //oracledbb
//    public List<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
//        List<Customer> customers = new ArrayList<>();
//        Connection connection = oracleConnection.getOracleConnection();
//        Statement statement = connection.createStatement();
////        ResultSet resultSet = statement.executeQuery(OracleQueries.GET_ALL_CUSTOMERS);
//
//        //procedure
//        CallableStatement callableStatement = connection.prepareCall("{call C##bilal.get_all_customers()}");
//        ResultSet resultSet = callableStatement.executeQuery();
//
//        while (resultSet.next()){
//            Integer customerId = resultSet.getInt("CUST_ID");
//            String msisdn = resultSet.getString("MSISDN");
//            String name = resultSet.getString("NAME");
//            String surname = resultSet.getString("SURNAME");
//            String email = resultSet.getString("EMAIL");
//            Timestamp sdate = resultSet.getTimestamp("SDATE");
//            String tcNo = resultSet.getString("TC_NO");
//
//            Customer customer = Customer.builder()
//                    .customerId(customerId)
//                    .msisdn(msisdn)
//                    .email(email)
//                    .name(name)
//                    .surname(surname)
//                    .sDate(sdate)
//                    .TCNumber(tcNo)
//                    .build();
//            customers.add(customer);
//        }
//
//        resultSet.close();
//        callableStatement.close();
//
//        connection.close();
//        return customers;
//    }


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

//    public ResponseEntity createUserInOracle(RegisterCustomerRequest registerCustomerRequest) throws SQLException, ClassNotFoundException {
//        Connection connection = oracleConnection.getOracleConnection();
//
//        PreparedStatement packageStmt = connection.prepareStatement(OracleQueries.SELECT_PACKAGE_ID);
//        packageStmt.setString(1, registerCustomerRequest.packageName());
//        ResultSet packageResultSet = packageStmt.executeQuery();
//
//        if (!packageResultSet.next()) {
//            packageStmt.close();
//            connection.close();
//            return new ResponseEntity<>("Package not found IN ORACLE", HttpStatus.NOT_FOUND);
//        }
//
//        int packageId = packageResultSet.getInt("PACKAGE_ID");
//        packageStmt.close();
//
//        logger.info("Retrieved package id: " + packageId);
//
//        String encodedPassword = encodePassword(registerCustomerRequest.password());
//
//        String insertCustomerSQL = "INSERT INTO CUSTOMER (CUST_ID, NAME, SURNAME, MSISDN, EMAIL, PASSWORD, SDATE, TC_NO) " +
//                "VALUES (cust_id_sequence.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
//        PreparedStatement customerStmt = connection.prepareStatement(insertCustomerSQL, new String[]{"CUST_ID"});
//        customerStmt.setString(1, registerCustomerRequest.name());
//        customerStmt.setString(2, registerCustomerRequest.surname());
//        customerStmt.setString(3, registerCustomerRequest.msisdn());
//        customerStmt.setString(4, registerCustomerRequest.email());
//        customerStmt.setString(5, encodedPassword);
//        customerStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
//        customerStmt.setString(7, registerCustomerRequest.TCNumber());
//        customerStmt.executeUpdate();
//
//        ResultSet generatedKeys = customerStmt.getGeneratedKeys();
//        if (generatedKeys.next()) {
//            int customerId = generatedKeys.getInt(1);
//
//            logger.info("Generated customer ID: " + customerId);
//
//            CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
//                    .customerId(customerId)
//                    .packageId(packageId)
//                    .build();
//            balanceRepository.createOracleBalance(createBalanceRequest);
//        }
//
//        customerStmt.close();
//        connection.close();
//
//        return new ResponseEntity<>("Customer created successfully", HttpStatus.CREATED);
//    }


    public ResponseEntity createUserInOracle(RegisterCustomerRequest registerCustomerRequest) throws SQLException, ClassNotFoundException {

        if (customerExists(registerCustomerRequest.msisdn(), registerCustomerRequest.email(), registerCustomerRequest.TCNumber())) {
            return new ResponseEntity<>("This customer already exists in Oracle DB.", HttpStatus.CONFLICT);
        }

        Connection connection = oracleConnection.getOracleConnection();

        CallableStatement packageStmt = connection.prepareCall("{call SELECT_PACKAGE_ID(?, ?)}");
        packageStmt.setString(1, registerCustomerRequest.packageName());
        packageStmt.registerOutParameter(2, Types.INTEGER);
        packageStmt.execute();

        int packageId = packageStmt.getInt(2);
        packageStmt.close();

        if (packageId == 0) {
            connection.close();
            return new ResponseEntity<>("Package not found IN ORACLE", HttpStatus.NOT_FOUND);
        }

        logger.info("Retrieved package id: " + packageId);

        String encodedPassword = encodePassword(registerCustomerRequest.password());

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

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT cust_id_sequence.CURRVAL FROM dual");
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

    //volt
    public Optional<Customer> getCustomerByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        Client client = voltDBConnection.getClient();
        ClientResponse response = client.callProcedure("GET_CUSTOMER_INFO_BY_MSISDN", msisdn);
//        ClientResponse response = client.callProcedure("GET_CUSTOMER_INFO_BY_MSISDN", msisdn);

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

//        ClientResponse packageResponse = client.callProcedure("GetPackageByName", registerCustomerRequest.packageName());
        ClientResponse packageResponse = client.callProcedure("GET_PACKAGE_INFO_BY_PACKAGE_NAME", registerCustomerRequest.packageName());
        VoltTable packageTable = packageResponse.getResults()[0];

        if (!packageTable.advanceRow()) {
            client.close();
            return new ResponseEntity<>("Package not found IN VOLT", HttpStatus.NOT_FOUND);
        }

        int packageId = (int) packageTable.getLong("PACKAGE_ID");

//        ClientResponse maxIdResponse = client.callProcedure("GetMaxCustomerId");
        ClientResponse maxIdResponse = client.callProcedure("GET_MAX_CUSTOMER_ID");
        VoltTable maxIdTable = maxIdResponse.getResults()[0];
        int maxCustomerId = 0;
        if (maxIdTable.advanceRow()) {
            maxCustomerId = (int) maxIdTable.getLong("MAX_CUST_ID");
        }
        int customerId = maxCustomerId + 1;

//        ClientResponse customerResponse = client.callProcedure("InsertCustomer",
        ClientResponse customerResponse = client.callProcedure("INSERT_NEW_CUSTOMER",
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

        CreateBalanceRequest createBalanceRequest = CreateBalanceRequest.builder()
                .customerId(customerId)
                .packageId(packageId)
                .build();

        ResponseEntity balanceResponse = balanceRepository.createVoltBalance(createBalanceRequest);

        client.close();
        return balanceResponse;
    }

    private static String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    private boolean customerExists(String msisdn, String email, String tcNo) throws SQLException, ClassNotFoundException {
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

    public String getEncodedCustomerPasswordByMsisdn(String msisdn) throws SQLException, ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();
        PreparedStatement stmt = connection.prepareStatement(OracleQueries.SELECT_PASSWORD);
        stmt.setString(1, msisdn);
        ResultSet resultSet= stmt.executeQuery();
        String encodedPassword = null;
        if (resultSet.next()) {
            encodedPassword = resultSet.getString("PASSWORD");
        }
        System.out.println("ENCODED PASSWORD: " + encodedPassword);
        resultSet.close();
        stmt.close();
        connection.close();
        return encodedPassword;

    }

}
