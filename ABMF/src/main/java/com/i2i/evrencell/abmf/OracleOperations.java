package com.i2i.evrencell.abmf;

import com.i2i.evrencell.kafka.message.BalanceMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class OracleOperations {

    private static final DataSource dataSource = DataSourceConfig.getDataSource();
    private static final Logger logger = LogManager.getLogger(OracleOperations.class);

    public static void updateUserBalance(BalanceMessage balanceMessage) {
        String procedureCall = "";

        switch (balanceMessage.getType()) {
            case SMS:
                procedureCall = "{call update_balance_by_msisdn_sms(?, ?)}";
                break;
            case VOICE:
                procedureCall = "{call update_balance_by_msisdn_minutes(?, ?)}";
                break;
            case DATA:
                procedureCall = "{call update_balance_by_msisdn_data(?, ?)}";
                break;
        }

        try (Connection connection = dataSource.getConnection();) {
            CallableStatement callableStatement = connection.prepareCall(procedureCall);

            callableStatement.setString(1, balanceMessage.getMsisdn());
            callableStatement.setInt(2, balanceMessage.getAmount());

            callableStatement.execute();

            logBalanceMessage(balanceMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logBalanceMessage(BalanceMessage message) {
        String logMessage = String.format("BalanceMessage [MSISDN: %s, Amount: %d, Type: %s]",
                message.getMsisdn(),
                message.getAmount(),
                message.getType());

        logger.info(logMessage);
    }

}
