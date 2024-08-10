package com.i2i.aom.service;

import com.i2i.aom.encryption.CustomerPasswordEncoder;
import com.i2i.aom.repository.CustomerRepository;
import com.i2i.aom.request.LoginCustomerRequest;
import com.i2i.aom.request.RegisterCustomerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final HazelcastService hazelcastService;
    private final CustomerPasswordEncoder customerPasswordEncoder;

    public AuthService(CustomerRepository customerRepository,
                       HazelcastService hazelcastService,
                       CustomerPasswordEncoder customerPasswordEncoder) {
        this.customerRepository = customerRepository;
        this.customerPasswordEncoder = customerPasswordEncoder;
        this.hazelcastService = hazelcastService;
    }

    public ResponseEntity registerCustomer(RegisterCustomerRequest registerCustomerRequest) throws SQLException,
            ClassNotFoundException, IOException, ProcCallException, InterruptedException {

        ResponseEntity oracleResponse = customerRepository.createUserInOracle(registerCustomerRequest);
        if (!oracleResponse.getStatusCode().is2xxSuccessful()) {
            return oracleResponse;
        }

        ResponseEntity voltResponse = customerRepository.createUserInVoltdb(registerCustomerRequest);
        if (!voltResponse.getStatusCode().is2xxSuccessful()) {
            return voltResponse;
        }

        // Store msisdn in Hazelcast map
//         hazelcastService.put(registerCustomerRequest.msisdn(), registerCustomerRequest.msisdn());

        return ResponseEntity.ok("Customer registered successfully in both Oracle and VoltDB");
    }

    public ResponseEntity<String> login(LoginCustomerRequest loginCustomerRequest) throws SQLException,
            ClassNotFoundException {
        String encodedPassword = customerRepository.getEncodedCustomerPasswordByMsisdn(loginCustomerRequest.msisdn());
        if (encodedPassword == null) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        System.out.println("MATCH: " + loginCustomerRequest.password() + " " +  encodedPassword);
        String decodedPassword = customerPasswordEncoder.decrypt(encodedPassword);
        boolean isPasswordMatch = loginCustomerRequest.password().equals(decodedPassword);
        if (isPasswordMatch) {
            return ResponseEntity.ok("Login successful");
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }


}
