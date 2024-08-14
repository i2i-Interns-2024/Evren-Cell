package com.i2i.evrencell.aom;

import com.i2i.evrencell.aom.helper.OracleConnection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AomApplication implements CommandLineRunner {

    private final OracleConnection oracleConnection;

    public AomApplication(OracleConnection oracleConnection
    ) {
        this.oracleConnection = oracleConnection;
    }

    public static void main(String[] args) {
        SpringApplication.run(AomApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(oracleConnection.getOracleConnection());

    }
}
