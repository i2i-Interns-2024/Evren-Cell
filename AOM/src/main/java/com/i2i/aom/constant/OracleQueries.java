package com.i2i.aom.constant;

public class OracleQueries {
//    public final static String IS_CUSTOMER_ALREADY_EXISTS = "SELECT COUNT(*) FROM C##bilal.CUSTOMER WHERE MSISDN = ? OR EMAIL = ? OR TC_NO = ?";
    public final static String IS_CUSTOMER_ALREADY_EXISTS = "SELECT COUNT(*) FROM CUSTOMER WHERE MSISDN = ? OR EMAIL = ? OR TC_NO = ?";
    public final static String SELECT_PASSWORD = "SELECT PASSWORD FROM CUSTOMER WHERE MSISDN = ?";
    public final static String SELECT_CUSTOMER_ID = "SELECT cust_id_sequence.CURRVAL FROM dual";
//    public final static String SELECT_CUSTOMER_ID = "SELECT cust_id_sequence.NEXTVAL FROM dual;";
//    public final static String SELECT_CUSTOMER_ID = "SELECT MAX(CUST_ID) FROM CUSTOMER;";
}
