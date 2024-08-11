package com.i2i.evrencell.packages;

public class PackageDetails {
    private final int id;
    private final String name;
    private final double price;
    private final int dataAmount;
    private final int smsAmount;
    private final int voiceAmount;

    public PackageDetails(int id, String name, double price, int dataAmount, int smsAmount, int voiceAmount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.dataAmount = dataAmount;
        this.smsAmount = smsAmount;
        this.voiceAmount = voiceAmount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getDataAmount() {
        return dataAmount;
    }

    public int getSmsAmount() {
        return smsAmount;
    }

    public int getVoiceAmount() {
        return voiceAmount;
    }
}
