package com.i2i.aom.model;

import lombok.Builder;

import java.util.Date;

@Builder
public class Customer {
    private Integer customerId;
    private String  msisdn;
    private String  name;
    private String  surname;
    private String  email;
    private String  password;
    private String  TCNumber;
    private Date    sDate;

    public Customer(Integer customerId, String msisdn, String name, String surname, String email, String password,  String securityKey, Date sDate) {
        this.customerId = customerId;
        this.msisdn = msisdn;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.TCNumber = securityKey;
        this.sDate = sDate;
    }
    public Customer() { }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getTCNumber() {
        return TCNumber;
    }

    public void setTCNumber(String TCNumber) {
        this.TCNumber = TCNumber;
    }

    public Date getsDate() {
        return sDate;
    }

    public void setsDate(Date sDate) {
        this.sDate = sDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", msisdn='" + msisdn + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", TCNumber='" + TCNumber + '\'' +
                ", sDate=" + sDate +
                '}';
    }
}
