package org.sk.i2i.evren.TGF;

public class DeadLetterStats {

    private long dataDeadLetters = 0;
    private long voiceDeadLetters = 0;
    private long smsDeadLetters = 0;

    public void reset() {
        dataDeadLetters = 0;
        voiceDeadLetters = 0;
        smsDeadLetters = 0;
    }

    public void incrementData() {
        dataDeadLetters++;
    }
    public void incrementVoice() {
        voiceDeadLetters++;
    }
    public void incrementSms() {
        smsDeadLetters++;
    }

    public long getDataDeadLetters() {
        return dataDeadLetters;
    }

    public long getVoiceDeadLetters() {
        return voiceDeadLetters;
    }

    public long getSmsDeadLetters() {
        return smsDeadLetters;
    }

}
