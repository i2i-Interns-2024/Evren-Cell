package com.i2i.evrencell.messages;

public class VoiceMessage {

    private final String callerMsisdn;
    private final String calleeMsisdn;
    private final int duration;

    public VoiceMessage(String callerMsisdn, String calleeMsisdn, int duration) {
        this.callerMsisdn = callerMsisdn;
        this.calleeMsisdn = calleeMsisdn;
        this.duration = duration;
    }

    public String getCallerMsisdn() {
        return callerMsisdn;
    }

    public String getCalleeMsisdn() {
        return calleeMsisdn;
    }

    public int getDuration() {
        return duration;
    }
}
