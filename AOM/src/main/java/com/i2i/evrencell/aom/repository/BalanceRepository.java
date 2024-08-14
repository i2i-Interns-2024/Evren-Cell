package com.i2i.evrencell.aom.repository;

import com.i2i.evrencell.aom.exception.NotFoundException;
import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.aom.request.CreateBalanceRequest;
import com.i2i.evrencell.voltdb.VoltPackageDetails;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

@Repository
public class BalanceRepository {
    private final OracleConnection oracleConnection;
    private final VoltdbOperator voltdbOperator = new VoltdbOperator();
    private final Logger logger = LoggerFactory.getLogger(BalanceRepository.class);

    public BalanceRepository(OracleConnection oracleConnection) {
        this.oracleConnection = oracleConnection;
    }

    /**
     * Create balance for customer
     * This method creates balance for customer in oracleDb
     * It first gets the package details from oracleDb
     * Then creates balance for customer with his package subscription
     * @param createBalanceRequest
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public ResponseEntity<String> createOracleBalance(CreateBalanceRequest createBalanceRequest) throws ClassNotFoundException, SQLException {
        Connection connection = oracleConnection.getOracleConnection();

        CallableStatement packageCallableStatement = connection.prepareCall("{call SELECT_PACKAGE_DETAILS_ID(?, ?, ?, ?, ?)}");
        packageCallableStatement.setInt(1, createBalanceRequest.packageId());
        packageCallableStatement.registerOutParameter(2, Types.INTEGER);
        packageCallableStatement.registerOutParameter(3, Types.INTEGER);
        packageCallableStatement.registerOutParameter(4, Types.INTEGER);
        packageCallableStatement.registerOutParameter(5, Types.INTEGER);
        packageCallableStatement.execute();

        int amountMinutes = packageCallableStatement.getInt(2);
        int amountSms = packageCallableStatement.getInt(3);
        int amountData = packageCallableStatement.getInt(4);
        int period = packageCallableStatement.getInt(5);
        packageCallableStatement.close();

        if (amountMinutes == 0 && amountSms == 0 && amountData == 0 && period == 0) {
            connection.close();
            throw new NotFoundException("Package not found in oracleDb");
        }

        Timestamp sdate = new Timestamp(System.currentTimeMillis());
        Timestamp edate = new Timestamp(sdate.getTime() + period * 24L * 60L * 60L * 1000L);

        CallableStatement balanceStmt = connection.prepareCall("{call INSERT_BALANCE_TO_CUSTOMER(?, ?, ?, ?, ?, ?, ?)}");
        balanceStmt.setInt(1, createBalanceRequest.customerId());
        balanceStmt.setInt(2, createBalanceRequest.packageId());
        balanceStmt.setInt(3, amountMinutes);
        balanceStmt.setInt(4, amountSms);
        balanceStmt.setInt(5, amountData);
        balanceStmt.setTimestamp(6, sdate);
        balanceStmt.setTimestamp(7, edate);
        balanceStmt.execute();

        balanceStmt.close();

        connection.close();

        return new ResponseEntity<>("Balance created successfully", HttpStatus.CREATED);
    }


    //==VOLTDB==

    /**
     * Create balance for customer
     * This method creates balance for customer in voltDb
     * It first gets the package details from voltDb with using packageId
     * Then creates balance for customer with his package subscription
     * @param createBalanceRequest
     * @return
     * @throws IOException
     * @throws ProcCallException
     */
    public ResponseEntity<String> createVoltBalance(CreateBalanceRequest createBalanceRequest) throws IOException, ProcCallException, InterruptedException {

        logger.info("PACKAGE ID:  {}", createBalanceRequest.packageId());
        VoltPackageDetails voltPackageDetails = voltdbOperator.getPackageInfoByPackageId(createBalanceRequest.packageId());
        logger.info("VOLT PACKAGE DETAILS:  {}", voltPackageDetails.toString());

        Timestamp sdate = new Timestamp(System.currentTimeMillis());
        Timestamp edate = new Timestamp(sdate.getTime() + voltPackageDetails.period() * 24L * 60L * 60L * 1000L);

        int maxBalanceId  = voltdbOperator.getMaxBalanceId();
        int balanceId = maxBalanceId + 1;

        voltdbOperator.insertBalance(
                balanceId,
                createBalanceRequest.customerId(),
                createBalanceRequest.packageId(),
                voltPackageDetails.amountMinutes(),
                voltPackageDetails.amountSms(),
                voltPackageDetails.amountData(),
                sdate,
                edate
        );

        System.out.println("Balance created successfully in VOLTDB");

        return new ResponseEntity<>("Balance created successfully", HttpStatus.CREATED);
    }

}
