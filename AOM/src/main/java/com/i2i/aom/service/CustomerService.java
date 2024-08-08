package com.i2i.aom.service;

import com.i2i.aom.dto.CustomerDto;
import com.i2i.aom.mapper.CustomerMapper;
import com.i2i.aom.model.Customer;
import com.i2i.aom.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerDto getCustomerByMsisdn(String msisdn) throws IOException, InterruptedException, ProcCallException {
        Customer customer = customerRepository.getCustomerByMsisdn(msisdn)
                .orElseThrow(() -> new RuntimeException("Customer cannot find by msisdn"));
        return customerMapper.customerToCustomerDto(customer);
    }

    public List<CustomerDto> getAllCustomers() throws SQLException, ClassNotFoundException {
        return customerRepository.getAllCustomers()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .toList();
    }






}
