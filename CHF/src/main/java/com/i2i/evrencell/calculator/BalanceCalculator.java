package com.i2i.evrencell.calculator;

import com.i2i.evrencell.kafka.KafkaOperator;
import com.i2i.evrencell.messages.DataMessage;
import com.i2i.evrencell.messages.SmsMessage;
import com.i2i.evrencell.messages.VoiceMessage;
import com.i2i.evrencell.packages.PackageDetails;
import com.i2i.evrencell.packages.PackageDetailsReader;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import com.i2i.evrencell.packages.UserDetails;

public class BalanceCalculator {

    private final VoltdbOperator voltdbOperator;
    private final PackageDetailsReader packageDetailsReader;
    private final KafkaOperator kafkaOperator;

    public BalanceCalculator(VoltdbOperator voltdbOperator, PackageDetailsReader packageDetailsReader, KafkaOperator kafkaOperator) {
        this.voltdbOperator = voltdbOperator;
        this.packageDetailsReader = packageDetailsReader;
        this.kafkaOperator = kafkaOperator;
    }

    public void calculateDataRequest(DataMessage dataMessage) {
        int dataUsage = dataMessage.getDataUsage();
        processRequest("data", dataMessage.getMsisdn(), dataUsage, this::getUserDataBalance);
    }

    public void calculateVoiceRequest(VoiceMessage voiceMessage) {
        int duration = voiceMessage.getDuration();
        processRequest("voice", voiceMessage.getCallerMsisdn(), duration, this::getUserVoiceBalance);
    }

    public void calculateSmsRequest(SmsMessage smsMessage) {
        int smsCount = 1;
        processRequest("sms", smsMessage.getSenderMsisdn(), smsCount, this::getUserSmsBalance);
    }

    private void processRequest(String type, String msisdn, int usage, java.util.function.Function<String, Integer> getBalanceFunction) {
        int userBalance = getBalanceFunction.apply(msisdn);

        if (userBalance >= usage) {
            int updatedBalance = userBalance - usage;
            updateUserBalance(type, msisdn, updatedBalance);
            sendUpdatedUserBalance(type, msisdn, updatedBalance);
            checkUsageThreshold(type, msisdn, updatedBalance);

        } else {
            insufficientBalance(type, msisdn);
        }
    }

    private void insufficientBalance(String type, String msisdn) {
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

    private void updateUserBalance(String type, String msisdn, int updatedBalance) {
        switch (type) {
            case "data":
                voltdbOperator.updateDataBalance(msisdn, updatedBalance);
                break;
            case "voice":
                voltdbOperator.updateVoiceBalance(msisdn, updatedBalance);
                break;
            case "sms":
                voltdbOperator.updateSmsBalance(msisdn, updatedBalance);
                break;
        }
    }

    private void sendUpdatedUserBalance(String type, String msisdn, int updatedBalance) {
        kafkaOperator.sendKafkaUpdatedBalance(type, msisdn, updatedBalance);
    }

    private void checkUsageThreshold(String type, String msisdn, int updatedUsage) {
        UserDetails userDetails = voltdbOperator.getUserDetails(msisdn);
        int packageId = userDetails.getPackageId();

        PackageDetails packageDetails = packageDetailsReader.getPackageDetailsById(packageId);

        int threshold80, threshold100;
        switch (type) {
            case "data":
                threshold80 = (int) (packageDetails.getDataAmount() * 0.80);
                threshold100 = packageDetails.getDataAmount();
                break;
            case "sms":
                threshold80 = (int) (packageDetails.getSmsAmount() * 0.80);
                threshold100 = packageDetails.getSmsAmount();
                break;
            case "voice":
                threshold80 = (int) (packageDetails.getVoiceAmount() * 0.80);
                threshold100 = packageDetails.getVoiceAmount();
                break;
            default:
                return;
        }

        if (updatedUsage >= threshold100) {
            kafkaOperator.sendKafkaUsageThresholdMessage(type, msisdn, "100%");
        } else if (updatedUsage >= threshold80) {
            kafkaOperator.sendKafkaUsageThresholdMessage(type, msisdn, "80%");
        }
    }
}
