package com.i2i.evrencell.abmf;

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
        String procedureCall = "{call C##BILAL.update_balance_by_msisdn(?, ?, ?, ?)}";

        try {
            CallableStatement callableStatement = connection.prepareCall(procedureCall);

            callableStatement.setString(1, balanceMessage.getMsisdn());
            callableStatement.setInt(2, balanceMessage.getMinute());
            callableStatement.setInt(3, balanceMessage.getSms());
            callableStatement.setInt(4, balanceMessage.getData());
            callableStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



}
