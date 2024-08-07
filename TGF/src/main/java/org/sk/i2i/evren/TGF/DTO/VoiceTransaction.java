package org.sk.i2i.evren.TGF.DTO;

public class VoiceTransaction {

    private final String callerMsisdn;
    private final String calleeMsisdn;
    private final int location;
    private final int duration;


    public VoiceTransaction(String callerMsisdn, String calleeMsisdn, int location, int duration) {
        this.callerMsisdn = callerMsisdn;
        this.calleeMsisdn = calleeMsisdn;
        this.location = location;
        this.duration = duration;
    }

    public String getCallerMsisdn() {
        return callerMsisdn;
    }

    public String getCalleeMsisdn() {
        return calleeMsisdn;
    }

    public int getLocation() {
        return location;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "VoiceTransaction{" +
                "callerMsisdn='" + callerMsisdn + '\'' +
                ", calleeMsisdn='" + calleeMsisdn + '\'' +
                ", location=" + location +
                ", duration=" + duration +
                '}';
    }

}
