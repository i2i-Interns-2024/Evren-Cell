package com.i2i.aom.repository;

import com.i2i.aom.constant.OracleQueries;
import com.i2i.aom.helper.OracleConnection;
import com.i2i.aom.helper.VoltDBConnection;
import com.i2i.aom.model.Package;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        ResultSet resultSet = statement.executeQuery(OracleQueries.SELECT_QUERY);
        List<Package> packageList = new ArrayList<>();

        while (resultSet.next()){
            Integer packageId = resultSet.getInt("PACKAGE_ID");
            String packageName = resultSet.getString("PACKAGE_NAME");
            Integer amountMinutes = resultSet.getInt("AMOUNT_MINUTES");
            Integer amountData = resultSet.getInt("AMOUNT_DATA");
            Integer amountSms = resultSet.getInt("AMOUNT_SMS");
            double  price = resultSet.getDouble("PRICE");
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
}
