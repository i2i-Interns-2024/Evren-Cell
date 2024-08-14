package com.i2i.evrencell.aom.service;

import com.i2i.evrencell.aom.dto.CustomerDto;
import com.i2i.evrencell.aom.mapper.CustomerMapper;
import com.i2i.evrencell.aom.repository.CustomerRepository;
import com.i2i.evrencell.voltdb.VoltCustomer;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final VoltdbOperator voltdbOperator = new VoltdbOperator();

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    /**
     * This method is used to get a customer by msisdn
     * @param msisdn
     * @return CustomerDto
     * @throws IOException
     * @throws InterruptedException
     * @throws ProcCallException
     */
    public CustomerDto getCustomerByMsisdn(String msisdn) throws IOException, InterruptedException, ProcCallException {

        VoltCustomer voltCustomer = voltdbOperator.getCustomerByMsisdn(msisdn)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.voltCustomerBalanceToCustomerDto(voltCustomer);
    }

    /**
     * This method is used to get all customers
     * @return List<CustomerDto>
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<CustomerDto> getAllCustomers() throws SQLException, ClassNotFoundException {
        return customerRepository.getAllCustomers()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .toList();
    }
}
