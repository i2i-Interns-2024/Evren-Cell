package com.i2i.aom.service;

import com.i2i.aom.encryption.CustomerPasswordEncoder;
import com.i2i.aom.exception.UnauthorizedException;
import com.i2i.aom.repository.CustomerRepository;
import com.i2i.aom.request.LoginCustomerRequest;
import com.i2i.aom.request.RegisterCustomerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;


/**
 * This class is used to handle the business logic for the authentication of customers
 */
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

    /**
     * This method is used to register a customer, it first creates the customer in Oracle
     * If the customer is successfully created in Oracle, it then creates the customer in VoltDB
     * If the customer is successfully created in VoltDB, it then adds the customer's MSISDN to Hazelcast
     * If the customer is successfully added to Hazelcast, it returns a success message
     * If the customer is not successfully created in Oracle or VoltDB, it returns an error message
     * @param registerCustomerRequest
     * @return ResponseEntity<String>
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public ResponseEntity<String> registerCustomer(RegisterCustomerRequest registerCustomerRequest) throws SQLException,
            ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        System.out.println("Customer created successfully in Oracle DB1.");

        ResponseEntity<String> oracleResponse = customerRepository.createUserInOracle(registerCustomerRequest);
        if (!oracleResponse.getStatusCode().is2xxSuccessful()) {
            return oracleResponse;
        }

        ResponseEntity<String> voltResponse = customerRepository.createUserInVoltdb(registerCustomerRequest);
        if (!voltResponse.getStatusCode().is2xxSuccessful()) {
            return voltResponse;
        }

        hazelcastService.put(registerCustomerRequest.msisdn(), registerCustomerRequest.msisdn());

        return ResponseEntity.ok("Customer registered successfully in both Oracle and VoltDB");
    }

    /**
     * This method is used to login a customer, it checks if the password provided by the customer matches the password
     * If the password matches, it returns a success message
     * If the password does not match, it returns an error message and a status code of 401
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> login(LoginCustomerRequest loginCustomerRequest) throws SQLException, ClassNotFoundException {
        String encodedPassword = customerRepository.getEncodedCustomerPasswordByMsisdn(loginCustomerRequest.msisdn());
        if (encodedPassword == null) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        String decodedPassword = customerPasswordEncoder.decrypt(encodedPassword);
        boolean isPasswordMatch = loginCustomerRequest.password().equals(decodedPassword);
        if (isPasswordMatch) {
            return ResponseEntity.ok("Login successful");
        } else {
            throw new UnauthorizedException("Invalid credentials please check your password or msisdn and try again.");
        }
    }

}
