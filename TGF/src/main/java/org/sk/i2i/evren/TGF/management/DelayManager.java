package org.sk.i2i.evren.TGF.management;

import org.sk.i2i.evren.TGF.constants.TransType;

public class DelayManager {

    private long voiceDelay;
    private long dataDelay;
    private long smsDelay;

    public DelayManager(long voiceDelay, long dataDelay, long smsDelay) {
        this.voiceDelay = voiceDelay;
        this.dataDelay = dataDelay;
        this.smsDelay = smsDelay;
    }

    public void printDelay() {
        System.out.println(
                "Voice: " + getVoiceDelay() +
                "\nData:  " + getDataDelay() +
                "\nSms:   " + getSmsDelay()
        );
    }

    public long getVoiceDelay() {
        return voiceDelay;
    }

    public void setVoiceDelay(long voiceDelay) {
        this.voiceDelay = voiceDelay;
    }

    public long getDataDelay() {
        return dataDelay;
    }

    public void setDataDelay(long dataDelay) {
        this.dataDelay = dataDelay;
    }

    public long getSmsDelay() {
        return smsDelay;
    }

    public void setSmsDelay(long smsDelay) {
        this.smsDelay = smsDelay;
    }

    public long getDelay(TransType type) {
        switch (type) {
            case DATA -> {
                return getDataDelay();
            }
            case VOICE -> {
                return getVoiceDelay();
            }
            case SMS -> {
                return getSmsDelay();
            }
        }
        return -1;
    }

    public void setDelayAll(long delay) {
        setSmsDelay(delay);
        setDataDelay(delay);
        setVoiceDelay(delay);
    }
}
