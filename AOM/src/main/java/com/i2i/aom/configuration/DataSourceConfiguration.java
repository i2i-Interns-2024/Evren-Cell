package com.i2i.aom.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    /**
     * DataSource configuration for Oracle
     * @return DataSource
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public DataSource oracleDataSource() {
        return new HikariDataSource();
    }

    /**
     * DataSource configuration for VoltDB
     * @return DataSource
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.voltdb")
    public DataSource voltdbDataSource() {
        return new HikariDataSource();
    }
}
