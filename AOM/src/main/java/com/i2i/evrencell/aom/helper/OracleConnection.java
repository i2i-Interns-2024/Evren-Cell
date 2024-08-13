package com.i2i.evrencell.aom.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class OracleConnection {

    @Value("${spring.datasource.oracle.driver-class-name}")
    private String databaseDriver;

    @Value("${spring.datasource.oracle.url}")
    private String connectionString;

    @Value("${spring.datasource.oracle.username}")
    private String userName;

    @Value("${spring.datasource.oracle.password}")
    private String password;

    public Connection getOracleConnection() throws ClassNotFoundException, SQLException {
        Class.forName(databaseDriver);
        return DriverManager.getConnection(
                connectionString,
                userName,
                password);
    }
}
