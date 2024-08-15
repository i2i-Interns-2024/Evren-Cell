package com.i2i.evrencell.aom;

import com.i2i.evrencell.aom.helper.OracleConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AomApplication implements CommandLineRunner {

    private final OracleConnection oracleConnection;
    private static final Logger logger = LogManager.getLogger(AomApplication.class);

    public AomApplication(OracleConnection oracleConnection
    ) {
        this.oracleConnection = oracleConnection;
    }

    public static void main(String[] args) {
        SpringApplication.run(AomApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.debug(oracleConnection.getOracleConnection());

    }
}
