package com.i2i.evrencell.CGF;

import com.i2i.evrencell.kafka.message.UsageRecordMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;

public class OracleCRUD {
    private static final DataSource dataSource = DataSourceConfig.getDataSource();
    private static final Logger logger = LogManager.getLogger(OracleCRUD.class);
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
            logger.info("procedure called successfully.");
        } catch (SQLException e){
            logger.error("SQL Exception while calling procedure... ", e);
        }
        finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing SQL statement... ", e);
            }
        }
    }
}
