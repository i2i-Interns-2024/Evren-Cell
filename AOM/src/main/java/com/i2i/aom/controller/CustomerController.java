package com.i2i.aom.controller;

import com.i2i.aom.dto.CustomerDto;
import com.i2i.aom.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


/**
 * Controller class for Customer related operations
 */
@RestController
@RequestMapping("/v1/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/getCustomerByMsisdn")
    public ResponseEntity<CustomerDto> getCustomerByMsisdn(@RequestParam String msisdn) throws IOException, InterruptedException, ProcCallException {
        return ResponseEntity.ok(customerService.getCustomerByMsisdn(msisdn));
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() throws SQLException, ClassNotFoundException {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
}


