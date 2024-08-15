package org.sk.i2i.evren.TGF.management;

import org.sk.i2i.evren.TGF.constants.TransType;

public class DelayManager {

    private Long voiceDelay;
    private Long dataDelay;
    private Long smsDelay;

    public DelayManager(Long voiceDelay, Long dataDelay, Long smsDelay) {
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

    public double calculateTps(long delay) {
        return Math.round( ((double) 1000000000 /delay) * 100.0) / 100.0;
    }

    public Long getVoiceDelay() {
        return voiceDelay;
    }

    public void setVoiceDelay(Long voiceDelay) {
        this.voiceDelay = voiceDelay;
    }

    public Long getDataDelay() {
        return dataDelay;
    }

    public void setDataDelay(Long dataDelay) {
        this.dataDelay = dataDelay;
    }

    public Long getSmsDelay() {
        return smsDelay;
    }

    public void setSmsDelay(Long smsDelay) {
        this.smsDelay = smsDelay;
    }

    public Long getDelay(TransType type) {
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
        return -1L;
    }

    public void setDelayAll(Long delay) {
        setSmsDelay(delay);
        setDataDelay(delay);
        setVoiceDelay(delay);
    }
}
