package org.example;

import javax.sql.DataSource;
import java.sql.*;

public class OracleCRUD {
    private static final DataSource dataSource = DataSourceConfig.getDataSource();

    public static void callInsertProcedure(UsageRecord usageRecord) {
        CallableStatement statement = null;
        try(Connection connection = dataSource.getConnection()){
            String procedureCall = "{call C##BILAL.insert_personal_usage(?, ?, ?, ?, ?)}";
            statement = connection.prepareCall(procedureCall);
            statement.setString(1, usageRecord.getMsisdn1());
            statement.setString(2, usageRecord.getMsisdn2());
            statement.setTimestamp(3, usageRecord.getUsageDate());
            statement.setString(4, usageRecord.getUsageType());
            statement.setInt(5, usageRecord.getUsageDuration());

            statement.execute();
            System.out.println("procedure called successfully.");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
