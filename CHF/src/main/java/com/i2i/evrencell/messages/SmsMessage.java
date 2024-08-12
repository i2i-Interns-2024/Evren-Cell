package com.i2i.evrencell.messages;

public class SmsMessage {

    private final String senderMsisdn;
    private final String receiverMsisdn;

    public SmsMessage(String senderMsisdn, String receiverMsisdn) {
        this.senderMsisdn = senderMsisdn;
        this.receiverMsisdn = receiverMsisdn;
    }

    public String getSenderMsisdn() {
        return senderMsisdn;
    }

    public String getReceiverMsisdn() {
        return receiverMsisdn;
    }

}
