package com.i2i.aom.controller;

import com.i2i.aom.dto.CustomerDto;
import com.i2i.aom.repository.CustomerRepository;
import com.i2i.aom.request.RegisterCustomerRequest;
import com.i2i.aom.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerService customerService,
                              CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/getCustomerByMsisdn")
    public ResponseEntity<CustomerDto> getCustomerByMsisdn(@RequestParam String msisdn) throws IOException, InterruptedException, ProcCallException {
        return ResponseEntity.ok(customerService.getCustomerByMsisdn(msisdn));
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() throws SQLException, ClassNotFoundException {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping("/createCustomer")
    public ResponseEntity createCustomer(@RequestBody RegisterCustomerRequest registerCustomerRequest) throws SQLException, ClassNotFoundException {
        return ResponseEntity.ok(customerRepository.createUserInOracle(registerCustomerRequest));
    }
}
