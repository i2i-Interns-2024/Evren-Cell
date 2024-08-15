package com.i2i.evrencell.aom.service;

import com.i2i.evrencell.aom.encryption.CustomerPasswordEncoder;
import com.i2i.evrencell.aom.repository.CustomerRepository;
import com.i2i.evrencell.aom.request.LoginCustomerRequest;
import com.i2i.evrencell.aom.request.RegisterCustomerRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.i2i.hazelcast.utils.HazelcastMWOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger("DebugLog");

    private final CustomerRepository customerRepository;
    private final CustomerPasswordEncoder customerPasswordEncoder;

    public AuthService(CustomerRepository customerRepository,
                       CustomerPasswordEncoder customerPasswordEncoder) {
        this.customerRepository = customerRepository;
        this.customerPasswordEncoder = customerPasswordEncoder;
    }

    public ResponseEntity<String> registerCustomer(RegisterCustomerRequest registerCustomerRequest) throws SQLException,
            ClassNotFoundException, IOException, ProcCallException, InterruptedException {

        logger.debug("Starting to register customer in Oracle and VoltDB with MSISDN: " + registerCustomerRequest.msisdn());
        ResponseEntity<String> oracleResponse = customerRepository.createUserInOracle(registerCustomerRequest);
        if (!oracleResponse.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to create customer in Oracle for MSISDN: " + registerCustomerRequest.msisdn());
            return oracleResponse;
        }

        ResponseEntity<String> voltResponse = customerRepository.createUserInVoltdb(registerCustomerRequest);
        if (!voltResponse.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to create customer in VoltDB for MSISDN: " + registerCustomerRequest.msisdn());
            return voltResponse;
        }

        HazelcastMWOperation.put(registerCustomerRequest.msisdn(), registerCustomerRequest.msisdn());
        logger.debug("Successfully registered customer in Oracle, VoltDB, and Hazelcast for MSISDN: " + registerCustomerRequest.msisdn());

        return ResponseEntity.ok("Customer registered successfully in both Oracle and VoltDB");
    }

    public ResponseEntity<String> login(LoginCustomerRequest loginCustomerRequest) throws SQLException, ClassNotFoundException {
        logger.debug("Attempting login for MSISDN: " + loginCustomerRequest.msisdn());
        String encodedPassword = customerRepository.getEncodedCustomerPasswordByMsisdn(loginCustomerRequest.msisdn());
        if (encodedPassword == null) {
            logger.debug("Invalid credentials for MSISDN: " + loginCustomerRequest.msisdn());
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        String decodedPassword = customerPasswordEncoder.decrypt(encodedPassword);
        boolean isPasswordMatch = loginCustomerRequest.password().equals(decodedPassword);
        if (isPasswordMatch) {
            logger.debug("Login successful for MSISDN: " + loginCustomerRequest.msisdn());
            return ResponseEntity.ok("Login successful");
        } else {
            logger.warn("Invalid credentials for MSISDN: " + loginCustomerRequest.msisdn());
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}