package com.i2i.evrencell.aom.repository;

import com.i2i.evrencell.aom.dto.CustomerBalance;
import com.i2i.evrencell.aom.exception.NotFoundException;
import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.aom.helper.VoltDBConnection;
import com.i2i.evrencell.aom.request.CreateBalanceRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
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
    private final VoltDBConnection voltDBConnection;

    public BalanceRepository(OracleConnection oracleConnection,
                             VoltDBConnection voltDBConnection) {
        this.oracleConnection = oracleConnection;
        this.voltDBConnection = voltDBConnection;
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
    public ResponseEntity<String> createVoltBalance(CreateBalanceRequest createBalanceRequest) throws IOException, ProcCallException {
        Client client = voltDBConnection.getClient();


        ClientResponse packageResponse = client.callProcedure("GET_PACKAGE_INFO_BY_PACKAGE_ID", createBalanceRequest.packageId());
        VoltTable packageTable = packageResponse.getResults()[0];

        if (!packageTable.advanceRow()) {
            throw new NotFoundException("Package not found in voltDb");
        }

        int amountMinutes = (int) packageTable.getLong("AMOUNT_MINUTES");
        int amountSms = (int) packageTable.getLong("AMOUNT_SMS");
        int amountData = (int) packageTable.getLong("AMOUNT_DATA");
        int period = (int) packageTable.getLong("PERIOD");

        Timestamp sdate = new Timestamp(System.currentTimeMillis());
        Timestamp edate = new Timestamp(sdate.getTime() + period * 24L * 60L * 60L * 1000L);

        ClientResponse maxIdResponse = client.callProcedure("GET_MAX_BALANCE_ID");
        VoltTable maxIdTable = maxIdResponse.getResults()[0];
        int maxBalanceId = 0;
        if (maxIdTable.advanceRow()) {
            maxBalanceId = (int) maxIdTable.getLong("MAX_BALANCE_ID");
        }
        int balanceId = maxBalanceId + 1;

        client.callProcedure("INSERT_BALANCE_TO_CUSTOMER",
                balanceId,
                createBalanceRequest.customerId(),
                createBalanceRequest.packageId(),
                amountMinutes,
                amountSms,
                amountData,
                sdate,
                edate
        );

        return new ResponseEntity<>("Balance created successfully", HttpStatus.CREATED);
    }

    /**
     * Get remaining customer balance by msisdn
     * This method gets remaining customer balance by msisdn from voltDb
     * @param msisdn
     * @return
     * @throws IOException
     * @throws ProcCallException
     * @throws InterruptedException
     */
    public CustomerBalance getRemainingCustomerBalanceByMsisdn(String msisdn) throws IOException, ProcCallException, InterruptedException {
        Client client = voltDBConnection.getClient();
        ClientResponse response = client.callProcedure("GET_REMAINING_CUSTOMER_BALANCE_BY_MSISDN", msisdn);

        if (response.getStatus() == ClientResponse.SUCCESS) {
            VoltTable resultTable = response.getResults()[0];
            if (resultTable.advanceRow()) {
                String msisdnResult = resultTable.getString("MSISDN");
                int balanceData = (int) resultTable.getLong("BAL_LVL_DATA");
                int balanceSms = (int) resultTable.getLong("BAL_LVL_SMS");
                int balanceMinutes = (int) resultTable.getLong("BAL_LVL_MINUTES");
                Timestamp sdate = resultTable.getTimestampAsSqlTimestamp("SDATE");
                Timestamp edate = resultTable.getTimestampAsSqlTimestamp("EDATE");

                CustomerBalance balanceResponse = CustomerBalance.builder()
                        .msisdn(msisdnResult)
                        .balanceData(balanceData)
                        .balanceMinutes(balanceMinutes)
                        .balanceSms(balanceSms)
                        .sdate(sdate)
                        .edate(edate)
                        .build();

                client.close();
                return balanceResponse;
            }
        }
        client.close();
        throw new NotFoundException("Customer balance not found for msisdn: " + msisdn);
    }
}
