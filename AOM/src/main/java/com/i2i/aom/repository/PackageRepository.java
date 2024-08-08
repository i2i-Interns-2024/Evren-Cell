package com.i2i.aom.repository;

import com.i2i.aom.constant.OracleQueries;
import com.i2i.aom.dto.PackageDetails;
import com.i2i.aom.helper.OracleConnection;
import com.i2i.aom.helper.VoltDBConnection;
import com.i2i.aom.model.Package;
import org.springframework.stereotype.Repository;
import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PackageRepository {
    private final OracleConnection oracleConnection;
    private final VoltDBConnection voltDBConnection;

    public PackageRepository(OracleConnection oracleConnection, VoltDBConnection voltDBConnection) {
        this.oracleConnection = oracleConnection;
        this.voltDBConnection = voltDBConnection;
    }

    public List<Package> getAllPackages() throws SQLException, ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();
        System.out.println(connection);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(OracleQueries.SELECT_ALL_PACKAGES);
        List<Package> packageList = new ArrayList<>();
        while (resultSet.next()) {
            Integer packageId = resultSet.getInt("PACKAGE_ID");
            String packageName = resultSet.getString("PACKAGE_NAME");
            Integer amountMinutes = resultSet.getInt("AMOUNT_MINUTES");
            Integer amountData = resultSet.getInt("AMOUNT_DATA");
            Integer amountSms = resultSet.getInt("AMOUNT_SMS");
            double price = resultSet.getDouble("PRICE");
            Integer period = resultSet.getInt("PERIOD");

            Package packageModel = Package.builder()
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

        connection.close();
        return packageList;

    }

    public List<Package> getUserPackageByMsisdn(String msisdn) throws IOException, ProcCallException {
        Client client = voltDBConnection.getClient();
        ClientResponse clientResponse = client.callProcedure("GetPackageByMsisdn", msisdn);
        VoltTable tablePackageInfo = clientResponse.getResults()[0];

        List<Package> packageInfo = new ArrayList<>();

        while (tablePackageInfo.advanceRow()) {
            Integer packageId = (int) tablePackageInfo.getLong("PACKAGE_ID");
            String packageName = tablePackageInfo.getString("PACKAGE_NAME");
            double price = tablePackageInfo.getDouble("PRICE");
            Integer amountMinutes = (int) tablePackageInfo.getLong("AMOUNT_MINUTES");
            Integer amountData = (int) tablePackageInfo.getLong("AMOUNT_DATA");
            Integer amountSms = (int) tablePackageInfo.getLong("AMOUNT_SMS");
            Integer period = (int) tablePackageInfo.getLong("PERIOD");

            Package packageModel = Package.builder()
                    .packageId(packageId)
                    .packageName(packageName)
                    .price(price)
                    .amountMinutes(amountMinutes)
                    .amountData(amountData)
                    .amountSms(amountSms)
                    .period(period)
                    .build();

            packageInfo.add(packageModel);
        }

        return packageInfo;
    }


    public Optional<PackageDetails> getPackageDetails(String packageName) throws SQLException, ClassNotFoundException {
        Connection connection = oracleConnection.getOracleConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(OracleQueries.SELECT_PACKAGE_DETAILS_NAME);
        preparedStatement.setString(1, packageName);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Integer amountMinutes = resultSet.getInt("AMOUNT_MINUTES");
            Integer amountSms = resultSet.getInt("AMOUNT_SMS");
            Integer amountData = resultSet.getInt("AMOUNT_DATA");

            connection.close();
            return Optional.of(PackageDetails.builder()
                    .packageName(packageName)
                    .amountMinutes(amountMinutes)
                    .amountSms(amountSms)
                    .amountData(amountData)
                    .build());
        } else {
            connection.close();
            return Optional.empty();
        }
    }
}
