package com.i2i.evrencell.CHF.calculator;

import com.i2i.evrencell.kafka.message.BalanceType;
import com.i2i.evrencell.voltdb.UserDetails;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.VoiceTransaction;

import java.sql.Timestamp;

import static com.i2i.evrencell.CHF.kafka.KafkaOperations.*;

public class BalanceCalculator {

    private final VoltdbOperator voltdbOperator;

    public BalanceCalculator(VoltdbOperator voltdbOperator) {
        this.voltdbOperator = voltdbOperator;
    }

    public void calculateDataRequest(DataTransaction dataMessage) {
        processRequest(BalanceType.DATA, dataMessage.getMsisdn(), dataMessage.getDataUsage());
    }

    public void calculateVoiceRequest(VoiceTransaction voiceMessage) {
        processRequest(BalanceType.VOICE, voiceMessage.getCallerMsisdn(), voiceMessage.getDuration(), voiceMessage.getCalleeMsisdn());
    }

    public void calculateSmsRequest(SmsTransaction smsMessage) {
        processRequest(BalanceType.SMS, smsMessage.getSenderMsisdn(), 1, smsMessage.getReceiverMsisdn());
    }

    private void processRequest(BalanceType type, String msisdn, int usage, String... otherMsisdn) {
        int userBalance = switch (type) {
            case DATA -> getUserDataBalance(msisdn);
            case VOICE -> getUserVoiceBalance(msisdn);
            case SMS -> getUserSmsBalance(msisdn);
            default -> 0;
        };

        if (userBalance >= usage) {
            int updatedBalance = userBalance - usage;
            updateUserBalance(type, msisdn, updatedBalance, otherMsisdn);
            sendUpdatedBalanceMessage(type, msisdn, updatedBalance);
            //TODO Notification bozuk amk
            //checkUsageThreshold(type, msisdn, updatedBalance);

        } else {
            insufficientBalance(type, msisdn);
        }
    }

    private void insufficientBalance(BalanceType type, String msisdn) {
        System.out.println("No sufficient " + type + " balance for " + msisdn);
    }

    private int getUserDataBalance(String msisdn) {
        return voltdbOperator.getDataBalance(msisdn);
    }

    private int getUserVoiceBalance(String msisdn) {
        return voltdbOperator.getVoiceBalance(msisdn);
    }

    private int getUserSmsBalance(String msisdn) {
        return voltdbOperator.getSmsBalance(msisdn);
    }

    private void updateUserBalance(BalanceType type, String msisdn, int updatedBalance, String... otherMsisdn) {
        switch (type) {
            case DATA:
                voltdbOperator.updateDataBalance(updatedBalance, msisdn);
                sendUsageRecordMessage(type, msisdn, null, updatedBalance, new Timestamp(System.currentTimeMillis()));
                break;
            case VOICE:
                voltdbOperator.updateVoiceBalance(updatedBalance, msisdn);
                sendUsageRecordMessage(type, msisdn, otherMsisdn[0], updatedBalance, new Timestamp(System.currentTimeMillis()));
                break;
            case SMS:
                voltdbOperator.updateSmsBalance(updatedBalance, msisdn);
                sendUsageRecordMessage(type, msisdn, otherMsisdn[0], updatedBalance, new Timestamp(System.currentTimeMillis()));
                break;
        }
    }

    private void checkUsageThreshold(BalanceType type, String msisdn, int currentBalance) {
        int packageBalance;
        int threshold80;
        switch (type) {
            case DATA:
                packageBalance = voltdbOperator.getPackageDataBalance(msisdn);
                threshold80 = (int) (packageBalance * 0.20);
                break;
            case SMS:
                packageBalance = voltdbOperator.getPackageSmsBalance(msisdn);
                threshold80 = (int) (packageBalance * 0.20);
                break;
            case VOICE:
                packageBalance = voltdbOperator.getPackageVoiceBalance(msisdn);
                threshold80 = (int) (packageBalance * 0.20);
                break;
            default:
                return;
        }


        //TODO Notification bozuk amk
        //üsttekine girince alttakine girmiyor
        UserDetails userDetails = voltdbOperator.getUserDetails(msisdn);
        if (currentBalance <= threshold80) {
            sendNotificationMessage(userDetails.getName(), userDetails.getLastName(), msisdn, userDetails.getEmail(), type, packageBalance, "%80", new Timestamp(System.currentTimeMillis()));
        } else if ((packageBalance*0.01) > currentBalance) {
            sendNotificationMessage(userDetails.getName(), userDetails.getLastName(), msisdn, userDetails.getEmail(), type, packageBalance, "%100", new Timestamp(System.currentTimeMillis()));
        }

    }
}
