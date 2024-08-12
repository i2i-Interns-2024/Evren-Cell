package org.sk.i2i.evren;

public class SmsTransaction {
    private final String senderMsisdn;
    private final String receiverMsisdn;
    private final int location;

    public SmsTransaction(String senderMsisdn, String receiverMsisdn, int location) {
        this.senderMsisdn = senderMsisdn;
        this.receiverMsisdn = receiverMsisdn;
        this.location = location;
    }

    public String getSenderMsisdn() {
        return senderMsisdn;
    }

    public String getReceiverMsisdn() {
        return receiverMsisdn;
    }

    public int getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "SmsTransaction{" +
                "senderMsisdn='" + senderMsisdn + '\'' +
                ", receiverMsisdn='" + receiverMsisdn + '\'' +
                ", location=" + location +
                '}';
    }
}
