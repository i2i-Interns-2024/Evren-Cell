package com.i2i.evrencell.abmf;

import com.i2i.evrencell.abmf.ConfigLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.getProperty("oracle.url"));
        config.setUsername(ConfigLoader.getProperty("oracle.username"));
        config.setPassword(ConfigLoader.getProperty("oracle.password"));
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}