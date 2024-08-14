package com.i2i.evrencell.nf;

import com.i2i.evrencell.kafka.message.NotificationMessage;

import javax.sql.DataSource;
import java.sql.*;

public class OracleOperations {

    private static final DataSource dataSource = DataSourceConfig.getDataSource();

    public void callInsertProcedure(NotificationMessage notificationMessage) throws SQLException {
        CallableStatement statement = null;
        try(Connection connection = dataSource.getConnection()) {
            String procedureCall = "{call INSERT_NOTIFICATION_LOG_FOR_NF(?, ?, ?)}";

            statement = connection.prepareCall(procedureCall);

            String textType = "";
            switch (notificationMessage.getType()) {
                case VOICE -> {
                    textType = "DK konusma";
                }
                case SMS -> {
                    textType = "SMS";
                }
                case DATA -> {
                    textType = "MB internet";
                }
            }

            statement.setString(1, String.format("%s %s, toplam %s %s hakkinin %s kadarini kullandi.",notificationMessage.getName()
                    ,notificationMessage.getLastname(),notificationMessage.getAmount(),textType,notificationMessage.getThreshold()));
            statement.setTimestamp(2, new java.sql.Timestamp(notificationMessage.getTimestamp().getTime()));
            statement.setString(3, notificationMessage.getMsisdn());

            statement.execute();

            System.out.println("Procedure called successfully.");
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

}
