package com.i2i.evrencell.CHF.calculator;

import com.i2i.evrencell.kafka.message.BalanceType;
import com.i2i.evrencell.voltdb.UserDetails;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.apache.log4j.Logger;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.VoiceTransaction;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.i2i.evrencell.CHF.kafka.KafkaOperations.*;

public class BalanceCalculator {

    private static final Logger logger = Logger.getLogger(BalanceCalculator.class);

    private final VoltdbOperator voltdbOperator;
    private final Map<String, Boolean> threshold80SentMap = new HashMap<>();
    private final Map<String, Boolean> threshold100SentMap = new HashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

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
        CompletableFuture.supplyAsync(() -> getUserBalance(type, msisdn), executor).thenAccept(userBalance -> {
            if (userBalance >= usage) {
                int updatedBalance = userBalance - usage;
                updateUserBalance(type, msisdn, updatedBalance, otherMsisdn);
                sendUpdatedBalanceMessage(type, msisdn, updatedBalance);
                checkUsageThreshold(type, msisdn, updatedBalance);
            } else {
                logger.warn("Insufficient balance: Requested " + usage + " but only " + userBalance + " available for " + msisdn);
                handlePartialUsage(type, msisdn, userBalance, usage, otherMsisdn);
            }
        }).exceptionally(ex -> {
            logger.error("Error processing request: " + ex.getMessage(), ex);
            return null;
        });
    }

    private int getUserBalance(BalanceType type, String msisdn) {
        return switch (type) {
            case DATA -> voltdbOperator.getDataBalance(msisdn);
            case VOICE -> voltdbOperator.getVoiceBalance(msisdn);
            case SMS -> voltdbOperator.getSmsBalance(msisdn);
            default -> 0;
        };
    }

    private void updateUserBalance(BalanceType type, String msisdn, int updatedBalance, String... otherMsisdn) {
        switch (type) {
            case DATA -> voltdbOperator.updateDataBalance(updatedBalance, msisdn);
            case VOICE -> voltdbOperator.updateVoiceBalance(updatedBalance, msisdn);
            case SMS -> voltdbOperator.updateSmsBalance(updatedBalance, msisdn);
        }
        sendUsageRecordMessage(type, msisdn, otherMsisdn.length > 0 ? otherMsisdn[0] : null, updatedBalance, new Timestamp(System.currentTimeMillis()));
        logger.info("Updated balance for " + msisdn + ": " + updatedBalance + " " + type);
    }

    private void handlePartialUsage(BalanceType type, String msisdn, int userBalance, int usage, String... otherMsisdn) {
        if (userBalance > 0) {
            updateUserBalance(type, msisdn, 0, otherMsisdn);
            sendUpdatedBalanceMessage(type, msisdn, 0);
            checkUsageThreshold(type, msisdn, 0);
            logger.info("User " + msisdn + " used remaining balance of " + userBalance + " " + type + ". Request for " + usage + " was partially fulfilled.");
        } else {
            insufficientBalance(type, msisdn);
        }
    }

    private void insufficientBalance(BalanceType type, String msisdn) {
        logger.warn("No sufficient " + type + " balance for " + msisdn);
    }

    private void checkUsageThreshold(BalanceType type, String msisdn, int currentBalance) {
        int packageBalance;
        int threshold80;
        int threshold1;

        switch (type) {
            case DATA:
                packageBalance = voltdbOperator.getPackageDataBalance(msisdn);
                threshold80 = (int) (packageBalance * 0.20);
                threshold1 = (int) (packageBalance * 0.01);
                break;
            case SMS:
                packageBalance = voltdbOperator.getPackageSmsBalance(msisdn);
                threshold80 = (int) (packageBalance * 0.20);
                threshold1 = (int) (packageBalance * 0.01);
                break;
            case VOICE:
                packageBalance = voltdbOperator.getPackageVoiceBalance(msisdn);
                threshold80 = (int) (packageBalance * 0.20);
                threshold1 = (int) (packageBalance * 0.01);
                break;
            default:
                return;
        }

        UserDetails userDetails = voltdbOperator.getUserDetails(msisdn);

        if (currentBalance <= threshold80 && !threshold80SentMap.getOrDefault(msisdn, false)) {
            sendNotificationMessage(userDetails.getName(), userDetails.getLastName(), msisdn, userDetails.getEmail(), type, packageBalance, "%80", new Timestamp(System.currentTimeMillis()));
            threshold80SentMap.put(msisdn, true);
            logger.info("80% usage notification sent for " + msisdn);
        } else if (currentBalance <= threshold1 && !threshold100SentMap.getOrDefault(msisdn, false)) {
            sendNotificationMessage(userDetails.getName(), userDetails.getLastName(), msisdn, userDetails.getEmail(), type, packageBalance, "%100", new Timestamp(System.currentTimeMillis()));
            threshold100SentMap.put(msisdn, true);
            logger.info("100% usage notification sent for " + msisdn);
        }
    }
}
