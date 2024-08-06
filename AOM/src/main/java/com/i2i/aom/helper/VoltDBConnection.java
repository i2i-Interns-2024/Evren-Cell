package com.i2i.aom.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class VoltDBConnection {

    @Value("${spring.datasource.voltdb.driver-class-name}")
    private String databaseDriver;

    @Value("${spring.datasource.voltdb.url}")
    private String connectionString;

    @Value("${spring.datasource.voltdb.username}")
    private String userName;

    @Value("${spring.datasource.voltdb.password}")
    private String password;

    public Connection getVoltDBConnection() throws ClassNotFoundException, SQLException {
        Class.forName(databaseDriver);
        return DriverManager.getConnection(
                connectionString,
                userName,
                password);
    }
}