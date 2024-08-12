package com.i2i.evrencell.abmf;

import com.i2i.evrencell.kafka.message.BalanceMessage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleOperations {

    private Connection connection;

    public OracleOperations(String url, String userName, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, userName, password);
    }

    public void updateUserBalance(BalanceMessage balanceMessage) {
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

        try {
            CallableStatement callableStatement = connection.prepareCall(procedureCall);

            callableStatement.setString(1, balanceMessage.getMsisdn());
            callableStatement.setInt(2, balanceMessage.getAmount());

            callableStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
