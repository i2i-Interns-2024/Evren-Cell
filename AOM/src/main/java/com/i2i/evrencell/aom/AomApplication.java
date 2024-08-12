package com.i2i.evrencell.aom;

import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.aom.helper.VoltDBConnection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AomApplication implements CommandLineRunner {

    private final OracleConnection oracleConnection;
    private final VoltDBConnection voltDBConnection;

    public AomApplication(OracleConnection oracleConnection, VoltDBConnection voltDBConnection) {
        this.oracleConnection = oracleConnection;
        this.voltDBConnection = voltDBConnection;
    }

    public static void main(String[] args) {
        SpringApplication.run(AomApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(oracleConnection.getOracleConnection());
        System.out.println(voltDBConnection.getClient());

    }
}
