package com.i2i.evrencell.aom.repository;

import com.i2i.evrencell.aom.dto.PackageDetails;
import com.i2i.evrencell.aom.exception.NotFoundException;
import com.i2i.evrencell.aom.helper.OracleConnection;
import com.i2i.evrencell.voltdb.VoltPackage;
import oracle.jdbc.OracleTypes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PackageRepository {
    private final OracleConnection oracleConnection;
    private final VoltdbOperator voltdbOperator = new VoltdbOperator();
    private static final Logger logger = LogManager.getLogger(PackageRepository.class);

    public PackageRepository(OracleConnection oracleConnection) {
        this.oracleConnection = oracleConnection;
    }


    /**
     * Get all packages
     * This method gets all packages from OracleDB with the help of stored procedure
     *
     * @return List<Package>
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<com.i2i.evrencell.aom.model.Package> getAllPackages() throws SQLException, ClassNotFoundException {
        logger.debug("Getting all packages");
        Connection connection = oracleConnection.getOracleConnection();
        CallableStatement callableStatement = connection.prepareCall("{call SELECT_ALL_PACKAGES(?)}");
        callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
        callableStatement.execute();

        ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
        List<com.i2i.evrencell.aom.model.Package> packageList = new ArrayList<>();
        while (resultSet.next()) {
            Integer packageId = resultSet.getInt("PACKAGE_ID");
            String packageName = resultSet.getString("PACKAGE_NAME");
            Integer amountMinutes = resultSet.getInt("AMOUNT_MINUTES");
            Integer amountData = resultSet.getInt("AMOUNT_DATA");
            Integer amountSms = resultSet.getInt("AMOUNT_SMS");
            double price = resultSet.getDouble("PRICE");
            Integer period = resultSet.getInt("PERIOD");

            com.i2i.evrencell.aom.model.Package packageModel = com.i2i.evrencell.aom.model.Package.builder()
                    .packageId(packageId)
                    .packageName(packageName)
                    .amountMinutes(amountMinutes)
                    .amountData(amountData)
                    .price(price)
                    .amountSms(amountSms)
                    .period(period)
                    .build();
            packageList.add(packageModel);
        }

        resultSet.close();
        callableStatement.close();
        connection.close();
        return packageList;
    }

    /**
     * Get user package by MSISDN
     * This method gets user package by msisdn from voltDb with the help of stored procedure.
     *
     * @param msisdn
     * @return Package
     * @throws IOException
     * @throws ProcCallException
     */
    public VoltPackage getUserPackageByMsisdn(String msisdn) throws IOException, ProcCallException {
        return voltdbOperator.getPackageByMsisdn(msisdn);
    }

    /**
     * Get package details by package name
     * This method gets package details by package name from oracle with the help of stored procedure
     *
     * @param packageName
     * @return Optional<PackageDetails>
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Optional<PackageDetails> getPackageDetails(String packageName) throws SQLException, ClassNotFoundException {
        logger.debug("Getting package details for package: " + packageName);
        logger.debug("Connecting to OracleDB");
        Connection connection = oracleConnection.getOracleConnection();
        logger.debug("Connected to OracleDB");
        logger.debug("Preparing call for SELECT_PACKAGE_DETAILS_NAME");
        CallableStatement callableStatement = connection.prepareCall("{call SELECT_PACKAGE_DETAILS_NAME(?, ?, ?, ?)}");
        callableStatement.setString(1, packageName);
        callableStatement.registerOutParameter(2, Types.INTEGER);
        callableStatement.registerOutParameter(3, Types.INTEGER);
        callableStatement.registerOutParameter(4, Types.INTEGER);
        logger.debug("Executing call for SELECT_PACKAGE_DETAILS_NAME");
        callableStatement.execute();
        logger.debug("Executed call for SELECT_PACKAGE_DETAILS_NAME");

        int amountMinutes = callableStatement.getInt(2);
        int amountSms = callableStatement.getInt(3);
        int amountData = callableStatement.getInt(4);

        logger.debug("Closing callable statement and connection");
        callableStatement.close();
        connection.close();

        if (amountMinutes == 0 && amountSms == 0 && amountData == 0) {
            throw new NotFoundException("Package details not found for package: " + packageName);
        }

        return Optional.of(PackageDetails.builder()
                .packageName(packageName)
                .amountMinutes(amountMinutes)
                .amountSms(amountSms)
                .amountData(amountData)
                .build());
    }
}
