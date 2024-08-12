package com.ikbalci.calculator;

import com.ikbalci.kafka.KafkaOperator;
import com.ikbalci.messages.DataMessage;
import com.ikbalci.messages.SmsMessage;
import com.ikbalci.messages.VoiceMessage;
import com.ikbalci.voltdb.VoltdbOperations;

import java.math.BigDecimal;

public class BalanceCalculator {

    VoltdbOperations voltOperation = new VoltdbOperations();
    KafkaOperator kafkaOperator = new KafkaOperator();

    public void calculateVoiceRequest(VoiceMessage requestMessage) {

        int requestUsageAmount = requestMessage.getDuration();

        BigDecimal userVoiceBalance = BigDecimal.valueOf(voltOperation.getVoiceBalance(requestMessage.getMsisdn()));

        long userId = voltOperation.getUserIdByMSISDN(requestMessage.getMsisdn());

        System.out.println("User ID: " + userId);
        System.out.println("Request Amount: " + requestUsageAmount);
        System.out.println("Voice Balance: " + userVoiceBalance.intValueExact());

        if (userVoiceBalance.intValueExact() <= 0) {
            System.out.println("No Sufficient VOICE Balance");

        } else if (userVoiceBalance.intValueExact() >= requestUsageAmount) {
            voltOperation.updateVoiceBalance(requestMessage.getMsisdn(), BigDecimal.valueOf(-requestUsageAmount));
            kafkaOperator.sendKafkaUsageMessage("voice", requestMessage.getMsisdn(), userId, requestUsageAmount);

        } else if (userVoiceBalance.intValueExact() < requestUsageAmount) {
            int remainingVoiceAmount = userVoiceBalance.intValueExact();
            requestMessage.setDuration(requestUsageAmount - remainingVoiceAmount);

            voltOperation.updateVoiceBalance(requestMessage.getMsisdn(), BigDecimal.valueOf(-remainingVoiceAmount));
            kafkaOperator.sendKafkaUsageMessage("voice", requestMessage.getMsisdn(), userId, remainingVoiceAmount);
        }
    }

    public void calculateSmsRequest(SmsMessage requestMessage) {

        int requestUsageAmount = 1;

        BigDecimal userSMSBalance = BigDecimal.valueOf(voltOperation.getSmsBalance(requestMessage.getMsisdn()));
        long userId = voltOperation.getUserIdByMSISDN(requestMessage.getMsisdn());

        System.out.println("User ID: " + userId);
        System.out.println("Request Amount: " + requestUsageAmount);
        System.out.println("SMS Balance: " + userSMSBalance.intValueExact());

        if (userSMSBalance.intValueExact() <= 0) {
            System.out.println("No Sufficient SMS Balance");
        } else {
            voltOperation.updateSmsBalance(requestMessage.getMsisdn(), BigDecimal.valueOf(-requestUsageAmount));
            kafkaOperator.sendKafkaUsageMessage("sms", requestMessage.getMsisdn(), userId, requestUsageAmount);
        }
    }

    public void calculateDataRequest(DataMessage requestMessage) {

        int requestUsageAmount = requestMessage.getDataUsage();

        BigDecimal userDataBalance = BigDecimal.valueOf(voltOperation.getDataBalance(requestMessage.getMsisdn()));
        long userId = voltOperation.getUserIdByMSISDN(requestMessage.getMsisdn());

        System.out.println("User ID: " + userId);
        System.out.println("Request Amount: " + requestUsageAmount);
        System.out.println("DATA Balance: " + userDataBalance.intValueExact());

        if (userDataBalance.intValueExact() <= 0) {
            System.out.println("No Sufficient DATA Balance");

        } else if (userDataBalance.intValueExact() >= requestUsageAmount) {
            voltOperation.updateDataBalance(requestMessage.getMsisdn(), BigDecimal.valueOf(-requestUsageAmount));
            kafkaOperator.sendKafkaUsageMessage("data", requestMessage.getMsisdn(), userId, requestUsageAmount);

        } else if (userDataBalance.intValueExact() < requestUsageAmount) {
            int remainingDataAmount = userDataBalance.intValueExact();
            requestMessage.setDataUsage(requestUsageAmount - remainingDataAmount);

            voltOperation.updateDataBalance(requestMessage.getMsisdn(), BigDecimal.valueOf(-remainingDataAmount));
            kafkaOperator.sendKafkaUsageMessage("data", requestMessage.getMsisdn(), userId, remainingDataAmount);
        }
    }
}