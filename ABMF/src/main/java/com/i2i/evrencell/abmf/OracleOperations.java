package com.i2i.evrencell.abmf;

import com.i2i.evrencell.kafka.message.BalanceMessage;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class OracleOperations {

    private static final DataSource dataSource = DataSourceConfig.getDataSource();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
