package com.i2i.evrencell.aom.repository;

import com.i2i.evrencell.aom.exception.NotFoundException;
import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.aom.request.CreateBalanceRequest;
import com.i2i.evrencell.voltdb.VoltPackageDetails;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
    private final Logger logger = LogManager.getLogger(BalanceRepository.class);

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
        logger.debug("Creating balance for customer with packageId: " + createBalanceRequest.packageId());
        logger.debug("Creating balance for customer with customerId: " + createBalanceRequest.customerId());
        logger.debug("Creating balance for customer with packageId: " + createBalanceRequest.packageId());

        logger.debug("Connecting to OracleDb");
        Connection connection = oracleConnection.getOracleConnection();
        logger.debug("Connected to OracleDb");

        logger.debug("Creating callable statement for SELECT_PACKAGE_DETAILS_ID");
        CallableStatement packageCallableStatement = connection.prepareCall("{call SELECT_PACKAGE_DETAILS_ID(?, ?, ?, ?, ?)}");
        packageCallableStatement.setInt(1, createBalanceRequest.packageId());
        packageCallableStatement.registerOutParameter(2, Types.INTEGER);
        packageCallableStatement.registerOutParameter(3, Types.INTEGER);
        packageCallableStatement.registerOutParameter(4, Types.INTEGER);
        packageCallableStatement.registerOutParameter(5, Types.INTEGER);

        logger.debug("Executing SELECT_PACKAGE_DETAILS_ID");
        packageCallableStatement.execute();
        logger.debug("SELECT_PACKAGE_DETAILS_ID executed successfully");

        logger.debug("Getting package details from SELECT_PACKAGE_DETAILS_ID");
        int amountMinutes = packageCallableStatement.getInt(2);
        int amountSms = packageCallableStatement.getInt(3);
        int amountData = packageCallableStatement.getInt(4);
        int period = packageCallableStatement.getInt(5);
        packageCallableStatement.close();
        logger.debug("Package details retrieved successfully");

        if (amountMinutes == 0 && amountSms == 0 && amountData == 0 && period == 0) {
            connection.close();
            throw new NotFoundException("Package not found in oracleDb");
        }

        Timestamp sdate = new Timestamp(System.currentTimeMillis());
        Timestamp edate = new Timestamp(sdate.getTime() + period * 24L * 60L * 60L * 1000L);

        logger.debug("Creating callable statement for INSERT_BALANCE_TO_CUSTOMER");
        CallableStatement balanceStmt = connection.prepareCall("{call INSERT_BALANCE_TO_CUSTOMER(?, ?, ?, ?, ?, ?, ?)}");
        balanceStmt.setInt(1, createBalanceRequest.customerId());
        balanceStmt.setInt(2, createBalanceRequest.packageId());
        balanceStmt.setInt(3, amountMinutes);
        balanceStmt.setInt(4, amountSms);
        balanceStmt.setInt(5, amountData);
        balanceStmt.setTimestamp(6, sdate);
        balanceStmt.setTimestamp(7, edate);
        logger.debug("Executing INSERT_BALANCE_TO_CUSTOMER");
        balanceStmt.execute();
        logger.debug("INSERT_BALANCE_TO_CUSTOMER executed successfully");
        balanceStmt.close();
        connection.close();
        logger.debug("Balance created successfully in OracleDb");

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

        logger.debug("Creating balance for customer with packageId: " + createBalanceRequest.packageId());
        logger.debug("Creating balance for customer with customerId: " + createBalanceRequest.customerId());
        logger.debug("Creating balance for customer with packageId: " + createBalanceRequest.packageId());
        VoltPackageDetails voltPackageDetails = voltdbOperator.getPackageInfoByPackageId(createBalanceRequest.packageId());

        Timestamp sdate = new Timestamp(System.currentTimeMillis());
        Timestamp edate = new Timestamp(sdate.getTime() + voltPackageDetails.period() * 24L * 60L * 60L * 1000L);

        logger.debug("Getting max balance id from VOLTDB");
        int maxBalanceId  = voltdbOperator.getMaxBalanceId();
        int balanceId = maxBalanceId + 1;

        logger.debug("Inserting balance to VOLTDB");
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
        logger.debug("Balance created successfully in VOLTDB");

        return new ResponseEntity<>("Balance created successfully", HttpStatus.CREATED);
    }

}
