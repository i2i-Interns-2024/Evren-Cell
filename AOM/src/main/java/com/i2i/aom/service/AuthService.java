package com.i2i.aom.service;

import com.i2i.aom.repository.CustomerRepository;
import com.i2i.aom.request.RegisterCustomerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;

    public AuthService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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

        return ResponseEntity.ok("Customer registered successfully in both Oracle and VoltDB");
    }

}
