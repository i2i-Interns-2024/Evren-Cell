package com.i2i.evrencell.voltdb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor

public class VoltCustomer {
    private Integer customerId;
    private String msisdn;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String TCNumber;
    private Date sDate;



    // Getters and setters can be added here if needed
}
