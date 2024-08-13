package com.i2i.evrencell.voltdb;

import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
