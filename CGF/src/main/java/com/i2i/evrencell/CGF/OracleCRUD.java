package com.i2i.evrencell.CGF;

import com.i2i.evrencell.kafka.message.UsageRecordMessage;

import javax.sql.DataSource;
import java.sql.*;

public class OracleCRUD {
    private static final DataSource dataSource = DataSourceConfig.getDataSource();

    public static void callInsertProcedure(UsageRecordMessage usageRecord) {
        CallableStatement statement = null;
        try(Connection connection = dataSource.getConnection()){
            String procedureCall = "{call insert_personal_usage(?, ?, ?, ?, ?)}";
            statement = connection.prepareCall(procedureCall);
            statement.setString(1, usageRecord.getCallerMsisdn());
            statement.setString(2, usageRecord.getCalleeMsisdn());
            statement.setTimestamp(3, usageRecord.getUsageDate());
            statement.setString(4, usageRecord.getUsageType().name());
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
