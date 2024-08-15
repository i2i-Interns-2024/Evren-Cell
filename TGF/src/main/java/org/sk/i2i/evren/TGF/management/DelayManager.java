package org.sk.i2i.evren.TGF.management;

import org.sk.i2i.evren.TGF.constants.TransType;

public class DelayManager {

    private int voiceDelay;
    private int dataDelay;
    private int smsDelay;

    public DelayManager(int voiceDelay, int dataDelay, int smsDelay) {
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

    public void printTps() {

        double voice = calculateTps(getVoiceDelay());
        double data = calculateTps(getDataDelay());
        double sms = calculateTps(getSmsDelay());

        System.out.println(
                "Voice tps: " + voice +
                "\nData tps:  " + data +
                "\nSms tps:   " + sms +
                "\nTotal tps: " + (voice + data + sms)
        );
    }

    public double calculateTps(int delay) {
        return Math.round( ((double) 1000 /delay) * 100.0) / 100.0;
    }

    public int getVoiceDelay() {
        return voiceDelay;
    }

    public void setVoiceDelay(int voiceDelay) {
        this.voiceDelay = voiceDelay;
    }

    public int getDataDelay() {
        return dataDelay;
    }

    public void setDataDelay(int dataDelay) {
        this.dataDelay = dataDelay;
    }

    public int getSmsDelay() {
        return smsDelay;
    }

    public void setSmsDelay(int smsDelay) {
        this.smsDelay = smsDelay;
    }

    public int getDelay(TransType type) {
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

    public void setDelayAll(int delay) {
        setSmsDelay(delay);
        setDataDelay(delay);
        setVoiceDelay(delay);
    }
}
